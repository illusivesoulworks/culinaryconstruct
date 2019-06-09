/*
 * Copyright (c) 2019 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.common.util;

import c4.culinaryconstruct.common.item.ItemSandwich;
import ca.wescook.nutrition.Nutrition;
import ca.wescook.nutrition.capabilities.INutrientManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientUtils;
import ca.wescook.nutrition.proxy.ClientProxy;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class NutritionHelper {

    @CapabilityInject(INutrientManager.class)
    private static final Capability<INutrientManager> NUTRITION_CAPABILITY = null;

    public static void applyNutrients(EntityPlayer player, ItemStack stack) {
        List<Nutrient> foundNutrients = getNutrients(stack);
        float nutritionValue = NutrientUtils.calculateNutrition(stack, foundNutrients);

        if (!player.world.isRemote) {
            INutrientManager manager = player.getCapability(NUTRITION_CAPABILITY, null);

            if (manager != null) {
                manager.add(foundNutrients, nutritionValue);
            }
        } else {
            ClientProxy.localNutrition.add(foundNutrients, nutritionValue);
        }
    }

    public static String getNutrientsTooltip(ItemStack stack) {
        List<Nutrient> foundNutrients = getNutrients(stack);
        StringJoiner stringJoiner = new StringJoiner(", ");

        for (Nutrient nutrient : foundNutrients) {

            if (nutrient.visible) {
                stringJoiner.add(I18n.format("nutrient." + Nutrition.MODID + ":" + nutrient.name));
            }
        }
        String nutrientString = stringJoiner.toString();
        float nutritionValue = NutrientUtils.calculateNutrition(stack, foundNutrients);

        if (!nutrientString.isEmpty()) {
            return I18n.format("tooltip." + Nutrition.MODID + ":nutrients") + " " +
                    TextFormatting.DARK_GREEN + nutrientString +
                    TextFormatting.DARK_AQUA + " (" + String.format("%.1f", nutritionValue) + "%)";
        } else {
            return nutrientString;
        }
    }

    private static List<Nutrient> getNutrients(ItemStack stack) {
        NonNullList<ItemStack> ingredients = NBTHelper.getIngredientsList(stack, true);
        Set<Nutrient> foundNutrients = Sets.newHashSet();

        for (ItemStack ing : ingredients) {

            if (ing.getItem() instanceof ItemSandwich) {
                foundNutrients.addAll(getNutrients(ing));
            } else {
                foundNutrients.addAll(NutrientUtils.getFoodNutrients(ing));
            }
        }
        return Lists.newArrayList(foundNutrients);
    }
}
