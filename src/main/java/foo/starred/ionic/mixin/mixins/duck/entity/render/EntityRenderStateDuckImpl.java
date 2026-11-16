package foo.starred.ionic.mixin.mixins.duck.entity.render;

import foo.starred.ionic.duck.entity.render.EntityRenderStateDuck;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateDuckImpl implements EntityRenderStateDuck {
    @Unique
    private Entity ionic$entity;

    @Override
    public Entity ionic$entity() {
        return this.ionic$entity;
    }

    @Override
    public void ionic$entity(Entity entity) {
        this.ionic$entity = entity;
    }
}
