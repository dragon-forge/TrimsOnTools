package org.zeith.trims_on_tools.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

public class TextureAtlasHolderToT
		extends TextureAtlasHolder
{
	public TextureAtlasHolderToT(TextureManager manager, ResourceLocation location, ResourceLocation location1)
	{
		super(manager, location, location1);
	}
	
	@Override
	public TextureAtlasSprite getSprite(ResourceLocation location)
	{
		return super.getSprite(location);
	}
}