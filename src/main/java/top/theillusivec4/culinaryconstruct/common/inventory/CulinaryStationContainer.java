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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import top.theillusivec4.culinaryconstruct.common.tileentity.CulinaryStationTileEntity;

public class CulinaryStationContainer extends Container {

  public static final String REGISTRY_NAME = "culinary_station_container";

  private final IWorldPosCallable worldPosCallable;

  private List<LazyOptional<IItemHandler>> handlers = new ArrayList<>(
      Arrays.asList(LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty()));

  public CulinaryStationContainer(int windowId, PlayerInventory playerInventory,
      PacketBuffer unused) {
    this(windowId, playerInventory, IWorldPosCallable.DUMMY, null);
  }

  public CulinaryStationContainer(int windowId, PlayerInventory playerInventory,
      IWorldPosCallable worldPosCallable, @Nullable TileEntity tileEntity) {
    super(CulinaryConstructRegistry.CULINARY_STATION_CONTAINER, windowId);
    this.worldPosCallable = worldPosCallable;
    this.init(tileEntity);
    addPlayerSlots(playerInventory);
    addFoodSlots();
  }

  private void init(@Nullable TileEntity tileEntity) {
    if (tileEntity instanceof CulinaryStationTileEntity) {
      handlers = ((CulinaryStationTileEntity) tileEntity).handlers;
    }
  }

  private void addFoodSlots() {
    IItemHandler inputHandler = handlers.get(0).map(itemHandler -> itemHandler).orElse(new ItemStackHandler());
    IItemHandler ingredientsHandler = handlers.get(1).map(itemHandler -> itemHandler).orElse(new ItemStackHandler(5));
    IItemHandler outputHandler = handlers.get(2).map(itemHandler -> itemHandler).orElse(new ItemStackHandler());
    this.addSlot(new SlotItemHandler(inputHandler, 0, 17, 56));

    this.addSlot(new SlotItemHandler(ingredientsHandler, 0, 71, 38));
    this.addSlot(new SlotItemHandler(ingredientsHandler, 1, 89, 38));

    for (int i = 2; i < ingredientsHandler.getSlots(); i++) {
      this.addSlot(new SlotItemHandler(ingredientsHandler, i, 62 + (i - 2) * 18, 56));
    }
    this.addSlot(new SlotItemHandler(outputHandler, 0, 144, 56));
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
