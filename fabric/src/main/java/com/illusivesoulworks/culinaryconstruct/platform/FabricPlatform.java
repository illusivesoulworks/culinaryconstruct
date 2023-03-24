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

import com.illusivesoulworks.culinaryconstruct.api.CulinaryIngredientLookup;
import com.illusivesoulworks.culinaryconstruct.api.ICulinaryIngredient;
import com.illusivesoulworks.culinaryconstruct.client.CulinaryConstructFabricClientMod;
import com.illusivesoulworks.culinaryconstruct.common.network.CPacketRename;
import com.illusivesoulworks.culinaryconstruct.common.network.CulinaryConstructPackets;
import com.illusivesoulworks.culinaryconstruct.platform.services.IPlatform;
import java.util.Iterator;
import java.util.Optional;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FabricPlatform implements IPlatform {

  @Override
  public Optional<ICulinaryIngredient> getCulinaryIngredient(ItemStack stack) {
    return Optional.ofNullable(CulinaryIngredientLookup.INSTANCE.find(stack, null));
  }

  @Override
  public void giveItemToPlayer(ItemStack stack, Player player) {
    player.addItem(stack);
  }

  @SuppressWarnings("all")
  @Override
  public boolean isFluidValid(ItemStack stack) {
    Storage<FluidVariant> storage =
        FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));

    if (storage != null) {
      Iterator<StorageView<FluidVariant>> iter = storage.iterator();

      if (iter.hasNext()) {
        StorageView<FluidVariant> next = iter.next();
        return next.getAmount() > 0 && !next.isResourceBlank();
      }
    }
    return false;
  }

  @Override
  public Integer getFluidColor(ItemStack stack) {
    return CulinaryConstructFabricClientMod.getFluidColor(stack);
  }

  @Override
  public void cureStatusEffects(ItemStack stack, Player player) {
    player.removeAllEffects();
  }

  @Override
  public ItemStack getContainerItem(ItemStack stack) {
    Item item = stack.getItem();

    if (item.getCraftingRemainingItem() != null) {
      return item.getCraftingRemainingItem().getDefaultInstance();
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void sendRenamePacket(CPacketRename msg) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    CPacketRename.encode(msg, buf);
    ClientPlayNetworking.send(CulinaryConstructPackets.RENAME, buf);
  }
}
