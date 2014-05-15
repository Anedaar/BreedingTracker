/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.BreedingTracker.src.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import flamefeed.BreedingTracker.src.client.gui.BreedingJFrame;
import java.util.EnumSet;
import net.minecraft.client.settings.KeyBinding;

/**
 *
 * @author Anedaar
 */
public class ClientKeyHandler extends KeyHandler {

    private final EnumSet tickTypes = EnumSet.of(TickType.CLIENT);

    public ClientKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings) {
        super(keyBindings, repeatings);
    }

    @Override
    public String getLabel() {
        return "KeyHandler";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
        if (tickEnd && FMLClientHandler.instance().getClient().currentScreen == null) {

    //Schedule a job for the event-dispatching thread:
            //creating and showing this application's GUI.
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        BreedingJFrame.getInstance().setTitle("Breeding Tracker");
                        BreedingJFrame.getInstance().setVisible(true);
//                        window.pack();
                    }
                });
        }
    }

    @Override
    public EnumSet<TickType> ticks() {
        return tickTypes;
    }

}
