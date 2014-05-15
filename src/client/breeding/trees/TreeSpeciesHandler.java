/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flamefeed.BreedingTracker.src.client.breeding.trees;

import flamefeed.BreedingTracker.src.client.breeding.SpeciesHandler;
import forestry.api.genetics.AlleleManager;

/**
 *
 * @author Anedaar
 */
public class TreeSpeciesHandler extends SpeciesHandler{

    public TreeSpeciesHandler(String saveFile) {
        super(AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees"),saveFile);
        super.init();
    }

    @Override
    public String toString() {
        return "Trees";
    }
    
}
