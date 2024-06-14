package org.zeith.trims_on_tools.mixins;

import net.minecraft.core.component.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.trims_on_tools.init.DataComponentsToT;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
		implements DataComponentHolder
{
	@Inject(
			method = "addToTooltip",
			at = @At("HEAD")
	)
	private <T extends TooltipProvider> void TrimsOnTools_addToTooltip(DataComponentType<T> com, Item.TooltipContext ctx, Consumer<Component> tooltip, TooltipFlag p_331177_, CallbackInfo ci)
	{
		if(com == DataComponents.TRIM)
		{
			TooltipProvider t = get(DataComponentsToT.TRIM);
			if(t != null) t.addToTooltip(ctx, tooltip, p_331177_);
			
			t = get(DataComponentsToT.GLOW);
			if(t != null) t.addToTooltip(ctx, tooltip, p_331177_);
		}
	}
}