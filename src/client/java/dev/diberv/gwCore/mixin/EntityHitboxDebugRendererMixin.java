package dev.diberv.gwCore.mixin;

import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {
    @Inject(
            method = "showHitboxes",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disShowHitboxes(final Entity entity, final float partialTicks, final boolean isServerEntity, CallbackInfo ci) {
        ci.cancel();
    }

}
