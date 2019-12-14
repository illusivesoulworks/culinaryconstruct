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

package top.theillusivec4.culinaryconstruct.common.inventory;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;

public class CulinaryStationContainer extends Container {

  public static final String REGISTRY_NAME = "culinary_station_container";

  private final IWorldPosCallable worldPosCallable;

  public CulinaryStationContainer(int windowId, PlayerInventory playerInventory,
      PacketBuffer extraData) {
    this(windowId, playerInventory, IWorldPosCallable.DUMMY);
  }

  public CulinaryStationContainer(int windowId, PlayerInventory playerInventory,
      IWorldPosCallable worldPosCallable) {
    super(CulinaryConstructRegistry.CULINARY_STATION_CONTAINER, windowId);
    addPlayerSlots(playerInventory);
    this.worldPosCallable = worldPosCallable;
  }

  private void addPlayerSlots(PlayerInventory playerInventory) {

    //Main inventory
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 79 + row * 18));
      }
    }

    //Hotbar
    for (int hotbar = 0; hotbar < 9; hotbar++) {
      this.addSlot(new Slot(playerInventory, hotbar, 8 + hotbar * 18, 137));
    }
  }

  @Override
  public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
    return isWithinUsableDistance(this.worldPosCallable, playerIn,
        CulinaryConstructRegistry.CULINARY_STATION);
  }
}
