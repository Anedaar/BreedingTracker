/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.BreedingTracker.src.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import flamefeed.BreedingTracker.src.client.EventLogger;
import flamefeed.BreedingTracker.src.client.breeding.SpeciesHandler;
import flamefeed.BreedingTracker.src.client.breeding.SpeciesHandler.TrackedSpecies;
import flamefeed.BreedingTracker.src.client.breeding.bees.BeeSpeciesHandler;
import flamefeed.BreedingTracker.src.client.breeding.butterflys.ButterflySpeciesHandler;
import flamefeed.BreedingTracker.src.client.breeding.trees.TreeSpeciesHandler;
import forestry.api.genetics.IAlleleSpecies;
import java.io.File;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

/**
 *
 * @author Anedaar
 */
public class BreedingJFrame extends javax.swing.JFrame {

    public static String configFolder;
    private static String configFile;
    private static final String modFolder = "BreedingTracker";
    private static final String localFolder = "local";
    private static final String remoteFolder = "server";
    private boolean fileUpdated=false;


    private static BreedingJFrame instance;
    private SpeciesHandler handler;
    private final MutationPanel panelMutation;

    public static BreedingJFrame getInstance() {
        if (BreedingJFrame.instance == null) {
            BreedingJFrame.instance = new BreedingJFrame();
        }
        return BreedingJFrame.instance;
    }

    /**
     * Creates new form BreedingJFrame
     */
    private BreedingJFrame() {
        initComponents();

        updateFileName(false);

        //Handlers
        SpeciesHandler newHandler;
        newHandler = new BeeSpeciesHandler(configFile + "bees.dat");
        this.comboSpeciesRoot.addItem(newHandler);
        handler = newHandler;
        newHandler = new TreeSpeciesHandler(configFile + "trees.dat");
        this.comboSpeciesRoot.addItem(newHandler);
        newHandler = new ButterflySpeciesHandler(configFile + "butterflies.dat");
        this.comboSpeciesRoot.addItem(newHandler);
        newHandler = null;
        fileUpdated=true;

        panelMutation = new MutationPanel(null);
        jScrollPane1.setViewportView(panelMutation);

        fillAllSpecies();

        handler.loadFromFile();

        updateOverview();

    }
    
    public void updateFileName(){
        updateFileName(true);
    }

    private void updateFileName(boolean updateHandlers) {
        if (fileUpdated)
            return;

        //fileNames
        configFile = configFolder + File.separator + modFolder + File.separator;
        String serverName = getServerName();

        if (FMLClientHandler.instance().getClient() != null && 
                FMLClientHandler.instance().getClient().getIntegratedServer() != null ) {
            configFile = configFile + localFolder + File.separator
                    + FMLClientHandler.instance().getClient().getIntegratedServer().getFolderName() + ".";
        } else if (serverName != null) {
            configFile = configFile + remoteFolder + File.separator + serverName + ".";
        } else {
            configFile = configFile + "default.";
        }

        EventLogger.log(Level.INFO, configFile);

        if (updateHandlers) {
            for (int i = 0; i < this.comboSpeciesRoot.getItemCount(); i++) {
                Object item = this.comboSpeciesRoot.getItemAt(i);
                if(item instanceof SpeciesHandler){
                    ((SpeciesHandler)item).setFile(configFile+((SpeciesHandler)item).fileName());
                    ((SpeciesHandler)item).loadFromFile();
                }
            }
            
            updateOverview();
            fillAllSpecies();
            fillToDoSpecies();
        }
    }

    private String getServerName() {
        try {
            ServerData serverData = null;
            Object serverDataObj = getServerData();
            if (serverDataObj != null) {
                serverData = (ServerData) serverDataObj;
            }
            if (serverData != null) {
//          EventLogger.logChat("serverName: "+serverData.serverName);
//          EventLogger.logChat("serverIP: "+serverData.serverIP);
//          EventLogger.logChat("serverMOTD: "+serverData.serverMOTD);
                return serverData.serverIP;
            }
        } catch (Exception e) {
        }
        return null;
    }

