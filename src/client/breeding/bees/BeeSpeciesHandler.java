/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flamefeed.BreedingTracker.src.client.breeding.bees;

import flamefeed.BreedingTracker.src.client.breeding.SpeciesHandler;
import forestry.api.genetics.AlleleManager;

/**
 *
 * @author Anedaar
 */
public class BeeSpeciesHandler extends SpeciesHandler{

    public BeeSpeciesHandler(String saveFile) {
        super(AlleleManager.alleleRegistry.getSpeciesRoot("rootBees"),saveFile);
        super.init();
    }
    
    @Override
    public String toString(){
        return "Bees";
    }
    
    @Override
    public String fileName(){
        return "bees.dat";
    }
}
