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

package com.illusivesoulworks.culinaryconstruct.platform;

import com.illusivesoulworks.culinaryconstruct.common.block.CulinaryStationMenu;
import com.illusivesoulworks.culinaryconstruct.common.registry.RegistryObject;
import com.illusivesoulworks.culinaryconstruct.common.registry.RegistryProvider;
import com.illusivesoulworks.culinaryconstruct.platform.services.IRegistrator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeRegistrator implements IRegistrator {

  @Override
  public Optional<Item> getItem(ResourceLocation resourceLocation) {
    return Optional.ofNullable(ForgeRegistries.ITEMS.getValue(resourceLocation));
  }

  @Override
  public TagKey<Item> createTag(String name) {
    return ItemTags.create(new ResourceLocation("forge", name));
  }

  @Override
  public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
    return stack.getFoodProperties(entity);
  }

  @Override
  public <T> RegistryProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey,
                                        String modId) {
    final Optional<? extends ModContainer> containerOpt = ModList.get().getModContainerById(modId);

    if (containerOpt.isEmpty()) {
      throw new NullPointerException("Cannot find mod container for id " + modId);
    }
    final ModContainer cont = containerOpt.get();

    if (cont instanceof FMLModContainer fmlModContainer) {
      final var register = DeferredRegister.create(resourceKey, modId);
      register.register(fmlModContainer.getEventBus());
      return new Provider<>(modId, register);
    } else {
      throw new ClassCastException("The container of the mod " + modId + " is not a FML one!");
    }
  }

  @SuppressWarnings("all")
  @Override
  public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(
      BiFunction<BlockPos, BlockState, T> builder, Block... blocks) {
    return BlockEntityType.Builder.of(builder::apply, blocks).build(null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <I extends MenuType<?>> I createMenuType() {
    return (I) IForgeMenuType.create(CulinaryStationMenu::new);
  }

  private static class Provider<T> implements RegistryProvider<T> {
    private final String modId;
    private final DeferredRegister<T> registry;

    private final Set<RegistryObject<T>> entries = new HashSet<>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries);

    private Provider(String modId, DeferredRegister<T> registry) {
      this.modId = modId;
      this.registry = registry;
    }

    @Override
    public String getModId() {
      return modId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier) {
      final var obj = registry.<I>register(name, supplier);
      final var ro = new RegistryObject<I>() {

        @Override
        public ResourceKey<I> getResourceKey() {
          return obj.getKey();
        }

        @Override
        public ResourceLocation getId() {
          return obj.getId();
        }

        @Override
        public I get() {
          return obj.get();
        }

        @Override
        public Holder<I> asHolder() {
          return obj.getHolder().orElseThrow();
        }
      };
      entries.add((RegistryObject<T>) ro);
      return ro;
    }

    @Override
    public Set<RegistryObject<T>> getEntries() {
      return entriesView;
    }
  }
}
