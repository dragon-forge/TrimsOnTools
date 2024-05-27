package org.zeith.trims_on_tools.mixins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(SmithingTemplateItem.class)
public interface SmithingTemplateItemAccessor
{
	@Invoker
	static List<ResourceLocation> callCreateTrimmableMaterialIconList() {throw new UnsupportedOperationException();}
	
	@Mutable
	@Accessor
	void setBaseSlotEmptyIcons(List<ResourceLocation> baseSlotEmptyIcons);
}
