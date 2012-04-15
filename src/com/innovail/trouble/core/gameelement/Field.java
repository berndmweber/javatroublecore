/**
 * @file:   com.innovail.trouble.core.gameelement - Field.java
 * @date:   Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core.gameelement;

import java.util.Vector;

import com.innovail.trouble.core.Settings;
import com.innovail.trouble.core.gameelement.Spot.Attributes;

/**
 * 
 */
public class Field {
    private Vector<Spot> _spots;
    
    private static Field instance;
    
    private Field ()
    {}

    public static Field createField (Vector<Player> players)
    {
        if (instance == null) {
            instance = new Field ();
        }
        instance._spots = new Vector<Spot> ();

        if ((players != null) && (players.size () > 0)) {
            Spot lastNormalSpot = null;
            Spot firstTurnOutSpot = null;
            for (int i = 0; i < players.size (); i++) {
                Spot[] homeSpot = new Spot[Settings.getInstance ().getNumberOfTokensPerPlayer(players.size ())];
                for (int j = 0; j < homeSpot.length; j++) {
                    homeSpot[j] = Spot.createSpot (Attributes.SPOT_IS_HOME, players.get (i));
                    if (j > 0) {
                        homeSpot[j-1].setNextSpot (homeSpot[j]);
                    }
                    instance._spots.add (homeSpot[j]);
                }
                Spot startSpot = Spot.createSpot (Attributes.SPOT_IS_START, players.get (i));
                instance._spots.lastElement ().setNextSpot (startSpot);
                instance._spots.add (startSpot);
                Spot[] normalSpot = new Spot[9];
                /* TODO: Need to get the number of normal spots from settings. */
                for (int j = 0; j < normalSpot.length; j++) {
                    normalSpot[j] = Spot.createSpot (Attributes.SPOT_IS_NORMAL);
                    if (j > 0) {
                        normalSpot[j-1].setNextSpot (normalSpot[j]);
                    } else {
                        startSpot.setNextSpot (normalSpot[j]);
                    }
                    instance._spots.add (normalSpot[j]);
                }
                Spot turnOutSpot = Spot.createSpot (Attributes.SPOT_IS_TURN_OUT, players.get (i));
                turnOutSpot.setNextSpot (startSpot);
                instance._spots.add (turnOutSpot);
                if (i == 0) {
                    firstTurnOutSpot = turnOutSpot;
                }
                Spot[] finishSpot = new Spot[Settings.getInstance ().getNumberOfTokensPerPlayer(players.size ())];
                for (int j = 0; j < finishSpot.length; j++) {
                    finishSpot[j] = Spot.createSpot (Attributes.SPOT_IS_FINISH, players.get (i));
                    if (j > 0) {
                        finishSpot[j-1].setNextSpot (finishSpot[j]);
                    } else {
                        turnOutSpot.setNextTurnOutSpot (finishSpot[j]);
                    }
                    instance._spots.add (finishSpot[j]);
                 }
                if (i > 0) {
                    lastNormalSpot.setNextSpot (turnOutSpot);
                }
                lastNormalSpot = normalSpot[normalSpot.length - 1];
                if (i == players.size () - 1) {
                    lastNormalSpot.setNextSpot (firstTurnOutSpot);
                }
            }
        }
        
        return instance;
    }
}
