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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BreadHelper {

    private static Set<String> breadOres = new HashSet<>();

    public static void initOreDict() {
        Item item;

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
            item = Item.getByNameOrId("actuallyadditions:item_food");
            if (item != null) {
                OreDictionary.registerOre("bread", new ItemStack(item,1, 10));
                OreDictionary.registerOre("bread", new ItemStack(item,1, 15));
                OreDictionary.registerOre("bread", new ItemStack(item,1, 17));
                OreDictionary.registerOre("bread", new ItemStack(item,1, 19));
            }
        }

        //Reliquary
        if (Loader.isModLoaded("xreliquary")) {
            item = Item.getByNameOrId("xreliquary:glowing_bread");
            if (item != null) {
                OreDictionary.registerOre("bread", new ItemStack(item));
            }
        }

        //AshenWheat
        if (Loader.isModLoaded("ashenwheat")) {
            item = Item.getByNameOrId("ashenwheat:ashbread");
            if (item != null) {
                OreDictionary.registerOre("bread", new ItemStack(item));
            }
            item = Item.getByNameOrId("ashenwheat:scintillabread");
            if (item != null) {
                OreDictionary.registerOre("bread", new ItemStack(item));
            }
        }

        //10pak's Plant Mega Pack
        if (Loader.isModLoaded("pmp")) {
            addBreadOre("foodCornBread");
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
