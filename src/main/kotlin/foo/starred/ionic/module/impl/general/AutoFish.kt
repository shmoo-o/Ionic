@file:Suppress("ObjectPrivatePropertyName", "Unused")

package foo.starred.ionic.module.impl.general

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
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.item.Items

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

    private var bobber: FishingHook? = null

    init {
        on<EntityEvent.Load> {
            val entity = entity as? FishingHook ?: return@on
            val player = client.player ?: return@on
            if (entity.owner != player) return@on

            val id = player.mainHandItem.get<GenericKontextItemDataResolver.ID, String>() ?: return@on
            if (id == "SOUL_WHIP" || id == "FLAMING_FLAY") return@on

            bobber = entity
        }

        on<EntityEvent.Unload> {
            val entity = entity as? FishingHook ?: return@on
            if (entity != bobber) return@on

            bobber = null
        }

        on<EntityEvent.Update.Named> {
            if (!autoPull) return@on

            val hook = bobber ?: return@on
            if (component.stripped() != "!!!") return@on
            if (entity.distanceTo(hook) > 2f) return@on

            val delay = (`delay$pull` + if (`variance$pull` > 0) (0..`variance$pull`).random() else 0).coerceAtLeast(0)
            IonicScheduler.schedule(delay.clientTicks.start) {
                rightClick()

                if (!recast) return@schedule
                val recast = 2 + `delay$recast` + if (`variance$recast` > 0) (0..`variance$recast`).random() else 0
                IonicScheduler.schedule(recast.clientTicks.start) {
                    rightClick()
                }
            }
        }

        IonicScheduler.repeat((15 * 20).clientTicks.start) {
            if (!enabled) return@repeat
            if (!recast) return@repeat
            if (!`recast$check`) return@repeat
            if (client.player == null) return@repeat
            if (bobber?.isAlive == true) return@repeat
            if (client.player?.mainHandItem?.item != Items.FISHING_ROD) return@repeat

            rightClick()
        }
    }
}
