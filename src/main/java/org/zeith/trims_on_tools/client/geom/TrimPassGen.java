package org.zeith.trims_on_tools.client.geom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;
import org.zeith.trims_on_tools.api.data.ToolTrim;

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
		
		var extra = TrimItemModels.getModel(access, itemStack, trim);
		if(extra == null) return prev;
		
		var al = new ArrayList<BakedModel>(1 + prev.size());
		al.add(extra);
		al.addAll(prev);
		return al;
	}
}