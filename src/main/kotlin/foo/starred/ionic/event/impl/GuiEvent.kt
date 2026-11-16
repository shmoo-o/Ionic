package foo.starred.ionic.event.impl

import foo.starred.ionic.event.core.IonicEvent
import foo.starred.kbus.data.event.traits.KBusCancellableTrait
import foo.starred.kontext.api.format.extension.string.stripped
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent

sealed class GuiEvent {
    sealed class Render {
        sealed class Any {
            data class Pre(
                val graphics: GuiGraphicsExtractor
            ) : IonicEvent()

            data class Main(
                val graphics: GuiGraphicsExtractor
            ) : IonicEvent()

            data class Post(
                val graphics: GuiGraphicsExtractor
            ) : IonicEvent()
        }

        sealed class Screen {
            data class Pre(
                val graphics: GuiGraphicsExtractor
            ) : IonicEvent(), KBusCancellableTrait

            data class Post(
                val graphics: GuiGraphicsExtractor
            ) : IonicEvent()
        }
    }

    sealed class Open {
        data class Container(
            val screen: AbstractContainerScreen<*>
        ) : IonicEvent() {
            val stripped = screen.title.stripped()
        }

        data class Any(
            val screen: Screen
        ) : IonicEvent() {
            val stripped = screen.title.stripped()
        }
    }

    sealed class Close {
        data class Container(
            val screen: AbstractContainerScreen<*>
        ) : IonicEvent() {
            val stripped = screen.title.stripped()
        }

        data class Any(
            val screen: Screen
        ) : IonicEvent() {
            val stripped = screen.title.stripped()
        }
    }

    sealed class Input {
        sealed class Key {
            data class Press(
                val keyEvent: KeyEvent
            ) : IonicEvent(), KBusCancellableTrait

            data class Release(
                val keyEvent: KeyEvent
            ) : IonicEvent()
        }

        sealed class Mouse {
            data class Press(
                val keyEvent: MouseButtonEvent
            ) : IonicEvent(), KBusCancellableTrait

            data class Release(
                val keyEvent: MouseButtonEvent
            ) : IonicEvent()

            data class Scroll(
                val amount: Double
            ) : IonicEvent(), KBusCancellableTrait
        }
    }
}
