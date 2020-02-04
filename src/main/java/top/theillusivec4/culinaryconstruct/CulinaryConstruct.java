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

package top.theillusivec4.culinaryconstruct;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.culinaryconstruct.client.CulinaryScreen;
import top.theillusivec4.culinaryconstruct.client.FoodBowlModel.BakedFoodBowlModel;
import top.theillusivec4.culinaryconstruct.client.FoodBowlModel.BakedFoodBowlOverrideHandler;
import top.theillusivec4.culinaryconstruct.client.SandwichModel.BakedSandwichModel;
import top.theillusivec4.culinaryconstruct.client.SandwichModel.BakedSandwichOverrideHandler;
import top.theillusivec4.culinaryconstruct.common.capability.CapabilityCulinaryFood;
import top.theillusivec4.culinaryconstruct.common.network.CulinaryConstructNetwork;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import top.theillusivec4.culinaryconstruct.common.registry.RegistryReference;

@Mod(CulinaryConstruct.MODID)
public class CulinaryConstruct {

  public static final String MODID = "culinaryconstruct";
  public static final Logger LOGGER = LogManager.getLogger();

  public CulinaryConstruct() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
  }

  private void setup(final FMLCommonSetupEvent evt) {
    CulinaryConstructNetwork.register();
    CapabilityCulinaryFood.register();
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    ScreenManager
        .registerFactory(CulinaryConstructRegistry.CULINARY_STATION_CONTAINER, CulinaryScreen::new);
  }

  @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class ClientProxy {

    @SubscribeEvent
    public static void bakeModels(final ModelBakeEvent evt) {
      ModelResourceLocation rl = new ModelResourceLocation(RegistryReference.SANDWICH, "inventory");
      SimpleBakedModel original = (SimpleBakedModel) evt.getModelRegistry().get(rl);
      BlockModel unbaked = (BlockModel) evt.getModelLoader().getUnbakedModel(rl);
      IBakedModel model = new BakedSandwichModel(original,
          new BakedSandwichOverrideHandler(evt.getModelLoader(), unbaked));
      evt.getModelRegistry().put(rl, model);
      rl = new ModelResourceLocation(RegistryReference.FOOD_BOWL, "inventory");
      original = (SimpleBakedModel) evt.getModelRegistry().get(rl);
      unbaked = (BlockModel) evt.getModelLoader().getUnbakedModel(rl);
      model = new BakedFoodBowlModel(original,
          new BakedFoodBowlOverrideHandler(evt.getModelLoader(), unbaked));
      evt.getModelRegistry().put(rl, model);
    }

    @SubscribeEvent
    public static void registerTextures(final TextureStitchEvent.Pre evt) {
      AtlasTexture map = evt.getMap();

      if (map.getBasePath().equals("textures")) {
        for (int i = 0; i < 5; i++) {
          evt.addSprite(new ResourceLocation(CulinaryConstruct.MODID, "item/sandwich/bread" + i));
          evt.addSprite(new ResourceLocation(CulinaryConstruct.MODID, "item/sandwich/layer" + i));
          evt.addSprite(new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/layer" + i));
        }
        evt.addSprite(new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/liquid_base"));
        evt.addSprite(new ResourceLocation(CulinaryConstruct.MODID, "item/bowl/liquid_overflow"));
      }
    }
  }
}
