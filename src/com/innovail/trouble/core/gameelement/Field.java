/**
 * @file: com.innovail.trouble.core.gameelement - Field.java
 * @date: Apr 14, 2012
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
public final class Field
{
    private List <Spot>          _spots;

    private static Field         instance;

    /* This will only work for 4 players right now... */
    private static final Vector3 Normal = new Vector3 (-5, 0, -5);

    public static Field createField (final List <Player> players)
    {
        final List <PreSpot> preSpots = GameSettings.getInstance ().getCurrentField ();

        if (instance == null) {
            instance = new Field ();
        }

        instance._spots = new ArrayList <Spot> ();
        for (int i = 0; i < preSpots.size (); i++) {
            instance._spots.add (new Spot ());
        }

        if ((players != null) && (!players.isEmpty ())) {
            int spotIndex = 0;
            for (final PreSpot preSpot : preSpots) {
                final Spot currentSpot = instance._spots.get (spotIndex);
                final Attributes spotType = preSpot.getType ();
                final int player = preSpot.getPlayer ();
                if ((player != PreSpot.NoPlayer) && (players.size () > player)) {
                    final Player actualPlayer = players.get (player);
                    currentSpot.configureSpot (spotType, actualPlayer);
                } else {
                    currentSpot.configureSpot (Attributes.SPOT_IS_NORMAL, null);
                }
                final PreSpot nextSpot = preSpot.getNextSpot ();
                if (nextSpot != null) {
                    currentSpot.setNextSpot (instance._spots.get (preSpots.indexOf (nextSpot)));
                }
                final PreSpot nextTOSpot = preSpot.getNextTurnoutSpot ();
                if (nextTOSpot != null) {
                    currentSpot.setNextTurnOutSpot (instance._spots.get (preSpots.indexOf (nextTOSpot)));
                }
                /*
                 * We need to do this, but only once, since the OpenGl
                 * coordinate system has the (0,0) point in the center. Our
                 * coordinates start on the top left.
                 */
                currentSpot.setPosition (normalizeVector (preSpot.getVectorPosition ()));
                spotIndex++;
            }
        }

        return instance;
    }

    public static Vector3 normalizeVector (final Vector3 vector)
    {
        return vector.add (Normal);
    }

    private Field ()
    {}

    public List <Spot> getSpots ()
    {
        return _spots;
    }
}
