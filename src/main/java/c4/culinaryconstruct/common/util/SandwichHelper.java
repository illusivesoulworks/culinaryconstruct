/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.common.util;

import c4.culinaryconstruct.common.item.ItemSandwich;
import net.minecraft.block.BlockCake;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

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

    public static boolean isValidIngredient(ItemStack stack) {
        return (stack.getItem() instanceof ItemFood || (stack.getItem() instanceof ItemBlockSpecial
                && ((ItemBlockSpecial) stack.getItem()).getBlock() instanceof BlockCake))
                && !(stack.getItem() instanceof ItemSandwich && NBTHelper.getDepth(stack) >= ConfigHandler.maxSandwichNesting)
		&& !SandwichHelper.isBlacklistedIngredient(stack);
    }

    public static boolean isBlacklistedIngredient(ItemStack stack) {
        Item item = stack.getItem();
        int food = 0;
        double saturation = 0.0D;
        boolean blacklisted = false;

        if (item instanceof ItemFood) {
            ItemFood itemFood = (ItemFood)item;
            food = itemFood.getHealAmount(stack);
            saturation = itemFood.getSaturationModifier(stack);
        } else if (stack.getItem() instanceof ItemBlockSpecial && ((ItemBlockSpecial) stack.getItem()).getBlock()
                instanceof BlockCake) {
            food = 14;
            saturation = 2.8D;
        }

        if (ConfigHandler.maxFood >= 0) {
            blacklisted = food > ConfigHandler.maxFood;
        }

        if (ConfigHandler.maxSaturation >= 0) {
            blacklisted = blacklisted || saturation > ConfigHandler.maxSaturation;
        }
        return blacklisted || SandwichHelper.blacklist.contains(item);
    }
}
