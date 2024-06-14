package org.zeith.trims_on_tools.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import org.zeith.hammerlib.api.proxy.IClientProxy;
import org.zeith.hammerlib.util.mcf.RunnableReloader;
import org.zeith.trims_on_tools.TrimsOnToolsMod;
import org.zeith.trims_on_tools.client.geom.TrimItemModels;
import org.zeith.trims_on_tools.client.resource.TrimPermutationsSource;
import org.zeith.trims_on_tools.init.ItemsToT;

import java.util.Map;
import java.util.stream.StreamSupport;

public class ClientProxyToT
		extends BaseProxyToT
		implements IClientProxy
{
	@Override
	public void construct(IEventBus modBus)
	{
		modBus.addListener(this::registerSpriteSources);
		modBus.addListener(this::onRegisterReloadListenerEvent);
		modBus.addListener(this::buildTabs);
		
		NeoForge.EVENT_BUS.addListener(this::clientTick);
		NeoForge.EVENT_BUS.addListener(this::tagsUpdated);
	}
	
	protected boolean hasLevel;
	
	private void clientTick(ClientTickEvent.Pre event)
	{
		var hasLevel = Minecraft.getInstance().level != null;
		if(hasLevel != this.hasLevel)
		{
			this.hasLevel = hasLevel;
			
			if(!hasLevel)
			{
				TrimItemModels.resetRegistryCache();
			}
		}
	}
	
	private void tagsUpdated(TagsUpdatedEvent e)
	{
		if(e.getUpdateCause() != TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) return;
		TrimItemModels.resetRegistryCache(); // Wipe the cache just to be extra safe!
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
	
	private void onRegisterReloadListenerEvent(RegisterClientReloadListenersEvent event)
	{
		event.registerReloadListener(RunnableReloader.of(TrimItemModels::reload));
	}
	
	private void registerSpriteSources(RegisterSpriteSourceTypesEvent e)
	{
		TrimPermutationsSource.TRIM_PERMUTATIONS = e.register(TrimsOnToolsMod.id("trim_permutations"), TrimPermutationsSource.CODEC);
	}
}