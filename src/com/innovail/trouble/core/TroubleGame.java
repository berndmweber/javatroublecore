/**
 * @file:   com.innovail.trouble.core - Game.java
 * @date:   Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.Vector;

import com.innovail.trouble.core.gameelement.Field;
import com.innovail.trouble.core.gameelement.Player;

/**
 * 
 */
public class TroubleGame {
    private Vector<Player> _players;
    private Field _gameField;
    
    public TroubleGame ()
    {}
    
    public void createGame ()
    {
        _players = new Vector<Player> ();
        int numberOfPlayers = GameSettings.getInstance ().getNumberOfPlayers ();
        if (numberOfPlayers < GameSettings.getInstance ().getMinimumNumberOfPlayers ()) {
            numberOfPlayers = GameSettings.getInstance ().getMinimumNumberOfPlayers ();
        }
        for (int i = 0; i < numberOfPlayers; i++) {
            _players.add (new Player (i));
            _players.get (i).createTokens (GameSettings.getInstance ().getNumberOfTokensPerPlayer(numberOfPlayers));
        }
        _gameField = Field.createField (_players);
    }
    
    public Field getField ()
    {
        return _gameField;
    }
    
    public Vector<Player> getPlayers ()
    {
        return _players;
    }

}
