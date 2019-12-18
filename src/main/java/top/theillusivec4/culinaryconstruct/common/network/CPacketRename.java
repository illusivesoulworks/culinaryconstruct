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

package top.theillusivec4.culinaryconstruct.common.network;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import top.theillusivec4.culinaryconstruct.common.inventory.CulinaryStationContainer;

public class CPacketRename {

  private String name;

  public CPacketRename(String name) {
    this.name = name;
  }

  public static void encode(CPacketRename msg, PacketBuffer buf) {
    buf.writeString(msg.name);
  }

  public static CPacketRename decode(PacketBuffer buf) {
    return new CPacketRename(buf.readString());
  }

  public static void handle(CPacketRename msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity playerEntity = ctx.get().getSender();
      String name = msg.name;

      if (playerEntity != null && playerEntity.openContainer instanceof CulinaryStationContainer) {
        ((CulinaryStationContainer) playerEntity.openContainer).updateItemName(name);
      }
    });
    ctx.get().setPacketHandled(true);
  }

}
