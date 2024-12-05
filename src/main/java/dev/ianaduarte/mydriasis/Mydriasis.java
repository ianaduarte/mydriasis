package dev.ianaduarte.mydriasis;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mydriasis implements ClientModInitializer {
	public static final String MOD_ID = "mydriasis";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	public static float INV_LIGHT_MAX = 1 / 15f;
	public static float gradient(float delta, float... values) {
		if(values.length == 0) throw new IllegalArgumentException("Gradient array cannot be empty.");
		if(delta <= 0) return values[0];
		if(delta >= 1) return values[values.length - 1];
		
		int index = (int)(delta * (values.length - 1));
		float t = delta * (values.length - 1) - index;
		return Mth.lerp(t, values[index], values[index + 1]);
	}
	public static long wrap(long x, long min, long max) {
		long range = (Math.max(max, min) - Math.min(min, max));
		return ((x % range) + range) % range + min;
	}
	
	public static BlockPos playerEyePos(Player player, float partialTicks) {
		Vec3 position = player.getPosition(partialTicks);
		int i = Mth.floor(position.x);
		int j = Mth.floor(position.y + player.getEyeHeight());
		int k = Mth.floor(position.z);
		return new BlockPos(i, j, k);
	}
	public static BlockPos playerFeetPos(Player player, float partialTicks) {
		Vec3 position = player.getPosition(partialTicks);
		int i = Mth.floor(position.x);
		int j = Mth.floor(position.y + 1);
		int k = Mth.floor(position.z);
		return new BlockPos(i, j, k);
	}
	public static float getDaytime(Level level) {
		return wrap(level.dayTime() + 6000, 0, 24_000) / 24_000f;
	}

	@Override
	public void onInitializeClient() {
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
			ResourceManagerHelper.registerBuiltinResourcePack(
				ResourceLocation.fromNamespaceAndPath(MOD_ID, "lightmap_patch"),
				modContainer,
				ResourcePackActivationType.DEFAULT_ENABLED
			);
		});
	}
}