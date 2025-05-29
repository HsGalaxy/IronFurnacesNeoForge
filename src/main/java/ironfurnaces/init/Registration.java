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

package ironfurnaces.init;

import com.mojang.serialization.Codec;
import ironfurnaces.IronFurnaces;
import ironfurnaces.blocks.BlockWirelessEnergyHeater;
import ironfurnaces.blocks.furnaces.*;
import ironfurnaces.blocks.furnaces.other.BlockAllthemodiumFurnace;
import ironfurnaces.blocks.furnaces.other.BlockUnobtainiumFurnace;
import ironfurnaces.blocks.furnaces.other.BlockVibraniumFurnace;
import ironfurnaces.capability.PlayerFurnacesListProvider;
import ironfurnaces.capability.PlayerShowConfigProvider;
import ironfurnaces.container.BlockWirelessEnergyHeaterContainer;
import ironfurnaces.container.furnaces.*;
import ironfurnaces.container.furnaces.other.BlockAllthemodiumFurnaceContainer;
import ironfurnaces.container.furnaces.other.BlockUnobtainiumFurnaceContainer;
import ironfurnaces.container.furnaces.other.BlockVibraniumFurnaceContainer;
import ironfurnaces.items.*;
import ironfurnaces.items.augments.*;
import ironfurnaces.items.upgrades.*;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.recipes.SimpleGeneratorRecipe;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.furnaces.*;
import ironfurnaces.tileentity.furnaces.other.BlockAllthemodiumFurnaceTile;
import ironfurnaces.tileentity.furnaces.other.BlockUnobtainiumFurnaceTile;
import ironfurnaces.tileentity.furnaces.other.BlockVibraniumFurnaceTile;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static ironfurnaces.IronFurnaces.MOD_ID;

