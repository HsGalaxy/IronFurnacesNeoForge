/*
 * Copyright 2025 pizzaatime and XenoMustache
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ironfurnaces.jei;

import com.google.common.collect.Lists;
import ironfurnaces.IronFurnaces;
import ironfurnaces.init.Registration;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.recipes.SimpleGeneratorRecipe;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.EventHooks;

import java.util.List;

@JeiPlugin
public class IronFurnacesJEIPlugin implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(IronFurnaces.MOD_ID, "plugin_" + IronFurnaces.MOD_ID);
	}

	@Override
	public void registerAdvanced(IAdvancedRegistration registration) {

	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {

			registration.addRecipeCategories(new RecipeCategoryGeneratorBlasting(registration.getJeiHelpers().getGuiHelper()));
			registration.addRecipeCategories(new RecipeCategoryGeneratorSmoking(registration.getJeiHelpers().getGuiHelper()));
			registration.addRecipeCategories(new RecipeCategoryGeneratorRegular(registration.getJeiHelpers().getGuiHelper()));


	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {


			List<SimpleGeneratorRecipe> recipes = Lists.newArrayList();
			for (Item item : BuiltInRegistries.ITEM.stream().toList())
			{
				int burntime = EventHooks.getItemBurnTime(new ItemStack(item), item.getBurnTime(new ItemStack(item), RecipeType.SMELTING), RecipeType.SMELTING);
				if (burntime > 0)
				{
					ItemStack stack = new ItemStack(item);
					recipes.add(new SimpleGeneratorRecipe(burntime * 20, stack));
				}
			}
			registration.addRecipes(Registration.RecipeTypes.GENERATOR_REGULAR, recipes);

			List<GeneratorRecipe> recipes1 = Lists.newArrayList();
			List<RecipeHolder<GeneratorRecipe>> list = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(Registration.GENERATOR_RECIPE_TYPE.get()).stream().toList();
			for (RecipeHolder<GeneratorRecipe> item : list)
			{
				recipes1.add(item.value());
			}
			registration.addRecipes(Registration.RecipeTypes.GENERATOR_BLASTING, recipes1);

			List<SimpleGeneratorRecipe> recipes2 = Lists.newArrayList();
			for (Item item : BuiltInRegistries.ITEM.stream().toList())
			{
				ItemStack stack = new ItemStack(item);
				if (stack.get(DataComponents.FOOD) != null && stack.get(DataComponents.FOOD).nutrition() > 0) {
					recipes2.add(new SimpleGeneratorRecipe(BlockIronFurnaceTileBase.getSmokingBurn(stack) * 40, stack));
				}
			}
			registration.addRecipes(Registration.RecipeTypes.GENERATOR_SMOKING, recipes2);



	}


	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
			registry.addRecipeCatalyst(new ItemStack(Registration.BLASTING_AUGMENT.get()), RecipeTypes.BLASTING);
			registry.addRecipeCatalyst(new ItemStack(Registration.SMOKING_AUGMENT.get()), RecipeTypes.SMOKING);

			registry.addRecipeCatalyst(new ItemStack(Registration.GENERATOR_AUGMENT.get()), Registration.RecipeTypes.GENERATOR_REGULAR);
			registry.addRecipeCatalyst(new ItemStack(Registration.GENERATOR_AUGMENT.get()), Registration.RecipeTypes.GENERATOR_BLASTING);
			registry.addRecipeCatalyst(new ItemStack(Registration.GENERATOR_AUGMENT.get()), Registration.RecipeTypes.GENERATOR_SMOKING);

			registry.addRecipeCatalyst(new ItemStack(Registration.FACTORY_AUGMENT.get()), RecipeTypes.SMELTING);

			registry.addRecipeCatalyst(new ItemStack(Registration.IRON_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(Registration.GOLD_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(Registration.DIAMOND_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(Registration.EMERALD_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(Registration.OBSIDIAN_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(Registration.CRYSTAL_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(Registration.NETHERITE_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(Registration.COPPER_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(Registration.SILVER_FURNACE.get()), RecipeTypes.SMELTING);


			registry.addRecipeCatalyst(new ItemStack(Registration.MILLION_FURNACE.get()), RecipeTypes.SMELTING);


			registry.addRecipeCatalyst(new ItemStack(Registration.IRON_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(Registration.GOLD_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(Registration.DIAMOND_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(Registration.EMERALD_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(Registration.OBSIDIAN_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(Registration.CRYSTAL_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(Registration.NETHERITE_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(Registration.COPPER_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(Registration.SILVER_FURNACE.get()), RecipeTypes.FUELING);


			registry.addRecipeCatalyst(new ItemStack(Registration.MILLION_FURNACE.get()), RecipeTypes.FUELING);


			registry.addRecipeCatalyst(new ItemStack(Registration.BLASTING_AUGMENT.get()), Registration.RecipeTypes.GENERATOR_BLASTING);
			registry.addRecipeCatalyst(new ItemStack(Registration.SMOKING_AUGMENT.get()), Registration.RecipeTypes.GENERATOR_SMOKING);



			if (ModList.get().isLoaded("allthemodium"))
			{
				registry.addRecipeCatalyst(new ItemStack(Registration.ALLTHEMODIUM_FURNACE.get()), RecipeTypes.SMELTING);
				registry.addRecipeCatalyst(new ItemStack(Registration.VIBRANIUM_FURNACE.get()), RecipeTypes.SMELTING);
				registry.addRecipeCatalyst(new ItemStack(Registration.UNOBTAINIUM_FURNACE.get()), RecipeTypes.SMELTING);
				registry.addRecipeCatalyst(new ItemStack(Registration.ALLTHEMODIUM_FURNACE.get()), RecipeTypes.FUELING);
				registry.addRecipeCatalyst(new ItemStack(Registration.VIBRANIUM_FURNACE.get()), RecipeTypes.FUELING);
				registry.addRecipeCatalyst(new ItemStack(Registration.UNOBTAINIUM_FURNACE.get()), RecipeTypes.FUELING);
			}
	}
}

/**@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		if (Config.enableJeiPlugin.get() && Config.enableJeiClickArea.get()) {
			registry.addRecipeClickArea(BlockIronFurnaceScreen.class, 79, 35, 24, 17, RecipeTypes.FUELING, VanillaRecipeCategoryUid.FURNACE);
			registry.addRecipeClickArea(BlockGoldFurnaceScreen.class, 79, 35, 24, 17, VanillaRecipeCategoryUid.FUEL, VanillaRecipeCategoryUid.FURNACE);
			registry.addRecipeClickArea(BlockDiamondFurnaceScreen.class, 79, 35, 24, 17, VanillaRecipeCategoryUid.FUEL, VanillaRecipeCategoryUid.FURNACE);
			registry.addRecipeClickArea(BlockEmeraldFurnaceScreen.class, 79, 35, 24, 17, VanillaRecipeCategoryUid.FUEL, VanillaRecipeCategoryUid.FURNACE);
			registry.addRecipeClickArea(BlockCrystalFurnaceScreen.class, 79, 35, 24, 17, VanillaRecipeCategoryUid.FUEL, VanillaRecipeCategoryUid.FURNACE);
			registry.addRecipeClickArea(BlockObsidianFurnaceScreen.class, 79, 35, 24, 17, VanillaRecipeCategoryUid.FUEL, VanillaRecipeCategoryUid.FURNACE);
			registry.addRecipeClickArea(BlockNetheriteFurnaceScreen.class, 79, 35, 24, 17, VanillaRecipeCategoryUid.FUEL, VanillaRecipeCategoryUid.FURNACE);
			registry.addRecipeClickArea(BlockCopperFurnaceScreen.class, 79, 35, 24, 17, VanillaRecipeCategoryUid.FUEL, VanillaRecipeCategoryUid.FURNACE);
			registry.addRecipeClickArea(BlockSilverFurnaceScreen.class, 79, 35, 24, 17, VanillaRecipeCategoryUid.FUEL, VanillaRecipeCategoryUid.FURNACE);
			registry.addRecipeClickArea(BlockMillionFurnaceScreen.class, 79, 35, 24, 17, VanillaRecipeCategoryUid.FUEL, VanillaRecipeCategoryUid.FURNACE);

			if (ModList.get().isLoaded("allthemodium"))
			{
				registry.addRecipeClickArea(BlockAllthemodiumFurnaceScreen.class, 79, 35, 24, 17, VanillaRecipeCategoryUid.FUEL, VanillaRecipeCategoryUid.FURNACE);
				registry.addRecipeClickArea(BlockVibraniumFurnaceScreen.class, 79, 35, 24, 17, VanillaRecipeCategoryUid.FUEL, VanillaRecipeCategoryUid.FURNACE);
				registry.addRecipeClickArea(BlockUnobtainiumFurnaceScreen.class, 79, 35, 24, 17, VanillaRecipeCategoryUid.FUEL, VanillaRecipeCategoryUid.FURNACE);
			}
		}
	}**/





