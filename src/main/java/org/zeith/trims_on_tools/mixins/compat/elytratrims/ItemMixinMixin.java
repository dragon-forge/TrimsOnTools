package org.zeith.trims_on_tools.mixins.compat.elytratrims;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.trims_on_tools.compat.elytratrims.ElytraTrimsToT;

import java.util.List;

/**
 * Fixing ItemMixin from ElytraTrims, which adds "glowing" tooltip line for elytras (our mod already does it in a better way, let's unify it)
 */
@Mixin(value = Item.class, priority = 100)
public class ItemMixinMixin
{
	@Inject(
			method = "appendHoverText",
			at = @At("HEAD")
	)
	private void TrimsOnTools$fixElytraTrimsTooltip_HEAD(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context, CallbackInfo ci)
	{
		ElytraTrimsToT.isRenderingTooltip = true;
	}
	
	@Inject(
			method = "appendHoverText",
			at = @At("RETURN")
	)
	private void TrimsOnTools$fixElytraTrimsTooltip_END(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context, CallbackInfo ci)
	{
		ElytraTrimsToT.isRenderingTooltip = false;
	}
}