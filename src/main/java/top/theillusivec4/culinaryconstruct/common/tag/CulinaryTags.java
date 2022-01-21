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

import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag.Named;
import net.minecraft.tags.ItemTags;

public class CulinaryTags {

  public static final Named<Item> BREAD = tag("bread");
  public static final Named<Item> BOWL = tag("bowls");

  private static Named<Item> tag(String name) {
    return ItemTags.bind("forge:" + name);
  }
}
