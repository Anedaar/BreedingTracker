/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flamefeed.BreedingTracker.src.client.breeding.butterflys;

import flamefeed.BreedingTracker.src.client.breeding.SpeciesHandler;
import forestry.api.genetics.AlleleManager;

/**
 *
 * @author Anedaar
 */
public class ButterflySpeciesHandler extends SpeciesHandler{

    public ButterflySpeciesHandler(String saveFile) {
        super(AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies"),saveFile);
        super.init();
    }
    
    @Override
    public String toString(){
        return "Butterflies";
    }
    
    @Override
    public String fileName(){
        return "butterflies.dat";
    }
    
}
