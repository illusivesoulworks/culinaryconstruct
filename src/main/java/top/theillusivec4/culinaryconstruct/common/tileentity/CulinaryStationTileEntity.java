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

package top.theillusivec4.culinaryconstruct.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;

public class CulinaryStationTileEntity extends TileEntity {

  protected final LazyOptional<IItemHandler> holderOpt;
  protected final LazyOptional<IItemHandler> ingredientsOpt;
  protected final LazyOptional<IItemHandler> outputOpt;

  public final ItemStackHandler holder;
  public final ItemStackHandler ingredients;
  public final ItemStackHandler output;

  public CulinaryStationTileEntity() {
    super(CulinaryConstructRegistry.CULINARY_STATION_TE);
    this.holder = new CulinaryStackHandler();
    this.ingredients = new CulinaryStackHandler(5);
    this.output = new CulinaryStackHandler();
    this.holderOpt = LazyOptional.of(() -> holder);
    this.ingredientsOpt = LazyOptional.of(() -> ingredients);
    this.outputOpt = LazyOptional.of(() -> output);
  }

  @Override
  public void read(@Nonnull CompoundNBT compound) {
    super.read(compound);
    if (compound.contains("Holder", 10)) {
      this.holder.deserializeNBT(compound.getCompound("Holder"));
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
  public CompoundNBT write(@Nonnull CompoundNBT compound) {
    super.write(compound);
    compound.put("Holder", this.holder.serializeNBT());
    compound.put("Ingredients", this.ingredients.serializeNBT());
    compound.put("Output", this.output.serializeNBT());
    return compound;
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability,
      @Nullable Direction facing) {
    if (!this.removed && facing != null
        && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      if (facing == Direction.UP) {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, this.holderOpt);
      } else if (facing == Direction.DOWN) {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, this.outputOpt);
      } else {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
            .orEmpty(capability, this.ingredientsOpt);
      }
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public void remove() {
    super.remove();
    holderOpt.invalidate();
    ingredientsOpt.invalidate();
    outputOpt.invalidate();
  }

  private class CulinaryStackHandler extends ItemStackHandler {

    public CulinaryStackHandler() {
      super();
    }

    public CulinaryStackHandler(int size) {
      super(size);
    }

    @Override
    protected void onContentsChanged(int slot) {
      super.onContentsChanged(slot);
      CulinaryStationTileEntity.this.markDirty();
    }
  }
}
