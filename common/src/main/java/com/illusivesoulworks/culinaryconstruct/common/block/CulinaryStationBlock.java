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

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CulinaryStationBlock extends Block implements EntityBlock {

  private static final Component CONTAINER_NAME =
      Component.translatable("culinaryconstruct.culinary_container");

  public CulinaryStationBlock() {
    super(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));
  }

  @Nonnull
  @SuppressWarnings("deprecation")
  @Override
  public InteractionResult use(BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos,
                               @Nonnull Player player, @Nonnull InteractionHand handIn,
                               @Nonnull BlockHitResult hit) {
    player.openMenu(state.getMenuProvider(worldIn, pos));
    return InteractionResult.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onRemove(BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos,
                       BlockState newState, boolean isMoving) {

    if (!state.is(newState.getBlock())) {
      BlockEntity blockentity = worldIn.getBlockEntity(pos);

      if (blockentity instanceof CulinaryStationBlockEntity culinaryStationBlockEntity) {

        if (worldIn instanceof ServerLevel) {
          Containers.dropContents(worldIn, pos, culinaryStationBlockEntity);
        }
        worldIn.updateNeighbourForOutputSignal(pos, this);
      }
      super.onRemove(state, worldIn, pos, newState, isMoving);
    }
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
    return new CulinaryStationBlockEntity(pos, state);
  }

  @SuppressWarnings("deprecation")
  @Override
  public MenuProvider getMenuProvider(@Nonnull BlockState state, @Nonnull Level worldIn,
                                      @Nonnull BlockPos pos) {
    BlockEntity blockEntity = worldIn.getBlockEntity(pos);

    if (blockEntity instanceof CulinaryStationBlockEntity culinaryStationBlockEntity) {
      return new SimpleMenuProvider(
          (windowId, playerInventory, playerEntity) -> new CulinaryStationMenu(windowId,
              playerInventory, culinaryStationBlockEntity),
          CONTAINER_NAME);
    } else {
      return null;
    }
  }
}
