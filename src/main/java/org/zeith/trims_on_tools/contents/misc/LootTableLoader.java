package org.zeith.trims_on_tools.contents.misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.zeith.hammerlib.core.adapter.LootTableAdapter;
import org.zeith.trims_on_tools.TrimsOnToolsMod;
import org.zeith.trims_on_tools.init.ItemsToT;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LootTableLoader
{
	public static final Set<ResourceLocation> GLOW_TEMPLATE_TABLES = Stream.of(
			"minecraft:chests/abandoned_mineshaft",
			"minecraft:chests/simple_dungeon"
	).map(ResourceLocation::new).collect(Collectors.toSet());
	
	public static void loadTable(ResourceLocation id, LootTable table)
	{
		if(!GLOW_TEMPLATE_TABLES.contains(id))
			return;
		
		try
		{
			var pools = LootTableAdapter.getPools(table);
			
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