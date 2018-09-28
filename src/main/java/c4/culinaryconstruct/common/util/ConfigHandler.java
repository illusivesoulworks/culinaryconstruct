/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.common.util;

import c4.culinaryconstruct.CulinaryConstruct;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = CulinaryConstruct.MODID)
public class ConfigHandler {

    @Name("Max Ingredient Saturation")
    @Comment("Blacklist ingredients with more than this max saturation modifier, -1 to disable")
    public static double maxSaturation = -1.0D;

    @Name("Max Ingredient Food")
    @Comment("Blacklist ingredients with more than this max food value, -1 to disable")
    public static int maxFood = -1;

    @Name("Blacklist")
    @Comment("List of items to blacklist as sandwich ingredients")
    @RequiresMcRestart
    public static String[] blacklist = new String[]{};

    @Mod.EventBusSubscriber(modid = CulinaryConstruct.MODID)
    private static class ConfigEventHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {
            if (evt.getModID().equals(CulinaryConstruct.MODID)) {
                ConfigManager.sync(CulinaryConstruct.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
