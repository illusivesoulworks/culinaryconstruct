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

package com.illusivesoulworks.culinaryconstruct.platform;

import com.illusivesoulworks.culinaryconstruct.CulinaryConstructConstants;
import com.illusivesoulworks.culinaryconstruct.api.CulinaryConstructCapabilities;
import com.illusivesoulworks.culinaryconstruct.api.ICulinaryIngredient;
import com.illusivesoulworks.culinaryconstruct.client.CulinaryConstructForgeClientMod;
import com.illusivesoulworks.culinaryconstruct.common.network.CPacketRename;
import com.illusivesoulworks.culinaryconstruct.common.network.CulinaryConstructForgeNetwork;
import com.illusivesoulworks.culinaryconstruct.platform.services.IPlatform;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.PacketDistributor;

public class ForgePlatform implements IPlatform {

  @Override
  public Optional<ICulinaryIngredient> getCulinaryIngredient(ItemStack stack) {
    return stack.getCapability(CulinaryConstructCapabilities.CULINARY_INGREDIENT).resolve();
  }

  @Override
  public void giveItemToPlayer(ItemStack stack, Player player) {
    ItemHandlerHelper.giveItemToPlayer(player, stack);
  }

  @Override
  public boolean isFluidValid(ItemStack stack) {
    Fluid fluid = FluidUtil.getFluidHandler(stack)
        .map(fluidHandler -> fluidHandler.getFluidInTank(0).getFluid()).orElse(Fluids.EMPTY);

    if (fluid == Fluids.EMPTY) {
      return false;
    }
    FluidType fluidType = fluid.getFluidType();
    return !fluidType.isLighterThanAir() && fluidType.getTemperature() <= 400 && !fluidType.isAir();
  }

  @Override
  public Integer getFluidColor(ItemStack stack) {
    Fluid fluid = FluidUtil.getFluidHandler(stack)
        .map(fluidHandler -> fluidHandler.getFluidInTank(0).getFluid()).orElse(Fluids.EMPTY);

    if (fluid == Fluids.WATER || fluid == Fluids.EMPTY) {
      return null;
    }

    if (FMLEnvironment.dist.isClient()) {
      return CulinaryConstructForgeClientMod.getFluidColor(fluid);
    } else {
      CulinaryConstructConstants.LOG.error("Attempted to retrieve fluid color in a non-client!");
      return null;
    }
  }

  @Override
  public void cureStatusEffects(ItemStack stack, Player player) {
    player.curePotionEffects(stack);
  }

  @Override
  public ItemStack getContainerItem(ItemStack stack) {
    return stack.getCraftingRemainingItem();
  }

  @Override
  public void sendRenamePacket(CPacketRename msg) {
    CulinaryConstructForgeNetwork.get().send(msg, PacketDistributor.SERVER.noArg());
  }
}
