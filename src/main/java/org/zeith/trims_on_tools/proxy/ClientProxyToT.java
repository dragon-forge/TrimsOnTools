package org.zeith.trims_on_tools.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.zeith.hammerlib.api.proxy.IClientProxy;
import org.zeith.hammerlib.util.mcf.RunnableReloader;
import org.zeith.trims_on_tools.TrimsOnToolsMod;
import org.zeith.trims_on_tools.client.TextureAtlasHolderToT;
import org.zeith.trims_on_tools.client.TrimItemModels;
import org.zeith.trims_on_tools.init.ItemsToT;
import org.zeith.trims_on_tools.init.SheetsToT;

import java.util.Map;
import java.util.stream.StreamSupport;

public class ClientProxyToT
		extends BaseProxyToT
		implements IClientProxy
{
	private static TextureAtlasHolderToT TOOL_TRIMS_ATLAS;
	
	@Override
	public void construct(IEventBus modBus)
	{
		modBus.addListener(this::clientSetup);
		modBus.addListener(this::onRegisterReloadListenerEvent);
		modBus.addListener(this::buildTabs);
	}
	
	private void buildTabs(BuildCreativeModeTabContentsEvent e)
	{
		if(CreativeModeTabs.INGREDIENTS.equals(e.getTabKey()))
		{
			putItemAfter(e, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemsToT.GLOW_TOOL_TRIM_SMITHING_TEMPLATE);
		}
	}
	
	protected void putItemAfter(BuildCreativeModeTabContentsEvent e, Item key, ItemLike newItem)
	{
		putItemAfter(e, key, new ItemStack(newItem));
	}
	
	protected void putItemAfter(BuildCreativeModeTabContentsEvent e, Item key, ItemStack newItem)
	{
		StreamSupport.stream(e.getEntries().spliterator(), false)
				.map(Map.Entry::getKey)
				.filter(s -> s.is(key))
				.findFirst()
				.ifPresentOrElse(after ->
						e.getEntries().putAfter(after, newItem, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS), () ->
						e.accept(newItem)
				);
	}
	
	public static TextureAtlasHolderToT getToolTrimsAtlas()
	{
		if(TOOL_TRIMS_ATLAS == null)
		{
			var txm = Minecraft.getInstance().getTextureManager();
			TOOL_TRIMS_ATLAS = new TextureAtlasHolderToT(txm, SheetsToT.TOOL_TRIMS_SHEET, TrimsOnToolsMod.id("tool_trims"));
		}
		return TOOL_TRIMS_ATLAS;
	}
	
	private void onRegisterReloadListenerEvent(RegisterClientReloadListenersEvent event)
	{
		event.registerReloadListener(getToolTrimsAtlas());
		event.registerReloadListener(RunnableReloader.of(TrimItemModels::reload));
	}
	
	private void clientSetup(FMLClientSetupEvent e)
	{
	}
}