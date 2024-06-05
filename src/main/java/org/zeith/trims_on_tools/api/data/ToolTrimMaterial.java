package org.zeith.trims_on_tools.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.zeith.trims_on_tools.TrimsOnToolsMod;
import org.zeith.trims_on_tools.api.CodecsToT;
import org.zeith.trims_on_tools.api.RegistriesToT;
import org.zeith.trims_on_tools.api.util.Conditionals;
import org.zeith.trims_on_tools.api.util.LazilyInitializedPredicate;

import java.util.*;
import java.util.function.Predicate;

public record ToolTrimMaterial(
		ResourceLocation asset,
		Optional<Holder<Item>> ingredient,
		Map<TagKey<Item>, ResourceLocation> overrideAssets,
		Component description,
		List<ICondition> conditions,
		Predicate<ICondition.IContext> enabled
)
{
	public ToolTrimMaterial(ResourceLocation asset, Optional<Holder<Item>> ingredient, Map<TagKey<Item>, ResourceLocation> overrideAssets, Component description, List<ICondition> conditions)
	{
		this(asset, ingredient, overrideAssets, description, conditions, LazilyInitializedPredicate.of(ctx -> Conditionals.processConditions(ctx, conditions)));
	}
	
	public boolean isEnabled()
	{
		return enabled.test(Conditionals.currentServerContext);
	}
	
	public static final ResourceKey<ToolTrimMaterial> QUARTZ = registryKey("quartz");
	public static final ResourceKey<ToolTrimMaterial> IRON = registryKey("iron");
	public static final ResourceKey<ToolTrimMaterial> NETHERITE = registryKey("netherite");
	public static final ResourceKey<ToolTrimMaterial> REDSTONE = registryKey("redstone");
	public static final ResourceKey<ToolTrimMaterial> COPPER = registryKey("copper");
	public static final ResourceKey<ToolTrimMaterial> GOLD = registryKey("gold");
	public static final ResourceKey<ToolTrimMaterial> EMERALD = registryKey("emerald");
	public static final ResourceKey<ToolTrimMaterial> DIAMOND = registryKey("diamond");
	public static final ResourceKey<ToolTrimMaterial> LAPIS = registryKey("lapis");
	public static final ResourceKey<ToolTrimMaterial> AMETHYST = registryKey("amethyst");
	
	public static final Codec<ToolTrimMaterial> DIRECT_CODEC = RecordCodecBuilder.create((inst) ->
			inst.group(
					ResourceLocation.CODEC.fieldOf("asset").forGetter(ToolTrimMaterial::asset),
					RegistryFixedCodec.create(Registries.ITEM).optionalFieldOf("ingredient").forGetter(ToolTrimMaterial::ingredient),
					Codec.unboundedMap(TagKey.codec(Registries.ITEM), ResourceLocation.CODEC).optionalFieldOf("override_assets", Map.of()).forGetter(ToolTrimMaterial::overrideAssets),
					ExtraCodecs.COMPONENT.fieldOf("description").forGetter(ToolTrimMaterial::description),
					CodecsToT.CONDITION.listOf().optionalFieldOf("conditions", List.of()).forGetter(ToolTrimMaterial::conditions)
			).apply(inst, ToolTrimMaterial::new)
	);
	
	public static final Codec<Holder<ToolTrimMaterial>> CODEC = RegistryFileCodec.create(RegistriesToT.TOOL_TRIM_MATERIAL, DIRECT_CODEC);
	
	private static ResourceKey<ToolTrimMaterial> registryKey(String id)
	{
		return ResourceKey.create(RegistriesToT.TOOL_TRIM_MATERIAL, TrimsOnToolsMod.id(id));
	}
	
	public static Optional<Holder.Reference<ToolTrimMaterial>> getFromIngredient(RegistryAccess access, ItemStack ingredient)
	{
		return access.registryOrThrow(RegistriesToT.TOOL_TRIM_MATERIAL)
				.holders()
				.filter(holder ->
				{
					var mat = holder.value();
					var ing = mat.ingredient();
					return ing.map(ingredient::is).orElse(false) && mat.isEnabled();
				})
				.findFirst();
	}
}