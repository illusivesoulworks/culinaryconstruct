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

package com.illusivesoulworks.culinaryconstruct.client.model.color;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.IQuadTransformer;

public class ColoredQuadTransformer implements IQuadTransformer {

  private int color;

  public void color(List<BakedQuad> quads, int color) {
    this.color = color;
    this.processInPlace(quads);
  }

  @Override
  public void processInPlace(@Nonnull BakedQuad quad) {
    int[] vertices = quad.getVertices();
    int a = (this.color >> 24);

    if (a == 0) {
      a = 255;
    }
    int r = (this.color >> 16) & 0xFF;
    int g = (this.color >> 8) & 0xFF;
    int b = (this.color) & 0xFF;

    for (int i = 0; i < 4; i++) {
      int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.COLOR;
      vertices[offset] = ((a & 0xFF) << 24) | ((b & 0xFF) << 16) | ((g & 0xFF) << 8) | (r & 0xFF);
    }
  }
}
