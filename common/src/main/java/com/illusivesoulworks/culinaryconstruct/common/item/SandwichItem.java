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

package com.illusivesoulworks.culinaryconstruct.common.item;

import com.illusivesoulworks.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import com.illusivesoulworks.culinaryconstruct.common.util.CulinaryNBT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SandwichItem extends CulinaryItemBase {

  public static ItemStack generateCreativeItem() {
    ItemStack sub = new ItemStack(CulinaryConstructRegistry.SANDWICH.get());
    CulinaryNBT.setBase(sub, new ItemStack(Items.BREAD));
    generateCreativeNBT(sub);
    return sub;
  }
}
