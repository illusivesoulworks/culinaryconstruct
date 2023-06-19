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

import com.illusivesoulworks.culinaryconstruct.api.CulinaryIngredientLookup;
import com.illusivesoulworks.culinaryconstruct.api.ICulinaryIngredient;
import com.illusivesoulworks.culinaryconstruct.common.advancement.CraftFoodTrigger;
import com.illusivesoulworks.culinaryconstruct.common.capability.CulinaryIngredients;
import com.illusivesoulworks.culinaryconstruct.common.item.FoodBowlItem;
import com.illusivesoulworks.culinaryconstruct.common.item.SandwichItem;
import com.illusivesoulworks.culinaryconstruct.common.network.CPacketRename;
import com.illusivesoulworks.culinaryconstruct.common.network.CulinaryConstructPackets;
import com.illusivesoulworks.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class CulinaryConstructQuiltMod implements ModInitializer {

  @Override
  public void onInitialize(ModContainer modContainer) {
    CulinaryConstructMod.setup();
    CriteriaTriggers.register(CraftFoodTrigger.INSTANCE);
    ServerPlayNetworking.registerGlobalReceiver(CulinaryConstructPackets.RENAME,
        (server, player, handler, buf, responseSender) -> {
          CPacketRename msg = CPacketRename.decode(buf);
          server.execute(() -> CPacketRename.handle(msg, player));
        });
    for (Item item : BuiltInRegistries.ITEM) {
      testIngredient(item);
    }
    RegistryEntryAddedCallback.event(BuiltInRegistries.ITEM)
        .register((rawId, id, object) -> testIngredient(object));
    ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
      entries.accept(CulinaryConstructRegistry.CULINARY_STATION_ITEM.get());
    });
    ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
      entries.accept(SandwichItem.generateCreativeItem());
      entries.accept(FoodBowlItem.generateCreativeItem());
    });
  }

  private static void testIngredient(Item item) {

    for (Pair<Predicate<Item>, Function<ItemStack, ICulinaryIngredient>> entry : CulinaryIngredients.getDefaults()) {

      if (entry.getFirst().test(item)) {
        CulinaryIngredientLookup.INSTANCE.registerForItems(
            ((stack, context) -> entry.getSecond().apply(stack)), item);
      }
    }
  }
}
