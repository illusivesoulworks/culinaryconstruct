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

package com.illusivesoulworks.culinaryconstruct.api;

import com.illusivesoulworks.culinaryconstruct.CulinaryConstructConstants;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.resources.ResourceLocation;

public class CulinaryIngredientLookup {

  public static final ItemApiLookup<ICulinaryIngredient, Void> INSTANCE = ItemApiLookup.get(
      new ResourceLocation(CulinaryConstructConstants.MOD_ID, "culinary_ingredient"),
      ICulinaryIngredient.class, Void.class);
}
