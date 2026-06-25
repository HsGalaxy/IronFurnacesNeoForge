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

package ironfurnaces.container;

import ironfurnaces.container.slots.SlotHeater;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;



public class BlockWirelessEnergyHeaterContainerBase extends AbstractContainerMenu {


    protected BlockWirelessEnergyHeaterTile te;
    protected Player playerEntity;
    protected Inventory playerInventory;
    protected final Level world;


    public BlockWirelessEnergyHeaterContainerBase(MenuType<?> menuType, int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(menuType, windowId);
        this.te = (BlockWirelessEnergyHeaterTile) world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = playerInventory;
        this.world = playerInventory.player.level();
        trackPower();
        this.addSlot(new SlotHeater(te, 0, 80, 37));
        this.addStandardInventorySlots(this.playerInventory, 8, 84);

    }

    public int getEnergy() {
        return te.getEnergy();
    }

    public int getMaxEnergy()
    {
        return te.getCapacity();
    }


    private void trackPower() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getMaxEnergy();
            }

            @Override
            public void set(int value) {
                te.setMaxEnergy(value);
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy();
            }

            @Override
            public void set(int value) {
                te.setEnergy(value);
            }
        });
    }

    public int getEnergyScaled(int pixels) {
        int i = this.getEnergy();
        int j = this.getMaxEnergy();
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

    protected void addInventoryHotbarSlots(Container inventory, int left, int top) {
        for(int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(inventory, x, left + x * 18, top));
        }

    }

    protected void addInventoryExtendedSlots(Container inventory, int left, int top) {
        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(inventory, x + (y + 1) * 9, left + x * 18, top + y * 18));
            }
        }

    }

    protected void addStandardInventorySlots(Container container, int left, int top) {
        this.addInventoryExtendedSlots(container, left, top);
        int hotbarSeparator = 4;
        int topToHotbar = 58;
        this.addInventoryHotbarSlots(container, left, top + 58);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (!(itemstack.getItem() instanceof ItemHeater))
            {
                return ItemStack.EMPTY;
            }
            if (index < 1) {
                if (!this.moveItemStackTo(itemstack1, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return this.te.stillValid(p_38874_);
    }

}