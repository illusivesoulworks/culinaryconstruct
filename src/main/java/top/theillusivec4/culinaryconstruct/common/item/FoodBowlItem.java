/*
 * Copyright (c) 2018-2020 C4
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import top.theillusivec4.culinaryconstruct.common.registry.RegistryReference;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class FoodBowlItem extends CulinaryItemBase {

  public FoodBowlItem() {
    super();
    this.setRegistryName(RegistryReference.FOOD_BOWL);
  }

  @Override
  public ItemStack getContainerItem(ItemStack itemStack) {
    return CulinaryNBTHelper.getBase(itemStack);
  }

  @Override
  public boolean hasContainerItem(ItemStack stack) {
    return true;
  }

  @Nonnull
  @Override
  public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World worldIn,
      @Nonnull LivingEntity livingEntity) {
    super.onItemUseFinish(stack, worldIn, livingEntity);
    return getContainerItem(stack);
  }

  @Override
  public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
      ItemStack sub = new ItemStack(this);
      CulinaryNBTHelper.setBase(sub, new ItemStack(Items.BOWL));
      generateCreativeNBT(sub);
      items.add(sub);
    }
  }
}
