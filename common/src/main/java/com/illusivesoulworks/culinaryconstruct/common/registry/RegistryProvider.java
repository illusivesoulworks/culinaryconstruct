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

import com.illusivesoulworks.culinaryconstruct.platform.Services;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface RegistryProvider<T> {

  static <T> RegistryProvider<T> get(ResourceKey<? extends Registry<T>> resourceKey, String modId) {
    return Services.REGISTRY.create(resourceKey, modId);
  }

  static <T> RegistryProvider<T> get(Registry<T> registry, String modId) {
    return Services.REGISTRY.create(registry, modId);
  }

  <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier);

  Collection<RegistryObject<T>> getEntries();

  String getModId();
}
