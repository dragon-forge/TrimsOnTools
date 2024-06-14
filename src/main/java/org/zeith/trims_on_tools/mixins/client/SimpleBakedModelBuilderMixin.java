package org.zeith.trims_on_tools.mixins.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.trims_on_tools.client.geom.BakedModelWrapperToT;

@Mixin(SimpleBakedModel.Builder.class)
public abstract class SimpleBakedModelBuilderMixin
		implements BakedModel
{
	@Inject(
			method = "build(Lnet/neoforged/neoforge/client/RenderTypeGroup;)Lnet/minecraft/client/resources/model/BakedModel;",
			at = @At("RETURN"),
			cancellable = true,
			remap = false
	)
	private void ToolTrims_build(CallbackInfoReturnable<BakedModel> cir)
	{
		var rv = cir.getReturnValue();
		if(!(rv instanceof BakedModelWrapper<?>))
			cir.setReturnValue(new BakedModelWrapperToT<>(rv));
	}
}