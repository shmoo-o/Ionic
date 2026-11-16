package foo.starred.ionic.mixin.mixins.event.input;

import com.mojang.blaze3d.platform.InputConstants;
import foo.starred.ionic.event.impl.InputEvent;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
    private void ionic$onButton(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
        if (rawButtonInfo.button() == InputConstants.UNKNOWN.getValue()) return;

        if (action == 1) {
            if (new InputEvent.Mouse.Press(rawButtonInfo).post()) ci.cancel();
            return;
        }

        if (action == 0) {
            new InputEvent.Mouse.Release(rawButtonInfo).post();
        }
    }
}
