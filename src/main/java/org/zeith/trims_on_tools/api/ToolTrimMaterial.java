package org.zeith.trims_on_tools.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.zeith.trims_on_tools.TrimsOnToolsMod;

import java.util.Map;
import java.util.Optional;

public record ToolTrimMaterial(String assetName, Holder<Item> ingredient, Map<TagKey<Item>, String> overrideMaterials, Component description)
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
					Codec.STRING.fieldOf("asset_name").forGetter(ToolTrimMaterial::assetName),
					RegistryFixedCodec.create(Registries.ITEM).fieldOf("ingredient").forGetter(ToolTrimMaterial::ingredient),
					Codec.unboundedMap(TagKey.codec(Registries.ITEM), Codec.STRING).optionalFieldOf("override_materials", Map.of()).forGetter(ToolTrimMaterial::overrideMaterials),
					ExtraCodecs.COMPONENT.fieldOf("description").forGetter(ToolTrimMaterial::description)
			).apply(inst, ToolTrimMaterial::new)
	);
	
	public static final Codec<Holder<ToolTrimMaterial>> CODEC = RegistryFileCodec.create(RegistriesToT.TOOL_TRIM_MATERIAL, DIRECT_CODEC);
	
	public static ToolTrimMaterial create(String assetName, Item ingredient, Component description, Map<TagKey<Item>, String> p_267977_)
	{
		return new ToolTrimMaterial(assetName, BuiltInRegistries.ITEM.wrapAsHolder(ingredient), p_267977_, description);
	}
	
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