package org.zeith.trims_on_tools.contents.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.recipes.SerializableRecipeType;
import org.zeith.trims_on_tools.api.*;
import org.zeith.trims_on_tools.mixins.SmithingTrimRecipeAccessor;

import java.util.Optional;

public class SmithingGlowTrimRecipe
		extends SmithingTrimRecipe
{
	final Ingredient template;
	final Ingredient base;
	final Ingredient addition;
	
	public SmithingGlowTrimRecipe(ResourceLocation id, Ingredient template, Ingredient base, Ingredient addition)
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
			var hasTrim = ToolTrim.getTrim(access, itemstack).isPresent()
						  || ArmorTrim.getTrim(access, itemstack).isPresent();
			
			if(!hasTrim) return ItemStack.EMPTY;
			
			var prev = TrimGlowData.getGlowData(access, itemstack).orElseGet(() -> new TrimGlowData(false));
			
			var next = prev.copyWithGlow(true);
			
			if(prev.equals(next))
				return ItemStack.EMPTY;
			
			ItemStack stack = itemstack.copyWithCount(1);
			if(TrimGlowData.setGlowData(access, stack, next))
				return stack;
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess access)
	{
		ItemStack itemstack = new ItemStack(Items.IRON_PICKAXE);
		Optional<Holder.Reference<TrimPattern>> optional = access.registryOrThrow(RegistriesToT.TOOL_TRIM_PATTERN).holders().findFirst();
		if(optional.isPresent())
		{
			Optional<Holder.Reference<ToolTrimMaterial>> optional1 = access.registryOrThrow(RegistriesToT.TOOL_TRIM_MATERIAL).getHolder(ToolTrimMaterial.REDSTONE);
			if(optional1.isPresent())
			{
				ToolTrim trim = new ToolTrim(optional1.get(), optional.get());
				ToolTrim.setTrim(access, itemstack, trim);
				TrimGlowData.setGlowData(access, itemstack, new TrimGlowData(true));
			}
		}
		
		return itemstack;
	}
	
	public static class Type
			extends SerializableRecipeType<SmithingGlowTrimRecipe>
	{
		@Override
		public SmithingGlowTrimRecipe fromJson(ResourceLocation id, JsonObject json)
		{
			SmithingTrimRecipe re = RecipeSerializer.SMITHING_TRIM.fromJson(id, json);
			SmithingTrimRecipeAccessor rea = (SmithingTrimRecipeAccessor) re;
			return new SmithingGlowTrimRecipe(
					re.getId(),
					rea.getTemplate(),
					rea.getBase(),
					rea.getAddition()
			);
		}
		
		@Override
		public @Nullable SmithingGlowTrimRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf net)
		{
			SmithingTrimRecipe re = RecipeSerializer.SMITHING_TRIM.fromNetwork(id, net);
			if(re == null) return null;
			SmithingTrimRecipeAccessor rea = (SmithingTrimRecipeAccessor) re;
			return new SmithingGlowTrimRecipe(
					id,
					rea.getTemplate(),
					rea.getBase(),
					rea.getAddition()
			);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf buf, SmithingGlowTrimRecipe recipe)
		{
			RecipeSerializer.SMITHING_TRIM.toNetwork(buf, recipe);
		}
	}
}