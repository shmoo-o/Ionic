package foo.starred.ionic.event.impl

import foo.starred.ionic.event.core.IonicEvent
import foo.starred.kontext.api.format.extension.string.stripped
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand

sealed class EntityEvent {
    data class Load(
        val entity: Entity
    ) : IonicEvent()

    data class Unload(
        val entity: Entity
    ) : IonicEvent()

    data class Death(
        val entity: Entity
    ) : IonicEvent()

    sealed class Update {
        data class Attach(
            val component: Component,
            val entity: Entity
        ) : IonicEvent() {
            val stripped: String by lazy(LazyThreadSafetyMode.NONE) {
                component.stripped()
            }
        }

        data class Named(
            val component: Component,
            val entity: ArmorStand
        ) : IonicEvent() {
            val stripped: String by lazy(LazyThreadSafetyMode.NONE) {
                component.stripped()
            }
        }

    }
}
