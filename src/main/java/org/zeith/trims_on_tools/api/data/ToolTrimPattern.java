package org.zeith.trims_on_tools.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.zeith.hammerlib.api.forge.StreamCodecs;
import org.zeith.trims_on_tools.api.RegistriesToT;
import org.zeith.trims_on_tools.api.util.Conditionals;
import org.zeith.trims_on_tools.api.util.LazilyInitializedPredicate;

import java.util.*;
import java.util.function.Predicate;

public final class ToolTrimPattern
{
	public static final Codec<ToolTrimPattern> DIRECT_CODEC = RecordCodecBuilder.create(inst ->
			inst.group(
					ResourceLocation.CODEC.fieldOf("asset_id").forGetter(ToolTrimPattern::assetId),
					RegistryFixedCodec.create(Registries.ITEM).fieldOf("template_item").forGetter(ToolTrimPattern::templateItem),
					ComponentSerialization.CODEC.fieldOf("description").forGetter(ToolTrimPattern::description),
					ICondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(ToolTrimPattern::conditions)
			).apply(inst, ToolTrimPattern::new)
	);
	
	public static final StreamCodec<RegistryFriendlyByteBuf, ToolTrimPattern> DIRECT_STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, ToolTrimPattern::assetId,
			ByteBufCodecs.holderRegistry(Registries.ITEM), ToolTrimPattern::templateItem,
			ComponentSerialization.STREAM_CODEC, ToolTrimPattern::description,
			StreamCodecs.createRegistryAwareStreamCodec(ICondition.LIST_CODEC), ToolTrimPattern::conditions,
			ToolTrimPattern::new
	);
	
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ToolTrimPattern>> STREAM_CODEC = ByteBufCodecs.holder(
			RegistriesToT.TOOL_TRIM_PATTERN, DIRECT_STREAM_CODEC
	);
	
	public static final Codec<Holder<ToolTrimPattern>> CODEC = RegistryFileCodec.create(RegistriesToT.TOOL_TRIM_PATTERN, DIRECT_CODEC);
	private final ResourceLocation assetId;
	private final Holder<Item> templateItem;
	private final Component description;
	private final List<ICondition> conditions;
	private final Predicate<ICondition.IContext> enabled;
	
	public ToolTrimPattern(
			ResourceLocation assetId,
			Holder<Item> templateItem,
			Component description,
			List<ICondition> conditions
	)
	{
		this.assetId = assetId;
		this.templateItem = templateItem;
		this.description = description;
		this.conditions = conditions;
		this.enabled = LazilyInitializedPredicate.of(ctx -> Conditionals.processConditions(ctx, conditions));
	}
	
	public boolean isEnabled()
	{
		return enabled.test(Conditionals.currentServerContext);
	}
	
	public Component copyWithStyle(Holder<TrimMaterial> material)
	{
		return this.description.copy().withStyle(material.value().description().getStyle());
	}
	
	public static Optional<Holder.Reference<ToolTrimPattern>> getFromTemplate(HolderLookup.Provider access, ItemStack template)
	{
		return access.lookup(RegistriesToT.TOOL_TRIM_PATTERN)
				.stream()
				.flatMap(HolderLookup::listElements)
				.filter((holder) ->
				{
					var pat = holder.value();
					return template.is(pat.templateItem()) && pat.isEnabled();
				})
				.findFirst();
	}
	
	public ResourceLocation assetId()
	{
		return assetId;
	}
	
	public Holder<Item> templateItem()
	{
		return templateItem;
	}
	
	public Component description()
	{
		return description;
	}
	
	public List<ICondition> conditions()
	{
		return conditions;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		var that = (ToolTrimPattern) obj;
		return Objects.equals(this.assetId, that.assetId) &&
			   Objects.equals(this.templateItem, that.templateItem) &&
			   Objects.equals(this.description, that.description) &&
			   Objects.equals(this.conditions, that.conditions);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(assetId, templateItem, description, conditions);
	}
	
	@Override
	public String toString()
	{
		return "ToolTrimPattern[" +
			   "assetId=" + assetId + ", " +
			   "templateItem=" + templateItem + ", " +
			   "description=" + description + ", " +
			   "conditions=" + conditions + ']';
	}
	
}