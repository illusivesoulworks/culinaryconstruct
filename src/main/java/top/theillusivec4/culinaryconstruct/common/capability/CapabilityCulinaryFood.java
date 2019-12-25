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

package top.theillusivec4.culinaryconstruct.common.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.culinaryconstruct.api.capability.CulinaryConstructCapability;
import top.theillusivec4.culinaryconstruct.api.capability.ICulinaryIngredient;

public class CapabilityCulinaryFood {

  public static void register() {
    CapabilityManager.INSTANCE
        .register(ICulinaryIngredient.class, new IStorage<ICulinaryIngredient>() {
          @Nullable
          @Override
          public INBT writeNBT(Capability<ICulinaryIngredient> capability,
              ICulinaryIngredient instance, Direction side) {
            return null;
          }

          @Override
          public void readNBT(Capability<ICulinaryIngredient> capability,
              ICulinaryIngredient instance, Direction side, INBT nbt) {

          }
        }, CulinaryFoodWrapper::new);
    MinecraftForge.EVENT_BUS.register(new CapabilityEventHandler());
  }

  public static ICapabilityProvider createCulinaryIngredient(final ICulinaryIngredient ingredient) {
    return new Provider(ingredient);
  }

  private static class CulinaryFoodWrapper implements ICulinaryIngredient {

  }

  public static class Provider implements ICapabilityProvider {

    final LazyOptional<ICulinaryIngredient> capability;

    Provider(ICulinaryIngredient ingredient) {
      this.capability = LazyOptional.of(() -> ingredient);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return CulinaryConstructCapability.CULINARY_INGREDIENT.orEmpty(cap, capability);
    }
  }
}
