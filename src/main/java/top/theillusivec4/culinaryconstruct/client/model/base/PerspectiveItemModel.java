package top.theillusivec4.culinaryconstruct.client.model.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.math.Transformation;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;

@SuppressWarnings("deprecation")
public class PerspectiveItemModel extends BakedItemModel {

  private ItemTransforms cameraTransforms;

  public PerspectiveItemModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle,
      ImmutableMap<TransformType, Transformation> transforms, ItemOverrides overrides,
      boolean untransformed, boolean isSideLit, ItemTransforms cameraTransforms) {
    super(quads, particle, transforms, overrides, untransformed, isSideLit);
    this.cameraTransforms = cameraTransforms;
  }

  @Nonnull
  @Override
  public BakedModel handlePerspective(@Nonnull ItemTransforms.TransformType type,
      @Nonnull PoseStack mat) {

    if (cameraTransforms != null) {
      return net.minecraftforge.client.ForgeHooksClient.handlePerspective(this, type, mat);
    }
    return PerspectiveMapWrapper.handlePerspective(this, transforms, type, mat);
  }

  @Override
  public boolean doesHandlePerspectives() {
    return true;
  }

  @Deprecated
  @Nonnull
  @Override
  public ItemTransforms getTransforms() {
    return cameraTransforms;
  }
}
