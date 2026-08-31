package dev.diberv.gwCore.mixin;


import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    //отменяем выполнение рендера таба как оверлея
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void disTabOverlay(final GuiGraphicsExtractor graphics, final int screenWidth, final Scoreboard scoreboard, final @Nullable Objective displayObjective, CallbackInfo ci) {
        ci.cancel();
    }
}
