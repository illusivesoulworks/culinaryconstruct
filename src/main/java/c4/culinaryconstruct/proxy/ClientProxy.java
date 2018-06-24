/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.proxy;

import c4.culinaryconstruct.CulinaryConstruct;
import c4.culinaryconstruct.client.model.ModelSandwich;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public static void registerTextures(TextureStitchEvent.Pre evt) {
        TextureMap map = evt.getMap();
        for (int i = 0; i < 5; i++) {
            map.registerSprite(new ResourceLocation(CulinaryConstruct.MODID, "items/bread" + i));
            map.registerSprite(new ResourceLocation(CulinaryConstruct.MODID, "items/layer" + i));
        }
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent evt) {

        ModelLoaderRegistry.registerLoader(ModelSandwich.LoaderSandwich.INSTANCE);
        sandwichStation.initModel();
        sandwich.initModel();
    }
}
