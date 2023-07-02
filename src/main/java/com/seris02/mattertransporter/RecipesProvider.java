package com.seris02.mattertransporter;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class RecipesProvider extends RecipeProvider {

	public RecipesProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(Content.MATTER_TRANSPORTER.get())
		.pattern(" e ")
		.pattern(" de")
		.pattern("s  ")
		.define('s', Tags.Items.RODS_WOODEN)
		.define('d', Tags.Items.GEMS_DIAMOND)
		.define('e', Tags.Items.ENDER_PEARLS)
		.save(consumer);
		ShapedRecipeBuilder.shaped(Content.NETHERITE_MATTER_TRANSPORTER.get())
		.pattern(" e ")
		.pattern(" de")
		.pattern("s  ")
		.define('s', Tags.Items.RODS_WOODEN)
		.define('d', Tags.Items.INGOTS_NETHERITE)
		.define('e', Tags.Items.ENDER_PEARLS)
		.save(consumer);
	}
	
	@Override
	public String getName() {
		return "Matter Transporter Recipes";
	}
}