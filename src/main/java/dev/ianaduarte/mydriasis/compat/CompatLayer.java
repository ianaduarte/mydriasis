package dev.ianaduarte.mydriasis.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface CompatLayer {
	CompatLayer compatLayer = loadCompat();
	
	boolean hasSkyLight(Level level);
	boolean hasBlockLight(Level level);
	int getSkyLight(Level level, BlockPos pos);
	int getBlockLight(Level level, BlockPos pos);
	
	private static CompatLayer loadCompat() {
		if(FabricLoader.getInstance().isModLoaded("moonrise")) {
			return new MoonriseCompat();
		}
		else if(FabricLoader.getInstance().isModLoaded("scalablelux")) {
			return new ScalableLuxCompat();
		}
		return null;
	}
}
