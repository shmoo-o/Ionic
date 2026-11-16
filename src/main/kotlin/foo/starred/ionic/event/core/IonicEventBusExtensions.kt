package foo.starred.ionic.event.core

import foo.starred.ionic.Ionic
import foo.starred.ionic.event.impl.PacketEvent
import foo.starred.kbus.data.event.base.KBusEvent
import foo.starred.kbus.data.node.KBusNode
import foo.starred.kbus.extensions.on
import net.minecraft.network.protocol.Packet

inline fun <reified T : KBusEvent> on(priority: Int = 0, noinline handler: T.() -> Unit): KBusNode<T> {
    return Ionic.BUS.on(priority, handler)
}

inline fun <reified E : PacketEvent, reified P : Packet<*>> on(priority: Int = 0, noinline handler: P.(E) -> Unit): KBusNode<*> {
    return on<E>(priority) {
        (packet as? P)?.handler(this)
    }
}
