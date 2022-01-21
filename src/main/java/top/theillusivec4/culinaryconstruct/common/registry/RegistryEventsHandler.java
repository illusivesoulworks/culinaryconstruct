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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.culinaryconstruct.common.block.CulinaryStationBlock;
import top.theillusivec4.culinaryconstruct.common.inventory.CulinaryStationContainer;
import top.theillusivec4.culinaryconstruct.common.item.FoodBowlItem;
import top.theillusivec4.culinaryconstruct.common.item.SandwichItem;
import top.theillusivec4.culinaryconstruct.common.blockentity.CulinaryStationBlockEntity;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEventsHandler {

  @SubscribeEvent
  public static void onBlocksRegistry(final RegistryEvent.Register<Block> evt) {
    evt.getRegistry().register(new CulinaryStationBlock());
  }

  @SubscribeEvent
  public static void onItemsRegistry(final RegistryEvent.Register<Item> evt) {
    BlockItem culinaryStation = new BlockItem(CulinaryConstructRegistry.CULINARY_STATION,
        (new Item.Properties()).tab(CreativeModeTab.TAB_DECORATIONS));
    culinaryStation.setRegistryName(RegistryReference.CULINARY_STATION);
    evt.getRegistry().registerAll(culinaryStation, new SandwichItem(), new FoodBowlItem());
  }

  @SubscribeEvent
  public static void onContainerRegistry(final RegistryEvent.Register<MenuType<?>> evt) {
    evt.getRegistry().register(IForgeMenuType.create(CulinaryStationContainer::new)
        .setRegistryName(RegistryReference.CULINARY_STATION));
  }

  @SuppressWarnings("ConstantConditions")
  @SubscribeEvent
  public static void onTileEntityRegistry(final RegistryEvent.Register<BlockEntityType<?>> evt) {
    evt.getRegistry().register(BlockEntityType.Builder
        .of(CulinaryStationBlockEntity::new, CulinaryConstructRegistry.CULINARY_STATION)
        .build(null).setRegistryName(RegistryReference.CULINARY_STATION));
  }
}
