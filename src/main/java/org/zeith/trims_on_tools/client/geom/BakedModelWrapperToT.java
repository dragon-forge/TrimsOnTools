package org.zeith.trims_on_tools.client.geom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

import java.util.List;

public class BakedModelWrapperToT<T extends BakedModel>
		extends BakedModelWrapper<T>
{
	public BakedModelWrapperToT(T originalModel)
	{
		super(originalModel);
	}
	
	@Override
	public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform)
	{
		var bm = super.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
		if(bm == originalModel) return this;
		return bm;
	}
	
	@Override
	public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
	{
		var bms = super.getRenderPasses(itemStack, fabulous);
		if(bms.size() == 1 && bms.get(0) == originalModel) return List.of(this);
		return bms;
	}
}