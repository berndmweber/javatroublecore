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
    private Vector<Player> players;
    private Field gameField;
    
    public TroubleGame ()
    {}
    
    public void createGame ()
    {
        players = new Vector<Player> ();
        int numberOfPlayers = GameSettings.getInstance ().getNumberOfPlayers ();
        if (numberOfPlayers < GameSettings.getInstance ().getMinimumNumberOfPlayers ()) {
            numberOfPlayers = GameSettings.getInstance ().getMinimumNumberOfPlayers ();
        }
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add (new Player (i));
        }
        gameField = Field.createField (players);
    }

}