    private Object getServerData() {
        Minecraft mc = FMLClientHandler.instance().getClient();

        if (mc == null) {
            EventLogger.logChat("MC is null");
            return null;
        }

        Class minecraft = mc.getClass();
        Class fieldClasstype;
        try {
            fieldClasstype = Class.forName("net.minecraft.client.multiplayer.ServerData");
        } catch (ClassNotFoundException ex) {
            return null;
        }

        Field[] fields = minecraft.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fieldClasstype.equals(fields[i].getType())) {
                try {
                    fields[i].setAccessible(true);
                    return fields[i].get(mc);
                } catch (IllegalAccessException ex) {
                }
            }
        }
        return null;
    }

    public SpeciesHandler getHandler() {
        return handler;
    }

    public void setSpecies(TrackedSpecies species) {
        this.setTitle("Breeding Tracker: " + species.getSpecies().getName());
        panelMutation.setSpecies(species);
        this.revalidate();
        this.repaint();
    }

    public void updateOverview() {

        //TODO: Update overview values;
        if (handler == null) {
            return;
        }
        int max = handler.getNumSpecies();
        int cur = handler.getNumDone();
        barSpeciesProgress.setMaximum(max);
        barSpeciesProgress.setValue(cur);
        labelSpeciesProgress.setText("Progress: " + cur + "/" + max + " Species");
        labelToDo.setText("ToDo-List: " + handler.getNumToDo());
    }

    private void fillAllSpecies() {
        panelAllSpecies.removeAll();
        for (Map.Entry<IAlleleSpecies, TrackedSpecies> entry : handler.getSetSpecies()) {
            panelAllSpecies.add(new SpeciesPanel(entry.getValue()));
        }
        panelAllSpecies.revalidate();
        panelAllSpecies.repaint();
    }

    public void fillToDoSpecies() {
        panelToDo.removeAll();
        for (Map.Entry<IAlleleSpecies, TrackedSpecies> entry : handler.getSetToDo()) {
            panelToDo.add(new SpeciesPanel(entry.getValue()));
        }
        panelToDo.revalidate();
        panelToDo.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        comboSpeciesRoot = new javax.swing.JComboBox();
        tabbedPain = new javax.swing.JTabbedPane();
        panelOverview = new javax.swing.JPanel();
        barSpeciesProgress = new javax.swing.JProgressBar();
        labelSpeciesProgress = new javax.swing.JLabel();
        labelToDo = new javax.swing.JLabel();
        panelToDoTab = new javax.swing.JPanel();
        scrollToDo = new javax.swing.JScrollPane();
        panelToDo = new javax.swing.JPanel();
        panelAllSpeciesTab = new javax.swing.JPanel();
        scrollAllSpecies = new javax.swing.JScrollPane();
        panelAllSpecies = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        comboSpeciesRoot.setMaximumSize(new java.awt.Dimension(150, 20));
        comboSpeciesRoot.setMinimumSize(new java.awt.Dimension(50, 20));
        comboSpeciesRoot.setPreferredSize(new java.awt.Dimension(100, 20));
        comboSpeciesRoot.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboSpeciesRootItemStateChanged(evt);
            }
        });
        jPanel1.add(comboSpeciesRoot);

        tabbedPain.setMaximumSize(new java.awt.Dimension(250, 1000));
        tabbedPain.setMinimumSize(new java.awt.Dimension(250, 100));
        tabbedPain.setPreferredSize(new java.awt.Dimension(250, 300));
        tabbedPain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPainStateChanged(evt);
            }
        });

        barSpeciesProgress.setMaximum(2);
        barSpeciesProgress.setToolTipText("");
        barSpeciesProgress.setValue(1);
        barSpeciesProgress.setPreferredSize(new java.awt.Dimension(40, 14));

        labelSpeciesProgress.setText("Progress: 1/2 Species");

        labelToDo.setText("jLabel1");

        javax.swing.GroupLayout panelOverviewLayout = new javax.swing.GroupLayout(panelOverview);
        panelOverview.setLayout(panelOverviewLayout);
        panelOverviewLayout.setHorizontalGroup(
            panelOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOverviewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(barSpeciesProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelOverviewLayout.createSequentialGroup()
                        .addGroup(panelOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelSpeciesProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelToDo))
                        .addGap(0, 75, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelOverviewLayout.setVerticalGroup(
            panelOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOverviewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(barSpeciesProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelSpeciesProgress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelToDo)
                .addContainerGap(192, Short.MAX_VALUE))
        );

        tabbedPain.addTab("Overview", panelOverview);

        panelToDoTab.setLayout(new javax.swing.BoxLayout(panelToDoTab, javax.swing.BoxLayout.LINE_AXIS));

        panelToDo.setLayout(new javax.swing.BoxLayout(panelToDo, javax.swing.BoxLayout.PAGE_AXIS));
        scrollToDo.setViewportView(panelToDo);

        panelToDoTab.add(scrollToDo);

        tabbedPain.addTab("ToDo-List", panelToDoTab);

        panelAllSpeciesTab.setLayout(new javax.swing.BoxLayout(panelAllSpeciesTab, javax.swing.BoxLayout.LINE_AXIS));

        scrollAllSpecies.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollAllSpecies.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panelAllSpecies.setLayout(new javax.swing.BoxLayout(panelAllSpecies, javax.swing.BoxLayout.PAGE_AXIS));
        scrollAllSpecies.setViewportView(panelAllSpecies);

        panelAllSpeciesTab.add(scrollAllSpecies);

        tabbedPain.addTab("Complete Species List", panelAllSpeciesTab);

        jPanel1.add(tabbedPain);
        tabbedPain.getAccessibleContext().setAccessibleDescription("");

        getContentPane().add(jPanel1);
        getContentPane().add(jScrollPane1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabbedPainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPainStateChanged
        if (tabbedPain.getSelectedComponent() == panelOverview) {
            updateOverview();
        } else if (tabbedPain.getSelectedComponent() == panelToDoTab) {
            fillToDoSpecies();
        }
    }//GEN-LAST:event_tabbedPainStateChanged

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        handler.saveToFile();
        fileUpdated=false;
    }//GEN-LAST:event_formWindowClosed

    private void comboSpeciesRootItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboSpeciesRootItemStateChanged
        if (this.comboSpeciesRoot.getSelectedItem() instanceof SpeciesHandler) {
            handler = (SpeciesHandler) this.comboSpeciesRoot.getSelectedItem();
            updateOverview();
            fillAllSpecies();
            fillToDoSpecies();
        }
    }//GEN-LAST:event_comboSpeciesRootItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barSpeciesProgress;
    private javax.swing.JComboBox comboSpeciesRoot;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelSpeciesProgress;
    private javax.swing.JLabel labelToDo;
    private javax.swing.JPanel panelAllSpecies;
    private javax.swing.JPanel panelAllSpeciesTab;
    private javax.swing.JPanel panelOverview;
    private javax.swing.JPanel panelToDo;
    private javax.swing.JPanel panelToDoTab;
    private javax.swing.JScrollPane scrollAllSpecies;
    private javax.swing.JScrollPane scrollToDo;
    private javax.swing.JTabbedPane tabbedPain;
    // End of variables declaration//GEN-END:variables
}
