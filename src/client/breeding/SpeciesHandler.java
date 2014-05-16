/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.BreedingTracker.src.client.breeding;

import flamefeed.BreedingTracker.src.client.EventLogger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anedaar
 */
public abstract class SpeciesHandler {

    private final ISpeciesRoot speciesRoot;
    private final Map<IAlleleSpecies, TrackedSpecies> species;
    public final Set<TrackedSpecies> toDo;
    private String saveFile;
    private boolean init = false;

    public SpeciesHandler(ISpeciesRoot root, String save) {
        this.species = new TreeMap(new AlleleComparator());
        this.toDo = new TreeSet();
        this.speciesRoot = root;
        this.saveFile = save;
    }

    @Override
    public abstract String toString();

    public abstract String fileName();

    public void init() {

        Collection<IMutation> mutations = this.speciesRoot.getMutations(true);

        for (IMutation mutation : mutations) {
            TrackedMutation mut = new TrackedMutation(mutation);
            getSpecies(mut.parent1);
            getSpecies(mut.parent2);
            getSpecies(mut.result).addMutation(mut);

        }

        EventLogger.logInfo("%s mutations found.", new Object[]{mutations.size()});

        for (Map.Entry entry : this.species.entrySet()) {
            ((TrackedSpecies) entry.getValue()).updateTier();
        }

        EventLogger.logInfo("%s species found.", new Object[]{species.size()});

        init = true;
    }

    public void setFile(String name) {
        this.saveFile = name;
    }

    public void loadFromFile() {
        try {
            if (!init) {
                return;
            }

            for (Map.Entry entry : this.species.entrySet()) {
                ((TrackedSpecies) entry.getValue()).setFound(false);
                ((TrackedSpecies) entry.getValue()).setSerum(false);
                ((TrackedSpecies) entry.getValue()).setToDo(false);
            }

            BufferedReader br;
            String line;
            String[] value;

            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(saveFile), Charset.forName("UTF-8")));
            TrackedSpecies spec;

            while ((line = br.readLine()) != null) {
                EventLogger.logInfo("Read: %s", new Object[]{line});
                value = line.split(",");
                if (value.length >= 4) {
                    spec = getSpecies(value[0]);
                    if (spec != null) {
                        spec.set(value);
                    } else {
                        EventLogger.log(Level.INFO, "Species not found");
                    }
                }

            }

