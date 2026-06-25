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

package ironfurnaces.tileentity;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

public abstract class TileEntityInventory extends BlockEntity implements ITileInventory, WorldlyContainer, MenuProvider, Nameable {

    public NonNullList<ItemStack> inventory;
    protected Component name;

    public TileEntityInventory(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int sizeInventory) {
        super(tileEntityTypeIn, pos, state);
        inventory = NonNullList.withSize(sizeInventory, ItemStack.EMPTY);
    }



    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        ProblemReporter.Collector reporter = new ProblemReporter.Collector();
        TagValueOutput output = TagValueOutput.createWithContext(reporter, provider);
        saveAdditional(output);
        return output.buildResult();
    }

    @Override
    public void onDataPacket(Connection connection, ValueInput valueInput) {
        loadAdditional(valueInput);
        setChanged();
        if (level != null) {
            level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), level.getBlockState(worldPosition).getBlock().defaultBlockState(), level.getBlockState(worldPosition), 2, 3);
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        setChanged();
        return ClientboundBlockEntityDataPacket.create(this);
    }


    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.inventory);
        HolderLookup.Provider provider = this.level != null ? this.level.registryAccess() : RegistryAccess.EMPTY;
        input.read("CustomName", ComponentSerialization.CODEC).ifPresent(n -> this.name = n);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.name != null) {
            HolderLookup.Provider provider = this.level != null ? this.level.registryAccess() : RegistryAccess.EMPTY;
            output.store("CustomName", ComponentSerialization.CODEC, this.name);
        }
        ContainerHelper.saveAllItems(output, this.inventory);
    }




    public void setCustomName(Component name) {
        this.name = name;
    }

    @Override
    public Component getName() {
        return (this.name != null ? this.name : Component.translatable(IgetName()));
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return this.IgetSlotsForFace(side);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return IisItemValidForSlot(i, itemStack);
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return IisItemValidForSlot(i, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return IcanExtractItem(i, itemStack, direction);
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int i, int i1) {
        return ContainerHelper.removeItem(this.inventory, i, i1);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(this.inventory, i);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.inventory.set(index, stack);
        stack.limitSize(this.getMaxStackSize(stack));
    }




    @Override
    public boolean stillValid(Player playerEntity) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(playerEntity.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public boolean hasCustomName() {
        return this.name != null;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        assert level != null;
        return IcreateMenu(i, inventory, player);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();

    }
}
