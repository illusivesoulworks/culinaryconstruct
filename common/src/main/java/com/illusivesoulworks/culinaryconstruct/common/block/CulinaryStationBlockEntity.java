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

import com.illusivesoulworks.culinaryconstruct.CulinaryConstructConstants;
import com.illusivesoulworks.culinaryconstruct.api.CulinaryConstructApi;
import com.illusivesoulworks.culinaryconstruct.api.ICulinaryIngredient;
import com.illusivesoulworks.culinaryconstruct.common.config.CulinaryConstructConfig;
import com.illusivesoulworks.culinaryconstruct.common.item.CulinaryItemBase;
import com.illusivesoulworks.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import com.illusivesoulworks.culinaryconstruct.common.util.CulinaryTags;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CulinaryStationBlockEntity extends BaseContainerBlockEntity
    implements WorldlyContainer, StackedContentsCompatible {

  private static final int[] SLOTS_FOR_UP = new int[] {0};
  private static final int[] SLOTS_FOR_DOWN = new int[] {6};
  private static final int[] SLOTS_FOR_SIDES = new int[] {1, 2, 3, 4, 5};

  protected NonNullList<ItemStack> items;

  public CulinaryStationBlockEntity(BlockPos pos, BlockState state) {
    super(CulinaryConstructRegistry.CULINARY_STATION_BLOCK_ENTITY.get(), pos, state);
    this.items = NonNullList.withSize(7, ItemStack.EMPTY);
  }

  @Override
  public void load(@Nonnull CompoundTag tag) {
    super.load(tag);
    ContainerHelper.loadAllItems(tag, this.items);
  }

  @Override
  protected void saveAdditional(@Nonnull CompoundTag tag) {
    super.saveAdditional(tag);
    ContainerHelper.saveAllItems(tag, this.items);
  }

  @Nonnull
  @Override
  public int[] getSlotsForFace(@Nonnull Direction side) {

    if (side == Direction.DOWN) {
      return SLOTS_FOR_DOWN;
    } else {
      return side == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
    }
  }

  @Override
  public boolean canPlaceItemThroughFace(int index, @Nonnull ItemStack stack,
                                         @Nullable Direction side) {
    return this.canPlaceItem(index, stack);
  }

  @Override
  public boolean canTakeItemThroughFace(int index, @Nonnull ItemStack stack,
                                        @Nonnull Direction side) {
    return false;
  }

  @Override
  public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {

    if (index == 6) {
      return false;
    } else if (index == 0) {
      return CulinaryTags.isBowl(stack) || CulinaryTags.isBread(stack);
    } else {
      Optional<ICulinaryIngredient> culinary = CulinaryConstructApi.getCulinaryIngredient(stack);
      return !(stack.getItem() instanceof CulinaryItemBase) && (stack.getItem().isEdible() ||
          culinary.map(ICulinaryIngredient::isValid).orElse(false)) &&
          CulinaryConstructConfig.isValidIngredient(stack);
    }
  }

  @Nonnull
  @Override
  protected Component getDefaultName() {
    return Component.translatable(
        "container." + CulinaryConstructConstants.MOD_ID + ".culinary_station");
  }

  @Nonnull
  @Override
  protected AbstractContainerMenu createMenu(int index, @Nonnull Inventory inv) {
    return new CulinaryStationMenu(index, inv, this);
  }

  @Override
  public int getContainerSize() {
    return this.items.size();
  }

  @Override
  public boolean isEmpty() {

    for (ItemStack itemstack : this.items) {

      if (!itemstack.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Nonnull
  @Override
  public ItemStack getItem(int index) {
    return this.items.get(index);
  }

  @Nonnull
  @Override
  public ItemStack removeItem(int index, int count) {
    return ContainerHelper.removeItem(this.items, index, count);
  }

  @Nonnull
  @Override
  public ItemStack removeItemNoUpdate(int index) {
    return ContainerHelper.takeItem(this.items, index);
  }

  @Override
  public void setItem(int index, @Nonnull ItemStack stack) {
    ItemStack itemstack = this.items.get(index);
    boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemstack);
    this.items.set(index, stack);

    if (stack.getCount() > this.getMaxStackSize()) {
      stack.setCount(this.getMaxStackSize());
    }

    if (!flag) {
      this.setChanged();
    }
  }

  @Override
  public boolean stillValid(@Nonnull Player player) {

    if (this.level != null && this.level.getBlockEntity(this.worldPosition) != this) {
      return false;
    } else {
      return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D,
          (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <=
          64.0D;
    }
  }

  @Override
  public void clearContent() {
    this.items.clear();
  }

  @Override
  public void fillStackedContents(@Nonnull StackedContents stackedContents) {

    for (ItemStack itemstack : this.items) {
      stackedContents.accountStack(itemstack);
    }
  }
}
