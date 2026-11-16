package foo.starred.ionic.event.impl

import foo.starred.ionic.event.core.IonicEvent
import foo.starred.kbus.data.event.traits.KBusCancellableTrait
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonInfo

sealed class InputEvent {
    sealed class Keyboard {
        data class Press(
            val keyEvent: KeyEvent
        ) : IonicEvent(), KBusCancellableTrait

        data class Release(
            val keyEvent: KeyEvent
        ) : IonicEvent()
    }

    sealed class Mouse {
        data class Press(
            val buttonInfo: MouseButtonInfo
        ) : IonicEvent(), KBusCancellableTrait

        data class Release(
            val buttonInfo: MouseButtonInfo
        ) : IonicEvent()
    }
}
