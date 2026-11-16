package foo.starred.ionic.mixin.mixins.event.entity.die;

import foo.starred.ionic.event.impl.EntityEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setPose(Lnet/minecraft/world/entity/Pose;)V"))
    private void ionic$die(DamageSource source, CallbackInfo ci) {
        new EntityEvent.Death(ionic$self()).post();
    }

    @Unique
    private LivingEntity ionic$self() {
        return (LivingEntity) (Object) this;
    }
}
