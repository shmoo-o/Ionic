@file:Suppress("PropertyName")

package foo.starred.ionic.module.impl.general

import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.event.impl.InputEvent
import foo.starred.ionic.event.impl.TickEvent
import foo.starred.ionic.module.base.Module
import foo.starred.kbus.extensions.override
import foo.starred.kontext.abstraction.client.extension.boundKey
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.gui.MinecraftGui
import foo.starred.kontext.abstraction.input.impl.GenericInputState
import net.minecraft.client.KeyMapping

@LoadableIonicModule
object SnapTap : Module(
    "Snap-Tap",
    "Snap Tap allows you to strafe while continuing to hold the initial key and quickly tapping the opposing key.",
    ConfigCategory.GENERAL
) {
    private val active = HashSet<Int>(4)
    private val pairs = ArrayList<Pair>(4)

    private data class Pair(
        val _current: KeyMapping,
        val _opposite: KeyMapping
    ) {
        val current: Int
            get() = _current.boundKey().value

        val opposite: Int
            get() = _opposite.boundKey().value
    }

    init {
        on<InputEvent.Keyboard.Press> {
            if (MinecraftGui.screen != null) return@on

            val key = keyEvent.key()
            if (!active.add(key)) return@on

            key.pair(false)
        }

        on<InputEvent.Keyboard.Release> {
            if (MinecraftGui.screen != null) {
                active.clear()
                return@on
            }

            val key = keyEvent.key()
            if (!active.remove(key)) return@on

            key.pair(true)
        }

        on<TickEvent.Client.End> {
            val options = client.options

            with(pairs) {
                add(Pair(options.keyLeft, options.keyRight))
                add(Pair(options.keyRight, options.keyLeft))
                add(Pair(options.keyUp, options.keyDown))
                add(Pair(options.keyDown, options.keyUp))
            }
        }.override().once()
    }

    private fun Int.pair(down: Boolean) {
        for (pair in pairs) {
            if (pair.current != this) continue
            if (pair.opposite !in active) return

            GenericInputState.press(pair._opposite, down)
            return
        }
    }
}
