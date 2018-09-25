/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.common.item;

import c4.culinaryconstruct.CulinaryConstruct;
import c4.culinaryconstruct.client.model.ModelSandwich;
import c4.culinaryconstruct.common.util.NBTHelper;
import c4.culinaryconstruct.common.util.SandwichHelper;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ItemSandwich extends ItemFood {

    private static final Method ON_FOOD_EATEN = ReflectionHelper.findMethod(ItemFood.class, "onFoodEaten", "func_77849_c", ItemStack.class, World.class, EntityPlayer.class);

    public ItemSandwich() {
        super(0, false);
        this.setRegistryName("sandwich");
        this.setTranslationKey(CulinaryConstruct.MODID + ".sandwich");
        this.setCreativeTab(CreativeTabs.FOOD);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomMeshDefinition(this, stack -> ModelSandwich.LOCATION);
        ModelBakery.registerItemVariants(this, ModelSandwich.LOCATION);
    }

    @Override
    public int getHealAmount(ItemStack stack)
    {
        return NBTHelper.getFoodAmount(stack);
    }

    @Override
    public float getSaturationModifier(ItemStack stack)
    {
        return NBTHelper.getSaturationModifier(stack);
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        StringBuilder fullName = new StringBuilder();
        NonNullList<ItemStack> ingredients = NBTHelper.getIngredientsList(stack, false);

        if (!ingredients.isEmpty()) {
            Map<String, Long> countMap = ingredients.stream().collect(Collectors.groupingBy(ItemStack::getDisplayName, Collectors
                    .counting()));
            List<String> names = new ArrayList<>();
            if (!countMap.isEmpty()) {
                for (String name : countMap.keySet()) {
                    long size = countMap.get(name);
                    StringBuilder builder = new StringBuilder();

                    if (size > 1L) {
                        builder.append(I18n.format("tooltip.culinaryconstruct.count." + size));
                        builder.append(" ");
                    }
                    builder.append(name);
                    names.add(builder.toString());
                }
            }
            fullName.append(I18n.format("tooltip.culinaryconstruct.list." + names.size(), names.toArray()).trim());
            fullName.append(" ");
        }
        fullName.append(I18n.format(this.getUnlocalizedNameInefficiently(stack) + ".name").trim());
        return fullName.toString();
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        NonNullList<ItemStack> ingredients = NBTHelper.getIngredientsList(stack, true);
        int bonus = NBTHelper.getBonus(stack);
        tooltip.add(String.format("%s: %s", I18n.format("tooltip.culinaryconstruct.quality.name"), I18n.format("tooltip.culinaryconstruct.quality." + (bonus + 2))));
        tooltip.add("");
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            tooltip.add(TextFormatting.UNDERLINE + I18n.format("tooltip.culinaryconstruct.ingredients.name"));
            for (ItemStack ing : ingredients) {
                if (!ing.isEmpty()) {
                    tooltip.add("- " + ing.getDisplayName());
                }
            }
        } else {
            tooltip.add(I18n.format("tooltip.culinaryconstruct.ingredients"));
        }
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            ItemStack sub = new ItemStack(this);
            NBTHelper.setTagSize(sub, 5);
            NBTHelper.setIngredientsList(sub, NonNullList.from(ItemStack.EMPTY,
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(Items.BREAD)));
            NBTHelper.setTagFood(sub, 20);
            NBTHelper.setTagSaturation(sub, 1.0F);
            NBTHelper.setTagBonus(sub, 2);
            items.add(sub);
        }
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, @Nonnull EntityPlayer player)
    {
        if (!worldIn.isRemote)
        {
            NonNullList<ItemStack> ingredients = NBTHelper.getIngredientsList(stack, true);
            for (ItemStack ing : ingredients) {
                if (!ing.isEmpty() && ing.getItem() instanceof ItemFood) {
                    try {
                        ItemFood foodItem = (ItemFood) ing.getItem();
                        ON_FOOD_EATEN.invoke(foodItem, ing, worldIn, player);
                    } catch (Exception e) {
                        CulinaryConstruct.logger.log(Level.ERROR, "Error invoking onFoodEaten for stack " + ing.toString());
                    }
                }
            }
        }
    }
}
