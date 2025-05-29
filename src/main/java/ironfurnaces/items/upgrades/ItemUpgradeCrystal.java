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

package ironfurnaces.items.upgrades;

import ironfurnaces.init.Registration;

public class ItemUpgradeCrystal extends ItemUpgrade {


    public ItemUpgradeCrystal(Properties properties) {
        super(properties, Registration.DIAMOND_FURNACE.get(), Registration.CRYSTAL_FURNACE.get());
    }
}
