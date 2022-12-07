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

import com.illusivesoulworks.culinaryconstruct.common.util.CulinaryNBT;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SandwichItem extends CulinaryItemBase {

  @Override
  public void fillItemCategory(@Nonnull CreativeModeTab group,
                               @Nonnull NonNullList<ItemStack> items) {

    if (this.allowedIn(group)) {
      ItemStack sub = new ItemStack(this);
      CulinaryNBT.setBase(sub, new ItemStack(Items.BREAD));
      generateCreativeNBT(sub);
      items.add(sub);
    }
  }
}
