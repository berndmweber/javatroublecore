/**
 * @file:   com.innovail.trouble.core.gameelement - Player.java
 * @date:   Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core.gameelement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import com.innovail.trouble.core.GameSettings;

/**
 * 
 */
public class Player {
    private final List <Token> _tokens;
    private final List <Spot> _spots;
    private Color _color;
    private String _name;
    private final int _number;
    private boolean _isActive = false;
    private boolean _hasFinished = false;
    
    public Player (final int playerNumber)
    {
        _tokens = new ArrayList <Token> ();
        _spots = new ArrayList <Spot> ();
        _color = GameSettings.getInstance ().getPlayerColor (playerNumber);
        _name = new String ("Player " + playerNumber);
        _number = playerNumber;
    }
    
    public void setColor (final Color color)
    {
        if (color != null) {
            _color = color;
        }
    }
    
    public Color getColor ()
    {
        return _color;
    }
    
    public void setName (final String name)
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
    
    public void addOwnerSpot (Spot spot)
    {
        _spots.add (spot);
    }
    
    public List <Spot> getOwnerSpots ()
    {
        return _spots;
    }
    
    public List <Token> getTokens ()
    {
        return _tokens;
    }
    
    public Token getToken (final int number)
    {
        if (number < _tokens.size ()) {
            return _tokens.get (number);
        }
        return null;
    }
    
    public void createTokens (final int number)
    {
        for (int i = 0; i < number; i++) {
            _tokens.add (new Token (this));
        }
    }
    
    public boolean hasTokensAtHome ()
    {
        boolean atHome = false;
        
        final Iterator<Token> tokens = _tokens.iterator ();
        while (tokens.hasNext ()) {
            if (tokens.next ().getPosition ().isHome ()) {
                atHome = true;
                break;
            }
        }
        
        return atHome;
    }
    
    public boolean hasTokenOnStart () {
        boolean onStart = false;
        
        final Iterator<Token> tokens = _tokens.iterator ();
        while (tokens.hasNext ()) {
            if (tokens.next ().getPosition ().isStart ())
            {
                onStart = true;
                break;
            }
        }
        
        return onStart;
    }
    
    public boolean hasTokenOnSpot (final Spot targetPosition)
    {
        boolean onSpot = false;
        
        final Iterator<Token> tokens = _tokens.iterator ();
        while (tokens.hasNext ()) {
            if (tokens.next ().getPosition ().equals (targetPosition)) {
                onSpot = true;
                break;
            }
        }
        return onSpot;
    }

    public boolean hasTokensOnField ()
    {
        boolean onField = false;
        
        final Iterator<Token> tokens = _tokens.iterator ();
        while (tokens.hasNext ()) {
            final Spot tokenPosition = tokens.next ().getPosition ();
            if (!tokenPosition.isHome ()) {
                if (tokenPosition.isFinish ()) {
                    boolean spotOccupied = true;
                    do {
                        Spot current = tokenPosition.getNextSpot ();
                        if ((current != null) &&
                            (current.getCurrentToken () == null))
                        {
                            spotOccupied = false;
                        }
                    } while (spotOccupied);
                    if (!spotOccupied) {
                        onField = true;
                        break;
                    }
                } else {
                    onField = true;
                    break;
                }
            }
        }
        
        return onField;
    }
    
    public boolean hasAllTokensFinished ()
    {
        boolean allFinished = true;
        final Iterator <Token> tokens = _tokens.iterator ();
        while (tokens.hasNext ()) {
            if (!tokens.next ().getPosition ().isFinish ()) {
                allFinished = false;
                break;
            }
        }
        return allFinished;
    }
    
    public Token getTokenOnStart ()
    {
        Token onStart = null;
        
        final Iterator<Token> tokens = _tokens.iterator ();
        while (tokens.hasNext ()) {
            final Token current = tokens.next ();
            if (current.getPosition ().isStart ())
            {
                onStart = current;
                break;
            }
        }

        return onStart;
    }
    
    public Token getTokenAtHome ()
    {
        Token atHome = null;
        
        final Iterator<Token> tokens = _tokens.iterator ();
        while (tokens.hasNext ()) {
            final Token current = tokens.next ();
            if (current.getPosition ().isHome ()) {
                atHome = current;
                break;
            }
        }
        
        return atHome;
    }
    
    public List <Token> getMovableTokens (final int moves)
    {
        final ArrayList <Token> availableTokens = new ArrayList<Token> ();
        
        final Iterator<Token> tokens = _tokens.iterator ();
        while (tokens.hasNext ()) {
            final Token token = tokens.next ();
            if (!token.getPosition ().isHome ()) {
                final Spot targetPosition = token.getTargetPosition (moves);
                if (targetPosition != null) {
                    final Token potentialToken = targetPosition.getCurrentToken ();
                    if ((potentialToken == null) ||
                         !potentialToken.getOwner ().equals (this))
                    {
                        availableTokens.add (token);
                        targetPosition.setPotentialToken (token);
                        token.setPotentialPosition (targetPosition);
                        token.setSelected (true);
                    }
                }
            }
        }
        
        return availableTokens;
    }
    
    public void deselectAllTokens ()
    {
        final Iterator <Token> tokens = _tokens.iterator ();
        while (tokens.hasNext ()) {
            Token token = tokens.next ();
            token.setSelected (false);
            token.resetPotentialPosition ();
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
    
    public void finished ()
    {
        _hasFinished = true;
    }
    
    public boolean hasFinished ()
    {
        return _hasFinished;
    }
}
