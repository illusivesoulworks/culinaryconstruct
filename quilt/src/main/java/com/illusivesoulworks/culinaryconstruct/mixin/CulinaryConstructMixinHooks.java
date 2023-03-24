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

package com.illusivesoulworks.culinaryconstruct.mixin;

import com.illusivesoulworks.culinaryconstruct.common.item.CulinaryItemBase;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CulinaryConstructMixinHooks {

  public static boolean addEatEffect(ItemStack stack, Level level, LivingEntity livingEntity) {
    Item item = stack.getItem();

    if (item instanceof CulinaryItemBase culinaryItem) {
      FoodProperties foodProperties = culinaryItem.getFoodProperties(stack, livingEntity);

      if (foodProperties != null) {
        List<Pair<MobEffectInstance, Float>> list = foodProperties.getEffects();

        for (Pair<MobEffectInstance, Float> pair : list) {

          if (level.isClientSide || pair.getFirst() == null ||
              !(level.random.nextFloat() < pair.getSecond())) {
            continue;
          }
          livingEntity.addEffect(new MobEffectInstance(pair.getFirst()));
        }
      }
      return true;
    }
    return false;
  }

  public static boolean eat(FoodData foodData, Item item, ItemStack stack) {

    if (item instanceof CulinaryItemBase culinaryItem) {
      FoodProperties foodProperties = culinaryItem.getFoodProperties(stack, null);

      if (foodProperties != null) {
        foodData.eat(foodProperties.getNutrition(), foodProperties.getSaturationModifier());
      }
      return true;
    }
    return false;
  }

  public static Optional<Boolean> isFood(ItemStack stack, Wolf wolf) {

    if (stack.getItem() instanceof CulinaryItemBase culinaryItem) {
      FoodProperties foodProperties = culinaryItem.getFoodProperties(stack, wolf);

      if (foodProperties != null) {
        return Optional.of(foodProperties.isMeat());
      }
      return Optional.of(false);
    }
    return Optional.empty();
  }
}
