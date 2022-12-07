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

package com.illusivesoulworks.culinaryconstruct;

import com.illusivesoulworks.culinaryconstruct.api.ICulinaryIngredient;
import com.illusivesoulworks.culinaryconstruct.common.capability.CulinaryIngredientCapability;
import com.illusivesoulworks.culinaryconstruct.client.CulinaryConstructForgeClientMod;
import com.illusivesoulworks.culinaryconstruct.client.CulinaryStationScreen;
import com.illusivesoulworks.culinaryconstruct.common.advancement.CraftFoodTrigger;
import com.illusivesoulworks.culinaryconstruct.common.network.CulinaryConstructForgeNetwork;
import com.illusivesoulworks.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CulinaryConstructConstants.MOD_ID)
public class CulinaryConstructForgeMod {

  public CulinaryConstructForgeMod() {
    CulinaryConstructMod.setup();
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::registerCaps);
    eventBus.addListener(this::clientSetup);
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
        () -> () -> eventBus.register(new CulinaryConstructForgeClientMod()));
  }

  private void setup(final FMLCommonSetupEvent evt) {
    CulinaryConstructForgeNetwork.setup();
    CulinaryIngredientCapability.setup();
    evt.enqueueWork(() -> {
      CriteriaTriggers.register(CraftFoodTrigger.INSTANCE);
    });
  }

  private void registerCaps(final RegisterCapabilitiesEvent evt) {
    evt.register(ICulinaryIngredient.class);
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    MenuScreens.register(CulinaryConstructRegistry.CULINARY_STATION_MENU.get(),
        CulinaryStationScreen::new);
  }
}
