package org.zeith.trims_on_tools.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.zeith.hammerlib.api.forge.StreamCodecs;
import org.zeith.trims_on_tools.TrimsOnToolsMod;
import org.zeith.trims_on_tools.api.RegistriesToT;
import org.zeith.trims_on_tools.api.util.Conditionals;
import org.zeith.trims_on_tools.api.util.LazilyInitializedPredicate;

import java.util.*;
import java.util.function.Predicate;

public final class ToolTrimMaterial
{
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
					RegistryFixedCodec.create(Registries.ITEM).lenientOptionalFieldOf("ingredient").forGetter(ToolTrimMaterial::ingredient),
					Codec.unboundedMap(TagKey.codec(Registries.ITEM), ResourceLocation.CODEC).optionalFieldOf("override_assets", Map.of()).forGetter(ToolTrimMaterial::overrideAssets),
					ComponentSerialization.CODEC.fieldOf("description").forGetter(ToolTrimMaterial::description),
					ICondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(ToolTrimMaterial::conditions)
			).apply(inst, ToolTrimMaterial::new)
	);
	public static final Codec<Holder<ToolTrimMaterial>> CODEC = RegistryFileCodec.create(RegistriesToT.TOOL_TRIM_MATERIAL, DIRECT_CODEC);
	
	public static final StreamCodec<RegistryFriendlyByteBuf, ToolTrimMaterial> DIRECT_STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, ToolTrimMaterial::asset,
			ByteBufCodecs.holderRegistry(Registries.ITEM).map(Optional::ofNullable, o -> o.orElseGet(() -> Holder.direct(Items.AIR))), ToolTrimMaterial::ingredient,
			ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC.map(ItemTags::create, TagKey::location), ResourceLocation.STREAM_CODEC), ToolTrimMaterial::overrideAssets,
			ComponentSerialization.STREAM_CODEC, ToolTrimMaterial::description,
			StreamCodecs.createRegistryAwareStreamCodec(ICondition.LIST_CODEC), ToolTrimMaterial::conditions,
			ToolTrimMaterial::new
	);
	
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ToolTrimMaterial>> STREAM_CODEC = ByteBufCodecs.holder(
			RegistriesToT.TOOL_TRIM_MATERIAL, DIRECT_STREAM_CODEC
	);
	
	private final ResourceLocation asset;
	private final Optional<Holder<Item>> ingredient;
	private final Map<TagKey<Item>, ResourceLocation> overrideAssets;
	private final Component description;
	private final List<ICondition> conditions;
	private final Predicate<ICondition.IContext> enabled;
	
	public ToolTrimMaterial(
			ResourceLocation asset,
			Optional<Holder<Item>> ingredient,
			Map<TagKey<Item>, ResourceLocation> overrideAssets,
			Component description,
			List<ICondition> conditions
	)
	{
		this.asset = asset;
		this.ingredient = ingredient;
		this.overrideAssets = overrideAssets;
		this.description = description;
		this.conditions = conditions;
		this.enabled = LazilyInitializedPredicate.of(ctx -> Conditionals.processConditions(ctx, conditions));
	}
	
	public boolean isEnabled()
	{
		return enabled.test(Conditionals.currentServerContext);
	}
	
	public ResourceLocation asset()
	{
		return asset;
	}
	
	public Optional<Holder<Item>> ingredient()
	{
		return ingredient;
	}
	
	public Map<TagKey<Item>, ResourceLocation> overrideAssets()
	{
		return overrideAssets;
	}
	
	public Component description()
	{
		return description;
	}
	
	public List<ICondition> conditions()
	{
		return conditions;
	}
	
	public static Optional<Holder.Reference<ToolTrimMaterial>> getFromIngredient(HolderLookup.Provider access, ItemStack ingredient)
	{
		return access.lookupOrThrow(RegistriesToT.TOOL_TRIM_MATERIAL)
				.listElements()
				.filter((holder) ->
				{
					var mat = holder.value();
					var ing = mat.ingredient();
					return ing.map(ingredient::is).orElse(false) && mat.isEnabled();
				})
				.findFirst();
	}
	
	private static ResourceKey<ToolTrimMaterial> registryKey(String id)
	{
		return ResourceKey.create(RegistriesToT.TOOL_TRIM_MATERIAL, TrimsOnToolsMod.id(id));
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		var that = (ToolTrimMaterial) obj;
		return Objects.equals(this.asset, that.asset) &&
			   Objects.equals(this.ingredient, that.ingredient) &&
			   Objects.equals(this.overrideAssets, that.overrideAssets) &&
			   Objects.equals(this.description, that.description) &&
			   Objects.equals(this.conditions, that.conditions);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(asset, ingredient, overrideAssets, description, conditions);
	}
	
	@Override
	public String toString()
	{
		return "ToolTrimMaterial[" +
			   "asset=" + asset + ", " +
			   "ingredient=" + ingredient + ", " +
			   "overrideAssets=" + overrideAssets + ", " +
			   "description=" + description + ", " +
			   "conditions=" + conditions + ']';
	}
}