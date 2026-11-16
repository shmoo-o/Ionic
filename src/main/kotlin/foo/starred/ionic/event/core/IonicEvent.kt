package foo.starred.ionic.event.core

import foo.starred.ionic.Ionic
import foo.starred.kbus.data.event.base.KBusEvent

abstract class IonicEvent : KBusEvent() {
    @Suppress("NOTHING_TO_INLINE")
    inline fun post(): Boolean {
        return post(Ionic.BUS)
    }
}
