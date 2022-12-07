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

package com.illusivesoulworks.culinaryconstruct.platform.services;

import com.illusivesoulworks.culinaryconstruct.api.ICulinaryIngredient;
import com.illusivesoulworks.culinaryconstruct.common.network.CPacketRename;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IPlatform {

  Optional<ICulinaryIngredient> getCulinaryIngredient(ItemStack stack);

  void giveItemToPlayer(ItemStack stack, Player player);

  boolean isFluidValid(ItemStack stack);

  Integer getFluidColor(ItemStack stack);

  void cureStatusEffects(ItemStack stack, Player player);

  ItemStack getContainerItem(ItemStack stack);

  void sendRenamePacket(CPacketRename msg);
}
