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

package ironfurnaces.tileentity.furnaces;

import com.google.common.collect.Maps;
import ironfurnaces.Config;
import ironfurnaces.blocks.furnaces.BlockIronFurnaceBase;
import ironfurnaces.blocks.furnaces.BlockMillionFurnace;
import ironfurnaces.energy.FEnergyStorage;
import ironfurnaces.init.Registration;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.items.augments.*;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.TileEntityInventory;
import ironfurnaces.tileentity.furnaces.other.BlockAllthemodiumFurnaceTile;
import ironfurnaces.tileentity.furnaces.other.BlockUnobtainiumFurnaceTile;
import ironfurnaces.tileentity.furnaces.other.BlockVibraniumFurnaceTile;
import ironfurnaces.util.DirectionUtil;
import ironfurnaces.util.FurnaceSettings;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static ironfurnaces.init.ModSetup.*;

public abstract class BlockIronFurnaceTileBase extends TileEntityInventory implements RecipeCraftingHolder, StackedContentsCompatible {
    public static final int INPUT = 0;
    public static final int FUEL = 1;
    public static final int OUTPUT = 2;
    public static final int AUGMENT_RED = 3;
    public static final int AUGMENT_GREEN = 4;
    public static final int AUGMENT_BLUE = 5;
    public static final int GENERATOR_FUEL = 6;
    public static final int[] FACTORY_INPUT = new int[]{7, 8, 9, 10, 11, 12};
    //public Player savedPlayer;

    public final int[] provides = new int[Direction.values().length];
    protected final int[] lastProvides = new int[this.provides.length];
    public int jovial;
    public int[] currentAugment = new int[3];
    public int[] factoryCookTime = new int[6];
    public int[] factoryTotalCookTime = new int[6];
    public double[] usedRF = new double[6];
    public double generatorBurn;
    public int generatorRecentRecipeRF;
    public double gottenRF;
    public int furnaceBurnTime;
    public int cookTime;
    public int totalCookTime;
    public int recipesUsed;

    public long lastGameTickEnergyUpdated;

    public UUID owner;

    public boolean rainbowGenerating;

    public final Object2IntOpenHashMap<ResourceLocation> recipes = new Object2IntOpenHashMap<>();
    public RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> recipeCheckSmelting;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> recipeCheckSmoking;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> recipeCheckBlasting;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends GeneratorRecipe> recipeCheckGenerator;
    public FurnaceSettings furnaceSettings;

    public FEnergyStorage energyStorage = new FEnergyStorage(Config.furnaceEnergyCapacityTier2.get()) {
        @Override
        protected void onEnergyChanged() {
            if (level != null && level.getBlockEntity(getBlockPos()) != null)
            {
                if (lastGameTickEnergyUpdated <= 0)
                {
                    setChanged();
                    lastGameTickEnergyUpdated = level.getGameTime();
                }
                else if (level.getGameTime() - lastGameTickEnergyUpdated >= 20)
                {
                    setChanged();
                    lastGameTickEnergyUpdated = level.getGameTime();
                }
            }



        }
    };

    public BlockIronFurnaceTileBase(BlockEntityType<?> tileentitytypeIn, BlockPos pos, BlockState state) {
        super(tileentitytypeIn, pos, state, 19);
        recipeCheckSmelting = RecipeManager.createCheck(RecipeType.SMELTING);
        recipeCheckSmoking = RecipeManager.createCheck(RecipeType.SMOKING);
        recipeCheckBlasting = RecipeManager.createCheck(RecipeType.BLASTING);
        recipeCheckGenerator = RecipeManager.createCheck(Registration.GENERATOR_RECIPE_TYPE.get());
        recipeType = RecipeType.SMELTING;
        furnaceSettings = new FurnaceSettings() {
            @Override
            public void onChanged() {
                setChanged();
            }
        };


    }



    public int getEnergy() {
        return energyStorage.getEnergy();
    }

    public int getCapacity() {
        return energyStorage.getCapacity();
    }

    public void setEnergy(int energy) {
        energyStorage.setEnergy(energy);
    }

    public void setMaxEnergy(int energy) {
        energyStorage.setCapacity(energy);
    }

    public void removeEnergy(int energy) {
        energyStorage.setEnergy(energyStorage.getEnergy() - energy);
    }

    public boolean hasRecipe(ItemStack stack) {

        Item item = stack.getItem();
        if (recipeType == RecipeType.SMOKING)
        {
            return HAS_RECIPE_SMOKING.computeIfAbsent(item, (value) -> this.recipeCheckSmoking.getRecipeFor(new SingleRecipeInput(stack), this.level).isPresent());
        }
        else if (recipeType == RecipeType.BLASTING)
        {
            return HAS_RECIPE_BLASTING.computeIfAbsent(item, (value) -> this.recipeCheckBlasting.getRecipeFor(new SingleRecipeInput(stack), this.level).isPresent());

        }
        return HAS_RECIPE.computeIfAbsent(item, (value) -> this.recipeCheckSmelting.getRecipeFor(new SingleRecipeInput(stack), this.level).isPresent());


    }

    protected RecipeHolder<? extends AbstractCookingRecipe> getRecipeNonCached(ItemStack stack) {
        if (recipeType == RecipeType.SMOKING)
        {
            return this.recipeCheckSmoking.getRecipeFor(new SingleRecipeInput(stack), this.level).orElse(null);
        }
        else if (recipeType == RecipeType.BLASTING)
        {
            return this.recipeCheckBlasting.getRecipeFor(new SingleRecipeInput(stack), this.level).orElse(null);

        }
        else
        {
            return this.recipeCheckSmelting.getRecipeFor(new SingleRecipeInput(stack), this.level).orElse(null);

        }
    }

    public boolean hasGeneratorBlastingRecipe(ItemStack stack) {
        return this.recipeCheckGenerator.getRecipeFor(new SingleRecipeInput(stack), this.level).isPresent();
    }

    protected RecipeHolder<? extends GeneratorRecipe> getRecipeGeneratorBlasting(ItemStack stack) {
        return this.recipeCheckGenerator.getRecipeFor(new SingleRecipeInput(stack), this.level).orElse(null);

    }

    protected void checkRecipeType() {
        ItemStack stack = this.getItem(AUGMENT_RED);
        if (stack.getItem() instanceof ItemAugmentBlasting) {
            if (recipeType != RecipeType.BLASTING) {
                recipeType = RecipeType.BLASTING;
            }
        }
        if (stack.getItem() instanceof ItemAugmentSmoking) {
            if (recipeType != RecipeType.SMOKING) {
                recipeType = RecipeType.SMOKING;
            }
        }
        if (!(stack.getItem() instanceof ItemAugmentSmoking) && !(stack.getItem() instanceof ItemAugmentBlasting)) {
            if (recipeType != RecipeType.SMELTING) {
                recipeType = RecipeType.SMELTING;
            }
        }
    }

