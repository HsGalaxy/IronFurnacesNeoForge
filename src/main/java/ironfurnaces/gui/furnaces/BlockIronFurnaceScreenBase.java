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

package ironfurnaces.gui.furnaces;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import ironfurnaces.IronFurnaces;
import ironfurnaces.capability.ClientShowConfig;
import ironfurnaces.container.furnaces.BlockIronFurnaceContainerBase;
import ironfurnaces.items.ItemMillionFurnace;
import ironfurnaces.network.*;
import ironfurnaces.util.DirectionUtil;
import ironfurnaces.util.FurnaceSettings;
import ironfurnaces.util.StringHelper;
import ironfurnaces.util.gui.FurnaceGuiButton;
import ironfurnaces.util.gui.FurnaceGuiEnergy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public abstract class BlockIronFurnaceScreenBase<T extends BlockIronFurnaceContainerBase> extends AbstractContainerScreen<T> {

    public Identifier GUI = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/furnace.png");
    public static final Identifier GUI_NETHERITE = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/furnace_netherite.png");
    public static final Identifier GUI_ATM = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/furnace_allthemodium.png");
    public static final Identifier GUI_VIB = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/furnace_vibranium.png");
    public static final Identifier GUI_UNOB = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/furnace_unobtainium.png");
    public static final Identifier GUI_FACTORY = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/furnace_factory.png");
    public static final Identifier GUI_GENERATOR = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/furnace_generator.png");
    public static final Identifier GUI_GENERATOR_NETHERITE = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/furnace_generator_netherite.png");
    public static final Identifier GUI_GENERATOR_ALLTHEMODIUM = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/furnace_generator_allthemodium.png");
    public static final Identifier GUI_GENERATOR_VIBRANIUM = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/furnace_generator_vibranium.png");
    public static final Identifier GUI_GENERATOR_UNOBTAINIUM = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/furnace_generator_unobtainium.png");
    public static final Identifier GUI_AUGMENTS = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/augment.png");
    public static final Identifier WIDGETS = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "textures/gui/widgets.png");
    Inventory playerInv;
    Component name;

    public List<FurnaceGuiButton> sideButtons = Lists.newArrayList();
    public FurnaceGuiButton autoSplitButton;
    public FurnaceGuiButton augmentButton;
    public FurnaceGuiButton autoInputButton;
    public FurnaceGuiButton autoOutputButton;
    public FurnaceGuiButton topButton;
    public FurnaceGuiButton leftButton;
    public FurnaceGuiButton frontButton;
    public FurnaceGuiButton rightButton;
    public FurnaceGuiButton bottomButton;
    public FurnaceGuiButton backButton;
    public FurnaceGuiButton redstoneIgnoredButton;
    public FurnaceGuiButton redstoneLowButton;
    public FurnaceGuiButton redstoneHighButton;
    public FurnaceGuiEnergy energyBar;

    private int timer;
    private Random rand = new Random();

    public BlockIronFurnaceScreenBase(T t, Inventory inv, Component name) {
        super(t, inv, name);
        playerInv = inv;
        this.name = name;
    }




    @Override
    protected void init() {
        super.init();
        int left = getLeftPos();
        int top = getTopPos();
        energyBar = new FurnaceGuiEnergy(left, top, 109, 22, 14, 42, 0, 0);
        autoSplitButton = new FurnaceGuiButton(left, top, 9, 56, 14, 14,70, 132);
        augmentButton = new FurnaceGuiButton(left, top, 161, 4, 11, 11);
        autoInputButton = new FurnaceGuiButton(left, top, -47, 8, 14, 14, 0, 132);
        autoOutputButton = new FurnaceGuiButton(left, top, -29, 8, 14, 14, 14, 132);
        redstoneIgnoredButton = new FurnaceGuiButton(left, top, -47, 66, 14, 14, 28, 132);
        redstoneLowButton = new FurnaceGuiButton(left, top, -31, 66, 14, 14, 42, 132);
        redstoneHighButton = new FurnaceGuiButton(left, top, -15, 66, 14, 14, 56, 132);
        sideButtons.add(bottomButton = new FurnaceGuiButton(left, top, -32,  51, 10, 10));
        sideButtons.add(topButton = new FurnaceGuiButton(left, top, -32,  27, 10, 10));
        sideButtons.add(frontButton = new FurnaceGuiButton(left, top, -32,  39, 10, 10));
        sideButtons.add(backButton = new FurnaceGuiButton(left, top, -20,  51, 10, 10));
        sideButtons.add(leftButton = new FurnaceGuiButton(left, top, -44,  39, 10, 10));
        sideButtons.add(rightButton = new FurnaceGuiButton(left, top, -20,  39, 10, 10));
    }


    private boolean showInventoryButtons() {
        return getShowConfig() == 1;
    }

    public void setShowConfig(int value)
    {
        ClientShowConfig.set(value);
        Messages.sendToServer(new PacketShowConfig(value));
    }

    public int getShowConfig()
    {
        return ClientShowConfig.getShowConfig();
    }

    @Override
    public void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int actualMouseX = mouseX - ((this.width - this.getImageWidth()) / 2);
        int actualMouseY = mouseY - ((this.height - this.getImageHeight()) / 2);
        if (this.getMenu().isRainbowFurnace())
        {
            timer++;
            if (timer % 20 == 0) {
                timer = 0;
                String name = this.name.getString();
                ArrayList<Component> names = Lists.newArrayList();
                for (int i = 0; i < name.length(); i++) {
                    names.add((Component) Component.literal("" + name.charAt(i)).withStyle(ChatFormatting.getById(ItemMillionFurnace.getIDRandom(rand.nextInt(6)))));
                }
                MutableComponent component = Component.literal("");
                for (int i = 0; i < names.size(); i++) {
                    component.append(names.get(i));
                }
                this.name = component;

            }
        }

        if (this.getMenu().isRainbowFurnace())
        {
            graphics.text(font, name, this.getImageWidth() / 2 - this.minecraft.font.width(name.getString()) / 2, -10, -1, false);
        }
        else
        {
            if (this.getMenu().getIsFactory()) {
                graphics.text(font, name, this.getImageWidth() / 2 - this.minecraft.font.width(name.getString()) / 2, -10, -1, false);
            }
            else {
                graphics.text(font, name, this.getMenu().getIsFurnace() ? 7 + this.getImageWidth() / 2 - this.minecraft.font.width(name.getString()) / 2 : this.getImageWidth() / 2 - this.minecraft.font.width(name.getString()) / 2, 6, -12566464, false);
            }
        }



        graphics.text(font, this.playerInventoryTitle, 8, imageHeight - 93, -12566464, false);



        this.addTooltips(graphics, actualMouseX, actualMouseY);
    }

    private void addTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int tooltipX = getLeftPos() + mouseX;
        int tooltipY = getTopPos() + mouseY;

        augmentButton.renderTooltip(font, graphics, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_open_augments"), tooltipX, tooltipY, mouseX, mouseY, !getMenu().getAugmentGUI());
        augmentButton.renderTooltip(font, graphics, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_open_furnace"), tooltipX, tooltipY, mouseX, mouseY, getMenu().getAugmentGUI());
        energyBar.changePos(109, 22, getMenu().getIsGenerator() && !getMenu().getAugmentGUI());
        energyBar.changePos(9, 7, getMenu().getIsFactory() && !getMenu().getAugmentGUI());
        energyBar.renderTooltip(font, graphics, tooltipX, tooltipY, mouseX, mouseY, getMenu().getEnergy(), getMenu().getMaxEnergy(), getMenu().getIsGenerator() && !getMenu().getAugmentGUI());
        energyBar.renderTooltip(font, graphics, tooltipX, tooltipY, mouseX, mouseY, getMenu().getEnergy(), getMenu().getMaxEnergy(), getMenu().getIsFactory() && !getMenu().getAugmentGUI());
        List<Component> tl = Lists.newArrayList(Component.literal("Auto Split"), Component.literal("ON"));
        autoSplitButton.renderComponentTooltip(font, graphics, tl, tooltipX, tooltipY, mouseX, mouseY, getMenu().isAutoSplit() && getMenu().getIsFactory() && !getMenu().getAugmentGUI());
        tl = Lists.newArrayList(Component.literal("Auto Split"), Component.literal("OFF"));
        autoSplitButton.renderComponentTooltip(font, graphics, tl, tooltipX, tooltipY, mouseX, mouseY, !getMenu().isAutoSplit() && getMenu().getIsFactory() && !getMenu().getAugmentGUI());


        if (!showInventoryButtons()) {
            if (mouseX >= -16 && mouseX <= 0 && mouseY >= 0 && mouseY <= 16) {
                graphics.setTooltipForNextFrame(font, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_open"), tooltipX, tooltipY);
            }
        } else {
            if (mouseX >= -12 && mouseX <= -1 && mouseY >= 6 && mouseY <= 17) {
                graphics.setTooltipForNextFrame(font, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_close"), tooltipX, tooltipY);
            }
            List<Component> list = Lists.newArrayList();
            list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_auto_input"));
            list.add(Component.literal("" + (getMenu().getAutoInput() ? "ON" : "OFF")));
            autoInputButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            list = Lists.newArrayList();
            list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_auto_output"));
            list.add(Component.literal("" + (getMenu().getAutoOutput() ? "ON" : "OFF")));
            autoOutputButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            list = Lists.newArrayList();
            list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_top"));
            list.add(this.getMenu().getTooltip(Direction.UP));
            topButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            list = Lists.newArrayList();
            list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_bottom"));
            list.add(this.getMenu().getTooltip(Direction.DOWN));
            bottomButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            list = Lists.newArrayList();
            if (isShiftKeyDown()) {
                list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_reset"));
            } else {
                list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_front"));
                list.add(this.getMenu().getTooltip(getMenu().getFrontDirection()));
            }
            frontButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            list = Lists.newArrayList();
            list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_back"));
            list.add(this.getMenu().getTooltip(getMenu().getBackDirection()));
            backButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            list = Lists.newArrayList();
            list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_left"));
            list.add(this.getMenu().getTooltip(getMenu().getLeftDirection()));
            leftButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            list = Lists.newArrayList();
            list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_right"));
            list.add(this.getMenu().getTooltip(getMenu().getRightDirection()));
            rightButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            redstoneIgnoredButton.renderTooltip(font, graphics, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_redstone_ignored"), tooltipX, tooltipY, mouseX, mouseY, true);
            redstoneLowButton.renderTooltip(font, graphics, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_redstone_low"), tooltipX, tooltipY, mouseX, mouseY, true);
            redstoneHighButton.renderTooltip(font, graphics, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_redstone_high"), tooltipX, tooltipY, mouseX, mouseY, true);

        }
    }

    private void bg(GuiGraphicsExtractor matrix, int relX, int relY)
    {
        if (!this.getMenu().getAugmentGUI())
        {
            if (this.getMenu().getIsFactory())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, GUI_FACTORY, relX, relY, 0, 0, this.getImageWidth(), this.getImageHeight(), 256, 256);

            }
            if (this.getMenu().getIsGenerator())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, GUI_GENERATOR, relX, relY, 0, 0, this.getImageWidth(), this.getImageHeight(), 256, 256);

            }
            if (!getMenu().getIsGenerator() && !getMenu().getIsFactory())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0, 0, this.getImageWidth(), this.getImageHeight(), 256, 256);
            }

        }
        else
        {
            matrix.blit(RenderPipelines.GUI_TEXTURED, GUI_AUGMENTS, relX, relY, 0, 0, this.getImageWidth(), this.getImageHeight(), 256, 256);

        }
    }

    protected void renderFurnaceBg(GuiGraphicsExtractor matrix)
    {
        if (getMenu().getIsFurnace() && !getMenu().getAugmentGUI())
        {

            int i;
            if (this.getMenu().isBurning()) {
                i = this.getMenu().getBurnLeftScaled(13);
                matrix.blit(RenderPipelines.GUI_TEXTURED, GUI, getLeftPos() + 56, getTopPos() + 36 + 12 - i, 176, 12 - i, 14, i + 1, 256, 256);
            }

            i = this.getMenu().getCookScaled(24);
            matrix.blit(RenderPipelines.GUI_TEXTURED, GUI, getLeftPos() + 79, getTopPos() + 34, 176, 14, i + 1, 16, 256, 256);
        }
    }

    protected void renderGeneratorBg(GuiGraphicsExtractor matrix)
    {
        if (getMenu().getIsGenerator() && !getMenu().getAugmentGUI())
        {
            int i;
            if (this.getMenu().isGeneratorBurning()) {
                i = this.getMenu().getGeneratorBurnScaled(13);
                matrix.blit(RenderPipelines.GUI_TEXTURED, GUI_GENERATOR, getLeftPos() + 56, getTopPos() + 23 + 12 - i, 176, 12 - i, 14, i + 1, 256, 256);
            }
            energyBar.render(matrix, getMenu().getEnergyScaled(42));
        }
    }

    protected void renderFactoryBg(GuiGraphicsExtractor matrix)
    {
        if (getMenu().getIsFactory() && !getMenu().getAugmentGUI())
        {
            addSlots(matrix, this.getMenu().getTier());
            energyBar.changePos(9, 7, true);
            energyBar.render(matrix, getMenu().getEnergyScaled(42));

            int i;
            for (int j = 0; j < getMenu().getFactoryCooktimeSize(); j++)
            {
                i = this.getMenu().getFactoryCookScaled(j, 22);
                matrix.blit(RenderPipelines.GUI_TEXTURED, GUI_FACTORY, getLeftPos() + 29 + (21 * j), getTopPos() + 27, 176, 0, 15, i + 1, 256, 256);
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor matrix, int mouseX, int mouseY, float partialTicks) {

        super.extractBackground(matrix, mouseX, mouseY, partialTicks);

        int relX = (this.width - this.getImageWidth()) / 2;
        int relY = (this.height - this.getImageHeight()) / 2;
        bg(matrix, relX, relY);
        renderFurnaceBg(matrix);
        renderGeneratorBg(matrix);
        renderFactoryBg(matrix);
        int actualMouseX = mouseX - ((this.width - this.getImageWidth()) / 2);
        int actualMouseY = mouseY - ((this.height - this.getImageHeight()) / 2);
        this.addFactoryButtons(matrix, actualMouseX, actualMouseY);
        this.addInventoryButtons(matrix, actualMouseX, actualMouseY);
        this.addRedstoneButtons(matrix, actualMouseX, actualMouseY);
    }

    protected void addSlots(GuiGraphicsExtractor matrix, int amount)
    {
        if (this.getMenu().getIsFactory())
        {
            if (amount > 0)
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, GUI_FACTORY, getLeftPos() + 48, getTopPos() + 5, 176, 64, 18, 67, 256, 256);
                matrix.blit(RenderPipelines.GUI_TEXTURED, GUI_FACTORY, getLeftPos() + 111, getTopPos() + 5, 176, 64, 18, 67, 256, 256);
                if (amount == 2)
                {
                    matrix.blit(RenderPipelines.GUI_TEXTURED, GUI_FACTORY, getLeftPos() + 27, getTopPos() + 5, 176, 64, 18, 67, 256, 256);
                    matrix.blit(RenderPipelines.GUI_TEXTURED, GUI_FACTORY, getLeftPos() + 132, getTopPos() + 5, 176, 64, 18, 67, 256, 256);
                }
            }


        }
    }


    private void addFactoryButtons(GuiGraphicsExtractor matrix, int mouseX, int mouseY)
    {
        if (getMenu().getIsFactory() && !getMenu().getAugmentGUI())
        {
            autoSplitButton.render(WIDGETS, matrix, mouseX, mouseY, getMenu().isAutoSplit());
        }
    }


    private void addRedstoneButtons(GuiGraphicsExtractor matrix, int mouseX, int mouseY) {
        if (showInventoryButtons()) {
            int setting = this.getMenu().getRedstoneMode();
            redstoneIgnoredButton.render(WIDGETS, matrix, mouseX, mouseY, setting == FurnaceSettings.REDSTONE_IGNORED);
            redstoneLowButton.render(WIDGETS, matrix, mouseX, mouseY, setting == FurnaceSettings.REDSTONE_LOW);
            redstoneHighButton.render(WIDGETS, matrix, mouseX, mouseY, setting == FurnaceSettings.REDSTONE_HIGH);
        }
    }

    private void addInventoryButtons(GuiGraphicsExtractor matrix, int mouseX, int mouseY) {
        if (!showInventoryButtons()) {
            matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() - 13, getTopPos() + 2, 0, 0, 12, 13, 256, 256);
        } else if (showInventoryButtons()) {
            matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() - 56, getTopPos(), 0, 13, 60, 91, 256, 256);
            autoInputButton.render(WIDGETS, matrix, mouseX, mouseY, getMenu().getAutoInput());
            autoOutputButton.render(WIDGETS, matrix, mouseX, mouseY, getMenu().getAutoOutput());
            this.blitIO(matrix, mouseX, mouseY);
        }


    }

    private void blitIO(GuiGraphicsExtractor matrix, int mouseX, int mouseY) {
        int[] settings = new int[]{
                getMenu().getSettingBottom(),
                getMenu().getSettingTop(),
                getMenu().getSettingFront(),
                getMenu().getSettingBack(),
                getMenu().getSettingLeft(),
                getMenu().getSettingRight()
        };
        for (int i = 0; i < settings.length; i++)
        {
            if (settings.length != sideButtons.size())
                break;

            if (settings[i] == 0)
                continue;

            FurnaceGuiButton button = sideButtons.get(i);
            button.changeEnabledUV((10 * (settings[i])) - 10, 104);
            button.render(WIDGETS, matrix, mouseX, mouseY, true);

        }
        if (getMenu().getAugmentGUI()) {
            return;
        }
        boolean input = false;
        boolean output = false;
        boolean both = false;
        boolean fuel = false;
        for (int set : settings) {
            if (set == 1) {
                input = true;
            } else if (set == 2) {
                output = true;
            } else if (set == 3) {
                both = true;
            } else if (set == 4) {
                fuel = true;
            }
        }
        if (input || both) {
            if (getMenu().getIsFurnace())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 55, getTopPos() + 16, 0, 114, 18, 18, 256, 256);
            }
            if (getMenu().getIsFactory())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 69, getTopPos() + 5, 0, 114, 18, 18, 256, 256);
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 90, getTopPos() + 5, 0, 114, 18, 18, 256, 256);
                if (getMenu().getTier() > 0)
                {
                    matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 48, getTopPos() + 5, 0, 114, 18, 18, 256, 256);
                    matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 111, getTopPos() + 5, 0, 114, 18, 18, 256, 256);
                    if (getMenu().getTier() > 1)
                    {
                        matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 27, getTopPos() + 5, 0, 114, 18, 18, 256, 256);
                        matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 132, getTopPos() + 5, 0, 114, 18, 18, 256, 256);
                    }
                }
            }


        }
        if (output || both) {
            if (getMenu().getIsFurnace())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 111, getTopPos() + 30, 0, 146, 26, 26, 256, 256);
            }
            if (getMenu().getIsFactory())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 69, getTopPos() + 54, 36, 114, 18, 18, 256, 256);
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 90, getTopPos() + 54, 36, 114, 18, 18, 256, 256);
                if (getMenu().getTier() > 0)
                {
                    matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 48, getTopPos() + 54, 36, 114, 18, 18, 256, 256);
                    matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 111, getTopPos() + 54, 36, 114, 18, 18, 256, 256);
                    if (getMenu().getTier() > 1)
                    {
                        matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 27, getTopPos() + 54, 36, 114, 18, 18, 256, 256);
                        matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 132, getTopPos() + 54, 36, 114, 18, 18, 256, 256);
                    }
                }
            }
        }
        if (fuel) {
            if (getMenu().getIsFurnace())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 55, getTopPos() + 52, 18, 114, 18, 18, 256, 256);
            }
            if (getMenu().getIsGenerator())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 55, getTopPos() + 39, 18, 114, 18, 18, 256, 256);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        double actualMouseX = mouseX - (((double) this.width - (double) this.getImageWidth()) / 2);
        double actualMouseY = mouseY - (((double) this.height - (double) this.getImageHeight()) / 2);
        this.mouseClickedRedstoneButtons(actualMouseX, actualMouseY, button);
        this.mouseClickedInventoryButtons(button, actualMouseX, actualMouseY);
        this.mouseClickedAugmentButton(actualMouseX, actualMouseY, button);
        this.mouseClickedAutoSplitButton(actualMouseX, actualMouseY, button);
        return super.mouseClicked(event, doubleClick);
    }

    public void mouseClickedAutoSplitButton(double mouseX, double mouseY, int buttonid)
    {
        if (!this.getMenu().isAutoSplit()) {
            autoSplitButton.onClick(mouseX, mouseY, buttonid, getMenu().getIsFactory(), () -> sendSplitButtonClick(1));
        } else {
            autoSplitButton.onClick(mouseX, mouseY, buttonid, getMenu().getIsFactory(), () -> sendSplitButtonClick(0));
        }
    }

    public void mouseClickedAugmentButton(double mouseX, double mouseY, int buttonid) {
        if (!this.getMenu().getAugmentGUI()) {
            augmentButton.onClick(mouseX, mouseY, buttonid, true, () -> sendAugmentGUIButtonClick(1));
        } else {
            augmentButton.onClick(mouseX, mouseY, buttonid, true, () -> sendAugmentGUIButtonClick(0));
        }
    }

    public void mouseClickedInventoryButtons(int button, double mouseX, double mouseY) {
        if (!showInventoryButtons()) {
            if (mouseX >= -17 && mouseX <= 1 && mouseY >= -1 && mouseY <= 17) {
                setShowConfig(1);
            }
        } else {
            if (mouseX >= -13 && mouseX <= 0 && mouseY >= 5 && mouseY <= 18) {
                setShowConfig(0);
            }
            if (!getMenu().getAutoInput())
            {
                autoInputButton.onClick(mouseX, mouseY, button, true, () -> sendAutoIOButtonClick(0, 1));
            }
            else if (getMenu().getAutoInput())
            {
                autoInputButton.onClick(mouseX, mouseY, button, true, () -> sendAutoIOButtonClick(0, 0));
            }
            if (!getMenu().getAutoOutput())
            {
                autoOutputButton.onClick(mouseX, mouseY, button, true, () -> sendAutoIOButtonClick(1, 1));
            }
            else if (getMenu().getAutoOutput())
            {
                autoOutputButton.onClick(mouseX, mouseY, button, true, () -> sendAutoIOButtonClick(1, 0));

            }
            clickSideButton(mouseX, mouseY, topButton, button, getMenu().getSettingTop(), Direction.UP);
            clickSideButton(mouseX, mouseY, bottomButton, button, getMenu().getSettingBottom(), Direction.DOWN);
            clickSideButton(mouseX, mouseY, frontButton, button, getMenu().getSettingFront(), getMenu().getFrontDirection(), isShiftKeyDown());
            clickSideButton(mouseX, mouseY, backButton, button, getMenu().getSettingBack(), getMenu().getBackDirection());
            clickSideButton(mouseX, mouseY, leftButton, button, getMenu().getSettingLeft(), getMenu().getLeftDirection());
            clickSideButton(mouseX, mouseY, rightButton, button, getMenu().getSettingRight(), getMenu().getRightDirection());
        }
    }



    /**
     *
     * @param set 0, 1 are valid
     */
    private void sendSplitButtonClick(int set)
    {

        if (set < 0 || set > 1)
        {
            throw new IllegalArgumentException(
                    "Invalid set value: " + set + " (valid: 0-1)"
            );
        }

        int x = getMenu().getPos().getX();
        int y = getMenu().getPos().getY();
        int z = getMenu().getPos().getZ();
        Messages.sendToServer(new PacketSplitFurnaceSetting(x, y, z, set));
    }

    /**
     *
     * @param set 0, 1 are valid
     */
    private void sendAugmentGUIButtonClick(int set)
    {

        if (set < 0 || set > 1)
        {
            throw new IllegalArgumentException(
                    "Invalid set value: " + set + " (valid: 0-1)"
            );
        }

        int x = getMenu().getPos().getX();
        int y = getMenu().getPos().getY();
        int z = getMenu().getPos().getZ();
        Messages.sendToServer(new PacketAugmentGUIFurnaceSetting(x, y, z, set));
    }


    /**
     * @param index 0, 1 are valid 0 == INPUT, 1 == OUTPUT
     * @param set 0, 1 are valid 0 == FALSE, 1 == TRUE
     */
    private void sendAutoIOButtonClick(int index, int set)
    {

        if (index < 0 || index > 1)
        {
            throw new IllegalArgumentException(
                    "Invalid index: " + index + " (valid: 0-1)"
            );
        }

        if (set < 0 || set > 1)
        {
            throw new IllegalArgumentException(
                    "Invalid set value: " + set + " (valid: 0-1)"
            );
        }

        int x = getMenu().getPos().getX();
        int y = getMenu().getPos().getY();
        int z = getMenu().getPos().getZ();
        Messages.sendToServer(new PacketAutoIOFurnaceSetting(x, y, z, index, set));
    }

    /**
     *
     * @param set 0, 1, 2 are valid
     */
    private void sendRedstoneButtonClick(int set)
    {

        if (set < 0 || set > 2)
        {
            throw new IllegalArgumentException(
                    "Invalid set value: " + set + " (valid: 0-2)"
            );
        }

        int x = getMenu().getPos().getX();
        int y = getMenu().getPos().getY();
        int z = getMenu().getPos().getZ();
        Messages.sendToServer(new PacketRedstoneFurnaceSetting(x, y, z, set));
    }

    private void sendSideButtonClick(Direction direction, int set)
    {
        int x = getMenu().getPos().getX();
        int y = getMenu().getPos().getY();
        int z = getMenu().getPos().getZ();
        int index = DirectionUtil.getId(direction);
        Messages.sendToServer(new PacketFurnaceSettings(x, y, z, index, set));
    }

    protected void clickSideButton(double mouseX, double mouseY, FurnaceGuiButton button, int buttonid, int setting, Direction direction)
    {
        clickSideButton(mouseX, mouseY, button, buttonid, setting, direction, false);
    }

    protected void clickSideButton(double mouseX, double mouseY, FurnaceGuiButton button, int buttonid, int setting, Direction direction, boolean shift)
    {

        if (shift && frontButton.hovering(mouseX, mouseY))
        {
            for (int i = 0; i < sideButtons.size(); i++)
            {
                Messages.sendToServer(new PacketFurnaceSettings(getMenu().getPos().getX(), getMenu().getPos().getY(), getMenu().getPos().getZ(), i, 0));
            }
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 0.6F, 0.3F));
        }
        else if (buttonid == GLFW.GLFW_MOUSE_BUTTON_1) {
            int set = setting == FurnaceSettings.FUEL_INPUT ? FurnaceSettings.NONE : setting + 1;
            button.onClick(mouseX, mouseY, buttonid, true, () -> sendSideButtonClick(direction, set));
        }
        else if (buttonid == GLFW.GLFW_MOUSE_BUTTON_2) {
            int set = setting == FurnaceSettings.NONE ? FurnaceSettings.FUEL_INPUT : setting - 1;
            button.onRightClick(mouseX, mouseY, buttonid, true, () -> sendSideButtonClick(direction, set));
        }

    }

    public void mouseClickedRedstoneButtons(double mouseX, double mouseY, int buttonid) {
        if (showInventoryButtons()) {
            boolean shift = isShiftKeyDown();
            redstoneIgnoredButton.onClick(mouseX, mouseY, buttonid, getMenu().getRedstoneMode() != 0, () -> sendRedstoneButtonClick(0));
            redstoneLowButton.onClick(mouseX, mouseY, buttonid, getMenu().getRedstoneMode() != 1, () -> sendRedstoneButtonClick(1));
            redstoneHighButton.onClick(mouseX, mouseY, buttonid, getMenu().getRedstoneMode() != 2, () -> sendRedstoneButtonClick(2));
        }
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