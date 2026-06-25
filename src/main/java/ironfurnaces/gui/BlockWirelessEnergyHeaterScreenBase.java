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

package ironfurnaces.gui;

import ironfurnaces.IronFurnaces;
import ironfurnaces.container.BlockWirelessEnergyHeaterContainer;
import ironfurnaces.util.StringHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


public abstract class BlockWirelessEnergyHeaterScreenBase<T extends BlockWirelessEnergyHeaterContainer> extends AbstractContainerScreen<T> {

    public Identifier GUI = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/heater.png");
    Inventory playerInv;
    Component name;

    public BlockWirelessEnergyHeaterScreenBase(T t, Inventory inv, Component name) {
        super(t, inv, name);
        playerInv = inv;
        this.name = name;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {


        graphics.text(font, this.playerInv.getDisplayName(), 7, this.getImageHeight() - 93, 4210752, false);
        graphics.text(font, name, this.getImageWidth() / 2 - this.minecraft.font.width(name.getString()) / 2, 6, 4210752, false);

        int actualMouseX = mouseX - ((this.width - this.getImageWidth()) / 2);
        int actualMouseY = mouseY - ((this.height - this.getImageHeight()) / 2);
        if (actualMouseX >= 68 && actualMouseX <= 108 && actualMouseY >= 64 && actualMouseY <= 76) {
            int energy = ((BlockWirelessEnergyHeaterContainer) this.getMenu()).getEnergy();
            int capacity = ((BlockWirelessEnergyHeaterContainer) this.getMenu()).getMaxEnergy();
            graphics.setTooltipForNextFrame(this.font, Component.literal(StringHelper.displayEnergy(energy, capacity).get(0)), mouseX, mouseY);
        }

    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {

        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        int relX = (this.width - this.getImageWidth()) / 2;
        int relY = (this.height - this.getImageHeight()) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0, 0, this.getImageWidth(), this.getImageHeight(), 256, 256);

        int i;
        i = ((BlockWirelessEnergyHeaterContainer) this.getMenu()).getEnergyScaled(42);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, getLeftPos() + 67, getTopPos() + 63, 176, 0, i + 1, 14, 256, 256);

    }

}