@file:Suppress("Unused")

package foo.starred.ionic.module.impl.general.wardrobe.auto.impl

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.ionic.annotation.impl.IonicModuleScope
import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.api.command.command
import foo.starred.ionic.api.messaging.impl.MessagingAPI.displayAsMod
import foo.starred.ionic.api.time.IonicScheduler
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.event.impl.GuiEvent
import foo.starred.ionic.event.impl.InputEvent
import foo.starred.ionic.event.impl.TickEvent
import foo.starred.ionic.module.base.Module
import foo.starred.ionic.module.impl.general.wardrobe.auto.data.AutoWardrobeSlot
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.chat.command.MinecraftCommands
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.gui.MinecraftGui
import foo.starred.kontext.api.data.Observable
import foo.starred.kontext.api.format.extension.string.stripped
import foo.starred.kontext.api.scheduling.scheduler.extensions.clientTicks
import foo.starred.kontext.api.scheduling.scheduler.extensions.start

@LoadableIonicModule
@IonicModuleScope(skyblock = true)
object AutoWardrobe : Module(
    "Auto wardrobe",
    "Automatically equips your wardrobe slots!",
    ConfigCategory.GENERAL
) {
    private val equipDelay by config.slider("Equip delay", 1, 1, 8, "ticks")
    private val closeDelay by config.slider("Close delay", 1, 1, 8, "ticks")
    private val variance by config.slider("Max delay variety", 1, 1, 5, "ticks")

    private val keybinds by config.group("Keybinds")
    val key0 by keybinds.keybind("Slot 1", InputConstants.UNKNOWN.value)
    val key1 by keybinds.keybind("Slot 2", InputConstants.UNKNOWN.value)
    val key2 by keybinds.keybind("Slot 3", InputConstants.UNKNOWN.value)
    val key3 by keybinds.keybind("Slot 4", InputConstants.UNKNOWN.value)
    val key4 by keybinds.keybind("Slot 5", InputConstants.UNKNOWN.value)
    val key5 by keybinds.keybind("Slot 6", InputConstants.UNKNOWN.value)
    val key6 by keybinds.keybind("Slot 7", InputConstants.UNKNOWN.value)
    val key7 by keybinds.keybind("Slot 8", InputConstants.UNKNOWN.value)
    val key8 by keybinds.keybind("Slot 9", InputConstants.UNKNOWN.value)

    private val menuRegex: Regex = Regex("^\\((?<cur>\\d)/(?<max>\\d)\\) Armor Sets$")
    private val open: Observable<Boolean> = Observable(false)

    private var slot: AutoWardrobeSlot? = null
    private var swapping: Boolean = false
    private var delay: Int = 0

    init {
        command {
            "auto" / "wardrobe" / int("slot", 1, 9) {
                if (!enabled) return@int "Enable auto wardrobe!".displayAsMod()
                val int = int("slot")

                slot = AutoWardrobeSlot.ALL.find { it.index == 35 + int } ?: return@int
                delay = equipDelay + (0..variance).random()
                swapping = true
            }
        }

        on<InputEvent.Keyboard.Press> {
            if (MinecraftGui.screen != null) return@on
            if (swapping) return@on

            slot = AutoWardrobeSlot.ALL.find { it.keybind().value == keyEvent.key } ?: return@on
            delay = equipDelay + (0..variance).random()
            swapping = true

            MinecraftCommands.client("wd")
            cancel()
        }

        on<GuiEvent.Open.Container> {
            if (!menuRegex.matches(stripped)) return@on
            if (!swapping) return@on

            open.value = true
        }

        on<TickEvent.Client.Start> {
            val title = MinecraftGui.screen?.title?.stripped() ?: return@on
            if (!menuRegex.matches(title)) {
                reset()
                return@on
            }

            val player = client.player ?: return@on
            val slot = slot ?: return@on
            if (slot.slot?.item?.isEmpty != false) return@on

            val id = player.containerMenu.containerId
            if (!slot.equipped) {
                MinecraftGui.click(id, slot.index)
            }

            IonicScheduler.schedule((closeDelay + (0..variance).random()).clientTicks.start) {
                val player = client.player ?: return@schedule
                if (player.containerMenu.containerId != id) return@schedule

                player.closeContainer()
            }

            reset()
        }.runWhen(open)

        on<GuiEvent.Input.Key.Press> {
            cancel()
        }.runWhen(open)

        on<GuiEvent.Input.Mouse.Press> {
            cancel()
        }.runWhen(open)

        on<GuiEvent.Close.Container> {
            if (!menuRegex.matches(stripped)) return@on
            if (!swapping) return@on

            reset()
        }
    }

    private fun reset() {
        open.value = false
        swapping = false
        delay = 0
    }
}
