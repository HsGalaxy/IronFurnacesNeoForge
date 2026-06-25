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

package ironfurnaces.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class PlayerFurnacesListProvider implements ValueIOSerializable {

    public PlayerFurnacesList furnacesList = new PlayerFurnacesList();

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("count", furnacesList.listFurances.size());
        ValueOutput furnaces = output.child("furnaces");
        for (int i = 0; i < furnacesList.listFurances.size(); i++) {
            ValueOutput blockpos = furnaces.child("furnace" + i);
            blockpos.putInt("X", furnacesList.listFurances.get(i).getX());
            blockpos.putInt("Y", furnacesList.listFurances.get(i).getY());
            blockpos.putInt("Z", furnacesList.listFurances.get(i).getZ());
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        furnacesList.listFurances.clear();
        int size = input.getIntOr("count", 0);
        input.child("furnaces").ifPresent(furnaces -> {
            for (int i = 0; i < size; i++) {
                int idx = i;
                furnaces.child("furnace" + idx).ifPresent(furnace -> {
                    BlockPos pos = new BlockPos(furnace.getIntOr("X", 0), furnace.getIntOr("Y", 0), furnace.getIntOr("Z", 0));
                    furnacesList.listFurances.add(pos);
                });
            }
        });
    }
}