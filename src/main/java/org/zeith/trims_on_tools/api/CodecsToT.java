package org.zeith.trims_on_tools.api;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.function.Function;

public class CodecsToT
{
	public static final Codec<ICondition> CONDITION = ExtraCodecs.JSON.xmap(JsonElement::getAsJsonObject, Function.identity())
			.xmap(CraftingHelper::getCondition, CraftingHelper::serialize);
}
