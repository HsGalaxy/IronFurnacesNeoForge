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

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerFurnacesListProvider implements INBTSerializable<CompoundTag> {

    public PlayerFurnacesList furnacesList = new PlayerFurnacesList();

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        CompoundTag furnaces = new CompoundTag();
        for (int i = 0; i < furnacesList.listFurances.size(); i++)
        {
            CompoundTag blockpos = new CompoundTag();
            blockpos.putInt("X", furnacesList.listFurances.get(i).getX());
            blockpos.putInt("Y", furnacesList.listFurances.get(i).getY());
            blockpos.putInt("Z", furnacesList.listFurances.get(i).getZ());
            furnaces.put("furnace" + i, blockpos);
        }


        tag.put("furnaces", furnaces);
        tag.putInt("count", furnacesList.listFurances.size());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        int size = tag.getInt("count");
        CompoundTag furances = tag.getCompound("furnaces");
        for (int i = 0; i < size; i++)
        {
            CompoundTag furance = furances.getCompound("furnace" + i);
            BlockPos pos = new BlockPos(furance.getInt("X"), furance.getInt("Y"), furance.getInt("Z"));
            furnacesList.listFurances.add(pos);
        }
    }
}
