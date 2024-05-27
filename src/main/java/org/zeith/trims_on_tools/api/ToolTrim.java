package org.zeith.trims_on_tools.api;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimPattern;
import org.slf4j.Logger;
import org.zeith.trims_on_tools.TrimsOnToolsMod;

import java.util.*;
import java.util.function.BiFunction;

import static org.zeith.trims_on_tools.ConstantsToT.TAG_TOOL_TRIM_ID;

public class ToolTrim
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	private static final Component UPGRADE_TITLE = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.upgrade"))).withStyle(ChatFormatting.GRAY);
	
	public static final Codec<Holder<TrimPattern>> TOOL_TRIM_PATTERN_CODEC = RegistryFileCodec.create(RegistriesToT.TOOL_TRIM_PATTERN, TrimPattern.DIRECT_CODEC);
	public static final Codec<ToolTrim> CODEC = RecordCodecBuilder.create((inst) ->
			inst.group(
					ToolTrimMaterial.CODEC.fieldOf("material").forGetter(ToolTrim::material),
					TOOL_TRIM_PATTERN_CODEC.fieldOf("pattern").forGetter(ToolTrim::pattern)
			).apply(inst, ToolTrim::new)
	);
	
	private final Holder<ToolTrimMaterial> material;
	private final Holder<TrimPattern> pattern;
	private final TrimKey key;
	private final BiFunction<ItemStack, ToolType, ResourceLocation> toolTexture;
	
	public ToolTrim(Holder<ToolTrimMaterial> material, Holder<TrimPattern> pattern)
	{
		this.material = material;
		this.pattern = pattern;
		
		this.key = new TrimKey(
				pattern.unwrapKey().map(ResourceKey::location).orElseThrow(),
				material.unwrapKey().map(ResourceKey::location).orElseThrow()
		);
		
		this.toolTexture = Util.memoize((mat, tool) ->
		{
			ResourceLocation resourcelocation = this.pattern.value().assetId();
			String s = this.getColorPaletteSuffix(mat);
			return resourcelocation.withPath(path -> "trims/models/tools/" + tool.name() + "/" + path + "_" + s);
		});
	}
	
	private String getColorPaletteSuffix(ItemStack mat)
	{
		var overrides = this.material.value().overrideMaterials();
		String override = overrides
				.entrySet()
				.stream()
				.filter(e -> mat.is(e.getKey()))
				.findFirst()
				.map(Map.Entry::getValue)
				.orElse(null);
		
		return override != null ? override : this.material.value().assetName();
	}
	
	public boolean hasPatternAndMaterial(Holder<TrimPattern> pattern, Holder<ToolTrimMaterial> material)
	{
		return pattern == this.pattern && material == this.material;
	}
	
	public Holder<TrimPattern> pattern()
	{
		return this.pattern;
	}
	
	public Holder<ToolTrimMaterial> material()
	{
		return this.material;
	}
	
	public ResourceLocation texture(ItemStack material, ToolType tool)
	{
		if(tool == null) return null;
		return this.toolTexture.apply(material, tool);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(!(other instanceof ToolTrim trim)) return false;
		
		return trim.pattern == this.pattern && trim.material == this.material;
	}
	
	public TrimKey getKey()
	{
		return key;
	}
	
	public static final TagKey<Item> TRIMMABLE_TOOLS = ItemTags.create(TrimsOnToolsMod.id("trimmable_tools"));
	
	public static boolean setTrim(RegistryAccess access, ItemStack stack, ToolTrim trim)
	{
		if(stack.is(TRIMMABLE_TOOLS))
		{
			if(trim == null)
			{
				stack.removeTagKey(TAG_TOOL_TRIM_ID);
				return true;
			}
			
			stack.getOrCreateTag().put(TAG_TOOL_TRIM_ID, CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, access), trim).result().orElseThrow());
			return true;
		} else
		{
			return false;
		}
	}
	
	public static Optional<ToolTrim> getTrim(RegistryAccess access, ItemStack stack)
	{
		if(stack.is(TRIMMABLE_TOOLS) && stack.getTag() != null && stack.getTag().contains(TAG_TOOL_TRIM_ID))
		{
			CompoundTag compoundtag = stack.getTagElement(TAG_TOOL_TRIM_ID);
			ToolTrim armortrim = CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, access), compoundtag).resultOrPartial(LOGGER::error).orElse(null);
			return Optional.ofNullable(armortrim);
		} else
		{
			return Optional.empty();
		}
	}
	
	public static void appendUpgradeHoverText(ItemStack stack, RegistryAccess access, List<Component> hoverText)
	{
		Optional<ToolTrim> opt = getTrim(access, stack);
		if(!opt.isPresent()) return;
		
		ToolTrim trim = opt.get();
		hoverText.add(UPGRADE_TITLE);
		hoverText.add(CommonComponents.space().append(trim.pattern().value().description().copy().withStyle(trim.material().value().description().getStyle())));
		hoverText.add(CommonComponents.space().append(trim.material().value().description()));
	}
}