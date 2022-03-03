/*
 * Copyright (c) 2018-2019 C4
 *
 * This file is part of Culinary Construct, a mod made for Minecraft.
 *
 * Culinary Construct is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Culinary Construct is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Culinary Construct.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.culinaryconstruct.common.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

public class CulinaryTags {

  public static final TagKey<Item> BREAD = forgeTag("bread");
  public static final TagKey<Item> BOWL = forgeTag("bowls");

  private static TagKey<Item> forgeTag(String name) {
    return ItemTags.create(new ResourceLocation("forge", name));
  }

  public static boolean isBread(ItemStack stack) {
    return stack.is(BREAD);
  }

  public static boolean isBowl(ItemStack stack) {
    return stack.is(BOWL);
  }
}
