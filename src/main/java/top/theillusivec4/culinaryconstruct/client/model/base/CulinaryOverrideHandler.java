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
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.client.model.utils.ModelHelper.CacheKey;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public abstract class CulinaryOverrideHandler<T extends IModelGeometry<T>> extends
    ItemOverrideList {

  protected final T model;
  protected final ModelBakery bakery;
  protected final IModelConfiguration owner;
  protected final Function<RenderMaterial, TextureAtlasSprite> spriteGetter;
  protected final IModelTransform modelTransform;
  protected final ResourceLocation modelLocation;
  private Cache<CacheKey, IBakedModel> bakedModelCache = CacheBuilder.newBuilder().maximumSize(1000)
      .expireAfterWrite(5, TimeUnit.MINUTES).build();

  public CulinaryOverrideHandler(T model, IModelConfiguration owner, ModelBakery bakery,
      Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform,
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
  public IBakedModel func_239290_a_(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack,
      @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn) {
    CompoundNBT data = CulinaryNBTHelper.getTagSafe(stack);
    IBakedModel output = originalModel;

    if (!data.isEmpty()) {
      CacheKey key = getCacheKey(originalModel, stack);
      try {
        output = bakedModelCache
            .get(key, () -> getBakedModel(originalModel, stack, worldIn, entityIn));
      } catch (ExecutionException e) {
        CulinaryConstruct.LOGGER.error("Error baking model!");
      }
    }
    return output;
  }

  protected abstract IBakedModel getBakedModel(IBakedModel originalModel, ItemStack stack,
      @Nullable World world, @Nullable LivingEntity entity);

  CacheKey getCacheKey(IBakedModel original, ItemStack stack) {
    return new CacheKey(original, stack);
  }
}
