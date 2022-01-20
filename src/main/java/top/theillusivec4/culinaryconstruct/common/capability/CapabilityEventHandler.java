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

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import org.apache.commons.lang3.tuple.Pair;
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
    } else if (item instanceof BucketItem) {
      evt.addCapability(CulinaryConstructCapability.INGREDIENT_ID,
          CapabilityCulinaryFood.createCulinaryIngredient(new ICulinaryIngredient() {
            @Override
            public boolean isValid() {
              Fluid fluid = ((BucketItem) item).getFluid();

              if (fluid == Fluids.EMPTY) {
                return false;
              }
              FluidAttributes attributes = fluid.getAttributes();
              return !attributes.isGaseous() && !attributes.isLighterThanAir()
                  && attributes.getTemperature() <= 400;
            }

            @Override
            public boolean isLiquid() {
              return true;
            }

            @Override
            public Integer getLiquidColor() {
              Fluid fluid = ((BucketItem) item).getFluid();

              if (fluid == Fluids.WATER) {
                return null;
              }
              FluidAttributes attributes = fluid.getAttributes();
              return attributes.getColor();
            }
          }));
    } else if (item instanceof MilkBucketItem) {
      evt.addCapability(CulinaryConstructCapability.INGREDIENT_ID,
          CapabilityCulinaryFood.createCulinaryIngredient(new ICulinaryIngredient() {
            @Override
            public void onEaten(PlayerEntity player) {
              if (!player.world.isRemote) {
                player.curePotionEffects(stack);
              }
            }

            @Override
            public boolean isLiquid() {
              return true;
            }

            @Override
            public Integer getLiquidColor() {
              return 16777215;
            }
          }));
    } else if (item instanceof PotionItem) {
      evt.addCapability(CulinaryConstructCapability.INGREDIENT_ID,
          CapabilityCulinaryFood.createCulinaryIngredient(new ICulinaryIngredient() {
            @Override
            public List<Pair<EffectInstance, Float>> getEffects() {
              List<Pair<EffectInstance, Float>> list = new ArrayList<>();
              PotionUtils.getEffectsFromStack(stack).forEach(effect -> list.add(Pair.of(
                  new EffectInstance(effect.getPotion(), effect.getDuration(),
                      effect.getAmplifier()), 1.0F)));
              return list;
            }

            @Override
            public boolean isLiquid() {
              return true;
            }

            @Override
            public Integer getLiquidColor() {
              return PotionUtils.getColor(stack);
            }
          }));
    } else if (item instanceof SuspiciousStewItem) {
      evt.addCapability(CulinaryConstructCapability.INGREDIENT_ID,
          CapabilityCulinaryFood.createCulinaryIngredient(new ICulinaryIngredient() {
            @Override
            public List<Pair<EffectInstance, Float>> getEffects() {
              List<Pair<EffectInstance, Float>> list = new ArrayList<>();
              CompoundNBT compoundnbt = stack.getTag();

              if (compoundnbt != null && compoundnbt.contains("Effects", 9)) {
                ListNBT listnbt = compoundnbt.getList("Effects", 10);

                for (int i = 0; i < listnbt.size(); ++i) {
                  int j = 160;
                  CompoundNBT compoundnbt1 = listnbt.getCompound(i);

                  if (compoundnbt1.contains("EffectDuration", 3)) {
                    j = compoundnbt1.getInt("EffectDuration");
                  }
                  Effect effect = Effect.get(compoundnbt1.getByte("EffectId"));

                  if (effect != null) {
                    list.add(Pair.of(new EffectInstance(effect, j), 1.0F));
                  }
                }
              }
              return list;
            }
          }));
    }
  }
}
