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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.client.model.utils.ModelHelper.CacheKey;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public abstract class CulinaryOverrideHandler extends ItemOverrideList {

  protected final ModelBakery bakery;
  protected final BlockModel unbaked;
  private Cache<CacheKey, IBakedModel> bakedModelCache = CacheBuilder.newBuilder().maximumSize(1000)
      .expireAfterWrite(5, TimeUnit.MINUTES).build();

  public CulinaryOverrideHandler(ModelBakery bakery, BlockModel unbaked) {
    super();
    this.bakery = bakery;
    this.unbaked = unbaked;
  }

  @Nonnull
  @Override
  public IBakedModel getModelWithOverrides(@Nonnull IBakedModel model, @Nonnull ItemStack stack,
      @Nullable World worldIn, @Nullable LivingEntity entityIn) {
    CompoundNBT data = CulinaryNBTHelper.getTagSafe(stack);
    IBakedModel output = model;

    if (!data.isEmpty()) {
      CulinaryModelWrapper original = (CulinaryModelWrapper) model;
      CacheKey key = getCacheKey(stack, original);
      try {
        output = bakedModelCache.get(key, () -> getBakedModel(stack));
      } catch (ExecutionException e) {
        CulinaryConstruct.LOGGER.error("Error baking model!");
      }
    }
    return output;
  }

  protected abstract IBakedModel getBakedModel(ItemStack stack);

  CacheKey getCacheKey(ItemStack stack, CulinaryModelWrapper original) {
    return new CacheKey(original, stack);
  }
}
