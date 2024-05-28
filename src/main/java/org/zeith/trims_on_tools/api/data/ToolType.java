package org.zeith.trims_on_tools.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.zeith.trims_on_tools.api.RegistriesToT;

import java.util.Optional;

public record ToolType(TagKey<Item> tag)
{
	public static final Codec<ToolType> DIRECT_CODEC = RecordCodecBuilder.create((inst) ->
			inst.group(
					TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(ToolType::tag)
			).apply(inst, ToolType::new)
	);
	
	public static final Codec<Holder<ToolType>> CODEC = RegistryFileCodec.create(RegistriesToT.TOOL_TYPES, DIRECT_CODEC);
	
	public static Optional<Holder.Reference<ToolType>> getFromTool(RegistryAccess access, ItemStack tool)
	{
		return access.registryOrThrow(RegistriesToT.TOOL_TYPES)
				.holders()
				.filter(holder -> tool.is(holder.value().tag()))
				.findFirst();
	}
	
	public ResourceLocation getKey(RegistryAccess access)
	{
		return access.registryOrThrow(RegistriesToT.TOOL_TYPES)
				.getKey(this);
	}
}