package org.zeith.trims_on_tools.api;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.zeith.trims_on_tools.TrimsOnToolsMod;
import org.zeith.trims_on_tools.api.data.*;
import org.zeith.trims_on_tools.mixins.SmithingTemplateItemAccessor;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistriesToT
{
	private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
	private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
	private static final Component TOOL_TRIM_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", TrimsOnToolsMod.id("smithing_template.tool_trim.applies_to"))).withStyle(DESCRIPTION_FORMAT);
	private static final Component TOOL_TRIM_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", TrimsOnToolsMod.id("smithing_template.tool_trim.ingredients"))).withStyle(DESCRIPTION_FORMAT);
	private static final Component TOOL_TRIM_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", TrimsOnToolsMod.id("smithing_template.tool_trim.base_slot_description")));
	private static final Component TOOL_TRIM_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", TrimsOnToolsMod.id("smithing_template.tool_trim.additions_slot_description")));
	
	private static final ResourceLocation EMPTY_SLOT_HOE = new ResourceLocation("item/empty_slot_hoe");
	private static final ResourceLocation EMPTY_SLOT_AXE = new ResourceLocation("item/empty_slot_axe");
	private static final ResourceLocation EMPTY_SLOT_SWORD = new ResourceLocation("item/empty_slot_sword");
	private static final ResourceLocation EMPTY_SLOT_SHOVEL = new ResourceLocation("item/empty_slot_shovel");
	private static final ResourceLocation EMPTY_SLOT_PICKAXE = new ResourceLocation("item/empty_slot_pickaxe");
	
	public static final ResourceKey<Registry<ToolTrimPattern>> TOOL_TRIM_PATTERN = createRegistryKey("tool_trim_pattern");
	public static final ResourceKey<Registry<ToolTrimMaterial>> TOOL_TRIM_MATERIAL = createRegistryKey("tool_trim_material");
	public static final ResourceKey<Registry<ToolType>> TOOL_TYPES = createRegistryKey("tool_types");
	
	@SubscribeEvent
	public static void newRegistries(DataPackRegistryEvent.NewRegistry e)
	{
		e.dataPackRegistry(TOOL_TRIM_PATTERN, ToolTrimPattern.DIRECT_CODEC, ToolTrimPattern.DIRECT_CODEC);
		e.dataPackRegistry(TOOL_TRIM_MATERIAL, ToolTrimMaterial.DIRECT_CODEC, ToolTrimMaterial.DIRECT_CODEC);
		e.dataPackRegistry(TOOL_TYPES, ToolType.DIRECT_CODEC, ToolType.DIRECT_CODEC);
	}
	
	private static <T> ResourceKey<Registry<T>> createRegistryKey(String id)
	{
		return ResourceKey.createRegistryKey(TrimsOnToolsMod.id(id));
	}
	
	public static SmithingTemplateItem createToolTrimTemplate(ResourceLocation patternKey)
	{
		return new SmithingTemplateItem(TOOL_TRIM_APPLIES_TO, TOOL_TRIM_INGREDIENTS,
				Component.translatable(Util.makeDescriptionId("trim_pattern", patternKey)).withStyle(TITLE_FORMAT),
				TOOL_TRIM_BASE_SLOT_DESCRIPTION, TOOL_TRIM_ADDITIONS_SLOT_DESCRIPTION,
				createTrimmableToolIconList(),
				SmithingTemplateItemAccessor.callCreateTrimmableMaterialIconList()
		);
	}
	
	private static List<ResourceLocation> createTrimmableToolIconList()
	{
		return List.of(EMPTY_SLOT_SWORD, EMPTY_SLOT_PICKAXE, EMPTY_SLOT_AXE, EMPTY_SLOT_HOE, EMPTY_SLOT_SHOVEL);
	}
	
	public static Optional<Holder.Reference<ToolTrimPattern>> getFromTemplate(RegistryAccess access, ItemStack template)
	{
		return access.registryOrThrow(TOOL_TRIM_PATTERN)
				.holders()
				.filter((holder) -> template.is(holder.value().templateItem()))
				.findFirst();
	}
}