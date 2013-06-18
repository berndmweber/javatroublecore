/**
 * @file:   com.innovail.trouble.core.gameelement - Field.java
 * @date:   Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core.gameelement;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;

import com.innovail.trouble.core.GameSettings;
import com.innovail.trouble.core.gameelement.Spot.Attributes;

/**
 * 
 */
public final class Field {
    private List <Spot> _spots;

    private static Field instance;

    /* This will only work for 4 players right now... */
    private static Vector3[] _spotPosition = {
                                        new Vector3 (0, 0, 0), //0 HP P0
                                        new Vector3 (1, 0, 0), //1 HP P0
                                        new Vector3 (1, 0, 1), //2 HP P0
                                        new Vector3 (0, 0, 1), //3 HP P0
                                        new Vector3 (0, 0, 4), //4 SP P0
                                        new Vector3 (1, 0, 4), //5
                                        new Vector3 (2, 0, 4), //6
                                        new Vector3 (3, 0, 4), //7
                                        new Vector3 (4, 0, 4), //8
                                        new Vector3 (4, 0, 3), //9
                                        new Vector3 (4, 0, 2), //10
                                        new Vector3 (4, 0, 1), //11
                                        new Vector3 (4, 0, 0), //12
                                        new Vector3 (0, 0, 5), //13 TO P0
                                        new Vector3 (1, 0, 5), //14 FP P0
                                        new Vector3 (2, 0, 5), //15 FP P0
                                        new Vector3 (3, 0, 5), //16 FP P0
                                        new Vector3 (4, 0, 5), //17 FP P0

                                        new Vector3 (10, 0, 0), //0 HP P1
                                        new Vector3 (10, 0, 1), //1 HP P1
                                        new Vector3 (9, 0, 1), //2 HP P1
                                        new Vector3 (9, 0, 0), //3 HP P1
                                        new Vector3 (6, 0, 0), //4 SP P1
                                        new Vector3 (6, 0, 1), //5
                                        new Vector3 (6, 0, 2), //6
                                        new Vector3 (6, 0, 3), //7
                                        new Vector3 (6, 0, 4), //8
                                        new Vector3 (7, 0, 4), //9
                                        new Vector3 (8, 0, 4), //10
                                        new Vector3 (9, 0, 4), //11
                                        new Vector3 (10, 0, 4), //12
                                        new Vector3 (5, 0, 0), //13 TO P1
                                        new Vector3 (5, 0, 1), //14 FP P1
                                        new Vector3 (5, 0, 2), //15 FP P1
                                        new Vector3 (5, 0, 3), //16 FP P1
                                        new Vector3 (5, 0, 4), //17 FP P1

                                        new Vector3 (10, 0, 10), //0 HP P2
                                        new Vector3 (9, 0, 10), //1 HP P2
                                        new Vector3 (9, 0, 9), //2 HP P2
                                        new Vector3 (10, 0, 9), //3 HP P2
                                        new Vector3 (10, 0, 6), //4 SP P2
                                        new Vector3 (9, 0, 6), //5
                                        new Vector3 (8, 0, 6), //6
                                        new Vector3 (7, 0, 6), //7
                                        new Vector3 (6, 0, 6), //8
                                        new Vector3 (6, 0, 7), //9
                                        new Vector3 (6, 0, 8), //10
                                        new Vector3 (6, 0, 9), //11
                                        new Vector3 (6, 0, 10), //12
                                        new Vector3 (10, 0, 5), //13 TO P2
                                        new Vector3 (9, 0, 5), //14 FP P2
                                        new Vector3 (8, 0, 5), //15 FP P2
                                        new Vector3 (7, 0, 5), //16 FP P2
                                        new Vector3 (6, 0, 5), //17 FP P2

                                        new Vector3 (0, 0, 10), //0 HP P3
                                        new Vector3 (0, 0, 9), //1 HP P3
                                        new Vector3 (1, 0, 9), //2 HP P3
                                        new Vector3 (1, 0, 10), //3 HP P3
                                        new Vector3 (4, 0, 10), //4 SP P3
                                        new Vector3 (4, 0, 9), //5
                                        new Vector3 (4, 0, 8), //6
                                        new Vector3 (4, 0, 7), //7
                                        new Vector3 (4, 0, 6), //8
                                        new Vector3 (3, 0, 6), //9
                                        new Vector3 (2, 0, 6), //10
                                        new Vector3 (1, 0, 6), //11
                                        new Vector3 (0, 0, 6), //12
                                        new Vector3 (5, 0, 10), //13 TO P3
                                        new Vector3 (5, 0, 9), //14 FP P3
                                        new Vector3 (5, 0, 8), //15 FP P3
                                        new Vector3 (5, 0, 7), //16 FP P3
                                        new Vector3 (5, 0, 6), //17 FP P3
                                        };

