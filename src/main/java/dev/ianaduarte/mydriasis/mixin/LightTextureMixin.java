package dev.ianaduarte.mydriasis.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import dev.ianaduarte.mydriasis.LightAttenuationGetter;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MappableRingBuffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public class LightTextureMixin {
	@Shadow @Final private GameRenderer renderer;
	@Unique private static final int INFO_BUFFER_SIZE = new Std140SizeCalculator().putFloat().get();
	@Unique private final MappableRingBuffer infoBuffer = new MappableRingBuffer(
		() -> "Mydriasis Info UBO",
		GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE,
		INFO_BUFFER_SIZE
	);
	
	@Inject(
		method = "updateLightTexture",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lcom/mojang/blaze3d/systems/GpuDevice;createCommandEncoder()Lcom/mojang/blaze3d/systems/CommandEncoder;",
			shift = At.Shift.AFTER
		)
	)
	private void setInfo(float partialTicks, CallbackInfo ci, @Local CommandEncoder commandEncoder) {
		try(GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(this.infoBuffer.currentBuffer(), false, true)) {
			Std140Builder.intoBuffer(mappedView.data())
				.putFloat(((LightAttenuationGetter)renderer).getAttenuation(partialTicks));
		}
	}
	
	@Inject(
		method = "updateLightTexture",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/systems/RenderSystem;bindDefaultUniforms(Lcom/mojang/blaze3d/systems/RenderPass;)V",
			shift = At.Shift.AFTER
		)
	)
	private void sendUniform(float partialTicks, CallbackInfo ci, @Local RenderPass renderPass) {
		renderPass.setUniform("MydriasisInfo", this.infoBuffer.currentBuffer());
	}
	
	@Inject(
		method = "updateLightTexture",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/MappableRingBuffer;rotate()V",
			shift = At.Shift.AFTER
		)
	)
	private void rotateBuffer(float partialTicks, CallbackInfo ci) {
		this.infoBuffer.rotate();
	}
}
