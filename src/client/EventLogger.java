/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.BreedingTracker.src.client;

import cpw.mods.fml.client.FMLClientHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import cpw.mods.fml.common.FMLLog;

/**
 *
 * @author Anedaar
 */
public class EventLogger {

    private static final Logger logger = Logger.getLogger("BreedingTracker");

    public static void init() {
        logger.setParent(FMLLog.getLogger());
    }

    public static void logWarning(String message, Object[] params) {
        logger.log(Level.WARNING, String.format(message, params));
    }

    public static void logInfo(String message, Object[] params) {
        logger.log(Level.INFO, String.format(message, params));
    }

    public static void log(Level logLevel, String message) {
        logger.log(logLevel, message);
    }

    public static void logChat(String message) {
        try {
            FMLClientHandler.instance().getClient().thePlayer.addChatMessage(message);
        } catch (NullPointerException ex) {
        }
    }
}
