package org.zeith.trims_on_tools.client.geom;

import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.trims_on_tools.api.data.*;

import java.util.*;
import java.util.function.Function;

public class TrimItemModels
{
	static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
	private static final Map<Item, ToolType> TOOL_TYPE_CACHE = new HashMap<>();
	private static final Map<ModelSettings, BakedModel> CACHE = new HashMap<>();
	private static ModelBakerImpl baker;
	
	public static BakedModel getModel(RegistryAccess access, ItemStack toolItem, ToolTrim trim)
	{
		var texture = trim.texture(access, toolItem, TOOL_TYPE_CACHE.computeIfAbsent(toolItem.getItem(), it ->
				ToolType.getFromTool(access, toolItem)
						.map(Holder.Reference::value)
						.orElse(null))
		);
		
		if(baker == null || texture == null) return null;
		
		TrimGlowData glowData = TrimGlowData.getGlowData(toolItem).orElse(null);
		
		return CACHE.computeIfAbsent(
				new ModelSettings(texture, glowData != null && glowData.glow()),
				TrimItemModels::generateModel
		);
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
		
		var pi = gen.getParticleIcon(ModelData.EMPTY);
		if(pi == null || pi.contents().name().equals(MissingTextureAtlasSprite.getLocation()))
			return null;
		
		List<BakedQuad> allQuadsPre = new ArrayList<>(gen.getQuads(null, null, RandomSource.create()));
		if(emissive) QuadTransformers.settingMaxEmissivity().processInPlace(allQuadsPre);
		List<BakedQuad> allQuads = List.copyOf(allQuadsPre); // Unmodifiable copy plz thx
		
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
		};
	}
	
	public static void reload()
	{
		CACHE.clear();
		
		baker = new ModelBakerImpl(
				Minecraft.getInstance().getModelManager().getModelBakery(),
				Material::sprite
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
			return overrides.computeIfAbsent(location, bakery::getModel);
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