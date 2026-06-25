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

import ironfurnaces.init.Registration;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.util.DirectionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import java.util.function.Consumer;

public class ItemFurnaceCopy extends Item {


    public ItemFurnaceCopy(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext pContext, TooltipDisplay tooltipDisplay, Consumer<Component> components, TooltipFlag pTooltipFlag) {
        CustomData customData = stack.get(ironfurnaces.init.Registration.FURNACE_SETTINGS.get());
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (!tag.isEmpty()) {
                tag.getIntArray("settings").ifPresent(settings -> {
                    if (settings.length >= 10) {
                        components.accept(Component.literal("Down: " + settings[0]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                        components.accept(Component.literal("Up: " + settings[1]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                        components.accept(Component.literal("North: " + settings[2]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                        components.accept(Component.literal("South: " + settings[3]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                        components.accept(Component.literal("West: " + settings[4]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                        components.accept(Component.literal("East: " + settings[5]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                        components.accept(Component.literal("Auto Input: " + settings[6]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                        components.accept(Component.literal("Auto Output: " + settings[7]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                        components.accept(Component.literal("Redstone Mode: " + settings[8]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                        components.accept(Component.literal("Redstone Value: " + settings[9]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                        tag.getInt("direction").ifPresent(dir -> components.accept(Component.literal("Direction: " + DirectionUtil.fromId(dir)).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
                    }
                });
            }
        }

        components.accept(Component.literal("Right-click to copy settings").withStyle(ChatFormatting.GRAY));
        components.accept(Component.literal("Sneak & right-click to apply settings").withStyle(ChatFormatting.GRAY));
    }


    @Override
    public InteractionResult useOn(UseOnContext ctx) {

        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        if (!ctx.getPlayer().isCrouching())
        {
            return super.useOn(ctx);
        }
        if (!world.isClientSide()) {
            BlockEntity te = world.getBlockEntity(pos);

            if (!(te instanceof BlockIronFurnaceTileBase)) {
                return super.useOn(ctx);
            }

            ItemStack stack = ctx.getItemInHand();
            CustomData customData = stack.get(ironfurnaces.init.Registration.FURNACE_SETTINGS.get());
            if (customData != null) {
                CompoundTag tag = customData.copyTag();
                if (!tag.isEmpty()) {
                    int[] settings = tag.getIntArray("settings").orElse(null);
                    if (settings == null) {
                        return super.useOn(ctx);
                    }
                    for (int i = 0; i < settings.length; i++) {
                        ((BlockIronFurnaceTileBase) te).furnaceSettings.set(i, settings[i]);
                    }
                    Direction dir = DirectionUtil.fromId(tag.getInt("direction").orElse(0));
                    if (dir != Direction.UP && dir != Direction.DOWN)
                    {
                        if (te.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING) != dir) {
                            te.getLevel().setBlock(te.getBlockPos(), te.getBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, dir), 3);
                        }
                    }



                }
            }
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos).getBlock().defaultBlockState(), world.getBlockState(pos), 3, 3);
            ctx.getPlayer().sendSystemMessage(Component.literal("Settings applied"));
        }

        return super.useOn(ctx);
    }
}