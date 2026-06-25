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
import ironfurnaces.network.Messages;
import ironfurnaces.network.PacketFurnaceSettings;
import ironfurnaces.network.PacketShowConfig;
import ironfurnaces.util.StringHelper;
import ironfurnaces.util.gui.FurnaceGuiButton;
import ironfurnaces.util.gui.FurnaceGuiEnergy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
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
    public FurnaceGuiButton comparatorButton;
    public FurnaceGuiButton comparatorSubButton;
    public FurnaceGuiButton addButton;
    public FurnaceGuiButton subButton;

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
        energyBar = new FurnaceGuiEnergy(left, top, 109, 22, 14, 42, 176, 14);
        autoSplitButton = new FurnaceGuiButton(left, top, 9, 56, 14, 14,112, 189);
        augmentButton = new FurnaceGuiButton(left, top, 161, 4, 11, 11);
        autoInputButton = new FurnaceGuiButton(left, top, -47, 12, 14, 14, 0, 189);
        autoOutputButton = new FurnaceGuiButton(left, top, -29, 12, 14, 14, 14, 189);
        redstoneIgnoredButton = new FurnaceGuiButton(left, top, -47, 70, 14, 14, 28, 189);
        redstoneLowButton = new FurnaceGuiButton(left, top, -31, 70, 14, 14, 84, 189, 98, 189, 98, 189);
        redstoneHighButton = new FurnaceGuiButton(left, top, -31, 70, 14, 14, 42, 189);
        comparatorButton = new FurnaceGuiButton(left, top, -15, 70, 14, 14, 56, 189);
        comparatorSubButton = new FurnaceGuiButton(left, top, -47, 86, 14, 14, 70, 189);
        addButton = new FurnaceGuiButton(left, top, -31, 86, 14, 14, 0, 14, 14, 14, 28, 14);
        subButton = new FurnaceGuiButton(left, top, -31, 86, 14, 14, 0, 0, 14, 0, 28, 0);
        sideButtons.add(bottomButton = new FurnaceGuiButton(left, top, -32,  55, 10, 10));
        sideButtons.add(topButton = new FurnaceGuiButton(left, top, -32,  31, 10, 10));
        sideButtons.add(frontButton = new FurnaceGuiButton(left, top, -32,  43, 10, 10));
        sideButtons.add(backButton = new FurnaceGuiButton(left, top, -20,  55, 10, 10));
        sideButtons.add(leftButton = new FurnaceGuiButton(left, top, -44,  43, 10, 10));
        sideButtons.add(rightButton = new FurnaceGuiButton(left, top, -20,  43, 10, 10));
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

        if (this.getMenu().getIsFactory())
            graphics.text(font, name, this.getImageWidth() / 2 - this.minecraft.font.width(name.getString()) / 2, -10, 16777215, false);
        else
            graphics.text(font, name, this.getMenu().getIsFurnace() ? 7 + this.getImageWidth() / 2 - this.minecraft.font.width(name.getString()) / 2 : this.getImageWidth() / 2 - this.minecraft.font.width(name.getString()) / 2, 6, 4210752, false);

        graphics.text(font, this.playerInv.getDisplayName(), 7, this.getImageHeight() - 93, 4210752, false);

        if (showInventoryButtons() && this.getMenu().getRedstoneMode() == 4) {
            int comSub = this.getMenu().getComSub();
            int i = comSub > 9 ? 28 : 31;
            graphics.text(font, Component.literal("" + comSub), i - 42, 90, 4210752, false);
        }


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
            if (mouseX >= -20 && mouseX <= 0 && mouseY >= 4 && mouseY <= 26) {
                graphics.setTooltipForNextFrame(font, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_open"), tooltipX, tooltipY);
            }
        } else {
            if (mouseX >= -13 && mouseX <= 0 && mouseY >= 4 && mouseY <= 26) {
                graphics.setComponentTooltipForNextFrame(font, StringHelper.getShiftInfoGui(), tooltipX, tooltipY);
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
            list.add(this.getMenu().getTooltip(Direction.UP.ordinal()));
            topButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            list = Lists.newArrayList();
            list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_bottom"));
            list.add(this.getMenu().getTooltip(Direction.DOWN.ordinal()));
            bottomButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            list = Lists.newArrayList();
            if (isShiftKeyDown()) {
                list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_reset"));
            } else {
                list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_front"));
                list.add(this.getMenu().getTooltip(getMenu().getIndexFront()));
            }
            frontButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            list = Lists.newArrayList();
            list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_back"));
            list.add(this.getMenu().getTooltip(getMenu().getIndexBack()));
            backButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            list = Lists.newArrayList();
            list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_left"));
            list.add(this.getMenu().getTooltip(getMenu().getIndexLeft()));
            leftButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            list = Lists.newArrayList();
            list.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_right"));
            list.add(this.getMenu().getTooltip(getMenu().getIndexRight()));
            rightButton.renderComponentTooltip(font, graphics, list, tooltipX, tooltipY, mouseX, mouseY, true);
            redstoneIgnoredButton.renderTooltip(font, graphics, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_redstone_ignored"), tooltipX, tooltipY, mouseX, mouseY, true);
            redstoneLowButton.renderTooltip(font, graphics, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_redstone_low"), tooltipX, tooltipY, mouseX, mouseY, isShiftKeyDown());
            redstoneHighButton.renderTooltip(font, graphics, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_redstone_high"), tooltipX, tooltipY, mouseX, mouseY, !isShiftKeyDown());
            comparatorButton.renderTooltip(font, graphics, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_redstone_comparator"), tooltipX, tooltipY, mouseX, mouseY, true);
            comparatorSubButton.renderTooltip(font, graphics, Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_redstone_comparator_sub"), tooltipX, tooltipY, mouseX, mouseY, true);
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
            energyBar.render(GUI_GENERATOR, matrix, getMenu().getEnergyScaled(42));
        }
    }

    protected void renderFactoryBg(GuiGraphicsExtractor matrix)
    {
        if (getMenu().getIsFactory() && !getMenu().getAugmentGUI())
        {
            addSlots(matrix, this.getMenu().getTier());
            energyBar.changePos(9, 7, true);
            energyBar.changeUV(176, 22, true);
            energyBar.render(GUI_FACTORY, matrix, getMenu().getEnergyScaled(42));

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
            boolean flag = isShiftKeyDown();
            int setting = this.getMenu().getRedstoneMode();
            if (setting == 0) redstoneIgnoredButton.render(WIDGETS, matrix, mouseX, mouseY, true);
            if (flag) redstoneLowButton.render(WIDGETS, matrix, mouseX, mouseY, setting == 2);
            if (!flag) redstoneHighButton.render(WIDGETS, matrix, mouseX, mouseY, setting == 1);
            if (setting == 3) comparatorButton.render(WIDGETS, matrix, mouseX, mouseY, true);
            if (setting == 4) {
                comparatorSubButton.render(WIDGETS, matrix, mouseX, mouseY, true);
                int comSub = getMenu().getComSub();
                addButton.render(WIDGETS, matrix, mouseX, mouseY, comSub == 15);
                if (flag)
                    subButton.render(WIDGETS, matrix, mouseX, mouseY, comSub == 0);
            }
        }
    }

    private void addInventoryButtons(GuiGraphicsExtractor matrix, int mouseX, int mouseY) {
        if (!showInventoryButtons()) {
            matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() - 20, getTopPos() + 4, 0, 28, 23, 26, 256, 256);
        } else if (showInventoryButtons()) {
            matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() - 56, getTopPos() + 4, 0, 54, 59, 107, 256, 256);
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
            button.changeEnabledUV((10 * (settings[i])) - 10, 161);
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
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 55, getTopPos() + 16, 0, 171, 18, 18, 256, 256);
            }
            if (getMenu().getIsFactory())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 69, getTopPos() + 5, 0, 171, 18, 18, 256, 256);
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 90, getTopPos() + 5, 0, 171, 18, 18, 256, 256);
                if (getMenu().getTier() > 0)
                {
                    matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 48, getTopPos() + 5, 0, 171, 18, 18, 256, 256);
                    matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 111, getTopPos() + 5, 0, 171, 18, 18, 256, 256);
                    if (getMenu().getTier() > 1)
                    {
                        matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 27, getTopPos() + 5, 0, 171, 18, 18, 256, 256);
                        matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 132, getTopPos() + 5, 0, 171, 18, 18, 256, 256);
                    }
                }
            }


        }
        if (output || both) {
            if (getMenu().getIsFurnace())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 111, getTopPos() + 30, 0, 203, 26, 26, 256, 256);
            }
            if (getMenu().getIsFactory())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 69, getTopPos() + 54, 36, 171, 18, 18, 256, 256);
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 90, getTopPos() + 54, 36, 171, 18, 18, 256, 256);
                if (getMenu().getTier() > 0)
                {
                    matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 48, getTopPos() + 54, 36, 171, 18, 18, 256, 256);
                    matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 111, getTopPos() + 54, 36, 171, 18, 18, 256, 256);
                    if (getMenu().getTier() > 1)
                    {
                        matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 27, getTopPos() + 54, 36, 171, 18, 18, 256, 256);
                        matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 132, getTopPos() + 54, 36, 171, 18, 18, 256, 256);
                    }
                }
            }
        }
        if (fuel) {
            if (getMenu().getIsFurnace())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 55, getTopPos() + 52, 18, 171, 18, 18, 256, 256);
            }
            if (getMenu().getIsGenerator())
            {
                matrix.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, getLeftPos() + 55, getTopPos() + 39, 18, 171, 18, 18, 256, 256);
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
        this.mouseClickedRedstoneButtons(actualMouseX, actualMouseY);
        this.mouseClickedInventoryButtons(button, actualMouseX, actualMouseY);
        this.mouseClickedAugmentButton(actualMouseX, actualMouseY);
        this.mouseClickedAutoSplitButton(actualMouseX, actualMouseY);
        return super.mouseClicked(event, doubleClick);
    }

    public void mouseClickedAutoSplitButton(double mouseX, double mouseY)
    {
        if (!this.getMenu().isAutoSplit()) {
            autoSplitButton.onClick(mouseX, mouseY, getMenu().getPos(), 11, 1, getMenu().getIsFactory());
        } else {
            autoSplitButton.onClick(mouseX, mouseY, getMenu().getPos(), 11, 0, getMenu().getIsFactory());
        }
    }

    public void mouseClickedAugmentButton(double mouseX, double mouseY) {
        if (!this.getMenu().getAugmentGUI()) {
            augmentButton.onClick(mouseX, mouseY, getMenu().getPos(), 10, 1, true);
        } else {
            augmentButton.onClick(mouseX, mouseY, getMenu().getPos(), 10, 0, true);
        }
    }

    public void mouseClickedInventoryButtons(int button, double mouseX, double mouseY) {
        if (!showInventoryButtons()) {
            if (mouseX >= -20 && mouseX <= 0 && mouseY >= 4 && mouseY <= 26) {
                setShowConfig(1);
            }
        } else {
            if (mouseX >= -13 && mouseX <= 0 && mouseY >= 4 && mouseY <= 26) {
                setShowConfig(0);
            }
            if (!getMenu().getAutoInput())
            {
                autoInputButton.onClick(mouseX, mouseY, getMenu().getPos(), 6, 1, true);
            }
            else if (getMenu().getAutoInput())
            {
                autoInputButton.onClick(mouseX, mouseY, getMenu().getPos(), 6, 0, true);
            }
            if (!getMenu().getAutoOutput())
            {
                autoOutputButton.onClick(mouseX, mouseY, getMenu().getPos(), 7, 1, true);
            }
            else if (getMenu().getAutoOutput())
            {
                autoOutputButton.onClick(mouseX, mouseY, getMenu().getPos(), 7, 0, true);
            }
            clickInvButton(mouseX, mouseY, topButton, button, getMenu().getSettingTop(), Direction.UP.ordinal());
            clickInvButton(mouseX, mouseY, bottomButton, button, getMenu().getSettingBottom(), Direction.DOWN.ordinal());
            clickInvButton(mouseX, mouseY, frontButton, button, getMenu().getSettingFront(), getMenu().getIndexFront(), isShiftKeyDown());
            clickInvButton(mouseX, mouseY, backButton, button, getMenu().getSettingBack(), getMenu().getIndexBack());
            clickInvButton(mouseX, mouseY, leftButton, button, getMenu().getSettingLeft(), getMenu().getIndexLeft());
            clickInvButton(mouseX, mouseY, rightButton, button, getMenu().getSettingRight(), getMenu().getIndexRight());
        }
    }

    protected void clickInvButton(double mouseX, double mouseY, FurnaceGuiButton button, int buttonid, int setting, int index)
    {
        clickInvButton(mouseX, mouseY, button, buttonid, setting, index, false);
    }

    protected void clickInvButton(double mouseX, double mouseY, FurnaceGuiButton button, int buttonid, int setting, int index, boolean shift)
    {
        int set = setting == 4 ? 0 : setting + 1;
        button.onClick(mouseX, mouseY, getMenu().getPos(), index, set, buttonid == GLFW.GLFW_MOUSE_BUTTON_1);
        set = setting == 0 ? 4 : setting - 1;
        button.onRightClick(mouseX, mouseY, buttonid, getMenu().getPos(), index, set, true);

        if (shift && frontButton.hovering(mouseX, mouseY))
        {
            for (int i = 0; i < sideButtons.size(); i++)
            {
                Messages.sendToServer(new PacketFurnaceSettings(getMenu().getPos().getX(), getMenu().getPos().getY(), getMenu().getPos().getZ(), i, 0));
            }
        }




    }

    public void mouseClickedRedstoneButtons(double mouseX, double mouseY) {
        if (showInventoryButtons()) {
            boolean shift = isShiftKeyDown();
            addButton.onClick(mouseX, mouseY, getMenu().getPos(), 9, getMenu().getComSub() + 1, !shift && getMenu().getComSub() < 15);
            subButton.onClick(mouseX, mouseY, getMenu().getPos(), 9, getMenu().getComSub() - 1, shift && getMenu().getComSub() > 0);
            redstoneIgnoredButton.onClick(mouseX, mouseY, getMenu().getPos(), 8, 0, getMenu().getRedstoneMode() != 0);
            redstoneLowButton.onClick(mouseX, mouseY, getMenu().getPos(), 8, 2, getMenu().getRedstoneMode() != 2 && shift);
            redstoneHighButton.onClick(mouseX, mouseY, getMenu().getPos(), 8, 1, getMenu().getRedstoneMode() != 1 && !shift);
            comparatorButton.onClick(mouseX, mouseY, getMenu().getPos(), 8, 3, getMenu().getRedstoneMode() != 3);
            comparatorSubButton.onClick(mouseX, mouseY, getMenu().getPos(), 8, 4, getMenu().getRedstoneMode() != 4);
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