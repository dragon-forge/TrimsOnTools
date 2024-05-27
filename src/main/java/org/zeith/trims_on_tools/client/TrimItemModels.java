package org.zeith.trims_on_tools.client;

import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.trims_on_tools.api.*;
import org.zeith.trims_on_tools.init.SheetsToT;
import org.zeith.trims_on_tools.proxy.ClientProxyToT;

import java.util.*;
import java.util.function.Function;

public class TrimItemModels
{
	static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
	private static final Map<Item, ToolType> TOOL_TYPE_CACHE = new HashMap<>();
	private static final Map<ModelSettings, BakedModel> CACHE = new HashMap<>();
	private static ModelBakerImpl baker;
	
	private static final RenderType TRANSLUCENT_ITEM_CULL_TRIM_SHEET = RenderType.itemEntityTranslucentCull(SheetsToT.TOOL_TRIMS_SHEET);
	
	public static BakedModel getModel(RegistryAccess access, ItemStack toolItem, ToolTrim trim)
	{
		var texture = trim.texture(toolItem, TOOL_TYPE_CACHE.computeIfAbsent(toolItem.getItem(), it ->
				ToolType.getFromTool(access, toolItem)
						.map(Holder.Reference::value)
						.orElse(null))
		);
		if(texture == null) return null;
		
		TrimGlowData glowData = TrimGlowData.getGlowData(access, toolItem).orElse(null);
		
		ModelSettings ms = new ModelSettings(texture, glowData != null && glowData.glow());
		BakedModel model = CACHE.get(ms);
		if(model != null || baker == null) return model;
		var gen = generateModel(ms);
		CACHE.put(ms, gen);
		return gen;
	}
	
	private static BakedModel generateModel(final ModelSettings settings)
	{
		final var texture = settings.texture();
		final var emissive = settings.emissive();
		
		BlockModel blockmodel = BlockModel.fromString("""
				{
					"parent": "item/generated",
					"textures": {
						"layer0": "%s"
					}
				}""".formatted(texture));
		
		baker.overrides.put(texture, blockmodel);
		
		Transformation transformation = new Transformation(new Matrix4f()
				.scale(1.005F)
		);
		
		Variant var = new Variant(texture, transformation, false, 0);
		var gen = Cast.or(
				baker.bake(texture, var),
				Minecraft.getInstance().getModelManager().getMissingModel()
		);
		
		List<BakedQuad> allQuads = new ArrayList<>(gen.getQuads(null, null, RandomSource.create()));
		
		if(emissive) QuadTransformers.settingMaxEmissivity().processInPlace(allQuads);
		
		return new BakedModelWrapper<>(gen)
		{
			@Override
			public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand)
			{
				return allQuads;
			}
			
			@Override
			public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType)
			{
				return allQuads;
			}
			
			@Override
			public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous)
			{
				return List.of(TRANSLUCENT_ITEM_CULL_TRIM_SHEET);
			}
		};
	}
	
	public static void reload()
	{
		CACHE.clear();
		
		baker = new ModelBakerImpl(
				Minecraft.getInstance().getModelManager().getModelBakery(),
				mat ->
				{
					var atlas = ClientProxyToT.getToolTrimsAtlas();
					TextureAtlasSprite sprite = atlas.getSprite(mat.texture());
					return sprite;
				}
		);
	}
	
	@OnlyIn(Dist.CLIENT)
	static class ModelBakerImpl
			implements ModelBaker
	{
		private final Map<ResourceLocation, UnbakedModel> overrides = new HashMap<>();
		private final ModelBakery bakery;
		private final Function<Material, TextureAtlasSprite> modelTextureGetter;
		
		ModelBakerImpl(ModelBakery bakery, Function<Material, TextureAtlasSprite> modelTextureGetter)
		{
			this.bakery = bakery;
			this.modelTextureGetter = modelTextureGetter;
		}
		
		@Override
		public UnbakedModel getModel(ResourceLocation location)
		{
			return overrides.getOrDefault(location, bakery.getModel(location));
		}
		
		@Override
		public Function<Material, TextureAtlasSprite> getModelTextureGetter()
		{
			return this.modelTextureGetter;
		}
		
		@Override
		public BakedModel bake(ResourceLocation location, ModelState state)
		{
			return bake(location, state, this.modelTextureGetter);
		}
		
		@Override
		public BakedModel bake(ResourceLocation location, ModelState state, Function<Material, TextureAtlasSprite> sprites)
		{
			UnbakedModel model = this.getModel(location);
			
			if(model instanceof BlockModel blockmodel)
			{
				return ITEM_MODEL_GENERATOR.generateBlockModel(sprites, blockmodel)
						.bake(this, blockmodel, sprites, state, location, false);
			}
			
			return model.bake(this, sprites, state, location);
		}
	}
	
	private record ModelSettings(ResourceLocation texture, boolean emissive)
	{
	}
}