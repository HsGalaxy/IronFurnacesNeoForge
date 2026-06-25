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

package ironfurnaces.items;

import com.google.common.collect.Lists;
import ironfurnaces.Config;
import ironfurnaces.IronFurnaces;
import ironfurnaces.gui.furnaces.BlockIronFurnaceScreenBase;
import ironfurnaces.util.StringHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class ItemMillionFurnace extends BlockItem {
    public ItemMillionFurnace(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    private Random rand = new Random();
    private int timer = 0;



    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext pContext, TooltipDisplay tooltipDisplay, Consumer<Component> components, TooltipFlag pTooltipFlag) {
        components.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".cooktime").append(Component.literal(" (" + ItemFurnace.getCooktime(stack) + ")").withStyle(ChatFormatting.BLUE)));

        timer++;
        if (timer % 20 == 0) {
            timer = 0;
            String name = Component.translatable("block.ironfurnaces.million_furnace").getString();
            ArrayList<Component> names = Lists.newArrayList();
            for (int i = 0; i < name.length(); i++) {
                names.add((Component) Component.literal("" + name.charAt(i)).withStyle(ChatFormatting.getById(getIDRandom(rand.nextInt(6)))));
            }
            MutableComponent component = Component.literal("");
            for (int i = 0; i < names.size(); i++) {
                component.append(names.get(i));
            }
            stack.set(DataComponents.CUSTOM_NAME, component);

        }

        Format decimal = new DecimalFormat();
        String part1 = Component.translatable("tooltip.ironfurnaces.rainbow_gen1").getString();
        String part2 = Component.translatable("tooltip.ironfurnaces.rainbow_gen2").getString();
        components.accept(Component.literal(part1 + " " + decimal.format(Config.millionFurnacePowerToGenerate.get()).toString().replaceAll("\u00A0", ",") + " " + part2).withStyle(ChatFormatting.GRAY));
        components.accept(Component.translatable("tooltip.ironfurnaces.rainbow_blowup").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));



        if (BlockIronFurnaceScreenBase.isShiftKeyDown())
        {
            components.accept(Component.translatable("tooltip.ironfurnaces.rainbow_gen3").withStyle(ChatFormatting.GRAY));
            components.accept(Component.translatable("tooltip.ironfurnaces.rainbow_gen4").withStyle(ChatFormatting.GRAY));
            components.accept(Component.translatable("tooltip.ironfurnaces.rainbow_gen5").withStyle(ChatFormatting.GRAY));
        }
        else
        {
            components.accept(StringHelper.getShiftInfoText());
        }
    }


    public static int getIDRandom(int id)
    {
        switch (id)
        {
            case 0:
                return 12;
            case 1:
                return 14;
            case 2:
                return 10;
            case 3:
                return 11;
            case 4:
                return 9;
            case 5:
                return 13;
            case 6:
                return 5;
            default:
                return 0;
        }
    }

}