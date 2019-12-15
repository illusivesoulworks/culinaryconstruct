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

package top.theillusivec4.culinaryconstruct.common.util;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class CulinaryNBTHelper {

  public static final String TAG_INGREDIENTS = "Ingredients";
  public static final String TAG_DEPTH = "Depth";
  public static final String TAG_FOOD = "Food";
  public static final String TAG_SATURATION = "Saturation";
  public static final String TAG_SIZE = "Size";
  public static final String TAG_BONUS = "Bonus";

  public static CompoundNBT getCompoundSafe(ItemStack stack) {
    if (!stack.isEmpty() && stack.hasTag()) {
      return stack.getTag();
    }
    return new CompoundNBT();
  }

  public static int getBonus(ItemStack stack) {
    CompoundNBT compound = getCompoundSafe(stack);
    return compound.getInt(TAG_BONUS);
  }

  public static int getSize(ItemStack stack) {
    CompoundNBT compound = getCompoundSafe(stack);
    return compound.getInt(TAG_SIZE);
  }

  public static int getDepth(ItemStack stack) {
    CompoundNBT compound = getCompoundSafe(stack);
    return compound.getInt(TAG_DEPTH);
  }

  public static int getFoodAmount(ItemStack stack) {
    CompoundNBT compound = getCompoundSafe(stack);
    return compound.getInt(TAG_FOOD);
  }

  public static float getSaturationModifier(ItemStack stack) {
    CompoundNBT compound = getCompoundSafe(stack);
    return compound.getFloat(TAG_SATURATION);
  }

  public static NonNullList<ItemStack> getIngredientsList(ItemStack stack, boolean includeHolder) {
    CompoundNBT compound = getCompoundSafe(stack);
    CompoundNBT tag = compound.getCompound(TAG_INGREDIENTS);
    NonNullList<ItemStack> list = NonNullList
        .withSize(getSize(stack) + (includeHolder ? 1 : 0), ItemStack.EMPTY);
    ItemStackHelper.loadAllItems(tag, list);
    return list;
  }

  public static void setTagBonus(ItemStack stack, int bonus) {
    CompoundNBT compound = getCompoundSafe(stack);
    compound.putInt(TAG_BONUS, bonus);
    stack.setTag(compound);
  }

  public static void setTagSize(ItemStack stack, int size) {
    CompoundNBT compound = getCompoundSafe(stack);
    compound.putInt(TAG_SIZE, size);
    stack.setTag(compound);
  }

  public static void setTagDepth(ItemStack stack, int depth) {
    CompoundNBT compound = getCompoundSafe(stack);
    compound.putInt(TAG_DEPTH, depth);
    stack.setTag(compound);
  }

  public static void setTagFood(ItemStack stack, int food) {
    CompoundNBT compound = getCompoundSafe(stack);
    compound.putInt(TAG_FOOD, food);
    stack.setTag(compound);
  }

  public static void setTagSaturation(ItemStack stack, float saturation) {
    CompoundNBT compound = getCompoundSafe(stack);
    compound.putFloat(TAG_SATURATION, saturation);
    stack.setTag(compound);
  }

  public static void setTagIngredientsList(ItemStack stack, NonNullList<ItemStack> ingredients) {
    CompoundNBT compound = getCompoundSafe(stack);
    CompoundNBT tag = new CompoundNBT();
    ItemStackHelper.saveAllItems(tag, ingredients);
    compound.put(TAG_INGREDIENTS, tag);
    stack.setTag(compound);
  }
}
