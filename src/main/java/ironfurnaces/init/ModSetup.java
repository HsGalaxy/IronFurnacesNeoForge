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

import net.minecraft.world.item.Item;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.Map;

public class ModSetup {


    public static final Map<Item, Integer> SMOKING_BURNS = new HashMap<>();
    public static final Map<Item, Boolean> HAS_RECIPE = new HashMap<>();
    public static final Map<Item, Boolean> HAS_RECIPE_SMOKING = new HashMap<>();
    public static final Map<Item, Boolean> HAS_RECIPE_BLASTING = new HashMap<>();

    public static void init(final FMLCommonSetupEvent event) {

    }




}
