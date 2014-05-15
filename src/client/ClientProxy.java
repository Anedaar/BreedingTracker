/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.BreedingTracker.src.client;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import flamefeed.BreedingTracker.src.client.breeding.bees.BeeSpeciesHandler;
import flamefeed.BreedingTracker.src.server.ServerProxy;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author Anedaar
 */
public class ClientProxy extends ServerProxy {

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        KeyBinding[] key = {new KeyBinding("Show Bee Window", Keyboard.KEY_B)};
        boolean[] repeat = {false};
        KeyBindingRegistry.registerKeyBinding(new ClientKeyHandler(key, repeat));
    }
}
