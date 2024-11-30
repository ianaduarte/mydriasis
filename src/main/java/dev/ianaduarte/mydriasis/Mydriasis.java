package dev.ianaduarte.mydriasis;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ModInitializer;

public class Mydriasis implements ModInitializer {
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
	/// @return Visual time of day, for some ungodly reason there's an offset to the sun and moon;<br> I HATE MODULAR ARITHMETIC!!
	public static float getDaytime(Level level) {
		return wrap(level.dayTime() + 6000, 0, 24_000) / 24_000f;
	}
	public static Vector3f tonemap(Vector3f v) {
		//return v.mul(1.1f, 1.075f, 1.0f).mul(v);
		return v.mul(1.06f, 1.04f, 1.01f).mul(v);
	}

	@Override
	public void onInitialize() {}
}