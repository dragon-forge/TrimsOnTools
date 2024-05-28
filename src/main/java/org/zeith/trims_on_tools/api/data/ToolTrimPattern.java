package org.zeith.trims_on_tools.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import org.zeith.trims_on_tools.api.RegistriesToT;

public record ToolTrimPattern(ResourceLocation assetId, Holder<Item> templateItem, Component description)
{
	public static final Codec<ToolTrimPattern> DIRECT_CODEC = RecordCodecBuilder.create(inst ->
			inst.group(
					ResourceLocation.CODEC.fieldOf("asset_id").forGetter(ToolTrimPattern::assetId),
					RegistryFixedCodec.create(Registries.ITEM).fieldOf("template_item").forGetter(ToolTrimPattern::templateItem),
					ExtraCodecs.COMPONENT.fieldOf("description").forGetter(ToolTrimPattern::description)
			).apply(inst, ToolTrimPattern::new)
	);
	
	public static final Codec<Holder<ToolTrimPattern>> CODEC = RegistryFileCodec.create(RegistriesToT.TOOL_TRIM_PATTERN, DIRECT_CODEC);
	
	public Component copyWithStyle(Holder<TrimMaterial> p_266827_)
	{
		return this.description.copy().withStyle(p_266827_.value().description().getStyle());
	}
}