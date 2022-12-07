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

package com.illusivesoulworks.culinaryconstruct.common.block;

import com.illusivesoulworks.culinaryconstruct.common.advancement.CraftFoodTrigger;
import com.illusivesoulworks.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import com.illusivesoulworks.culinaryconstruct.common.util.CulinaryCalculator;
import com.illusivesoulworks.culinaryconstruct.platform.Services;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.StringUtils;

public class CulinaryStationMenu extends AbstractContainerMenu {

  public static final int SLOT_COUNT = 7;

  private final Container container;
  private String outputItemName;

  public CulinaryStationMenu(int windowId, Inventory playerInventory, FriendlyByteBuf buf) {
    this(windowId, playerInventory, new SimpleContainer(SLOT_COUNT));
  }

  public CulinaryStationMenu(int windowId, Inventory playerInventory, @Nullable Container container) {
    super(CulinaryConstructRegistry.CULINARY_STATION_MENU.get(), windowId);
    this.container = container;
    addFoodSlots();
    addPlayerSlots(playerInventory);
  }

  private void addFoodSlots() {
    this.addSlot(new CulinarySlot(this.container, 0, 8, 44));

    for (int i = 1; i < SLOT_COUNT - 1; i++) {
      this.addSlot(new CulinarySlot(this.container, i, 44 + (i - 1) * 18, 44));
    }
    this.addSlot(new CulinaryResultSlot(this.container, SLOT_COUNT - 1, 152, 44));
  }

  private void addPlayerSlots(Inventory playerInventory) {

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
  public boolean stillValid(@Nonnull Player playerIn) {
    return this.container.stillValid(playerIn);
  }

  public void updateOutput() {
    ItemStack baseStack = this.container.getItem(0);

    if (baseStack.isEmpty()) {
      resetOutput();
      return;
    }
    NonNullList<ItemStack> ingredientsList = NonNullList.create();

    for (int i = 1; i < this.container.getContainerSize() - 1; i++) {
      ItemStack stack = this.container.getItem(i);

      if (!stack.isEmpty()) {
        ItemStack copy = stack.copy();
        copy.setCount(1);
        ingredientsList.add(copy);
      }
    }

    if (ingredientsList.isEmpty()) {
      resetOutput();
      return;
    }
    ItemStack baseCopy = baseStack.copy();
    baseCopy.setCount(1);
    CulinaryCalculator calculator = new CulinaryCalculator(baseCopy, ingredientsList);
    ItemStack result = calculator.getResult();

    if (result.isEmpty()) {
      resetOutput();
      return;
    }

    if (StringUtils.isBlank(this.outputItemName)) {
      result.resetHoverName();
    } else if (!this.outputItemName.equals(result.getHoverName().getString())) {
      result.setHoverName(Component.literal(this.outputItemName));
    }
    setOutput(result);
  }

  private void setOutput(ItemStack stack) {
    this.container.setItem(6, stack);
    this.broadcastChanges();
  }

  private void resetOutput() {
    ItemStack stack = this.container.getItem(6);

    if (!stack.isEmpty()) {
      this.setOutput(ItemStack.EMPTY);
    }
  }

  @Nonnull
  @Override
  public ItemStack quickMoveStack(@Nonnull Player playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);

    if (slot.hasItem()) {
      ItemStack itemstack1 = slot.getItem();
      itemstack = itemstack1.copy();

      if (index == 6) {

        if (!this.moveItemStackTo(itemstack1, 7, 43, true)) {
          return ItemStack.EMPTY;
        }
        slot.onQuickCraft(itemstack1, itemstack);
      } else if (index >= 7 && index < 43) {

        if (!this.moveItemStackTo(itemstack1, 0, 1, false) && !this
            .moveItemStackTo(itemstack1, 1, 6, false)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.moveItemStackTo(itemstack1, 7, 43, false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.isEmpty()) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }

      if (itemstack1.getCount() == itemstack.getCount()) {
        return ItemStack.EMPTY;
      }
      slot.onTake(playerIn, itemstack1);
    }
    return itemstack;
  }

  public void updateItemName(String newName) {
    this.outputItemName = newName;
    this.updateOutput();
  }

  public class CulinarySlot extends Slot {

    public CulinarySlot(Container container, int index, int x, int y) {
      super(container, index, x, y);
    }

    @Override
    public void setChanged() {
      CulinaryStationMenu.this.updateOutput();
    }

    public boolean mayPlace(@Nonnull ItemStack stack) {
      return this.container.canPlaceItem(index, stack);
    }
  }

  private class CulinaryResultSlot extends CulinarySlot {

    public CulinaryResultSlot(Container container, int index, int x, int y) {
      super(container, index, x, y);
    }

    @Override
    public void onTake(@Nonnull Player player, @Nonnull ItemStack stack) {

      if (player instanceof ServerPlayer serverPlayer) {
        CraftFoodTrigger.INSTANCE.trigger(serverPlayer);
      }

      if (CulinaryStationMenu.this.container instanceof CulinaryStationBlockEntity blockEntity) {

        for (int i = 0; i < blockEntity.getContainerSize() - 1; i++) {
          ItemStack itemStack = blockEntity.getItem(i);

          if (!itemStack.isEmpty()) {
            boolean isPotion = itemStack.getItem() instanceof PotionItem;
            boolean isSoup = itemStack.getItem() instanceof BowlFoodItem;

            ItemStack container = Services.PLATFORM.getContainerItem(itemStack);
            itemStack.shrink(1);

            if (container.isEmpty()) {

              if (isPotion) {
                container = new ItemStack(Items.GLASS_BOTTLE);
              } else if (isSoup) {
                container = new ItemStack(Items.BOWL);
              }
            }

            if (!container.isEmpty()) {
              Services.PLATFORM.giveItemToPlayer(container, player);
            }
          }
        }
      }
      CulinaryStationMenu.this.updateOutput();
    }
  }
}
