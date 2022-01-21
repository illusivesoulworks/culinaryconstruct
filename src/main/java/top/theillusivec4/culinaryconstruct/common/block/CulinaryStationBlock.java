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

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.CapabilityItemHandler;
import top.theillusivec4.culinaryconstruct.common.blockentity.CulinaryStationBlockEntity;
import top.theillusivec4.culinaryconstruct.common.inventory.CulinaryStationContainer;
import top.theillusivec4.culinaryconstruct.common.registry.RegistryReference;

public class CulinaryStationBlock extends Block implements EntityBlock {

  private static final Component CONTAINER_NAME = new TranslatableComponent(
      "culinaryconstruct.culinary_container");

  public CulinaryStationBlock() {
    super(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));
    this.setRegistryName(RegistryReference.CULINARY_STATION);
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

    if (state.getBlock() != newState.getBlock()) {
      BlockEntity tileentity = worldIn.getBlockEntity(pos);

      if (tileentity instanceof CulinaryStationBlockEntity cte) {
        NonNullList<ItemStack> items = NonNullList.create();
        cte.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP)
            .ifPresent(cap -> {
              for (int i = 0; i < cap.getSlots(); i++) {
                items.add(cap.getStackInSlot(i));
              }
            });
        cte.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.EAST)
            .ifPresent(cap -> {
              for (int i = 0; i < cap.getSlots(); i++) {
                items.add(cap.getStackInSlot(i));
              }
            });
        Containers.dropContents(worldIn, pos, items);
        worldIn.updateNeighbourForOutputSignal(pos, this);
      }
      super.onRemove(state, worldIn, pos, newState, isMoving);
    }
  }

  @Override
  public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
    return new CulinaryStationBlockEntity(pos, state);
  }

  @SuppressWarnings("deprecation")
  @Override
  public MenuProvider getMenuProvider(@Nonnull BlockState state, @Nonnull Level worldIn,
                                      @Nonnull BlockPos pos) {
    return new SimpleMenuProvider(
        (windowId, playerInventory, playerEntity) -> new CulinaryStationContainer(windowId,
            playerInventory, ContainerLevelAccess.create(worldIn, pos),
            worldIn.getBlockEntity(pos)),
        CONTAINER_NAME);
  }
}
