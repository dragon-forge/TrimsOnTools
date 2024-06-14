package org.zeith.trims_on_tools;

import org.zeith.hammerlib.annotations.ProvideRecipes;
import org.zeith.hammerlib.api.IRecipeProvider;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.util.mcf.Resources;

@ProvideRecipes
public class RecipesToT
		implements IRecipeProvider
{
	@Override
	public void provideRecipes(RegisterRecipesEvent e)
	{
		e.removeRecipe(Resources.location("elytratrims", "elytra_glow"));
	}
}