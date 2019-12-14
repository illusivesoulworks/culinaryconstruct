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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;

public class CulinaryStationTileEntity extends TileEntity {

  public static final String REGISTRY_NAME = "culinary_station_te";

  public final List<LazyOptional<IItemHandler>> handlers = new ArrayList<>(Arrays
      .asList(LazyOptional.of(ItemStackHandler::new),
          LazyOptional.of(() -> new ItemStackHandler(5)), LazyOptional.of(ItemStackHandler::new)));

  public CulinaryStationTileEntity() {
    super(CulinaryConstructRegistry.CULINARY_STATION_TE);
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability,
      @Nullable Direction facing) {
    if (!this.removed && facing != null
        && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      if (facing == Direction.UP) {
        return handlers.get(0).cast();
      } else if (facing == Direction.DOWN) {
        return handlers.get(2).cast();
      } else {
        return handlers.get(1).cast();
      }
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public void remove() {
    super.remove();

    for (LazyOptional<IItemHandler> handler : handlers) {
      handler.invalidate();
    }
  }
}
