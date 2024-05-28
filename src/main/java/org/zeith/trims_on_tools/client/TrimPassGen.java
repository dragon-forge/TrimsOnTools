package org.zeith.trims_on_tools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.zeith.trims_on_tools.api.ToolTrim;

import java.util.ArrayList;
import java.util.List;

public class TrimPassGen
{
	public static List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous, List<BakedModel> prev)
	{
		RegistryAccess access;
		var level = Minecraft.getInstance().level;
		if(level != null) access = level.registryAccess();
		else access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
		ToolTrim trim = ToolTrim.getTrim(access, itemStack).orElse(null);
		if(trim == null) return prev;
		
		var al = new ArrayList<BakedModel>(1 + prev.size());
		al.add(TrimItemModels.getModel(access, itemStack, trim));
		al.addAll(prev);
		return al;
	}
}