package foo.starred.ionic.api.time

import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.event.core.on
import foo.starred.ionic.event.impl.TickEvent
import foo.starred.kontext.api.scheduling.scheduler.impl.AbstractScheduler

@LoadableIonicModule
object IonicScheduler : AbstractScheduler() {
    init {
        on<TickEvent.Client.Start> {
            client0()
        }

        on<TickEvent.Client.End> {
            client1()
        }

        on<TickEvent.Server> {
            server()
        }
    }
}
