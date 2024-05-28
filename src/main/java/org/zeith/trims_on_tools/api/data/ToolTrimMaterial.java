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
import org.zeith.trims_on_tools.TrimsOnToolsMod;
import org.zeith.trims_on_tools.api.RegistriesToT;

import java.util.Map;
import java.util.Optional;

public record ToolTrimMaterial(ResourceLocation asset, Holder<Item> ingredient, Map<TagKey<Item>, ResourceLocation> overrideAssets, Component description)
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
					RegistryFixedCodec.create(Registries.ITEM).fieldOf("ingredient").forGetter(ToolTrimMaterial::ingredient),
					Codec.unboundedMap(TagKey.codec(Registries.ITEM), ResourceLocation.CODEC).optionalFieldOf("override_assets", Map.of()).forGetter(ToolTrimMaterial::overrideAssets),
					ExtraCodecs.COMPONENT.fieldOf("description").forGetter(ToolTrimMaterial::description)
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
				.filter(holder -> ingredient.is(holder.value().ingredient()))
				.findFirst();
	}
}