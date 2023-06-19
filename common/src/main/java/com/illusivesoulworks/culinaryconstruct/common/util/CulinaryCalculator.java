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

import com.illusivesoulworks.culinaryconstruct.api.CulinaryConstructApi;
import com.illusivesoulworks.culinaryconstruct.api.ICulinaryIngredient;
import com.illusivesoulworks.culinaryconstruct.common.config.CulinaryConstructConfig;
import com.illusivesoulworks.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import com.illusivesoulworks.culinaryconstruct.platform.Services;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class CulinaryCalculator {

  private final NonNullList<ItemStack> ingredients;
  private final NonNullList<ItemStack> solids;
  private final List<ItemStack> processed = new ArrayList<>();
  private final ItemStack base;
  private final List<Integer> liquidColors;

  private int food;
  private float saturation;
  private int complexity;

  public CulinaryCalculator(ItemStack baseIn, NonNullList<ItemStack> ingredientsIn) {
    this.base = baseIn;
    this.ingredients = ingredientsIn;
    this.solids = NonNullList.create();
    this.liquidColors = new ArrayList<>();
  }

  public ItemStack getResult() {
    List<ItemStack> process = new ArrayList<>(this.ingredients);
    this.processed.clear();
    int maxFood;

    OutputType type;
    if (CulinaryTags.isBread(base)) {
      type = OutputType.SANDWICH;
      maxFood = CulinaryConstructConfig.SERVER.maxFoodPerSandwich.get();
    } else if (CulinaryTags.isBowl(base)) {
      type = OutputType.BOWL;
      maxFood = Integer.MAX_VALUE;
    } else {
      return ItemStack.EMPTY;
    }

    if (type == OutputType.SANDWICH) {
      process.add(this.base);
    }

    for (ItemStack stack : process) {
      if (!stack.isEmpty()) {
        if (!this.processStack(stack)) {
          return ItemStack.EMPTY;
        }
      }
    }

    if (type == OutputType.SANDWICH && !this.liquidColors.isEmpty()) {
      return ItemStack.EMPTY;
    }

    if (this.saturation <= 0 || this.food <= 0) {
      return ItemStack.EMPTY;
    }
    this.saturation /= this.food;
    int count = 1;

    if (type == OutputType.SANDWICH) {
      count = (int) Math.ceil(this.food / (double) maxFood);
    }
    this.food = (int) Math.ceil(this.food / (double) count);
    int quality = Mth.clamp(this.complexity - (this.getSize() / 2) + 1, 0, 4);
    this.saturation *= 1.0F + ((quality - 2) * 0.3F);
    ItemStack result =
        type == OutputType.SANDWICH ? new ItemStack(CulinaryConstructRegistry.SANDWICH.get())
            : new ItemStack(CulinaryConstructRegistry.BOWL.get());
    CulinaryNBT.setSize(result, this.getSize());
    CulinaryNBT.setIngredientsList(result, this.ingredients);
    CulinaryNBT.setFoodAmount(result, this.food);
    CulinaryNBT.setSaturation(result, this.saturation);
    CulinaryNBT.setQuality(result, quality);
    CulinaryNBT.setBase(result, this.base);
    CulinaryNBT.setSolids(result, this.solids);
    CulinaryNBT.setSolidsSize(result, this.solids.size());
    CulinaryNBT.setOriginalCount(result, count);

    if (!this.liquidColors.isEmpty() && type != OutputType.SANDWICH) {
      this.liquidColors.removeIf(Objects::isNull);
      CulinaryNBT.setLiquids(result, this.liquidColors);
    }
    result.setCount(count);
    return result;
  }

  public boolean processStack(ItemStack stack) {
    FoodProperties food = Services.REGISTRY.getFoodProperties(stack, null);
    Optional<ICulinaryIngredient> culinary = CulinaryConstructApi.getCulinaryIngredient(stack);
    int foodAmount = 0;
    float saturationAmount = 0;
    boolean valid = true;

    if (culinary.isPresent()) {
      culinary.ifPresent(ingredient -> {
        if (ingredient.isLiquid()) {
          this.liquidColors.add(ingredient.getLiquidColor());
        } else {
          this.solids.add(stack);
        }
      });
      valid = culinary.map(ICulinaryIngredient::isValid).orElse(true);
      foodAmount = culinary.map(ICulinaryIngredient::getFoodAmount).orElse(0);
      saturationAmount = culinary.map(ICulinaryIngredient::getSaturation).orElse(0.0F);
    } else if (food != null) {
      foodAmount = food.getNutrition();
      saturationAmount = food.getSaturationModifier();
      this.solids.add(stack);
    }

    if (!valid) {
      return false;
    }
    this.food += foodAmount;
    this.saturation += saturationAmount * foodAmount;
    boolean unique = true;

    for (ItemStack existing : this.processed) {
      if (!existing.isEmpty() && ItemStack.matches(existing, stack)) {
        unique = false;
        break;
      }
    }

    if (unique) {
      this.complexity++;
    }
    this.processed.add(stack);
    return true;
  }

  public int getSize() {
    return this.ingredients.size();
  }

  enum OutputType {
    SANDWICH, BOWL
  }
}
