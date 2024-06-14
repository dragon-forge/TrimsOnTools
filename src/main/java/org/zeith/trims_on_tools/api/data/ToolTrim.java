package org.zeith.trims_on_tools.api.data;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipProvider;
import org.slf4j.Logger;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.trims_on_tools.TrimsOnToolsMod;
import org.zeith.trims_on_tools.api.util.TrimKey;
import org.zeith.trims_on_tools.init.DataComponentsToT;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ToolTrim implements TooltipProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	private static final Component UPGRADE_TITLE = Component.translatable(Util.makeDescriptionId("item", Resources.location("smithing_template.upgrade"))).withStyle(ChatFormatting.GRAY);
	
	public static final Codec<ToolTrim> CODEC = RecordCodecBuilder.create((inst) ->
			inst.group(
					ToolTrimMaterial.CODEC.fieldOf("material").forGetter(ToolTrim::material),
					ToolTrimPattern.CODEC.fieldOf("pattern").forGetter(ToolTrim::pattern)
			).apply(inst, ToolTrim::new)
	);
	
	public static final StreamCodec<RegistryFriendlyByteBuf, ToolTrim> STREAM_CODEC = StreamCodec.composite(
			ToolTrimMaterial.STREAM_CODEC, ToolTrim::material,
			ToolTrimPattern.STREAM_CODEC, ToolTrim::pattern,
//			ByteBufCodecs.BOOL, p_330107_ -> p_330107_.showInTooltip,
			ToolTrim::new
	);
	
	private final Holder<ToolTrimMaterial> material;
	private final Holder<ToolTrimPattern> pattern;
	@Getter
	private final TrimKey key;
	private final BiFunction<ItemStack, ResourceLocation, ResourceLocation> toolTexture;
	
	public ToolTrim(Holder<ToolTrimMaterial> material, Holder<ToolTrimPattern> pattern)
	{
		this.material = material;
		this.pattern = pattern;
		
		this.key = new TrimKey(
				pattern.unwrapKey().map(ResourceKey::location).orElseThrow(),
				material.unwrapKey().map(ResourceKey::location).orElseThrow()
		);
		
		this.toolTexture = Util.memoize((mat, tool) ->
		{
			if(mat == null || tool == null) return null;
			ResourceLocation patId = this.pattern.value().assetId();
			ResourceLocation s = this.getColorPaletteAsset(mat);
			return patId.withPath(path -> "trims_on_tools/trims/" + tool.getNamespace() + "/" + tool.getPath() + "/" + path + "/" + s.getNamespace() + "/" + s.getPath());
		});
	}
	
	private ResourceLocation getColorPaletteAsset(ItemStack mat)
	{
		var overrides = this.material.value().overrideAssets();
		
		ResourceLocation override = overrides
				.entrySet()
				.stream()
				.filter(e -> mat.is(e.getKey()))
				.findFirst()
				.map(Map.Entry::getValue)
				.orElse(null);
		
		return override != null ? override : this.material.value().asset();
	}
	
	public boolean hasPatternAndMaterial(Holder<ToolTrimPattern> pattern, Holder<ToolTrimMaterial> material)
	{
		return pattern == this.pattern && material == this.material;
	}
	
	public Holder<ToolTrimPattern> pattern()
	{
		return this.pattern;
	}
	
	public Holder<ToolTrimMaterial> material()
	{
		return this.material;
	}
	
	public ResourceLocation texture(RegistryAccess access, ItemStack material, ToolType tool)
	{
		if(tool == null) return null;
		return this.toolTexture.apply(material, tool.getKey(access));
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(!(other instanceof ToolTrim trim)) return false;
		return trim.pattern == this.pattern && trim.material == this.material;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(material, pattern);
	}
	
	public static final TagKey<Item> TRIMMABLE_TOOLS = ItemTags.create(TrimsOnToolsMod.id("trimmable_tools"));
	
	public static boolean setTrim(HolderLookup.Provider access, ItemStack stack, ToolTrim trim)
	{
		if(stack.is(TRIMMABLE_TOOLS))
		{
			if(trim == null)
			{
				stack.remove(DataComponentsToT.TRIM);
				return true;
			}
			
			stack.set(DataComponentsToT.TRIM, trim);
			return true;
		} else
		{
			return false;
		}
	}
	
	public static Optional<ToolTrim> getTrim(ItemStack stack)
	{
		if(stack.is(TRIMMABLE_TOOLS))
		{
			return Optional.ofNullable(stack.get(DataComponentsToT.TRIM));
		} else
		{
			return Optional.empty();
		}
	}
	
	@Override
	public void addToTooltip(Item.TooltipContext ctx, Consumer<Component> hoverText, TooltipFlag flags)
	{
		hoverText.accept(UPGRADE_TITLE);
		hoverText.accept(CommonComponents.space().append(pattern().value().description().copy().withStyle(material().value().description().getStyle())));
		hoverText.accept(CommonComponents.space().append(material().value().description()));
	}
}