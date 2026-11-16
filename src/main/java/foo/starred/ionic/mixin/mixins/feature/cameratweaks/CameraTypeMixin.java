package foo.starred.ionic.mixin.mixins.feature.cameratweaks;

import foo.starred.ionic.module.impl.render.CameraTweaks;
import net.minecraft.client.CameraType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CameraType.class)
public class CameraTypeMixin {
    @Inject(method = "cycle", at = @At("HEAD"), cancellable = true)
    private void ionic$cycle(CallbackInfoReturnable<CameraType> cir) {
        if (!CameraTweaks.getFront()) return;
        final CameraType self = ionic$self();

        cir.setReturnValue(switch (self) {
            case FIRST_PERSON -> CameraType.THIRD_PERSON_BACK;
            case THIRD_PERSON_BACK, THIRD_PERSON_FRONT -> CameraType.FIRST_PERSON;
        });
    }

    @Unique
    private CameraType ionic$self() {
        return (CameraType) (Object) this;
    }
}
