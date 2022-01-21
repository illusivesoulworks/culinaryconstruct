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

package top.theillusivec4.culinaryconstruct.common.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.common.inventory.CulinaryStationContainer;
import top.theillusivec4.culinaryconstruct.common.blockentity.CulinaryStationBlockEntity;

@ObjectHolder(CulinaryConstruct.MOD_ID)
public class CulinaryConstructRegistry {

  @ObjectHolder(RegistryReference.CULINARY_STATION)
  public static final Block CULINARY_STATION;

  @ObjectHolder(RegistryReference.CULINARY_STATION)
  public static final MenuType<CulinaryStationContainer> CULINARY_STATION_CONTAINER;

  @ObjectHolder(RegistryReference.CULINARY_STATION)
  public static final BlockEntityType<CulinaryStationBlockEntity> CULINARY_STATION_TE;

  @ObjectHolder(RegistryReference.SANDWICH)
  public static final Item SANDWICH;

  @ObjectHolder(RegistryReference.FOOD_BOWL)
  public static final Item FOOD_BOWL;

  static {
    CULINARY_STATION = null;
    CULINARY_STATION_CONTAINER = null;
    CULINARY_STATION_TE = null;
    SANDWICH = null;
    FOOD_BOWL = null;
  }
}
