/**
 * @file:   com.innovail.trouble.core.gameelement - Spot.java
 * @date:   Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core.gameelement;

import com.badlogic.gdx.graphics.Color;
import com.innovail.trouble.core.Settings;

/**
 * 
 */
public class Spot {
    private boolean _isHome = false;
    private boolean _isStart = false;
    private boolean _isFinish = false;
    private boolean _isTurnOut = false;
    private Token _currentToken;
    private Player _owner;
    private Spot _nextSpot;
    private Spot _nextSpotWhenTurnOut;
    private Color _color;

    public Spot ()
    {
        _color = Settings.getInstance ().getSpotDefaultColor ();
    }
    
    public boolean isHome ()
    {
        return _isHome;
    }

    public void makeHome (Player player)
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
    
    public void makeStart (Player player)
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
    
    public void makeFinish (Player player)
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
    
    public void makeTurnout (Player player)
    {
        if (player != null) {
            _owner = player;
            _isTurnOut = true;
        }
    }
    
    public boolean positionToken (Token newToken)
    {
        if (_currentToken != null) {
            return false;
        }
        _currentToken = newToken;
        return true;
    }
    
    public Token getCurrentToken ()
    {
        return _currentToken;
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
    
    public void setNextSpot (Spot spot)
    {
        _nextSpot = null;
    }
    
    public Spot getNextSpot (Player owner)
    {
        if (_owner == owner) {
            if (_isTurnOut) {
                return _nextSpotWhenTurnOut;
            }
        }
        return _nextSpot;
    }
    
    public void setNextTurnOutSpot (Spot spot)
    {
        if (_isTurnOut) {
            _nextSpotWhenTurnOut = spot;
        }
    }
    
    public Color getColor ()
    {
        if (_owner == null) {
            return _color;
        }
        return _owner.getColor ();
    }
}
