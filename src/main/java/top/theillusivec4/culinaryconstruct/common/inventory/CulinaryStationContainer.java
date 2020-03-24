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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.commons.lang3.StringUtils;
import top.theillusivec4.culinaryconstruct.api.CulinaryConstructAPI;
import top.theillusivec4.culinaryconstruct.api.capability.ICulinaryIngredient;
import top.theillusivec4.culinaryconstruct.common.CulinaryConstructConfig;
import top.theillusivec4.culinaryconstruct.common.advancement.CulinaryTriggers;
import top.theillusivec4.culinaryconstruct.common.item.CulinaryItemBase;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import top.theillusivec4.culinaryconstruct.common.tag.CulinaryTags;
import top.theillusivec4.culinaryconstruct.common.tileentity.CulinaryStationTileEntity;

public class CulinaryStationContainer extends Container {

  private final IWorldPosCallable worldPosCallable;

  private ItemStackHandler base = new ItemStackHandler();
  private ItemStackHandler ingredients = new ItemStackHandler(5);
  private ItemStackHandler output = new ItemStackHandler();
  private String outputItemName;

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
      this.base = te.base;
      this.ingredients = te.ingredients;
      this.output = te.output;
    }
  }

  private void addFoodSlots() {
    this.addSlot(new BaseSlot(this.base, 0, 17, 56));

    this.addSlot(new IngredientSlot(this.ingredients, 0, 71, 38));
    this.addSlot(new IngredientSlot(this.ingredients, 1, 89, 38));

    for (int i = 2; i < this.ingredients.getSlots(); i++) {
      this.addSlot(new IngredientSlot(this.ingredients, i, 62 + (i - 2) * 18, 56));
    }
    this.addSlot(new OutputSlot(this.output, 0, 144, 56));
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

  public void updateOutput() {
    ItemStack baseStack = this.base.getStackInSlot(0);

    if (baseStack.isEmpty()) {
      resetOutput();
      return;
    }
    NonNullList<ItemStack> ingredientsList = NonNullList.create();

    for (int i = 0; i < this.ingredients.getSlots(); i++) {
      ItemStack stack = this.ingredients.getStackInSlot(i);

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
      result.clearCustomName();
    } else if (!this.outputItemName.equals(result.getDisplayName().getString())) {
      result.setDisplayName(new StringTextComponent(this.outputItemName));
    }
    setOutput(result);
  }

  private void setOutput(ItemStack stack) {
    this.output.setStackInSlot(0, stack);
    this.detectAndSendChanges();
  }

  private void resetOutput() {
    ItemStack outputStack = this.output.getStackInSlot(0);

    if (!outputStack.isEmpty()) {
      this.output.setStackInSlot(0, ItemStack.EMPTY);
      this.detectAndSendChanges();
    }
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

  public void updateItemName(String newName) {
    this.outputItemName = newName;
    this.updateOutput();
  }

  private class BaseSlot extends SlotItemHandler {

    public BaseSlot(IItemHandler handler, int index, int xPos, int yPos) {
      super(handler, index, xPos, yPos);
    }

    @Override
    public void onSlotChanged() {
      CulinaryStationContainer.this.updateOutput();
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      return stack.getItem().isIn(CulinaryTags.BREAD) || stack.getItem().isIn(CulinaryTags.BOWL);
    }
  }

  private class IngredientSlot extends SlotItemHandler {

    public IngredientSlot(IItemHandler handler, int index, int xPos, int yPos) {
      super(handler, index, xPos, yPos);
    }

    @Override
    public void onSlotChanged() {
      CulinaryStationContainer.this.updateOutput();
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      LazyOptional<ICulinaryIngredient> culinary = CulinaryConstructAPI
          .getCulinaryIngredient(stack);
      return !(stack.getItem() instanceof CulinaryItemBase) && (stack.getItem().isFood() || culinary
          .map(ICulinaryIngredient::isValid).orElse(false)) && !CulinaryConstructConfig
          .isBlacklistedIngredient(stack);
    }
  }

  private class OutputSlot extends SlotItemHandler {

    public OutputSlot(IItemHandler handler, int index, int xPos, int yPos) {
      super(handler, index, xPos, yPos);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      return false;
    }

    @Override
    public void onSlotChanged() {
      CulinaryStationContainer.this.updateOutput();
      CulinaryStationContainer.this.detectAndSendChanges();
    }

    @Nonnull
    @Override
    public ItemStack onTake(PlayerEntity playerEntity, @Nonnull ItemStack stack) {

      if (!playerEntity.world.isRemote) {
        CulinaryTriggers.CRAFT_FOOD.trigger((ServerPlayerEntity) playerEntity);
      }
      IItemHandler ingredients = CulinaryStationContainer.this.ingredients;

      if (ingredients != null) {

        for (int i = 0; i < ingredients.getSlots(); i++) {
          ItemStack slot = ingredients.getStackInSlot(i);
          boolean isPotion = slot.getItem() instanceof PotionItem;

          ItemStack container = slot.getItem().getContainerItem(slot);
          slot.shrink(1);

          if (!container.isEmpty()) {
            ingredients.insertItem(i, container, false);
          } else if (isPotion) {
            ingredients.insertItem(i, new ItemStack(Items.GLASS_BOTTLE), false);
          }
        }
      }
      IItemHandler base = CulinaryStationContainer.this.base;

      if (base != null) {
        base.getStackInSlot(0).shrink(1);
      }
      return stack;
    }
  }
}
