/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.proxy;

import c4.culinaryconstruct.CulinaryConstruct;
import c4.culinaryconstruct.client.GuiHandler;
import c4.culinaryconstruct.common.block.BlockSandwichStation;
import c4.culinaryconstruct.common.item.ItemSandwich;
import c4.culinaryconstruct.common.tileentity.TileEntitySandwichStation;
import c4.culinaryconstruct.common.util.BreadHelper;
import c4.culinaryconstruct.common.util.SandwichHelper;
import c4.culinaryconstruct.network.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class CommonProxy {

    @GameRegistry.ObjectHolder("culinaryconstruct:sandwich_station")
    public static BlockSandwichStation sandwichStation;

    @GameRegistry.ObjectHolder("culinaryconstruct:sandwich")
    public static ItemSandwich sandwich;

    public void preInit(FMLPreInitializationEvent evt) {
        //NO-OP
    }

    public void init(FMLInitializationEvent evt) {
        NetworkRegistry.INSTANCE.registerGuiHandler(CulinaryConstruct.instance, new GuiHandler());
        NetworkHandler.register();
    }

    public void postInit(FMLPostInitializationEvent evt) {
        BreadHelper.initOreDict();
        BreadHelper.initBlacklist();
        SandwichHelper.initBlacklist();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> evt) {
        evt.getRegistry().register(new BlockSandwichStation());
        GameRegistry.registerTileEntity(TileEntitySandwichStation.class, new ResourceLocation(CulinaryConstruct.MODID, "sandwich_station"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> evt) {
        evt.getRegistry().register(new ItemBlock(sandwichStation).setRegistryName("sandwich_station"));
        evt.getRegistry().register(new ItemSandwich());
    }
}
