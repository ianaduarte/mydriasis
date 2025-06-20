package dev.ianaduarte.mydriasis.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import dev.ianaduarte.mydriasis.LightAttenuationGetter;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public class LightTextureMixin {
	@Shadow @Final private GameRenderer renderer;
	
	@Inject(
		method = "updateLightTexture",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/systems/RenderPass;setPipeline(Lcom/mojang/blaze3d/pipeline/RenderPipeline;)V",
			shift = At.Shift.AFTER
		)
	)
	private void attenuateLight(float partialTicks, CallbackInfo ci, @Local RenderPass renderPass) {
		renderPass.setUniform("MydriasisFactor", ((LightAttenuationGetter)renderer).getAttenuation(partialTicks));
	}
}
