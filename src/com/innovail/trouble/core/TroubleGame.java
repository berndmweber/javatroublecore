/**
 * @file:   com.innovail.trouble.core - Game.java
 * @date:   Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.innovail.trouble.core.gameelement.Dice;
import com.innovail.trouble.core.gameelement.Field;
import com.innovail.trouble.core.gameelement.Player;
import com.innovail.trouble.core.gameelement.Token;

/**
 * 
 */
public class TroubleGame {
    private static final String TAG = "TroubleGame";
    
    private static final boolean DEBUG = true;
    
    private enum GameState {
        ROLL_DIE,
        SELECT_TOKEN,
        MOVE_TOKEN,
        REMOVE_FOE_TOKEN,
        WIN_GAME
    };
    
    private List <Player> _players;
    private Field _gameField;
    private Dice _dice;
    
    private GameState _currentState = GameState.ROLL_DIE;
    
    private boolean _hasRolled = false;
    private boolean _doneMoving = false;
    private Player _activePlayer;
    private Token _movingToken;

    
    public void createGame ()
    {
        _players = new ArrayList <Player> ();
        int numberOfPlayers = GameSettings.getInstance ().getNumberOfPlayers ();
        if (numberOfPlayers < GameSettings.getInstance ().getMinimumNumberOfPlayers ()) {
            numberOfPlayers = GameSettings.getInstance ().getMinimumNumberOfPlayers ();
        }
        for (int i = 0; i < numberOfPlayers; i++) {
            _players.add (new Player (i));
            _players.get (i).createTokens (GameSettings.getInstance ().getNumberOfTokensPerPlayer(numberOfPlayers));
        }
        _activePlayer = _players.get (0);
        _activePlayer.makeActive ();
        
        _gameField = Field.createField (_players);
        _dice = new Dice (GameSettings.getInstance ().getNumberOfDice ());
    }
    
    public void updateGame ()
    {
        switch (_currentState) {
        case ROLL_DIE:
            rollDiceHandler ();
            break;
        case SELECT_TOKEN:
            selectTokenHandler ();
            break;
        case MOVE_TOKEN:
            moveTokenHandler ();
            break;
        case REMOVE_FOE_TOKEN:
            removeFoeTokenHandler ();
            break;
        case WIN_GAME:
            winGameHandler ();
            break;
        }
        if (DEBUG) {
            Gdx.app.log (TAG, "Current State: " + _currentState.toString ());
        }
    }
    
    private void rollDiceHandler ()
    {
        if (_hasRolled) {
            _hasRolled = false;
            _currentState = GameState.SELECT_TOKEN;
        }
    }
    
    private void selectTokenHandler ()
    {
        int diceValue = _dice.getCurrentFaceValue (0);
        
        if (diceValue == 6) {
            if (_activePlayer.hasTokensAtHome ()) {
                if (_activePlayer.hasTokenOnStart ()) {
                    _movingToken = _activePlayer.getTokenOnStart ();
                } else {
                    _movingToken = _activePlayer.getTokenAtHome ();
                }
                _currentState = GameState.MOVE_TOKEN;
            }
        } else {
            _currentState = GameState.ROLL_DIE;
        }
    }
    
    private void moveTokenHandler ()
    {
        if (!_movingToken.doneMoving ()) {
            if (!_movingToken.isMoving ()) {
                if (_movingToken.getPosition ().isHome ()) {
                    _movingToken.moveToStart ();
                } else {
                    _movingToken.moveTo (_dice.getCurrentFaceValue (0));
                }
            } else {
                _movingToken.move (); 
            }
        } else {
            _currentState = GameState.ROLL_DIE;
        }
    }
    
    private void removeFoeTokenHandler ()
    {
        
    }
    
    private void winGameHandler ()
    {
        
    }
    
    public Field getField ()
    {
        return _gameField;
    }
    
    public List <Player> getPlayers ()
    {
        return _players;
    }
    
    public Dice getDice ()
    {
        return _dice;
    }
    
    public void rollDice ()
    {
        _dice.roll ();
        _hasRolled = true;
    }

}
