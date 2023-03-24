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

package com.illusivesoulworks.culinaryconstruct.common.registry;

import com.illusivesoulworks.culinaryconstruct.CulinaryConstructConstants;
import com.illusivesoulworks.culinaryconstruct.common.block.CulinaryStationBlock;
import com.illusivesoulworks.culinaryconstruct.common.block.CulinaryStationBlockEntity;
import com.illusivesoulworks.culinaryconstruct.common.block.CulinaryStationMenu;
import com.illusivesoulworks.culinaryconstruct.common.item.FoodBowlItem;
import com.illusivesoulworks.culinaryconstruct.common.item.SandwichItem;
import com.illusivesoulworks.culinaryconstruct.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CulinaryConstructRegistry {

  public static final RegistryProvider<Item> ITEMS = RegistryProvider.get(Registries.ITEM,
      CulinaryConstructConstants.MOD_ID);
  public static final RegistryProvider<Block> BLOCKS = RegistryProvider.get(Registries.BLOCK,
      CulinaryConstructConstants.MOD_ID);
  public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
      RegistryProvider.get(Registries.BLOCK_ENTITY_TYPE,
          CulinaryConstructConstants.MOD_ID);
  public static final RegistryProvider<MenuType<?>> CONTAINER_MENUS =
      RegistryProvider.get(Registries.MENU, CulinaryConstructConstants.MOD_ID);

  public static final RegistryObject<Item> SANDWICH =
      ITEMS.register(CulinaryConstructConstants.SANDWICH_ID,
          SandwichItem::new);
  public static final RegistryObject<Item> BOWL =
      ITEMS.register(CulinaryConstructConstants.FOOD_BOWL_ID, FoodBowlItem::new);
  public static final RegistryObject<Block> CULINARY_STATION_BLOCK =
      BLOCKS.register(CulinaryConstructConstants.CULINARY_STATION_ID, CulinaryStationBlock::new);
  public static final RegistryObject<Item> CULINARY_STATION_ITEM =
      ITEMS.register(CulinaryConstructConstants.CULINARY_STATION_ID,
          () -> new BlockItem(CULINARY_STATION_BLOCK.get(), (new Item.Properties())));
  public static final RegistryObject<BlockEntityType<CulinaryStationBlockEntity>>
      CULINARY_STATION_BLOCK_ENTITY =
      BLOCK_ENTITY_TYPES.register(CulinaryConstructConstants.CULINARY_STATION_ID,
          () -> Services.REGISTRY.createBlockEntityType(CulinaryStationBlockEntity::new,
              CULINARY_STATION_BLOCK.get()));
  public static final RegistryObject<MenuType<CulinaryStationMenu>> CULINARY_STATION_MENU =
      CONTAINER_MENUS.register(CulinaryConstructConstants.CULINARY_STATION_ID,
          Services.REGISTRY::createMenuType);

  public static void setup() {

  }
}
