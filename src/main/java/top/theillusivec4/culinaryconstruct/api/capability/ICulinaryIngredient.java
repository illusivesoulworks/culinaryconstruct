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

package top.theillusivec4.culinaryconstruct.api.capability;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import org.apache.commons.lang3.tuple.Pair;

public interface ICulinaryIngredient {

  default int getFoodAmount() {
    return 0;
  }

  default float getSaturation() {
    return 0.0F;
  }

  default List<Pair<EffectInstance, Float>> getEffects() {
    return Collections.emptyList();
  }

  default void onEaten(PlayerEntity player) {

  }

  default boolean isLiquid() {
    return false;
  }

  @Nullable
  default Integer getLiquidColor() {
    return null;
  }

  default boolean isValid() { return true; }
}
