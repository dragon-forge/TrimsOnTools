package org.zeith.trims_on_tools.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ToolType(String name, TagKey<Item> tag)
{
	public static final Codec<ToolType> DIRECT_CODEC = RecordCodecBuilder.create((inst) ->
			inst.group(
					Codec.STRING.fieldOf("name").forGetter(ToolType::name),
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
}