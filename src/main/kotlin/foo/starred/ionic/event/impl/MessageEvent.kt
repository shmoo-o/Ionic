package foo.starred.ionic.event.impl

import foo.starred.ionic.event.core.IonicEvent
import foo.starred.kbus.data.event.traits.KBusCancellableTrait
import foo.starred.kontext.api.format.extension.string.stripped
import net.minecraft.network.chat.Component


sealed class MessageEvent {
    sealed class Chat {
        data class Intercept(val message: Component) : IonicEvent(), KBusCancellableTrait {
            val stripped = message.stripped()
        }

        data class Receive(val message: Component) : IonicEvent() {
            val stripped = message.stripped()
        }
    }
}
