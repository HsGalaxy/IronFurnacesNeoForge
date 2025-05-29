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

package ironfurnaces.container.slots;

import ironfurnaces.items.ItemHeater;
import ironfurnaces.items.augments.ItemAugmentBlasting;
import ironfurnaces.items.augments.ItemAugmentSmoking;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class SlotIronFurnaceInputGenerator extends Slot {

    BlockIronFurnaceTileBase te;
    public SlotIronFurnaceInputGenerator(BlockIronFurnaceTileBase te, int index, int x, int y) {
        super(te, index, x, y);
        this.te = te;
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    @Override
    public boolean mayPlace(ItemStack stack) {
        if (!te.getItem(3).isEmpty())
        {
            if (te.getItem(3).getItem() instanceof ItemAugmentBlasting)
            {
                return te.hasGeneratorBlastingRecipe(stack);
            }
            if (te.getItem(3).getItem() instanceof ItemAugmentSmoking)
            {
                if (stack.hasCraftingRemainingItem())
                {
                    return te.getItem(6).isEmpty() && te.getSmokingBurn(stack) > 0;
                }
                return te.getSmokingBurn(stack) > 0;
            }
        }
        if (stack.getItem() instanceof ItemHeater)
        {
            return false;
        }

        return BlockIronFurnaceTileBase.isItemFuel(stack, RecipeType.SMELTING);
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int increment) {
        if (stack.hasCraftingRemainingItem())
        {
            if (stack.getCount() > 1)
            {
                if (this.getItem().isEmpty())
                {
                    super.safeInsert(stack.copyWithCount(1), increment);
                    return stack.copyWithCount(stack.getCount() - 1);
                }

            }
        }
        return super.safeInsert(stack, increment);
    }

    @Override
    public boolean isActive() {
        return te.isGenerator() && te.getAugmentGUI() == 0;
    }
}
