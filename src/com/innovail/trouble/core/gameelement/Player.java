/**
 * @file:   com.innovail.trouble.core.gameelement - Player.java
 * @date:   Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core.gameelement;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.innovail.trouble.core.GameSettings;

/**
 * 
 */
public class Player {
    private List <Token> _tokens;
    private Color _color;
    private String _name;
    private int _number;
    private boolean _isActive = false;
    
    public Player (int playerNumber)
    {
        _tokens = new ArrayList <Token> ();
        _color = GameSettings.getInstance ().getPlayerColor (playerNumber);
        _name = new String ("Player " + playerNumber);
    }
    
    public void setColor (Color color)
    {
        if (color != null) {
            _color = color;
        }
    }
    
    public Color getColor ()
    {
        return _color;
    }
    
    public void setName (String name)
    {
        if (name != null) {
            _name = name;
        }
    }
    
    public String getName ()
    {
        return _name;
    }
    
    public int getNumber ()
    {
        return _number;
    }
    
    public List <Token> getTokens ()
    {
        return _tokens;
    }
    
    public Token getToken (int number)
    {
        if (number < _tokens.size ()) {
            return _tokens.get (number);
        }
        return null;
    }
    
    public void createTokens (int number)
    {
        for (int i = 0; i < number; i++) {
            _tokens.add (new Token (this));
        }
    }

    public void makeActive ()
    {
        _isActive = true;
    }
    
    public boolean isActive ()
    {
        return _isActive;
    }
}
