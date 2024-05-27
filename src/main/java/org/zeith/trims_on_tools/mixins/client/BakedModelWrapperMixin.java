package org.zeith.trims_on_tools.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.trims_on_tools.api.ToolTrim;
import org.zeith.trims_on_tools.client.TrimItemModels;

import java.util.ArrayList;
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
		
		RegistryAccess access;
		var level = Minecraft.getInstance().level;
		if(level != null) access = level.registryAccess();
		else access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
		ToolTrim trim = ToolTrim.getTrim(access, itemStack).orElse(null);
		if(trim == null) return;
		
		var prev = cir.getReturnValue();
		var al = new ArrayList<BakedModel>(1 + prev.size());
		al.add(TrimItemModels.getModel(access, itemStack, trim));
		al.addAll(prev);
		cir.setReturnValue(al);
	}
}