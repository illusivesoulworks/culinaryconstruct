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

package top.theillusivec4.culinaryconstruct.common.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.culinaryconstruct.api.CulinaryConstructApi;
import top.theillusivec4.culinaryconstruct.api.capability.ICulinaryIngredient;
import top.theillusivec4.culinaryconstruct.common.CulinaryConstructConfig;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import top.theillusivec4.culinaryconstruct.common.tag.CulinaryTags;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

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
      maxFood = CulinaryConstructConfig.maxFoodPerSandwich;
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
        type == OutputType.SANDWICH ? new ItemStack(CulinaryConstructRegistry.SANDWICH)
            : new ItemStack(CulinaryConstructRegistry.FOOD_BOWL);
    CulinaryNBTHelper.setSize(result, this.getSize());
    CulinaryNBTHelper.setIngredientsList(result, this.ingredients);
    CulinaryNBTHelper.setFoodAmount(result, this.food);
    CulinaryNBTHelper.setSaturation(result, this.saturation);
    CulinaryNBTHelper.setQuality(result, quality);
    CulinaryNBTHelper.setBase(result, this.base);
    CulinaryNBTHelper.setSolids(result, this.solids);
    CulinaryNBTHelper.setSolidsSize(result, this.solids.size());

    if (!this.liquidColors.isEmpty() && type != OutputType.SANDWICH) {
      this.liquidColors.removeIf(Objects::isNull);
      CulinaryNBTHelper.setLiquids(result, this.liquidColors);
    }
    result.setCount(count);
    return result;
  }

  public boolean processStack(ItemStack stack) {
    Item item = stack.getItem();
    FoodProperties food = item.getFoodProperties(stack, null);
    LazyOptional<ICulinaryIngredient> culinary = CulinaryConstructApi.getCulinaryIngredient(stack);
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
