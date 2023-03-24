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
import com.illusivesoulworks.culinaryconstruct.platform.Services;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FoodBowlItem extends CulinaryItemBase {

  public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
    return CulinaryNBT.getBase(itemStack);
  }

  public boolean hasCraftingRemainingItem(ItemStack stack) {
    return true;
  }

  @Nonnull
  @Override
  public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level worldIn,
                                   @Nonnull LivingEntity livingEntity) {
    ItemStack output = super.finishUsingItem(stack, worldIn, livingEntity);
    ItemStack container = getCraftingRemainingItem(stack);

    if (!container.isEmpty() && livingEntity instanceof Player player) {
      Services.PLATFORM.giveItemToPlayer(container, player);
    }
    return output;
  }

  public static ItemStack generateCreativeItem() {
    ItemStack sub = new ItemStack(CulinaryConstructRegistry.BOWL.get());
    CulinaryNBT.setBase(sub, new ItemStack(Items.BOWL));
    generateCreativeNBT(sub);
    CulinaryNBT.setSolidsSize(sub, 5);
    CulinaryNBT.setSolids(sub, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.NETHER_STAR),
        new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHER_STAR),
        new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHER_STAR)));
    return sub;
  }

  @Nonnull
  @Override
  public String getDescriptionId(@Nonnull ItemStack stack) {
    return CulinaryNBT.getLiquids(stack) != null ? "item.culinaryconstruct.stew" :
        this.getDescriptionId();
  }
}
