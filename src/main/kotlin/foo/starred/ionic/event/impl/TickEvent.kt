package foo.starred.ionic.event.impl

import foo.starred.ionic.api.time.IonicScheduler
import foo.starred.ionic.event.core.IonicEvent

sealed class TickEvent {
    sealed class Client {
        data object Start : IonicEvent()

        data object End : IonicEvent() {
            val ticks: Int
                get() = IonicScheduler.ticks.client
        }
    }

    data object Server : IonicEvent() {
        val ticks: Int
            get() = IonicScheduler.ticks.server
    }
}
