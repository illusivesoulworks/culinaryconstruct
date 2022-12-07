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

package com.illusivesoulworks.culinaryconstruct.client;

import com.illusivesoulworks.culinaryconstruct.CulinaryConstructConstants;
import com.illusivesoulworks.culinaryconstruct.client.model.FoodBowlLoader;
import com.illusivesoulworks.culinaryconstruct.client.model.SandwichLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CulinaryConstructForgeClientMod {

  @SubscribeEvent
  public void registerModels(final ModelEvent.RegisterGeometryLoaders evt) {
    evt.register(CulinaryConstructConstants.SANDWICH_ID, SandwichLoader.INSTANCE);
    evt.register(CulinaryConstructConstants.FOOD_BOWL_ID, FoodBowlLoader.INSTANCE);
  }

  @SubscribeEvent
  public void registerTextures(final TextureStitchEvent.Pre evt) {
    TextureAtlas map = evt.getAtlas();

    if (map.location() == InventoryMenu.BLOCK_ATLAS) {

      for (ResourceLocation resourceLocation : CulinaryConstructSprites.get()) {
        evt.addSprite(resourceLocation);
      }
    }
  }

  public static int getFluidColor(Fluid fluid) {
    return IClientFluidTypeExtensions.of(fluid).getTintColor();
  }
}
