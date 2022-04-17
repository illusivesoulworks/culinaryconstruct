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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.culinaryconstruct.api.CulinaryConstructApi;
import top.theillusivec4.culinaryconstruct.common.CulinaryConstructConfig;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class CulinaryItemBase extends Item {

  public static final Random RANDOM = new Random();

  public CulinaryItemBase() {
    super(new Item.Properties().group(ItemGroup.FOOD).food(new Food.Builder().build()));
  }

  protected static void generateCreativeNBT(ItemStack sub) {
    CulinaryNBTHelper.setSize(sub, 5);
    CulinaryNBTHelper.setIngredientsList(sub, NonNullList
        .from(ItemStack.EMPTY, new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHER_STAR),
            new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHER_STAR),
            new ItemStack(Items.NETHER_STAR)));
    CulinaryNBTHelper.setFoodAmount(sub, 20);
    CulinaryNBTHelper.setSaturation(sub, 1.0F);
    CulinaryNBTHelper.setQuality(sub, 4);
  }

  @Nonnull
  @Override
  public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World worldIn,
                                   @Nonnull LivingEntity livingEntity) {

    if (livingEntity instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) livingEntity;
      int food = CulinaryNBTHelper.getFoodAmount(stack);
      float saturation = CulinaryNBTHelper.getSaturation(stack);
      player.getFoodStats().addStats(food, saturation);
      List<ItemStack> consumed = new ArrayList<>(CulinaryNBTHelper.getIngredientsList(stack));
      consumed.add(CulinaryNBTHelper.getBase(stack));
      consumed.forEach(itemstack -> {
        if (!itemstack.isEmpty()) {
          CulinaryConstructApi.getCulinaryIngredient(itemstack).ifPresent(culinary -> {
            culinary.onEaten(player);
            culinary.getEffects().forEach(effect -> {
              if (RANDOM.nextFloat() < effect.getRight()) {
                player.addPotionEffect(effect.getLeft());
              }
            });
          });

          Food foodie = itemstack.getItem().getFood();

          if (foodie != null) {
            foodie.getEffects().forEach(effect -> {
              if (RANDOM.nextFloat() < effect.getSecond()) {
                player.addPotionEffect(effect.getFirst());
              }
            });
          }
        }
      });
    }
    return livingEntity.onFoodEaten(worldIn, stack);
  }

  @Nonnull
  @Override
  public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
    StringBuilder fullName = new StringBuilder();
    NonNullList<ItemStack> ingredients = CulinaryNBTHelper.getIngredientsList(stack);

    if (!ingredients.isEmpty()) {
      Map<Item, Long> countMap = ingredients.stream()
          .collect(Collectors.groupingBy(ItemStack::getItem, Collectors.counting()));
      List<String> names = new ArrayList<>();
      countMap.forEach((item, count) -> {
        StringBuilder builder = new StringBuilder();

        if (count > 1) {
          TranslationTextComponent trans = new TranslationTextComponent(
              "tooltip.culinaryconstruct.count." + count);
          builder.append(trans.getString());
          builder.append(" ");
        }
        builder.append(new TranslationTextComponent(item.getTranslationKey()).getString());
        names.add(builder.toString());
      });
      fullName.append(new TranslationTextComponent("tooltip.culinaryconstruct.list." + names.size(),
          names.toArray()).getString());
    }
    fullName.append(" ");
    fullName.append(new TranslationTextComponent(this.getTranslationKey(stack)).getString());

    return new StringTextComponent(fullName.toString());
  }

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(".#");

  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip,
                             ITooltipFlag flagIn) {
    ItemStack base = CulinaryNBTHelper.getBase(stack);
    int quality = CulinaryNBTHelper.getQuality(stack);
    tooltip.add(new TranslationTextComponent("tooltip.culinaryconstruct.quality." + quality)
        .mergeStyle(TextFormatting.GREEN));
    tooltip.add(
        new TranslationTextComponent(base.getTranslationKey()).mergeStyle(TextFormatting.GRAY));
    tooltip.add(new StringTextComponent(""));

    if (InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340)
        || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344)) {
      NonNullList<ItemStack> ingredients = CulinaryNBTHelper.getIngredientsList(stack);
      tooltip.add(new TranslationTextComponent("tooltip.culinaryconstruct.ingredients.name")
          .mergeStyle(TextFormatting.GRAY).mergeStyle(TextFormatting.UNDERLINE));

      for (ItemStack ing : ingredients) {

        if (!ing.isEmpty()) {
          tooltip.add(new TranslationTextComponent(ing.getTranslationKey())
              .mergeStyle(TextFormatting.GRAY));
        }
      }

      if (CulinaryConstructConfig.showNutritionInfo) {
        tooltip.add(new StringTextComponent(""));
        int food = CulinaryNBTHelper.getFoodAmount(stack);
        tooltip.add(
            new TranslationTextComponent("tooltip.culinaryconstruct.nutrition").appendString(
                ": " + food).mergeStyle(TextFormatting.RED));
        tooltip.add(
            new TranslationTextComponent("tooltip.culinaryconstruct.saturation").appendString(
                    ": " + DECIMAL_FORMAT.format(food * 2.0F * CulinaryNBTHelper.getSaturation(stack)))
                .mergeStyle(TextFormatting.YELLOW));
      }
    } else {
      tooltip.add(new TranslationTextComponent("tooltip.culinaryconstruct.ingredients")
          .mergeStyle(TextFormatting.GRAY));
    }
  }
}