public class Registration {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MOD_ID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MOD_ID);
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MOD_ID);



    //private static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, MOD_ID);
    //private static final DeferredRegister<ModDimension> DIMENSIONS = new DeferredRegister<>(ForgeRegistries.MOD_DIMENSIONS, MOD_ID);

    public static void init(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        TILES.register(modEventBus);
        CONTAINERS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        DATA_COMPONENT_TYPES.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
        //ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        //DIMENSIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    // Serialization via INBTSerializable
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerFurnacesListProvider>> PLAYER_FURNACES_LIST = ATTACHMENT_TYPES.register(
            "furnaces_list", () -> AttachmentType.serializable(PlayerFurnacesListProvider::new).build());

    // Serialization via INBTSerializable
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerShowConfigProvider>> PLAYER_SHOW_CONFIG = ATTACHMENT_TYPES.register(
            "show_config", () -> AttachmentType.serializable(PlayerShowConfigProvider::new).build());


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY = DATA_COMPONENT_TYPES.register("energy",
            () -> DataComponentType.<Integer>builder()
                    // The codec to read/write the data to disk
                    .persistent(Codec.INT)
                    // The codec to read/write the data across the network
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build()
    );

    public static final Supplier<DataComponentType<CustomData>> FURNACE_SETTINGS = DATA_COMPONENT_TYPES.register("furnace_settings",
            () -> DataComponentType.<CustomData>builder()
                    // The codec to read/write the data to disk
                    .persistent(CustomData.CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(CustomData.STREAM_CODEC)
                    .build()
    );



    public static final String GENERATOR_ID = "generator_blasting";

    public static final class RecipeTypes {

        public static mezz.jei.api.recipe.RecipeType<GeneratorRecipe> GENERATOR_BLASTING = mezz.jei.api.recipe.RecipeType.create(IronFurnaces.MOD_ID, "generator_blasting", GeneratorRecipe.class);
        public static mezz.jei.api.recipe.RecipeType<SimpleGeneratorRecipe> GENERATOR_SMOKING = mezz.jei.api.recipe.RecipeType.create(IronFurnaces.MOD_ID, "generator_smoking", SimpleGeneratorRecipe.class);
        public static mezz.jei.api.recipe.RecipeType<SimpleGeneratorRecipe> GENERATOR_REGULAR = mezz.jei.api.recipe.RecipeType.create(IronFurnaces.MOD_ID, "generator_regular", SimpleGeneratorRecipe.class);
    }
    public static Supplier<RecipeType<GeneratorRecipe>> GENERATOR_RECIPE_TYPE = RECIPE_TYPES.register(GENERATOR_ID, () -> new RecipeType<GeneratorRecipe>() {
        @Override
        public String toString() {
            return GENERATOR_ID;
        }
    });
    public static Supplier<RecipeSerializer<GeneratorRecipe>> GENERATOR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(GENERATOR_ID, GeneratorRecipe.Serializer::new);


    public static final DeferredHolder<Block, BlockIronFurnace> IRON_FURNACE = BLOCKS.register(BlockIronFurnace.IRON_FURNACE, () -> new BlockIronFurnace(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredHolder<Item, ItemFurnace> IRON_FURNACE_ITEM = ITEMS.register(BlockIronFurnace.IRON_FURNACE, () -> new ItemFurnace(IRON_FURNACE.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<BlockIronFurnaceTile>> IRON_FURNACE_TILE = TILES.register(BlockIronFurnace.IRON_FURNACE, () -> BlockEntityType.Builder.of(BlockIronFurnaceTile::new, IRON_FURNACE.get()).build(null));

    public static final Supplier<MenuType<BlockIronFurnaceContainer>> IRON_FURNACE_CONTAINER = CONTAINERS.register(BlockIronFurnace.IRON_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockIronFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player)));



    public static final DeferredHolder<Block, BlockGoldFurnace> GOLD_FURNACE = BLOCKS.register(BlockGoldFurnace.GOLD_FURNACE, () -> new BlockGoldFurnace(Block.Properties.ofFullCopy(Blocks.GOLD_BLOCK)));
    public static final DeferredHolder<Item, ItemFurnace> GOLD_FURNACE_ITEM = ITEMS.register(BlockGoldFurnace.GOLD_FURNACE, () -> new ItemFurnace(GOLD_FURNACE.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<BlockGoldFurnaceTile>> GOLD_FURNACE_TILE = TILES.register(BlockGoldFurnace.GOLD_FURNACE, () -> BlockEntityType.Builder.of(BlockGoldFurnaceTile::new, GOLD_FURNACE.get()).build(null));

    public static final Supplier<MenuType<BlockGoldFurnaceContainer>> GOLD_FURNACE_CONTAINER = CONTAINERS.register(BlockGoldFurnace.GOLD_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockGoldFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player)));


    public static final DeferredHolder<Block, BlockDiamondFurnace> DIAMOND_FURNACE = BLOCKS.register(BlockDiamondFurnace.DIAMOND_FURNACE, () -> new BlockDiamondFurnace(Block.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK)));
    public static final DeferredHolder<Item, ItemFurnace> DIAMOND_FURNACE_ITEM = ITEMS.register(BlockDiamondFurnace.DIAMOND_FURNACE, () -> new ItemFurnace(DIAMOND_FURNACE.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<BlockDiamondFurnaceTile>> DIAMOND_FURNACE_TILE = TILES.register(BlockDiamondFurnace.DIAMOND_FURNACE, () -> BlockEntityType.Builder.of(BlockDiamondFurnaceTile::new, DIAMOND_FURNACE.get()).build(null));

    public static final Supplier<MenuType<BlockDiamondFurnaceContainer>> DIAMOND_FURNACE_CONTAINER = CONTAINERS.register(BlockDiamondFurnace.DIAMOND_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockDiamondFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player)));


    public static final DeferredHolder<Block, BlockEmeraldFurnace> EMERALD_FURNACE = BLOCKS.register(BlockEmeraldFurnace.EMERALD_FURNACE, () -> new BlockEmeraldFurnace(Block.Properties.ofFullCopy(Blocks.EMERALD_BLOCK)));
    public static final DeferredHolder<Item, ItemFurnace> EMERALD_FURNACE_ITEM = ITEMS.register(BlockEmeraldFurnace.EMERALD_FURNACE, () -> new ItemFurnace(EMERALD_FURNACE.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<BlockEmeraldFurnaceTile>> EMERALD_FURNACE_TILE = TILES.register(BlockEmeraldFurnace.EMERALD_FURNACE, () -> BlockEntityType.Builder.of(BlockEmeraldFurnaceTile::new, EMERALD_FURNACE.get()).build(null));

    public static final Supplier<MenuType<BlockEmeraldFurnaceContainer>> EMERALD_FURNACE_CONTAINER = CONTAINERS.register(BlockEmeraldFurnace.EMERALD_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockEmeraldFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player)));


    public static final DeferredHolder<Block, BlockObsidianFurnace> OBSIDIAN_FURNACE = BLOCKS.register(BlockObsidianFurnace.OBSIDIAN_FURNACE, () -> new BlockObsidianFurnace(Block.Properties.ofFullCopy(Blocks.OBSIDIAN))); // Assuming BlockObsidianFurnace exists
    public static final DeferredHolder<Item, ItemFurnace> OBSIDIAN_FURNACE_ITEM = ITEMS.register(BlockObsidianFurnace.OBSIDIAN_FURNACE, () -> new ItemFurnace(OBSIDIAN_FURNACE.get(), new Item.Properties())); // Assuming Config.obsidianFurnaceSpeed exists and BlockObsidianFurnace

    public static final Supplier<BlockEntityType<BlockObsidianFurnaceTile>> OBSIDIAN_FURNACE_TILE = TILES.register(BlockObsidianFurnace.OBSIDIAN_FURNACE, () -> BlockEntityType.Builder.of(BlockObsidianFurnaceTile::new, OBSIDIAN_FURNACE.get()).build(null));

    public static final Supplier<MenuType<BlockObsidianFurnaceContainer>> OBSIDIAN_FURNACE_CONTAINER = CONTAINERS.register(BlockObsidianFurnace.OBSIDIAN_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockObsidianFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player)));



    public static final DeferredHolder<Block, BlockCrystalFurnace> CRYSTAL_FURNACE = BLOCKS.register(BlockCrystalFurnace.CRYSTAL_FURNACE, () -> new BlockCrystalFurnace(Block.Properties.ofFullCopy(Blocks.GLASS))); // Might want to use a custom crystal block here
    public static final DeferredHolder<Item, ItemFurnace> CRYSTAL_FURNACE_ITEM = ITEMS.register(BlockCrystalFurnace.CRYSTAL_FURNACE, () -> new ItemFurnace(CRYSTAL_FURNACE.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<BlockCrystalFurnaceTile>> CRYSTAL_FURNACE_TILE = TILES.register(BlockCrystalFurnace.CRYSTAL_FURNACE, () -> BlockEntityType.Builder.of(BlockCrystalFurnaceTile::new, CRYSTAL_FURNACE.get()).build(null));

    public static final Supplier<MenuType<BlockCrystalFurnaceContainer>> CRYSTAL_FURNACE_CONTAINER = CONTAINERS.register(BlockCrystalFurnace.CRYSTAL_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockCrystalFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player)));



    public static final DeferredHolder<Block, BlockNetheriteFurnace> NETHERITE_FURNACE = BLOCKS.register(BlockNetheriteFurnace.NETHERITE_FURNACE, () -> new BlockNetheriteFurnace(Block.Properties.ofFullCopy(Blocks.NETHERITE_BLOCK)));
    public static final DeferredHolder<Item, ItemFurnace> NETHERITE_FURNACE_ITEM = ITEMS.register(BlockNetheriteFurnace.NETHERITE_FURNACE, () -> new ItemFurnace(NETHERITE_FURNACE.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<BlockNetheriteFurnaceTile>> NETHERITE_FURNACE_TILE = TILES.register(BlockNetheriteFurnace.NETHERITE_FURNACE, () -> BlockEntityType.Builder.of(BlockNetheriteFurnaceTile::new, NETHERITE_FURNACE.get()).build(null));

    public static final Supplier<MenuType<BlockNetheriteFurnaceContainer>> NETHERITE_FURNACE_CONTAINER = CONTAINERS.register(BlockNetheriteFurnace.NETHERITE_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockNetheriteFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player)));



    public static final DeferredHolder<Block, BlockCopperFurnace> COPPER_FURNACE = BLOCKS.register(BlockCopperFurnace.COPPER_FURNACE, () -> new BlockCopperFurnace(Block.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final DeferredHolder<Item, ItemFurnace> COPPER_FURNACE_ITEM = ITEMS.register(BlockCopperFurnace.COPPER_FURNACE, () -> new ItemFurnace(COPPER_FURNACE.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<BlockCopperFurnaceTile>> COPPER_FURNACE_TILE = TILES.register(BlockCopperFurnace.COPPER_FURNACE, () -> BlockEntityType.Builder.of(BlockCopperFurnaceTile::new, COPPER_FURNACE.get()).build(null));

    public static final Supplier<MenuType<BlockCopperFurnaceContainer>> COPPER_FURNACE_CONTAINER = CONTAINERS.register(BlockCopperFurnace.COPPER_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockCopperFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player)));



    public static final DeferredHolder<Block, BlockSilverFurnace> SILVER_FURNACE = BLOCKS.register(BlockSilverFurnace.SILVER_FURNACE, () -> new BlockSilverFurnace(Block.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final DeferredHolder<Item, ItemFurnace> SILVER_FURNACE_ITEM = ITEMS.register(BlockSilverFurnace.SILVER_FURNACE, () -> new ItemFurnace(SILVER_FURNACE.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<BlockSilverFurnaceTile>> SILVER_FURNACE_TILE = TILES.register(BlockSilverFurnace.SILVER_FURNACE, () -> BlockEntityType.Builder.of(BlockSilverFurnaceTile::new, SILVER_FURNACE.get()).build(null));

    public static final Supplier<MenuType<BlockSilverFurnaceContainer>> SILVER_FURNACE_CONTAINER = CONTAINERS.register(BlockSilverFurnace.SILVER_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockSilverFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player)));

    public static final DeferredHolder<Item, ItemUpgradeIron> IRON_UPGRADE = ITEMS.register("upgrade_iron", () -> new ItemUpgradeIron(new Item.Properties()));
    public static final DeferredHolder<Item, ItemUpgradeGold> GOLD_UPGRADE = ITEMS.register("upgrade_gold", () -> new ItemUpgradeGold(new Item.Properties()));
    public static final DeferredHolder<Item, ItemUpgradeDiamond> DIAMOND_UPGRADE = ITEMS.register("upgrade_diamond", () -> new ItemUpgradeDiamond(new Item.Properties()));
    public static final DeferredHolder<Item, ItemUpgradeEmerald> EMERALD_UPGRADE = ITEMS.register("upgrade_emerald", () -> new ItemUpgradeEmerald(new Item.Properties()));
    public static final DeferredHolder<Item, ItemUpgradeObsidian> OBSIDIAN_UPGRADE = ITEMS.register("upgrade_obsidian", () -> new ItemUpgradeObsidian(new Item.Properties()));
    public static final DeferredHolder<Item, ItemUpgradeCrystal> CRYSTAL_UPGRADE = ITEMS.register("upgrade_crystal", () -> new ItemUpgradeCrystal(new Item.Properties()));
    public static final DeferredHolder<Item, ItemUpgradeNetherite> NETHERITE_UPGRADE = ITEMS.register("upgrade_netherite", () -> new ItemUpgradeNetherite(new Item.Properties()));

    public static final DeferredHolder<Item, ItemUpgradeCopper> COPPER_UPGRADE = ITEMS.register("upgrade_copper", () -> new ItemUpgradeCopper(new Item.Properties()));
    public static final DeferredHolder<Item, ItemUpgradeSilver> SILVER_UPGRADE = ITEMS.register("upgrade_silver", () -> new ItemUpgradeSilver(new Item.Properties()));

    public static final DeferredHolder<Item, ItemUpgradeObsidian2> OBSIDIAN2_UPGRADE = ITEMS.register("upgrade_obsidian2", () -> new ItemUpgradeObsidian2(new Item.Properties()));
    public static final DeferredHolder<Item, ItemUpgradeIron2> IRON2_UPGRADE = ITEMS.register("upgrade_iron2", () -> new ItemUpgradeIron2(new Item.Properties()));
    public static final DeferredHolder<Item, ItemUpgradeGold2> GOLD2_UPGRADE = ITEMS.register("upgrade_gold2", () -> new ItemUpgradeGold2(new Item.Properties()));
    public static final DeferredHolder<Item, ItemUpgradeSilver2> SILVER2_UPGRADE = ITEMS.register("upgrade_silver2", () -> new ItemUpgradeSilver2(new Item.Properties()));

    public static final DeferredHolder<Block, BlockAllthemodiumFurnace> ALLTHEMODIUM_FURNACE = BLOCKS.register(BlockAllthemodiumFurnace.ALLTHEMODIUM_FURNACE, () -> new BlockAllthemodiumFurnace(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK))); // Assuming BlockAllthemodiumFurnace exists
    public static final DeferredHolder<Item, ItemAllthemodiumFurnace> ALLTHEMODIUM_FURNACE_ITEM = ITEMS.register(BlockAllthemodiumFurnace.ALLTHEMODIUM_FURNACE, () -> new ItemAllthemodiumFurnace(ALLTHEMODIUM_FURNACE.get(), new Item.Properties())); // Assuming Config.allthemodiumFurnaceSpeed exists and BlockAllthemodiumFurnace

    public static final Supplier<BlockEntityType<BlockAllthemodiumFurnaceTile>> ALLTHEMODIUM_FURNACE_TILE = TILES.register(BlockAllthemodiumFurnace.ALLTHEMODIUM_FURNACE, () -> BlockEntityType.Builder.of(BlockAllthemodiumFurnaceTile::new, ALLTHEMODIUM_FURNACE.get()).build(null));

    public static final Supplier<MenuType<BlockAllthemodiumFurnaceContainer>> ALLTHEMODIUM_FURNACE_CONTAINER = CONTAINERS.register(BlockAllthemodiumFurnace.ALLTHEMODIUM_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockAllthemodiumFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player)));


    public static final DeferredHolder<Block, BlockVibraniumFurnace> VIBRANIUM_FURNACE = BLOCKS.register(BlockVibraniumFurnace.VIBRANIUM_FURNACE, () -> new BlockVibraniumFurnace(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK))); // Assuming BlockVibraniumFurnace exists
    public static final DeferredHolder<Item, ItemVibraniumFurnace> VIBRANIUM_FURNACE_ITEM = ITEMS.register(BlockVibraniumFurnace.VIBRANIUM_FURNACE, () -> new ItemVibraniumFurnace(VIBRANIUM_FURNACE.get(), new Item.Properties())); // Assuming Config.vibraniumFurnaceSpeed exists and BlockVibraniumFurnace

    public static final Supplier<BlockEntityType<BlockVibraniumFurnaceTile>> VIBRANIUM_FURNACE_TILE = TILES.register(BlockVibraniumFurnace.VIBRANIUM_FURNACE, () -> BlockEntityType.Builder.of(BlockVibraniumFurnaceTile::new, VIBRANIUM_FURNACE.get()).build(null));

    public static final Supplier<MenuType<BlockVibraniumFurnaceContainer>> VIBRANIUM_FURNACE_CONTAINER = CONTAINERS.register(BlockVibraniumFurnace.VIBRANIUM_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockVibraniumFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player)));


    public static final DeferredHolder<Block, BlockUnobtainiumFurnace> UNOBTAINIUM_FURNACE = BLOCKS.register(BlockUnobtainiumFurnace.UNOBTAINIUM_FURNACE, () -> new BlockUnobtainiumFurnace(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK))); // Assuming BlockUnobtainiumFurnace exists
    public static final DeferredHolder<Item, ItemUnobtainiumFurnace> UNOBTAINIUM_FURNACE_ITEM = ITEMS.register(BlockUnobtainiumFurnace.UNOBTAINIUM_FURNACE, () -> new ItemUnobtainiumFurnace(UNOBTAINIUM_FURNACE.get(), new Item.Properties())); // Assuming Config.unobtainiumFurnaceSpeed exists and BlockUnobtainiumFurnace

    public static final Supplier<BlockEntityType<BlockUnobtainiumFurnaceTile>> UNOBTAINIUM_FURNACE_TILE = TILES.register(BlockUnobtainiumFurnace.UNOBTAINIUM_FURNACE, () -> BlockEntityType.Builder.of(BlockUnobtainiumFurnaceTile::new, UNOBTAINIUM_FURNACE.get()).build(null));

    public static final Supplier<MenuType<BlockUnobtainiumFurnaceContainer>> UNOBTAINIUM_FURNACE_CONTAINER = CONTAINERS.register(BlockUnobtainiumFurnace.UNOBTAINIUM_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockUnobtainiumFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player)));



    public static final DeferredHolder<Item, ItemUpgradeAllthemodium> ALLTHEMODIUM_UPGRADE = ITEMS.register("upgrade_allthemodium", () -> new ItemUpgradeAllthemodium(new Item.Properties()));
    public static final DeferredHolder<Item, ItemUpgradeVibranium> VIBRANIUM_UPGRADE = ITEMS.register("upgrade_vibranium", () -> new ItemUpgradeVibranium(new Item.Properties()));
    public static final DeferredHolder<Item, ItemUpgradeUnobtainium> UNOBTAINIUM_UPGRADE = ITEMS.register("upgrade_unobtainium", () -> new ItemUpgradeUnobtainium(new Item.Properties()));



    public static final DeferredHolder<Block, BlockWirelessEnergyHeater> HEATER = BLOCKS.register(BlockWirelessEnergyHeater.HEATER, () -> new BlockWirelessEnergyHeater(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK))); // Assuming BlockHeater exists
    public static final DeferredHolder<Item, BlockItemHeater> HEATER_ITEM = ITEMS.register(BlockWirelessEnergyHeater.HEATER, () -> new BlockItemHeater(HEATER.get(), new Item.Properties())); // Assuming Config.ironFurnaceSpeed exists

    public static final Supplier<BlockEntityType<BlockWirelessEnergyHeaterTile>> HEATER_TILE = TILES.register(BlockWirelessEnergyHeater.HEATER, () -> BlockEntityType.Builder.of(BlockWirelessEnergyHeaterTile::new, HEATER.get()).build(null)); // Assuming BlockHeaterTile exists

    public static final Supplier<MenuType<BlockWirelessEnergyHeaterContainer>> HEATER_CONTAINER = CONTAINERS.register(BlockWirelessEnergyHeater.HEATER, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockWirelessEnergyHeaterContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player))); // Assuming BlockHeaterContainer exists


    public static final DeferredHolder<Item, ItemHeater> ITEM_HEATER = ITEMS.register("item_heater", () -> new ItemHeater(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, ItemAugmentBlasting> BLASTING_AUGMENT = ITEMS.register("augment_blasting", () -> new ItemAugmentBlasting(new Item.Properties()));
    public static final DeferredHolder<Item, ItemAugmentSmoking> SMOKING_AUGMENT = ITEMS.register("augment_smoking", () -> new ItemAugmentSmoking(new Item.Properties()));
    public static final DeferredHolder<Item, ItemAugmentFactory> FACTORY_AUGMENT = ITEMS.register("augment_factory", () -> new ItemAugmentFactory(new Item.Properties()));
    public static final DeferredHolder<Item, ItemAugmentGenerator> GENERATOR_AUGMENT = ITEMS.register("augment_generator", () -> new ItemAugmentGenerator(new Item.Properties()));
    public static final DeferredHolder<Item, ItemAugmentSpeed> SPEED_AUGMENT = ITEMS.register("augment_speed", () -> new ItemAugmentSpeed(new Item.Properties()));
    public static final DeferredHolder<Item, ItemAugmentFuel> FUEL_AUGMENT = ITEMS.register("augment_fuel", () -> new ItemAugmentFuel(new Item.Properties()));

    public static final DeferredHolder<Item, ItemSpooky> ITEM_SPOOKY = ITEMS.register("item_spooky", () -> new ItemSpooky(new Item.Properties()));
    public static final DeferredHolder<Item, ItemXmas> ITEM_XMAS = ITEMS.register("item_xmas", () -> new ItemXmas(new Item.Properties()));

    public static final DeferredHolder<Item, ItemFurnaceCopy> ITEM_COPY = ITEMS.register("item_copy", () -> new ItemFurnaceCopy(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> RAINBOW_CORE = ITEMS.register("rainbow_core", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> RAINBOW_PLATING = ITEMS.register("rainbow_plating", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, ItemRainbowCoal> RAINBOW_COAL = ITEMS.register("rainbow_coal", () -> new ItemRainbowCoal(new Item.Properties().stacksTo(1)));


    public static final DeferredHolder<Block, BlockMillionFurnace> MILLION_FURNACE = BLOCKS.register(BlockMillionFurnace.MILLION_FURNACE, () -> new BlockMillionFurnace(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK))); // Assuming BlockMillionFurnace exists
    public static final DeferredHolder<Item, ItemMillionFurnace> MILLION_FURNACE_ITEM = ITEMS.register(BlockMillionFurnace.MILLION_FURNACE, () -> new ItemMillionFurnace(MILLION_FURNACE.get(), new Item.Properties())); // Assuming Config.ironFurnaceSpeed exists

    public static final Supplier<BlockEntityType<BlockMillionFurnaceTile>> MILLION_FURNACE_TILE = TILES.register(BlockMillionFurnace.MILLION_FURNACE, () -> BlockEntityType.Builder.of(BlockMillionFurnaceTile::new, MILLION_FURNACE.get()).build(null)); // Assuming BlockMillionFurnaceTile exists

    public static final Supplier<MenuType<BlockMillionFurnaceContainer>> MILLION_FURNACE_CONTAINER = CONTAINERS.register(BlockMillionFurnace.MILLION_FURNACE, () -> IMenuTypeExtension.create(
            (windowId, playerInv, extraData) -> new BlockMillionFurnaceContainer(windowId, playerInv.player.level(), extraData.readBlockPos(), playerInv, playerInv.player))); // Assuming BlockMillionFurnaceContainer exists


    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> tabIronFurnaces = CREATIVE_MODE_TABS.register("ironfurnaces_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> IRON_FURNACE.get().asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup.ironfurnaces"))
            .displayItems((parameters, output) -> {

                output.accept(Registration.IRON_FURNACE_ITEM.get());
                output.accept(Registration.GOLD_FURNACE_ITEM.get());
                output.accept(Registration.DIAMOND_FURNACE_ITEM.get());
                output.accept(Registration.EMERALD_FURNACE_ITEM.get());
                output.accept(Registration.OBSIDIAN_FURNACE_ITEM.get());
                output.accept(Registration.CRYSTAL_FURNACE_ITEM.get());
                output.accept(Registration.NETHERITE_FURNACE_ITEM.get());
                output.accept(Registration.COPPER_FURNACE_ITEM.get());
                output.accept(Registration.SILVER_FURNACE_ITEM.get());

                output.accept(Registration.IRON_UPGRADE.get());
                output.accept(Registration.GOLD_UPGRADE.get());
                output.accept(Registration.DIAMOND_UPGRADE.get());
                output.accept(Registration.EMERALD_UPGRADE.get());
                output.accept(Registration.OBSIDIAN_UPGRADE.get());
                output.accept(Registration.CRYSTAL_UPGRADE.get());
                output.accept(Registration.NETHERITE_UPGRADE.get());
                output.accept(Registration.COPPER_UPGRADE.get());
                output.accept(Registration.SILVER_UPGRADE.get());

                output.accept(Registration.OBSIDIAN2_UPGRADE.get());
                output.accept(Registration.IRON2_UPGRADE.get());
                output.accept(Registration.GOLD2_UPGRADE.get());
                output.accept(Registration.SILVER2_UPGRADE.get());
                output.accept(Registration.HEATER_ITEM.get());
                output.accept(Registration.ITEM_HEATER.get());
                output.accept(Registration.BLASTING_AUGMENT.get());
                output.accept(Registration.SMOKING_AUGMENT.get());
                output.accept(Registration.FACTORY_AUGMENT.get());

                output.accept(Registration.GENERATOR_AUGMENT.get());
                output.accept(Registration.SPEED_AUGMENT.get());
                output.accept(Registration.FUEL_AUGMENT.get());
                output.accept(Registration.ITEM_SPOOKY.get());
                output.accept(Registration.ITEM_XMAS.get());
                output.accept(Registration.ITEM_COPY.get());
                output.accept(Registration.RAINBOW_CORE.get());
                output.accept(Registration.RAINBOW_PLATING.get());

                output.accept(Registration.MILLION_FURNACE_ITEM.get());
                output.accept(Registration.RAINBOW_COAL.get());
            }).build());




}
