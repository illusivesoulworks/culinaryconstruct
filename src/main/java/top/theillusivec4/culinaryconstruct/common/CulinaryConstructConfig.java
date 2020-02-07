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
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;

public class CulinaryConstructConfig {

  public static final ForgeConfigSpec serverSpec;
  public static final Server SERVER;
  private static final String CONFIG_PREFIX = "gui." + CulinaryConstruct.MODID + ".config.";

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
    public final ForgeConfigSpec.ConfigValue<List<String>> ingredientBlacklist;

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
          .define("ingredientBlacklist", new ArrayList<>());
    }
  }
}
