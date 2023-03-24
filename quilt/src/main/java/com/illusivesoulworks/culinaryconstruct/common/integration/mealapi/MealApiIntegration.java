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

package com.illusivesoulworks.culinaryconstruct.common.integration.mealapi;

import com.illusivesoulworks.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import com.illusivesoulworks.culinaryconstruct.common.util.CulinaryNBT;
import io.github.foundationgames.mealapi.api.v0.MealAPIInitializer;
import io.github.foundationgames.mealapi.api.v0.MealItemRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MealApiIntegration implements MealAPIInitializer {

  @Override
  public void onMealApiInit() {
    MealItemRegistry.instance()
        .register(CulinaryConstructRegistry.SANDWICH.get(), this::getFullness);
    MealItemRegistry.instance().register(CulinaryConstructRegistry.BOWL.get(), this::getFullness);
  }

  private int getFullness(Player player, ItemStack stack) {
    List<ItemStack> foods = new ArrayList<>(CulinaryNBT.getIngredientsList(stack));
    foods.add(CulinaryNBT.getBase(stack));
    int fullness = 0;

    for (ItemStack food : foods) {
      fullness += MealItemRegistry.instance().getFullness(player, food);
    }
    fullness = (int) Math.ceil((double) fullness / (double) CulinaryNBT.getOriginalCount(stack));
    return fullness;
  }
}
