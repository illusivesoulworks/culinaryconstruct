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
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import top.theillusivec4.culinaryconstruct.common.registry.RegistryReference;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class SandwichItem extends Item {

  public SandwichItem() {
    super(new Item.Properties().group(ItemGroup.FOOD));
    this.setRegistryName(RegistryReference.SANDWICH);
  }

  public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
      ItemStack sub = new ItemStack(this);
      CulinaryNBTHelper.setTagSize(sub, 5);
      CulinaryNBTHelper.setTagIngredientsList(sub, NonNullList.from(ItemStack.EMPTY,
          new ItemStack(Items.NETHER_STAR),
          new ItemStack(Items.NETHER_STAR),
          new ItemStack(Items.NETHER_STAR),
          new ItemStack(Items.NETHER_STAR),
          new ItemStack(Items.NETHER_STAR),
          new ItemStack(Items.BREAD)));
      CulinaryNBTHelper.setTagFood(sub, 20);
      CulinaryNBTHelper.setTagDepth(sub, 0);
      CulinaryNBTHelper.setTagSaturation(sub, 1.0F);
      CulinaryNBTHelper.setTagBonus(sub, 2);
      items.add(sub);
    }
  }
}
