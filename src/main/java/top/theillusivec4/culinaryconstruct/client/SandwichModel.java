///*
// * Copyright (c) 2018-2019 C4
// *
// * This file is part of Culinary Construct, a mod made for Minecraft.
// *
// * Culinary Construct is free software: you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as published
// * by the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * Culinary Construct is distributed in the hope that it will be useful, but
// * WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
// * GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with Culinary Construct.  If not, see <https://www.gnu.org/licenses/>.
// */
//
//package top.theillusivec4.culinaryconstruct.client;
//
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.ImmutableSet;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//import java.util.function.Function;
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.model.BakedQuad;
//import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.client.renderer.model.IUnbakedModel;
//import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
//import net.minecraft.client.renderer.model.ItemOverrideList;
//import net.minecraft.client.renderer.model.ModelBakery;
//import net.minecraft.client.renderer.texture.ISprite;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.renderer.vertex.VertexFormat;
//import net.minecraft.client.renderer.vertex.VertexFormatElement;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.fluid.Fluid;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.world.World;
//import net.minecraftforge.client.model.BakedItemModel;
//import net.minecraftforge.client.model.ItemLayerModel;
//import net.minecraftforge.client.model.ModelDynBucket;
//import net.minecraftforge.client.model.ModelDynBucket.BakedDynBucket;
//import net.minecraftforge.client.model.SimpleModelState;
//import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
//import net.minecraftforge.client.model.pipeline.VertexTransformer;
//import net.minecraftforge.common.model.TRSRTransformation;
//import net.minecraftforge.fluids.FluidUtil;
//import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
//
//public class SandwichModel implements IUnbakedModel {
//
//  @Nullable
//  private final List<TextureAtlasSprite> ingredients;
//  @Nullable
//  private final List<Integer> layers;
//  private final int size;
//
//  public SandwichModel() {
//    this(null, null);
//  }
//
//  public SandwichModel(@Nullable List<TextureAtlasSprite> ingredients,
//      @Nullable List<Integer> layers) {
//    this.ingredients = ingredients;
//    this.layers = layers;
//    this.size = ingredients == null ? 0 : ingredients.size() - 1;
//  }
//
//  @Nonnull
//  @Override
//  public Collection<ResourceLocation> getTextures(
//      @Nonnull Function<ResourceLocation, IUnbakedModel> modelGetter,
//      @Nonnull Set<String> missingTextureErrors) {
//    ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
//
//    if (ingredients != null && layers != null) {
//      builder.add(new ResourceLocation(CulinaryConstruct.MODID, "item/bread" + size));
//
//      for (TextureAtlasSprite sprite : ingredients) {
//        builder.add(sprite.getName());
//      }
//
//      for (int layer : layers) {
//        builder.add(new ResourceLocation(CulinaryConstruct.MODID, "item/layer" + layer));
//      }
//    }
//    return builder.build();
//  }
//
//  @Nonnull
//  @Override
//  public IBakedModel bake(@Nonnull ModelBakery bakery,
//      @Nonnull Function<ResourceLocation, TextureAtlasSprite> spriteGetter, @Nonnull ISprite sprite,
//      @Nonnull VertexFormat format) {
//    TextureAtlasSprite particleSprite;
//    Random random = new Random();
//    random.setSeed(42);
//    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
//    IBakedModel model = (new ItemLayerModel(
//        ImmutableList.of(new ResourceLocation(CulinaryConstruct.MODID, "item/bread" + size)))
//        .bake(bakery, spriteGetter, sprite, format));
//    builder.addAll(model.getQuads(null, null, random, null));
//    particleSprite = model.getParticleTexture();
//
//    if (ingredients != null && layers != null) {
//
//      for (int i = 0; i < ingredients.size(); i++) {
//        IBakedModel model1 = (new ItemLayerModel(ImmutableList
//            .of(new ResourceLocation(CulinaryConstruct.MODID, "item/layer" + layers.get(i))))
//            .bake(bakery, spriteGetter, sprite, format));
//        List<BakedQuad> quads = model1.getQuads(null, null, random, null);
//
//        for (BakedQuad quad : quads) {
//          ColorTransformer transformer = new ColorTransformer(
//              getAverageColorFromSprite(ingredientSprites.get(i)), quad.getFormat());
//          quad.pipe(transformer);
//          builder.add(transformer.build());
//        }
//      }
//    }
//
//    return new BakedSandwichModel(builder.build(), particleSprite, format);
//  }
//
//  private static final class BakedSandwichOverrideHandler extends ItemOverrideList {
//
//    private final ModelBakery bakery;
//
//    private BakedSandwichOverrideHandler(ModelBakery bakery) {
//      this.bakery = bakery;
//    }
//
//    @Override
//    public IBakedModel getModelWithOverrides(@Nonnull IBakedModel originalModel,
//        @Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
//      return FluidUtil.getFluidContained(stack).map(fluidStack -> {
//        BakedSandwichModel model = (BakedSandwichModel) originalModel;
//
//        Fluid fluid = fluidStack.getFluid();
//        String name = fluid.getRegistryName().toString();
//
//        if (!model.cache.containsKey(name)) {
//          IUnbakedModel parent = model.parent.process(ImmutableMap.of("fluid", name));
//          Function<ResourceLocation, TextureAtlasSprite> textureGetter;
//          textureGetter = location -> Minecraft.getInstance().getTextureMap()
//              .getAtlasSprite(location.toString());
//
//          IBakedModel bakedModel = parent
//              .bake(bakery, textureGetter, new SimpleModelState(model.transforms), model.format);
//          model.cache.put(name, bakedModel);
//          return bakedModel;
//        }
//
//        return model.cache.get(name);
//      })
//          // not a fluid item apparently
//          .orElse(originalModel); // empty bucket
//    }
//  }
//
//  private static final class BakedSandwichModel extends BakedItemModel {
//
//    private final ModelDynBucket parent;
//    private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
//    private final VertexFormat format;
//
//    BakedSandwichModel(ModelBakery bakery, ModelDynBucket parent, ImmutableList<BakedQuad> quads,
//        TextureAtlasSprite particle, VertexFormat format,
//        ImmutableMap<TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache,
//        boolean untransformed) {
//      super(quads, particle, transforms, new BakedSandwichOverrideHandler(bakery), untransformed);
//      this.format = format;
//      this.parent = parent;
//      this.cache = cache;
//    }
//  }
//
//  // Color Transformer from Mantle
//  private static class ColorTransformer extends VertexTransformer {
//
//    private final float r, g, b, a;
//
//    public ColorTransformer(int color, VertexFormat format) {
//      super(new UnpackedBakedQuad.Builder(format));
//
//      int a = (color >> 24);
//
//      if (a == 0) {
//        a = 255;
//      }
//      int r = (color >> 16) & 0xFF;
//      int g = (color >> 8) & 0xFF;
//      int b = (color) & 0xFF;
//
//      this.r = (float) r / 255F;
//      this.g = (float) g / 255F;
//      this.b = (float) b / 255F;
//      this.a = (float) a / 255F;
//    }
//
//    @Override
//    public void put(int element, @Nonnull float... data) {
//      VertexFormatElement.Usage usage = parent.getVertexFormat().getElement(element).getUsage();
//
//      // Transform normals and position
//      if (usage == VertexFormatElement.Usage.COLOR && data.length >= 4) {
//        data[0] = r;
//        data[1] = g;
//        data[2] = b;
//        data[3] = a;
//      }
//      super.put(element, data);
//    }
//
//    public UnpackedBakedQuad build() {
//      return ((UnpackedBakedQuad.Builder) parent).build();
//    }
//  }
//}
