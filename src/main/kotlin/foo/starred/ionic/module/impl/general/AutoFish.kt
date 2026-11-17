@file:Suppress("ObjectPrivatePropertyName", "Unused")

package foo.starred.ionic.module.impl.general

import com.mojang.brigadier.Command
import foo.starred.ionic.annotation.impl.IonicModuleScope
import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.api.time.IonicScheduler
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.event.impl.EntityEvent
import foo.starred.ionic.module.base.Module
import foo.starred.ionic.util.rightClick
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.format.extension.string.stripped
import foo.starred.kontext.api.item.extension.get
import foo.starred.kontext.api.item.resolver.impl.GenericKontextItemDataResolver
import foo.starred.kontext.api.scheduling.scheduler.extensions.clientTicks
import foo.starred.kontext.api.scheduling.scheduler.extensions.start
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import kotlin.math.abs
import kotlin.math.round
import kotlin.random.Random

@LoadableIonicModule
@IonicModuleScope(skyblock = true)
object AutoFish : Module(
    "Auto fish",
    "Helper features for fishing.",
    ConfigCategory.GENERAL
) {
    private val autoPull by config.switch("Auto pull", true)
    private val `delay$pull` by config.slider("Delay", 1, 0, 5, "ticks")
    private val `variance$pull` by config.slider("Delay variance", 0, 0, 3, "ticks")

    private val recast by config.switch("Auto recast")
    private val `_recast$check` by config.information("Recast check checks if the fishing rod is already being used, and uses it if not.")
    private val `recast$check` by config.switch("Recast check")
    private val `delay$recast` by config.slider("Recast delay", 1, 0, 10, "ticks")
    private val `variance$recast` by config.slider("Delay variance", 0, 0, 5, "ticks")

    private val kill by config.switch("Kill spawned mobs")
    private val `_kill$info` by config.information("After an auto pull, if a mob spawns near the bobber: swap to an iron sword, look straight down, right click, then return to fishing. If the mob is still alive afterwards, /ld is run. While an iron sword is in the hotbar, the recast waits for the detect window so the swap never happens mid cast. Requires auto pull.")
    private val `kill$radius` by config.slider("Mob radius", 8, 2, 20, "blocks")
    private val `kill$window` by config.slider("Detect window", 30, 5, 100, "ticks")
    private val `kill$delay` by config.slider("Action delay", 2, 1, 10, "ticks")
    private val `kill$variance` by config.slider("Action variance", 1, 0, 5, "ticks")
    private val `kill$rotation` by config.slider("Rotation time", 4, 1, 10, "ticks")
    private val `kill$drift` by config.slider("Aim drift", 5, 0, 10, "degrees")
    private val `kill$check` by config.slider("Kill check delay", 10, 1, 40, "ticks")

    private var bobber: FishingHook? = null

    // Mob watching (started after each auto pull)
    private var watchUntil = 0L
    private var watchOrigin: Vec3? = null

    // Incremented on every reel; a scheduled recast is dropped if this changed since it was scheduled
    private var reelId = 0

    // Set while the sword routine is running so nothing else clicks / casts
    private var busyUntil = 0L

    // The angle the rod was originally cast at; return angles stay within +/- drift of this
    private var anchorYaw: Float? = null
    private var anchorPitch: Float? = null

    private val watching get() = System.currentTimeMillis() < watchUntil
    private val busy get() = System.currentTimeMillis() < busyUntil

    init {
        registerCommands()

        on<EntityEvent.Load> {
            val entity = entity as? FishingHook ?: return@on
            val player = client.player ?: return@on
            if (entity.owner != player) return@on

            val id = player.mainHandItem.get<GenericKontextItemDataResolver.ID, String>() ?: return@on
            if (id == "SOUL_WHIP" || id == "FLAMING_FLAY") return@on

            bobber = entity
            if (kill) updateAnchor(player)
        }

        on<EntityEvent.Unload> {
            val entity = entity as? FishingHook ?: return@on
            if (entity != bobber) return@on

            bobber = null
        }

        // Mob spawned shortly after a reel, close to where the bobber was
        on<EntityEvent.Load> {
            if (!kill || !watching || busy) return@on

            val mob = entity as? Mob ?: return@on
            val origin = watchOrigin ?: return@on
            val max = `kill$radius`.toDouble()
            if (mob.position().distanceToSqr(origin) > max * max) return@on

            startKillRoutine(mob)
        }

        on<EntityEvent.Update.Named> {
            if (!autoPull) return@on
            if (busy) return@on

            val hook = bobber ?: return@on
            if (component.stripped() != "!!!") return@on
            if (entity.distanceTo(hook) > 2f) return@on

            val origin = hook.position()
            val delay = (`delay$pull` + if (`variance$pull` > 0) (0..`variance$pull`).random() else 0).coerceAtLeast(0)
            IonicScheduler.schedule(delay.clientTicks.start) {
                rightClick()

                val reel = ++reelId
                val watch = kill && findHotbarSlot(Items.IRON_SWORD) != null
                if (watch) startWatch(origin)

                if (!recast) return@schedule
                var recastDelay = 2 + `delay$recast` + if (`variance$recast` > 0) (0..`variance$recast`).random() else 0

                // Wait out the mob window first, so the sword swap can only happen after a pull, never mid cast
                if (watch) recastDelay = maxOf(recastDelay, `kill$window` + 2)

                IonicScheduler.schedule(recastDelay.clientTicks.start) {
                    // dropped if the sword routine started (it does its own recast) or another reel happened
                    if (reel != reelId || busy) return@schedule
                    rightClick()
                }
            }
        }

        IonicScheduler.repeat((15 * 20).clientTicks.start) {
            if (!enabled) return@repeat
            if (busy || watching) return@repeat
            if (!recast) return@repeat
            if (!`recast$check`) return@repeat
            if (client.player == null) return@repeat
            if (bobber?.isAlive == true) return@repeat
            if (client.player?.mainHandItem?.item != Items.FISHING_ROD) return@repeat

            rightClick()
        }
    }

    // ---------------------------------------------------------------------------------------
    // Command: /ionic hypetest
    // ---------------------------------------------------------------------------------------

    private fun registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher, _ ->
            dispatcher.register(
                ClientCommandManager.literal("ionic").then(
                    ClientCommandManager.literal("hypetest").executes {
                        runHypeTest()
                        Command.SINGLE_SUCCESS
                    }
                )
            )
        })
    }

    /**
     * Runs swap to iron sword -> look down -> right click, then restores the previous slot and view angle.
     * No fishing, no mob and no /ld check involved. Public so it can also be wired into another command system.
     */
    fun runHypeTest() {
        val player = client.player ?: return
        if (busy) return say("Routine already running.")

        val sword = findHotbarSlot(Items.IRON_SWORD) ?: return say("No iron sword in your hotbar.")

        say("Running hypetest.")
        runRoutine(
            sword = sword,
            returnSlot = player.inventory.selectedSlot,
            returnYaw = player.yRot,
            returnPitch = player.xRot,
            target = null,
            recastAtEnd = false,
            requireEnabled = false
        )
    }

    // ---------------------------------------------------------------------------------------
    // Mob kill routine
    // ---------------------------------------------------------------------------------------

    private class Step(val delay: Int, val action: () -> Unit)

    private fun startWatch(origin: Vec3) {
        watchOrigin = origin
        watchUntil = System.currentTimeMillis() + `kill$window` * 50L
    }

    /** Remembers the original cast angle, and re-anchors if the player manually turned away from it. */
    private fun updateAnchor(player: LocalPlayer) {
        val limit = `kill$drift` + 2f
        val yaw = anchorYaw
        val pitch = anchorPitch

        val moved = yaw == null || pitch == null ||
            abs(Mth.wrapDegrees(player.yRot - yaw)) > limit ||
            abs(player.xRot - pitch) > limit

        if (moved) {
            anchorYaw = player.yRot
            anchorPitch = player.xRot
        }
    }

    private fun startKillRoutine(mob: Mob) {
        val player = client.player ?: return
        if (player.mainHandItem.item != Items.FISHING_ROD) return

        val sword = findHotbarSlot(Items.IRON_SWORD) ?: return
        val rodSlot = player.inventory.selectedSlot
        if (sword == rodSlot) return

        // Return angle: random point within +/- drift of the ORIGINAL cast angle, not the last one
        val drift = `kill$drift`.toFloat()
        val returnYaw = (anchorYaw ?: player.yRot) + randomOffset(drift)
        val returnPitch = ((anchorPitch ?: player.xRot) + randomOffset(drift)).coerceIn(-90f, 90f)

        watchUntil = 0L
        reelId++ // cancels the recast that was scheduled after the pull

        runRoutine(
            sword = sword,
            returnSlot = rodSlot,
            returnYaw = returnYaw,
            returnPitch = returnPitch,
            target = mob,
            recastAtEnd = recast,
            requireEnabled = true
        )
    }

    private fun runRoutine(
        sword: Int,
        returnSlot: Int,
        returnYaw: Float,
        returnPitch: Float,
        target: Mob?,
        recastAtEnd: Boolean,
        requireEnabled: Boolean
    ) {
        busyUntil = System.currentTimeMillis() + 15_000L // failsafe in case the scheduler drops a step

        var escaped = false

        // Every step runs on its own tick, always at least 1 tick after the previous one
        val steps = buildList<Step> {
            add(Step(actionDelay()) { selectSlot(sword) })
            addAll(rotationSteps(null, 90f))
            add(Step(actionDelay()) { rightClick() })

            // After the single right click: if the mob is still there, run /ld
            if (target != null) {
                add(Step(`kill$check`.coerceAtLeast(1)) {
                    if (target.isAlive) {
                        sendCommand("ld")
                        escaped = true
                    }
                })
            }

            add(Step(actionDelay()) { selectSlot(returnSlot) })
            addAll(rotationSteps(returnYaw, returnPitch))

            if (recastAtEnd) {
                add(Step(actionDelay()) {
                    if (!escaped) rightClick()
                })
            }
        }

        runSteps(steps, 0, requireEnabled)
    }

    private fun runSteps(steps: List<Step>, index: Int, requireEnabled: Boolean) {
        if (index >= steps.size) {
            // short grace period so the recast check can't double click before the new bobber exists
            busyUntil = System.currentTimeMillis() + 1000L
            return
        }

        val step = steps[index]
        IonicScheduler.schedule(step.delay.coerceAtLeast(1).clientTicks.start) {
            if ((requireEnabled && !enabled) || client.player == null) {
                busyUntil = 0L
                return@schedule
            }

            step.action()
            runSteps(steps, index + 1, requireEnabled)
        }
    }

    /** Smooth rotation spread over N ticks, one rotate call per tick. A null yaw keeps the current yaw. */
    private fun rotationSteps(yaw: Float?, pitch: Float): List<Step> {
        val total = `kill$rotation`.coerceAtLeast(1)
        var startYaw = 0f
        var startPitch = 0f
        var endYaw = 0f

        return (1..total).map { i ->
            Step(if (i == 1) actionDelay() else 1) action@{
                val player = client.player ?: return@action

                if (i == 1) {
                    startYaw = player.yRot
                    startPitch = player.xRot
                    endYaw = startYaw + Mth.wrapDegrees((yaw ?: startYaw) - startYaw)
                }

                val t = ease(i / total.toFloat())
                rotate(
                    player,
                    startYaw + (endYaw - startYaw) * t,
                    startPitch + (pitch - startPitch) * t
                )
            }
        }
    }

    // ---------------------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------------------

    private fun rotate(player: LocalPlayer, targetYaw: Float, targetPitch: Float) {
        val gcd = gcd()

        val yaw = snap(player.yRot, player.yRot + Mth.wrapDegrees(targetYaw - player.yRot), gcd)
        val pitch = snap(player.xRot, targetPitch, gcd).coerceIn(-90f, 90f)

        player.yRot = yaw
        player.xRot = pitch

        player.yHeadRot = yaw
        player.yBodyRot = yaw

        player.forceSetRotation(yaw, false, pitch, false)
    }

    /** Smallest rotation step a real mouse can produce at the current sensitivity. */
    private fun gcd(): Float {
        val f = client.options.sensitivity().get() * 0.6 + 0.2
        return (f * f * f * 8.0 * 0.15).toFloat()
    }

    private fun snap(from: Float, to: Float, gcd: Float): Float = from + round((to - from) / gcd) * gcd

    private fun ease(t: Float): Float {
        if (t < 0.5f) return 4f * t * t * t
        val u = -2f * t + 2f
        return 1f - u * u * u / 2f
    }

    private fun randomOffset(range: Float): Float = (Random.nextFloat() * 2f - 1f) * range

    private fun actionDelay(): Int =
        (`kill$delay` + if (`kill$variance` > 0) (0..`kill$variance`).random() else 0).coerceAtLeast(1)

    private fun selectSlot(slot: Int) {
        val player = client.player ?: return
        if (player.inventory.selectedSlot == slot) return
        player.inventory.selectedSlot = slot
    }

    private fun findHotbarSlot(item: Item): Int? {
        val player = client.player ?: return null
        return (0..8).firstOrNull { player.inventory.getItem(it).item == item }
    }

    /** Sends a command without the leading slash. */
    private fun sendCommand(command: String) {
        client.player?.connection?.sendCommand(command)
    }

    private fun say(message: String) {
        client.player?.sendSystemMessage(Component.literal("[Auto fish] $message"))
    }
}
