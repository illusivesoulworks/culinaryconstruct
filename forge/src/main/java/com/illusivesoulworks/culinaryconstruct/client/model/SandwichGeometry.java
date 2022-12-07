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

import com.google.common.collect.ImmutableList;
import com.illusivesoulworks.culinaryconstruct.CulinaryConstructConstants;
import com.illusivesoulworks.culinaryconstruct.client.model.color.ColorMixer;
import com.illusivesoulworks.culinaryconstruct.client.model.color.ColoredQuadTransformer;
import com.illusivesoulworks.culinaryconstruct.common.util.CulinaryNBT;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;

public final class SandwichGeometry implements IUnbakedGeometry<SandwichGeometry> {

  private final List<TextureAtlasSprite> ingredients;
  private final List<Integer> layers;

  public SandwichGeometry() {
    this(Collections.emptyList(), Collections.emptyList());
  }

  private SandwichGeometry(List<TextureAtlasSprite> ingredients, List<Integer> layers) {
    this.ingredients = ingredients;
    this.layers = layers;
  }

  private SandwichGeometry withStack(ItemStack stack) {
    ImmutableList.Builder<TextureAtlasSprite> builder = ImmutableList.builder();
    NonNullList<ItemStack> ingredients = CulinaryNBT.getIngredientsList(stack);

    for (ItemStack ing : ingredients) {
      builder.add(Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(ing)
          .getParticleIcon(ModelData.EMPTY));
    }
    int size = CulinaryNBT.getSize(stack);
    List<Integer> list = new ArrayList<>();

    switch (size) {
      case 2 -> list.addAll(Arrays.asList(1, 2));
      case 3 -> list.addAll(Arrays.asList(1, 2, 3));
      case 4 -> list.addAll(Arrays.asList(0, 1, 2, 3));
      case 5 -> list.addAll(Arrays.asList(0, 1, 2, 3, 4));
      default -> list.add(2);
    }
    return new SandwichGeometry(builder.build(), list);
  }

  public static RenderTypeGroup getLayerRenderTypes() {
    return new RenderTypeGroup(RenderType.translucent(), ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
  }

  @Override
  public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery,
                         Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState,
                         ItemOverrides overrides, ResourceLocation modelLocation) {
    int index = Math.max(0, this.ingredients.size() - 1);
    TextureAtlasSprite baseSprite = spriteGetter.apply(context.getMaterial("bread" + index));
    StandaloneGeometryBakingContext itemContext =
        StandaloneGeometryBakingContext.builder(context).withGui3d(false).withUseBlockLight(false)
            .build(modelLocation);
    CompositeModel.Baked.Builder modelBuilder =
        CompositeModel.Baked.builder(itemContext, baseSprite,
            new BakedSandwichOverrides(this, context, bakery, spriteGetter, modelState,
                modelLocation), context.getTransforms());
    List<Integer> ingredientColors = new ArrayList<>();
    this.ingredients.forEach(
        sprite -> ingredientColors.add(ColorMixer.getDominantColor(sprite)));
    List<BlockElement> unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite);
    List<BakedQuad> quads =
        UnbakedGeometryHelper.bakeElements(unbaked, material -> baseSprite, modelState,
            modelLocation);
    RenderTypeGroup normalRenderTypes = getLayerRenderTypes();
    modelBuilder.addQuads(normalRenderTypes, quads);
    ColoredQuadTransformer coloredQuadTransformer = new ColoredQuadTransformer();

    for (int i = 0; i < this.ingredients.size(); i++) {
      TextureAtlasSprite sprite =
          spriteGetter.apply(context.getMaterial("layer" + this.layers.get(i)));
      unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, sprite);
      quads = UnbakedGeometryHelper.bakeElements(unbaked, material -> sprite, modelState,
          modelLocation);
      coloredQuadTransformer.color(quads, ingredientColors.get(i));
      modelBuilder.addQuads(normalRenderTypes, quads);
    }
    modelBuilder.setParticle(baseSprite);
    return modelBuilder.build();
  }

  @Override
  public Collection<Material> getMaterials(IGeometryBakingContext context,
                                           Function<ResourceLocation, UnbakedModel> modelGetter,
                                           Set<Pair<String, String>> missingTextureErrors) {
    Set<Material> textures = new HashSet<>();

    for (int i = 0; i < 5; i++) {
      textures.add(context.getMaterial("layer" + i));
      textures.add(context.getMaterial("bread" + i));
    }
    return textures;
  }

  private static final class BakedSandwichOverrides extends
      CulinaryOverrides<SandwichGeometry> {

    public BakedSandwichOverrides(SandwichGeometry model, IGeometryBakingContext context,
                                  ModelBakery bakery,
                                  Function<Material, TextureAtlasSprite> spriteGetter,
                                  ModelState modelState, ResourceLocation modelLocation) {
      super(model, context, bakery, spriteGetter, modelState, modelLocation);
    }

    @Override
    protected BakedModel getBakedModel(BakedModel originalModel, ItemStack stack,
                                       @Nullable Level world, @Nullable LivingEntity entity) {
      SandwichGeometry unbaked = this.model.withStack(stack);
      return unbaked.bake(this.context, this.bakery, this.spriteGetter, this.modelState,
          this, new ResourceLocation(CulinaryConstructConstants.MOD_ID,
              CulinaryConstructConstants.SANDWICH_ID));
    }
  }
}
