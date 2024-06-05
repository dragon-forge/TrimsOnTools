package org.zeith.trims_on_tools.api.util;

import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class Conditionals
{
	public static ICondition.IContext currentServerContext = ICondition.IContext.EMPTY;
	
	public static boolean processConditions(List<ICondition> conditions)
	{
		return processConditions(currentServerContext, conditions);
	}
	
	public static boolean processConditions(ICondition.IContext context, List<ICondition> conditions)
	{
		for(var cond : conditions)
			if(!cond.test(context))
				return false;
		return true;
	}
	
	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent e)
	{
		currentServerContext = e.getConditionContext();
	}
	
	@SubscribeEvent
	public static void serverStopping(ServerStoppingEvent e)
	{
		currentServerContext = null;
	}
}