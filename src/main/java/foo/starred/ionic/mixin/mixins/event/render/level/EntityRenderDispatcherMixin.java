package foo.starred.ionic.mixin.mixins.event.render.level;

import com.mojang.blaze3d.vertex.PoseStack;
import foo.starred.ionic.duck.entity.render.EntityRenderStateDuck;
import foo.starred.ionic.event.impl.WorldRenderEvent;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "extractEntity", at = @At("RETURN"))
    private void ionic$extractEntity(Entity entity, float partialTicks, CallbackInfoReturnable<EntityRenderState> cir) {
        ((EntityRenderStateDuck) cir.getReturnValue()).ionic$entity(entity);
    }

    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    private void ionic$submit$pre(EntityRenderState state, CameraRenderState camera, double x, double y, double z, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CallbackInfo ci) {
        if (new WorldRenderEvent.Entity(state, ((EntityRenderStateDuck) state).ionic$entity()).post()) ci.cancel();
    }
}
