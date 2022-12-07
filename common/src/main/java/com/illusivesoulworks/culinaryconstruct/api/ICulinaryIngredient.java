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

package com.illusivesoulworks.culinaryconstruct.api;

import com.illusivesoulworks.culinaryconstruct.CulinaryConstructConstants;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public interface ICulinaryIngredient {

  ResourceLocation INGREDIENT_ID = new ResourceLocation(CulinaryConstructConstants.MOD_ID,
      "ingredient");

  default int getFoodAmount() {
    return 0;
  }

  default float getSaturation() {
    return 0.0F;
  }

  default List<Pair<MobEffectInstance, Float>> getEffects() {
    return Collections.emptyList();
  }

  default void onEaten(Player player) {

  }

  default boolean isLiquid() {
    return false;
  }

  @Nullable
  default Integer getLiquidColor() {
    return null;
  }

  default boolean isValid() {
    return true;
  }
}
