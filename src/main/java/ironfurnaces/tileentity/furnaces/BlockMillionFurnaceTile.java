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

package ironfurnaces.tileentity.furnaces;

import ironfurnaces.Config;
import ironfurnaces.container.furnaces.BlockMillionFurnaceContainer;
import ironfurnaces.init.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class BlockMillionFurnaceTile extends BlockIronFurnaceTileBase {
    public BlockMillionFurnaceTile(BlockPos pos, BlockState state) {
        super(Registration.MILLION_FURNACE_TILE.get(), pos, state);
    }

    public List<BlockIronFurnaceTileBase> furnaces = new ArrayList<>();
    public List<BlockPos> furnaces_to_load = new ArrayList<>();

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        CompoundTag furnaces = new CompoundTag();
        for (int i = 0; i < this.furnaces.size(); i++)
        {
            CompoundTag tag2 = new CompoundTag();
            tag2.putInt("X", this.furnaces.get(i).getBlockPos().getX());
            tag2.putInt("Y", this.furnaces.get(i).getBlockPos().getY());
            tag2.putInt("Z", this.furnaces.get(i).getBlockPos().getZ());
            furnaces.put("Furnace" + i, tag2);
        }
        tag.put("Furnaces", furnaces);

    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        CompoundTag furnaces = tag.getCompound("Furnaces");
        for (int i = 0; i < furnaces.size(); i++)
        {
            CompoundTag furnace = furnaces.getCompound("Furnace" + i);
            furnaces_to_load.add(new BlockPos(furnace.getInt("X"), furnace.getInt("Y"), furnace.getInt("Z")));
        }
    }

    @Override
    public ModConfigSpec.IntValue getCookTimeConfig() {
        return Config.millionFurnaceSpeed;
    }

    @Override
    public String IgetName() {
        return "container.ironfurnaces.million_furnace";
    }

    @Override
    public AbstractContainerMenu IcreateMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new BlockMillionFurnaceContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public int getTier() {
        return Config.millionFurnaceTier.get();
    }


}
