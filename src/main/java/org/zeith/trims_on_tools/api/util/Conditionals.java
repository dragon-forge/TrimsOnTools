package org.zeith.trims_on_tools.api.util;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.util.List;

@EventBusSubscriber
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