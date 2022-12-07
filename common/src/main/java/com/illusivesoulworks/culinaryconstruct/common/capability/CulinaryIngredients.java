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

package com.illusivesoulworks.culinaryconstruct.common.capability;

import com.illusivesoulworks.culinaryconstruct.api.ICulinaryIngredient;
import com.illusivesoulworks.culinaryconstruct.platform.Services;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.CakeBlock;

public class CulinaryIngredients {

  private static final List<Pair<Predicate<Item>, Function<ItemStack, ICulinaryIngredient>>>
      DEFAULT_LIST = new ArrayList<>();

  public static void setup() {
    DEFAULT_LIST.clear();
    DEFAULT_LIST.add(new Pair<>(
        (item) -> item instanceof BlockItem blockItem && blockItem.getBlock() instanceof CakeBlock,
        (stack) -> new ICulinaryIngredient() {
          @Override
          public int getFoodAmount() {
            return 14;
          }

          @Override
          public float getSaturation() {
            return 0.2f;
          }
        }));
    DEFAULT_LIST.add(new Pair<>(
        (item) -> item instanceof BucketItem,
        (stack) -> new ICulinaryIngredient() {
          @Override
          public boolean isValid() {
            return Services.PLATFORM.isFluidValid(stack);
          }

          @Override
          public boolean isLiquid() {
            return true;
          }

          @Override
          public Integer getLiquidColor() {
            return Services.PLATFORM.getFluidColor(stack);
          }
        }));
    DEFAULT_LIST.add(new Pair<>(
        (item) -> item instanceof MilkBucketItem,
        (stack) -> new ICulinaryIngredient() {
          @Override
          public void onEaten(Player player) {

            if (!player.getLevel().isClientSide()) {
              Services.PLATFORM.cureStatusEffects(stack, player);
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
        }
    ));
    DEFAULT_LIST.add(new Pair<>(
        (item) -> item instanceof PotionItem,
        (stack) -> new ICulinaryIngredient() {
          @Override
          public List<Pair<MobEffectInstance, Float>> getEffects() {
            List<Pair<MobEffectInstance, Float>> list = new ArrayList<>();
            PotionUtils.getMobEffects(stack).forEach(effect -> list.add(Pair.of(
                new MobEffectInstance(effect.getEffect(), effect.getDuration(),
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
        }
    ));
    DEFAULT_LIST.add(new Pair<>(
        (item) -> item instanceof SuspiciousStewItem,
        (stack) -> new ICulinaryIngredient() {
          @Override
          public List<Pair<MobEffectInstance, Float>> getEffects() {
            List<Pair<MobEffectInstance, Float>> list = new ArrayList<>();
            CompoundTag compoundnbt = stack.getTag();

            if (compoundnbt != null && compoundnbt.contains("Effects", 9)) {
              ListTag listnbt = compoundnbt.getList("Effects", 10);

              for (int i = 0; i < listnbt.size(); ++i) {
                int j = 160;
                CompoundTag compoundnbt1 = listnbt.getCompound(i);

                if (compoundnbt1.contains("EffectDuration", 3)) {
                  j = compoundnbt1.getInt("EffectDuration");
                }
                MobEffect effect = MobEffect.byId(compoundnbt1.getByte("EffectId"));

                if (effect != null) {
                  list.add(Pair.of(new MobEffectInstance(effect, j), 1.0F));
                }
              }
            }
            return list;
          }
        }
    ));
  }

  public static List<Pair<Predicate<Item>, Function<ItemStack, ICulinaryIngredient>>> getDefaults() {
    return DEFAULT_LIST;
  }
}
