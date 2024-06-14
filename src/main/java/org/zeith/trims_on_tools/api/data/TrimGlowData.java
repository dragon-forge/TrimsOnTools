package org.zeith.trims_on_tools.api.data;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipProvider;
import org.slf4j.Logger;
import org.zeith.trims_on_tools.TrimsOnToolsMod;
import org.zeith.trims_on_tools.init.DataComponentsToT;

import java.util.Optional;
import java.util.function.Consumer;

public record TrimGlowData(boolean glow)
		implements TooltipProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public static final Codec<TrimGlowData> CODEC = RecordCodecBuilder.create((inst) ->
			inst.group(
					Codec.BOOL.fieldOf("glow").forGetter(TrimGlowData::glow)
			).apply(inst, TrimGlowData::new)
	);
	
	public static final StreamCodec<ByteBuf, TrimGlowData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, TrimGlowData::glow,
			TrimGlowData::new
	);
	
	public static boolean setGlowData(ItemStack stack, TrimGlowData trim)
	{
		if(trim == null)
		{
			stack.remove(DataComponentsToT.GLOW);
			return true;
		}
		stack.set(DataComponentsToT.GLOW, trim);
		return true;
	}
	
	public static Optional<TrimGlowData> getGlowData(ItemStack stack)
	{
		return Optional.ofNullable(stack.get(DataComponentsToT.GLOW));
	}
	
	public TrimGlowData copyWithGlow(boolean glow)
	{
		return new TrimGlowData(glow);
	}
	
	@Override
	public void addToTooltip(Item.TooltipContext ctx, Consumer<Component> hoverText, TooltipFlag flags)
	{
		if(glow())
			hoverText.accept(CommonComponents.space().append(Component.translatable(Util.makeDescriptionId("trim_pattern", TrimsOnToolsMod.id("glowing"))).copy().withStyle(Style.EMPTY.withColor(0x83F8F8))));
	}
}