    public int getCookTime() {
        ItemStack stack = this.getItem(AUGMENT_GREEN);
        if (this.getItem(INPUT).getItem() == Items.AIR) {
            return totalCookTime;
        }
        int speed = getSpeed();
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemAugmentSpeed) {
                speed = Math.max(1, (speed / 2));
            }
            if (stack.getItem() instanceof ItemAugmentFuel) {
                speed = Math.max(1, (int) (Math.ceil(speed * 1.25)));
            }
        }
        return Math.max(1, speed);
    }

    protected int getSpeed() {
        int regular = getCookTimeConfig().get();
        RecipeHolder<? extends AbstractCookingRecipe> recipe = getRecipeNonCached(this.getItem(INPUT));
        if (recipe != null)
        {
            int recipe_cooktime = recipe.value().getCookingTime();
            double div = 200.0 / recipe_cooktime;
            double i = regular / div;
            return (int)Math.max(1, i);
        }
        else
        {
            return 0;
        }

    }

    protected int getFactoryCookTime(int slot) {
        ItemStack stack = this.getItem(AUGMENT_GREEN);
        if (this.getItem(slot).getItem() == Items.AIR) {
            return factoryTotalCookTime[slot - FACTORY_INPUT[0]];
        }
        int speed = getFactorySpeed(slot);
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemAugmentSpeed) {
                speed = Math.max(1, (speed / 2));
            }
            if (stack.getItem() instanceof ItemAugmentFuel) {
                speed = Math.max(1, (int) (Math.ceil(speed * 1.25)));
            }
        }
        return Math.max(1, speed);
    }

    protected int getFactorySpeed(int slot) {
        int regular = getCookTimeConfig().get();
        RecipeHolder<? extends AbstractCookingRecipe> recipe = getRecipeNonCached(this.getItem(slot));
        if (recipe != null)
        {
            int recipe_cooktime = recipe.value().getCookingTime();
            double div = 200.0 / recipe_cooktime;
            double i = regular / div;
            return (int)Math.max(1, i);
        }
        else
        {
            return 0;
        }

    }

    public ModConfigSpec.IntValue getCookTimeConfig() {
        return null;
    }

    protected int getAugment(ItemStack stack) {
        if (stack.getItem() instanceof ItemAugmentBlasting) {
            return 1;
        } else if (stack.getItem() instanceof ItemAugmentSmoking) {
            return 2;
        } else if (stack.getItem() instanceof ItemAugmentSpeed) {
            return 1;
        } else if (stack.getItem() instanceof ItemAugmentFuel) {
            return 2;
        } else if (stack.getItem() instanceof ItemAugmentFactory) {
            return 1;
        } else if (stack.getItem() instanceof ItemAugmentGenerator) {
            return 2;
        }
        return 0;
    }

    public void forceUpdateAllStates() {
        BlockState state = level.getBlockState(worldPosition);
        if (state.getValue(BlockStateProperties.LIT) != this.isBurning()) {
            level.setBlock(worldPosition, state.setValue(BlockStateProperties.LIT, this.isBurning()), 3);
        }
        if (state.getValue(BlockIronFurnaceBase.TYPE) != this.getStateType()) {
            level.setBlock(worldPosition, state.setValue(BlockIronFurnaceBase.TYPE, this.getStateType()), 3);
        }
        if (state.getValue(BlockIronFurnaceBase.JOVIAL) != jovial) {
            level.setBlock(worldPosition, state.setValue(BlockIronFurnaceBase.JOVIAL, jovial), 3);
        }
    }

    public void dropContents() {
        for (int i = 0; i <= 18; i++) {
            if (i < 3 || i > 5) {
                ItemStack stack = getItem(i);
                Containers.dropItemStack(level, (double) worldPosition.getX(), (double) worldPosition.getY(), (double) worldPosition.getZ(), stack);
            }
        }
    }

    public int getGeneration() {
        int rf = 0;
        if (this instanceof BlockCopperFurnaceTile) {
            rf = Config.copperFurnaceGeneration.get();
        } else if (this instanceof BlockIronFurnaceTile) {
            rf = Config.ironFurnaceGeneration.get();
        } else if (this instanceof BlockSilverFurnaceTile) {
            rf = Config.silverFurnaceGeneration.get();
        } else if (this instanceof BlockGoldFurnaceTile) {
            rf = Config.goldFurnaceGeneration.get();
        } else if (this instanceof BlockDiamondFurnaceTile) {
            rf = Config.diamondFurnaceGeneration.get();
        } else if (this instanceof BlockEmeraldFurnaceTile) {
            rf = Config.emeraldFurnaceGeneration.get();
        } else if (this instanceof BlockCrystalFurnaceTile) {
            rf = Config.crystalFurnaceGeneration.get();
        } else if (this instanceof BlockObsidianFurnaceTile) {
            rf = Config.obsidianFurnaceGeneration.get();
        } else if (this instanceof BlockNetheriteFurnaceTile) {
            rf = Config.netheriteFurnaceGeneration.get();
        } else if (this instanceof BlockMillionFurnaceTile) {
            rf = Config.millionFurnaceGeneration.get();
        } else if (this instanceof BlockAllthemodiumFurnaceTile) {
            rf = Config.allthemodiumGeneration.get();
        } else if (this instanceof BlockVibraniumFurnaceTile) {
            rf = Config.vibraniumGeneration.get();
        } else if (this instanceof BlockUnobtainiumFurnaceTile) {
            rf = Config.unobtainiumGeneration.get();
        }
        return getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed ? rf * 2 : getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel ? (int) (rf * 0.75) : rf;
    }

    public static int getSmokingBurn(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return 0;
        }
        else
        {
            Item item = stack.getItem();
            return SMOKING_BURNS.getOrDefault(item, addSmokingBurn(stack));
        }
    }

    public static int addSmokingBurn(ItemStack stack) {
        int burnTime = getSmokingBurnTime(stack);
        Item item = stack.getItem();
        SMOKING_BURNS.put(item, burnTime);
        return 0;
    }
    public static int getSmokingBurnTime(ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.get(DataComponents.FOOD) != null) {
                if (stack.get(DataComponents.FOOD).nutrition() > 0) {
                    return stack.get(DataComponents.FOOD).nutrition() * 800;
                }
            }
        }
        return 0;
    }

    public int getGeneratorBurn() {
        int burn = 0;
        if (getItem(AUGMENT_RED).getItem() instanceof ItemAugmentSmoking) {
            burn = getSmokingBurn(getItem(GENERATOR_FUEL));
        } else if (getItem(AUGMENT_RED).getItem() instanceof ItemAugmentBlasting) {
            if (!getItem(GENERATOR_FUEL).isEmpty()) {
                int energy = getRecipeGeneratorBlasting(getItem(GENERATOR_FUEL)).value().getEnergy();
                burn = energy / 20;
            }
        } else {
            burn = getBurnTime(getItem(GENERATOR_FUEL), RecipeType.SMELTING);
        }
        if (getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed) {
            burn /= 2;
        } else if (getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel) {
            burn *= 2;
        }
        return burn;
    }

    private boolean isItemGeneratorFuel(ItemStack stack) {
        int burn = 0;
        if (getItem(AUGMENT_RED).getItem() instanceof ItemAugmentSmoking) {
            burn = getSmokingBurn(stack);
        } else if (getItem(AUGMENT_RED).getItem() instanceof ItemAugmentBlasting) {
            if (!stack.isEmpty()) {
                RecipeHolder<? extends GeneratorRecipe> recipe = getRecipeGeneratorBlasting(stack);
                if (recipe != null)
                {
                    int energy = recipe.value().getEnergy();
                    burn = energy / 20;
                }
            }
        } else {
            burn = getBurnTime(stack, RecipeType.SMELTING);
        }
        if (getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed) {
            burn /= 2;
        } else if (getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel) {
            burn *= 2;
        }
        return burn > 0;
    }


    public boolean isFactoryCooking() {
        for (int i = 0; i < factoryCookTime.length; i++) {
            if (factoryCookTime[i] > 0) {
                return true;
            }
        }
        return false;
    }

    // ???????????????? what does this even do
    public Map<Integer, Integer> getSplitCounts(int[] slot, int[] input) {
        if (slot.length != input.length) {
            return null;
        }
        Map<Integer, Integer> output = Maps.newHashMap();
        double sum = 0;
        for (int i = 0; i < input.length; i++) {
            sum += input[i];
        }
        double splitted = sum / (double) input.length;
        if (sum % input.length != 0) {
            if (Math.floor(splitted) < splitted) {
                double lowest = Math.floor(sum / input.length) * input.length;
                int itemsLeftOver = (int) sum - (int) lowest;
                for (int i = 0; i < input.length; i++) {
                    if (itemsLeftOver > 0) {
                        input[i] = (int) Math.ceil(splitted);
                        itemsLeftOver--;
                    } else {
                        input[i] = (int) splitted;
                    }

                }
            }

        } else {
            for (int i = 0; i < input.length; i++) {
                input[i] = (int) splitted;
            }
        }
        for (int i = 0; i < input.length; i++) {
            output.put(slot[i], input[i]);
        }
        return output;
    }

    // lmao
    public void fillEmptySlots(int start, int size) {
        int amount = 0;
        for (int i = start; i < size; i++) {
            if (getItem(FACTORY_INPUT[i]).isEmpty()) {
                amount++;
            }
        }
        if (amount == 0) {
            return;
        }
        ItemStack stack = ItemStack.EMPTY;
        for (int j = start; j < size; j++) {
            if (!getItem(FACTORY_INPUT[j]).isEmpty()) {
                if (getItem(FACTORY_INPUT[j]).getCount() > 1 && amount > 0) {
                    if (amount >= getItem(FACTORY_INPUT[j]).getCount()) {
                        amount = getItem(FACTORY_INPUT[j]).getCount() - 1;
                    }
                    stack = getItem(FACTORY_INPUT[j]).copy();
                    getItem(FACTORY_INPUT[j]).shrink(amount);
                    for (int i = start; i < size; i++) {
                        if (getItem(FACTORY_INPUT[i]).isEmpty() && amount > 0) {
                            setItem(FACTORY_INPUT[i], stack.copyWithCount(1));
                            amount--;
                            setChanged();
                        }
                    }
                    setChanged();
                    break;
                }

            }
        }
    }


    // whaaat
    public void split(boolean fullCheck, int start, int size) {
        ItemStack itemToCheck = ItemStack.EMPTY;
        int fullCheckCount = 0;
        if (!fullCheck) {
            for (int i = start; i < size; i++) {
                if (getItem(FACTORY_INPUT[i]).isEmpty()) {
                    fullCheckCount++;
                }
            }
            if (fullCheckCount == 0) {
                return;
            }
        }
        for (int i = start; i < size; i++) {
            if (!getItem(FACTORY_INPUT[i]).isEmpty()) {
                itemToCheck = getItem(FACTORY_INPUT[i]).copy();
            }
        }
        if (!itemToCheck.isEmpty()) {
            fillEmptySlots(start, size);
        } else {
            return;
        }

        Map<Integer, Integer> items = Maps.newHashMap();
        Map<Integer, Integer> setCounts = Maps.newHashMap();

        for (int i = start; i < size; i++) {
            if (!getItem(FACTORY_INPUT[i]).isEmpty() && getItem(FACTORY_INPUT[i]).getItem() == itemToCheck.getItem()) {
                items.put(FACTORY_INPUT[i], getItem(FACTORY_INPUT[i]).getCount());
            }

        }
        if (items.isEmpty()) {
            return;
        }
        int[] slot = new int[items.size()];
        int[] input = new int[items.size()];
        int j = 0;
        for (Map.Entry<Integer, Integer> itemEntry : items.entrySet()) {
            slot[j] = itemEntry.getKey();
            input[j] = itemEntry.getValue();
            j++;
        }
        setCounts = getSplitCounts(slot, input);
        int check = 0;
        for (Map.Entry<Integer, Integer> countsEntry : setCounts.entrySet()) {
            int count = getItem(countsEntry.getKey()).getCount();
            if (count == countsEntry.getValue()) {
                check++;
            }

        }
        if (check == setCounts.size()) {
            return;
        }
        for (Map.Entry<Integer, Integer> countsEntry : setCounts.entrySet()) {
            ItemStack newStack = getItem(countsEntry.getKey()).copy();
            newStack.setCount(countsEntry.getValue());
            setItem(countsEntry.getKey(), newStack);
            setChanged();
        }


    }


    boolean rainbowCheckFurnaceTiers(List<BlockIronFurnaceTileBase> list)
    {
        if (list.isEmpty())
        {
            return false;
        }
        int check = 0;
        for (BlockIronFurnaceTileBase furnace : list) {
            if (furnace.generatorBurn > 0 && furnace.getEnergy() < furnace.getCapacity()) {
                check++;
            }
        }
        if (check == 0)
        {
            return false;
        }
        return true;
    }

    public static void tick(Level level, BlockPos worldPosition, BlockState blockState, BlockIronFurnaceTileBase e) {
            if (!e.level.isClientSide) {
                if (e.isGenerator())
                {


                    boolean flag3 = false;
                    List<BlockIronFurnaceTileBase> iron = new ArrayList<>();
                    List<BlockIronFurnaceTileBase> gold = new ArrayList<>();
                    List<BlockIronFurnaceTileBase> diamond = new ArrayList<>();
                    List<BlockIronFurnaceTileBase> emerald = new ArrayList<>();
                    List<BlockIronFurnaceTileBase> obsidian = new ArrayList<>();
                    List<BlockIronFurnaceTileBase> crystal = new ArrayList<>();
                    List<BlockIronFurnaceTileBase> netherite = new ArrayList<>();
                    List<BlockIronFurnaceTileBase> copper = new ArrayList<>();
                    List<BlockIronFurnaceTileBase> silver = new ArrayList<>();
                    List<BlockIronFurnaceTileBase> rainbow = new ArrayList<>();

                    if (e instanceof BlockMillionFurnaceTile) {
                        BlockMillionFurnaceTile furnaceTile = (BlockMillionFurnaceTile) e;
                        if (furnaceTile.owner != null)
                        {
                            flag3 = true;
                            if (level.getPlayerByUUID(furnaceTile.owner) != null)
                            {

                                List<BlockPos> furnacesBlockPos = level.getPlayerByUUID(furnaceTile.owner).getData(Registration.PLAYER_FURNACES_LIST.get()).furnacesList.get();
                                if (!furnacesBlockPos.isEmpty())
                                {

                                    for (int i = 0; i < furnacesBlockPos.size(); i++)
                                    {
                                        level.getChunkAt(furnacesBlockPos.get(i)).setLoaded(true);
                                        BlockEntity be = level.getBlockEntity(furnacesBlockPos.get(i));
                                        if (be != null)
                                        {
                                            if (be instanceof BlockIronFurnaceTileBase)
                                            {
                                                BlockIronFurnaceTileBase te = (BlockIronFurnaceTileBase)be;
                                                if (te instanceof BlockIronFurnaceTile)
                                                {
                                                    iron.add((BlockIronFurnaceTile) te);
                                                }
                                                if (te instanceof BlockGoldFurnaceTile)
                                                {
                                                    gold.add((BlockGoldFurnaceTile) te);
                                                }
                                                if (te instanceof BlockDiamondFurnaceTile)
                                                {
                                                    diamond.add((BlockDiamondFurnaceTile) te);
                                                }

                                                if (te instanceof BlockEmeraldFurnaceTile)
                                                {
                                                    emerald.add((BlockEmeraldFurnaceTile) te);
                                                }
                                                if (te instanceof BlockObsidianFurnaceTile)
                                                {
                                                    obsidian.add((BlockObsidianFurnaceTile) te);
                                                }
                                                if (te instanceof BlockCrystalFurnaceTile)
                                                {
                                                    crystal.add((BlockCrystalFurnaceTile) te);
                                                }

                                                if (te instanceof BlockNetheriteFurnaceTile)
                                                {
                                                    netherite.add((BlockNetheriteFurnaceTile) te);
                                                }
                                                if (te instanceof BlockCopperFurnaceTile)
                                                {
                                                    copper.add((BlockCopperFurnaceTile) te);
                                                }
                                                if (te instanceof BlockSilverFurnaceTile)
                                                {
                                                    silver.add((BlockSilverFurnaceTile) te);
                                                }
                                                if (te instanceof BlockMillionFurnaceTile)
                                                {
                                                    rainbow.add((BlockMillionFurnaceTile) te);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (rainbow.size() > 1)
                        {
                            int rainbowGens = 0;
                            for (int i = 0; i < rainbow.size(); i++)
                            {
                                if (rainbow.get(i).isGenerator())
                                {
                                    rainbowGens++;
                                }
                            }
                            if (rainbowGens > 1)
                            {
                                flag3 = false;
                            }
                        }
                        if (flag3
                                && e.rainbowCheckFurnaceTiers(iron)
                                && e.rainbowCheckFurnaceTiers(gold)
                                && e.rainbowCheckFurnaceTiers(diamond)
                                && e.rainbowCheckFurnaceTiers(emerald)
                                && e.rainbowCheckFurnaceTiers(obsidian)
                                && e.rainbowCheckFurnaceTiers(crystal)
                                && e.rainbowCheckFurnaceTiers(netherite)
                                && e.rainbowCheckFurnaceTiers(copper)
                                && e.rainbowCheckFurnaceTiers(silver)
                        ) {
                            e.rainbowGenerating = flag3;
                            BlockState state = level.getBlockState(worldPosition);
                            if (state.getValue(BlockMillionFurnace.RAINBOW_GENERATING) != e.rainbowGenerating) {
                                level.setBlock(worldPosition, state.setValue(BlockMillionFurnace.RAINBOW_GENERATING, e.rainbowGenerating), 3);
                            }
                            e.rainbowEnergyOut();
                        }
                        else
                        {
                            e.rainbowGenerating = false;
                            BlockState state = level.getBlockState(worldPosition);
                            if (state.getValue(BlockMillionFurnace.RAINBOW_GENERATING) != e.rainbowGenerating) {
                                level.setBlock(worldPosition, state.setValue(BlockMillionFurnace.RAINBOW_GENERATING, e.rainbowGenerating), 3);
                            }
                        }
                    }
                }
            }


        boolean flag1 = false;
        boolean wasBurning = e.isBurning();
        if (e.furnaceSettings.size() <= 0) {
            e.furnaceSettings = new FurnaceSettings() {
                @Override
                public void onChanged() {
                    e.setChanged();
                }
            };
        }
        for (int i = 3; i <= 5; i++) {
            if (e.currentAugment[i - 3] != e.getAugment(e.getItem(i))) {
                e.currentAugment[i - 3] = e.getAugment(e.getItem(i));
                e.furnaceBurnTime = 0;
                e.generatorBurn = 0;
                if (i - 3 == 2 || (e.isGenerator() && i - 3 == 0)) {
                    e.dropContents();
                }

            }
        }
        if (!e.level.isClientSide) {

            if (e.getCapacity() != e.getCapacityFromTier()) {
                e.setMaxEnergy(e.getCapacityFromTier());
            }
            if (e.totalCookTime != e.getCookTime()) {
                e.totalCookTime = e.getCookTime();
            }
            int mode = e.getRedstoneSetting();
            if (mode != 0) {
                if (mode == 2) {
                    int i = 0;
                    for (Direction side : Direction.values()) {
                        if (level.getSignal(worldPosition.offset(side.getNormal()), side) > 0) {
                            i++;
                        }
                    }
                    if (i != 0) {
                        e.cookTime = 0;
                        e.furnaceBurnTime = 0;
                        e.forceUpdateAllStates();
                        return;
                    }
                }
                if (mode == 1) {
                    boolean flag = false;
                    for (Direction side : Direction.values()) {

                        if (level.getSignal(worldPosition.offset(side.getNormal()), side) > 0) {
                            flag = true;
                        }
                    }
                    if (!flag) {
                        e.cookTime = 0;
                        e.furnaceBurnTime = 0;
                        e.forceUpdateAllStates();
                        return;
                    }
                }
                for (int i = 0; i < Direction.values().length; i++)
                    e.provides[i] = e.getBlockState().getDirectSignal(e.level, worldPosition, DirectionUtil.fromId(i));

            } else {
                for (int i = 0; i < Direction.values().length; i++)
                    e.provides[i] = 0;
            }
            if (e.doesNeedUpdateSend()) {
                e.onUpdateSent();
            }


        }



        if (e.isFactory()) {
            if (!e.level.isClientSide) {

                if (!e.energyStorage.canReceive()) {
                    e.energyStorage.setMaxReceive(e.energyStorage.getMaxEnergyStored());
                }
                if (e.energyStorage.canExtract()) {
                    e.energyStorage.setMaxExtract(0);
                }
                e.checkRecipeType();
                int start = e.getTier() == 0 ? 2 : e.getTier() == 1 ? 1 : 0;
                int size = e.getTier() == 0 ? 4 : e.getTier() == 1 ? 5 : 6;
                if (e.isAutoSplit()) {
                    e.split(false, start, size);
                }
                for (int i = start; i < size; i++) {
                    int slot = FACTORY_INPUT[i];
                    if (e.factoryTotalCookTime[i] != e.getFactoryCookTime(slot)) {
                        e.factoryTotalCookTime[i] = e.getFactoryCookTime(slot);
                    }
                    if (!e.getItem(slot).isEmpty()) {
                        RecipeHolder<? extends AbstractCookingRecipe> irecipe = e.getRecipeNonCached(e.getItem(slot));
                        boolean valid = e.canFactorySmelt(irecipe, slot);
                        if (valid) {
                            int energyRecipe = irecipe.value().getCookingTime() * 20;
                            int energy = e.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed ?
                                    energyRecipe * 2 : e.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel ?
                                    energyRecipe / 2 : energyRecipe;
                            if (e.getEnergy() >= energy || e.factoryCookTime[i] > 0) {
                                e.factoryCookTime[i]++;
                                e.usedRF[i] += (double) (energy / e.factoryTotalCookTime[i]);
                                e.setEnergy((int) (e.getEnergy() - (double) (energy / e.factoryTotalCookTime[i])));
                                if (level.getBlockState(e.getBlockPos()).getValue(BlockStateProperties.LIT) != e.isFactoryCooking()) {
                                    level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.LIT, e.isFactoryCooking()), 3);
                                }
                                if (e.factoryCookTime[i] >= e.factoryTotalCookTime[i]) {
                                    e.factoryCookTime[i] = 0;
                                    if (e.usedRF[i] < energy) {
                                        double diff = energy - e.usedRF[i];
                                        e.setEnergy((int) (e.getEnergy() - diff));
                                    }
                                    e.usedRF[i] = 0;
                                    e.factoryTotalCookTime[i] = e.getFactoryCookTime(slot);
                                    if (e.isAutoSplit()) {
                                        e.split(true, start, size);
                                    }
                                    e.factorySmelt(irecipe, slot);
                                    e.autoFactoryIO();
                                    e.setChanged();
                                }
                            }
                        }
                    } else {
                        e.factoryCookTime[i] = 0;
                        if (level.getBlockState(e.getBlockPos()).getValue(BlockStateProperties.LIT) != e.isFactoryCooking()) {
                            level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.LIT, e.isFactoryCooking()), 3);
                        }
                    }

                }
                if (e.level.getGameTime() % 24 == 0) {
                    BlockState state = level.getBlockState(worldPosition);
                    if (state.getValue(BlockIronFurnaceBase.TYPE) != e.getStateType()) {
                        level.setBlock(worldPosition, state.setValue(BlockIronFurnaceBase.TYPE, e.getStateType()), 3);
                    }
                    if (state.getValue(BlockIronFurnaceBase.JOVIAL) != e.jovial) {
                        level.setBlock(worldPosition, state.setValue(BlockIronFurnaceBase.JOVIAL, e.jovial), 3);
                    }
                    for (int i = 0; i < e.factoryCookTime.length; i++) {
                        if (e.factoryCookTime[i] <= 0) {
                            for (int j = 0; j < FACTORY_INPUT.length; j++) {
                                if (e.getItem(FACTORY_INPUT[j]).isEmpty()) {
                                    e.autoFactoryIO();
                                    e.setChanged();
                                } else if (e.getItem(FACTORY_INPUT[j]).getCount() < e.getItem(FACTORY_INPUT[j]).getMaxStackSize()) {
                                    e.autoFactoryIO();
                                    e.setChanged();
                                }
                            }
                            for (int j = 0; j < FACTORY_INPUT.length; j++)
                            {
                                int outputSlot = FACTORY_INPUT[j] + 6;
                                if (!e.getItem(outputSlot).isEmpty() && e.getItem(outputSlot).getCount() >= 64)
                                {
                                    e.autoFactoryIO();
                                }
                            }

                        }
                    }
                }
            }
        } else if (e.isGenerator()) {
            if (!level.isClientSide) {
                if (e.energyStorage.canReceive()) {
                    e.energyStorage.setMaxReceive(0);
                }
                if (!e.energyStorage.canExtract()) {
                    e.energyStorage.setMaxExtract(e.energyStorage.getMaxEnergyStored());
                }
                if (e.getEnergy() < e.getCapacity()) {
                    if (!e.getItem(GENERATOR_FUEL).isEmpty() && e.generatorBurn <= 0) {
                        if (e.getGeneratorBurn() > 0)
                        {
                            e.generatorBurn = e.getGeneratorBurn();
                            e.generatorRecentRecipeRF = (int) e.generatorBurn;
                            if (e.getItem(GENERATOR_FUEL).hasCraftingRemainingItem())
                                e.setItem(GENERATOR_FUEL, e.getItem(GENERATOR_FUEL).getCraftingRemainingItem());
                            else if (!e.getItem(GENERATOR_FUEL).isEmpty()) {
                                e.getItem(GENERATOR_FUEL).shrink(1);
                                if (e.getItem(GENERATOR_FUEL).isEmpty()) {
                                    e.setItem(GENERATOR_FUEL, e.getItem(GENERATOR_FUEL).getCraftingRemainingItem());
                                }
                            }
                        }

                        e.setChanged();
                    }
                    if (e.isGenerator()) {
                        if (level.getBlockState(e.getBlockPos()).getValue(BlockStateProperties.LIT) != e.generatorBurn > 0) {
                            level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.LIT, e.generatorBurn > 0), 3);
                        }
                    }
                    if (e.generatorBurn > 0) {
                        double max = e.generatorRecentRecipeRF * 20;
                        e.gottenRF += e.getGeneration();

                        e.setEnergy(e.getEnergy() + e.getGeneration());
                        if (e.generatorBurn - (double) ((double) e.getGeneration() / 20) <= 0) {
                            if (e.gottenRF + e.getGeneration() > max && e.gottenRF + e.getGeneration() < e.getCapacity()) {
                                int diff = (int) (e.gottenRF + e.getGeneration() - max);
                                e.setEnergy(e.getEnergy() + e.getGeneration());
                                e.removeEnergy(diff);
                            }
                            if (e.gottenRF + e.getGeneration() < max) {
                                int diff = (int) (max - e.gottenRF + e.getGeneration());
                                e.setEnergy(e.getEnergy() + e.getGeneration());
                                e.setEnergy(e.getEnergy() + diff);
                            }
                            e.gottenRF = 0;
                        }
                        e.generatorBurn -= (double) ((double) e.getGeneration() / 20);
                        if (e.generatorBurn <= 0) {
                            e.autoIOGenerator();
                            e.generatorBurn = 0;
                        }
                    }
                }
                if (e.generatorBurn <= 0) {
                    e.generatorBurn = 0;
                }


                e.energyOut();
                if (e.level.getGameTime() % 24 == 0) {

                    if (e.generatorBurn <= 0) {
                        if (e.getItem(GENERATOR_FUEL).isEmpty()) {
                            e.autoIOGenerator();
                            e.setChanged();
                        } else if (e.getItem(GENERATOR_FUEL).getCount() < e.getItem(GENERATOR_FUEL).getMaxStackSize()) {
                            e.autoIOGenerator();
                            e.setChanged();
                        }
                    }


                }


            }
            if (e.level.getGameTime() % 24 == 0) {
                BlockState state = level.getBlockState(worldPosition);
                if (state.getValue(BlockIronFurnaceBase.TYPE) != e.getStateType()) {
                    level.setBlock(worldPosition, state.setValue(BlockIronFurnaceBase.TYPE, e.getStateType()), 3);
                }
                if (state.getValue(BlockIronFurnaceBase.JOVIAL) != e.jovial) {
                    level.setBlock(worldPosition, state.setValue(BlockIronFurnaceBase.JOVIAL, e.jovial), 3);
                }
            }


        } else if (e.isFurnace()) {
            if (e.energyStorage.canReceive()) {
                e.energyStorage.setMaxReceive(0);
            }
            if (e.energyStorage.canExtract()) {
                e.energyStorage.setMaxExtract(0);
            }

            if (!e.level.isClientSide) {
                if (e.isBurning()) {
                    --e.furnaceBurnTime;
                }
                e.checkRecipeType();

                ItemStack itemstack = e.getItem(FUEL);
                if (e.isBurning() || !itemstack.isEmpty() && !e.getItem(INPUT).isEmpty()) {
                    RecipeHolder<? extends AbstractCookingRecipe> irecipe = null;
                    if (!e.getItem(INPUT).isEmpty()) {
                        irecipe = e.getRecipeNonCached(e.getItem(INPUT));
                    }

                    boolean valid = e.canSmelt(irecipe);
                    if (!e.isBurning() && valid) {
                        if (itemstack.getItem() instanceof ItemHeater) {
                            CustomData data = itemstack.get(DataComponents.CUSTOM_DATA);
                            if (data != null)
                            {
                                CompoundTag tag = data.copyTag();
                                int x = tag.getInt("HeaterPosX");
                                int y = tag.getInt("HeaterPosY");
                                int z = tag.getInt("HeaterPosZ");

                                BlockEntity te = level.getBlockEntity(new BlockPos(x, y, z));
                                if (te != null)
                                {
                                    if (te instanceof BlockWirelessEnergyHeaterTile) {
                                        int energy = ((BlockWirelessEnergyHeaterTile) te).getEnergy();
                                        if (energy >= 2000) {
                                            if (!e.getItem(AUGMENT_GREEN).isEmpty() && e.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel) {
                                                e.furnaceBurnTime = 400 * e.getCookTime() / 200;
                                            } else if (!e.getItem(AUGMENT_GREEN).isEmpty() && e.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed) {
                                                if (energy >= 4000) {
                                                    e.furnaceBurnTime = 100 * e.getCookTime() / 200;
                                                }
                                            } else {
                                                e.furnaceBurnTime = 200 * e.getCookTime() / 200;
                                            }
                                            if (e.furnaceBurnTime > 0)
                                                ((BlockWirelessEnergyHeaterTile) te).removeEnergy(2000);

                                            e.recipesUsed = e.furnaceBurnTime;
                                        }
                                    }
                                }
                            }



                        } else {
                            if (!e.getItem(AUGMENT_GREEN).isEmpty())
                            {
                                if (e.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel)
                                {
                                    e.furnaceBurnTime = (getBurnTime(itemstack, e.recipeType) * e.getCookTime() / 200) * 2;
                                }
                                else if (e.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed)
                                {
                                    e.furnaceBurnTime = (getBurnTime(itemstack, e.recipeType) * e.getCookTime() / 200) / 2;

                                }
                            }
                            else
                            {
                                e.furnaceBurnTime = getBurnTime(itemstack, e.recipeType) * e.getCookTime() / 200;
                            }
                            e.recipesUsed = e.furnaceBurnTime;
                        }
                        if (e.isBurning()) {
                            flag1 = true;
                            if (!(itemstack.getItem() instanceof ItemHeater)) {
                                if (itemstack.hasCraftingRemainingItem())
                                    e.setItem(FUEL, itemstack.getCraftingRemainingItem());
                                else if (!itemstack.isEmpty()) {
                                    itemstack.shrink(1);
                                    if (itemstack.isEmpty()) {
                                        e.setItem(FUEL, itemstack.getCraftingRemainingItem());
                                    }
                                }
                            }
                        }
                    }
                    if (e.isBurning() && valid) {
                        ++e.cookTime;
                        if (e.cookTime >= e.totalCookTime) {
                            e.cookTime = 0;
                            e.totalCookTime = e.getCookTime();
                            e.smelt(irecipe);
                            e.autoIO();
                            flag1 = true;
                        }
                    } else {
                        e.cookTime = 0;
                    }
                } else if (!e.isBurning() && e.cookTime > 0) {
                    e.cookTime = clamp(e.cookTime - 2, 0, e.totalCookTime);
                }
                if (e.level.getGameTime() % 24 == 0) {

                    if (e.cookTime <= 0) {

                        if (e.getItem(INPUT).isEmpty()) {
                            e.autoIO();
                            flag1 = true;
                        } else if (e.getItem(INPUT).getCount() < e.getItem(INPUT).getMaxStackSize()) {
                            e.autoIO();
                            flag1 = true;
                        }
                        if (e.getItem(FUEL).isEmpty()) {
                            e.autoIO();
                            flag1 = true;
                        } else if (e.getItem(FUEL).getCount() < e.getItem(FUEL).getMaxStackSize()) {
                            e.autoIO();
                            flag1 = true;
                        }

                        if (!e.getItem(OUTPUT).isEmpty() && e.getItem(OUTPUT).getCount() >= 64)
                        {
                            e.autoIO();
                        }
                    }


                }
            }
            if (wasBurning != e.isBurning()) {
                level.setBlock(worldPosition, level.getBlockState(e.worldPosition).setValue(BlockStateProperties.LIT, e.isBurning()), 3);
            }
            if (e.level.getGameTime() % 24 == 0) {
                BlockState state = level.getBlockState(worldPosition);
                if (state.getValue(BlockIronFurnaceBase.TYPE) != e.getStateType()) {
                    level.setBlock(worldPosition, state.setValue(BlockIronFurnaceBase.TYPE, e.getStateType()), 3);
                }
                if (state.getValue(BlockIronFurnaceBase.JOVIAL) != e.jovial) {
                    level.setBlock(worldPosition, state.setValue(BlockIronFurnaceBase.JOVIAL, e.jovial), 3);
                }
            }

            if (flag1) {
                e.setChanged();
            }


        }
    }

    public static int clamp(int p_76125_0_, int p_76125_1_, int p_76125_2_) {
        if (p_76125_0_ < p_76125_1_) {
            return p_76125_1_;
        } else {
            return p_76125_0_ > p_76125_2_ ? p_76125_2_ : p_76125_0_;
        }
    }

    protected int getCapacityFromTier() {
        return switch (getTier()) {
            case 1 -> Config.furnaceEnergyCapacityTier1.get();
            case 2 -> Config.furnaceEnergyCapacityTier2.get();
            default -> Config.furnaceEnergyCapacityTier0.get();
        };
    }

    protected void rainbowEnergyOut()
    {
        Map<BlockEntity, Direction> tiles = Maps.newHashMap();
        for (Direction dir : Direction.values()) {
            BlockEntity tile = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
            if (tile == null) {
                continue;
            }
            if (furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3) {
                IEnergyStorage other = level.getCapability(Capabilities.EnergyStorage.BLOCK, tile.getBlockPos(), dir.getOpposite());
                if (other == null) {
                    continue;
                }
                if (other.canReceive() && other.getEnergyStored() < other.getMaxEnergyStored()) {
                    tiles.put(tile, dir.getOpposite());
                }
            }
        }
        for (Map.Entry<BlockEntity, Direction> entry : tiles.entrySet()) {
            int energy = Config.millionFurnacePowerToGenerate.get() / tiles.size();
            IEnergyStorage other = level.getCapability(Capabilities.EnergyStorage.BLOCK, entry.getKey().getBlockPos(), entry.getValue());
            if (other != null)
            {
                other.receiveEnergy(energy, false);
            }
        }
    }

    protected void energyOut() {

        Map<BlockEntity, Direction> tiles = Maps.newHashMap();
        for (Direction dir : Direction.values()) {
            BlockEntity tile = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
            if (tile == null) {
                continue;
            }
            if (furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3) {
                IEnergyStorage other = level.getCapability(Capabilities.EnergyStorage.BLOCK, tile.getBlockPos(), dir.getOpposite());
                if (other == null) {
                    continue;
                }
                if (other.canReceive() && other.getEnergyStored() < other.getMaxEnergyStored()) {

                    tiles.put(tile, dir.getOpposite());
                }
            }
        }
        for (Map.Entry<BlockEntity, Direction> entry : tiles.entrySet()) {
            IEnergyStorage other = level.getCapability(Capabilities.EnergyStorage.BLOCK, entry.getKey().getBlockPos(), entry.getValue());
            int energy = Math.min(energyStorage.getMaxExtract(), getEnergy());
            if (other != null)
            {
                removeEnergy(other.receiveEnergy(energy, false));

            }
        }
    }

    protected void autoIO() {
        for (Direction dir : Direction.values()) {
            BlockEntity tile = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
            if (tile == null) {
                continue;
            }
            if (furnaceSettings.get(dir.ordinal()) == 1 || furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3 || furnaceSettings.get(dir.ordinal()) == 4) {
                if (tile != null) {
                    IItemHandler other = level.getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), dir.getOpposite());

                    if (other == null) {
                        continue;
                    }
                    if (other != null) {
                        if (this.getAutoInput() != 0 || this.getAutoOutput() != 0) {
                            if (this.getAutoInput() == 1) {
                                if (furnaceSettings.get(dir.ordinal()) == 1 || furnaceSettings.get(dir.ordinal()) == 3) {
                                    if (this.getItem(INPUT).getCount() >= this.getItem(INPUT).getMaxStackSize()) {
                                        continue;
                                    }
                                    for (int i = 0; i < other.getSlots(); i++) {
                                        if (other.getStackInSlot(i).isEmpty()) {
                                            continue;
                                        }
                                        ItemStack stack = other.extractItem(i, other.getStackInSlot(i).getMaxStackSize(), true);
                                        if (hasRecipe(stack) && getItem(INPUT).isEmpty() || ItemStack.isSameItemSameComponents(getItem(INPUT), stack)) {
                                            insertItemInternal(INPUT, other.extractItem(i, other.getStackInSlot(i).getMaxStackSize() - this.getItem(INPUT).getCount(), false), false);
                                        }
                                    }
                                }
                                if (furnaceSettings.get(dir.ordinal()) == 4) {
                                    if (this.getItem(FUEL).getCount() >= this.getItem(FUEL).getMaxStackSize()) {
                                        continue;
                                    }
                                    for (int i = 0; i < other.getSlots(); i++) {
                                        if (other.getStackInSlot(i).isEmpty()) {
                                            continue;
                                        }
                                        if (!isItemFuel(other.getStackInSlot(i), recipeType)) {
                                            continue;
                                        }
                                        ItemStack stack = other.extractItem(i, other.getStackInSlot(i).getMaxStackSize(), true);
                                        if (isItemFuel(stack, recipeType) && getItem(FUEL).isEmpty() || ItemStack.isSameItemSameComponents(getItem(FUEL), stack)) {
                                            insertItemInternal(FUEL, other.extractItem(i, other.getStackInSlot(i).getMaxStackSize() - this.getItem(FUEL).getCount(), false), false);
                                        }
                                    }
                                }
                            }
                            if (this.getAutoOutput() == 1) {
                                if (furnaceSettings.get(dir.ordinal()) == 4) {
                                    if (this.getItem(FUEL).isEmpty()) {
                                        continue;
                                    }
                                    if (isItemFuel(this.getItem(FUEL), recipeType)) {
                                        continue;
                                    }
                                    for (int i = 0; i < other.getSlots(); i++) {
                                        ItemStack stack = extractItemInternal(FUEL, other.getSlotLimit(i) - other.getStackInSlot(i).getCount(), true);
                                        if (other.isItemValid(i, stack) && (other.getStackInSlot(i).isEmpty() || (ItemStack.isSameItemSameComponents(other.getStackInSlot(i), stack) && other.getStackInSlot(i).getCount() + stack.getCount() <= other.getSlotLimit(i)))) {
                                            boolean check = other.insertItem(i, extractItemInternal(FUEL, stack.getCount(), true), true).isEmpty();
                                            if (check) other.insertItem(i, extractItemInternal(FUEL, stack.getCount(), false), false);
                                        }
                                    }
                                }

                                if (furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3) {
                                    if (this.getItem(OUTPUT).isEmpty()) {
                                        continue;
                                    }
                                    for (int i = 0; i < other.getSlots(); i++) {
                                        ItemStack stack = extractItemInternal(OUTPUT, other.getSlotLimit(i) - other.getStackInSlot(i).getCount(), true);
                                        if (other.isItemValid(i, stack) && (other.getStackInSlot(i).isEmpty() || (ItemStack.isSameItemSameComponents(other.getStackInSlot(i), stack) && other.getStackInSlot(i).getCount() + stack.getCount() <= other.getSlotLimit(i)))) {
                                            boolean check = other.insertItem(i, extractItemInternal(OUTPUT, stack.getCount(), true), true).isEmpty();
                                            if (check) other.insertItem(i, extractItemInternal(OUTPUT, stack.getCount(), false), false);
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void autoIOGenerator() {
        for (Direction dir : Direction.values()) {
            BlockEntity tile = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
            if (tile == null) {
                continue;
            }
            if (furnaceSettings.get(dir.ordinal()) == 4) {
                if (tile != null) {
                    IItemHandler other = level.getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), dir.getOpposite());

                    if (other == null) {
                        continue;
                    }
                    if (other != null) {
                        if (this.getAutoInput() != 0) {
                            if (furnaceSettings.get(dir.ordinal()) == 4) {
                                if (this.getItem(GENERATOR_FUEL).getCount() >= this.getItem(GENERATOR_FUEL).getMaxStackSize()) {
                                    continue;
                                }
                                for (int i = 0; i < other.getSlots(); i++) {
                                    if (other.getStackInSlot(i).isEmpty()) {
                                        continue;
                                    }
                                    if (other.getStackInSlot(i).getItem() == Items.BUCKET)
                                    {
                                        continue;
                                    }
                                    ItemStack stack = other.extractItem(i, other.getStackInSlot(i).getMaxStackSize(), true);
                                    if (stack.getItem() instanceof ItemHeater)
                                    {
                                        continue;
                                    }
                                    if (isItemGeneratorFuel(stack) && getItem(GENERATOR_FUEL).isEmpty() || ItemStack.isSameItemSameComponents(getItem(GENERATOR_FUEL), stack)) {
                                        int count = other.getStackInSlot(i).getMaxStackSize() - this.getItem(GENERATOR_FUEL).getCount();
                                        if (stack.hasCraftingRemainingItem())
                                        {
                                            if (!getItem(GENERATOR_FUEL).isEmpty())
                                            {
                                                continue;
                                            }
                                            count = 1;
                                        }
                                        insertItemInternal(GENERATOR_FUEL, other.extractItem(i, count, false), false);
                                    }
                                }
                            }
                        }
                        if (this.getAutoOutput() != 0)
                        {
                            if (furnaceSettings.get(dir.ordinal()) == 4)
                            {
                                if (this.getItem(GENERATOR_FUEL).isEmpty()) {
                                    continue;
                                }
                                if (!isItemGeneratorFuel(getItem(GENERATOR_FUEL))) {
                                    for (int i = 0; i < other.getSlots(); i++) {
                                        ItemStack stack = extractItemInternal(GENERATOR_FUEL, this.getItem(GENERATOR_FUEL).getMaxStackSize() - other.getStackInSlot(i).getCount(), true);
                                        if (other.isItemValid(i, stack) && (other.getStackInSlot(i).isEmpty() || (ItemStack.isSameItemSameComponents(other.getStackInSlot(i), stack) && other.getStackInSlot(i).getCount() + stack.getCount() <= other.getSlotLimit(i)))) {
                                            boolean check = other.insertItem(i, extractItemInternal(GENERATOR_FUEL, stack.getCount(), true), true).isEmpty();
                                            if (check) other.insertItem(i, extractItemInternal(GENERATOR_FUEL, stack.getCount(), false), false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void autoFactoryIO() {
        for (Direction dir : Direction.values()) {
            BlockEntity tile = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
            if (tile == null) {
                continue;
            }
            if (furnaceSettings.get(dir.ordinal()) == 1 || furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3) {
                if (tile != null) {
                    IItemHandler other = level.getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), dir.getOpposite());

                    if (other == null) {
                        continue;
                    }
                    if (other != null) {
                        if (this.getAutoInput() != 0 || this.getAutoOutput() != 0) {
                            if (this.getAutoInput() == 1) {
                                if (furnaceSettings.get(dir.ordinal()) == 1 || furnaceSettings.get(dir.ordinal()) == 3) {
                                    int start = getTier() == 0 ? 2 : getTier() == 1 ? 1 : 0;
                                    int size = getTier() == 0 ? 4 : getTier() == 1 ? 5 : 6;

                                    for (int j = start; j < size; j++) {
                                        if (this.getItem(FACTORY_INPUT[j]).getCount() >= this.getItem(FACTORY_INPUT[j]).getMaxStackSize()) {
                                            continue;
                                        }
                                        for (int i = 0; i < other.getSlots(); i++) {
                                            if (other.getStackInSlot(i).isEmpty()) {
                                                continue;
                                            }
                                            ItemStack stack = other.extractItem(i, other.getStackInSlot(i).getMaxStackSize(), true);
                                            if (hasRecipe(stack) && getItem(FACTORY_INPUT[j]).isEmpty() || ItemStack.isSameItemSameComponents(getItem(FACTORY_INPUT[j]), stack)) {
                                                insertItemInternal(FACTORY_INPUT[j], other.extractItem(i, other.getStackInSlot(i).getMaxStackSize() - this.getItem(FACTORY_INPUT[j]).getCount(), false), false);
                                            }
                                        }
                                    }

                                }
                            }
                            if (this.getAutoOutput() == 1) {

                                if (furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3) {
                                    int start = getTier() == 0 ? 2 : getTier() == 1 ? 1 : 0;
                                    int size = getTier() == 0 ? 4 : getTier() == 1 ? 5 : 6;
                                    for (int j = start; j < size; j++) {
                                        if (this.getItem(FACTORY_INPUT[j] + 6).isEmpty()) {
                                            continue;
                                        }

                                        for (int i = 0; i < other.getSlots(); i++) {
                                            ItemStack stack = extractItemInternal(FACTORY_INPUT[j] + 6, other.getSlotLimit(i) - other.getStackInSlot(i).getCount(), true);
                                            if (other.isItemValid(i, stack) && (other.getStackInSlot(i).isEmpty() || (ItemStack.isSameItemSameComponents(other.getStackInSlot(i), stack) && other.getStackInSlot(i).getCount() + stack.getCount() <= other.getSlotLimit(i)))) {
                                                boolean check = other.insertItem(i, extractItemInternal(FACTORY_INPUT[j] + 6, stack.getCount(), true), true).isEmpty();
                                                if (check) other.insertItem(i, extractItemInternal(FACTORY_INPUT[j] + 6, stack.getCount(), false), false);
                                            }
                                        }
                                    }


                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Nonnull
    public ItemStack insertItemInternal(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;
        if (!canPlaceItemThroughFace(slot, stack, null))
            return stack;

        ItemStack existing = this.getItem(slot);

        int limit = stack.getMaxStackSize();

        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.setItem(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            this.setChanged();
        }

        return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
    }


    @Nonnull
    private ItemStack extractItemInternal(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack existing = this.getItem(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.setItem(slot, ItemStack.EMPTY);
                this.setChanged();
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.setItem(slot, existing.copyWithCount(existing.getCount() - toExtract));
                this.setChanged();
            }

            return existing.copyWithCount(toExtract);
        }
    }

    //CLIENT SYNC

    public boolean isAutoSplit() {
        return furnaceSettings.autoSplit == 1;
    }

    public int getSettingBottom() {
        return furnaceSettings.get(0);
    }

    public int getSettingTop() {
        return furnaceSettings.get(1);
    }

    public int getSettingFront() {
        int i = DirectionUtil.getId(this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
        return furnaceSettings.get(i);
    }

    public int getSettingBack() {
        int i = DirectionUtil.getId(this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite());
        return furnaceSettings.get(i);
    }

    public int getSettingLeft() {
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (facing == Direction.NORTH) {
            return furnaceSettings.get(DirectionUtil.getId(Direction.EAST));
        } else if (facing == Direction.WEST) {
            return furnaceSettings.get(DirectionUtil.getId(Direction.NORTH));
        } else if (facing == Direction.SOUTH) {
            return furnaceSettings.get(DirectionUtil.getId(Direction.WEST));
        } else {
            return furnaceSettings.get(DirectionUtil.getId(Direction.SOUTH));
        }
    }

    public int getSettingRight() {
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (facing == Direction.NORTH) {
            return furnaceSettings.get(DirectionUtil.getId(Direction.WEST));
        } else if (facing == Direction.WEST) {
            return furnaceSettings.get(DirectionUtil.getId(Direction.SOUTH));
        } else if (facing == Direction.SOUTH) {
            return furnaceSettings.get(DirectionUtil.getId(Direction.EAST));
        } else {
            return furnaceSettings.get(DirectionUtil.getId(Direction.NORTH));
        }
    }

    public int getIndexFront() {
        int i = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).ordinal();
        return i;
    }

    public int getIndexBack() {
        int i = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite().ordinal();
        return i;
    }

    public int getIndexLeft() {
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (facing == Direction.NORTH) {
            return Direction.EAST.ordinal();
        } else if (facing == Direction.WEST) {
            return Direction.NORTH.ordinal();
        } else if (facing == Direction.SOUTH) {
            return Direction.WEST.ordinal();
        } else {
            return Direction.SOUTH.ordinal();
        }
    }

    public int getIndexRight() {
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (facing == Direction.NORTH) {
            return Direction.WEST.ordinal();
        } else if (facing == Direction.WEST) {
            return Direction.SOUTH.ordinal();
        } else if (facing == Direction.SOUTH) {
            return Direction.EAST.ordinal();
        } else {
            return Direction.NORTH.ordinal();
        }
    }

    public int getAutoInput() {
        return furnaceSettings.get(6);
    }

    public int getAugmentGUI() {
        return furnaceSettings.get(10);
    }

    public int getAutoOutput() {
        return furnaceSettings.get(7);
    }

    public int getRedstoneSetting() {
        return furnaceSettings.get(8);
    }

    public int getRedstoneComSub() {
        return furnaceSettings.get(9);
    }


    protected int getStateType() {
        if (this.getItem(3).getItem() == Registration.SMOKING_AUGMENT.get()) {
            return 1;
        } else if (this.getItem(3).getItem() == Registration.BLASTING_AUGMENT.get()) {
            return 2;
        } else {
            return 0;
        }
    }

    public boolean isBurning() {
        return furnaceBurnTime > 0;
    }

    public boolean isRainbowFurnace() {
        return this instanceof BlockMillionFurnaceTile;
    }

    protected void smelt(@Nullable RecipeHolder<?> recipe) {
        if (this instanceof BlockMillionFurnaceTile) {
            smeltItemMult(recipe, 64);
        } else if (this instanceof BlockAllthemodiumFurnaceTile) {
            smeltItemMult(recipe, Config.allthemodiumFurnaceSmeltMult.get());
        } else if (this instanceof BlockVibraniumFurnaceTile) {
            smeltItemMult(recipe, Config.vibraniumFurnaceSmeltMult.get());
        } else if (this instanceof BlockUnobtainiumFurnaceTile) {
            smeltItemMult(recipe, Config.unobtainiumFurnaceSmeltMult.get());
        } else {
            smeltItem(recipe);
        }
    }

    protected void factorySmelt(@Nullable RecipeHolder<?> recipe, int slot) {
        if (this instanceof BlockMillionFurnaceTile) {
            smeltFactoryItemMult(recipe, slot, 64);
        } else if (this instanceof BlockAllthemodiumFurnaceTile) {
            smeltFactoryItemMult(recipe, slot, Config.allthemodiumFurnaceSmeltMult.get());
        } else if (this instanceof BlockVibraniumFurnaceTile) {
            smeltFactoryItemMult(recipe, slot, Config.vibraniumFurnaceSmeltMult.get());
        } else if (this instanceof BlockUnobtainiumFurnaceTile) {
            smeltFactoryItemMult(recipe, slot, Config.unobtainiumFurnaceSmeltMult.get());
        } else {
            smeltFactoryItem(recipe, slot);
        }
    }
    protected boolean canSmelt(@Nullable RecipeHolder<?> recipe) {
        if (!this.getItem(0).isEmpty() && recipe != null) {
            ItemStack recipeOutput = recipe.value().getResultItem(RegistryAccess.EMPTY);
            if (!recipeOutput.isEmpty()) {
                ItemStack output = this.getItem(OUTPUT);
                if (output.isEmpty()) return true;
                else if (!ItemStack.isSameItemSameComponents(output, recipeOutput)) return false;
                else return output.getCount() + recipeOutput.getCount() <= Math.min(output.getMaxStackSize(), 64);
            }
        }
        return false;
    }

    protected void smeltItem(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null && this.canSmelt(recipe)) {
            ItemStack itemstack = this.getItem(INPUT);
            ItemStack itemstack1 = recipe.value().getResultItem(RegistryAccess.EMPTY);
            ItemStack itemstack2 = this.getItem(OUTPUT);
            if (itemstack2.isEmpty()) {
                this.setItem(OUTPUT, itemstack1.copy());
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.grow(itemstack1.getCount());
            }
            if (!this.level.isClientSide) {
                this.setRecipeUsed(recipe);
            }

            if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.getItem(FUEL).isEmpty() && this.getItem(FUEL).getItem() == Items.BUCKET) {
                this.setItem(FUEL, new ItemStack(Items.WATER_BUCKET));
            }
            itemstack.shrink(1);
        }
    }

    protected boolean canFactorySmelt(@Nullable RecipeHolder<?> recipe, int slot) {
        int outputSlot = slot + 6;
        if (!this.getItem(slot).isEmpty() && recipe != null) {
            ItemStack recipeOutput = recipe.value().getResultItem(RegistryAccess.EMPTY);
            if (!recipeOutput.isEmpty()) {
                ItemStack output = this.getItem(outputSlot);
                if (output.isEmpty()) return true;
                else if (!ItemStack.isSameItemSameComponents(output, recipeOutput)) return false;
                else return output.getCount() + recipeOutput.getCount() <= output.getMaxStackSize();
            }
        }
        return false;
    }

    protected void smeltFactoryItem(@Nullable RecipeHolder<?> recipe, int slot) {
        int outputSlot = slot + 6;
        if (recipe != null && this.canFactorySmelt(recipe, slot)) {
            ItemStack itemstack = this.getItem(slot);
            ItemStack itemstack1 = recipe.value().getResultItem(RegistryAccess.EMPTY);
            ItemStack itemstack2 = this.getItem(outputSlot);
            if (itemstack2.isEmpty()) {
                this.setItem(outputSlot, itemstack1.copy());
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.grow(itemstack1.getCount());
            }
            if (!this.level.isClientSide) {
                this.setRecipeUsed(recipe);
            }
            itemstack.shrink(1);
        }
    }

    protected void smeltItemMult(@Nullable RecipeHolder<?> recipe, int div) {
        if (recipe != null && this.canSmelt(recipe)) {
            ItemStack itemstack = this.getItem(INPUT);
            ItemStack itemstack1 = recipe.value().getResultItem(RegistryAccess.EMPTY);
            ItemStack itemstack2 = this.getItem(OUTPUT);
            int maxCanSmelt = (64 - itemstack2.getCount()) / itemstack1.getCount();
            int wantToSmeltCount = Math.min(Math.min(div, maxCanSmelt), itemstack.getCount());
            int whenSmelted = itemstack1.getCount() * wantToSmeltCount;
            int decrement = whenSmelted / itemstack1.getCount();
            if (itemstack2.isEmpty()) {
                this.setItem(OUTPUT, new ItemStack(itemstack1.copy().getItem(), whenSmelted));
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.grow(whenSmelted);
            }
            if (!this.level.isClientSide) {
                for (int i = 0; i < decrement; i++) {
                    this.setRecipeUsed(recipe);
                }
            }

            if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.getItem(FUEL).isEmpty() && this.getItem(FUEL).getItem() == Items.BUCKET) {
                this.setItem(FUEL, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(decrement);
        }
    }

    protected void smeltFactoryItemMult(@Nullable RecipeHolder<?> recipe, int slot, int div) {
        int outputSlot = slot + 6;
        if (recipe != null && this.canFactorySmelt(recipe, slot)) {
            ItemStack itemstack = this.getItem(slot);
            ItemStack itemstack1 = recipe.value().getResultItem(RegistryAccess.EMPTY);
            ItemStack itemstack2 = this.getItem(outputSlot);
            int maxCanSmelt = (64 - itemstack2.getCount()) / itemstack1.getCount();
            int wantToSmeltCount = Math.min(Math.min(div, maxCanSmelt), itemstack.getCount());
            int whenSmelted = itemstack1.getCount() * wantToSmeltCount;
            int decrement = whenSmelted / itemstack1.getCount();
            if (itemstack2.isEmpty()) {
                this.setItem(outputSlot, new ItemStack(itemstack1.copy().getItem(), whenSmelted));
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.grow(whenSmelted);
            }
            if (!this.level.isClientSide) {
                for (int i = 0; i < decrement; i++) {
                    this.setRecipeUsed(recipe);
                }
            }
            itemstack.shrink(decrement);
        }
    }

    private void handleSmeltedPMMO(ItemStack stack, Level level, BlockPos pos) {
        //FurnaceHandler.handle(new FurnaceBurnEvent(stack, level, pos));
    }


    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {

        if (tag.get("Owner") != null)
        {
            owner = tag.getUUID("Owner");
        }

        tag.getBoolean("RainbowGen");

        for (int i = 0; i < factoryCookTime.length; i++) {
            int[] tagArr = tag.getIntArray("FactoryCookTime");
            if (tagArr.length == factoryCookTime.length) {
                factoryCookTime[i] = tagArr[i];
            }
        }
        for (int i = 0; i < factoryTotalCookTime.length; i++) {
            int[] tagArr = tag.getIntArray("FactoryTotalCookTime");
            if (tagArr.length == factoryTotalCookTime.length) {
                factoryTotalCookTime[i] = tagArr[i];
            }
        }
        for (int i = 0; i < usedRF.length; i++) {
            double tagRF = tag.getDouble("UsedRF" + i);
            usedRF[i] = tagRF;
        }

        generatorBurn = tag.getDouble("GeneratorBurn");
        generatorRecentRecipeRF = tag.getInt("GeneratorRecent");
        gottenRF = tag.getDouble("GottenRF");

        furnaceBurnTime = tag.getInt("BurnTime");
        cookTime = tag.getInt("CookTime");
        totalCookTime = tag.getInt("CookTimeTotal");
        currentAugment = tag.getIntArray("Augment");
        jovial = tag.getInt("Jovial");
        recipesUsed = this.getBurnTime(this.getItem(1), recipeType);
        CompoundTag compoundtag = tag.getCompound("RecipesUsed");
        Iterator var4 = compoundtag.getAllKeys().iterator();

        while(var4.hasNext()) {
            String s = (String)var4.next();
            this.recipes.put(ResourceLocation.parse(s), compoundtag.getInt(s));
        }
        furnaceSettings.read(tag);

        setEnergy(tag.getInt("Energy"));
        lastGameTickEnergyUpdated = 0;

        super.loadAdditional(tag, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (owner != null)
        {
            tag.putUUID("Owner", owner);
        }
        tag.putBoolean("RainbowGen", rainbowGenerating);
        tag.putIntArray("FactoryCookTime", factoryCookTime);
        tag.putIntArray("FactoryTotalCookTime", factoryTotalCookTime);
        for (int i = 0; i < usedRF.length; i++) {
            tag.putDouble("UsedRF" + i, usedRF[i]);
        }

        tag.putDouble("GeneratorBurn", generatorBurn);
        tag.putInt("GeneratorRecent", generatorRecentRecipeRF);
        tag.putDouble("GottenRF", gottenRF);

        tag.putInt("BurnTime", furnaceBurnTime);
        tag.putInt("CookTime", cookTime);
        tag.putInt("CookTimeTotal", totalCookTime);
        tag.putIntArray("Augment", currentAugment);
        tag.putInt("Jovial", jovial);
        furnaceSettings.write(tag);


        tag.putInt("Energy", getEnergy());


        CompoundTag compoundtag = new CompoundTag();
        this.recipes.forEach((p_187449_, p_187450_) -> {
            compoundtag.putInt(p_187449_.toString(), p_187450_);
        });
        tag.put("RecipesUsed", compoundtag);


        //tag.putString("SavedPlayer", savedPlayer.getStringUUID());
    }


    public static int getBurnTime(ItemStack stack, RecipeType recipeType) {
        return EventHooks.getItemBurnTime(stack, stack.getBurnTime(recipeType), recipeType);
    }


    public static boolean isItemFuel(ItemStack stack, RecipeType recipeType) {
        return getBurnTime(stack, recipeType) > 0 || stack.getItem() instanceof ItemHeater;
    }

    public static boolean isItemAugment(ItemStack stack, int type) {
        if (type == 0) {
            return stack.getItem() instanceof ItemAugmentRed;
        }
        if (type == 1) {
            return stack.getItem() instanceof ItemAugmentGreen;
        }
        if (type == 2) {
            return stack.getItem() instanceof ItemAugmentBlue;
        }
        return stack.getItem() instanceof ItemAugment;
    }


    @Override
    public int[] IgetSlotsForFace(Direction side) {
        if (isFurnace()) {
            if (furnaceSettings.get(DirectionUtil.getId(side)) == 0) {
                return new int[]{};
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 1) {
                return new int[]{0, 1};
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 2) {
                return new int[]{2};
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 3) {
                return new int[]{0, 1, 2};
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 4) {
                return new int[]{1};
            }
        } else if (isGenerator()) {
            if (furnaceSettings.get(DirectionUtil.getId(side)) == 4) {
                return new int[]{6};
            }
        } else if (isFactory()) {
            if (furnaceSettings.get(DirectionUtil.getId(side)) == 0) {
                return new int[]{};
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 1) {
                return FACTORY_INPUT;
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 2) {
                return new int[]{13, 14, 15, 16, 17, 18};
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 3) {
                return new int[]{7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
            }
        }

        return new int[]{};
    }

    @Override
    public boolean IcanExtractItem(int index, ItemStack stack, Direction direction) {
        if (isFurnace()) {
            if (furnaceSettings.get(DirectionUtil.getId(direction)) == 0) {
                return false;
            } else if (furnaceSettings.get(DirectionUtil.getId(direction)) == 1) {
                return false;
            } else if (furnaceSettings.get(DirectionUtil.getId(direction)) == 2) {
                return index == 2;
            } else if (furnaceSettings.get(DirectionUtil.getId(direction)) == 3) {
                return index == 2;
            } else if (furnaceSettings.get(DirectionUtil.getId(direction)) == 4 && stack.getItem() != Items.BUCKET) {
                return false;
            } else if (furnaceSettings.get(DirectionUtil.getId(direction)) == 4 && stack.getItem() == Items.BUCKET) {
                return true;
            }
        } else if (isGenerator()) {
            if (furnaceSettings.get(DirectionUtil.getId(direction)) == 4 && stack.getItem() == Items.BUCKET) {
                return true;
            }
        } else if (isFactory()) {
            if (furnaceSettings.get(DirectionUtil.getId(direction)) == 2) {
                return index >= 13 && index <= 18;
            } else if (furnaceSettings.get(DirectionUtil.getId(direction)) == 3) {
                return index >= 13 && index <= 18;
            }
        }
        return false;
    }

    @Override
    public boolean IisItemValidForSlot(int index, ItemStack stack) {
        if (isFurnace()) {
            if (index == OUTPUT || index == 3 || index == 4 || index == 5) {
                return false;
            }
            if (index == INPUT) {
                if (stack.isEmpty()) {
                    return false;
                }

                return hasRecipe(stack);
            }
            if (index == FUEL) {
                ItemStack itemstack = this.getItem(FUEL);
                return getBurnTime(stack, recipeType) > 0 || (stack.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET) || stack.getItem() instanceof ItemHeater;
            }
        } else if (isGenerator()) {
            if (index == GENERATOR_FUEL) {
                if (getItem(AUGMENT_RED).getItem() instanceof ItemAugmentSmoking && getSmokingBurn(stack) > 0) {
                    return true;
                }
                if (getItem(AUGMENT_RED).getItem() instanceof ItemAugmentBlasting && hasGeneratorBlastingRecipe(stack)) {
                    return true;
                }
                if (getItem(AUGMENT_RED).isEmpty() && getBurnTime(stack, recipeType) > 0) {
                    return true;
                }
                if (stack.getItem() instanceof ItemHeater)
                {
                    return false;
                }
            }
        } else if (isFactory()) {
            if ((index >= 13 && index <= 18) || index == 3 || index == 4 || index == 5) {
                return false;
            }
            if (index >= 7 && index <= 12) {
                if (stack.isEmpty()) {
                    return false;
                }
                if (getTier() == 0)
                {
                    if (index >= 9 && index <= 10)
                    {
                        return hasRecipe(stack);
                    }
                    else
                    {
                        return false;
                    }
                }
                else if (getTier() == 1)
                {
                    if (index >= 8 && index <= 11)
                    {
                        return hasRecipe(stack);
                    }
                    else
                    {
                        return false;
                    }
                }
                return hasRecipe(stack);
            }
        }
        return false;
    }

    public void setJovial(int value) {
        jovial = value;
    }

    public int getXpNeededForNextLevel(int experienceLevel) {
        if (experienceLevel >= 30) {
            return 112 + (experienceLevel - 30) * 9;
        } else {
            return experienceLevel >= 15 ? 37 + (experienceLevel - 15) * 5 : 7 + experienceLevel * 2;
        }
    }


    public int getXpNeededForLevel(int level)
    {
        int xp = 0;
        for (int i = 0; i < level; i++)
        {
            xp += getXpNeededForNextLevel(i);
        }
        return xp + 1;
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            ResourceLocation resourcelocation = recipe.id();
            this.recipes.addTo(resourcelocation, 1);
        }
    }

    @Nullable
    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }


    public void unlockRecipes(ServerPlayer player) {
        List<RecipeHolder<?>> list = this.grantStoredRecipeExperience(player.serverLevel(), player.position());
        player.awardRecipes(list);

        for (RecipeHolder<?> recipeholder : list) {
            if (recipeholder != null) {
                player.triggerRecipeCrafted(recipeholder, this.inventory);
            }
        }

        this.recipes.clear();
    }

    public List<RecipeHolder<?>> grantStoredRecipeExperience(ServerLevel level, Vec3 worldPosition) {
        List<RecipeHolder<?>> list = new ArrayList<>();

        for (Object2IntMap.Entry<ResourceLocation> entry : recipes.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((h) -> {
                list.add(h);
                splitAndSpawnExperience(level, worldPosition, entry.getIntValue(), ((AbstractCookingRecipe) h.value()).getExperience());
            });
        }


        return list;
    }

    private static void splitAndSpawnExperience(ServerLevel level, Vec3 worldPosition, int craftedAmount, float experience) {
        int i = Mth.floor((float) craftedAmount * experience);
        float f = Mth.frac((float) craftedAmount * experience);
        if (f != 0.0F && Math.random() < (double) f) {
            ++i;
        }
        ExperienceOrb.award(level, worldPosition, i);

    }

    @Override
    public void fillStackedContents(StackedContents helper) {
        for (ItemStack itemstack : this.inventory) {
            helper.accountStack(itemstack);
        }

    }

    protected boolean doesNeedUpdateSend() {
        return !Arrays.equals(this.provides, this.lastProvides);
    }

    public void onUpdateSent() {
        System.arraycopy(this.provides, 0, this.lastProvides, 0, this.provides.length);
        this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
    }


    public void placeConfig() {

        if (furnaceSettings != null) {
            furnaceSettings.set(0, 2);
            furnaceSettings.set(1, 1);
            for (Direction dir : Direction.values()) {
                if (dir != Direction.DOWN && dir != Direction.UP) {
                    furnaceSettings.set(dir.ordinal(), 4);
                }
            }
            level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), level.getBlockState(worldPosition).getBlock().defaultBlockState(), level.getBlockState(worldPosition), 3, 3);
        }

    }

    public boolean isGenerator() {
        return currentAugment[2] == 2;
    }

    public boolean isFactory() {
        return currentAugment[2] == 1;
    }

    public boolean isFurnace() {
        return currentAugment[2] == 0;
    }

    public int getTier() {
        return 0;
    }
}
