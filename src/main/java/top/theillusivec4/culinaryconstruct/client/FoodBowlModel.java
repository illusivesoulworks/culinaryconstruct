/*
 * Copyright (c) 2018-2019 C4
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

package top.theillusivec4.culinaryconstruct.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
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
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.api.CulinaryConstructAPI;
import top.theillusivec4.culinaryconstruct.api.capability.ICulinaryIngredient;
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
    IBakedModel model = (new ItemLayerModel(
        ImmutableList.of(new ResourceLocation("minecraft:item/bowl")))
        .bake(bakery, spriteGetter, sprite, format));
    builder.addAll(model.getQuads(null, null, random, EmptyModelData.INSTANCE));
    particleSprite = model.getParticleTexture(EmptyModelData.INSTANCE);

    if (ingredients != null && layers != null) {
      List<Integer> ingredientColors = new ArrayList<>();

      for (TextureAtlasSprite ing : this.ingredients) {
        ingredientColors.add(getDominantColor(ing));
      }

      if (this.liquids != null) {
        List<Integer> opaqueColors = new ArrayList<>();
        this.liquids.forEach(color -> {
          if (color != null) {
            opaqueColors.add(color);
          }
        });
        boolean isOpaque = !opaqueColors.isEmpty();
        int liquidColor = isOpaque ? getMixedColor(this.liquids) : getMixedColor(ingredientColors);
        IBakedModel model2 = (new ItemLayerModel(ImmutableList
            .of(new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/liquid_base")))
            .bake(bakery, spriteGetter, sprite, format));
        List<BakedQuad> quads = model2.getQuads(null, null, random, EmptyModelData.INSTANCE);

        for (BakedQuad quad : quads) {
          ColorTransformer transformer = new ColorTransformer(liquidColor, quad.getFormat());
          quad.pipe(transformer);
          builder.add(transformer.build());
        }

        if (this.ingredients.size() > 3) {
          IBakedModel model3 = (new ItemLayerModel(ImmutableList
              .of(new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/liquid_overflow")))
              .bake(bakery, spriteGetter, sprite, format));
          List<BakedQuad> quads2 = model3.getQuads(null, null, random, EmptyModelData.INSTANCE);

          for (BakedQuad quad : quads2) {
            ColorTransformer transformer = new ColorTransformer(liquidColor, quad.getFormat());
            quad.pipe(transformer);
            builder.add(transformer.build());
          }
        }
      }

      for (int i = 0; i < ingredients.size(); i++) {
        IBakedModel model1 = (new ItemLayerModel(ImmutableList
            .of(new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/layer" + layers.get(i))))
            .bake(bakery, spriteGetter, sprite, format));
        List<BakedQuad> quads = model1.getQuads(null, null, random, EmptyModelData.INSTANCE);

        for (BakedQuad quad : quads) {
          ColorTransformer transformer = new ColorTransformer(getDominantColor(ingredients.get(i)),
              quad.getFormat());
          quad.pipe(transformer);
          builder.add(transformer.build());
        }
      }
    }
    return new BakedItemModel(builder.build(), particleSprite, transformMap, ItemOverrideList.EMPTY,
        transform.isIdentity());
  }

  private int getMixedColor(List<Integer> colors) {
    int[] aint = new int[3];
    int i = 0;
    int j = 0;

    for (Integer color : colors) {
      float f = (float) (color >> 16 & 255) / 255.0F;
      float f1 = (float) (color >> 8 & 255) / 255.0F;
      float f2 = (float) (color & 255) / 255.0F;
      i = (int) ((float) i + Math.max(f, Math.max(f1, f2)) * 255.0F);
      aint[0] = (int) ((float) aint[0] + f * 255.0F);
      aint[1] = (int) ((float) aint[1] + f1 * 255.0F);
      aint[2] = (int) ((float) aint[2] + f2 * 255.0F);
      ++j;
    }
    int j1 = aint[0] / j;
    int k1 = aint[1] / j;
    int l1 = aint[2] / j;
    float f3 = (float) i / (float) j;
    float f4 = (float) Math.max(j1, Math.max(k1, l1));
    j1 = (int) ((float) j1 * f3 / f4);
    k1 = (int) ((float) k1 * f3 / f4);
    l1 = (int) ((float) l1 * f3 / f4);
    int j2 = (j1 << 8) + k1;
    j2 = (j2 << 8) + l1;
    return j2;
  }

  private int getDominantColor(TextureAtlasSprite sprite) {
    int iconWidth = sprite.getWidth();
    int iconHeight = sprite.getHeight();
    int frameCount = sprite.getFrameCount();

    if (iconWidth <= 0 || iconHeight <= 0 || frameCount <= 0) {
      return 0xFFFFFF;
    }
    TreeMap<Integer, Integer> counts = new TreeMap<>();

    for (int f = 0; f < frameCount; f++) {
      for (int v = 0; v < iconWidth; v++) {
        for (int u = 0; u < iconHeight; u++) {
          int rgba = sprite.getPixelRGBA(f, v, u);
          int alpha = rgba >> 24 & 0xFF;

          if (alpha > 0) {
            counts.merge(rgba, 1, (color, count) -> count + 1);
          }
        }
      }
    }
    int dominantColor = 0;
    int dominantSum = 0;

    for (Entry<Integer, Integer> entry : counts.entrySet()) {
      if (entry.getValue() > dominantSum) {
        dominantSum = entry.getValue();
        dominantColor = entry.getKey();
      }
    }
    Color color = new Color(dominantColor, true);
    // No idea why the r and b values are reversed, but they are
    return new Color(color.getBlue(), color.getGreen(), color.getRed()).brighter().getRGB();
  }

  public static final class BakedFoodBowlOverrideHandler extends ItemOverrideList {

    private Cache<CacheKey, IBakedModel> bakedModelCache = CacheBuilder.newBuilder()
        .maximumSize(1000).expireAfterWrite(5, TimeUnit.MINUTES).build();

    private final ModelBakery bakery;
    private final BlockModel unbaked;

    public BakedFoodBowlOverrideHandler(ModelBakery bakery, BlockModel unbaked) {
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
        BakedFoodBowlModel original = (BakedFoodBowlModel) model;
        CacheKey key = getCacheKey(stack, original);
        try {
          output = bakedModelCache.get(key, () -> getBakedModel(stack));
        } catch (ExecutionException e) {
          CulinaryConstruct.LOGGER.error("Error baking sandwich model!");
        }
      }
      return output;
    }

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

    CacheKey getCacheKey(ItemStack stack, BakedFoodBowlModel original) {
      return new CacheKey(original, stack);
    }
  }

  public static final class BakedFoodBowlModel extends BakedModelWrapper<SimpleBakedModel> {

    private final ItemOverrideList overrides;

    public BakedFoodBowlModel(SimpleBakedModel original, ItemOverrideList overrides) {
      super(original);
      this.overrides = overrides;
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
      return this.overrides;
    }
  }

  //Cache Key from Tinkers' Construct
  protected static class CacheKey {

    final IBakedModel parent;
    final CompoundNBT data;

    CacheKey(IBakedModel parent, ItemStack stack) {
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

  // Color Transformer from Mantle
  private static class ColorTransformer extends VertexTransformer {

    private final float r, g, b, a;

    public ColorTransformer(int color, VertexFormat format) {
      super(new UnpackedBakedQuad.Builder(format));

      int a = (color >> 24);

      if (a == 0) {
        a = 255;
      }
      int r = (color >> 16) & 0xFF;
      int g = (color >> 8) & 0xFF;
      int b = (color) & 0xFF;

      this.r = (float) r / 255F;
      this.g = (float) g / 255F;
      this.b = (float) b / 255F;
      this.a = (float) a / 255F;
    }

    @Override
    public void put(int element, @Nonnull float... data) {
      VertexFormatElement.Usage usage = parent.getVertexFormat().getElement(element).getUsage();

      // Transform normals and position
      if (usage == VertexFormatElement.Usage.COLOR && data.length >= 4) {
        data[0] = r;
        data[1] = g;
        data[2] = b;
        data[3] = a;
      }
      super.put(element, data);
    }

    public UnpackedBakedQuad build() {
      return ((UnpackedBakedQuad.Builder) parent).build();
    }
  }
}
