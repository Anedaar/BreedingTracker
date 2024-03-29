/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.BreedingTracker.src.client.gui;

import flamefeed.BreedingTracker.src.client.breeding.ITrackedSpeciesListener;
import flamefeed.BreedingTracker.src.client.breeding.SpeciesHandler.TrackedMutation;
import flamefeed.BreedingTracker.src.client.breeding.SpeciesHandler.TrackedSpecies;

/**
 *
 * @author Anedaar
 */
public class MutationPanel extends javax.swing.JPanel implements ITrackedSpeciesListener {

    TrackedSpecies species;

    /**
     * Creates new form MutationPanel
     *
     * @param spec the species this Panel shows
     */
    public MutationPanel(TrackedSpecies spec) {
        initComponents();
        setSpecies(spec);
    }

    public final void setSpecies(TrackedSpecies spec) {
        if (species == spec) {
            return;
        }

        if (species != null) {
            species.unregister(this);
        }

        species = spec;

        panelChild.removeAll();
        SpeciesPanel content = new SpeciesPanel(species);
        content.setVisible(true);
        panelChild.add(content);

        if (species != null) {
            species.register(this);
        }

        updateParents();

        this.revalidate();
        this.repaint();

    }

    private void updateParents() {
        TrackedMutation mutation = null;
        
        if (species != null) {
            mutation = species.getPreferredMutation();
        }

        this.panelParent1.removeAll();
        this.panelParent2.removeAll();

        if (mutation != null) {
            this.panelParent1.add(new MutationPanel(species.getHandler().getSpecies(mutation.parent1)));
            this.panelParent2.add(new MutationPanel(species.getHandler().getSpecies(mutation.parent2)));
        }
        this.revalidate();
        this.repaint();

    }

    @Override
    public void speciesUpdated(Value v) {
        if (v != Value.FOUND) {
            return;
        }

        updateParents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelChild = new javax.swing.JPanel();
        panelParents = new javax.swing.JPanel();
        panelParent1 = new javax.swing.JPanel();
        panelParent2 = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        panelChild.setLayout(new javax.swing.BoxLayout(panelChild, javax.swing.BoxLayout.LINE_AXIS));
        add(panelChild);

        panelParents.setLayout(new javax.swing.BoxLayout(panelParents, javax.swing.BoxLayout.LINE_AXIS));

        panelParent1.setLayout(new javax.swing.BoxLayout(panelParent1, javax.swing.BoxLayout.PAGE_AXIS));
        panelParents.add(panelParent1);

        panelParent2.setLayout(new javax.swing.BoxLayout(panelParent2, javax.swing.BoxLayout.PAGE_AXIS));
        panelParents.add(panelParent2);

        add(panelParents);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panelChild;
    private javax.swing.JPanel panelParent1;
    private javax.swing.JPanel panelParent2;
    private javax.swing.JPanel panelParents;
    // End of variables declaration//GEN-END:variables

}
