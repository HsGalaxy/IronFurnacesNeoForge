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

import ironfurnaces.IronFurnaces;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

public class ItemXmas extends Item {


    public ItemXmas(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay tooltipDisplay, Consumer<Component> components, TooltipFlag pTooltipFlag) {
        components.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".xmas_right_click").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
        components.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".xmas1").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
        components.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".xmas2").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
    }

}