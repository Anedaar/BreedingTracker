/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flamefeed.BreedingTracker.src.client.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 *
 * @author Anedaar
 */
public class SpeciesPanelLayout implements LayoutManager{
    
    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return new Dimension(100, 300);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(100, 300);
    }

    @Override
    public void layoutContainer(Container parent) {
        boolean laidOut = false;
        for (Component child : parent.getComponents()) {
            if (child.isVisible() && !laidOut) {
                child.setLocation(200, 100);
                child.setSize(child.getPreferredSize());
            } else {
                child.setSize(0, 0);
            }
        }
    }

}
  
