/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.common.util;

import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class SandwichHelper {

    public static List<Item> blacklist;

    public static void initBlacklist() {
        String[] config = ConfigHandler.blacklist;
        blacklist = new ArrayList<>();
        for (String s : config) {
            Item item = Item.getByNameOrId(s);
            if (item != null) {
                blacklist.add(item);
            }
        }
    }
}
