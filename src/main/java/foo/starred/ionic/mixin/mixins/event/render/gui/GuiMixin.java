package foo.starred.ionic.mixin.mixins.event.render.gui;

import foo.starred.ionic.event.impl.GuiEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if >= 26.2 'gui.Gui.class' -> 'gui.Hud.class'
@Mixin(net.minecraft.client.gui.Gui.class)
public abstract class GuiMixin {
    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void ionic$render$pre(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        new GuiEvent.Render.Any.Pre(graphics).post();
    }

    //~ if >= 26.2 'gui/Gui;' -> 'gui/Hud;'
    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractSleepOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    private void ionic$render$main(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        new GuiEvent.Render.Any.Main(graphics).post();
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void ionic$render$post(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        new GuiEvent.Render.Any.Post(graphics).post();
    }
}
