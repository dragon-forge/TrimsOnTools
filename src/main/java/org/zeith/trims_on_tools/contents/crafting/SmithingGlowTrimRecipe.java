package org.zeith.trims_on_tools.contents.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.registrars.SerializableRecipeType;
import org.zeith.trims_on_tools.api.RegistriesToT;
import org.zeith.trims_on_tools.api.data.*;
import org.zeith.trims_on_tools.mixins.SmithingTrimRecipeAccessor;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class SmithingGlowTrimRecipe
		extends SmithingTrimRecipe
{
	final Ingredient template;
	final Ingredient base;
	final Ingredient addition;
	
	public SmithingGlowTrimRecipe(Ingredient template, Ingredient base, Ingredient addition)
	{
		super(template, base, addition);
		this.template = template;
		this.base = base;
		this.addition = addition;
	}
	
	@Override
	public ItemStack assemble(SmithingRecipeInput container, HolderLookup.Provider access)
	{
		ItemStack itemstack = container.getItem(1);
		if(this.base.test(itemstack))
		{
			var hasTrim = ToolTrim.getTrim(itemstack).isPresent()
						  || itemstack.get(DataComponents.TRIM) != null;
			
			if(!hasTrim) return ItemStack.EMPTY;
			
			var prev = TrimGlowData.getGlowData(itemstack).orElseGet(() -> new TrimGlowData(false));
			
			var next = prev.copyWithGlow(true);
			
			if(prev.equals(next))
				return ItemStack.EMPTY;
			
			ItemStack stack = itemstack.copyWithCount(1);
			if(TrimGlowData.setGlowData(stack, next))
				return stack;
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack getResultItem(HolderLookup.Provider access)
	{
		ItemStack stack = new ItemStack(Items.IRON_PICKAXE);
		Optional<Holder.Reference<ToolTrimPattern>> optional = access.lookupOrThrow(RegistriesToT.TOOL_TRIM_PATTERN).listElements().findFirst();
		if(optional.isPresent())
		{
			Optional<Holder.Reference<ToolTrimMaterial>> optional1 = access.lookupOrThrow(RegistriesToT.TOOL_TRIM_MATERIAL).get(ToolTrimMaterial.REDSTONE);
			if(optional1.isPresent())
			{
				ToolTrim trim = new ToolTrim(optional1.get(), optional.get());
				ToolTrim.setTrim(access, stack, trim);
				TrimGlowData.setGlowData(stack, new TrimGlowData(true));
			}
		}
		
		return stack;
	}
	
	public static class Type
			extends SerializableRecipeType<SmithingGlowTrimRecipe>
	{
		public static final MapCodec<SmithingGlowTrimRecipe> CODEC = RecipeSerializer.SMITHING_TRIM.codec()
				.xmap(re ->
				{
					SmithingTrimRecipeAccessor rea = (SmithingTrimRecipeAccessor) re;
					return new SmithingGlowTrimRecipe(
							rea.getTemplate(),
							rea.getBase(),
							rea.getAddition()
					);
				}, UnaryOperator.identity());
		
		@Override
		public void toNetwork(RegistryFriendlyByteBuf buf, SmithingGlowTrimRecipe recipe)
		{
			RecipeSerializer.SMITHING_TRIM.streamCodec().encode(buf, recipe);
		}
		
		@Override
		public @Nullable SmithingGlowTrimRecipe fromNetwork(RegistryFriendlyByteBuf buf)
		{
			SmithingTrimRecipe re = RecipeSerializer.SMITHING_TRIM.streamCodec().decode(buf);
			if(re == null) return null;
			SmithingTrimRecipeAccessor rea = (SmithingTrimRecipeAccessor) re;
			return new SmithingGlowTrimRecipe(
					rea.getTemplate(),
					rea.getBase(),
					rea.getAddition()
			);
		}
		
		@Override
		public MapCodec<SmithingGlowTrimRecipe> codec()
		{
			return CODEC;
		}
	}
}