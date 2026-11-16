package foo.starred.ionic.event.dispatcher

import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.event.core.on
import foo.starred.ionic.event.impl.MessageEvent
import foo.starred.ionic.event.impl.PacketEvent
import foo.starred.ionic.event.impl.ScoreboardEvent
import foo.starred.kontext.abstraction.client.extension.mainThread
import foo.starred.kontext.core.event.impl.KontextEvents
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket

@LoadableIonicModule
object GenericEventDispatcher {
    init {
        on<PacketEvent.Receive, ClientboundSystemChatPacket> {
            if (overlay()) return@on

            mainThread {
                MessageEvent.Chat.Receive(content).post()
            }
        }

        KontextEvents.Scoreboard.UPDATE.register { event ->
            ScoreboardEvent.Update(event.lines0, event.lines1, event.components0, event.components1).post()
        }
    }
}
