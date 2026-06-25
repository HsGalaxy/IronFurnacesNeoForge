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

import com.mojang.blaze3d.platform.InputConstants;
import ironfurnaces.network.Messages;
import ironfurnaces.network.PacketFurnaceSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class FurnaceGuiButton {
    public int left;
    public int top;
    public int x;
    public int y;
    public int width;
    public int height;
    public int u;
    public int v;
    public int u_hover;
    public int v_hover;
    public int u_enabled;
    public int v_enabled;

    public FurnaceGuiButton(int left, int top, int x, int y, int width, int height, int u, int v, int u_hover, int v_hover, int u_enabled, int v_enabled) {
        this.left = left;
        this.top = top;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
        this.u_hover = u_hover;
        this.v_hover = v_hover;
        this.u_enabled = u_enabled;
        this.v_enabled = v_enabled;
    }

    public FurnaceGuiButton(int left, int top, int x, int y, int width, int height) {
        this(left, top, x, y, width, height, -1, -1, -1, -1, -1, -1);
    }

    public FurnaceGuiButton(int left, int top, int x, int y, int width, int height, int u_hover, int v_hover) {
        this(left, top, x, y, width, height, -1, -1, u_hover, v_hover, u_hover, v_hover);
    }

    public void changeEnabledUV(int u, int v) {
        this.u_enabled = u;
        this.v_enabled = v;
    }

    public void onClick(double mouseX, double mouseY, int button, boolean condition, Runnable action) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            if (condition) {
                if (hovering(mouseX, mouseY)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 0.6F, 0.3F));
                    action.run();
                }
            }
        }
    }

    public void onRightClick(double mouseX, double mouseY, int button, boolean condition, Runnable action) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
            if (condition) {
                if (hovering(mouseX, mouseY)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 0.3F, 0.3F));
                    action.run();
                }
            }
        }
    }

    public void render(Identifier location, GuiGraphicsExtractor matrix, int mouseX, int mouseY, boolean enabled) {

        if (!hovering(mouseX, mouseY) && hasUV())
            matrix.blit(RenderPipelines.GUI_TEXTURED, location, left + x, top + y, u, v, width, height, 256, 256);

        if (hovering(mouseX, mouseY) && hasUVHover())
            matrix.blit(RenderPipelines.GUI_TEXTURED, location, left + x, top + y, u_hover, v_hover, width, height, 256, 256);

        if (enabled && hasUVEnabled())
            matrix.blit(RenderPipelines.GUI_TEXTURED, location, left + x, top + y, u_enabled, v_enabled, width, height, 256, 256);


    }

    public boolean hovering(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void renderTooltip(Font font, GuiGraphicsExtractor graphics, Component text, int tooltipX, int tooltipY, int guiMouseX, int guiMouseY, boolean condition) {
        if (condition)
            if (hovering(guiMouseX, guiMouseY))
                graphics.setTooltipForNextFrame(font, text, tooltipX, tooltipY);
    }

    public void renderComponentTooltip(Font font, GuiGraphicsExtractor graphics, List<Component> text, int tooltipX, int tooltipY, int guiMouseX, int guiMouseY, boolean condition) {
        if (condition)
            if (hovering(guiMouseX, guiMouseY))
                graphics.setComponentTooltipForNextFrame(font, text, tooltipX, tooltipY);
    }

    public boolean hasUV() {
        return u >= 0 && v >= 0;
    }

    public boolean hasUVHover() {
        return u_hover >= 0 && v_hover >= 0;
    }

    public boolean hasUVEnabled() {
        return u_enabled >= 0 && v_enabled >= 0;
    }

    public static boolean isShiftKeyDown() {
        return isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isKeyDown(int glfw) {
        InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(glfw);
        int keyCode = key.getValue();
        if (keyCode != InputConstants.UNKNOWN.getValue()) {
            var window = Minecraft.getInstance().getWindow();
            try {
                if (key.getType() == InputConstants.Type.KEYSYM) {
                    return InputConstants.isKeyDown(window, keyCode);
                } /**else if (key.getType() == InputMappings.Type.MOUSE) {
                 return GLFW.glfwGetMouseButton(windowHandle, keyCode) == GLFW.GLFW_PRESS;
                 }**/
            } catch (Exception ignored) {
            }
        }
        return false;
    }


}