package foo.starred.ionic.mixin.mixins.feature.autosprint;

import foo.starred.ionic.module.impl.general.AutoSprint;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Input;<init>(ZZZZZZZ)V"), index = 6)
    private boolean ionic$tick(boolean sprint) {
        return sprint || AutoSprint.INSTANCE.getSprint();
    }
}
