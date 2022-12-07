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

package com.illusivesoulworks.culinaryconstruct.common.util;

import com.illusivesoulworks.culinaryconstruct.platform.Services;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CulinaryTags {

  private static final TagKey<Item> BREAD = tag("bread");
  private static final TagKey<Item> BOWL = tag("bowls");

  private static TagKey<Item> tag(String name) {
    return Services.REGISTRY.createTag(name);
  }

  public static boolean isBread(ItemStack stack) {
    return stack.is(BREAD);
  }

  public static boolean isBowl(ItemStack stack) {
    return stack.is(BOWL);
  }
}
