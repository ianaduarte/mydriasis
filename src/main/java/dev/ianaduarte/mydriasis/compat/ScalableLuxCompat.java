package dev.ianaduarte.mydriasis.compat;

import ca.spottedleaf.starlight.common.light.StarLightLightingProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ScalableLuxCompat implements CompatLayer {
	@Override
	public boolean hasSkyLight(Level level) {
		return ((StarLightLightingProvider)level.getLightEngine()).getLightEngine().hasSkyLight();
	}
	@Override
	public boolean hasBlockLight(Level level) {
		return ((StarLightLightingProvider)level.getLightEngine()).getLightEngine().hasBlockLight();
	}
	
	@Override
	public int getSkyLight(Level level, BlockPos pos) {
		return ((StarLightLightingProvider)level.getLightEngine()).getLightEngine().getSkyLightValue(pos, level.getChunk(pos));
	}
	@Override
	public int getBlockLight(Level level, BlockPos pos) {
		return ((StarLightLightingProvider)level.getLightEngine()).getLightEngine().getBlockLightValue(pos, level.getChunk(pos));
	}
}
