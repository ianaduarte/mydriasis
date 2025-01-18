package dev.ianaduarte.mydriasis.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.ianaduarte.mydriasis.Mydriasis;
import dev.ianaduarte.mydriasis.compat.CompatLayer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow @Final private Minecraft minecraft;
	@Unique private double brightnessAdjustmentPrev = 1f;
	@Unique private double brightnessAdjustment = 1f;
	
	@Unique
	private static float getBrightness(Level level, BlockPos at, LightLayer layer) {
		if(CompatLayer.compatLayer != null) {
			var cl = CompatLayer.compatLayer;
			return switch(layer){
				case BLOCK -> cl.hasBlockLight(level)? cl.getBlockLight(level, at) : 0;
				case SKY   -> cl.hasSkyLight  (level)? cl.getSkyLight  (level, at) : 0;
			};
		}
		LightEngineAccessor accessor = (LightEngineAccessor)level.getLightEngine();
		LightEngine<?, ?> engine = switch(layer){
			case BLOCK -> accessor.getBlockEngine();
			case SKY   -> accessor.getSkyEngine();
		};
		
		return engine == null? 0 : engine.getLightValue(at);
	}
	
	@Inject(method = "renderLevel", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/GameRenderer;pick(F)V",
		shift = At.Shift.AFTER
	))
	private void setShaderAttenuation(DeltaTracker deltaTracker, CallbackInfo ci, @Local(ordinal = 0) float f) {
		CompiledShaderProgram compiledShaderProgram = Objects.requireNonNull(
			RenderSystem.setShader(CoreShaders.LIGHTMAP), "Lightmap shader not loaded"
		);
		compiledShaderProgram.safeGetUniform("MydriasisFactor").set((float) Mth.lerp(f, brightnessAdjustmentPrev, brightnessAdjustment));
	}
	
	@Inject(method = "tick", at = @At("HEAD"))
	private void fetchAttenuation(CallbackInfo ci) {
		Player player = this.minecraft.player;
		if(player == null) return;
		
		Level level = player.level();
		BlockPos eyePos = Mydriasis.playerEyePos(player, 1);
		BlockPos feetPos = Mydriasis.playerFeetPos(player, 1);
		
		float forecast = 1 - (((level.getRainLevel(1) * 0.25f) + (level.getThunderLevel(1) * 0.75f)) * 0.125f);
		float moonBrightness = level.getMoonBrightness();
		
		float skyFactor = Mydriasis.gradient(Mydriasis.getDaytime(level), 0.1f * moonBrightness, 0.75f, 1.0f, 0.75f, 0.1f * moonBrightness) * forecast;
		float l1 = Math.max(getBrightness(level,  eyePos, LightLayer.BLOCK), (getBrightness(level,  eyePos, LightLayer.SKY) * skyFactor));
		float l2 = Math.max(getBrightness(level, feetPos, LightLayer.BLOCK), (getBrightness(level, feetPos, LightLayer.SKY) * skyFactor));
		float playerLight = (l1 + l2) * 0.5f * Mydriasis.INV_LIGHT_MAX;
		
		this.brightnessAdjustmentPrev = this.brightnessAdjustment;
		this.brightnessAdjustment = Mth.lerp(0.1, this.brightnessAdjustment, Mydriasis.gradient(playerLight, 2.5f, 1, 0.90f));
	}
}
