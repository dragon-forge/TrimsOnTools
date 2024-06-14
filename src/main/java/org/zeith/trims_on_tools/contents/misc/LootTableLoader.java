package org.zeith.trims_on_tools.contents.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.zeith.hammerlib.event.data.DataPackRegistryLoadEvent;
import org.zeith.hammerlib.mixins.LootTableAccessor;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.trims_on_tools.TrimsOnToolsMod;
import org.zeith.trims_on_tools.init.ItemsToT;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber
public class LootTableLoader
{
	public static final Set<ResourceLocation> GLOW_TEMPLATE_TABLES = Stream.of(
			"minecraft:chests/abandoned_mineshaft",
			"minecraft:chests/simple_dungeon"
	).map(Resources::location).collect(Collectors.toSet());
	
	@SubscribeEvent
	public static void dataRegistry(DataPackRegistryLoadEvent e)
	{
		for(ResourceLocation id : GLOW_TEMPLATE_TABLES)
			e.inspect(Registries.LOOT_TABLE, id, table -> loadTable(id, table));
	}
	
	public static void loadTable(ResourceLocation id, LootTable table)
	{
		try
		{
			var pools = ((LootTableAccessor) table).getPools();
			
			pools.add(LootPool.lootPool()
					.setBonusRolls(ConstantValue.exactly(0))
					.setRolls(ConstantValue.exactly(1F))
					.add(EmptyLootItem.emptyItem().setWeight(2))
					.add(LootItem.lootTableItem(ItemsToT.GLOW_TOOL_TRIM_SMITHING_TEMPLATE)
							.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1F)))
					)
					.name("tooltrims:glow_tool_trim_smithing_template")
					.build());
		} catch(Throwable err)
		{
			TrimsOnToolsMod.LOG.error("Failed to inject glow_tool_trim_smithing_template into LootTable '" + table.getLootTableId() + "'!!!", err);
		}
	}
}