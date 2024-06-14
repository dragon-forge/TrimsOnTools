package org.zeith.trims_on_tools.contents.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.registrars.SerializableRecipeType;
import org.zeith.trims_on_tools.api.RegistriesToT;
import org.zeith.trims_on_tools.api.data.*;
import org.zeith.trims_on_tools.mixins.SmithingTrimRecipeAccessor;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class SmithingToolTrimRecipe
		extends SmithingTrimRecipe
{
	final Ingredient template;
	final Ingredient base;
	final Ingredient addition;
	
	public SmithingToolTrimRecipe(Ingredient template, Ingredient base, Ingredient addition)
	{
		super( template, base, addition);
		this.template = template;
		this.base = base;
		this.addition = addition;
	}
	
	@Override
	public ItemStack assemble(SmithingRecipeInput container, HolderLookup.Provider access)
	{
		ItemStack inputTool = container.getItem(1);
		if(this.base.test(inputTool))
		{
			var mat = ToolTrimMaterial.getFromIngredient(access, container.getItem(2));
			var pat = ToolTrimPattern.getFromTemplate(access, container.getItem(0));
			if(mat.isPresent() && pat.isPresent())
			{
				Optional<ToolTrim> toolTrim = ToolTrim.getTrim(inputTool);
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
	public ItemStack getResultItem(HolderLookup.Provider access)
	{
		ItemStack exampleTool = new ItemStack(Items.IRON_PICKAXE);
		
		Optional<Holder.Reference<ToolTrimPattern>> pat = access.lookupOrThrow(RegistriesToT.TOOL_TRIM_PATTERN).listElements().findFirst();
		Optional<Holder.Reference<ToolTrimMaterial>> mat = access.lookupOrThrow(RegistriesToT.TOOL_TRIM_MATERIAL).get(ToolTrimMaterial.REDSTONE);
		
		if(pat.isPresent() && mat.isPresent())
			ToolTrim.setTrim(access, exampleTool, new ToolTrim(mat.get(), pat.get()));
		
		return exampleTool;
	}
	
	public static class Type
			extends SerializableRecipeType<SmithingToolTrimRecipe>
	{
		public static final MapCodec<SmithingToolTrimRecipe> CODEC = RecipeSerializer.SMITHING_TRIM.codec()
				.xmap(re ->
				{
					SmithingTrimRecipeAccessor rea = (SmithingTrimRecipeAccessor) re;
					return new SmithingToolTrimRecipe(
							rea.getTemplate(),
							rea.getBase(),
							rea.getAddition()
					);
				}, UnaryOperator.identity());
		
		@Override
		public void toNetwork(RegistryFriendlyByteBuf buf, SmithingToolTrimRecipe recipe)
		{
			RecipeSerializer.SMITHING_TRIM.streamCodec().encode(buf, recipe);
		}
		
		@Override
		public @Nullable SmithingToolTrimRecipe fromNetwork(RegistryFriendlyByteBuf buf)
		{
			SmithingTrimRecipe re = RecipeSerializer.SMITHING_TRIM.streamCodec().decode(buf);
			if(re == null) return null;
			SmithingTrimRecipeAccessor rea = (SmithingTrimRecipeAccessor) re;
			return new SmithingToolTrimRecipe(
					rea.getTemplate(),
					rea.getBase(),
					rea.getAddition()
			);
		}
		
		@Override
		public MapCodec<SmithingToolTrimRecipe> codec()
		{
			return CODEC;
		}
	}
}