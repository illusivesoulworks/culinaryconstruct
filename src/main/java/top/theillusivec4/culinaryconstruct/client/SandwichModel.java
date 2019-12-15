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
import java.util.Arrays;
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
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class SandwichModel implements IUnbakedModel {

  @Nullable
  private final List<TextureAtlasSprite> ingredients;
  @Nullable
  private final List<Integer> layers;
  private final int size;

  public SandwichModel(@Nullable List<TextureAtlasSprite> ingredients,
      @Nullable List<Integer> layers) {
    this.ingredients = ingredients;
    this.layers = layers;
    this.size = ingredients == null ? 0 : ingredients.size() - 1;
  }

  @Nonnull
  @Override
  public Collection<ResourceLocation> getTextures(
      @Nonnull Function<ResourceLocation, IUnbakedModel> modelGetter,
      @Nonnull Set<String> missingTextureErrors) {
    ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

    if (ingredients != null && layers != null) {
      builder.add(new ResourceLocation(CulinaryConstruct.MODID, "item/bread" + size));

      for (TextureAtlasSprite sprite : ingredients) {
        builder.add(sprite.getName());
      }

      for (int layer : layers) {
        builder.add(new ResourceLocation(CulinaryConstruct.MODID, "item/layer" + layer));
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
        ImmutableList.of(new ResourceLocation(CulinaryConstruct.MODID, "item/bread" + size)))
        .bake(bakery, spriteGetter, sprite, format));
    builder.addAll(model.getQuads(null, null, random, EmptyModelData.INSTANCE));
    particleSprite = model.getParticleTexture(EmptyModelData.INSTANCE);

    if (ingredients != null && layers != null) {

      for (int i = 0; i < ingredients.size(); i++) {
        IBakedModel model1 = (new ItemLayerModel(ImmutableList
            .of(new ResourceLocation(CulinaryConstruct.MODID, "item/layer" + layers.get(i))))
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

  public static final class BakedSandwichOverrideHandler extends ItemOverrideList {

    private Cache<CacheKey, IBakedModel> bakedModelCache = CacheBuilder.newBuilder()
        .maximumSize(1000).expireAfterWrite(5, TimeUnit.MINUTES).build();

    private final ModelBakery bakery;
    private final BlockModel unbaked;

    public BakedSandwichOverrideHandler(ModelBakery bakery, BlockModel unbaked) {
      this.bakery = bakery;
      this.unbaked = unbaked;
    }

    @Nonnull
    @Override
    public IBakedModel getModelWithOverrides(@Nonnull IBakedModel model, @Nonnull ItemStack stack,
        @Nullable World worldIn, @Nullable LivingEntity entityIn) {
      CompoundNBT data = CulinaryNBTHelper.getCompoundSafe(stack);
      IBakedModel output = model;

      if (!data.isEmpty()) {
        BakedSandwichModel original = (BakedSandwichModel) model;
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
      NonNullList<ItemStack> ingredients = CulinaryNBTHelper.getIngredientsList(stack, false);

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

    CacheKey getCacheKey(ItemStack stack, BakedSandwichModel original) {
      return new CacheKey(original, stack);
    }
  }

  public static final class BakedSandwichModel extends BakedModelWrapper<SimpleBakedModel> {

    private final ItemOverrideList overrides;

    public BakedSandwichModel(SimpleBakedModel original, ItemOverrideList overrides) {
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
      this.data = CulinaryNBTHelper.getCompoundSafe(stack);
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
