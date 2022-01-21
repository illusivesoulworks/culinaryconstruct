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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
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
  public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level worldIn,
      @Nonnull LivingEntity livingEntity) {
    ItemStack output = super.finishUsingItem(stack, worldIn, livingEntity);
    ItemStack container = getContainerItem(stack);

    if (!container.isEmpty() && livingEntity instanceof Player) {
      ItemHandlerHelper.giveItemToPlayer((Player) livingEntity, container);
    }
    return output;
  }

  @Override
  public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
    if (this.allowdedIn(group)) {
      ItemStack sub = new ItemStack(this);
      CulinaryNBTHelper.setBase(sub, new ItemStack(Items.BOWL));
      generateCreativeNBT(sub);
      CulinaryNBTHelper.setSolidsSize(sub, 5);
      CulinaryNBTHelper.setSolids(sub, NonNullList
          .of(ItemStack.EMPTY, new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHER_STAR),
              new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHER_STAR),
              new ItemStack(Items.NETHER_STAR)));
      items.add(sub);
    }
  }

  @Nonnull
  @Override
  public String getDescriptionId(@Nonnull ItemStack stack) {
    return CulinaryNBTHelper.getLiquids(stack) != null ? "item.culinaryconstruct.stew"
        : this.getDescriptionId();
  }
}
