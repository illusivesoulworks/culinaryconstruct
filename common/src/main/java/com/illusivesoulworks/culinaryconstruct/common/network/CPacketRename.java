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

package com.illusivesoulworks.culinaryconstruct.common.network;

import com.illusivesoulworks.culinaryconstruct.common.block.CulinaryStationMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record CPacketRename(String name) {

  public static void encode(CPacketRename msg, FriendlyByteBuf buf) {
    buf.writeUtf(msg.name);
  }

  public static CPacketRename decode(FriendlyByteBuf buf) {
    return new CPacketRename(buf.readUtf(35));
  }

  public static void handle(CPacketRename msg, ServerPlayer serverPlayer) {
    String name = msg.name;

    if (serverPlayer != null &&
        serverPlayer.containerMenu instanceof CulinaryStationMenu culinaryStationMenu) {
      culinaryStationMenu.updateItemName(name);
    }
  }
}
