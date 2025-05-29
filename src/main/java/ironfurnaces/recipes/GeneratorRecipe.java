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
import net.minecraft.world.level.Level;

import java.util.Arrays;

public class GeneratorRecipe implements Recipe<SingleRecipeInput> {


    public int energy;
    public Ingredient ingredient;

    public GeneratorRecipe(int energy, Ingredient ingredient)
    {
        this.energy = energy;
        this.ingredient = ingredient;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    @Override
    public boolean isIncomplete() {
        return Arrays.stream(ingredient.getItems()).toList().getFirst().isEmpty();
    }


    public int getEnergy()
    {
        return energy;
    }


    @Override
    public boolean matches(SingleRecipeInput inv, Level p_44003_) {
        return this.ingredient.test(inv.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput p_343633_, HolderLookup.Provider p_332698_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_331967_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.GENERATOR_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Registration.GENERATOR_RECIPE_TYPE.get();
    }

    public static class Serializer<T extends GeneratorRecipe> implements RecipeSerializer<GeneratorRecipe> {

        private static final MapCodec<GeneratorRecipe> codec= RecordCodecBuilder.mapCodec(
                h -> h.group(
                                Codec.INT.optionalFieldOf("energy", 0).forGetter(j -> j.energy),
                                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                                    return recipe.ingredient;
                                })
                        )
                        .apply(h, GeneratorRecipe::new)
        );
        private static final StreamCodec<RegistryFriendlyByteBuf, GeneratorRecipe> streamCodec = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        public Serializer() {

        }




        private static GeneratorRecipe fromNetwork(RegistryFriendlyByteBuf buf) {

            int energy = buf.readVarInt();
            Ingredient ingredient = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);

            return new GeneratorRecipe(energy, ingredient);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, GeneratorRecipe recipe) {

            buf.writeVarInt(recipe.energy);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.ingredient);

        }

        @Override
        public MapCodec<GeneratorRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GeneratorRecipe> streamCodec() {
            return streamCodec;
        }



    }
}
