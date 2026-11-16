package foo.starred.ionic.mixin.mixins.feature.cameratweaks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import foo.starred.ionic.module.impl.render.CameraTweaks;
import net.minecraft.client.Camera;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {
    @WrapOperation(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double ionic$setup(LivingEntity instance, Holder<Attribute> attribute, Operation<Double> original) {
        return CameraTweaks.getDistance$boolean() ? CameraTweaks.getDistance$blocks() : original.call(instance, attribute);
    }

    @Inject(method = "getMaxZoom", at = @At("HEAD"), cancellable = true)
    private void ionic$getMaxZoom(float cameraDist, CallbackInfoReturnable<Float> cir) {
        if (!CameraTweaks.getClip()) return;

        cir.setReturnValue(cameraDist);
    }
}
