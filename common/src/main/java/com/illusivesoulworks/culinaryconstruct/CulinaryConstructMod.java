/*
 * Copyright (C) 2018-2022 Illusive Soulworks
 *
 * Culinary Construct is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Culinary Construct is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Culinary Construct.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.culinaryconstruct;

import com.illusivesoulworks.culinaryconstruct.common.capability.CulinaryIngredients;
import com.illusivesoulworks.culinaryconstruct.common.config.CulinaryConstructConfig;
import com.illusivesoulworks.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import com.illusivesoulworks.spectrelib.config.SpectreConfig;
import com.illusivesoulworks.spectrelib.config.SpectreConfigLoader;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CulinaryConstructMod {

  public static void setup() {
    SpectreConfig config = SpectreConfigLoader.add(SpectreConfig.Type.SERVER,
        CulinaryConstructConfig.serverSpec, CulinaryConstructConstants.MOD_ID);
    config.addLoadListener(cfg -> CulinaryConstructConfig.bake());
    config.addReloadListener(cfg -> CulinaryConstructConfig.bake());
    CulinaryIngredients.setup();
    CulinaryConstructRegistry.setup();
  }
}