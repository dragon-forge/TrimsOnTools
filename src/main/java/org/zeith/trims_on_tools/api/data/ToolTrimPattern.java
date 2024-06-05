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
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.zeith.trims_on_tools.api.CodecsToT;
import org.zeith.trims_on_tools.api.RegistriesToT;
import org.zeith.trims_on_tools.api.util.Conditionals;
import org.zeith.trims_on_tools.api.util.LazilyInitializedPredicate;

import java.util.List;
import java.util.function.Predicate;

public record ToolTrimPattern(
		ResourceLocation assetId,
		Holder<Item> templateItem,
		Component description,
		List<ICondition> conditions,
		Predicate<ICondition.IContext> enabled
)
{
	public ToolTrimPattern(ResourceLocation assetId, Holder<Item> templateItem, Component description, List<ICondition> conditions)
	{
		this(assetId, templateItem, description, conditions, LazilyInitializedPredicate.of(ctx -> Conditionals.processConditions(ctx, conditions)));
	}
	
	public boolean isEnabled()
	{
		return enabled.test(Conditionals.currentServerContext);
	}
	
	public static final Codec<ToolTrimPattern> DIRECT_CODEC = RecordCodecBuilder.create(inst ->
			inst.group(
					ResourceLocation.CODEC.fieldOf("asset_id").forGetter(ToolTrimPattern::assetId),
					RegistryFixedCodec.create(Registries.ITEM).fieldOf("template_item").forGetter(ToolTrimPattern::templateItem),
					ExtraCodecs.COMPONENT.fieldOf("description").forGetter(ToolTrimPattern::description),
					CodecsToT.CONDITION.listOf().optionalFieldOf("conditions", List.of()).forGetter(ToolTrimPattern::conditions)
			).apply(inst, ToolTrimPattern::new)
	);
	
	public static final Codec<Holder<ToolTrimPattern>> CODEC = RegistryFileCodec.create(RegistriesToT.TOOL_TRIM_PATTERN, DIRECT_CODEC);
	
	public Component copyWithStyle(Holder<TrimMaterial> material)
	{
		return this.description.copy().withStyle(material.value().description().getStyle());
	}
}