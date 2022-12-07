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

import com.illusivesoulworks.culinaryconstruct.mixin.core.CulinaryConstructSpriteMixin;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ColorMixer {

  public static int getDominantColor(TextureAtlasSprite sprite) {
    int iconWidth = sprite.getWidth();
    int iconHeight = sprite.getHeight();

    if (iconWidth <= 0 || iconHeight <= 0) {
      return 0xFFFFFF;
    }
    TreeMap<Integer, Integer> counts = new TreeMap<>();
    for (int v = 0; v < iconWidth; v++) {
      for (int u = 0; u < iconHeight; u++) {
        int rgba = ((CulinaryConstructSpriteMixin) sprite).getMainImage()[0].getPixelRGBA(v, u);
        int alpha = rgba >> 24 & 0xFF;

        if (alpha > 0) {
          counts.merge(rgba, 1, (color, count) -> count + 1);
        }
      }
    }
    int dominantColor = 0;
    int dominantSum = 0;

    for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
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

    if (colors.isEmpty()) {
      return 0;
    }
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
    Color color = new Color(j2, true);
    return new Color(color.getRed(), color.getGreen(), color.getBlue()).getRGB();
  }
}
