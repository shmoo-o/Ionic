@file:Suppress("FunctionName")

package foo.starred.ionic.duck.entity.render

import net.minecraft.world.entity.Entity

interface EntityRenderStateDuck {
    fun `ionic$entity`(): Entity?
    fun `ionic$entity`(entity: Entity?)
}
