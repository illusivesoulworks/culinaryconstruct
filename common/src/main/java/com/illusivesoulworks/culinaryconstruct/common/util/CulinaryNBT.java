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

package com.illusivesoulworks.culinaryconstruct.common.util;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

public class CulinaryNBT {

  public static final String TAG_BASE = "Base";
  public static final String TAG_INGREDIENTS = "Ingredients";
  public static final String TAG_FOOD = "Food";
  public static final String TAG_SATURATION = "Saturation";
  public static final String TAG_SIZE = "Size";
  public static final String TAG_QUALITY = "Quality";
  public static final String TAG_LIQUIDS = "Liquids";
  public static final String TAG_SOLIDS = "Solids";
  public static final String TAG_SOLIDS_SIZE = "SolidsSize";
  public static final String TAG_COUNT = "Count";

  public static CompoundTag getTagSafe(ItemStack stack) {
    return stack.getOrCreateTag();
  }

  public static ItemStack getBase(ItemStack stack) {
    CompoundTag compound = getTagSafe(stack);
    return ItemStack.of(compound.getCompound(TAG_BASE));
  }

  public static int getQuality(ItemStack stack) {
    CompoundTag compound = getTagSafe(stack);
    return compound.getInt(TAG_QUALITY);
  }

  public static int getSize(ItemStack stack) {
    CompoundTag compound = getTagSafe(stack);
    return compound.getInt(TAG_SIZE);
  }

  public static int getFoodAmount(ItemStack stack) {
    CompoundTag compound = getTagSafe(stack);
    return compound.getInt(TAG_FOOD);
  }

  public static float getSaturation(ItemStack stack) {
    CompoundTag compound = getTagSafe(stack);
    return compound.getFloat(TAG_SATURATION);
  }

  public static NonNullList<ItemStack> getIngredientsList(ItemStack stack) {
    CompoundTag compound = getTagSafe(stack);
    CompoundTag tag = compound.getCompound(TAG_INGREDIENTS);
    NonNullList<ItemStack> list = NonNullList.withSize(getSize(stack), ItemStack.EMPTY);
    ContainerHelper.loadAllItems(tag, list);
    return list;
  }

  @Nullable
  public static List<Integer> getLiquids(ItemStack stack) {
    CompoundTag compound = getTagSafe(stack);

    if (!compound.contains(TAG_LIQUIDS)) {
      return null;
    }
    ListTag tag = compound.getList(TAG_LIQUIDS, 3);
    List<Integer> liquids = new ArrayList<>();
    tag.forEach(nbt -> liquids.add(((IntTag) nbt).getAsInt()));
    return liquids;
  }

  public static int getSolidsSize(ItemStack stack) {
    CompoundTag compound = getTagSafe(stack);
    return compound.getInt(TAG_SOLIDS_SIZE);
  }

  public static NonNullList<ItemStack> getSolids(ItemStack stack) {
    CompoundTag compound = getTagSafe(stack);
    CompoundTag tag = compound.getCompound(TAG_SOLIDS);
    NonNullList<ItemStack> list = NonNullList.withSize(getSolidsSize(stack), ItemStack.EMPTY);
    ContainerHelper.loadAllItems(tag, list);
    return list;
  }

  public static int getOriginalCount(ItemStack stack) {
    CompoundTag compound = getTagSafe(stack);
    return compound.getInt(TAG_COUNT);
  }

  public static void setBase(ItemStack stack, ItemStack base) {
    getTagSafe(stack).put(TAG_BASE, base.save(new CompoundTag()));
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
    CompoundTag compound = getTagSafe(stack);
    CompoundTag tag = new CompoundTag();
    ContainerHelper.saveAllItems(tag, ingredients);
    compound.put(TAG_INGREDIENTS, tag);
  }

  public static void setLiquids(ItemStack stack, List<Integer> liquids) {
    CompoundTag compound = getTagSafe(stack);
    ListTag tag = new ListTag();
    liquids.forEach(liquid -> tag.add(IntTag.valueOf(liquid)));
    compound.put(TAG_LIQUIDS, tag);
  }

  public static void setSolidsSize(ItemStack stack, int size) {
    getTagSafe(stack).putInt(TAG_SOLIDS_SIZE, size);
  }

  public static void setSolids(ItemStack stack, NonNullList<ItemStack> solids) {
    CompoundTag compound = getTagSafe(stack);
    CompoundTag tag = new CompoundTag();
    ContainerHelper.saveAllItems(tag, solids);
    compound.put(TAG_SOLIDS, tag);
  }

  public static void setOriginalCount(ItemStack stack, int count) {
    getTagSafe(stack).putInt(TAG_COUNT, count);
  }
}
