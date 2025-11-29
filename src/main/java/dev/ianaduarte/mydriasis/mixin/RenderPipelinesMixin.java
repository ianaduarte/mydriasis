package dev.ianaduarte.mydriasis.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderPipelines.class)
public abstract class RenderPipelinesMixin {
	////TODO: there's no fucking way that this is right
	@Shadow
	public static RenderPipeline register(RenderPipeline pipeline) {
		return null;
	}
	@Shadow @Mutable @Final public static RenderPipeline LIGHTMAP = register(
			RenderPipeline.builder()
				.withLocation("pipeline/lightmap")
				.withVertexShader("core/screenquad")
				.withFragmentShader("core/lightmap")
				.withUniform("LightmapInfo", UniformType.UNIFORM_BUFFER)
				.withUniform("MydriasisInfo", UniformType.UNIFORM_BUFFER)
				.withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
				.withDepthWrite(false)
				.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
				.build()
		);
	
	//@Redirect(
	//	method = "<clinit>",
	//	at = @At(
	//		value = "FIELD",
	//		target = "Lnet/minecraft/client/renderer/RenderPipelines;LIGHTMAP:Lcom/mojang/blaze3d/pipeline/RenderPipeline;",
	//		opcode = Opcodes.PUTSTATIC,
	//		ordinal = 82
	//	)
	//)
	//private static RenderPipeline insertLightmapUniform(RenderPipeline value) {
	//	return register(
	//		RenderPipeline.builder()
	//			.withLocation("pipeline/lightmap")
	//			.withVertexShader("core/blit_screen")
	//			.withFragmentShader("core/lightmap")
	//			.withUniform("LightmapInfo", UniformType.UNIFORM_BUFFER)
	//			.withUniform("MydriasisInfo", UniformType.UNIFORM_BUFFER)
	//			.withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
	//			.withDepthWrite(false)
	//			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
	//			.build()
	//	);
	//}
}
