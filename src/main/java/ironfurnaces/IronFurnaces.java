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

import com.mojang.logging.LogUtils;
import ironfurnaces.init.ClientSetup;
import ironfurnaces.init.ModSetup;
import ironfurnaces.init.Registration;
import ironfurnaces.network.Messages;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.util.EventHandler;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.List;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(IronFurnaces.MOD_ID)
public class IronFurnaces
{

    public static final String MOD_ID = "ironfurnaces";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static CreativeModeTab tabIronFurnaces;




    public IronFurnaces(IEventBus modEventBus, ModContainer modContainer) {

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);


        modEventBus.addListener(ModSetup::init);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(ClientSetup::init);
        modEventBus.register(Messages.class);

        Registration.init(modEventBus);

        NeoForge.EVENT_BUS.addListener(EventHandler::explosionEvent);




    }

    private void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        List furnaces = List.of(
                Registration.COPPER_FURNACE.get(),
                Registration.CRYSTAL_FURNACE.get(),
                Registration.DIAMOND_FURNACE.get(),
                Registration.EMERALD_FURNACE.get(),
                Registration.GOLD_FURNACE.get(),
                Registration.IRON_FURNACE.get(),
                Registration.MILLION_FURNACE.get(),
                Registration.NETHERITE_FURNACE.get(),
                Registration.OBSIDIAN_FURNACE.get(),
                Registration.SILVER_FURNACE.get(),
                Registration.ALLTHEMODIUM_FURNACE.get(),
                Registration.VIBRANIUM_FURNACE.get(),
                Registration.UNOBTAINIUM_FURNACE.get()
        );


        event.registerBlock(Capabilities.ItemHandler.BLOCK,
                (level, pos, state, be, side) -> (side == null ? new InvWrapper((Container) be) : new SidedInvWrapper((WorldlyContainer)be, side)),
                // blocks to register for
                Registration.HEATER.get());

        event.registerBlock(Capabilities.EnergyStorage.BLOCK,
                (level, pos, state, be, side) -> ((BlockWirelessEnergyHeaterTile) be).energyStorage,
                // blocks to register for
                Registration.HEATER.get());



        Iterator var3 = furnaces.iterator();
        Iterator var4 = furnaces.iterator();

        while(var3.hasNext()) {
            event.registerBlock(Capabilities.ItemHandler.BLOCK,
                    (level, pos, state, be, side) -> (side == null ? new InvWrapper((Container) be) : new SidedInvWrapper((WorldlyContainer)be, side)),
                    // blocks to register for
                    (Block) var3.next()
            );

        }

        while(var4.hasNext()) {
            event.registerBlock(Capabilities.EnergyStorage.BLOCK,
                    (level, pos, state, be, side) -> ((BlockIronFurnaceTileBase) be).energyStorage,
                    // blocks to register for
                    (Block) var4.next()
            );

        }


    }

}
