package foo.starred.ionic.event.impl

import foo.starred.ionic.event.core.IonicEvent
import foo.starred.kbus.data.event.traits.KBusCancellableTrait
import net.minecraft.network.protocol.Packet

sealed class PacketEvent(open val packet: Packet<*>) : IonicEvent(), KBusCancellableTrait {
    sealed class Process(override val packet: Packet<*>) : PacketEvent(packet) {
        data class Pre(
            override val packet: Packet<*>
        ) : Process(packet)

        data class Post(
            override val packet: Packet<*>
        ) : Process(packet)
    }

    data class Receive(
        override val packet: Packet<*>
    ) : PacketEvent(packet)

    data class Send(
        override val packet: Packet<*>
    ) : PacketEvent(packet)
}
