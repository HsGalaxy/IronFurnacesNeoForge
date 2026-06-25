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

package ironfurnaces;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = IronFurnaces.MOD_ID)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_FURNACE = "furnaces";
    public static final String CATEGORY_MODDED_FURNACE = "modded_furnaces";
    public static final String CATEGORY_MISC = "misc";
    

    public static ModConfigSpec.IntValue ironFurnaceSpeed;
    public static ModConfigSpec.IntValue goldFurnaceSpeed;
    public static ModConfigSpec.IntValue diamondFurnaceSpeed;
    public static ModConfigSpec.IntValue emeraldFurnaceSpeed;
    public static ModConfigSpec.IntValue obsidianFurnaceSpeed;
    public static ModConfigSpec.IntValue crystalFurnaceSpeed;
    public static ModConfigSpec.IntValue netheriteFurnaceSpeed;
    public static ModConfigSpec.IntValue copperFurnaceSpeed;
    public static ModConfigSpec.IntValue silverFurnaceSpeed;
    public static ModConfigSpec.IntValue millionFurnaceSpeed;
    public static ModConfigSpec.IntValue millionFurnacePowerToGenerate;


    public static ModConfigSpec.IntValue ironFurnaceGeneration;
    public static ModConfigSpec.IntValue goldFurnaceGeneration;
    public static ModConfigSpec.IntValue diamondFurnaceGeneration;
    public static ModConfigSpec.IntValue emeraldFurnaceGeneration;
    public static ModConfigSpec.IntValue obsidianFurnaceGeneration;
    public static ModConfigSpec.IntValue crystalFurnaceGeneration;
    public static ModConfigSpec.IntValue netheriteFurnaceGeneration;
    public static ModConfigSpec.IntValue copperFurnaceGeneration;
    public static ModConfigSpec.IntValue silverFurnaceGeneration;
    public static ModConfigSpec.IntValue millionFurnaceGeneration;


    public static ModConfigSpec.IntValue furnaceEnergyCapacityTier0;
    public static ModConfigSpec.IntValue furnaceEnergyCapacityTier1;
    public static ModConfigSpec.IntValue furnaceEnergyCapacityTier2;

    public static ModConfigSpec.IntValue ironFurnaceTier;
    public static ModConfigSpec.IntValue goldFurnaceTier;
    public static ModConfigSpec.IntValue diamondFurnaceTier;
    public static ModConfigSpec.IntValue emeraldFurnaceTier;
    public static ModConfigSpec.IntValue obsidianFurnaceTier;
    public static ModConfigSpec.IntValue crystalFurnaceTier;
    public static ModConfigSpec.IntValue netheriteFurnaceTier;
    public static ModConfigSpec.IntValue copperFurnaceTier;
    public static ModConfigSpec.IntValue silverFurnaceTier;
    public static ModConfigSpec.IntValue millionFurnaceTier;

    public static ModConfigSpec.IntValue recipeMaxXPLevel;


    public static ModConfigSpec.BooleanValue showErrors;
    public static ModConfigSpec.BooleanValue disableLightupdates;
    

    //ALLTHEMODS
    public static ModConfigSpec.IntValue vibraniumFurnaceSpeed;
    public static ModConfigSpec.IntValue unobtainiumFurnaceSpeed;
    public static ModConfigSpec.IntValue allthemodiumFurnaceSpeed;
    public static ModConfigSpec.IntValue vibraniumFurnaceSmeltMult;
    public static ModConfigSpec.IntValue unobtainiumFurnaceSmeltMult;
    public static ModConfigSpec.IntValue allthemodiumFurnaceSmeltMult;

    public static ModConfigSpec.IntValue allthemodiumGeneration;
    public static ModConfigSpec.IntValue vibraniumGeneration;
    public static ModConfigSpec.IntValue unobtainiumGeneration;

    public static ModConfigSpec.IntValue allthemodiumFurnaceTier;
    public static ModConfigSpec.IntValue vibraniumFurnaceTier;
    public static ModConfigSpec.IntValue unobtainiumFurnaceTier;

    static final ModConfigSpec SPEC;

    static {
   
        BUILDER.comment("Settings").push(CATEGORY_GENERAL);
        BUILDER.pop();

        BUILDER.comment("Furnace Settings").push(CATEGORY_FURNACE);

        setupFurnacesConfig();
        setupGenerationConfig();

        BUILDER.pop();

        BUILDER.comment("Modded Furnace Settings").push(CATEGORY_MODDED_FURNACE);

        setupModdedFurnacesConfig();

        BUILDER.pop();


        BUILDER.comment("Misc").push(CATEGORY_MISC);
        
        disableLightupdates = BUILDER
                .comment(" Enable or disable light-updates, furances will no longer emit light, true = disable").define("misc.lightupdates", false);
        
        BUILDER.pop();
        
        SPEC = BUILDER.build();
    }


    private static void setupGenerationConfig() {
        ironFurnaceGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 40")
                .defineInRange("iron_furnace.generation", 40, 1, 100000);
        goldFurnaceGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 160")
                .defineInRange("gold_furnace.generation", 160, 1, 100000);
        diamondFurnaceGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 240")
                .defineInRange("diamond_furnace.generation", 240, 1, 100000);
        emeraldFurnaceGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 320")
                .defineInRange("emerald_furnace.generation", 320, 1, 100000);
        obsidianFurnaceGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 500")
                .defineInRange("obsidian_furnace.generation", 500, 1, 100000);
        crystalFurnaceGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 360")
                .defineInRange("crystal_furnace.generation", 360, 1, 100000);
        netheriteFurnaceGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 1000")
                .defineInRange("netherite_furnace.generation", 1000, 1, 100000);
        millionFurnaceGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 2000")
                .defineInRange("rainbow_furnace.generation", 2000, 1, 100000);
        copperFurnaceGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 40")
                .defineInRange("copper_furnace.generation", 40, 1, 100000);
        silverFurnaceGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 100")
                .defineInRange("silver_furnace.generation", 100, 1, 100000);

    }

    private static void setupFurnacesConfig() {


        furnaceEnergyCapacityTier0 = BUILDER
                .comment(" How much energy can be stored in tier 0 furnaces.\n Default: 80 000")
                .defineInRange("energy.tier_0", 80000, 4000, Integer.MAX_VALUE);

        furnaceEnergyCapacityTier1 = BUILDER
                .comment(" How much energy can be stored in tier 1 furnaces.\n Default: 200 000")
                .defineInRange("energy.tier_1", 200000, 4000, Integer.MAX_VALUE);

        furnaceEnergyCapacityTier2 = BUILDER
                .comment(" How much energy can be stored in tier 2 furnaces.\n Default: 1 000 000")
                .defineInRange("energy.tier_2", 1000000, 4000, Integer.MAX_VALUE);

        ironFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 0")
                .defineInRange("iron_furnace.tier", 0, 0, 2);

        copperFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 0")
                .defineInRange("copper_furnace.tier", 0, 0, 2);

        goldFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 1")
                .defineInRange("gold_furnace.tier", 1, 0, 2);

        diamondFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 1")
                .defineInRange("diamond_furnace.tier", 2, 0, 2);

        emeraldFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 1")
                .defineInRange("emerald_furnace.tier", 2, 0, 2);

        silverFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 1")
                .defineInRange("silver_furnace.tier", 1, 0, 2);

        crystalFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 2")
                .defineInRange("crystal_furnace.tier", 2, 0, 2);

        obsidianFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 2")
                .defineInRange("obsidian_furnace.tier", 2, 0, 2);

        netheriteFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 2")
                .defineInRange("netherite_furnace.tier", 2, 0, 2);

        millionFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 2")
                .defineInRange("million_furnace.tier", 2, 0, 2);
        

        ironFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 160")
                .defineInRange("iron_furnace.speed", 160, 2, 72000);

        goldFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 120")
                .defineInRange("gold_furnace.speed", 120, 2, 72000);

        diamondFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 80")
                .defineInRange("diamond_furnace.speed", 80, 2, 72000);

        emeraldFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 40")
                .defineInRange("emerald_furnace.speed", 40, 2, 72000);

        obsidianFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 20")
                .defineInRange("obsidian_furnace.speed", 20, 2, 72000);

        crystalFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 40")
                .defineInRange("crystal_furnace.speed", 40, 2, 72000);

        netheriteFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 5")
                .defineInRange("netherite_furnace.speed", 5, 2, 72000);

        copperFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 180")
                .defineInRange("copper_furnace.speed", 180, 2, 72000);

        silverFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 140")
                .defineInRange("silver_furnace.speed", 140, 2, 72000);

        millionFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 20")
                .defineInRange("rainbow_furnace.speed", 20, 2, 72000);


        millionFurnacePowerToGenerate = BUILDER
                .comment(" How much power the Rainbow Furnace will generate.\n Default: 50000")
                .defineInRange("rainbow_furnace.rainbow_generation", 50000, 1, 100000000);


        recipeMaxXPLevel = BUILDER
                .comment(" How many levels of experience that can be stored in recipes stored in the furnace, after the experience stored in the recipe reaches this value (in levels) it will be voided.\n Default: 100 \n 100 levels is 30971 XP")
                .defineInRange("recipeMaxXPLevel.level", 100, 1, 1000);

    }

    private static void setupModdedFurnacesConfig() {

        allthemodiumFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 5")
                .defineInRange("allthemodium_furnace.speed", 5, 1, 72000);
        vibraniumFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 3")
                .defineInRange("vibranium_furnace.speed", 3, 1, 72000);
        unobtainiumFurnaceSpeed = BUILDER
                .comment(" Number of ticks to complete one smelting operation.\n 200 ticks is what a regular furnace takes.\n Default: 1")
                .defineInRange("unobtainium_furnace.speed", 1, 1, 72000);
        allthemodiumFurnaceSmeltMult = BUILDER
                .comment(" Number of items that can be smelted at once. The regular furnace only smelts 1 item at once of course.\n Default: 16")
                .defineInRange("allthemodium_furnace.mult", 16, 1, 64);
        vibraniumFurnaceSmeltMult = BUILDER
                .comment(" Number of items that can be smelted at once. The regular furnace only smelts 1 item at once of course.\n Default: 32")
                .defineInRange("vibranium_furnace.mult", 32, 1, 64);
        unobtainiumFurnaceSmeltMult = BUILDER
                .comment(" Number of items that can be smelted at once. The regular furnace only smelts 1 item at once of course.\n Default: 64")
                .defineInRange("unobtainium_furnace.mult", 64, 1, 64);
        allthemodiumGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 2000")
                .defineInRange("allthemodium_furnace.generation", 2000, 1, 100000);
        vibraniumGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 3000")
                .defineInRange("vibranium_furnace.generation", 3000, 1, 100000);
        unobtainiumGeneration = BUILDER
                .comment(" How much RF to generate per tick\n Default: 5000")
                .defineInRange("unobtainium_furnace.generation", 5000, 1, 100000);

        allthemodiumFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 2")
                .defineInRange("allthemodium_furnace.tier", 2, 0, 2);

        vibraniumFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 2")
                .defineInRange("vibranium_furnace.tier", 2, 0, 2);

        unobtainiumFurnaceTier = BUILDER
                .comment(" What tier this furnace should be.\n Default: 2")
                .defineInRange("unobtainium_furnace.tier", 2, 0, 2);

    }






    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        
    }


    /**

    @SubscribeEvent
    public static void player(final TickEvent.PlayerTickEvent event) {

        if (Config.disableWebContent.get())
        {
            return;
        }

        if (!run)
        {
            return;
        }
        if (!event.player.level().isClientSide) {
            if (event.player.getServer().getAdvancements() != null)
            {
                Advancement adv = event.player.getServer().getAdvancements().getAdvancement(new ResourceLocation(IronFurnaces.MOD_ID, "coal"));
                if (adv != null)
                {
                    if (!((ServerPlayer) event.player).getAdvancements().getOrStartProgress(adv).isDone()) {
                        Player player = getPlayer(event.player.level());
                        if (player != null && player == event.player) {
                            event.player.level().addFreshEntity(new ItemEntity(event.player.level(), event.player.position().x, event.player.position().y, event.player.position().z, new ItemStack(Registration.RAINBOW_COAL.get())));

                        }
                    }
                }
            }




        }
        run = false;


    }


    @Nullable
    public static Player getPlayer(Level world) {

        if (Config.disableWebContent.get())
        {
            return null;
        }

        if (world == null) {
            return null;
        }
        try {
            URL newestURL = new URL("https://raw.githubusercontent.com/Qelifern/IronFurnaces/" + IronFurnaces.GITHUB_BRANCH + "/update/uuids.json");
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader(newestURL.openStream()));
            JsonObject rootobj = root.getAsJsonObject();
            JsonArray array = rootobj.get("values").getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                if (world.getPlayerByUUID(UUID.fromString(array.get(i).getAsString())) != null) {
                    return world.getPlayerByUUID(UUID.fromString(array.get(i).getAsString()));
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return null;
    }

    **/
}
