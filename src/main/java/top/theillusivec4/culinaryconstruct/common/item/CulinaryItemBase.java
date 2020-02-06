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
import top.theillusivec4.culinaryconstruct.api.CulinaryConstructAPI;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class CulinaryItemBase extends Item {

  public static final Random RANDOM = new Random();

  public CulinaryItemBase() {
    super(new Item.Properties().group(ItemGroup.FOOD).food(new Food.Builder().build()));
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
          CulinaryConstructAPI.getCulinaryIngredient(itemstack).ifPresent(culinary -> {
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
              if (RANDOM.nextFloat() < effect.getRight()) {
                player.addPotionEffect(effect.getLeft());
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
          builder.append(new TranslationTextComponent("tooltip.culinaryconstruct.count." + count)
              .getUnformattedComponentText());
          builder.append(" ");
        }
        builder.append(item.getName().getUnformattedComponentText());
        names.add(builder.toString());
      });
      fullName.append(new TranslationTextComponent("tooltip.culinaryconstruct.list." + names.size(),
          names.toArray()).getUnformattedComponentText());
    }
    fullName.append(" ");
    fullName.append(
        new TranslationTextComponent(this.getTranslationKey(stack)).getUnformattedComponentText());
    return new StringTextComponent(fullName.toString());
  }

  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip,
      ITooltipFlag flagIn) {
    ItemStack base = CulinaryNBTHelper.getBase(stack);
    int quality = CulinaryNBTHelper.getQuality(stack);
    tooltip.add(new TranslationTextComponent("tooltip.culinaryconstruct.quality." + quality)
        .applyTextStyle(TextFormatting.GREEN));
    tooltip.add(
        new TranslationTextComponent(base.getTranslationKey()).applyTextStyle(TextFormatting.GRAY));
    tooltip.add(new StringTextComponent(""));

    if (InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 340)
        || InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 344)) {
      NonNullList<ItemStack> ingredients = CulinaryNBTHelper.getIngredientsList(stack);
      tooltip.add(new TranslationTextComponent("tooltip.culinaryconstruct.ingredients.name")
          .applyTextStyle(TextFormatting.GRAY).applyTextStyle(TextFormatting.UNDERLINE));

      for (ItemStack ing : ingredients) {

        if (!ing.isEmpty()) {
          tooltip.add(new TranslationTextComponent(ing.getTranslationKey())
              .applyTextStyle(TextFormatting.GRAY));
        }
      }
    } else {
      tooltip.add(new TranslationTextComponent("tooltip.culinaryconstruct.ingredients")
          .applyTextStyle(TextFormatting.GRAY));
    }
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
}
