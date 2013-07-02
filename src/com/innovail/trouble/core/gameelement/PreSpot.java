/**
 * @file: com.innovail.trouble.core.gameelement - PreSpot.java
 * @date: Jul 1, 2013
 * @author: bweber
 */
package com.innovail.trouble.core.gameelement;

import com.badlogic.gdx.math.Vector3;

import com.innovail.trouble.core.gameelement.Spot.Attributes;

/**
 * 
 */
public class PreSpot
{
    public static final int  NoPlayer = -1;

    private final Attributes _Type;
    private int              _Player;
    private String           _Position;
    private PreSpot          _NextSpot;
    private String           _NextSpotPosition;
    private PreSpot          _NextTurnOutSpot;
    private String           _NextTurnOutSpotPosition;

    public PreSpot (final Attributes type)
    {
        _Type = type;
        _Player = NoPlayer;
    }

    public PreSpot getNextSpot ()
    {
        return _NextSpot;
    }

    public String getNextSpotPosition ()
    {
        return _NextSpotPosition;
    }

    public PreSpot getNextTurnoutSpot ()
    {
        return _NextTurnOutSpot;
    }

    public String getNextTurnOutSpotPosition ()
    {
        return _NextTurnOutSpotPosition;
    }

    public int getPlayer ()
    {
        return _Player;
    }

    public String getPosition ()
    {
        return _Position;
    }

    public Attributes getType ()
    {
        return _Type;
    }

    public Vector3 getVectorPosition ()
    {
        int xDirection = 0;
        final int yDirection = 0;
        int zDirection = 0;

        /* We start with column 'A' */
        xDirection = _Position.charAt (0) - 'A';
        zDirection = Integer.parseInt (_Position.substring (1)) - 1;

        return new Vector3 (xDirection, yDirection, zDirection);
    }

    public void setNextSpot (final PreSpot spot)
    {
        _NextSpot = spot;
    }

    public void setNextSpotPosition (final String position)
    {
        _NextSpotPosition = position;
    }

    public void setNextTurnoutSpot (final PreSpot spot)
    {
        _NextTurnOutSpot = spot;
    }

    public void setNextTurnOutSpotPosition (final String position)
    {
        _NextTurnOutSpotPosition = position;
    }

    public void setPlayerNumber (final int number)
    {
        _Player = number;
    }

    public void setPosition (final String position)
    {
        _Position = position;
    }
}
