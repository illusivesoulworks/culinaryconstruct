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
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
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

public class FoodBowlModel extends CachedMeshModel
    implements UnbakedModel, BakedModel, FabricBakedModel {

  private final MeshBuilder meshBuilder;

  public FoodBowlModel(MeshBuilder meshBuilder) {
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
    NonNullList<ItemStack> solids = CulinaryNBT.getSolids(stack);
    ModelManager modelManager = Minecraft.getInstance().getModelManager();
    BakedModel base = modelManager.getModel(
        new ModelResourceLocation(new ResourceLocation("minecraft:bowl"), "inventory"));
    context.fallbackConsumer().accept(base);
    Mesh mesh = getOrBuildMesh(stack, solids, modelManager, randomSupplier);

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
        .apply(new ResourceLocation("minecraft:item/bowl"));
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
    List<Integer> liquids = CulinaryNBT.getLiquids(stack);

    if (liquids != null) {
      List<Integer> opaqueColors = new ArrayList<>();
      liquids.forEach(color -> {
        if (color != null) {
          opaqueColors.add(color);
        }
      });
      boolean isOpaque = !opaqueColors.isEmpty();
      int liquidColor = isOpaque ? ColorMixer.getMixedColor(liquids)
          : ColorMixer.getMixedColor(ingredientColors);
      BakedModel liquid = BakedModelManagerHelper.getModel(modelManager, new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "food_bowl/liquid_base"),
          "inventory"));

      if (liquid != null) {
        liquid.getQuads(null, null, randomSupplier.get()).forEach(q -> {
          quadEmitter.fromVanilla(q.getVertices(), 0, false);
          quadEmitter.spriteColor(0, liquidColor, liquidColor, liquidColor, liquidColor);
          quadEmitter.emit();
        });
      }

      if (ingredients.size() >= 3) {
        BakedModel overflow = BakedModelManagerHelper.getModel(modelManager,
            new ModelResourceLocation(new ResourceLocation(CulinaryConstructConstants.MOD_ID,
                "food_bowl/liquid_overflow"), "inventory"));

        if (overflow != null) {
          overflow.getQuads(null, null, randomSupplier.get()).forEach(q -> {
            quadEmitter.fromVanilla(q.getVertices(), 0, false);
            quadEmitter.spriteColor(0, liquidColor, liquidColor, liquidColor, liquidColor);
            quadEmitter.emit();
          });
        }
      }
    }

    for (int i = 0; i < ingredients.size(); i++) {
      int color = ingredientColors.get(i);
      BakedModel layer = BakedModelManagerHelper.getModel(modelManager, new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "food_bowl/layer" + i),
          "inventory"));

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
  public Collection<Material> getMaterials(
      @Nonnull Function<ResourceLocation, UnbakedModel> modelGetter,
      @Nonnull Set<Pair<String, String>> missingTextureErrors) {
    return Collections.emptyList();
  }

  @Nullable
  @Override
  public BakedModel bake(@Nonnull ModelBakery modelBakery,
                         @Nonnull Function<Material, TextureAtlasSprite> spriteGetter,
                         @Nonnull ModelState transform, @Nonnull ResourceLocation location) {
    return this;
  }
}
