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
            addBreadItems("actuallyadditions:item_food", 10, 15, 17, 19);
        }

        //Reliquary
        if (Loader.isModLoaded("xreliquary")) {
            addBreadItem("xreliquary:glowing_bread");
        }

        //AshenWheat
        if (Loader.isModLoaded("ashenwheat")) {
            addBreadItems("ashenwheat:ashbread", "ashenwheat:scintillabread");
        }

        //10pak's Plant Mega Pack
        if (Loader.isModLoaded("pmp")) {
            addBreadOre("foodCornBread");
        }

	      //XL Food Mod
        if (Loader.isModLoaded("xlfoodmod")) {
            addBreadItems("xlfoodmod:cheesy_bread", "xlfoodmod:potato_bread", "xlfoodmod:corn_bread", "xlfoodmod:rice_bread");
        }

        if (Loader.isModLoaded("gb")) {
            addBreadItem("gb:glob_bread");
        }

        if (Loader.isModLoaded("roots")) {
            addBreadItem("roots:wildewheet_bread");
        }

        for (String s : ConfigHandler.breadItems) {
            addBreadItem(s);
        }
    }

    private static void addBreadOre(String ore) {
        breadOres.add(ore);
    }

    private static void addBreadOres(String... ore) {
        breadOres.addAll(Arrays.asList(ore));
    }

    private static void addBreadItem(String id) {
        Item item = Item.getByNameOrId(id);

        if (item != null) {
            OreDictionary.registerOre("bread", new ItemStack(item));
        }
    }

    private static void addBreadItems(String... ids) {

        for (String s : ids) {
            addBreadItem(s);
        }
    }

    private static void addBreadItems(String id, int... metadata) {
        Item item = Item.getByNameOrId(id);

        if (item != null) {

            for (Integer meta : metadata) {
                OreDictionary.registerOre("bread", new ItemStack(item, 1, meta));
            }
        }
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
