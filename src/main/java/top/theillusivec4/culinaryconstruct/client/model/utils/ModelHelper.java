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

package top.theillusivec4.culinaryconstruct.client.model.utils;

import com.google.common.collect.ImmutableList;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ItemLayerModel;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class ModelHelper {

  public static IBakedModel getBakedLayerModel(IModelConfiguration owner, ModelBakery bakery,
      Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform,
      ItemOverrideList overrides, ResourceLocation modelLocation) {
    return new ItemLayerModel(ImmutableList
        .of(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, modelLocation)))
        .bake(owner, bakery, spriteGetter, modelTransform, overrides, modelLocation);
  }

  //Cache Key from Tinkers' Construct
  public static class CacheKey {

    final IBakedModel parent;
    final CompoundNBT data;

    public CacheKey(IBakedModel parent, ItemStack stack) {
      this.parent = parent;
      this.data = CulinaryNBTHelper.getTagSafe(stack);
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

      if (parent != null ? parent != cacheKey.parent : cacheKey.parent != null) {
        return false;
      }
      return Objects.equals(data, cacheKey.data);
    }

    @Override
    public int hashCode() {
      int result = parent != null ? parent.hashCode() : 0;
      result = 31 * result + (data != null ? data.hashCode() : 0);
      return result;
    }
  }
}
