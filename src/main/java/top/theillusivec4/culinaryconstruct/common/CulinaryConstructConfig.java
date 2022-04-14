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

package top.theillusivec4.culinaryconstruct.common;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.api.CulinaryConstructApi;
import top.theillusivec4.culinaryconstruct.api.capability.ICulinaryIngredient;

public class CulinaryConstructConfig {

  public static final ForgeConfigSpec serverSpec;
  public static final Server SERVER;
  private static final String CONFIG_PREFIX = "gui." + CulinaryConstruct.MOD_ID + ".config.";

  static {
    final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
        .configure(Server::new);
    serverSpec = specPair.getRight();
    SERVER = specPair.getLeft();
  }

  public static class Server {

    public final ForgeConfigSpec.IntValue maxFoodPerSandwich;
    public final ForgeConfigSpec.DoubleValue maxIngredientSaturation;
    public final ForgeConfigSpec.IntValue maxIngredientFood;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> ingredientBlacklist;

    public Server(ForgeConfigSpec.Builder builder) {
      builder.push("server");

      maxFoodPerSandwich = builder
          .comment("The maximum amount of food that a single sandwich can give")
          .translation(CONFIG_PREFIX + "maxFoodPerSandwich")
          .defineInRange("maxFoodPerSandwich", 10, 1, 100);

      maxIngredientSaturation = builder.comment(
              "Blacklist ingredients with more than this max saturation modifier, -1 to disable")
          .translation(CONFIG_PREFIX + "maxIngredientSaturation")
          .defineInRange("maxIngredientSaturation", -1.0D, -1.0D, 100.0D);

      maxIngredientFood = builder
          .comment("Blacklist ingredients with more than this max food value, -1 to disable")
          .translation(CONFIG_PREFIX + "maxIngredientFood")
          .defineInRange("maxIngredientFood", -1, -1, 100);

      ingredientBlacklist = builder.comment("List of items to blacklist as ingredients")
          .translation(CONFIG_PREFIX + "ingredientBlacklist")
          .defineList("ingredientBlacklist", new ArrayList<>(), s -> s instanceof String);
    }
  }

  public static int maxFoodPerSandwich;
  public static double maxIngredientSaturation;
  public static int maxIngredientFood;
  public static List<Item> ingredientBlacklist;

  public static void bake() {
    maxFoodPerSandwich = SERVER.maxFoodPerSandwich.get();
    maxIngredientFood = SERVER.maxIngredientFood.get();
    maxIngredientSaturation = SERVER.maxIngredientSaturation.get();
    ingredientBlacklist = new ArrayList<>();

    SERVER.ingredientBlacklist.get().forEach(item -> {
      Item type = ForgeRegistries.ITEMS.getValue(new ResourceLocation(item));

      if (type != null) {
        ingredientBlacklist.add(type);
      }
    });
  }

  public static boolean isValidIngredient(ItemStack stack) {
    Item item = stack.getItem();
    FoodProperties food = item.getFoodProperties(stack, null);
    LazyOptional<ICulinaryIngredient> culinary = CulinaryConstructApi.getCulinaryIngredient(stack);
    int foodAmount = 0;
    float saturationAmount = 0;

    if (culinary.isPresent()) {
      foodAmount = culinary.map(ICulinaryIngredient::getFoodAmount).orElse(0);
      saturationAmount = culinary.map(ICulinaryIngredient::getSaturation).orElse(0.0F);
    } else if (food != null) {
      foodAmount = food.getNutrition();
      saturationAmount = food.getSaturationModifier();
    }
    int maxFood = CulinaryConstructConfig.maxIngredientFood;
    double maxSaturation = CulinaryConstructConfig.maxIngredientSaturation;
    List<Item> blacklist = CulinaryConstructConfig.ingredientBlacklist;
    boolean blacklisted = false;

    if (!blacklist.isEmpty()) {
      blacklisted = blacklist.contains(item);
    }
    return (maxFood < 0 || foodAmount <= maxFood) && (maxSaturation < 0
        || saturationAmount <= maxSaturation) && !blacklisted;
  }
}
