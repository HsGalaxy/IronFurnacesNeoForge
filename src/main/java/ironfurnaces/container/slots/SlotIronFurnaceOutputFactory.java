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

package ironfurnaces.container.slots;

import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.core.jmx.Server;

public class SlotIronFurnaceOutputFactory extends Slot {

    private final Player player;
    private int removeCount;
    private BlockIronFurnaceTileBase te;
    public int index;

    public SlotIronFurnaceOutputFactory(int index, Player player, BlockIronFurnaceTileBase te, int slotIndex, int xPosition, int yPosition) {
        super(te, slotIndex, xPosition, yPosition);
        this.player = player;
        this.te = te;
        this.index = index;
    }

    @Override
    public boolean isActive() {
        if (index == 0 || index == 5)
        {
            if (te.getTier() > 1)
            {
                return te.isFactory() && !te.getAugmentGUI();
            }
        }
        else if (index == 1 || index == 4)
        {
            if (te.getTier() > 0)
            {
                return te.isFactory() && !te.getAugmentGUI();
            }
        }
        else
        {
            return te.isFactory() && !te.getAugmentGUI();
        }
        return false;
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    @Override
    public boolean mayPlace(ItemStack p_40231_) {
        return false;
    }

    public void onTake(Player thePlayer, ItemStack stack) {
        this.checkTakeAchievements(stack);
        super.onTake(thePlayer, stack);
    }

    public ItemStack remove(int p_39548_) {
        if (this.hasItem()) {
            this.removeCount += Math.min(p_39548_, this.getItem().getCount());
        }

        return super.remove(p_39548_);
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int p_75210_2_) {
        stack.onCraftedBy(this.player, this.removeCount);
        if (!this.player.level().isClientSide() && this.te instanceof BlockIronFurnaceTileBase) {
            ((BlockIronFurnaceTileBase)this.te).unlockRecipes((ServerPlayer) this.player);
        }

        this.removeCount = 0;
    }

    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(this.player, this.removeCount);
        if (this.player instanceof ServerPlayer && this.container instanceof BlockIronFurnaceTileBase) {
            ((BlockIronFurnaceTileBase)this.container).unlockRecipes((ServerPlayer)this.player);
        }

        this.removeCount = 0;
        net.neoforged.neoforge.event.EventHooks.firePlayerSmeltedEvent(this.player, stack, this.removeCount);
    }

}