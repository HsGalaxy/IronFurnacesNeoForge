/*
 * Copyright 2025 Astryxion
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

package ironfurnaces.blocks.furnaces;

import ironfurnaces.IronFurnaces;
import ironfurnaces.gui.furnaces.BlockIronFurnaceScreenBase;
import ironfurnaces.init.Registration;
import ironfurnaces.util.StringHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import java.util.function.Consumer;

public class BlockItemHeater extends BlockItem {


    public BlockItemHeater(Block block, Properties properties) {
        super(block, properties);
    }


    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> components, TooltipFlag tooltipFlag) {

        Integer energy = stack.get(ironfurnaces.init.Registration.ENERGY);
        if (energy != null) {
            components.accept(Component.literal(StringHelper.displayEnergy(energy, 1000000).get(0)).withStyle(ChatFormatting.GOLD));
        }
        if (BlockIronFurnaceScreenBase.isShiftKeyDown()) {
            components.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_block").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
            components.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_block1").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
        } else {
            components.accept(StringHelper.getShiftInfoText());
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.get(ironfurnaces.init.Registration.ENERGY) != null;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (stack.get(ironfurnaces.init.Registration.ENERGY) != null)
        {
            int energy = stack.get(ironfurnaces.init.Registration.ENERGY);
            return (int) ((int)13 * ((double) energy / (double) 1000000));
        }
        return 0;
    }

    @Override
    public int getBarColor(ItemStack p_150901_) {
        return 0xFF800600;
    }
}