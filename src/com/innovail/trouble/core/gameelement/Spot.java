/**
 * @file:   com.innovail.trouble.core.gameelement - Spot.java
 * @date:   Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core.gameelement;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.innovail.trouble.core.GameSettings;

/**
 * 
 */
public class Spot {
    private boolean _isHome = false;
    private boolean _isStart = false;
    private boolean _isFinish = false;
    private boolean _isTurnOut = false;
    
    private Token _currentToken;
    private Token _potentialToken;
    private Player _owner;
    private Spot _nextSpot;
    private Spot _nextSpotWhenTurnOut;
    
    private final Color _color;
    private Vector3 _position;
    
    public enum Attributes {
        SPOT_IS_NORMAL,
        SPOT_IS_HOME,
        SPOT_IS_START,
        SPOT_IS_FINISH,
        SPOT_IS_TURN_OUT
    }

    public Spot ()
    {
        _color = GameSettings.getInstance ().getSpotMesh ().getColor ();
    }
    
    public static Spot createSpot (final Attributes attribute)
    {
        return createSpot (attribute, null);
    }
    
    public static Spot createSpot (final Attributes attribute, final Player player)
    {
        final Spot spot = new Spot ();
        
        switch (attribute) {
        case SPOT_IS_HOME:
            spot.makeHome (player);
            break;
        case SPOT_IS_START:
            spot.makeStart (player);
            break;
        case SPOT_IS_TURN_OUT:
            spot.makeTurnout (player);
            break;
        case SPOT_IS_FINISH:
            spot.makeFinish (player);
            break;
        case SPOT_IS_NORMAL:
        default:
            break;
        }
        
        return spot;
    }
    
    public boolean isHome ()
    {
        return _isHome;
    }

    public void makeHome (final Player player)
    {
        if (player != null) {
            _owner = player;
            _isHome = true;
        }
    }
    
    public boolean isStart ()
    {
        return _isStart;
    }
    
    public void makeStart (final Player player)
    {
        if (player != null) {
            _owner = player;
            _isStart = true;
        }
    }
    
    public boolean isFinish ()
    {
        return _isFinish;
    }
    
    public void makeFinish (final Player player)
    {
        if (player != null) {
            _owner = player;
            _isFinish = true;
        }
    }
    
    public boolean isTurnout ()
    {
        return _isTurnOut;
    }
    
    public void makeTurnout (final Player player)
    {
        if (player != null) {
            _owner = player;
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
    
    public Token getCurrentToken ()
    {
        return _currentToken;
    }
    
    public void setPotentialToken (final Token potential)
    {
        _potentialToken = potential;
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
    
    public Player getOwner ()
    {
        return _owner;
    }
    
    public void setNextSpot (final Spot spot)
    {
        _nextSpot = spot;
    }
    
    public Spot getNextSpot ()
    {
        return getNextSpot (null);
    }
    
    public Spot getNextSpot (final Player owner)
    {
        if ((owner != null) && (_owner != null) &&
            _owner.equals (owner) && _isTurnOut)
        {
            return _nextSpotWhenTurnOut;
        }

        return _nextSpot;
    }
    
    public void setNextTurnOutSpot (final Spot spot)
    {
        if (_isTurnOut) {
            _nextSpotWhenTurnOut = spot;
        }
    }
    
    public Color getColor ()
    {
        if ((_owner == null) || (_isTurnOut)) {
            return _color;
        }
        return _owner.getColor ();
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
    
    public Vector3 getPosition ()
    {
        return _position;
    }
}
