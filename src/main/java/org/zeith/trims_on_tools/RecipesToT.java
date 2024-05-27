package org.zeith.trims_on_tools;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.annotations.ProvideRecipes;
import org.zeith.hammerlib.api.IRecipeProvider;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;

@ProvideRecipes
public class RecipesToT
	implements IRecipeProvider
{
	@Override
	public void provideRecipes(RegisterRecipesEvent e)
	{
		e.removeRecipe(new ResourceLocation("elytratrims", "elytra_glow"));
	}
}