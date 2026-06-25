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

import ironfurnaces.Config;
import ironfurnaces.IronFurnaces;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class FurnaceSettings {

    /**
     * <p>Index == | 0 == DOWN, 1 == UP, 2 == NORTH, 3 == SOUTH, 4 == WEST, 5 == EAST</p>
     * <p>Values range between, 0 == NONE, 1 == INPUT, 2 == OUTPUT, 3 == INPUT+OUTPUT, 4 == FUEL INPUT</p>
     **/
    private int[] settings;

    /**
     * <p>Index == | 0 == AUTO INPUT, 1 == AUTO OUTPUT</p>
     * <p>0 == OFF, 1 == ON</p>
     **/
    private int[] autoIO;

    /**
     *   0 == ignored, 1 == low, 2 == high
     **/
    private int redstoneSetting;

    private int augmentGUI;
    private int autoSplit;

    //statics
    public static final int NONE = 0;
    public static final int INPUT = 1;
    public static final int OUTPUT = 2;
    public static final int INPUT_OUTPUT = 3;
    public static final int FUEL_INPUT = 4;

    public static final int REDSTONE_IGNORED = 0;
    public static final int REDSTONE_LOW = 1;
    public static final int REDSTONE_HIGH = 2;

    public FurnaceSettings() {
        this(new int[]{0, 0, 0, 0, 0, 0}, new int[]{0, 0}, 0, 0, 0);
    }

    protected FurnaceSettings(int[] settings, int[] autoIO, int redstoneSetting, int augmentGUI, int autoSplit)
    {
        if (settings == null || settings.length != 6)
        {
            throw new IllegalArgumentException("settings must be an array of length 6");
        }

        if (autoIO == null || autoIO.length != 2)
        {
            throw new IllegalArgumentException("autoIO must be an array of length 2");
        }
        this.settings = settings;
        this.autoIO = autoIO;
        this.redstoneSetting = redstoneSetting;
        this.augmentGUI = augmentGUI;
        this.autoSplit = autoSplit;
    }


    public FurnaceSettings createCopy()
    {
       return new FurnaceSettings(this.settings, this.autoIO, this.redstoneSetting, this.augmentGUI, this.autoSplit);
    }


    /**
     * Returns the setting for the direction provided
     * Returned values range between, 0 == NONE, 1 == INPUT, 2 == OUTPUT, 3 == INPUT+OUTPUT, 4 == FUEL INPUT
     * @param direction == which side of the furnace to check
     * @return
     */
    public int getSideSetting(Direction direction)
    {
        return this.getSideSetting(DirectionUtil.getId(direction));
    }

    private int getSideSetting(int index)
    {
        switch (index) {
            case 0:
                return settings[0];
            case 1:
                return settings[1];
            case 2:
                return settings[2];
            case 3:
                return settings[3];
            case 4:
                return settings[4];
            case 5:
                return settings[5];
            default:
                throw new IllegalArgumentException("Invalid side index: " + index);
        }
    }

    /**
     * Sets the setting for the direction provided
     * Returns the value passed in
     * @param direction == which side of the furnace to check
     * @return
     */
    public int setSideSetting(Direction direction, int value)
    {
        return this.setSideSetting(DirectionUtil.getId(direction), value);
    }

    private int setSideSetting(int index, int value)
    {
        switch (index) {
            case 0:
                settings[0] = value;
                return value;
            case 1:
                settings[1] = value;
                return value;
            case 2:
                settings[2] = value;
                return value;
            case 3:
                settings[3] = value;
                return value;
            case 4:
                settings[4] = value;
                return value;
            case 5:
                settings[5] = value;
                return value;
            default:
                throw new IllegalArgumentException("Invalid side index: " + index);
        }
    }


    /**
     * <p>Gets the setting for AUTO INPUT</p>
     * @return
     */
    public boolean getAutoInputSetting()
    {
        switch (autoIO[0])
        {
            case 0:
                return false;
            case 1:
                return true;
            default:
                throw new IllegalStateException(
                        "Invalid auto input setting: " + autoIO[0]
                );
        }
    }

    /**
     * <p>Gets the setting for AUTO OUTPUT</p>
     * @return
     */
    public boolean getAutoOutputSetting()
    {
        switch (autoIO[1])
        {
            case 0:
                return false;
            case 1:
                return true;
            default:
                throw new IllegalStateException(
                        "Invalid auto output setting: " + autoIO[1]
                );
        }
    }

    /**
     * <p>Sets the AUTO INPUT setting to the provided value</p>
     * @param value
     */
    public void setAutoInputSetting(boolean value)
    {
        autoIO[0] = value ? 1 : 0;
    }

    /**
     * <p>Sets the AUTO OUTPUT setting to the provided value</p>
     * @param value
     */
    public void setAutoOutputSetting(boolean value)
    {
        autoIO[1] = value ? 1 : 0;
    }

    /**
     * <p>Gets the redstone setting</p>
     * @return <p>Returned value can be 0, 1, 2</p>
     */
    public int getRedstoneSetting()
    {
        return this.redstoneSetting;
    }

    /**
     * <p>Sets the redstone setting to the provided value</p>
     * @param value <p>Allowed values are 0, 1, 2</p>
     * @return <p>Returns the value passed in</p>
     */
    public int setRedstoneSetting(int value)
    {
        if (value < 0 || value > 2)
        {
            throw new IllegalArgumentException("Invalid value for redstone setting: " + value);
        }
        this.redstoneSetting = value;
        return value;
    }

    /**
     * <p>Gets the ShowAugmentGUI Setting</p>
     * @return
     */
    public boolean getShowAugmentGUISetting()
    {
        boolean b = this.augmentGUI == 1 ? true : false;
        return b;

    }

    /**
     * <p>Sets the ShowAugmentGUI setting</p>
     * @param value true/false
     * @return <p>Returns value provided</p>
     */
    public boolean setShowAugmentGUISetting(boolean value)
    {
        this.augmentGUI = value ? 1 : 0;
        return value;
    }

    /**
     * <p>Gets the AUTO SPLIT Setting</p>
     * @return
     */
    public boolean getAutoSplitSetting()
    {
        boolean b = this.autoSplit == 1 ? true : false;
        return b;
    }


    /**
     * <p>Sets the AUTO SPLIT setting</p>
     * @param value true/false
     * @return <p>Returns value provided</p>
     */
    public boolean setAutoSplitSetting(boolean value)
    {
        this.autoSplit = value ? 1 : 0;
        return value;
    }


    public void write(ValueOutput output) {
        output.putIntArray("Settings", settings);
        output.putIntArray("AutoIO", autoIO);
        output.putInt("Redstone", redstoneSetting);
        output.putInt("AugmentGUI", augmentGUI);
        output.putInt("AutoSplit", autoSplit);
    }

    public void read(ValueInput input) {
        input.getIntArray("Settings").ifPresent(a -> this.settings = a);
        input.getIntArray("AutoIO").ifPresent(a -> this.autoIO = a);
        input.getInt("Redstone").ifPresent(a -> this.redstoneSetting = a);
        input.getInt("AugmentGUI").ifPresent(v -> this.augmentGUI = v);
        input.getInt("AutoSplit").ifPresent(v -> this.autoSplit = v);
    }

    public void writeToTag(CompoundTag tag) {
        tag.putIntArray("Settings", settings);
        tag.putIntArray("AutoIO", autoIO);
        tag.putInt("Redstone", redstoneSetting);
        tag.putInt("AugmentGUI", augmentGUI);
        tag.putInt("AutoSplit", autoSplit);
    }

    public FurnaceSettings readFromTag(CompoundTag tag) {
        tag.getIntArray("Settings").ifPresent(a -> this.settings = a);
        tag.getIntArray("AutoIO").ifPresent(a -> this.autoIO = a);
        tag.getInt("Redstone").ifPresent(a -> this.redstoneSetting = a);
        tag.getInt("AugmentGUI").ifPresent(v -> this.augmentGUI = v);
        tag.getInt("AutoSplit").ifPresent(v -> this.autoSplit = v);
        return this;
    }


}