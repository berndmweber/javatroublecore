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
        WIN_GAME,
        UNDEFINED
    };
    
    private List <Player> _players;
    private Field _gameField;
    private Dice _dice;
    
    private GameState _currentState = GameState.ROLL_DIE;
    private GameState _lastState = GameState.UNDEFINED;
    
    private boolean _hasRolled = false;
    private boolean _hasSelected = false;

    private Player _activePlayer;
    private Token _movingToken;
    private List <Token> _availableTokens;

    
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
        _availableTokens = new ArrayList <Token> ();
    }
    
    public void updateGame ()
    {
        if (_currentState != _lastState) {
            if (DEBUG) {
                Gdx.app.log (TAG, "Current State: " + _currentState.toString ());
            }
        }
        _lastState = _currentState;
        
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
        default:
            break;
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
        final int diceValue = _dice.getCurrentFaceValue (0);
        
        if (_availableTokens.isEmpty ()) {
            if (diceValue == GameSettings.getInstance ().getTurnOutValue ()) {
                if (_activePlayer.hasTokensAtHome ()) {
                    if (_activePlayer.hasTokenOnStart ()) {
                        _movingToken = _activePlayer.getTokenOnStart ();
                        do {
                            final Token potentialToken = _movingToken.getTargetPosition (diceValue).getCurrentToken ();
                            if ((potentialToken != null) &&
                                 potentialToken.getOwner ().equals (_activePlayer))
                            {
                                _movingToken = potentialToken;
                            } else {
                                break;
                            }
                        /* TODO: This needs to be revisited. There is a chance that this could lead to
                         * an infinite loop depending on the play field.
                         */
                        } while (true);
                    } else {
                        _movingToken = _activePlayer.getTokenAtHome ();
                    }
                    _currentState = GameState.MOVE_TOKEN;
                } else {
                    _availableTokens = _activePlayer.getMovableTokens (diceValue);
                    if (_availableTokens.isEmpty ()) {
                        _currentState = GameState.ROLL_DIE;
                    }
                }
            } else {
                _availableTokens = _activePlayer.getMovableTokens (diceValue);
                if (_availableTokens.isEmpty ()) {
                    _currentState = GameState.ROLL_DIE;
                }
            }
        } else {
            if (_hasSelected) {
                _availableTokens.clear ();
                _hasSelected = false;
                _currentState = GameState.MOVE_TOKEN;
            }
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
            _movingToken = null;
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
        if (_currentState == GameState.ROLL_DIE) {
            _dice.roll ();
            _hasRolled = true;
        }
    }

    public void selectToken (Token selected)
    {
        if ((_currentState == GameState.SELECT_TOKEN) && !_availableTokens.isEmpty ()) {
            if (_availableTokens.contains (selected)) {
                _movingToken = selected;
                _activePlayer.deselectAllTokens ();
                _hasSelected = true;
            }
        }
    }
    
    public List <Token> getAvailableTokens ()
    {
        return _availableTokens;
    }
}
