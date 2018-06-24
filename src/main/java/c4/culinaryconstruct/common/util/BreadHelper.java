/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.common.util;

import c4.culinaryconstruct.api.BreadRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class BreadHelper {

    public static void initBreadRegistry() {
        BreadRegistry.registerBread(new ItemStack(Items.BREAD));

        if (Loader.isModLoaded("harvestcraft")) {
            String s = "harvestcraft:";
            BreadRegistry.registerBread(s + "pumpkinbreaditem");
            BreadRegistry.registerBread(s + "gingerbreaditem");
            BreadRegistry.registerBread(s + "garlicbreaditem");
            BreadRegistry.registerBread(s + "zucchinibreaditem");
            BreadRegistry.registerBread(s + "walnutraisinbreaditem");
            BreadRegistry.registerBread(s + "banananutbreaditem");
            BreadRegistry.registerBread(s + "datenutbreaditem");
            BreadRegistry.registerBread(s + "fairybreaditem");
            BreadRegistry.registerBread(s + "honeybreaditem");
        }
    }
}
