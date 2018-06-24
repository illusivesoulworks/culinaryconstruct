/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.common.util;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

public class BreadHelper {

    private static Set<String> breadOres = new HashSet<>();

    public static void initOreDict() {

        //Vanilla
        OreDictionary.registerOre("bread", Items.BREAD);
        addBreadOre("bread");

        //Harvestcraft
        if (Loader.isModLoaded("harvestcraft")) {
            addBreadOres(
                    "foodPumpkinbread",
                    "foodGingerbread",
                    "foodGarlicbread",
                    "foodZucchinibread",
                    "foodWalnutraisinbread",
                    "foodBanananutbread",
                    "foodDatenutbread",
                    "foodFairybread",
                    "foodHoneybread");
        }

        //Actually Additions
        if (Loader.isModLoaded("actuallyadditions")) {
            Item item = Item.getByNameOrId("actuallyadditions:item_food");
            if (item != null) {
                OreDictionary.registerOre("bread", new ItemStack(item,1, 10));
                OreDictionary.registerOre("bread", new ItemStack(item,1, 15));
                OreDictionary.registerOre("bread", new ItemStack(item,1, 17));
                OreDictionary.registerOre("bread", new ItemStack(item,1, 19));
            }
        }
    }

    private static void addBreadOre(String ore) {
        breadOres.add(ore);
    }

    private static void addBreadOres(String... ore) {
        breadOres.addAll(Arrays.asList(ore));
    }

    public static boolean isValidBread(ItemStack stack) {
        for (String oreDictEntry : breadOres) {
            if (OreDictionary.containsMatch(true, OreDictionary.getOres(oreDictEntry), stack)) {
                return true;
            }
        }
        return false;
    }
}
