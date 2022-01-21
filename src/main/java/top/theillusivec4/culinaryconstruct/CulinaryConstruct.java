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

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.culinaryconstruct.api.capability.ICulinaryIngredient;
import top.theillusivec4.culinaryconstruct.client.CulinaryScreen;
import top.theillusivec4.culinaryconstruct.client.model.FoodBowlLoader;
import top.theillusivec4.culinaryconstruct.client.model.SandwichLoader;
import top.theillusivec4.culinaryconstruct.common.CulinaryConstructConfig;
import top.theillusivec4.culinaryconstruct.common.advancement.CulinaryTriggers;
import top.theillusivec4.culinaryconstruct.common.capability.CapabilityCulinaryFood;
import top.theillusivec4.culinaryconstruct.common.integration.DietIntegration;
import top.theillusivec4.culinaryconstruct.common.network.CulinaryConstructNetwork;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;

@Mod(CulinaryConstruct.MOD_ID)
public class CulinaryConstruct {

  public static final String MOD_ID = "culinaryconstruct";
  public static final Logger LOGGER = LogManager.getLogger();

  public CulinaryConstruct() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    eventBus.addListener(this::config);
    eventBus.addListener(this::enqueue);
    eventBus.addListener(this::registerCaps);
    ModLoadingContext.get()
        .registerConfig(ModConfig.Type.SERVER, CulinaryConstructConfig.serverSpec);
  }

  private void setup(final FMLCommonSetupEvent evt) {
    CulinaryConstructNetwork.register();
    CapabilityCulinaryFood.register();
    CulinaryTriggers.register();
  }

  private void config(final ModConfigEvent evt) {

    if (evt.getConfig().getModId().equals(MOD_ID)) {
      CulinaryConstructConfig.bake();
    }
  }

  private void registerCaps(final RegisterCapabilitiesEvent evt) {
    evt.register(ICulinaryIngredient.class);
  }

  private void enqueue(final InterModEnqueueEvent evt) {

    if (ModList.get().isLoaded("diet")) {
      DietIntegration.setup();
    }
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    MenuScreens
        .register(CulinaryConstructRegistry.CULINARY_STATION_CONTAINER, CulinaryScreen::new);

  }

  @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class ClientProxy {

    @SubscribeEvent
    public static void registerModels(final ModelRegistryEvent evt) {
      ModelLoaderRegistry
          .registerLoader(new ResourceLocation(CulinaryConstruct.MOD_ID, "sandwich_loader"),
              SandwichLoader.INSTANCE);
      ModelLoaderRegistry
          .registerLoader(new ResourceLocation(CulinaryConstruct.MOD_ID, "food_bowl_loader"),
              FoodBowlLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerTextures(final TextureStitchEvent.Pre evt) {
      TextureAtlas map = evt.getMap();

      if (map.location() == InventoryMenu.BLOCK_ATLAS) {

        for (int i = 0; i < 5; i++) {
          evt.addSprite(new ResourceLocation(CulinaryConstruct.MOD_ID, "item/sandwich/bread" + i));
          evt.addSprite(new ResourceLocation(CulinaryConstruct.MOD_ID, "item/sandwich/layer" + i));
          evt.addSprite(new ResourceLocation(CulinaryConstruct.MOD_ID, "item/bowl/layer" + i));
        }
        evt.addSprite(new ResourceLocation(CulinaryConstruct.MOD_ID, "item/bowl/liquid_base"));
        evt.addSprite(new ResourceLocation(CulinaryConstruct.MOD_ID, "item/bowl/liquid_overflow"));
      }
    }
  }
}
