package org.zeith.trims_on_tools.init;

import net.minecraft.world.item.SmithingTemplateItem;
import org.zeith.hammerlib.annotations.*;
import org.zeith.trims_on_tools.TrimsOnToolsMod;

@SimplyRegister(
		creativeTabs = @Ref(value = TrimsOnToolsMod.class, field = "TAB")
)
public interface ItemsToT
{
	@RegistryName("glow_tool_trim_smithing_template")
	SmithingTemplateItem GLOW_TOOL_TRIM_SMITHING_TEMPLATE = SmithingTemplateItem.createArmorTrimTemplate(TrimsOnToolsMod.id("glow"));
}