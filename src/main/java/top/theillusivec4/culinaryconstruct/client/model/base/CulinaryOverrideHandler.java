/*
 * Copyright (c) 2018-2020 C4
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

package top.theillusivec4.culinaryconstruct.client.model.base;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.client.model.utils.ModelHelper.CacheKey;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public abstract class CulinaryOverrideHandler<T extends IModelGeometry<T>> extends
    ItemOverrides {

  protected final T model;
  protected final ModelBakery bakery;
  protected final IModelConfiguration owner;
  protected final Function<Material, TextureAtlasSprite> spriteGetter;
  protected final ModelState modelTransform;
  protected final ResourceLocation modelLocation;
  private Cache<CacheKey, BakedModel> bakedModelCache = CacheBuilder.newBuilder().maximumSize(1000)
      .expireAfterWrite(5, TimeUnit.MINUTES).build();

  public CulinaryOverrideHandler(T model, IModelConfiguration owner, ModelBakery bakery,
                                 Function<Material, TextureAtlasSprite> spriteGetter,
                                 ModelState modelTransform,
                                 ResourceLocation modelLocation) {
    super();
    this.model = model;
    this.owner = owner;
    this.bakery = bakery;
    this.spriteGetter = spriteGetter;
    this.modelLocation = modelLocation;
    this.modelTransform = modelTransform;
  }

  @Nonnull
  @Override
  public BakedModel resolve(@Nonnull BakedModel originalModel, @Nonnull ItemStack stack,
                            @Nullable ClientLevel level, @Nullable LivingEntity entity, int pSeed) {
    CompoundTag data = CulinaryNBTHelper.getTagSafe(stack);
    BakedModel output = originalModel;

    if (!data.isEmpty()) {
      CacheKey key = getCacheKey(originalModel, stack);
      try {
        output = bakedModelCache
            .get(key, () -> getBakedModel(originalModel, stack, level, entity));
      } catch (ExecutionException e) {
        CulinaryConstruct.LOGGER.error("Error baking model!");
      }
    }
    return output;
  }

  protected abstract BakedModel getBakedModel(BakedModel originalModel, ItemStack stack,
                                              @Nullable Level world, @Nullable LivingEntity entity);

  CacheKey getCacheKey(BakedModel original, ItemStack stack) {
    return new CacheKey(original, stack);
  }
}
