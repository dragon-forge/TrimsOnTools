package org.zeith.trims_on_tools.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.trims_on_tools.api.data.TrimGlowData;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
		extends RenderLayer<T, M>
{
	@Unique
	private int toolTrims$prevEmission;
	
	public HumanoidArmorLayerMixin(RenderLayerParent<T, M> p_117346_)
	{
		super(p_117346_);
	}
	
	@ModifyVariable(
			method = "renderArmorPiece",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/armortrim/ArmorTrim;getTrim(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;"),
			index = 5,
			argsOnly = true,
			remap = false
	)
	private int ToolTrims_renderTrimEmission(int prev, PoseStack p_117119_, MultiBufferSource p_117120_, T entity, EquipmentSlot slot, int p_117123_, A p_117124_)
	{
		toolTrims$prevEmission = prev;
		var emm = TrimGlowData.getGlowData(entity.getItemBySlot(slot)).orElse(null);
		if(emm == null || !emm.glow()) return prev;
		return LightTexture.FULL_BRIGHT;
	}
	
	@ModifyVariable(
			method = "renderArmorPiece",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z"),
			index = 5,
			argsOnly = true,
			remap = false
	)
	private int ToolTrims_renderArmorPiece_postinject(int prev)
	{
		return toolTrims$prevEmission;
	}
}