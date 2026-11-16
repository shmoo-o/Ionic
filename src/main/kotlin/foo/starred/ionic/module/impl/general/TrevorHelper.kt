@file:Suppress("ObjectPrivatePropertyName", "Unused")

package foo.starred.ionic.module.impl.general

import foo.starred.ionic.annotation.impl.IonicModuleScope
import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.ionic.api.time.IonicScheduler
import foo.starred.ionic.event.impl.LocationEvent
import foo.starred.ionic.event.impl.MessageEvent
import foo.starred.ionic.module.base.Module
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.ionic.event.impl.WorldRenderEvent
import foo.starred.kontext.abstraction.chat.command.MinecraftCommands
import foo.starred.kontext.abstraction.text.measurer.MinecraftFontMeasurer
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import foo.starred.kontext.abstraction.title.MinecraftTitle
import foo.starred.kontext.api.entity.extension.renderAABB
import foo.starred.kontext.api.entity.extension.renderVec3
import foo.starred.kontext.api.entity.extension.serverMaxHealth
import foo.starred.kontext.api.format.extension.component.formattedText
import foo.starred.kontext.api.format.extension.string.stripped
import foo.starred.kontext.api.format.extension.time.parseDuration
import foo.starred.kontext.api.scheduling.scheduler.extensions.clientTicks
import foo.starred.kontext.api.text.parser.impl.parse
import foo.starred.parallax.api.primitives.ParallaxBox
import foo.starred.parallax.api.primitives.ParallaxLine
import net.minecraft.network.chat.ClickEvent
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.chicken.Chicken
import net.minecraft.world.entity.animal.cow.Cow
import net.minecraft.world.entity.animal.equine.Horse
import net.minecraft.world.entity.animal.pig.Pig
import net.minecraft.world.entity.animal.rabbit.Rabbit
import net.minecraft.world.entity.animal.sheep.Sheep
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@LoadableIonicModule
@IonicModuleScope(islands = [PresetSkyBlockIsland.THE_BARN])
object TrevorHelper : Module(
    "Trevor helper",
    "Helper features for Trevor the Trapper!",
    ConfigCategory.GENERAL
) {
    private val esp by config.switch("Animal ESP")
    private val `esp$tracer` by config.switch("Show tracer")

    private val autoCall by config.switch("Auto call")
    private val callDelay by config.slider("Call delay", 1, 0, 5, "ticks")
    private val callOff by config.slider("Call early", 2.0, 0.0, 5.0, "seconds", true)

    private val autoAccept by config.switch("Auto accept")
    private val acceptDelay by config.slider("Accept delay", 1, 0, 5, "ticks")

    private val endAlert by config.switch("Cooldown end alert")
    private val `alert$message` by config.input("Alert message", "<red>Cooldown ended!")
    private val `alert$sound` by config.sound("Alert sound")

    private val hud = config.hud("Cooldown timer") {
        val example = "Cooldown: §c12.4s".formattedText()

        preview {
            MinecraftFontRenderer.extract(graphics, example)
        }

        constrain {
            MinecraftFontMeasurer.constrain(example)
        }

        render {
            if (cooldown <= 0) return@render
            val time = (cooldown - System.currentTimeMillis()).div(1000).coerceAtLeast(0).parseDuration(decimals = 1)

            MinecraftFontRenderer.extract(graphics, "Cooldown: <red>$time".parse())
        }
    }

    private val colors by config.group("Colors")
    private val `color$trackable` by colors.colorPicker("Trackable color", MochaColorScheme.Text.argb)
    private val `color$untrackable` by colors.colorPicker("Untrackable color", MochaColorScheme.Green.argb)
    private val `color$undetected` by colors.colorPicker("Undetected color", MochaColorScheme.Blue.argb)
    private val `color$endangered` by colors.colorPicker("Endangered color", MochaColorScheme.Mauve.argb)
    private val `color$elusive` by colors.colorPicker("Elusive color", MochaColorScheme.Yellow.argb)

    private val animals = setOf(Cow::class, Pig::class, Sheep::class, Chicken::class, Rabbit::class, Horse::class)
    private val startRegex = Regex("\\[NPC] Trevor: You can find your (?<type>\\w+) animal near the .*")

    private var cooldown: Long = 0
    private var rarity: Rarity? = null

    init {
        on<LocationEvent.Server.Connect> {
            reset()
        }

        on<WorldRenderEvent.Entity> {
            if (!esp) return@on

            val rarity = rarity ?: return@on
            val entity = entity as? LivingEntity ?: return@on
            if (entity::class !in animals) return@on

            val max = if (entity is Horse) entity.serverMaxHealth / 2f else entity.serverMaxHealth
            if (max != rarity.hp) return@on

            ParallaxBox.frame(entity.renderAABB(), rarity.color, depth = false)

            if (`esp$tracer`) {
                ParallaxLine.tracer(entity.renderVec3().toVector3f(), rarity.color)
            }
        }

        on<MessageEvent.Chat.Receive> {
            startRegex.matchEntire(stripped)?.let { result ->
                val rarity1 = result.groupValues[1]
                rarity = Rarity.get(rarity1) ?: return@let
                cooldown = System.currentTimeMillis() + 20_000

                IonicScheduler.schedule(20.seconds) {
                    cooldown = 0
                    if (!endAlert) return@schedule

                    `alert$sound`.play()
                    MinecraftTitle.display(`alert$message`)
                }

                return@on
            }

            if (stripped == "Return to the Trapper soon to get a new animal to hunt!") {
                if (!autoCall) return@on reset()

                val time = (cooldown - System.currentTimeMillis() - (callOff * 1000).toLong()).coerceAtLeast(0)
                val extra = (callDelay + (0..2).random()) * 50L

                IonicScheduler.schedule((time + extra).milliseconds) {
                    MinecraftCommands.client("/call trevor")
                }

                reset()
                return@on
            }

            if (!autoAccept) return@on
            if (message.siblings.getOrNull(0)?.stripped() != "Accept the trapper's task to hunt the animal?") return@on

            IonicScheduler.schedule((acceptDelay + (0..2).random()).clientTicks) {
                val command = (message.siblings[3].style.clickEvent as? ClickEvent.RunCommand)?.command ?: return@schedule

                MinecraftCommands.server(command)
            }
        }
    }

    private fun reset() {
        rarity = null
    }

    private enum class Rarity(val normal: Float, val derpy: Float) {
        Trackable(100f, 200f),
        Untrackable(500f, 1000f),
        Undetected(1000f, 2000f),
        Endangered(5000f, 10000f),
        Elusive(10000f, 20000f);

        val hp: Float
            get() = normal

        val color: Int
            get() = when (this) {
                Trackable -> `color$trackable`
                Untrackable -> `color$untrackable`
                Undetected -> `color$undetected`
                Endangered -> `color$endangered`
                Elusive -> `color$elusive`
            }

        companion object {
            fun get(string: String): Rarity? {
                return entries.find { it.name.uppercase() == string }
            }
        }
    }
}
