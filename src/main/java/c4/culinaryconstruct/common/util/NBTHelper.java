/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.common.util;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class NBTHelper {

    public static final String TAG_INGREDIENTS = "Ingredients";
    public static final String TAG_FOOD = "Food";
    public static final String TAG_SATURATION = "Saturation";
    public static final String TAG_SIZE = "Size";
    public static final String TAG_BONUS = "Bonus";

    public static NBTTagCompound getCompoundSafe(ItemStack stack) {
        if (!stack.isEmpty() && stack.hasTagCompound()) {
            return stack.getTagCompound();
        }
        return new NBTTagCompound();
    }

    public static int getBonus(ItemStack stack) {
        NBTTagCompound compound = getCompoundSafe(stack);
        return compound.getInteger(TAG_BONUS);
    }

    public static int getSize(ItemStack stack) {
        NBTTagCompound compound = getCompoundSafe(stack);
        return compound.getInteger(TAG_SIZE);
    }

    public static int getFoodAmount(ItemStack stack) {
        NBTTagCompound compound = getCompoundSafe(stack);
        return compound.getInteger(TAG_FOOD);
    }

    public static float getSaturationModifier(ItemStack stack) {
        NBTTagCompound compound = getCompoundSafe(stack);
        return compound.getFloat(TAG_SATURATION);
    }

    public static NonNullList<ItemStack> getIngredientsList(ItemStack stack, boolean includeBread) {
        NBTTagCompound compound = getCompoundSafe(stack);
        NBTTagCompound tag = compound.getCompoundTag(TAG_INGREDIENTS);
        NonNullList<ItemStack> list = NonNullList.withSize(getSize(stack) + (includeBread ? 1 : 0), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, list);
        return list;
    }

    public static void setTagBonus(ItemStack stack, int bonus) {
        NBTTagCompound compound = getCompoundSafe(stack);
        compound.setInteger(TAG_BONUS, bonus);
        stack.setTagCompound(compound);
    }

    public static void setTagSize(ItemStack stack, int size) {
        NBTTagCompound compound = getCompoundSafe(stack);
        compound.setInteger(TAG_SIZE, size);
        stack.setTagCompound(compound);
    }

    public static void setTagFood(ItemStack stack, int food) {
        NBTTagCompound compound = getCompoundSafe(stack);
        compound.setInteger(TAG_FOOD, food);
        stack.setTagCompound(compound);
    }

    public static void setTagSaturation(ItemStack stack, float saturation) {
        NBTTagCompound compound = getCompoundSafe(stack);
        compound.setFloat(TAG_SATURATION, saturation);
        stack.setTagCompound(compound);
    }

    public static void setIngredientsList(ItemStack stack, NonNullList<ItemStack> ingredients) {
        NBTTagCompound compound = getCompoundSafe(stack);
        NBTTagCompound tag = new NBTTagCompound();
        ItemStackHelper.saveAllItems(tag, ingredients);
        compound.setTag(TAG_INGREDIENTS, tag);
        stack.setTagCompound(compound);
    }
}
