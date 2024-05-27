package org.zeith.trims_on_tools.api;

import net.minecraft.resources.ResourceLocation;

public record TrimKey(ResourceLocation pattern, ResourceLocation material)
{
	public ResourceLocation id()
	{
		return new ResourceLocation(pattern.getNamespace() + "_" + material.getNamespace(), pattern.getPath() + "_" + material.getPath());
	}
}