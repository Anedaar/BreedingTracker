/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flamefeed.BreedingTracker.src.client.breeding;

/**
 *
 * @author Anedaar
 */
public interface ITrackedSpeciesListener {
    public enum Value {STEPS, FOUND, SERUM, TODO};
    
    void speciesUpdated(Value v);
}
