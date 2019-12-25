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

package top.theillusivec4.culinaryconstruct.common.capability;

import net.minecraft.block.CakeBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.culinaryconstruct.api.capability.CulinaryConstructCapability;
import top.theillusivec4.culinaryconstruct.api.capability.ICulinaryIngredient;

public class CapabilityEventHandler {

  @SubscribeEvent
  public void attachCapabilities(AttachCapabilitiesEvent<ItemStack> evt) {
    ItemStack stack = evt.getObject();
    Item item = stack.getItem();

    if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CakeBlock) {
      evt.addCapability(CulinaryConstructCapability.INGREDIENT_ID,
          CapabilityCulinaryFood.createCulinaryIngredient(new ICulinaryIngredient() {
            @Override
            public int getFoodAmount() {
              return 14;
            }

            @Override
            public float getSaturation() {
              return 0.2F;
            }
          }));
    }
  }
}
