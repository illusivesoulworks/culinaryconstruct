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

package top.theillusivec4.culinaryconstruct.common.blockentity;

import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import top.theillusivec4.culinaryconstruct.api.CulinaryConstructApi;
import top.theillusivec4.culinaryconstruct.api.capability.ICulinaryIngredient;
import top.theillusivec4.culinaryconstruct.common.CulinaryConstructConfig;
import top.theillusivec4.culinaryconstruct.common.item.CulinaryItemBase;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import top.theillusivec4.culinaryconstruct.common.tag.CulinaryTags;

public class CulinaryStationBlockEntity extends BlockEntity {

  public final ItemStackHandler base;
  public final ItemStackHandler ingredients;
  public final ItemStackHandler output;
  protected final LazyOptional<IItemHandler> baseOpt;
  protected final LazyOptional<IItemHandler> ingredientsOpt;
  protected final LazyOptional<IItemHandler> outputOpt;

  public CulinaryStationBlockEntity(BlockPos pos, BlockState state) {
    super(CulinaryConstructRegistry.CULINARY_STATION_TE, pos, state);
    this.base = new CulinaryStackHandler(stack -> CulinaryTags.BREAD.contains(stack.getItem()) ||
        CulinaryTags.BOWL.contains(stack.getItem()), 1);
    this.ingredients = new CulinaryStackHandler(stack -> {
      LazyOptional<ICulinaryIngredient> culinary = CulinaryConstructApi
          .getCulinaryIngredient(stack);
      return !(stack.getItem() instanceof CulinaryItemBase) &&
          (stack.getItem().isEdible() || culinary
              .map(ICulinaryIngredient::isValid).orElse(false)) && CulinaryConstructConfig
          .isValidIngredient(stack);
    }, 5);
    this.output = new CulinaryStackHandler(stack -> false, 1);
    this.baseOpt = LazyOptional.of(() -> base);
    this.ingredientsOpt = LazyOptional.of(() -> ingredients);
    this.outputOpt = LazyOptional.of(() -> output);
  }

  @Override
  public void load(@Nonnull CompoundTag compound) {
    super.load(compound);

    if (compound.contains("Holder", 10)) {
      this.base.deserializeNBT(compound.getCompound("Holder"));
    }
    if (compound.contains("Ingredients", 10)) {
      this.ingredients.deserializeNBT(compound.getCompound("Ingredients"));
    }
    if (compound.contains("Output", 10)) {
      this.output.deserializeNBT(compound.getCompound("Output"));
    }
  }

  @Nonnull
  @Override
  public CompoundTag save(@Nonnull CompoundTag compound) {
    super.save(compound);
    compound.put("Holder", this.base.serializeNBT());
    compound.put("Ingredients", this.ingredients.serializeNBT());
    compound.put("Output", this.output.serializeNBT());
    return compound;
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability,
                                           @Nullable Direction facing) {
    if (!this.remove && facing != null
        && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      if (facing == Direction.UP) {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, this.baseOpt);
      } else if (facing != Direction.DOWN) {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
            .orEmpty(capability, this.ingredientsOpt);
      }
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public void setRemoved() {
    super.setRemoved();
    baseOpt.invalidate();
    ingredientsOpt.invalidate();
    outputOpt.invalidate();
  }

  private class CulinaryStackHandler extends ItemStackHandler {

    private final Function<ItemStack, Boolean> validity;

    public CulinaryStackHandler(Function<ItemStack, Boolean> validity, int size) {
      super(size);
      this.validity = validity;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
      return validity.apply(stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
      super.onContentsChanged(slot);
      CulinaryStationBlockEntity.this.setChanged();
    }
  }
}
