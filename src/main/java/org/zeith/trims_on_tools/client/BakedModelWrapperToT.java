package org.zeith.trims_on_tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.BakedModelWrapper;

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
}