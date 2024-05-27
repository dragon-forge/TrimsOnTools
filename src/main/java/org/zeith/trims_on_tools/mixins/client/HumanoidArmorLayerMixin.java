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
import org.zeith.trims_on_tools.api.TrimGlowData;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
		extends RenderLayer<T, M>
{
	@Unique
	private TrimGlowData toolTrims$emission;
	
	public HumanoidArmorLayerMixin(RenderLayerParent<T, M> p_117346_)
	{
		super(p_117346_);
	}
	
	@Inject(
			method = "renderArmorPiece",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/armortrim/ArmorTrim;getTrim(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;")
	)
	private void ToolTrims_renderArmorPiece_preinject(PoseStack p_117119_, MultiBufferSource p_117120_, T entity, EquipmentSlot slot, int p_117123_, A p_117124_, CallbackInfo ci)
	{
		toolTrims$emission = TrimGlowData.getGlowData(entity.level().registryAccess(), entity.getItemBySlot(slot)).orElse(null);
	}
	
	@ModifyVariable(
			method = "renderTrim(Lnet/minecraft/world/item/ArmorMaterial;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
			at = @At("HEAD"),
			index = 4,
			argsOnly = true,
			remap = false
	)
	private int ToolTrims_renderTrimEmission(int prev)
	{
		if(toolTrims$emission == null || !toolTrims$emission.glow()) return prev;
		return LightTexture.FULL_BRIGHT;
	}
	
	@Inject(
			method = "renderArmorPiece",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z")
	)
	private void ToolTrims_renderArmorPiece_postinject(PoseStack p_117119_, MultiBufferSource p_117120_, T entity, EquipmentSlot slot, int p_117123_, A p_117124_, CallbackInfo ci)
	{
		toolTrims$emission = null;
	}
}