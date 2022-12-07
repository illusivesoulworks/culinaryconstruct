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

package com.illusivesoulworks.culinaryconstruct.common.item;

import com.illusivesoulworks.culinaryconstruct.api.CulinaryConstructApi;
import com.illusivesoulworks.culinaryconstruct.common.config.CulinaryConstructConfig;
import com.illusivesoulworks.culinaryconstruct.common.util.CulinaryNBT;
import com.illusivesoulworks.culinaryconstruct.platform.Services;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CulinaryItemBase extends Item {

  public CulinaryItemBase() {
    super(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)
        .food(new FoodProperties.Builder().nutrition(1).saturationMod(1.0f).build()));
  }

  @Nullable
  public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
    FoodProperties.Builder builder = new FoodProperties.Builder();
    builder.nutrition(CulinaryNBT.getFoodAmount(stack));
    builder.saturationMod(CulinaryNBT.getSaturation(stack));
    List<ItemStack> foods = new ArrayList<>(CulinaryNBT.getIngredientsList(stack));
    foods.add(CulinaryNBT.getBase(stack));

    for (ItemStack food : foods) {
      FoodProperties props = Services.REGISTRY.getFoodProperties(food, entity);

      if (props != null) {

        if (props.isMeat()) {
          builder.meat();
        }

        for (Pair<MobEffectInstance, Float> effect : props.getEffects()) {
          builder.effect(effect.getFirst(), effect.getSecond());
        }
      }
      CulinaryConstructApi.getCulinaryIngredient(food).ifPresent(culinary -> {
        for (Pair<MobEffectInstance, Float> effect : culinary.getEffects()) {
          builder.effect(effect.getFirst(), effect.getSecond());
        }
      });
    }
    return builder.build();
  }

  protected static void generateCreativeNBT(ItemStack sub) {
    CulinaryNBT.setSize(sub, 5);
    CulinaryNBT.setIngredientsList(sub, NonNullList
        .of(ItemStack.EMPTY, new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHER_STAR),
            new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHER_STAR),
            new ItemStack(Items.NETHER_STAR)));
    CulinaryNBT.setFoodAmount(sub, 20);
    CulinaryNBT.setSaturation(sub, 1.0F);
    CulinaryNBT.setQuality(sub, 4);
  }

  @Nonnull
  @Override
  public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level worldIn,
                                   @Nonnull LivingEntity livingEntity) {

    if (livingEntity instanceof Player player) {
      List<ItemStack> consumed = new ArrayList<>(CulinaryNBT.getIngredientsList(stack));
      consumed.add(CulinaryNBT.getBase(stack));
      consumed.forEach(itemstack -> {
        if (!itemstack.isEmpty()) {
          CulinaryConstructApi.getCulinaryIngredient(itemstack)
              .ifPresent(culinary -> culinary.onEaten(player));
        }
      });
    }
    return super.finishUsingItem(stack, worldIn, livingEntity);
  }

  @Nonnull
  @Override
  public Component getName(@Nonnull ItemStack stack) {
    StringBuilder fullName = new StringBuilder();
    NonNullList<ItemStack> ingredients = CulinaryNBT.getIngredientsList(stack);

    if (!ingredients.isEmpty()) {
      Map<Item, Long> countMap = ingredients.stream()
          .collect(Collectors.groupingBy(ItemStack::getItem, Collectors.counting()));
      List<String> names = new ArrayList<>();
      countMap.forEach((item, count) -> {
        StringBuilder builder = new StringBuilder();

        if (count > 1) {
          MutableComponent trans =
              Component.translatable("tooltip.culinaryconstruct.count." + count);
          builder.append(trans.getString());
          builder.append(" ");
        }
        builder.append(Component.translatable(item.getDescriptionId()).getString());
        names.add(builder.toString());
      });
      fullName.append(
          Component.translatable("tooltip.culinaryconstruct.list." + names.size(), names.toArray())
              .getString());
    }
    fullName.append(" ");
    fullName.append(Component.translatable(this.getDescriptionId(stack)).getString());

    return Component.literal(fullName.toString());
  }

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(".#");

  @Override
  public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn,
                              @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
    ItemStack base = CulinaryNBT.getBase(stack);
    int quality = CulinaryNBT.getQuality(stack);
    tooltip.add(Component.translatable("tooltip.culinaryconstruct.quality." + quality)
        .withStyle(ChatFormatting.GREEN));
    tooltip.add(Component.translatable(base.getDescriptionId()).withStyle(ChatFormatting.GRAY));
    tooltip.add(Component.empty());
    long windowId = Minecraft.getInstance().getWindow().getWindow();

    if (InputConstants.isKeyDown(windowId, 340) || InputConstants.isKeyDown(windowId, 344)) {
      NonNullList<ItemStack> ingredients = CulinaryNBT.getIngredientsList(stack);
      tooltip.add(Component.translatable("tooltip.culinaryconstruct.ingredients.name")
          .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.UNDERLINE));

      for (ItemStack ing : ingredients) {

        if (!ing.isEmpty()) {
          tooltip.add(Component.translatable(ing.getDescriptionId())
              .withStyle(ChatFormatting.GRAY));
        }
      }

      if (CulinaryConstructConfig.showNutritionInfo) {
        tooltip.add(Component.empty());
        int food = CulinaryNBT.getFoodAmount(stack);
        tooltip.add(
            Component.translatable("tooltip.culinaryconstruct.nutrition").append(": " + food)
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tooltip.culinaryconstruct.saturation")
            .append(": " + DECIMAL_FORMAT.format(food * 2.0F * CulinaryNBT.getSaturation(stack)))
            .withStyle(ChatFormatting.YELLOW));
      }
    } else {
      tooltip.add(Component.translatable("tooltip.culinaryconstruct.ingredients")
          .withStyle(ChatFormatting.GRAY));
    }
  }
}
