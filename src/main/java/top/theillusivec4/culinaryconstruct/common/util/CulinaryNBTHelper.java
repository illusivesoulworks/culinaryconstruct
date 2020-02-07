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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;

public class CulinaryNBTHelper {

  public static final String TAG_BASE = "Base";
  public static final String TAG_INGREDIENTS = "Ingredients";
  public static final String TAG_FOOD = "Food";
  public static final String TAG_SATURATION = "Saturation";
  public static final String TAG_SIZE = "Size";
  public static final String TAG_QUALITY = "Quality";
  public static final String TAG_LIQUIDS = "Liquids";
  public static final String TAG_SOLIDS = "Solids";
  public static final String TAG_SOLIDS_SIZE = "SolidsSize";

  public static CompoundNBT getTagSafe(ItemStack stack) {
    return stack.getOrCreateTag();
  }

  public static ItemStack getBase(ItemStack stack) {
    CompoundNBT compound = getTagSafe(stack);
    return ItemStack.read(compound.getCompound(TAG_BASE));
  }

  public static int getQuality(ItemStack stack) {
    CompoundNBT compound = getTagSafe(stack);
    return compound.getInt(TAG_QUALITY);
  }

  public static int getSize(ItemStack stack) {
    CompoundNBT compound = getTagSafe(stack);
    return compound.getInt(TAG_SIZE);
  }

  public static int getFoodAmount(ItemStack stack) {
    CompoundNBT compound = getTagSafe(stack);
    return compound.getInt(TAG_FOOD);
  }

  public static float getSaturation(ItemStack stack) {
    CompoundNBT compound = getTagSafe(stack);
    return compound.getFloat(TAG_SATURATION);
  }

  public static NonNullList<ItemStack> getIngredientsList(ItemStack stack) {
    CompoundNBT compound = getTagSafe(stack);
    CompoundNBT tag = compound.getCompound(TAG_INGREDIENTS);
    NonNullList<ItemStack> list = NonNullList.withSize(getSize(stack), ItemStack.EMPTY);
    ItemStackHelper.loadAllItems(tag, list);
    return list;
  }

  @Nullable
  public static List<Integer> getLiquids(ItemStack stack) {
    CompoundNBT compound = getTagSafe(stack);

    if (!compound.contains(TAG_LIQUIDS)) {
      return null;
    }
    ListNBT tag = compound.getList(TAG_LIQUIDS, 3);
    List<Integer> liquids = new ArrayList<>();
    tag.forEach(nbt -> liquids.add(((IntNBT) nbt).getInt()));
    return liquids;
  }

  public static int getSolidsSize(ItemStack stack) {
    CompoundNBT compound = getTagSafe(stack);
    return compound.getInt(TAG_SOLIDS_SIZE);
  }

  public static NonNullList<ItemStack> getSolids(ItemStack stack) {
    CompoundNBT compound = getTagSafe(stack);
    CompoundNBT tag = compound.getCompound(TAG_SOLIDS);
    NonNullList<ItemStack> list = NonNullList.withSize(getSolidsSize(stack), ItemStack.EMPTY);
    ItemStackHelper.loadAllItems(tag, list);
    return list;
  }

  public static void setBase(ItemStack stack, ItemStack base) {
    getTagSafe(stack).put(TAG_BASE, base.write(new CompoundNBT()));
  }

  public static void setQuality(ItemStack stack, int quality) {
    getTagSafe(stack).putInt(TAG_QUALITY, quality);
  }

  public static void setSize(ItemStack stack, int size) {
    getTagSafe(stack).putInt(TAG_SIZE, size);
  }

  public static void setFoodAmount(ItemStack stack, int food) {
    getTagSafe(stack).putInt(TAG_FOOD, food);
  }

  public static void setSaturation(ItemStack stack, float saturation) {
    getTagSafe(stack).putFloat(TAG_SATURATION, saturation);
  }

  public static void setIngredientsList(ItemStack stack, NonNullList<ItemStack> ingredients) {
    CompoundNBT compound = getTagSafe(stack);
    CompoundNBT tag = new CompoundNBT();
    ItemStackHelper.saveAllItems(tag, ingredients);
    compound.put(TAG_INGREDIENTS, tag);
  }

  public static void setLiquids(ItemStack stack, List<Integer> liquids) {
    CompoundNBT compound = getTagSafe(stack);
    ListNBT tag = new ListNBT();
    liquids.forEach(liquid -> tag.add(new IntNBT(liquid)));
    compound.put(TAG_LIQUIDS, tag);
  }

  public static void setSolidsSize(ItemStack stack, int size) {
    getTagSafe(stack).putInt(TAG_SOLIDS_SIZE, size);
  }

  public static void setSolids(ItemStack stack, NonNullList<ItemStack> solids) {
    CompoundNBT compound = getTagSafe(stack);
    CompoundNBT tag = new CompoundNBT();
    ItemStackHelper.saveAllItems(tag, solids);
    compound.put(TAG_SOLIDS, tag);
  }
}
