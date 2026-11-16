@file:Suppress("FunctionName", "Unused")

package foo.starred.ionic.duck.entity.generic

import foo.starred.kontext.api.format.extension.string.stripped
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import java.lang.ref.WeakReference

interface EntityDuck {
    fun `ionic$attachments`(): MutableList<WeakReference<Entity>>
    fun `ionic$attach`(): Entity?
    fun `ionic$attach`(entity: Entity)

    companion object {
        val Entity.parent: Entity?
            get() = (this as? EntityDuck)?.`ionic$attach`()

        val Entity.attached: List<Entity>
            get() = (this as? EntityDuck)?.`ionic$attachments`()?.mapNotNull { it.get() } ?: emptyList()

        val Entity.attachedNames: List<Component>
            get() = attached.mapNotNull { it.customName }

        val Entity.attachedStripped: List<String>
            get() = attached.mapNotNull { it.customName?.stripped() }
    }
}
