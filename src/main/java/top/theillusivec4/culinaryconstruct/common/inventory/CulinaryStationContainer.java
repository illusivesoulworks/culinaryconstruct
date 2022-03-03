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
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.commons.lang3.StringUtils;
import top.theillusivec4.culinaryconstruct.api.CulinaryConstructApi;
import top.theillusivec4.culinaryconstruct.api.capability.ICulinaryIngredient;
import top.theillusivec4.culinaryconstruct.common.CulinaryConstructConfig;
import top.theillusivec4.culinaryconstruct.common.advancement.CraftFoodTrigger;
import top.theillusivec4.culinaryconstruct.common.blockentity.CulinaryStationBlockEntity;
import top.theillusivec4.culinaryconstruct.common.item.CulinaryItemBase;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import top.theillusivec4.culinaryconstruct.common.tag.CulinaryTags;

public class CulinaryStationContainer extends AbstractContainerMenu {

  private final ContainerLevelAccess worldPosCallable;

  private ItemStackHandler base = new ItemStackHandler();
  private ItemStackHandler ingredients = new ItemStackHandler(5);
  private ItemStackHandler output = new ItemStackHandler();
  private String outputItemName;

  public CulinaryStationContainer(int windowId, Inventory playerInventory,
                                  FriendlyByteBuf unused) {
    this(windowId, playerInventory, ContainerLevelAccess.NULL, null);
  }

  public CulinaryStationContainer(int windowId, Inventory playerInventory,
                                  ContainerLevelAccess worldPosCallable,
                                  @Nullable BlockEntity tileEntity) {
    super(CulinaryConstructRegistry.CULINARY_STATION_CONTAINER, windowId);
    this.worldPosCallable = worldPosCallable;
    this.init(tileEntity);
    addFoodSlots();
    addPlayerSlots(playerInventory);
  }

  private void init(@Nullable BlockEntity tileEntity) {

    if (tileEntity instanceof CulinaryStationBlockEntity te) {
      this.base = te.base;
      this.ingredients = te.ingredients;
      this.output = te.output;
    }
  }

  private void addFoodSlots() {
    this.addSlot(new BaseSlot(this.base, 0, 8, 44));

    for (int i = 0; i < this.ingredients.getSlots(); i++) {
      this.addSlot(new IngredientSlot(this.ingredients, i, 44 + i * 18, 44));
    }
    this.addSlot(new OutputSlot(this.output, 0, 152, 44));
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
    return stillValid(this.worldPosCallable, playerIn,
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
      result.resetHoverName();
    } else if (!this.outputItemName.equals(result.getHoverName().getString())) {
      result.setHoverName(new TextComponent(this.outputItemName));
    }
    setOutput(result);
  }

  private void setOutput(ItemStack stack) {
    this.output.setStackInSlot(0, stack);
    this.broadcastChanges();
  }

  private void resetOutput() {
    ItemStack outputStack = this.output.getStackInSlot(0);

    if (!outputStack.isEmpty()) {
      this.output.setStackInSlot(0, ItemStack.EMPTY);
      this.broadcastChanges();
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

  private class BaseSlot extends SlotItemHandler {

    public BaseSlot(IItemHandler handler, int index, int xPos, int yPos) {
      super(handler, index, xPos, yPos);
    }

    @Override
    public void setChanged() {
      CulinaryStationContainer.this.updateOutput();
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
      return CulinaryTags.isBread(stack) || CulinaryTags.isBowl(stack);
    }
  }

  private class IngredientSlot extends SlotItemHandler {

    public IngredientSlot(IItemHandler handler, int index, int xPos, int yPos) {
      super(handler, index, xPos, yPos);
    }

    @Override
    public void setChanged() {
      CulinaryStationContainer.this.updateOutput();
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
      LazyOptional<ICulinaryIngredient> culinary = CulinaryConstructApi
          .getCulinaryIngredient(stack);
      return !(stack.getItem() instanceof CulinaryItemBase) &&
          (stack.getItem().isEdible() || culinary
              .map(ICulinaryIngredient::isValid).orElse(false)) && CulinaryConstructConfig
          .isValidIngredient(stack);
    }
  }

  private class OutputSlot extends SlotItemHandler {

    public OutputSlot(IItemHandler handler, int index, int xPos, int yPos) {
      super(handler, index, xPos, yPos);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
      return false;
    }

    @Override
    public void setChanged() {
      CulinaryStationContainer.this.updateOutput();
      CulinaryStationContainer.this.broadcastChanges();
    }

    @Override
    public void onTake(Player playerEntity, @Nonnull ItemStack stack) {

      if (!playerEntity.level.isClientSide) {
        CraftFoodTrigger.INSTANCE.trigger((ServerPlayer) playerEntity);
      }
      ItemStackHandler ingredients = CulinaryStationContainer.this.ingredients;

      if (ingredients != null) {

        for (int i = 0; i < ingredients.getSlots(); i++) {
          ItemStack slot = ingredients.getStackInSlot(i);

          if (!slot.isEmpty()) {
            boolean isPotion = slot.getItem() instanceof PotionItem;
            boolean isSoup = slot.getItem() instanceof BowlFoodItem;

            ItemStack container = slot.getItem().getContainerItem(slot);
            slot.shrink(1);

            if (container.isEmpty()) {

              if (isPotion) {
                container = new ItemStack(Items.GLASS_BOTTLE);
              } else if (isSoup) {
                container = new ItemStack(Items.BOWL);
              }
            }

            if (!container.isEmpty()) {
              ItemHandlerHelper.giveItemToPlayer(playerEntity, container);
            }
          }
        }
      }
      IItemHandler base = CulinaryStationContainer.this.base;

      if (base != null) {
        base.getStackInSlot(0).shrink(1);
      }
      CulinaryStationContainer.this.updateOutput();
    }
  }
}
