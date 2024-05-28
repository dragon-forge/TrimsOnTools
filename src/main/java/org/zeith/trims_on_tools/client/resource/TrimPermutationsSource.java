package org.zeith.trims_on_tools.client.resource;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.zeith.trims_on_tools.TrimsOnToolsMod;
import org.zeith.trims_on_tools.mixins.client.SpriteSourcesAccessor;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

public record TrimPermutationsSource(boolean debug)
		implements SpriteSource
{
	static final Logger LOGGER = LogUtils.getLogger();
	
	public static final Codec<TrimPermutationsSource> CODEC = RecordCodecBuilder.create((inst) ->
			inst.group(
					Codec.BOOL.optionalFieldOf("debug", false).forGetter(TrimPermutationsSource::debug)
			).apply(inst, TrimPermutationsSource::new));
	
	public static final SpriteSourceType TRIM_PERMUTATIONS = SpriteSourcesAccessor.callRegister(TrimsOnToolsMod.id("trim_permutations").toString(), CODEC);
	
	public static final ResourceLocation PALETTE_KEY = new ResourceLocation("trims/color_palettes/trim_palette");
	public static final FileToIdConverter TRIM_LISTER = new FileToIdConverter("textures/trims_on_tools/trims", ".png");
	public static final FileToIdConverter TOOL_TRIM_MATERIALS = new FileToIdConverter("trims_on_tools/materials", ".json");
	
	@Override
	public void run(@NotNull ResourceManager manager, @NotNull Output output)
	{
		var textures = TRIM_LISTER.listMatchingResources(manager);
		Map<ResourceLocation, ResourceLocation> permutations = new HashMap<>();
		
		if(debug)
		{
			LOGGER.info("Located {} tool trim patterns.", textures.size());
			for(var key : textures.keySet())
				LOGGER.info("- {}", TRIM_LISTER.fileToId(key));
		} else LOGGER.debug("Located {} tool trim patterns.", textures.size());
		
		for(var entry : TOOL_TRIM_MATERIALS.listMatchingResources(manager).entrySet())
		{
			var res = entry.getValue();
			try(var reader = res.openAsReader())
			{
				var mat = PermutationMaterialFile.CODEC.decode(JsonOps.INSTANCE, GsonHelper.parse(reader))
						.result()
						.orElseThrow(StreamCorruptedException::new)
						.getFirst();
				
				if(debug) mat.printDebug(entry.getKey());
				
				// Add all located permutations.
				permutations.putAll(mat.permutations());
				
				LOGGER.debug("Added {} tool trim materials from {}.", mat.permutations().size(), entry.getKey());
			} catch(IOException e)
			{
				LOGGER.error("Failed to decode {} tool material.", entry.getKey(), e);
			}
		}
		
		Supplier<int[]> paletteKeys = Suppliers.memoize(() -> loadPaletteEntryFromImage(manager, PALETTE_KEY));
		
		Map<ResourceLocation, Supplier<IntUnaryOperator>> map = new HashMap<>();
		permutations.forEach((name, path) ->
				map.put(name, Suppliers.memoize(() -> createPaletteMapping(paletteKeys.get(), loadPaletteEntryFromImage(manager, path))))
		);
		
		for(var entry0 : textures.entrySet())
		{
			var resourcelocation1 = entry0.getKey();
			ResourceLocation tex = TEXTURE_ID_CONVERTER.fileToId(resourcelocation1);
			Resource res = entry0.getValue();
			
			LazyLoadedImage lazyImg = new LazyLoadedImage(resourcelocation1, res, map.size());
			
			for(var entry1 : map.entrySet())
			{
				var rl = entry1.getKey();
				ResourceLocation rel = tex.withSuffix("/" + rl.getNamespace() + "/" + rl.getPath());
				output.add(rel, new PalettedSpriteSupplier(lazyImg, entry1.getValue(), rel));
			}
		}
	}
	
	@Override
	public @NotNull SpriteSourceType type()
	{
		return TRIM_PERMUTATIONS;
	}
	
	private static IntUnaryOperator createPaletteMapping(int[] p_266839_, int[] p_266776_)
	{
		if(p_266776_.length != p_266839_.length)
		{
			LOGGER.warn("Palette mapping has different sizes: {} and {}", p_266839_.length, p_266776_.length);
			throw new IllegalArgumentException();
		} else
		{
			Int2IntMap int2intmap = new Int2IntOpenHashMap(p_266776_.length);
			
			for(int i = 0; i < p_266839_.length; ++i)
			{
				int j = p_266839_[i];
				if(FastColor.ABGR32.alpha(j) != 0)
				{
					int2intmap.put(FastColor.ABGR32.transparent(j), p_266776_[i]);
				}
			}
			
			return (p_267899_) ->
			{
				int k = FastColor.ABGR32.alpha(p_267899_);
				if(k == 0)
				{
					return p_267899_;
				} else
				{
					int l = FastColor.ABGR32.transparent(p_267899_);
					int i1 = int2intmap.getOrDefault(l, FastColor.ABGR32.opaque(l));
					int j1 = FastColor.ABGR32.alpha(i1);
					return FastColor.ABGR32.color(k * j1 / 255, i1);
				}
			};
		}
	}
	
	public static int[] loadPaletteEntryFromImage(ResourceManager manager, ResourceLocation path)
	{
		Optional<Resource> optional = manager.getResource(TEXTURE_ID_CONVERTER.idToFile(path));
		if(optional.isEmpty())
		{
			LOGGER.error("Failed to load palette image {}", path);
			throw new IllegalArgumentException();
		} else
		{
			try(
					InputStream inputstream = optional.get().open();
					NativeImage nativeimage = NativeImage.read(inputstream);
			)
			{
				return nativeimage.getPixelsRGBA();
			} catch(Exception exception)
			{
				LOGGER.error("Couldn't load texture {}", path, exception);
				throw new IllegalArgumentException();
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	record PalettedSpriteSupplier(LazyLoadedImage baseImage, Supplier<IntUnaryOperator> palette, ResourceLocation permutationLocation)
			implements SpriteSupplier
	{
		@Nullable
		@Override
		public SpriteContents get()
		{
			SpriteContents content;
			
			try
			{
				NativeImage image = this.baseImage.get().mappedCopy(this.palette.get());
				return new SpriteContents(
						this.permutationLocation,
						new FrameSize(image.getWidth(), image.getHeight()),
						image,
						AnimationMetadataSection.EMPTY,
						ForgeTextureMetadata.EMPTY
				);
			} catch(IllegalArgumentException | IOException ioexception)
			{
				LOGGER.error("unable to apply palette to {}", this.permutationLocation, ioexception);
				content = null;
			} finally
			{
				this.baseImage.release();
			}
			
			return content;
		}
		
		@Override
		public void discard()
		{
			this.baseImage.release();
		}
	}
}