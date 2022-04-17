/*
 * Copyright (c) 2018-2020 C4
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

package top.theillusivec4.culinaryconstruct.common.item;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.culinaryconstruct.api.CulinaryConstructApi;
import top.theillusivec4.culinaryconstruct.common.CulinaryConstructConfig;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class CulinaryItemBase extends Item {

  public CulinaryItemBase() {
    super(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)
        .food(new FoodProperties.Builder().nutrition(1).saturationMod(1.0f).build()));
  }

  @Nullable
  @Override
  public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
    FoodProperties.Builder builder = new FoodProperties.Builder();
    builder.nutrition(CulinaryNBTHelper.getFoodAmount(stack));
    builder.saturationMod(CulinaryNBTHelper.getSaturation(stack));
    List<ItemStack> foods = new ArrayList<>(CulinaryNBTHelper.getIngredientsList(stack));
    foods.add(CulinaryNBTHelper.getBase(stack));

    for (ItemStack food : foods) {
      FoodProperties props = food.getFoodProperties(entity);

      if (props != null) {

        if (props.isMeat()) {
          builder.meat();
        }

        for (Pair<MobEffectInstance, Float> effect : props.getEffects()) {
          builder.effect(effect::getFirst, effect.getSecond());
        }
      }
      CulinaryConstructApi.getCulinaryIngredient(food).ifPresent(culinary -> {
        for (org.apache.commons.lang3.tuple.Pair<MobEffectInstance, Float> effect : culinary.getEffects()) {
          builder.effect(effect::getLeft, effect.getRight());
        }
      });
    }
    return builder.build();
  }

  protected static void generateCreativeNBT(ItemStack sub) {
    CulinaryNBTHelper.setSize(sub, 5);
    CulinaryNBTHelper.setIngredientsList(sub, NonNullList
        .of(ItemStack.EMPTY, new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHER_STAR),
            new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHER_STAR),
            new ItemStack(Items.NETHER_STAR)));
    CulinaryNBTHelper.setFoodAmount(sub, 20);
    CulinaryNBTHelper.setSaturation(sub, 1.0F);
    CulinaryNBTHelper.setQuality(sub, 4);
  }

  @Nonnull
  @Override
  public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level worldIn,
                                   @Nonnull LivingEntity livingEntity) {

    if (livingEntity instanceof Player player) {
      List<ItemStack> consumed = new ArrayList<>(CulinaryNBTHelper.getIngredientsList(stack));
      consumed.add(CulinaryNBTHelper.getBase(stack));
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
    NonNullList<ItemStack> ingredients = CulinaryNBTHelper.getIngredientsList(stack);

    if (!ingredients.isEmpty()) {
      Map<Item, Long> countMap = ingredients.stream()
          .collect(Collectors.groupingBy(ItemStack::getItem, Collectors.counting()));
      List<String> names = new ArrayList<>();
      countMap.forEach((item, count) -> {
        StringBuilder builder = new StringBuilder();

        if (count > 1) {
          TranslatableComponent trans = new TranslatableComponent(
              "tooltip.culinaryconstruct.count." + count);
          builder.append(trans.getString());
          builder.append(" ");
        }
        builder.append(new TranslatableComponent(item.getDescriptionId()).getString());
        names.add(builder.toString());
      });
      fullName.append(new TranslatableComponent("tooltip.culinaryconstruct.list." + names.size(),
          names.toArray()).getString());
    }
    fullName.append(" ");
    fullName.append(new TranslatableComponent(this.getDescriptionId(stack)).getString());

    return new TextComponent(fullName.toString());
  }

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(".#");

  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn,
                              List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
    ItemStack base = CulinaryNBTHelper.getBase(stack);
    int quality = CulinaryNBTHelper.getQuality(stack);
    tooltip.add(new TranslatableComponent("tooltip.culinaryconstruct.quality." + quality)
        .withStyle(ChatFormatting.GREEN));
    tooltip.add(
        new TranslatableComponent(base.getDescriptionId()).withStyle(ChatFormatting.GRAY));
    tooltip.add(new TextComponent(""));

    if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340)
        || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344)) {
      NonNullList<ItemStack> ingredients = CulinaryNBTHelper.getIngredientsList(stack);
      tooltip.add(new TranslatableComponent("tooltip.culinaryconstruct.ingredients.name")
          .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.UNDERLINE));

      for (ItemStack ing : ingredients) {

        if (!ing.isEmpty()) {
          tooltip.add(new TranslatableComponent(ing.getDescriptionId())
              .withStyle(ChatFormatting.GRAY));
        }
      }

      if (CulinaryConstructConfig.showNutritionInfo) {
        tooltip.add(new TextComponent(""));
        int food = CulinaryNBTHelper.getFoodAmount(stack);
        tooltip.add(
            new TranslatableComponent("tooltip.culinaryconstruct.nutrition").append(
                ": " + food).withStyle(ChatFormatting.RED));
        tooltip.add(
            new TranslatableComponent("tooltip.culinaryconstruct.saturation").append(
                    ": " + DECIMAL_FORMAT.format(food * 2.0F * CulinaryNBTHelper.getSaturation(stack)))
                .withStyle(ChatFormatting.YELLOW));
      }
    } else {
      tooltip.add(new TranslatableComponent("tooltip.culinaryconstruct.ingredients")
          .withStyle(ChatFormatting.GRAY));
    }
  }
}
