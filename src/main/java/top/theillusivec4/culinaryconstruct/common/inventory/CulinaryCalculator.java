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

package top.theillusivec4.culinaryconstruct.common.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.culinaryconstruct.api.CulinaryConstructAPI;
import top.theillusivec4.culinaryconstruct.api.capability.ICulinaryIngredient;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import top.theillusivec4.culinaryconstruct.common.tag.CulinaryTags;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class CulinaryCalculator {

  private static final int MAX_FOOD = 10;

  private final NonNullList<ItemStack> ingredients;
  private final List<ItemStack> processed = new ArrayList<>();
  private final ItemStack base;
  private final OutputType type;

  private int food;
  private float saturation;
  private int complexity;
  private int count = 1;

  public CulinaryCalculator(ItemStack baseIn, NonNullList<ItemStack> ingredientsIn) {
    this.base = baseIn;
    this.ingredients = ingredientsIn;
    this.type =
        CulinaryTags.BREAD.contains(baseIn.getItem()) ? OutputType.SANDWICH : OutputType.SOUP;
  }

  public ItemStack getResult() {
    List<ItemStack> process = new ArrayList<>(this.ingredients);
    this.processed.clear();

    if (this.type == OutputType.SANDWICH) {
      process.add(this.base);
    }
    process.forEach(stack -> {
      if (!stack.isEmpty()) {
        this.processStack(stack);
      }
    });

    if (this.saturation <= 0 || this.food <= 0) {
      return ItemStack.EMPTY;
    }
    this.saturation /= this.food;
    this.count = (int) Math.ceil(this.food / (double) MAX_FOOD);
    this.food = (int) Math.ceil(this.food / (double) this.count);
    this.complexity = MathHelper.clamp(this.complexity - (this.getSize() / 2) + 1, 0, 4);
    this.saturation *= 1.0F + ((this.complexity - 2) * 0.3F);
    ItemStack result = new ItemStack(CulinaryConstructRegistry.SANDWICH);
    CulinaryNBTHelper.setSize(result, this.getSize());
    CulinaryNBTHelper.setIngredientsList(result, this.ingredients);
    CulinaryNBTHelper.setFoodAmount(result, this.food);
    CulinaryNBTHelper.setSaturation(result, this.saturation);
    CulinaryNBTHelper.setComplexity(result, this.complexity);
    CulinaryNBTHelper.setBase(result, this.base);
    result.setCount(this.count);
    return result;
  }

  public void processStack(ItemStack stack) {
    Item item = stack.getItem();
    Food food = item.getFood();
    LazyOptional<ICulinaryIngredient> culinary = CulinaryConstructAPI.getCulinaryIngredient(stack);
    int foodAmount = 0;
    float saturationAmount = 0;

    if (culinary.isPresent()) {
      foodAmount = culinary.map(ICulinaryIngredient::getFoodAmount).orElse(0);
      saturationAmount = culinary.map(ICulinaryIngredient::getSaturation).orElse(0.0F);
    } else if (food != null) {
      foodAmount = food.getHealing();
      saturationAmount = food.getSaturation();
    }
    this.food += foodAmount;
    this.saturation += saturationAmount * foodAmount;
    boolean unique = true;

    for (ItemStack existing : this.processed) {
      if (!existing.isEmpty() && ItemStack.areItemStacksEqual(existing, stack)) {
        unique = false;
        break;
      }
    }

    if (unique) {
      this.complexity++;
    }
    this.processed.add(stack);
  }

  public int getSize() {
    return this.ingredients.size();
  }

  enum OutputType {
    SANDWICH, SOUP
  }
}
