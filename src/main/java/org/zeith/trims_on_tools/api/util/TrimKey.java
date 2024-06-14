package org.zeith.trims_on_tools.api.util;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.util.mcf.Resources;

public record TrimKey(ResourceLocation pattern, ResourceLocation material)
{
	public ResourceLocation id()
	{
		return Resources.location(pattern.getNamespace() + "_" + material.getNamespace(), pattern.getPath() + "_" + material.getPath());
	}
}