package org.zeith.trims_on_tools.contents.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.zeith.hammerlib.event.data.DataPackRegistryLoadEvent;
import org.zeith.hammerlib.mixins.LootTableAccessor;
import org.zeith.trims_on_tools.TrimsOnToolsMod;
import org.zeith.trims_on_tools.init.ItemsToT;

import java.util.Set;

@EventBusSubscriber
public class LootTableLoader
{
	public static final Set<ResourceKey<LootTable>> GLOW_TEMPLATE_TABLES = Set.of(
			BuiltInLootTables.ABANDONED_MINESHAFT,
			BuiltInLootTables.SIMPLE_DUNGEON
	);
	
	@SubscribeEvent
	public static void dataRegistry(DataPackRegistryLoadEvent e)
	{
		e.getRegistry(Registries.LOOT_TABLE).ifPresent(reg ->
		{
			for(var id : GLOW_TEMPLATE_TABLES)
			{
				var val = reg.get(id);
				if(val != null) insertGlowTrim(val);
			}
		});
	}
	
	public static void insertGlowTrim(LootTable table)
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