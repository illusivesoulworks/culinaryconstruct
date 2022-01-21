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

package top.theillusivec4.culinaryconstruct.common.item;

import javax.annotation.Nonnull;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.NonNullList;
import top.theillusivec4.culinaryconstruct.common.registry.RegistryReference;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class SandwichItem extends CulinaryItemBase {

  public SandwichItem() {
    super();
    this.setRegistryName(RegistryReference.SANDWICH);
  }

  @Override
  public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
    if (this.allowdedIn(group)) {
      ItemStack sub = new ItemStack(this);
      CulinaryNBTHelper.setBase(sub, new ItemStack(Items.BREAD));
      generateCreativeNBT(sub);
      items.add(sub);
    }
  }
}
