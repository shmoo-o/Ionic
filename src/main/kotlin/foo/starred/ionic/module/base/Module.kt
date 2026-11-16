@file:Suppress("PropertyName")

package foo.starred.ionic.module.base

import foo.starred.ionic.annotation.impl.IonicModuleScope
import foo.starred.ionic.config.dsl.impl.builders.config.ConfigMainBuilder
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.event.core.IonicEvent
import foo.starred.ionic.event.impl.PacketEvent
import foo.starred.kbus.data.node.KBusNode
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.api.data.Observable
import foo.starred.kontext.api.data.Observable.Companion.and
import foo.starred.kontext.api.format.extension.string.camelCased
import net.minecraft.network.protocol.Packet

open class Module(
    name: String,
    description: String,
    category: ConfigCategory,
    default: Boolean = false
) {
    private val _location: Observable<Boolean> = IonicModuleScope.get(this::class)

    val _key: String = name.camelCased()
    val config: ConfigMainBuilder = ConfigMainBuilder(_key, name, description, category, default).also { it.module = this }
    val observable: Observable<Boolean> = config.state and _location

    val enabled: Boolean
        get() = observable.value

    protected inline fun <reified T : IonicEvent> on(priority: Int = 0, noinline handler: T.() -> Unit): KBusNode<*> {
        return foo.starred.ionic.event.core.on<T>(priority, handler).runWhen(observable)
    }

    protected inline fun <reified E : PacketEvent, reified P : Packet<*>> on(priority: Int = 0, noinline handler: P.(E) -> Unit): KBusNode<*> {
        return foo.starred.ionic.event.core.on<E, P>(priority, handler).runWhen(observable)
    }
}
