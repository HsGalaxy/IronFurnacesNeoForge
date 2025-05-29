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

package ironfurnaces.tileentity.furnaces.other;

import ironfurnaces.Config;
import ironfurnaces.container.furnaces.other.BlockAllthemodiumFurnaceContainer;
import ironfurnaces.init.Registration;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ModConfigSpec;

public class BlockAllthemodiumFurnaceTile extends BlockIronFurnaceTileBase {
    public BlockAllthemodiumFurnaceTile(BlockPos pos, BlockState state) {
        super(Registration.ALLTHEMODIUM_FURNACE_TILE.get(), pos, state);
    }

    @Override
    public ModConfigSpec.IntValue getCookTimeConfig() {
        return Config.allthemodiumFurnaceSpeed;
    }



    @Override
    public String IgetName() {
        return "container.ironfurnaces.allthemodium_furnace";
    }


    @Override
    public AbstractContainerMenu IcreateMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new BlockAllthemodiumFurnaceContainer(i, level, worldPosition, playerInventory, playerEntity);
    }


    @Override
    public int getTier() {
        return Config.allthemodiumFurnaceTier.get();
    }

}
