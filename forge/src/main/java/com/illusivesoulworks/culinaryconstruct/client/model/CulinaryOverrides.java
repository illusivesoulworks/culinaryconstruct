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
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

public abstract class CulinaryOverrides<T extends IUnbakedGeometry<T>> extends
    ItemOverrides {

  protected final T model;
  protected final ModelBaker baker;
  protected final IGeometryBakingContext context;
  protected final Function<Material, TextureAtlasSprite> spriteGetter;
  protected final ModelState modelState;
  protected final ResourceLocation modelLocation;

  private final Cache<CacheKey, BakedModel> bakedModelCache =
      CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(5, TimeUnit.MINUTES).build();

  public CulinaryOverrides(T model, IGeometryBakingContext context, ModelBaker baker,
                           Function<Material, TextureAtlasSprite> spriteGetter,
                           ModelState modelState,
                           ResourceLocation modelLocation) {
    super();
    this.model = model;
    this.context = context;
    this.baker = baker;
    this.spriteGetter = spriteGetter;
    this.modelLocation = modelLocation;
    this.modelState = modelState;
  }

  @Nonnull
  @Override
  public BakedModel resolve(@Nonnull BakedModel originalModel, @Nonnull ItemStack stack,
                            @Nullable ClientLevel level, @Nullable LivingEntity entity, int pSeed) {
    CompoundTag data = CulinaryNBT.getTagSafe(stack);
    BakedModel output = originalModel;

    if (!data.isEmpty()) {
      CacheKey key = new CacheKey(originalModel, stack);
      try {
        output = bakedModelCache.get(key, () -> getBakedModel(originalModel, stack, level, entity));
      } catch (ExecutionException e) {
        CulinaryConstructConstants.LOG.error("Error baking model!");
      }
    }
    return output;
  }

  protected abstract BakedModel getBakedModel(BakedModel originalModel, ItemStack stack,
                                              @Nullable Level world, @Nullable LivingEntity entity);

  public static class CacheKey {

    final BakedModel parent;
    final CompoundTag data;

    public CacheKey(BakedModel parent, ItemStack stack) {
      this.parent = parent;
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

      if (this.parent != null ? this.parent != cacheKey.parent : cacheKey.parent != null) {
        return false;
      }
      return Objects.equals(this.data, cacheKey.data);
    }

    @Override
    public int hashCode() {
      int result = this.parent != null ? this.parent.hashCode() : 0;
      result = 31 * result + (this.data != null ? this.data.hashCode() : 0);
      return result;
    }
  }
}
