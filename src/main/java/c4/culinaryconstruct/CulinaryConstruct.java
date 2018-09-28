/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct;

import c4.culinaryconstruct.debug.CommandHunger;
import c4.culinaryconstruct.proxy.CommonProxy;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@Mod(   modid = CulinaryConstruct.MODID,
        name = CulinaryConstruct.NAME,
        version = CulinaryConstruct.VERSION,
        dependencies = "required-after:forge@[14.23.4.2705,)",
        acceptedMinecraftVersions = "[1.12.2, 1.13)",
        certificateFingerprint = "5d5b8aee896a4f5ea3f3114784742662a67ad32f")
public class CulinaryConstruct
{
    public static final String MODID = "culinaryconstruct";
    public static final String NAME = "Culinary Construct";
    public static final String VERSION = "1.2.0.1";

    private static final boolean DEBUG = false;

    @SidedProxy(clientSide = "c4.culinaryconstruct.proxy.ClientProxy", serverSide = "c4.culinaryconstruct.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Instance
    public static CulinaryConstruct instance;

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        logger = evt.getModLog();
        proxy.preInit(evt);
    }

    @EventHandler
    public void init(FMLInitializationEvent evt) {
        proxy.init(evt);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        proxy.postInit(evt);
    }

    @EventHandler
    public void onFingerPrintViolation(FMLFingerprintViolationEvent evt) {
        FMLLog.log.log(Level.ERROR, "Invalid fingerprint detected! The file " + evt.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent evt) {
        if (DEBUG) {
            evt.registerServerCommand(new CommandHunger());
        }
    }
}
