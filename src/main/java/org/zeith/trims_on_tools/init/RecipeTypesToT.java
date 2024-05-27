package org.zeith.trims_on_tools.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.trims_on_tools.contents.crafting.SmithingGlowTrimRecipe;
import org.zeith.trims_on_tools.contents.crafting.SmithingToolTrimRecipe;

@SimplyRegister
public interface RecipeTypesToT
{
	@RegistryName("smithing_tool_trim")
	SmithingToolTrimRecipe.Type SMITHING_TOOL_TRIM = new SmithingToolTrimRecipe.Type();
	
	@RegistryName("smithing_glow_trim")
	SmithingGlowTrimRecipe.Type SMITHING_GLOW_TRIM = new SmithingGlowTrimRecipe.Type();
}