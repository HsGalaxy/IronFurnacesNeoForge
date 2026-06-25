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

package ironfurnaces.util;


import ironfurnaces.tileentity.furnaces.BlockMillionFurnaceTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ExplosionKnockbackEvent;

import java.util.List;

public class EventHandler {


    @SubscribeEvent
    public static void explosionEvent(ExplosionKnockbackEvent event)
    {

        List<BlockPos> list = event.getAffectedBlocks();
        for (BlockPos pos : list)
        {
            Level world = event.getLevel();
            if (world.getBlockEntity(pos) instanceof BlockMillionFurnaceTile)
            {
                event.getAffectedBlocks().remove(pos);
                world.removeBlockEntity(pos);
                world.removeBlock(pos, false);

                world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY() + 6f, pos.getZ(), new ItemStack(ironfurnaces.init.Registration.RAINBOW_COAL.get())));
            }
        }
    }

}