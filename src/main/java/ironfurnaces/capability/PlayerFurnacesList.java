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

package ironfurnaces.capability;

import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.tileentity.furnaces.BlockSilverFurnaceTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class PlayerFurnacesList implements IPlayerFurnacesList {

    public List<BlockPos> listFurances;

    public PlayerFurnacesList()
    {
        listFurances = new ArrayList<>();
    }


    @Override
    public List<BlockPos> get() {
        return listFurances;
    }

    @Override
    public void add(BlockPos pos) {
        int check = 0;
        for (int i = 0; i < listFurances.size(); i++)
        {
            if (listFurances.get(i).getX() == pos.getX() && listFurances.get(i).getY() == pos.getY() && listFurances.get(i).getZ() == pos.getZ())
            {
                check++;
            }
        }
        if (check == 0)
        {
            listFurances.add(pos);
        }
    }

    @Override
    public void remove(BlockPos pos) {
        for (int i = 0; i < listFurances.size(); i++)
        {
            if (listFurances.get(i).getX() == pos.getX() && listFurances.get(i).getY() == pos.getY() && listFurances.get(i).getZ() == pos.getZ())
            {
                listFurances.remove(i);
            }
        }
    }
}
