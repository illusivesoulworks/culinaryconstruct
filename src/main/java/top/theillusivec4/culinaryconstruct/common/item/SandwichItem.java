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

package top.theillusivec4.culinaryconstruct.common.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.culinaryconstruct.common.registry.RegistryReference;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class SandwichItem extends Item {

  public SandwichItem() {
    super(new Item.Properties().group(ItemGroup.FOOD).food(new Food.Builder().build()));
    this.setRegistryName(RegistryReference.SANDWICH);
  }

  @Nonnull
  @Override
  public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World worldIn,
      @Nonnull LivingEntity livingEntity) {

    if (livingEntity instanceof PlayerEntity) {
      int food = CulinaryNBTHelper.getFoodAmount(stack);
      float saturation = CulinaryNBTHelper.getSaturationModifier(stack);
      ((PlayerEntity) livingEntity).getFoodStats().addStats(food, saturation);
    }
    return livingEntity.onFoodEaten(worldIn, stack);
  }

  @Nonnull
  @Override
  public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
    StringBuilder fullName = new StringBuilder();
    NonNullList<ItemStack> ingredients = CulinaryNBTHelper.getIngredientsList(stack, false);

    if (!ingredients.isEmpty()) {
      Map<String, Long> countMap = ingredients.stream()
          .filter(itemstack -> !(itemstack.getItem() instanceof SandwichItem)).collect(Collectors
              .groupingBy(itemstack -> itemstack.getDisplayName().getUnformattedComponentText(),
                  Collectors.counting()));
      List<String> names = new ArrayList<>();

      if (!countMap.isEmpty()) {

        for (String name : countMap.keySet()) {
          long size = countMap.get(name);
          StringBuilder builder = new StringBuilder();

          if (size > 1L) {
            builder.append(new TranslationTextComponent("tooltip.culinaryconstruct.count." + size)
                .getUnformattedComponentText());
            builder.append(" ");
          }
          builder.append(name);
          names.add(builder.toString());
        }
        fullName.append(
            new TranslationTextComponent("tooltip.culinaryconstruct.list." + names.size(),
                names.toArray()).getUnformattedComponentText());
        fullName.append(" ");
      }
    }
    int depth = CulinaryNBTHelper.getDepth(stack);

    if (depth > 0) {
      fullName.append(
          depth > 1 ? new TranslationTextComponent("tooltip.culinaryconstruct.metaplus", depth)
              .getUnformattedComponentText()
              : new TranslationTextComponent("tooltip.culinaryconstruct.meta")
                  .getUnformattedComponentText());
    }
    fullName.append(
        new TranslationTextComponent(this.getTranslationKey(stack)).getUnformattedComponentText());
    return new StringTextComponent(fullName.toString());
  }

  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip,
      ITooltipFlag flagIn) {
    NonNullList<ItemStack> ingredients = CulinaryNBTHelper.getIngredientsList(stack, true);
    int bonus = CulinaryNBTHelper.getBonus(stack);
    tooltip.add(new StringTextComponent(String.format("%s: %s",
        new TranslationTextComponent("tooltip.culinaryconstruct.quality.name")
            .getUnformattedComponentText(),
        new TranslationTextComponent("tooltip.culinaryconstruct.quality." + (bonus + 2))
            .getUnformattedComponentText())));
    tooltip.add(new StringTextComponent(""));

    if (InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 340)
        || InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 344)) {
      tooltip.add(new TranslationTextComponent("tooltip.culinaryconstruct.ingredients.name")
          .setStyle(new Style().setUnderlined(true)));

      for (ItemStack ing : ingredients) {

        if (!ing.isEmpty()) {
          tooltip.add(
              new StringTextComponent("- " + ing.getDisplayName().getUnformattedComponentText()));
        }
      }
    } else {
      tooltip.add(new TranslationTextComponent("tooltip.culinaryconstruct.ingredients"));
    }
  }

  @Override
  public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
      ItemStack sub = new ItemStack(this);
      CulinaryNBTHelper.setTagSize(sub, 5);
      CulinaryNBTHelper.setTagIngredientsList(sub, NonNullList
          .from(ItemStack.EMPTY, new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHER_STAR),
              new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHER_STAR),
              new ItemStack(Items.NETHER_STAR), new ItemStack(Items.BREAD)));
      CulinaryNBTHelper.setTagFood(sub, 20);
      CulinaryNBTHelper.setTagDepth(sub, 0);
      CulinaryNBTHelper.setTagSaturation(sub, 1.0F);
      CulinaryNBTHelper.setTagBonus(sub, 2);
      items.add(sub);
    }
  }
}
