/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.client.model;

import c4.culinaryconstruct.CulinaryConstruct;
import c4.culinaryconstruct.common.util.NBTHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@SideOnly(Side.CLIENT)
public final class ModelSandwich implements IModel {

    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation(CulinaryConstruct.MODID, "sandwich"), "inventory");

    public static final IModel MODEL = new ModelSandwich();

    @Nullable
    private final List<TextureAtlasSprite> ingredientSprites;
    @Nullable
    private final List<Integer> layers;
    private final int size;

    public ModelSandwich() {
        this(null, null);
    }

    public ModelSandwich(@Nullable List<TextureAtlasSprite> ingredients, @Nullable List<Integer> layers)
    {
        this.ingredientSprites = ingredients;
        this.layers = layers;
        this.size = ingredients == null ? 0 : ingredients.size() - 1;
    }

    @Nonnull
    @Override
    public Collection<ResourceLocation> getTextures()
    {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

        if (ingredientSprites != null && layers != null) {
            builder.add(new ResourceLocation(CulinaryConstruct.MODID, "items/bread" + size));
            for (TextureAtlasSprite sprite : ingredientSprites) {
                builder.add(new ResourceLocation(sprite.getIconName()));
            }
            for (int layer : layers) {
                builder.add(new ResourceLocation(CulinaryConstruct.MODID, "items/layer" + layer));
            }
        }
        return builder.build();
    }

    @Nonnull
    @Override
    public IBakedModel bake(@Nonnull IModelState state, @Nonnull VertexFormat format,
                            @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        TextureAtlasSprite particleSprite;
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        IBakedModel model = (new ItemLayerModel(ImmutableList.of(new ResourceLocation(CulinaryConstruct.MODID, "items/bread" + size))).bake(state, format, bakedTextureGetter));
        builder.addAll(model.getQuads(null, null, 0));
        particleSprite = model.getParticleTexture();

        if (ingredientSprites != null && layers != null) {
            for (int i = 0; i < ingredientSprites.size(); i++) {
                IBakedModel model1 = (new ItemLayerModel(ImmutableList.of(new ResourceLocation(CulinaryConstruct.MODID, "items/layer" + layers.get(i)))).bake(state, format, bakedTextureGetter));
                List<BakedQuad> quads = model1.getQuads(null, null, 0);
                for (BakedQuad quad : quads) {
                    ColorTransformer transformer = new ColorTransformer(getAverageColorFromSprite(ingredientSprites.get(i)), quad.getFormat());
                    quad.pipe(transformer);
                    builder.add(transformer.build());
                }
            }
        }

        return new BakedSandwichModel(builder.build(), particleSprite, format);
    }

    private int getAverageColorFromSprite(TextureAtlasSprite sprite) {
        int iconWidth = sprite.getIconWidth();
        int iconHeight = sprite.getIconHeight();
        int frameCount = sprite.getFrameCount();

        if (iconWidth <= 0 || iconHeight <= 0 || frameCount <= 0) {
            return 0xFFFFFF;
        }

        int rBucket = 0;
        int gBucket = 0;
        int bBucket = 0;
        int passes = 0;
        int[][] frameTextureData = sprite.getFrameTextureData(0);
        int[] largestMipMapTextureData = frameTextureData[0];
        for (int j : largestMipMapTextureData) {
            if (j != 0) {
                Color color = new Color(j);
                color.brighter();
                rBucket += color.getRed();
                gBucket += color.getGreen();
                bBucket += color.getBlue();
                passes++;
            }
        }

        rBucket /= passes;
        gBucket /= passes;
        bBucket /= passes;

        return new Color(rBucket, gBucket, bBucket).getRGB();
    }

    public enum LoaderSandwich implements ICustomModelLoader
    {
        INSTANCE;

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals(CulinaryConstruct.MODID)
                    && modelLocation.getResourcePath().contains("sandwich")
                    && !modelLocation.getResourcePath().contains("station");
        }

        @Nonnull
        @Override
        public IModel loadModel(@Nonnull ResourceLocation modelLocation)
        {
            return MODEL;
        }

        @Override
        public void onResourceManagerReload(@Nonnull IResourceManager resourceManager)
        {
            //NO-OP
        }
    }

    private static final class BakedSandwichItemOverrideHandler extends ItemOverrideList
    {
        private Cache<CacheKey, IBakedModel> bakedModelCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();

        public static final BakedSandwichItemOverrideHandler INSTANCE = new BakedSandwichItemOverrideHandler();

        private BakedSandwichItemOverrideHandler()
        {
            super(ImmutableList.of());
        }

        @Nonnull
        @Override
        public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
        {
            NBTTagCompound data = NBTHelper.getCompoundSafe(stack);
            IBakedModel output = originalModel;
            if (!data.hasNoTags()) {
                BakedSandwichModel original = (BakedSandwichModel) originalModel;
                CacheKey key = getCacheKey(stack, original);
                try {
                    output = bakedModelCache.get(key, () -> getBakedModel(stack, original));
                } catch (ExecutionException e) {
                    //NO-OP
                }
            }
            return output;
        }

        protected IBakedModel getBakedModel(ItemStack stack, BakedSandwichModel original) {

            ImmutableList.Builder<TextureAtlasSprite> builder = ImmutableList.builder();
            NonNullList<ItemStack> ingredients = NBTHelper.getIngredientsList(stack, false);
            for (ItemStack ing : ingredients) {
                builder.add(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(ing).getParticleTexture());
            }
            int size = NBTHelper.getSize(stack);
            List<Integer> list = new ArrayList<>();
            switch (size) {
                case 1: list.add(2); break;
                case 2: list.addAll(Arrays.asList(1, 2)); break;
                case 3: list.addAll(Arrays.asList(1, 2, 3)); break;
                case 4: list.addAll(Arrays.asList(0, 1, 2, 3)); break;
                case 5: list.addAll(Arrays.asList(0, 1, 2, 3, 4)); break;
            }
            IModel parent = new ModelSandwich(builder.build(), list);
            Function<ResourceLocation, TextureAtlasSprite> textureGetter;
            textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());

            return parent.bake(new SimpleModelState(original.transforms), original.format, textureGetter);
        }

        CacheKey getCacheKey(ItemStack stack, BakedSandwichModel original) {
            return new CacheKey(original, stack);
        }
    }

    private static final class BakedSandwichModel implements IBakedModel
    {
        private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;
        private final ImmutableList<BakedQuad> quads;
        private final TextureAtlasSprite particle;
        private final VertexFormat format;

        public BakedSandwichModel(ImmutableList<BakedQuad> quads,
                              TextureAtlasSprite particle,
                              VertexFormat format)
        {
            this.quads = quads;
            this.particle = particle;
            this.format = format;
            this.transforms = itemTransforms();
        }

        @Nonnull
        @Override
        public ItemOverrideList getOverrides() {
            return BakedSandwichItemOverrideHandler.INSTANCE;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull ItemCameraTransforms.TransformType cameraTransformType) {
            return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType);
        }

        private static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> itemTransforms() {
            TRSRTransformation thirdperson = get(0, 3, 1, 0, 0, 0, 0.55f);
            TRSRTransformation firstperson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
            ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
            builder.put(ItemCameraTransforms.TransformType.GROUND,                  get(0, 2, 0, 0, 0, 0, 0.5f));
            builder.put(ItemCameraTransforms.TransformType.HEAD,                    get(0, 13, 7, 0, 180, 0, 1));
            builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
            builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,  leftify(thirdperson));
            builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstperson);
            builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,  leftify(firstperson));
            return builder.build();
        }

        private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
            return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
                    new Vector3f(tx / 16, ty / 16, tz / 16),
                    TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null));
        }

        private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1),null);

        private static TRSRTransformation leftify(TRSRTransformation transform) {
            return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
            if (side == null)
                return quads;
            return ImmutableList.of();
        }

        @Override
        public boolean isAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleTexture() {
            return particle;
        }
    }

    //Cache Key from Tinkers' Construct
    protected static class CacheKey {

        final IBakedModel parent;
        final NBTTagCompound data;

        CacheKey(IBakedModel parent, ItemStack stack) {
            this.parent = parent;
            this.data = NBTHelper.getCompoundSafe(stack);
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }

            CacheKey cacheKey = (CacheKey) o;

            if(parent != null ? parent != cacheKey.parent : cacheKey.parent != null) {
                return false;
            }
            return data != null ? data.equals(cacheKey.data) : cacheKey.data == null;

        }

        @Override
        public int hashCode() {
            int result = parent != null ? parent.hashCode() : 0;
            result = 31 * result + (data != null ? data.hashCode() : 0);
            return result;
        }
    }

    //Color Transformer from Mantle
    private static class ColorTransformer extends VertexTransformer {

        private final float r,g,b,a;

        public ColorTransformer(int color, VertexFormat format) {
            super(new UnpackedBakedQuad.Builder(format));

            int a = (color >> 24);
            if(a == 0) {
                a = 255;
            }
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color) & 0xFF;

            this.r = (float)r/255f;
            this.g = (float)g/255f;
            this.b = (float)b/255f;
            this.a = (float)a/255f;
        }

        @Override
        public void put(int element, float... data) {
            VertexFormatElement.EnumUsage usage = parent.getVertexFormat().getElement(element).getUsage();

            // transform normals and position
            if(usage == VertexFormatElement.EnumUsage.COLOR && data.length >= 4) {
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
