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
import org.zeith.trims_on_tools.api.*;
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
		ItemStack itemstack = container.getItem(1);
		if(this.base.test(itemstack))
		{
			Optional<Holder.Reference<ToolTrimMaterial>> optional = ToolTrimMaterial.getFromIngredient(access, container.getItem(2));
			Optional<Holder.Reference<ToolTrimPattern>> optional1 = RegistriesToT.getFromTemplate(access, container.getItem(0));
			if(optional.isPresent() && optional1.isPresent())
			{
				Optional<ToolTrim> toolTrim = ToolTrim.getTrim(access, itemstack);
				if(toolTrim.isPresent() && toolTrim.get().hasPatternAndMaterial(optional1.get(), optional.get()))
					return ItemStack.EMPTY;
				
				ItemStack itemstack1 = itemstack.copy();
				itemstack1.setCount(1);
				ToolTrim armortrim = new ToolTrim(optional.get(), optional1.get());
				if(ToolTrim.setTrim(access, itemstack1, armortrim))
					return itemstack1;
			}
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess access)
	{
		ItemStack itemstack = new ItemStack(Items.IRON_PICKAXE);
		Optional<Holder.Reference<ToolTrimPattern>> optional = access.registryOrThrow(RegistriesToT.TOOL_TRIM_PATTERN).holders().findFirst();
		if(optional.isPresent())
		{
			Optional<Holder.Reference<ToolTrimMaterial>> optional1 = access.registryOrThrow(RegistriesToT.TOOL_TRIM_MATERIAL).getHolder(ToolTrimMaterial.REDSTONE);
			if(optional1.isPresent())
			{
				ToolTrim trim = new ToolTrim(optional1.get(), optional.get());
				ToolTrim.setTrim(access, itemstack, trim);
			}
		}
		
		return itemstack;
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