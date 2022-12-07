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

package com.illusivesoulworks.culinaryconstruct.client.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.illusivesoulworks.culinaryconstruct.CulinaryConstructConstants;
import com.illusivesoulworks.culinaryconstruct.common.util.CulinaryNBT;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class CachedMeshModel implements UnbakedModel, BakedModel, FabricBakedModel {

  private final Cache<CacheKey, Mesh> meshCache =
      CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(5, TimeUnit.MINUTES).build();

  protected Mesh getOrBuildMesh(ItemStack stack, List<ItemStack> ingredients,
                                ModelManager modelManager, Supplier<RandomSource> randomSupplier) {
    CompoundTag data = CulinaryNBT.getTagSafe(stack);

    if (!data.isEmpty()) {
      try {
        return meshCache.get(new CacheKey(stack),
            () -> buildMesh(stack, ingredients, modelManager, randomSupplier));
      } catch (ExecutionException e) {
        CulinaryConstructConstants.LOG.error("Error baking model!");
      }
    }
    return null;
  }

  protected abstract Mesh buildMesh(ItemStack stack, List<ItemStack> ingredients,
                                    ModelManager modelManager,
                                    Supplier<RandomSource> randomSupplier);

  private static ItemTransforms generated;

  @Override
  public ItemTransforms getTransforms() {

    if (generated == null) {
      Optional<Resource> resource = Minecraft.getInstance().getResourceManager()
          .getResource(new ResourceLocation("models/item/generated.json"));
      resource.ifPresent(resource1 -> {
        try {
          generated =
              BlockModel.fromStream(new BufferedReader(new InputStreamReader(resource1.open())))
                  .getTransforms();
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }
    return generated;
  }


  public static class CacheKey {

    final Item item;
    final CompoundTag data;

    public CacheKey(ItemStack stack) {
      this.item = stack.getItem();
      this.data = CulinaryNBT.getTagSafe(stack);
    }

    @Override
    public boolean equals(Object o) {

      if (this == o) {
        return true;
      }

      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      CacheKey cacheKey = (CacheKey) o;

      if (this.item != null ? this.item != cacheKey.item : cacheKey.item != null) {
        return false;
      }
      return Objects.equals(this.data, cacheKey.data);
    }

    @Override
    public int hashCode() {
      int result = this.item != null ? this.item.hashCode() : 0;
      result = 31 * result + (this.data != null ? this.data.hashCode() : 0);
      return result;
    }
  }
}
