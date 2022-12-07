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

package com.illusivesoulworks.culinaryconstruct.common.capability;

import com.illusivesoulworks.culinaryconstruct.api.CulinaryConstructCapabilities;
import com.illusivesoulworks.culinaryconstruct.api.ICulinaryIngredient;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class CulinaryIngredientCapability {

  public static void setup() {
    MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class,
        CulinaryIngredientCapability::attachCapabilities);
  }

  public static ICapabilityProvider createCulinaryIngredient(final ICulinaryIngredient ingredient) {
    return new Provider(ingredient);
  }

  private static void attachCapabilities(AttachCapabilitiesEvent<ItemStack> evt) {
    ItemStack stack = evt.getObject();
    Item item = stack.getItem();
    for (Pair<Predicate<Item>, Function<ItemStack, ICulinaryIngredient>> entry : CulinaryIngredients.getDefaults()) {

      if (entry.getFirst().test(item)) {
        evt.addCapability(ICulinaryIngredient.INGREDIENT_ID,
            createCulinaryIngredient(entry.getSecond().apply(stack)));
      }
    }
  }

  public static class Provider implements ICapabilityProvider {

    final LazyOptional<ICulinaryIngredient> capability;

    Provider(ICulinaryIngredient ingredient) {
      this.capability = LazyOptional.of(() -> ingredient);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return CulinaryConstructCapabilities.CULINARY_INGREDIENT.orEmpty(cap, capability);
    }
  }
}