    private Field ()
    {}

    public static Field createField (final List <Player> players)
    {
        if (instance == null) {
            instance = new Field ();
            /* We need to do this, but only once, since the OpenGl coordinate system has
             * the (0,0) point in the center. Our coordinates start on the top left.
             */
            normalizeVectors (new Vector3 (-5, 0, -5));
        }

        instance._spots = new ArrayList <Spot> ();

        int positionIndex = 0;
        if ((players != null) && (!players.isEmpty ())) {
            Spot lastNormalSpot = null;
            Spot firstTurnOutSpot = null;
            for (int i = 0; i < players.size (); i++) {
                Spot[] homeSpot = new Spot[GameSettings.getInstance ().getNumberOfTokensPerPlayer(players.size ())];
                for (int j = 0; j < homeSpot.length; j++) {
                    homeSpot[j] = Spot.createSpot (Attributes.SPOT_IS_HOME, players.get (i));
                    if (j > 0) {
                        homeSpot[j-1].setNextSpot (homeSpot[j]);
                    }
                    homeSpot[j].setPosition (_spotPosition[positionIndex++]);
                    players.get (i).getToken (j).setPosition (homeSpot[j]);
                    players.get (i).addOwnerSpot (homeSpot[j]);
                    instance._spots.add (homeSpot[j]);
                }
                final Spot startSpot = Spot.createSpot (Attributes.SPOT_IS_START, players.get (i));
                instance._spots.get (instance._spots.size () - 1).setNextSpot (startSpot);
                startSpot.setPosition (_spotPosition[positionIndex++]);
                players.get (i).addOwnerSpot (startSpot);
                instance._spots.add (startSpot);
                Spot[] normalSpot = new Spot[8];
                /* TODO: Need to get the number of normal spots from settings. */
                for (int j = 0; j < normalSpot.length; j++) {
                    normalSpot[j] = Spot.createSpot (Attributes.SPOT_IS_NORMAL);
                    if (j > 0) {
                        normalSpot[j-1].setNextSpot (normalSpot[j]);
                    } else {
                        startSpot.setNextSpot (normalSpot[j]);
                    }
                    normalSpot[j].setPosition (_spotPosition[positionIndex++]);
                    instance._spots.add (normalSpot[j]);
                }
                final Spot turnOutSpot = Spot.createSpot (Attributes.SPOT_IS_TURN_OUT, players.get (i));
                turnOutSpot.setNextSpot (startSpot);
                turnOutSpot.setPosition (_spotPosition[positionIndex++]);
                players.get (i).addOwnerSpot (turnOutSpot);
                instance._spots.add (turnOutSpot);
                if (i == 0) {
                    firstTurnOutSpot = turnOutSpot;
                }
                Spot[] finishSpot = new Spot[GameSettings.getInstance ().getNumberOfTokensPerPlayer(players.size ())];
                for (int j = 0; j < finishSpot.length; j++) {
                    finishSpot[j] = Spot.createSpot (Attributes.SPOT_IS_FINISH, players.get (i));
                    if (j > 0) {
                        finishSpot[j-1].setNextSpot (finishSpot[j]);
                    } else {
                        turnOutSpot.setNextTurnOutSpot (finishSpot[j]);
                    }
                    finishSpot[j].setPosition (_spotPosition[positionIndex++]);
                    players.get (i).addOwnerSpot (finishSpot[j]);
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

    public List <Spot> getSpots ()
    {
        return _spots;
    }

    public static void normalizeVectors (final Vector3 normal)
    {
        for (int i = 0; i < _spotPosition.length; i++) {
            _spotPosition[i].add (normal);
        }
    }
}
