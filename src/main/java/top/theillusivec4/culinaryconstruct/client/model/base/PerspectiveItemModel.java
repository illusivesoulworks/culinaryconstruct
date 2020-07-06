package top.theillusivec4.culinaryconstruct.client.model.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;

@SuppressWarnings("deprecation")
public class PerspectiveItemModel extends BakedItemModel {

  private ItemCameraTransforms cameraTransforms;

  public PerspectiveItemModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle,
      ImmutableMap<TransformType, TransformationMatrix> transforms, ItemOverrideList overrides,
      boolean untransformed, boolean isSideLit, ItemCameraTransforms cameraTransforms) {
    super(quads, particle, transforms, overrides, untransformed, isSideLit);
    this.cameraTransforms = cameraTransforms;
  }

  @Nonnull
  @Override
  public IBakedModel handlePerspective(@Nonnull ItemCameraTransforms.TransformType type,
      @Nonnull MatrixStack mat) {

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
  public ItemCameraTransforms getItemCameraTransforms() {
    return cameraTransforms;
  }
}
