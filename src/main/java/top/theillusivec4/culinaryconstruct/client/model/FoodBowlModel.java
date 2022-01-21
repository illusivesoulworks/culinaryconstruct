package top.theillusivec4.culinaryconstruct.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.client.model.base.CulinaryOverrideHandler;
import top.theillusivec4.culinaryconstruct.client.model.base.PerspectiveItemModel;
import top.theillusivec4.culinaryconstruct.client.model.utils.ColorHelper;
import top.theillusivec4.culinaryconstruct.client.model.utils.ModelHelper;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public final class FoodBowlModel implements IModelGeometry<FoodBowlModel> {

  @Override
  public BakedModel bake(IModelConfiguration owner, ModelBakery bakery,
      Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
      ItemOverrides overrides, ResourceLocation modelLocation) {
    BakedModel model = ModelHelper
        .getBakedLayerModel(owner, bakery, spriteGetter, modelTransform, overrides,
            new ResourceLocation(CulinaryConstruct.MOD_ID, "item/sandwich/bread0"));
    TextureAtlasSprite particleSprite = model.getParticleIcon(EmptyModelData.INSTANCE);
    return new PerspectiveItemModel(ImmutableList.of(), particleSprite,
        PerspectiveMapWrapper.getTransforms(modelTransform),
        new BakedFoodBowlOverrideHandler(this, owner, bakery, spriteGetter, modelTransform,
            modelLocation), modelTransform.getRotation().isIdentity(), owner.isSideLit(),
        owner.getCameraTransforms());
  }

  public BakedModel bake(List<TextureAtlasSprite> ingredients, List<Integer> layers,
      @Nullable List<Integer> liquids, IModelConfiguration owner, ModelBakery bakery,
      Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
      ItemOverrides overrides) {
    Random random = new Random();
    random.setSeed(42);
    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
    BakedModel model = ModelHelper
        .getBakedLayerModel(owner, bakery, spriteGetter, modelTransform, overrides,
            new ResourceLocation("minecraft:item/bowl"));
    builder.addAll(model.getQuads(null, null, random, EmptyModelData.INSTANCE));
    TextureAtlasSprite particleSprite = model.getParticleIcon(EmptyModelData.INSTANCE);
    List<Integer> ingredientColors = new ArrayList<>();

    ingredients.forEach(sprite -> ingredientColors.add(ColorHelper.getDominantColor(sprite)));

    if (liquids != null) {
      List<Integer> opaqueColors = new ArrayList<>();
      liquids.forEach(color -> {
        if (color != null) {
          opaqueColors.add(color);
        }
      });
      boolean isOpaque = !opaqueColors.isEmpty();
      int liquidColor = isOpaque ? ColorHelper.getMixedColor(liquids)
          : ColorHelper.getMixedColor(ingredientColors);
      BakedModel liquidBase = ModelHelper
          .getBakedLayerModel(owner, bakery, spriteGetter, modelTransform, overrides,
              new ResourceLocation(CulinaryConstruct.MOD_ID, "item/bowl/liquid_base"));
      ColorHelper.colorQuads(liquidBase, liquidColor, random, builder);

      if (ingredients.size() >= 3) {
        BakedModel liquidOverflow = ModelHelper
            .getBakedLayerModel(owner, bakery, spriteGetter, modelTransform, overrides,
                new ResourceLocation(CulinaryConstruct.MOD_ID, "item/bowl/liquid_overflow"));
        ColorHelper.colorQuads(liquidOverflow, liquidColor, random, builder);
      }
    }

    for (int i = 0; i < ingredients.size(); i++) {
      BakedModel ingredientModel = ModelHelper
          .getBakedLayerModel(owner, bakery, spriteGetter, modelTransform, overrides,
              new ResourceLocation(CulinaryConstruct.MOD_ID, "item/bowl/layer" + layers.get(i)));
      ColorHelper.colorQuads(ingredientModel, ingredientColors.get(i), random, builder);
    }
    return new PerspectiveItemModel(builder.build(), particleSprite,
        PerspectiveMapWrapper.getTransforms(modelTransform), overrides,
        modelTransform.getRotation().isIdentity(), owner.isSideLit(), owner.getCameraTransforms());
  }

  @Override
  public Collection<Material> getTextures(IModelConfiguration owner,
      Function<ResourceLocation, UnbakedModel> modelGetter,
      Set<Pair<String, String>> missingTextureErrors) {
    return Collections.emptyList();
  }

  private static final class BakedFoodBowlOverrideHandler extends
      CulinaryOverrideHandler<FoodBowlModel> {

    public BakedFoodBowlOverrideHandler(FoodBowlModel model, IModelConfiguration owner,
        ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter,
        ModelState modelTransform, ResourceLocation modelLocation) {
      super(model, owner, bakery, spriteGetter, modelTransform, modelLocation);
    }

    @Override
    protected BakedModel getBakedModel(BakedModel originalModel, ItemStack stack,
        @Nullable Level world, @Nullable LivingEntity entity) {
      ImmutableList.Builder<TextureAtlasSprite> builder = ImmutableList.builder();
      List<ItemStack> solids = CulinaryNBTHelper.getSolids(stack);

      for (ItemStack ing : solids) {
        builder.add(Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(ing)
            .getParticleIcon(EmptyModelData.INSTANCE));
      }
      List<Integer> list = new ArrayList<>();

      for (int i = 0; i < solids.size(); i++) {
        list.add(i);
      }
      List<Integer> liquids = CulinaryNBTHelper.getLiquids(stack);
      return this.model
          .bake(builder.build(), list, liquids, this.owner, this.bakery, this.spriteGetter,
              this.modelTransform, ItemOverrides.EMPTY);
    }
  }
}

