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

package ironfurnaces.tileentity.furnaces;

import ironfurnaces.Config;
import ironfurnaces.container.furnaces.BlockMillionFurnaceContainer;
import ironfurnaces.init.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class BlockMillionFurnaceTile extends BlockIronFurnaceTileBase {
    public BlockMillionFurnaceTile(BlockPos pos, BlockState state) {
        super(ironfurnaces.init.Registration.MILLION_FURNACE_TILE.get(), pos, state);
    }

    public List<BlockIronFurnaceTileBase> furnaces = new ArrayList<>();
    public List<BlockPos> furnaces_to_load = new ArrayList<>();

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag furnaces = new CompoundTag();
        for (int i = 0; i < this.furnaces.size(); i++) {
            CompoundTag tag2 = new CompoundTag();
            tag2.putInt("X", this.furnaces.get(i).getBlockPos().getX());
            tag2.putInt("Y", this.furnaces.get(i).getBlockPos().getY());
            tag2.putInt("Z", this.furnaces.get(i).getBlockPos().getZ());
            furnaces.put("Furnace" + i, tag2);
        }
        output.store("Furnaces", CompoundTag.CODEC, furnaces);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        furnaces_to_load.clear();
        input.read("Furnaces", CompoundTag.CODEC).ifPresent(furnaces -> {
            for (int i = 0; ; i++) {
                String key = "Furnace" + i;
                var furnaceOpt = furnaces.getCompound(key);
                if (furnaceOpt.isEmpty()) {
                    break;
                }
                furnaceOpt.ifPresent(furnace -> furnaces_to_load.add(new BlockPos(
                        furnace.getInt("X").orElse(0),
                        furnace.getInt("Y").orElse(0),
                        furnace.getInt("Z").orElse(0))));
            }
        });
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