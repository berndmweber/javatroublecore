/**
 * @file:   com.innovail.trouble.core - Token.java
 * @date:   Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core.gameelement;

import com.badlogic.gdx.graphics.Color;

/**
 * 
 */
public class Token {
    private final Player _owner;
    private Spot _position;

    public Token (final Player player)
    {
        _owner = player;
    }
    
    public Player getOwner ()
    {
        return _owner;
    }
    
    public Spot getPosition ()
    {
        return _position;
    }
    
    public void setPosition (final Spot spot)
    {
        _position = spot;
    }
    
    public Color getColor ()
    {
        return _owner.getColor ();
    }
}
