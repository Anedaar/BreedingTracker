/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.BreedingTracker.src;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import flamefeed.BreedingTracker.src.client.gui.BreedingJFrame;
import flamefeed.BreedingTracker.src.server.ServerProxy;

/**
 *
 * @author Anedaar
 */
@Mod(modid = "BreedingTracker", name = "Forestry Breeding Tracker", version = "0.1")
@NetworkMod(channels = {}, clientSideRequired = false, serverSideRequired = false)

public class BreedingTracker {

    @SidedProxy(serverSide = "flamefeed.BreedingTracker.src.server.ServerProxy", clientSide = "flamefeed.BreedingTracker.src.client.ClientProxy")
    public static ServerProxy proxy;

    @EventHandler
    @SideOnly(Side.CLIENT)
    public static void preInit(FMLPreInitializationEvent event) {
        
    BreedingJFrame.configFolder = event.getSuggestedConfigurationFile().getParent();
    }

    @EventHandler
    @SideOnly(Side.CLIENT)
    public static void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        
    }
       
  
//
//    @EventHandler
//    public void serverLoad(FMLServerStartingEvent event) {
//        proxy.serverLoad(event);
//    }

}
