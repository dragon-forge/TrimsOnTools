package org.zeith.trims_on_tools.mixins.compat.elytratrims;

import dev.kikugie.elytratrims.common.access.FeatureAccess;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.trims_on_tools.api.data.TrimGlowData;
import org.zeith.trims_on_tools.compat.elytratrims.ElytraTrimsToT;

@Mixin(value = FeatureAccess.class, remap = false)
public class FeatureAccessMixin
{
	@Inject(
			method = "hasGlow",
			at = @At("HEAD"),
			cancellable = true
	)
	private void ToolTrims_hasGlow(ItemStack stack, CallbackInfoReturnable<Boolean> cir)
	{
		if(!ElytraTrimsToT.isRenderingTooltip && TrimGlowData.getGlowData(stack).map(TrimGlowData::glow).orElse(false))
			cir.setReturnValue(true);
	}
}