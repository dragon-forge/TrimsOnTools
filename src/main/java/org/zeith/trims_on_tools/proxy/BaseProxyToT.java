package org.zeith.trims_on_tools.proxy;

import net.neoforged.bus.api.IEventBus;
import org.zeith.hammerlib.api.proxy.IProxy;

public abstract class BaseProxyToT
		implements IProxy
{
	public abstract void construct(IEventBus modBus);
}