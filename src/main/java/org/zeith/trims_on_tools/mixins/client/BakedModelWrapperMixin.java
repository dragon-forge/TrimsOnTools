package org.zeith.trims_on_tools.mixins.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.trims_on_tools.api.ToolTrim;
import org.zeith.trims_on_tools.client.TrimPassGen;

import java.util.List;

@Mixin(BakedModelWrapper.class)
public abstract class BakedModelWrapperMixin
		implements BakedModel
{
	@Inject(
			method = "getRenderPasses",
			at = @At("RETURN"),
			cancellable = true,
			remap = false
	)
	private void ToolTrims_getRenderPasses(ItemStack itemStack, boolean fabulous, CallbackInfoReturnable<List<BakedModel>> cir)
	{
		if(!itemStack.is(ToolTrim.TRIMMABLE_TOOLS)) return;
		
		var prev = cir.getReturnValue();
		var n = TrimPassGen.getRenderPasses(itemStack, fabulous, prev);
		if(n != prev) cir.setReturnValue(n);
	}
}