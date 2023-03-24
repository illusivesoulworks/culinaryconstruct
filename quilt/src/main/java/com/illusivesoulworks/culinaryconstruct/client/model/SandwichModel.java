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

import com.illusivesoulworks.culinaryconstruct.CulinaryConstructConstants;
import com.illusivesoulworks.culinaryconstruct.common.util.CulinaryNBT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SandwichModel extends CachedMeshModel {

  private final MeshBuilder meshBuilder;

  public SandwichModel(MeshBuilder meshBuilder) {
    this.meshBuilder = meshBuilder;
  }

  @Override
  public boolean isVanillaAdapter() {
    return false;
  }

  @Override
  public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos,
                             Supplier<RandomSource> randomSupplier, RenderContext context) {

  }

  @Override
  public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier,
                            RenderContext context) {
    NonNullList<ItemStack> ingredients = CulinaryNBT.getIngredientsList(stack);
    int index = Math.max(0, ingredients.size() - 1);
    ModelManager modelManager = Minecraft.getInstance().getModelManager();
    BakedModel base = BakedModelManagerHelper.getModel(modelManager, new ModelResourceLocation(
        new ResourceLocation(CulinaryConstructConstants.MOD_ID, "sandwich/bread" + index),
        "inventory"));
    context.bakedModelConsumer().accept(base);
    Mesh mesh = getOrBuildMesh(stack, ingredients, modelManager, randomSupplier);

    if (mesh != null) {
      context.meshConsumer().accept(mesh);
    }
  }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction,
                                  @Nonnull RandomSource random) {
    return Collections.emptyList();
  }

  @Override
  public boolean useAmbientOcclusion() {
    return true;
  }

  @Override
  public boolean isGui3d() {
    return false;
  }

  @Override
  public boolean usesBlockLight() {
    return false;
  }

  @Override
  public boolean isCustomRenderer() {
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleIcon() {
    return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
        .apply(new ResourceLocation(CulinaryConstructConstants.MOD_ID, "item/sandwich/bread0"));
  }

  @Override
  public ItemOverrides getOverrides() {
    return ItemOverrides.EMPTY;
  }

  @Override
  protected Mesh buildMesh(ItemStack stack, List<ItemStack> ingredients, ModelManager modelManager,
                           Supplier<RandomSource> randomSupplier) {
    QuadEmitter quadEmitter = this.meshBuilder.getEmitter();
    List<Integer> ingredientColors = new ArrayList<>();
    ingredients.forEach(stack1 -> {
      TextureAtlasSprite sprite =
          Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(stack1)
              .getParticleIcon();
      ingredientColors.add(ColorMixer.getDominantColor(sprite));
    });
    int size = CulinaryNBT.getSize(stack);
    List<Integer> layers = new ArrayList<>();

    switch (size) {
      case 2 -> layers.addAll(Arrays.asList(1, 2));
      case 3 -> layers.addAll(Arrays.asList(1, 2, 3));
      case 4 -> layers.addAll(Arrays.asList(0, 1, 2, 3));
      case 5 -> layers.addAll(Arrays.asList(0, 1, 2, 3, 4));
      default -> layers.add(2);
    }
    for (int i = 0; i < ingredients.size(); i++) {
      int color = ingredientColors.get(i);
      BakedModel layer = BakedModelManagerHelper.getModel(modelManager, new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID,
              "sandwich/layer" + layers.get(i)), "inventory"));

      if (layer != null) {
        layer.getQuads(null, null, randomSupplier.get()).forEach(q -> {
          quadEmitter.fromVanilla(q.getVertices(), 0, false);
          quadEmitter.spriteColor(0, color, color, color, color);
          quadEmitter.emit();
        });
      }
    }
    return this.meshBuilder.build();
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return Collections.emptyList();
  }

  @Override
  public void resolveParents(@Nonnull Function<ResourceLocation, UnbakedModel> function) {

  }

  @Nullable
  @Override
  public BakedModel bake(@Nonnull ModelBaker baker,
                         @Nonnull Function<Material, TextureAtlasSprite> spriteGetter,
                         @Nonnull ModelState state, @Nonnull ResourceLocation location) {
    return this;
  }
}
