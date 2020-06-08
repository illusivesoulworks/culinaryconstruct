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
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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

@SuppressWarnings("deprecation")
public final class FoodBowlModel implements IModelGeometry<FoodBowlModel> {

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery,
      Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform,
      ItemOverrideList overrides, ResourceLocation modelLocation) {
    IBakedModel model = ModelHelper
        .getBakedLayerModel(owner, bakery, spriteGetter, modelTransform, overrides,
            new ResourceLocation(CulinaryConstruct.MODID, "item/sandwich/bread0"));
    TextureAtlasSprite particleSprite = model.getParticleTexture(EmptyModelData.INSTANCE);
    return new PerspectiveItemModel(ImmutableList.of(), particleSprite,
        PerspectiveMapWrapper.getTransforms(modelTransform),
        new BakedFoodBowlOverrideHandler(this, owner, bakery, spriteGetter, modelTransform,
            modelLocation), modelTransform.getRotation().isIdentity(), owner.isSideLit(),
        owner.getCameraTransforms());
  }

  public IBakedModel bake(List<TextureAtlasSprite> ingredients, List<Integer> layers,
      @Nullable List<Integer> liquids, IModelConfiguration owner, ModelBakery bakery,
      Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform,
      ItemOverrideList overrides) {
    Random random = new Random();
    random.setSeed(42);
    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
    IBakedModel model = ModelHelper
        .getBakedLayerModel(owner, bakery, spriteGetter, modelTransform, overrides,
            new ResourceLocation("minecraft:item/bowl"));
    builder.addAll(model.getQuads(null, null, random, EmptyModelData.INSTANCE));
    TextureAtlasSprite particleSprite = model.getParticleTexture(EmptyModelData.INSTANCE);
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
      IBakedModel liquidBase = ModelHelper
          .getBakedLayerModel(owner, bakery, spriteGetter, modelTransform, overrides,
              new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/liquid_base"));
      ColorHelper.colorQuads(liquidBase, liquidColor, random, builder);

      if (ingredients.size() >= 3) {
        IBakedModel liquidOverflow = ModelHelper
            .getBakedLayerModel(owner, bakery, spriteGetter, modelTransform, overrides,
                new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/liquid_overflow"));
        ColorHelper.colorQuads(liquidOverflow, liquidColor, random, builder);
      }
    }

    for (int i = 0; i < ingredients.size(); i++) {
      IBakedModel ingredientModel = ModelHelper
          .getBakedLayerModel(owner, bakery, spriteGetter, modelTransform, overrides,
              new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/layer" + layers.get(i)));
      ColorHelper.colorQuads(ingredientModel, ingredientColors.get(i), random, builder);
    }
    return new PerspectiveItemModel(builder.build(), particleSprite,
        PerspectiveMapWrapper.getTransforms(modelTransform), overrides,
        modelTransform.getRotation().isIdentity(), owner.isSideLit(), owner.getCameraTransforms());
  }

  @Override
  public Collection<Material> getTextures(IModelConfiguration owner,
      Function<ResourceLocation, IUnbakedModel> modelGetter,
      Set<Pair<String, String>> missingTextureErrors) {
    return Collections.emptyList();
  }

  private static final class BakedFoodBowlOverrideHandler extends
      CulinaryOverrideHandler<FoodBowlModel> {

    public BakedFoodBowlOverrideHandler(FoodBowlModel model, IModelConfiguration owner,
        ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter,
        IModelTransform modelTransform, ResourceLocation modelLocation) {
      super(model, owner, bakery, spriteGetter, modelTransform, modelLocation);
    }

    @Override
    protected IBakedModel getBakedModel(IBakedModel originalModel, ItemStack stack,
        @Nullable World world, @Nullable LivingEntity entity) {
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
      return this.model
          .bake(builder.build(), list, liquids, this.owner, this.bakery, this.spriteGetter,
              this.modelTransform, ItemOverrideList.EMPTY);
    }
  }
}

