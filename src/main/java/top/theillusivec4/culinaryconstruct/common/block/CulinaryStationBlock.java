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

package top.theillusivec4.culinaryconstruct.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import top.theillusivec4.culinaryconstruct.common.inventory.CulinaryStationContainer;

public class CulinaryStationBlock extends Block {

  public static final String REGISTRY_NAME = "culinary_station";

  private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent(
      "culinaryconstruct.culinary_container");

  public CulinaryStationBlock() {
    super(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD));
    this.setRegistryName(REGISTRY_NAME);
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos,
      PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    player.openContainer(state.getContainer(worldIn, pos));
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
    return new SimpleNamedContainerProvider(
        (windowId, playerInventory, playerEntity) -> new CulinaryStationContainer(windowId, playerInventory,
            IWorldPosCallable.of(worldIn, pos)), CONTAINER_NAME);
  }
}
