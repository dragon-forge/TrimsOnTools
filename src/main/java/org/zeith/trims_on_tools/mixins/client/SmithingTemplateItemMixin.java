package org.zeith.trims_on_tools.mixins.client;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.TrimPatterns;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.hammerlib.api.lighting.ColoredLightManager;
import org.zeith.trims_on_tools.api.data.ToolTrimPattern;
import org.zeith.trims_on_tools.mixins.SmithingTemplateItemAccessor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.zeith.trims_on_tools.TrimsOnToolsMod.MOD_ID;

@Mixin(SmithingTemplateItem.class)
public class SmithingTemplateItemMixin
{
	@Shadow
	@Final
	private static ChatFormatting DESCRIPTION_FORMAT;
	
	@Shadow
	@Final
	private static ResourceLocation EMPTY_SLOT_SWORD;
	@Shadow
	@Final
	private static ResourceLocation EMPTY_SLOT_PICKAXE;
	@Shadow
	@Final
	private static ResourceLocation EMPTY_SLOT_AXE;
	@Shadow
	@Final
	private static ResourceLocation EMPTY_SLOT_HOE;
	@Shadow
	@Final
	private static ResourceLocation EMPTY_SLOT_SHOVEL;
	
	@Unique
	private static final Component TOOL_TRIM_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(MOD_ID, "smithing_template.tool_trim.applies_to"))).withStyle(DESCRIPTION_FORMAT);
	
	@Unique
	private static final Set<ResourceLocation> TOOL_TRIM_VANILLA_TEMPLATES = Stream.of(
			TrimPatterns.SENTRY,
			TrimPatterns.DUNE,
			TrimPatterns.COAST,
			TrimPatterns.WILD,
			TrimPatterns.WARD,
			TrimPatterns.EYE,
			TrimPatterns.VEX,
			TrimPatterns.TIDE,
			TrimPatterns.SNOUT,
			TrimPatterns.RIB,
			TrimPatterns.SPIRE,
			TrimPatterns.WAYFINDER,
			TrimPatterns.SHAPER,
			TrimPatterns.SILENCE,
			TrimPatterns.RAISER,
			TrimPatterns.HOST
	).map(ResourceKey::location).collect(Collectors.toSet());
	
	@Unique
	private static final TagKey<Item> TOOLTRIMS$TOOL_TEMPLATE_MODIFIERS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(MOD_ID, "tool_template_modifiers"));
	
	@Inject(
			method = "appendHoverText",
			at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 4)
	)
	private void ToolTrims_appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flags, CallbackInfo ci)
	{
		if(ctx == null)
		{
			var pl = ColoredLightManager.getClientPlayer();
			if(pl != null) ctx = Item.TooltipContext.of(pl.level());
		}
		if((ctx != null && ToolTrimPattern.getFromTemplate(ctx.registries(), stack).isPresent()) || stack.is(TOOLTRIMS$TOOL_TEMPLATE_MODIFIERS))
			tooltip.add(CommonComponents.space().append(TOOL_TRIM_APPLIES_TO));
	}
	
	@Inject(
			method = "createArmorTrimTemplate(Lnet/minecraft/resources/ResourceLocation;[Lnet/minecraft/world/flag/FeatureFlag;)Lnet/minecraft/world/item/SmithingTemplateItem;",
			at = @At("RETURN")
	)
	private static void ToolTrims_createArmorTrimTemplate(ResourceLocation location, FeatureFlag[] featureFlags, CallbackInfoReturnable<SmithingTemplateItem> cir)
	{
		if(!TOOL_TRIM_VANILLA_TEMPLATES.contains(location)) return;
		
		SmithingTemplateItem it = cir.getReturnValue();
		SmithingTemplateItemAccessor ac = (SmithingTemplateItemAccessor) it;
		
		List<ResourceLocation> textures = new ArrayList<>(it.getBaseSlotEmptyIcons());
		
		List<ResourceLocation> add = List.of(EMPTY_SLOT_SWORD, EMPTY_SLOT_PICKAXE, EMPTY_SLOT_AXE, EMPTY_SLOT_HOE, EMPTY_SLOT_SHOVEL);
		for(int i = 0; i < add.size(); i++) textures.add(i + 1, add.get(i));
		
		ac.setBaseSlotEmptyIcons(textures.stream().distinct().toList());
	}
}