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

package top.theillusivec4.culinaryconstruct.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.client.model.base.CulinaryOverrideHandler;
import top.theillusivec4.culinaryconstruct.client.model.utils.ColorHelper;
import top.theillusivec4.culinaryconstruct.client.model.utils.ModelHelper;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class SandwichModel implements IUnbakedModel {

  @Nullable
  private final List<TextureAtlasSprite> ingredients;
  @Nullable
  private final List<Integer> layers;
  private final int baseIndex;

  public SandwichModel(@Nullable List<TextureAtlasSprite> ingredients,
      @Nullable List<Integer> layers) {
    this.ingredients = ingredients;
    this.layers = layers;
    this.baseIndex = ingredients == null ? 0 : ingredients.size() - 1;
  }

  @Nonnull
  @Override
  public Collection<ResourceLocation> getTextures(
      @Nonnull Function<ResourceLocation, IUnbakedModel> modelGetter,
      @Nonnull Set<String> missingTextureErrors) {
    ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

    if (ingredients != null && layers != null) {
      builder.add(new ResourceLocation(CulinaryConstruct.MODID, "item/sandwich/bread" + baseIndex));

      for (TextureAtlasSprite sprite : ingredients) {
        builder.add(sprite.getName());
      }

      for (int layer : layers) {
        builder.add(new ResourceLocation(CulinaryConstruct.MODID, "item/sandwich/layer" + layer));
      }
    }
    return builder.build();
  }

  @Nonnull
  @Override
  public Collection<ResourceLocation> getDependencies() {
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public IBakedModel bake(@Nonnull ModelBakery bakery,
      @Nonnull Function<ResourceLocation, TextureAtlasSprite> spriteGetter, @Nonnull ISprite sprite,
      @Nonnull VertexFormat format) {
    TextureAtlasSprite particleSprite;
    IModelState state = sprite.getState();
    ImmutableMap<TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper
        .getTransforms(state);
    TRSRTransformation transform = state.apply(Optional.empty())
        .orElse(TRSRTransformation.identity());
    Random random = new Random();
    random.setSeed(42);
    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
    IBakedModel model = ModelHelper.getBakedLayerModel(
        new ResourceLocation(CulinaryConstruct.MODID, "item/sandwich/bread" + baseIndex), bakery,
        spriteGetter, sprite, format);
    builder.addAll(model.getQuads(null, null, random, EmptyModelData.INSTANCE));
    particleSprite = model.getParticleTexture(EmptyModelData.INSTANCE);

    if (ingredients != null && layers != null) {
      List<Integer> ingredientColors = new ArrayList<>();

      for (TextureAtlasSprite ing : this.ingredients) {
        ingredientColors.add(ColorHelper.getDominantColor(ing));
      }

      for (int i = 0; i < ingredients.size(); i++) {
        IBakedModel ingredient = ModelHelper.getBakedLayerModel(
            new ResourceLocation(CulinaryConstruct.MODID, "item/sandwich/layer" + layers.get(i)),
            bakery, spriteGetter, sprite, format);
        ColorHelper.colorQuads(ingredient, ingredientColors.get(i), random, builder);
      }
    }
    return new BakedItemModel(builder.build(), particleSprite, transformMap, ItemOverrideList.EMPTY,
        transform.isIdentity());
  }

  public static final class BakedSandwichOverrideHandler extends CulinaryOverrideHandler {

    public BakedSandwichOverrideHandler(ModelBakery bakery, BlockModel unbaked) {
      super(bakery, unbaked);
    }

    @Override
    protected IBakedModel getBakedModel(ItemStack stack) {
      ImmutableList.Builder<TextureAtlasSprite> builder = ImmutableList.builder();
      NonNullList<ItemStack> ingredients = CulinaryNBTHelper.getIngredientsList(stack);

      for (ItemStack ing : ingredients) {
        builder.add(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(ing)
            .getParticleTexture(EmptyModelData.INSTANCE));
      }
      int size = CulinaryNBTHelper.getSize(stack);
      List<Integer> list = new ArrayList<>();

      switch (size) {
        case 1:
          list.add(2);
          break;
        case 2:
          list.addAll(Arrays.asList(1, 2));
          break;
        case 3:
          list.addAll(Arrays.asList(1, 2, 3));
          break;
        case 4:
          list.addAll(Arrays.asList(0, 1, 2, 3));
          break;
        case 5:
          list.addAll(Arrays.asList(0, 1, 2, 3, 4));
          break;
      }
      IUnbakedModel parent = new SandwichModel(builder.build(), list);
      Function<ResourceLocation, TextureAtlasSprite> textureGetter;
      textureGetter = location -> Minecraft.getInstance().getTextureMap()
          .getAtlasSprite(location.toString());
      return parent.bake(bakery, textureGetter, new SimpleModelState(
              ImmutableMap.copyOf(PerspectiveMapWrapper.getTransforms(unbaked.getAllTransforms()))),
          DefaultVertexFormats.ITEM);
    }
  }
}
