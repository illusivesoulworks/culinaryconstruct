package top.theillusivec4.culinaryconstruct.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
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
import net.minecraft.core.NonNullList;
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

public final class SandwichModel implements IModelGeometry<SandwichModel> {

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
        new BakedSandwichOverrideHandler(this, owner, bakery, spriteGetter, modelTransform,
            modelLocation), modelTransform.getRotation().isIdentity(), owner.isSideLit(),
        owner.getCameraTransforms());
  }

  public BakedModel bake(List<TextureAtlasSprite> ingredients, List<Integer> layers,
      IModelConfiguration owner, ModelBakery bakery,
      Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
      ItemOverrides overrides) {
    int index = Math.max(0, ingredients.size() - 1);
    Random random = new Random();
    random.setSeed(42);
    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
    BakedModel model = ModelHelper
        .getBakedLayerModel(owner, bakery, spriteGetter, modelTransform, overrides,
            new ResourceLocation(CulinaryConstruct.MOD_ID, "item/sandwich/bread" + index));
    builder.addAll(model.getQuads(null, null, random, EmptyModelData.INSTANCE));
    TextureAtlasSprite particleSprite = model.getParticleIcon(EmptyModelData.INSTANCE);
    List<Integer> ingredientColors = new ArrayList<>();

    ingredients.forEach(sprite -> ingredientColors.add(ColorHelper.getDominantColor(sprite)));

    for (int i = 0; i < ingredients.size(); i++) {
      BakedModel ingredientModel = ModelHelper
          .getBakedLayerModel(owner, bakery, spriteGetter, modelTransform, overrides,
              new ResourceLocation(CulinaryConstruct.MOD_ID, "item/sandwich/layer" + layers.get(i)));
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

  private static final class BakedSandwichOverrideHandler extends
      CulinaryOverrideHandler<SandwichModel> {

    public BakedSandwichOverrideHandler(SandwichModel model, IModelConfiguration owner,
        ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter,
        ModelState modelTransform, ResourceLocation modelLocation) {
      super(model, owner, bakery, spriteGetter, modelTransform, modelLocation);
    }

    @Override
    protected BakedModel getBakedModel(BakedModel originalModel, ItemStack stack,
        @Nullable Level world, @Nullable LivingEntity entity) {
      ImmutableList.Builder<TextureAtlasSprite> builder = ImmutableList.builder();
      NonNullList<ItemStack> ingredients = CulinaryNBTHelper.getIngredientsList(stack);

      for (ItemStack ing : ingredients) {
        builder.add(Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(ing)
            .getParticleIcon(EmptyModelData.INSTANCE));
      }
      int size = CulinaryNBTHelper.getSize(stack);
      List<Integer> list = new ArrayList<>();

      switch (size) {
        case 2 -> list.addAll(Arrays.asList(1, 2));
        case 3 -> list.addAll(Arrays.asList(1, 2, 3));
        case 4 -> list.addAll(Arrays.asList(0, 1, 2, 3));
        case 5 -> list.addAll(Arrays.asList(0, 1, 2, 3, 4));
        default -> list.add(2);
      }
      return this.model.bake(builder.build(), list, this.owner, this.bakery, this.spriteGetter,
          this.modelTransform, ItemOverrides.EMPTY);
    }
  }
}
