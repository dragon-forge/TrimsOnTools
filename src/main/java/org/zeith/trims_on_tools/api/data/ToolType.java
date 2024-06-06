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
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.zeith.trims_on_tools.api.CodecsToT;
import org.zeith.trims_on_tools.api.RegistriesToT;
import org.zeith.trims_on_tools.api.util.Conditionals;
import org.zeith.trims_on_tools.api.util.LazilyInitializedPredicate;

import java.util.*;
import java.util.function.Predicate;

public final class ToolType
{
	public static final Codec<ToolType> DIRECT_CODEC = RecordCodecBuilder.create((inst) ->
			inst.group(
					TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(ToolType::tag),
					CodecsToT.CONDITION.listOf().optionalFieldOf("conditions", List.of()).forGetter(ToolType::conditions)
			).apply(inst, ToolType::new)
	);
	
	public static final Codec<Holder<ToolType>> CODEC = RegistryFileCodec.create(RegistriesToT.TOOL_TYPES, DIRECT_CODEC);
	private final TagKey<Item> tag;
	private final List<ICondition> conditions;
	private final Predicate<ICondition.IContext> enabled;
	
	public ToolType(TagKey<Item> tag, List<ICondition> conditions)
	{
		this.tag = tag;
		this.conditions = conditions;
		this.enabled = LazilyInitializedPredicate.of(ctx -> Conditionals.processConditions(ctx, conditions));
	}
	
	public static Optional<Holder.Reference<ToolType>> getFromTool(RegistryAccess access, ItemStack tool)
	{
		return access.registryOrThrow(RegistriesToT.TOOL_TYPES)
				.holders()
				.filter((holder) ->
				{
					var t = holder.value();
					return tool.is(t.tag()) && t.isEnabled();
				})
				.findFirst();
	}
	
	public ResourceLocation getKey(RegistryAccess access)
	{
		return access.registryOrThrow(RegistriesToT.TOOL_TYPES)
				.getKey(this);
	}
	
	public boolean isEnabled()
	{
		return enabled.test(Conditionals.currentServerContext);
	}
	
	public TagKey<Item> tag()
	{
		return tag;
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
		var that = (ToolType) obj;
		return Objects.equals(this.tag, that.tag) &&
			   Objects.equals(this.conditions, that.conditions);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(tag, conditions);
	}
	
	@Override
	public String toString()
	{
		return "ToolType[" +
			   "tag=" + tag + ", " +
			   "conditions=" + conditions + ']';
	}
}