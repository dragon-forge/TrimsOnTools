package org.zeith.trims_on_tools;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.zeith.hammerlib.api.items.CreativeTab;
import org.zeith.hammerlib.api.proxy.IProxy;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.event.fml.FMLFingerprintCheckEvent;
import org.zeith.hammerlib.proxy.HLConstants;
import org.zeith.hammerlib.util.CommonMessages;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.trims_on_tools.init.ItemsToT;
import org.zeith.trims_on_tools.proxy.*;

@Mod(TrimsOnToolsMod.MOD_ID)
public class TrimsOnToolsMod
{
	public static final Logger LOG = LogUtils.getLogger();
	public static final String MOD_ID = "trims_on_tools";
	
	public static final BaseProxyToT PROXY = IProxy.create(() -> ClientProxyToT::new, () -> ServerProxyToT::new);
	
	@CreativeTab.RegisterTab
	public static final CreativeTab TAB = new CreativeTab(id("root"), b ->
			b.icon(ItemsToT.GLOW_TOOL_TRIM_SMITHING_TEMPLATE::getDefaultInstance)
	).putAfter(HLConstants.HL_TAB);
	
	public TrimsOnToolsMod(IEventBus mb)
	{
		CommonMessages.printMessageOnIllegalRedistribution(TrimsOnToolsMod.class, LogManager.getLogger("TrimsOnToolsMod"), "TrimsOnTools", "https://modrinth.com/project/b3wKSVMw");
		LanguageAdapter.registerMod(MOD_ID);
		PROXY.construct(mb);
		mb.addListener(this::checkFingerprint);
	}
	
	public void checkFingerprint(FMLFingerprintCheckEvent e)
	{
		CommonMessages.printMessageOnFingerprintViolation(e, "97e852e9b3f01b83574e8315f7e77651c6605f2b455919a7319e9869564f013c", LogManager.getLogger("TrimsOnToolsMod"), "TrimsOnTools", "https://modrinth.com/project/b3wKSVMw");
	}
	
	public static ResourceLocation id(String path)
	{
		return Resources.location(MOD_ID, path);
	}
}