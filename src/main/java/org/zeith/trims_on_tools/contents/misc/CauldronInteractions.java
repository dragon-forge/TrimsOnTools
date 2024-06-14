package org.zeith.trims_on_tools.contents.misc;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.zeith.trims_on_tools.api.data.TrimGlowData;

@EventBusSubscriber
public class CauldronInteractions
{
	@SubscribeEvent
	public static void playerInteract(PlayerInteractEvent.RightClickBlock e)
	{
		var level = e.getLevel();
		var pos = e.getPos();
		
		var state = level.getBlockState(pos);
		
		var heldStack = e.getItemStack();
		if(heldStack.isEmpty()) return;
		
		boolean succcess = false;
		
		if(state.is(Blocks.WATER_CAULDRON))
		{
			var trim = TrimGlowData.getGlowData(heldStack).orElse(null);
			if(trim == null || !trim.glow()) return;
			TrimGlowData.setGlowData(heldStack, null);
			level.playSound(e.getEntity(), pos, SoundEvents.PLAYER_SPLASH, SoundSource.PLAYERS, 0.5F, 2F);
			LayeredCauldronBlock.lowerFillLevel(state, level, pos);
			succcess = true;
		}
		
		if(succcess)
		{
			e.setCanceled(true);
			e.setCancellationResult(InteractionResult.SUCCESS);
		}
	}
}
