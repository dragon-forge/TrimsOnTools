package org.zeith.trims_on_tools.mixins;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTrimRecipe.class)
public interface SmithingTrimRecipeAccessor
{
	@Accessor
	Ingredient getTemplate();
	
	@Accessor
	Ingredient getBase();
	
	@Accessor
	Ingredient getAddition();
}
