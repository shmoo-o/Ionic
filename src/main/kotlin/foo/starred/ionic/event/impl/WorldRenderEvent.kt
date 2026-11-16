package foo.starred.ionic.event.impl

import com.mojang.blaze3d.vertex.PoseStack
import foo.starred.ionic.event.core.IonicEvent
import foo.starred.kbus.data.event.traits.KBusCancellableTrait
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.state.EntityRenderState

sealed class WorldRenderEvent {
    class Entity(
        val state: EntityRenderState,
        val entity: net.minecraft.world.entity.Entity?
    ) : IonicEvent(), KBusCancellableTrait

    data class Render(
        val pose: PoseStack,
        val collector: SubmitNodeCollector
    ) : IonicEvent()

    data object Extract : IonicEvent()
}
