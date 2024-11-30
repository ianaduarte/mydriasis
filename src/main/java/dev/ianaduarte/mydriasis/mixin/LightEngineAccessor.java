package dev.ianaduarte.mydriasis.mixin;

import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelLightEngine.class)
public interface LightEngineAccessor {
	@Accessor("blockEngine")
	@Nullable LightEngine<?, ?> getBlockEngine();
	@Accessor("skyEngine")
	@Nullable LightEngine<?, ?> getSkyEngine();
}
