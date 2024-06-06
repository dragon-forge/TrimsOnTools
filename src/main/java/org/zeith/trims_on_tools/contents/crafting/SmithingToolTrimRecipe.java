package org.zeith.trims_on_tools.contents.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.recipes.SerializableRecipeType;
import org.zeith.trims_on_tools.api.RegistriesToT;
import org.zeith.trims_on_tools.api.data.*;
import org.zeith.trims_on_tools.mixins.SmithingTrimRecipeAccessor;

import java.util.Optional;

public class SmithingToolTrimRecipe
		extends SmithingTrimRecipe
{
	final Ingredient template;
	final Ingredient base;
	final Ingredient addition;
	
	public SmithingToolTrimRecipe(ResourceLocation id, Ingredient template, Ingredient base, Ingredient addition)
	{
		super(id, template, base, addition);
		this.template = template;
		this.base = base;
		this.addition = addition;
	}
	
	@Override
	public ItemStack assemble(Container container, RegistryAccess access)
	{
		ItemStack inputTool = container.getItem(1);
		if(this.base.test(inputTool))
		{
			var mat = ToolTrimMaterial.getFromIngredient(access, container.getItem(2));
			var pat = ToolTrimPattern.getFromTemplate(access, container.getItem(0));
			if(mat.isPresent() && pat.isPresent())
			{
				Optional<ToolTrim> toolTrim = ToolTrim.getTrim(access, inputTool);
				if(toolTrim.isPresent() && toolTrim.get().hasPatternAndMaterial(pat.get(), mat.get()))
					return ItemStack.EMPTY;
				
				ItemStack trimmedStack = inputTool.copyWithCount(1);
				ToolTrim trim = new ToolTrim(mat.get(), pat.get());
				if(ToolTrim.setTrim(access, trimmedStack, trim))
					return trimmedStack;
			}
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess access)
	{
		ItemStack exampleTool = new ItemStack(Items.IRON_PICKAXE);
		
		Optional<Holder.Reference<ToolTrimPattern>> pat = access.registryOrThrow(RegistriesToT.TOOL_TRIM_PATTERN).holders().findFirst();
		Optional<Holder.Reference<ToolTrimMaterial>> mat = access.registryOrThrow(RegistriesToT.TOOL_TRIM_MATERIAL).getHolder(ToolTrimMaterial.REDSTONE);
		
		if(pat.isPresent() && mat.isPresent())
			ToolTrim.setTrim(access, exampleTool, new ToolTrim(mat.get(), pat.get()));
		
		return exampleTool;
	}
	
	public static class Type
			extends SerializableRecipeType<SmithingToolTrimRecipe>
	{
		@Override
		public SmithingToolTrimRecipe fromJson(ResourceLocation id, JsonObject json)
		{
			SmithingTrimRecipe re = RecipeSerializer.SMITHING_TRIM.fromJson(id, json);
			SmithingTrimRecipeAccessor rea = (SmithingTrimRecipeAccessor) re;
			return new SmithingToolTrimRecipe(
					re.getId(),
					rea.getTemplate(),
					rea.getBase(),
					rea.getAddition()
			);
		}
		
		@Override
		public @Nullable SmithingToolTrimRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf net)
		{
			SmithingTrimRecipe re = RecipeSerializer.SMITHING_TRIM.fromNetwork(id, net);
			if(re == null) return null;
			SmithingTrimRecipeAccessor rea = (SmithingTrimRecipeAccessor) re;
			return new SmithingToolTrimRecipe(
					id,
					rea.getTemplate(),
					rea.getBase(),
					rea.getAddition()
			);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf buf, SmithingToolTrimRecipe recipe)
		{
			RecipeSerializer.SMITHING_TRIM.toNetwork(buf, recipe);
		}
	}
}