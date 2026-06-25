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

package ironfurnaces.util.gui;

import ironfurnaces.IronFurnaces;
import ironfurnaces.util.StringHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class FurnaceGuiEnergy {

    public static final Identifier GUI_ENERGY = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/energy.png");

    private int left;
    private int top;
    public int x;
    public int y;
    public int width;
    public int height;
    public int u;
    public int v;

    public FurnaceGuiEnergy(int left, int top, int x, int y, int width, int height, int u, int v) {
        this.left = left;
        this.top = top;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
    }

    public void changePos(int newX, int newY, boolean condition) {
        if (condition) {
            this.x = newX;
            this.y = newY;
        }
    }

    public void changeUV(int newU, int newV, boolean condition) {
        if (condition) {
            this.u = newU;
            this.v = newV;
        }
    }


    public void render(GuiGraphicsExtractor matrix, int scaled) {
        matrix.blit(RenderPipelines.GUI_TEXTURED, GUI_ENERGY, left + x, top + y + 42 - scaled, u, v + height - scaled, width, scaled, 256, 256);
    }

    public boolean hovering(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void renderTooltip(Font font, GuiGraphicsExtractor graphics, int tooltipX, int tooltipY, int guiMouseX, int guiMouseY, int energy, int capacity, boolean condition) {
        if (condition)
            if (hovering(guiMouseX, guiMouseY))
                graphics.setTooltipForNextFrame(font, Component.literal(StringHelper.displayEnergy(energy, capacity).get(0)), tooltipX, tooltipY);
    }


}