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

public class FoodBowlModel implements IUnbakedModel {

  @Nullable
  private final List<TextureAtlasSprite> ingredients;
  @Nullable
  private final List<Integer> layers;
  @Nullable
  private final List<Integer> liquids;

  public FoodBowlModel(@Nullable List<TextureAtlasSprite> ingredients,
      @Nullable List<Integer> layers, @Nullable List<Integer> liquids) {
    this.ingredients = ingredients;
    this.layers = layers;
    this.liquids = liquids;
  }

  @Nonnull
  @Override
  public Collection<ResourceLocation> getTextures(
      @Nonnull Function<ResourceLocation, IUnbakedModel> modelGetter,
      @Nonnull Set<String> missingTextureErrors) {
    ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

    if (ingredients != null && layers != null) {
      builder.add(new ResourceLocation("minecraft:item/bowl"));
      builder.add(new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/liquid_base"));
      builder.add(new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/liquid_overflow"));

      for (TextureAtlasSprite sprite : ingredients) {
        builder.add(sprite.getName());
      }

      for (int layer : layers) {
        builder.add(new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/layer" + layer));
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
    IBakedModel model = ModelHelper
        .getBakedLayerModel(new ResourceLocation("minecraft:item/bowl"), bakery, spriteGetter,
            sprite, format);
    builder.addAll(model.getQuads(null, null, random, EmptyModelData.INSTANCE));
    particleSprite = model.getParticleTexture(EmptyModelData.INSTANCE);

    if (ingredients != null && layers != null) {
      List<Integer> ingredientColors = new ArrayList<>();

      for (TextureAtlasSprite ing : this.ingredients) {
        ingredientColors.add(ColorHelper.getDominantColor(ing));
      }

      if (this.liquids != null) {
        List<Integer> opaqueColors = new ArrayList<>();
        this.liquids.forEach(color -> {
          if (color != null) {
            opaqueColors.add(color);
          }
        });
        boolean isOpaque = !opaqueColors.isEmpty();
        int liquidColor = isOpaque ? ColorHelper.getMixedColor(this.liquids)
            : ColorHelper.getMixedColor(ingredientColors);
        IBakedModel liquidBase = ModelHelper.getBakedLayerModel(
            new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/liquid_base"), bakery,
            spriteGetter, sprite, format);
        ColorHelper.colorQuads(liquidBase, liquidColor, random, builder);

        if (this.ingredients.size() >= 3) {
          IBakedModel liquidOverflow = ModelHelper.getBakedLayerModel(
              new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/liquid_overflow"), bakery,
              spriteGetter, sprite, format);
          ColorHelper.colorQuads(liquidOverflow, liquidColor, random, builder);
        }
      }

      for (int i = 0; i < ingredients.size(); i++) {
        IBakedModel ingredient = ModelHelper.getBakedLayerModel(
            new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/layer" + layers.get(i)),
            bakery, spriteGetter, sprite, format);
        ColorHelper.colorQuads(ingredient, ingredientColors.get(i), random, builder);
      }
    }
    return new BakedItemModel(builder.build(), particleSprite, transformMap, ItemOverrideList.EMPTY,
        transform.isIdentity());
  }

  public static final class BakedFoodBowlOverrideHandler extends CulinaryOverrideHandler {

    public BakedFoodBowlOverrideHandler(ModelBakery bakery, BlockModel unbaked) {
      super(bakery, unbaked);
    }

    @Override
    protected IBakedModel getBakedModel(ItemStack stack) {
      ImmutableList.Builder<TextureAtlasSprite> builder = ImmutableList.builder();
      List<ItemStack> solids = CulinaryNBTHelper.getSolids(stack);

      for (ItemStack ing : solids) {
        builder.add(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(ing)
            .getParticleTexture(EmptyModelData.INSTANCE));
      }
      List<Integer> list = new ArrayList<>();

      for (int i = 0; i < solids.size(); i++) {
        list.add(i);
      }
      List<Integer> liquids = CulinaryNBTHelper.getLiquids(stack);
      IUnbakedModel parent = new FoodBowlModel(builder.build(), list, liquids);
      Function<ResourceLocation, TextureAtlasSprite> textureGetter;
      textureGetter = location -> Minecraft.getInstance().getTextureMap()
          .getAtlasSprite(location.toString());
      return parent.bake(bakery, textureGetter, new SimpleModelState(
              ImmutableMap.copyOf(PerspectiveMapWrapper.getTransforms(unbaked.getAllTransforms()))),
          DefaultVertexFormats.ITEM);
    }
  }
}
