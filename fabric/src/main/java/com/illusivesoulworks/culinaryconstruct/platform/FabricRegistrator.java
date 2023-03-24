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

import com.illusivesoulworks.culinaryconstruct.CulinaryConstructConstants;
import com.illusivesoulworks.culinaryconstruct.common.block.CulinaryStationMenu;
import com.illusivesoulworks.culinaryconstruct.common.registry.RegistryObject;
import com.illusivesoulworks.culinaryconstruct.common.registry.RegistryProvider;
import com.illusivesoulworks.culinaryconstruct.platform.services.IRegistrator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.FabricScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.tag.convention.TagRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FabricRegistrator implements IRegistrator {

  @Override
  public Optional<Item> getItem(ResourceLocation resourceLocation) {
    return Optional.of(BuiltInRegistries.ITEM.get(resourceLocation));
  }

  @Override
  public TagKey<Item> createTag(String name) {
    return TagRegistration.ITEM_TAG_REGISTRATION.registerCommon(name);
  }

  @Override
  public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
    return stack.getItem().getFoodProperties();
  }

  @Override
  public <T> RegistryProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey,
                                        String modId) {
    return new Provider<>(modId, resourceKey);
  }

  @Override
  public <T> RegistryProvider<T> create(Registry<T> registry, String modId) {
    return new Provider<>(modId, registry);
  }

  @Override
  public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(
      BiFunction<BlockPos, BlockState, T> builder, Block... blocks) {
    return FabricBlockEntityTypeBuilder.create(builder::apply, blocks).build();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <I extends MenuType<?>> I createMenuType() {
    return (I) new MenuType<>((id, inventory) -> new CulinaryStationMenu(id, inventory,
        PacketByteBufs.empty()), FeatureFlagSet.of());
  }

  private static class Provider<T> implements RegistryProvider<T> {
    private final String modId;
    private final Registry<T> registry;

    private final Set<RegistryObject<T>> entries = new HashSet<>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries);

    @SuppressWarnings({"unchecked"})
    private Provider(String modId, ResourceKey<? extends Registry<T>> key) {
      this.modId = modId;

      final var reg = BuiltInRegistries.REGISTRY.get(key.location());
      if (reg == null) {
        throw new RuntimeException("Registry with name " + key.location() + " was not found!");
      }
      registry = (Registry<T>) reg;
    }

    private Provider(String modId, Registry<T> registry) {
      this.modId = modId;
      this.registry = registry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier) {
      final ResourceLocation rl = new ResourceLocation(modId, name);
      final I obj = Registry.register(registry, rl, supplier.get());
      final RegistryObject<I> ro = new RegistryObject<>() {
        final ResourceKey<I> key =
            ResourceKey.create((ResourceKey<? extends Registry<I>>) registry.key(), rl);

        @Override
        public ResourceKey<I> getResourceKey() {
          return key;
        }

        @Override
        public ResourceLocation getId() {
          return rl;
        }

        @Override
        public I get() {
          return obj;
        }

        @Override
        public Holder<I> asHolder() {
          return (Holder<I>) registry.getHolderOrThrow((ResourceKey<T>) this.key);
        }
      };
      entries.add((RegistryObject<T>) ro);
      return ro;
    }

    @Override
    public Collection<RegistryObject<T>> getEntries() {
      return entriesView;
    }

    @Override
    public String getModId() {
      return modId;
    }
  }
}
