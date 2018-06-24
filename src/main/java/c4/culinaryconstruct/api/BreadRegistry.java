/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.api;

import c4.culinaryconstruct.CulinaryConstruct;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BreadRegistry {

    private static final Set<ItemStack> breads = new HashSet<>();

    public static void registerBread(String name) {
        registerBread(name, 0);
    }

    public static void registerBread(String name, int metadata) {
        Item item = Item.getByNameOrId(name);
        if (item != null) {
            if (metadata > 0) {
                breads.add(new ItemStack(item, 1, metadata));
            } else {
                breads.add(new ItemStack(item));
            }
        } else {
            CulinaryConstruct.logger.log(Level.ERROR, "Could not find item " + name + " for bread registry!");
        }
    }

    public static void registerBread(@Nonnull ItemStack bread) {
        breads.add(bread);
    }

    public static boolean isValidBread(ItemStack stack) {
        for (ItemStack bread : getBreads()) {
            if (!bread.isEmpty() && bread.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == bread.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, bread)) {
                return true;
            }
        }
        return false;
    }

    public static ImmutableList<ItemStack> getBreads() {
        return ImmutableList.copyOf(breads);
    }
}
