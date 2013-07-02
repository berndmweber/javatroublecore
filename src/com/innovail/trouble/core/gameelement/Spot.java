/**
 * @file: com.innovail.trouble.core.gameelement - Spot.java
 * @date: Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core.gameelement;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import com.innovail.trouble.core.GameSettings;

/**
 * 
 */
public class Spot
{
    public enum Attributes {
        SPOT_IS_NORMAL,
        SPOT_IS_HOME,
        SPOT_IS_START,
        SPOT_IS_FINISH,
        SPOT_IS_TURN_OUT
    }

    public static Spot createSpot (final Attributes attribute)
    {
        return createSpot (attribute, null);
    }

    public static Spot createSpot (final Attributes attribute,
                                   final Player player)
    {
        final Spot spot = new Spot ();

        spot.configureSpot (attribute, player);

        return spot;
    }

    private boolean     _isHome    = false;
    private boolean     _isStart   = false;
    private boolean     _isFinish  = false;
    private boolean     _isTurnOut = false;

    private Token       _currentToken;
    private Token       _potentialToken;

    private Player      _owner;

    private Spot        _nextSpot;

    private Spot        _nextSpotWhenTurnOut;

    private final Color _color;

    private Vector3     _position;

    public Spot ()
    {
        _color = GameSettings.getInstance ().getSpotMesh ().getColor ();
    }

    public void configureSpot (final Attributes type, final Player player)
    {
        switch (type) {
        case SPOT_IS_HOME:
            makeHome (player);
            break;
        case SPOT_IS_START:
            makeStart (player);
            break;
        case SPOT_IS_TURN_OUT:
            makeTurnout (player);
            break;
        case SPOT_IS_FINISH:
            makeFinish (player);
            break;
        case SPOT_IS_NORMAL:
        default:
            break;
        }
    }

    public Color getColor ()
    {
        if ((_owner == null) || (_isTurnOut)) {
            return _color;
        }
        return _owner.getColor ();
    }

    public Token getCurrentToken ()
    {
        return _currentToken;
    }

    public Spot getNextSpot ()
    {
        return getNextSpot (null);
    }

    public Spot getNextSpot (final Player owner)
    {
        if ((owner != null) && (_owner != null) && _owner.equals (owner) && _isTurnOut) {
            return _nextSpotWhenTurnOut;
        }

        return _nextSpot;
    }

    public Player getOwner ()
    {
        return _owner;
    }

    public Vector3 getPosition ()
    {
        return _position;
    }

    public Token getPotentialToken ()
    {
        return _potentialToken;
    }

    public boolean hasOwner ()
    {
        if (_owner != null) {
            return true;
        }
        return false;
    }

    public boolean isFinish ()
    {
        return _isFinish;
    }

    public boolean isHome ()
    {
        return _isHome;
    }

    public boolean isStart ()
    {
        return _isStart;
    }

    public boolean isTurnout ()
    {
        return _isTurnOut;
    }

    public void makeFinish (final Player player)
    {
        if (player != null) {
            _owner = player;
            player.addOwnerSpot (this);
            _isFinish = true;
        }
    }

    public void makeHome (final Player player)
    {
        if (player != null) {
            _owner = player;
            player.addOwnerSpot (this);
            final List <Token> tokens = player.getTokens ();
            for (final Token token : tokens) {
                if (token.getPosition () == null) {
                    token.setPosition (this);
                    break;
                }
            }
            _isHome = true;
        }
    }

    public void makeStart (final Player player)
    {
        if (player != null) {
            _owner = player;
            player.addOwnerSpot (this);
            _isStart = true;
        }
    }

    public void makeTurnout (final Player player)
    {
        if (player != null) {
            _owner = player;
            player.addOwnerSpot (this);
            _isTurnOut = true;
        }
    }

    public boolean positionToken (final Token newToken)
    {
        if (_currentToken != null) {
            return false;
        }
        _currentToken = newToken;
        return true;
    }

    public void releaseToken ()
    {
        _currentToken = null;
    }

    public void setNextSpot (final Spot spot)
    {
        _nextSpot = spot;
    }

    public void setNextTurnOutSpot (final Spot spot)
    {
        if (_isTurnOut) {
            _nextSpotWhenTurnOut = spot;
        }
    }

    public void setPosition (final float x, final float y, final float z)
    {
        if (_position == null) {
            _position = new Vector3 ();
        }
        _position.x = x;
        _position.y = y;
        _position.z = z;
    }

    public void setPosition (final Vector3 position)
    {
        _position = position;
    }

    public void setPotentialToken (final Token potential)
    {
        _potentialToken = potential;
    }
}
