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

package com.illusivesoulworks.culinaryconstruct.client;

import com.illusivesoulworks.culinaryconstruct.CulinaryConstructConstants;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class CulinaryConstructSprites {

  public static Set<ResourceLocation> get() {
    Set<ResourceLocation> result = new HashSet<>();
    for (int i = 0; i < 5; i++) {
      result.add(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "item/sandwich/bread" + i));
      result.add(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "item/sandwich/layer" + i));
      result.add(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "item/food_bowl/layer" + i));
    }
    result.add(
        new ResourceLocation(CulinaryConstructConstants.MOD_ID, "item/food_bowl/liquid_base"));
    result.add(
        new ResourceLocation(CulinaryConstructConstants.MOD_ID, "item/food_bowl/liquid_overflow"));
    return result;
  }
}
