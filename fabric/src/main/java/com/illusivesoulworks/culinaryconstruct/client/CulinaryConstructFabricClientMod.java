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
import com.illusivesoulworks.culinaryconstruct.client.model.FoodBowlModel;
import com.illusivesoulworks.culinaryconstruct.client.model.SandwichModel;
import com.illusivesoulworks.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import java.util.Iterator;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class CulinaryConstructFabricClientMod implements ClientModInitializer {

  @SuppressWarnings("all")
  public static Integer getFluidColor(ItemStack stack) {
    Storage<FluidVariant> storage =
        FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));

    if (storage != null) {
      Iterator<StorageView<FluidVariant>> iter = storage.iterator();

      if (iter.hasNext()) {
        StorageView<FluidVariant> next = iter.next();
        Fluid fluid = next.getResource().getFluid();

        if (fluid == Fluids.WATER || fluid == Fluids.EMPTY) {
          return null;
        }
        return FluidRenderHandlerRegistry.INSTANCE.get(fluid)
            .getFluidColor(null, null, fluid.defaultFluidState());
      }
    }
    return null;
  }

  @Override
  public void onInitializeClient() {
    MenuScreens.register(CulinaryConstructRegistry.CULINARY_STATION_MENU.get(),
        CulinaryStationScreen::new);
    ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "food_bowl/layer0"),
          "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "food_bowl/layer1"),
          "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "food_bowl/layer2"),
          "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "food_bowl/layer3"),
          "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "food_bowl/layer4"),
          "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "food_bowl/liquid_base"),
          "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "food_bowl/liquid_overflow"),
          "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "sandwich/layer0"), "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "sandwich/layer1"), "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "sandwich/layer2"), "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "sandwich/layer3"), "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "sandwich/layer4"), "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "sandwich/bread0"), "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "sandwich/bread1"), "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "sandwich/bread2"), "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "sandwich/bread3"), "inventory"));
      out.accept(new ModelResourceLocation(
          new ResourceLocation(CulinaryConstructConstants.MOD_ID, "sandwich/bread4"), "inventory"));

    });
    ModelLoadingRegistry.INSTANCE.registerVariantProvider(
        resourceManager -> (modelIdentifier, modelProviderContext) -> {
          Renderer renderer = RendererAccess.INSTANCE.getRenderer();

          if (modelIdentifier.getNamespace().equals(CulinaryConstructConstants.MOD_ID)) {

            if (modelIdentifier.getPath().equals("sandwich")) {

              if (renderer == null) {
                return BlockModel.fromString(
                    "{\"parent\":\"minecraft:item/generated\",\"textures\":{\"layer0\":\"culinaryconstruct:item/sandwich/bread0\"}}");
              }
              return new SandwichModel(renderer.meshBuilder());
            } else if (modelIdentifier.getPath().equals("food_bowl")) {

              if (renderer == null) {
                return BlockModel.fromString(
                    "{\"parent\":\"minecraft:item/generated\",\"textures\":{\"layer0\":\"minecraft:item/bowl\"}}");
              }
              return new FoodBowlModel(renderer.meshBuilder());
            }
          }
          return null;
        });
  }
}
