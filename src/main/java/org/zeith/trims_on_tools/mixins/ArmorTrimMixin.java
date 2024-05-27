package org.zeith.trims_on_tools.mixins;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.trims_on_tools.api.ToolTrim;
import org.zeith.trims_on_tools.api.TrimGlowData;

import java.util.List;

@Mixin(ArmorTrim.class)
public class ArmorTrimMixin
{
	@Inject(
			method = "appendUpgradeHoverText",
			at = @At("TAIL")
	)
	private static void ToolTrims_appendUpgradeHoverText(ItemStack stack, RegistryAccess access, List<Component> hoverText, CallbackInfo ci)
	{
		ToolTrim.appendUpgradeHoverText(stack, access, hoverText);
		TrimGlowData.appendUpgradeHoverText(stack, access, hoverText);
	}
}