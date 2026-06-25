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

package ironfurnaces.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.init.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class GeneratorRecipe implements Recipe<SingleRecipeInput> {

    public int energy;
    public Ingredient ingredient;

    public GeneratorRecipe(int energy, Ingredient ingredient) {
        this.energy = energy;
        this.ingredient = ingredient;
    }

    private static final MapCodec<GeneratorRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            h -> h.group(
                            Codec.INT.optionalFieldOf("energy", 0).forGetter(j -> j.energy),
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient)
                    )
                    .apply(h, GeneratorRecipe::new)
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, GeneratorRecipe> STREAM_CODEC = StreamCodec.of(
            (buf, recipe) -> {
                buf.writeVarInt(recipe.energy);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.ingredient);
            },
            buf -> {
                int energy = buf.readVarInt();
                Ingredient ing = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                return new GeneratorRecipe(energy, ing);
            }
    );

    public static final RecipeSerializer<GeneratorRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public int getEnergy() {
        return energy;
    }

    @Override
    public boolean matches(SingleRecipeInput inv, Level level) {
        return this.ingredient.test(inv.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<GeneratorRecipe> getSerializer() {
        return ironfurnaces.init.Registration.GENERATOR_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<GeneratorRecipe> getType() {
        return ironfurnaces.init.Registration.GENERATOR_RECIPE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(this.ingredient));
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
    }
}
