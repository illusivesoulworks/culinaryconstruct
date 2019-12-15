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
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import top.theillusivec4.culinaryconstruct.common.tag.CulinaryTags;
import top.theillusivec4.culinaryconstruct.common.tileentity.CulinaryStationTileEntity;

public class CulinaryStationContainer extends Container {

  private final IWorldPosCallable worldPosCallable;

  private IItemHandler holder = new ItemStackHandler();
  private IItemHandler ingredients = new ItemStackHandler(5);
  private IItemHandler output = new ItemStackHandler();

  public CulinaryStationContainer(int windowId, PlayerInventory playerInventory,
      PacketBuffer unused) {
    this(windowId, playerInventory, IWorldPosCallable.DUMMY, null);
  }

  public CulinaryStationContainer(int windowId, PlayerInventory playerInventory,
      IWorldPosCallable worldPosCallable, @Nullable TileEntity tileEntity) {
    super(CulinaryConstructRegistry.CULINARY_STATION_CONTAINER, windowId);
    this.worldPosCallable = worldPosCallable;
    this.init(tileEntity);
    addFoodSlots();
    addPlayerSlots(playerInventory);
  }

  private void init(@Nullable TileEntity tileEntity) {
    if (tileEntity instanceof CulinaryStationTileEntity) {
      CulinaryStationTileEntity te = (CulinaryStationTileEntity) tileEntity;
      this.holder = te.holder;
      this.ingredients = te.ingredients;
      this.output = te.output;
    }
  }

  private void addFoodSlots() {
    this.addSlot(new HolderSlot(this.holder, 0, 17, 56));

    this.addSlot(new IngredientSlot(this.ingredients, 0, 71, 38));
    this.addSlot(new IngredientSlot(this.ingredients, 1, 89, 38));

    for (int i = 2; i < this.ingredients.getSlots(); i++) {
      this.addSlot(new IngredientSlot(this.ingredients, i, 62 + (i - 2) * 18, 56));
    }
    this.addSlot(new SlotItemHandler(this.output, 0, 144, 56));
  }

  private void addPlayerSlots(PlayerInventory playerInventory) {

    // Main inventory
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 79 + row * 18));
      }
    }

    // Hotbar
    for (int hotbar = 0; hotbar < 9; hotbar++) {
      this.addSlot(new Slot(playerInventory, hotbar, 8 + hotbar * 18, 137));
    }
  }

  @Override
  public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
    return isWithinUsableDistance(this.worldPosCallable, playerIn,
        CulinaryConstructRegistry.CULINARY_STATION);
  }

  @Nonnull
  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);

    if (slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();

      if (index == 6) {

        if (!this.mergeItemStack(itemstack1, 7, 43, true)) {
          return ItemStack.EMPTY;
        }
        slot.onSlotChange(itemstack1, itemstack);
      } else if (index >= 7 && index < 43) {

        if (!this.mergeItemStack(itemstack1, 0, 1, false) && !this
            .mergeItemStack(itemstack1, 1, 6, false)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.mergeItemStack(itemstack1, 7, 43, false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }

      if (itemstack1.getCount() == itemstack.getCount()) {
        return ItemStack.EMPTY;
      }
      slot.onTake(playerIn, itemstack1);
    }
    return itemstack;
  }

  private static class HolderSlot extends SlotItemHandler {

    public HolderSlot(IItemHandler handler, int index, int xPos, int yPos) {
      super(handler, index, xPos, yPos);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      return stack.getItem().isIn(CulinaryTags.BREAD) || stack.getItem().isIn(CulinaryTags.BOWL);
    }
  }

  private static class IngredientSlot extends SlotItemHandler {

    public IngredientSlot(IItemHandler handler, int index, int xPos, int yPos) {
      super(handler, index, xPos, yPos);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      return stack.getItem().isFood();
    }
  }
}
