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

package ironfurnaces.items;

import ironfurnaces.IronFurnaces;
import ironfurnaces.gui.furnaces.BlockIronFurnaceScreenBase;
import ironfurnaces.util.StringHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class ItemHeater extends Item {


    public ItemHeater(Properties properties) {
        super(properties);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext pContext, List<Component> tooltip, TooltipFlag pTooltipFlag) {
        if (BlockIronFurnaceScreenBase.isShiftKeyDown())
        {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data != null)
            {
                CompoundTag tag = data.copyTag();
                int x = tag.getInt("HeaterPosX");
                int y = tag.getInt("HeaterPosY");
                int z = tag.getInt("HeaterPosZ");

                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heaterX").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + x).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heaterY").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + y).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heaterZ").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + z).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));

            } else {
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_not_bound").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_tip").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_tip1").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
            }
        }
        else
        {
            tooltip.add(StringHelper.getShiftInfoText());
        }
    }

}
