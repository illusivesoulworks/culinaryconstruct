/*
 * Copyright (c) 2018-2020 C4
 *
 * This file is part of Culinary Construct, a mod made for Minecraft.
 *
 * Culinary Construct is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Culinary Construct is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Culinary Construct.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.culinaryconstruct.client.model.utils;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.VertexTransformer;

public class ColorHelper {

  public static void colorQuads(IBakedModel bakedModel, int color, Random random,
      ImmutableList.Builder<BakedQuad> builder) {
    List<BakedQuad> quads = bakedModel.getQuads(null, null, random, EmptyModelData.INSTANCE);

    for (BakedQuad quad : quads) {
      ColorTransformer transformer = new ColorTransformer(color, quad);
      quad.pipe(transformer);
      builder.add(transformer.build());
    }
  }

  public static int getDominantColor(TextureAtlasSprite sprite) {
    int iconWidth = sprite.getWidth();
    int iconHeight = sprite.getHeight();
    int frameCount = sprite.getFrameCount();

    if (iconWidth <= 0 || iconHeight <= 0 || frameCount <= 0) {
      return 0xFFFFFF;
    }
    TreeMap<Integer, Integer> counts = new TreeMap<>();

    for (int f = 0; f < frameCount; f++) {
      for (int v = 0; v < iconWidth; v++) {
        for (int u = 0; u < iconHeight; u++) {
          int rgba = sprite.getPixelRGBA(f, v, u);
          int alpha = rgba >> 24 & 0xFF;

          if (alpha > 0) {
            counts.merge(rgba, 1, (color, count) -> count + 1);
          }
        }
      }
    }
    int dominantColor = 0;
    int dominantSum = 0;

    for (Entry<Integer, Integer> entry : counts.entrySet()) {
      if (entry.getValue() > dominantSum) {
        dominantSum = entry.getValue();
        dominantColor = entry.getKey();
      }
    }
    Color color = new Color(dominantColor, true);
    // No idea why the r and b values are reversed, but they are
    return new Color(color.getBlue(), color.getGreen(), color.getRed()).brighter().getRGB();
  }

  public static int getMixedColor(List<Integer> colors) {
    int[] aint = new int[3];
    int i = 0;
    int j = 0;

    for (Integer color : colors) {
      float f = (float) (color >> 16 & 255) / 255.0F;
      float f1 = (float) (color >> 8 & 255) / 255.0F;
      float f2 = (float) (color & 255) / 255.0F;
      i = (int) ((float) i + Math.max(f, Math.max(f1, f2)) * 255.0F);
      aint[0] = (int) ((float) aint[0] + f * 255.0F);
      aint[1] = (int) ((float) aint[1] + f1 * 255.0F);
      aint[2] = (int) ((float) aint[2] + f2 * 255.0F);
      ++j;
    }
    int j1 = aint[0] / j;
    int k1 = aint[1] / j;
    int l1 = aint[2] / j;
    float f3 = (float) i / (float) j;
    float f4 = (float) Math.max(j1, Math.max(k1, l1));
    j1 = (int) ((float) j1 * f3 / f4);
    k1 = (int) ((float) k1 * f3 / f4);
    l1 = (int) ((float) l1 * f3 / f4);
    int j2 = (j1 << 8) + k1;
    j2 = (j2 << 8) + l1;
    return j2;
  }

  // Color Transformer from Mantle
  private static class ColorTransformer extends VertexTransformer {

    private final float r, g, b, a;

    public ColorTransformer(int color, BakedQuad quad) {
      super(new BakedQuadBuilder(quad.func_187508_a()));

      int a = (color >> 24);

      if (a == 0) {
        a = 255;
      }
      int r = (color >> 16) & 0xFF;
      int g = (color >> 8) & 0xFF;
      int b = (color) & 0xFF;

      this.r = (float) r / 255F;
      this.g = (float) g / 255F;
      this.b = (float) b / 255F;
      this.a = (float) a / 255F;
    }

    @Override
    public void put(int element, @Nonnull float... data) {
      VertexFormatElement.Usage usage = parent.getVertexFormat().getElements().get(element)
          .getUsage();

      // Transform normals and position
      if (usage == VertexFormatElement.Usage.COLOR && data.length >= 4) {
        data[0] = r;
        data[1] = g;
        data[2] = b;
        data[3] = a;
      }
      super.put(element, data);
    }

    public BakedQuad build() {
      return ((BakedQuadBuilder) parent).build();
    }
  }
}
