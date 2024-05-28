package org.zeith.trims_on_tools.client.resource;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.Map;

public record PermutationMaterialFile(Map<ResourceLocation, ResourceLocation> permutations)
{
	static final Logger LOGGER = LogUtils.getLogger();
	
	public static final Codec<PermutationMaterialFile> CODEC = RecordCodecBuilder.create((inst) ->
			inst.group(
					Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).fieldOf("permutations").forGetter(PermutationMaterialFile::permutations)
			).apply(inst, PermutationMaterialFile::new)
	);
	
	public void printDebug(ResourceLocation key)
	{
		LOGGER.info("Permutations in {}:", key);
		for(Map.Entry<ResourceLocation, ResourceLocation> e : permutations.entrySet())
		{
			LOGGER.info("- Material '{}' points to texture '{}'", e.getKey(), SpriteSource.TEXTURE_ID_CONVERTER.idToFile(e.getValue()));
		}
	}
}