            br.close();
            br = null;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SpeciesHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SpeciesHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveToFile() {
        PrintWriter writer = null;
        EventLogger.logInfo("Saving to File: %s", new Object[]{saveFile});
        try {
            new File(saveFile).getParentFile().mkdirs();
            writer = new PrintWriter(saveFile, "UTF-8");

            String line;
            for (Map.Entry entry : this.species.entrySet()) {
                line = ((TrackedSpecies) entry.getValue()).serialize();
                if (line != null) {
                    writer.println(line);
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SpeciesHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SpeciesHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            writer.close();
        }
    }

    public TrackedSpecies getSpecies(IAlleleSpecies s) {
        if (species.get(s) == null) {
            species.put(s, new TrackedSpecies(s, this));
        }

        return species.get(s);

    }

    public TrackedSpecies getSpecies(String name) {
        for (Map.Entry entry : species.entrySet()) {
            if (name.equals(((IAlleleSpecies) entry.getKey()).getName())) {
                return species.get((IAlleleSpecies) entry.getKey());
            }
        }
        return null;
    }

    public int getNumSpecies() {
        return species.size();
    }

    public Set<Entry<IAlleleSpecies, TrackedSpecies>> getSetSpecies() {
        return species.entrySet();
    }

    public int getNumDone() {
        int done = 0;

        for (Entry<IAlleleSpecies, TrackedSpecies> entry : species.entrySet()) {
            if (entry.getValue().found == true) {
                ++done;
            }
        }
        return done;
    }

    public Set<Entry<IAlleleSpecies, TrackedSpecies>> getSetDone() {
        Set done = new HashSet<Entry<IAlleleSpecies, TrackedSpecies>>();

        for (Entry<IAlleleSpecies, TrackedSpecies> entry : species.entrySet()) {
            if (entry.getValue().found == true) {
                done.add(entry);
            }
        }
        return done;
    }

    public int getNumToDo() {
        int done = 0;

        for (Entry<IAlleleSpecies, TrackedSpecies> entry : species.entrySet()) {
            if (entry.getValue().toDo == true) {
                ++done;
            }
        }
        return done;
    }

    public Set<Entry<IAlleleSpecies, TrackedSpecies>> getSetToDo() {
        Set done = new HashSet<Entry<IAlleleSpecies, TrackedSpecies>>();

        for (Entry<IAlleleSpecies, TrackedSpecies> entry : species.entrySet()) {
            if (entry.getValue().toDo == true) {
                done.add(entry);
            }
        }
        return done;
    }

    private class AlleleComparator implements Comparator<IAlleleSpecies> {

        @Override
        public int compare(IAlleleSpecies o1, IAlleleSpecies o2) {
            return o1.getName().compareTo(o2.getName());
        }

    }

    public class TrackedSpecies implements ITrackedSpeciesListener, Comparable<TrackedSpecies> {

        private final IAlleleSpecies species;
        private final SpeciesHandler handler;

        private ArrayList<SpeciesHandler.TrackedMutation> mutations;

        //Species collected in the world have tier 0, others have max(p1.tier,p2.tier)+1
        private int tier;

        //Found Species have distance 0, others have max(p1.distance,p2.distance)+1
        private int steps;

        private boolean found;
        private boolean serum;
        private boolean toDo;

        private final HashSet<ITrackedSpeciesListener> eventListeners = new HashSet();

        public TrackedSpecies(IAlleleSpecies species, SpeciesHandler handler) {
            this.species = species;
            this.handler = handler;
            this.mutations = new ArrayList<SpeciesHandler.TrackedMutation>();

            this.tier = -1;
            this.steps = -1;

            this.found = false;
            this.serum = false;
        }

        public IAlleleSpecies getSpecies() {
            return species;
        }

        public SpeciesHandler getHandler() {
            return handler;
        }

        public int getTier() {
            return tier;
        }

        public void updateTier() {
            if (this.tier != -1) {
                return;
            }

            this.tier = 0;

            for (SpeciesHandler.TrackedMutation mutation : this.mutations) {
                //update parents
                handler.getSpecies(mutation.parent1).updateTier();
                handler.getSpecies(mutation.parent2).updateTier();

                //tier from this mutation
                int newTier = Math.max(handler.getSpecies(mutation.parent1).tier,
                        handler.getSpecies(mutation.parent2).tier) + 1;

                if (this.tier == 0) {
                    this.tier = newTier;
                } else {
                    this.tier = Math.min(this.tier, newTier);
                }

            }
        }

        public int getSteps() {
            return steps;
        }

        public boolean isFound() {
            return found;
        }

        public void setFound(boolean found) {
            if (found == this.found) {
                return;
            }

            this.found = found;
            updated(Value.FOUND);

            if (found == true && this.steps != 0) {
                //newly found, notify childs
                this.steps = 0;
                updated(Value.STEPS);
            } else if (found == false && this.steps == 0) {
                //not found anymore, update the own steps
                speciesUpdated(Value.STEPS);
            }
        }

        public boolean isSerum() {
            return serum;
        }

        public void setSerum(boolean serum) {
            if (this.serum != serum) {
                this.serum = serum;
                updated(Value.SERUM);
            }
        }

        public boolean isToDo() {
            return toDo;
        }

        public void setToDo(boolean toDo) {
            if (this.toDo != toDo) {
                this.toDo = toDo;
                updated(Value.TODO);
            }
        }

        public void addMutation(TrackedMutation mut) {
            mutations.add(mut);
            handler.getSpecies(mut.parent1).register(this);
            handler.getSpecies(mut.parent2).register(this);
        }

        public TrackedMutation getPreferredMutation() {
            if (this.found) {
                return null;
            }

            TrackedMutation preferred = null;
            int actualSteps = 0;

            for (SpeciesHandler.TrackedMutation mutation : this.mutations) {

                //steps from this mutation
                int newSteps = Math.max(handler.getSpecies(mutation.parent1).getSteps(),
                        handler.getSpecies(mutation.parent2).getSteps()) + 1;

                if (preferred == null || newSteps < actualSteps) {
                    preferred = mutation;
                    actualSteps = newSteps;
                }

            }

            return preferred;

        }

        public void register(ITrackedSpeciesListener listener) {
            eventListeners.add(listener);
        }

        public void unregister(ITrackedSpeciesListener listener) {
            eventListeners.remove(listener);
        }

        private void updated(Value v) {
            for (ITrackedSpeciesListener listener : eventListeners) {
                listener.speciesUpdated(v);
            }
        }

        @Override
        public void speciesUpdated(Value v) {
            if (v == Value.STEPS) {

                //species already found, steps is 0
                if (this.found) {
                    return;
                }

                int oldSteps = this.steps;

                //assign a value that can never occur
                this.steps = -1;

                for (SpeciesHandler.TrackedMutation mutation : this.mutations) {

                    //steps from this mutation
                    int newSteps = Math.max(handler.getSpecies(mutation.parent1).getSteps(),
                            handler.getSpecies(mutation.parent2).getSteps()) + 1;

                    if (this.steps == -1) {
                        //no previous mutation found
                        this.steps = newSteps;
                    } else {
                        this.steps = Math.min(this.steps, newSteps);
                    }

                }

                //no mutation found at all: collect that species is the first step
                if (this.steps == -1) {
                    this.steps = 1;
                }

                //if steps has changed, fire the event to childrens
                if (this.steps != oldSteps) {
                    updated(Value.STEPS);
                }
            }

        }

        @Override
        public int compareTo(TrackedSpecies o) {
            return this.getSpecies().getName().compareTo(o.getSpecies().getName());
        }

        private String serialize() {
            String data = this.found + ","
                    + this.serum + ","
                    + this.toDo;
            if (data.equals("false,false,false")) {
//                EventLogger.logInfo("%s species will not be saved.", new Object[]{this.species.getName()});
                return null;
            } else {
//                EventLogger.logInfo("Saving: %s,%s", new Object[]{this.species.getName(), data});
                return this.species.getName() + "," + data;
            }
        }

        private void set(String[] value) {
            if (value.length < 4) {
                return;
            }

            setFound(Boolean.parseBoolean(value[1]));
            setSerum(Boolean.parseBoolean(value[2]));
            setToDo(Boolean.parseBoolean(value[3]));
        }
    }

    public class TrackedMutation {

        public IAlleleSpecies parent1;
        public IAlleleSpecies parent2;
        public IAlleleSpecies result;
        public float chance;
        public Collection<String> requirements;

        public TrackedMutation(IMutation mutation) {
            this.parent1 = (IAlleleSpecies) mutation.getAllele0();
            this.parent2 = (IAlleleSpecies) mutation.getAllele1();
            this.result = (IAlleleSpecies) mutation.getTemplate()[0];
            this.chance = mutation.getBaseChance();

            this.requirements = mutation.getSpecialConditions();
        }

    }

}
