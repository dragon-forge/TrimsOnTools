package org.zeith.trims_on_tools.init;

import net.minecraft.core.component.DataComponentType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.registrars.Registrar;
import org.zeith.trims_on_tools.api.data.ToolTrim;
import org.zeith.trims_on_tools.api.data.TrimGlowData;

@SimplyRegister
public interface DataComponentsToT
{
	@RegistryName("tool_trim")
	Registrar<DataComponentType<ToolTrim>> TRIM = Registrar.dataComponentType(
			DataComponentType.<ToolTrim>builder()
					.persistent(ToolTrim.CODEC)
					.networkSynchronized(ToolTrim.STREAM_CODEC)
					.cacheEncoding()
	);
	
	@RegistryName("trim_glow")
	Registrar<DataComponentType<TrimGlowData>> GLOW = Registrar.dataComponentType(
			DataComponentType.<TrimGlowData>builder()
					.persistent(TrimGlowData.CODEC)
					.networkSynchronized(TrimGlowData.STREAM_CODEC)
					.cacheEncoding()
	);
}