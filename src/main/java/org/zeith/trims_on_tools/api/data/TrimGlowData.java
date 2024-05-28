package org.zeith.trims_on_tools.api.data;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.zeith.trims_on_tools.TrimsOnToolsMod;

import java.util.List;
import java.util.Optional;

import static org.zeith.trims_on_tools.ConstantsToT.TAG_TRIM_GLOW_ID;

public record TrimGlowData(boolean glow)
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public static final Codec<TrimGlowData> CODEC = RecordCodecBuilder.create((inst) ->
			inst.group(
					Codec.BOOL.fieldOf("glow").forGetter(TrimGlowData::glow)
			).apply(inst, TrimGlowData::new)
	);
	
	public static boolean setGlowData(ItemStack stack, TrimGlowData trim)
	{
		if(trim == null)
		{
			stack.removeTagKey(TAG_TRIM_GLOW_ID);
			return true;
		}
		stack.getOrCreateTag().put(TAG_TRIM_GLOW_ID, CODEC.encodeStart(NbtOps.INSTANCE, trim).result().orElseThrow());
		return true;
	}
	
	public static Optional<TrimGlowData> getGlowData(ItemStack stack)
	{
		if(stack.getTag() != null && stack.getTag().contains(TAG_TRIM_GLOW_ID))
		{
			CompoundTag compoundtag = stack.getTagElement(TAG_TRIM_GLOW_ID);
			var armortrim = CODEC.parse(NbtOps.INSTANCE, compoundtag).resultOrPartial(LOGGER::error).orElse(null);
			return Optional.ofNullable(armortrim);
		} else
		{
			return Optional.empty();
		}
	}
	
	public TrimGlowData copyWithGlow(boolean glow)
	{
		return new TrimGlowData(glow);
	}
	
	public static void appendUpgradeHoverText(ItemStack stack, RegistryAccess access, List<Component> hoverText)
	{
		var gd = TrimGlowData.getGlowData(stack);
		if(gd.isEmpty()) return;
		var glowData = gd.get();
		if(glowData.glow())
			hoverText.add(CommonComponents.space().append(Component.translatable(Util.makeDescriptionId("trim_pattern", TrimsOnToolsMod.id("glowing"))).copy().withStyle(Style.EMPTY.withColor(0x83F8F8))));
	}
}