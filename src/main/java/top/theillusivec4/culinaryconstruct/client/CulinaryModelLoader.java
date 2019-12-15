/*
 * Copyright (c) 2018-2019 C4
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

package top.theillusivec4.culinaryconstruct.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;

public class CulinaryModelLoader {

//  private static List<Item> items = new ArrayList<>(
//      Collections.singletonList(CulinaryConstructRegistry.SANDWICH));
//
//  public static void loadModels(final ModelBakeEvent evt) {
//    for (Item item : items) {
//      ModelResourceLocation resourceLocation = new ModelResourceLocation(
//          Objects.requireNonNull(item.getRegistryName()), "inventory");
//      SimpleBakedModel model = (SimpleBakedModel) evt.getModelRegistry().get(resourceLocation);
//      IBakedModel bakedModel = loadModels();
//    }
//  }
}
