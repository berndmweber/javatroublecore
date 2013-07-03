/**
 * @file: com.innovail.trouble.core - TroubleGame.java
 * @date: Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import com.innovail.trouble.core.TroubleGameAI.Difficulty;
import com.innovail.trouble.core.TroubleGameAI.Policy;
import com.innovail.trouble.core.gameelement.Dice;
import com.innovail.trouble.core.gameelement.Field;
import com.innovail.trouble.core.gameelement.Player;
import com.innovail.trouble.core.gameelement.Token;

/**
 * 
 */
public class TroubleGame
{
    private enum GameState {
        EVALUATE_PLAYERS,
        ROLL_DIE,
        ROLLING_DIE,
        SELECT_TOKEN,
        MOVE_TOKEN,
        REMOVE_FOE_TOKEN,
        WIN_GAME,
        END_GAME,
        UNDEFINED
    }

    private static final String  TAG                 = "TroubleGame";

    private static final boolean DEBUG               = true;

    private List <Player>        _players;
    private Field                _gameField;
    private Dice                 _dice;

    private GameState            _currentState       = GameState.ROLL_DIE;
    private GameState            _lastState          = GameState.UNDEFINED;

    private boolean              _doneRolling        = false;
    private boolean              _hasRolled          = false;
    private boolean              _hasSelected        = false;
    private boolean              _playerChanged      = false;
    private boolean              _tokenStartedMoving = false;
    private boolean              _tokenMoved         = false;
    private int                  _rollTrysLeft       = 3;

    private TroubleGameAI        _gameAI;
    private Player               _activePlayer;
    private Token                _movingToken;
    private Token                _foeToken;
    private List <Token>         _availableTokens;

    public boolean canRollDice ()
    {
        if (_currentState == GameState.ROLL_DIE) {
            return true;
        }
        return false;
    }

    public void createGame ()
    {
        _players = new ArrayList <Player> ();
        int numberOfPlayers = GameSettings.getInstance ().getNumberOfPlayers ();
        if (numberOfPlayers < GameSettings.getInstance ().getMinimumNumberOfPlayers ()) {
            numberOfPlayers = GameSettings.getInstance ().getMinimumNumberOfPlayers ();
        }
        for (int i = 0; i < numberOfPlayers; i++) {
            final Player newPlayer = new Player (i);
            _players.add (newPlayer);
            newPlayer.createTokens (GameSettings.getInstance ().getNumberOfTokensPerPlayer (numberOfPlayers));
        }
        _activePlayer = _players.get (MathUtils.random (0, numberOfPlayers - 1));
        final Player humanPlayer = _players.get (0);
        humanPlayer.makeHuman (true);
        humanPlayer.makeActive ();

        _gameField = Field.createField (_players);
        _dice = new Dice (GameSettings.getInstance ().getNumberOfDice ());
        _availableTokens = new ArrayList <Token> ();
        _gameAI = new TroubleGameAI (Policy.AGGRESSIVE, Difficulty.HARD);
    }

    public void doneRolling ()
    {
        _doneRolling = true;
    }

    private void evaluatePlayersHandler ()
    {
        final int maxRetries = GameSettings.getInstance ().getTurnOutRetries ();

        if (_dice.getCurrentFaceValue (0) != GameSettings.getInstance ().getTurnOutValue ()) {
            if (_activePlayer.hasTokensOnField () || _tokenMoved) {
                _tokenMoved = false;
                getNextPlayer ();
                _rollTrysLeft = maxRetries;
            } else {
                if (--_rollTrysLeft == 0) {
                    getNextPlayer ();
                    _rollTrysLeft = maxRetries;
                }
            }
        } else {
            _rollTrysLeft = maxRetries;
        }
        _currentState = GameState.ROLL_DIE;

        if (DEBUG) {
            Gdx.app.log (TAG, "tries left: " + _rollTrysLeft);
            Gdx.app.log (TAG, "Player: " + _activePlayer.getName ());
        }
    }

    public Player getActivePlayer ()
    {
        return _activePlayer;
    }

    public List <Token> getAvailableTokens ()
    {
        return _availableTokens;
    }

    public Dice getDice ()
    {
        return _dice;
    }

    public Field getField ()
    {
        return _gameField;
    }

    private void getNextPlayer ()
    {
        if (_players.indexOf (_activePlayer) == (_players.size () - 1)) {
            _activePlayer = _players.get (0);
        } else {
            _activePlayer = _players.get (_players.indexOf (_activePlayer) + 1);
        }
        if (_activePlayer.hasFinished ()) {
            getNextPlayer ();
        }
        _playerChanged = true;
    }

    public List <Player> getPlayers ()
    {
        return _players;
    }

    public boolean isFinished ()
    {
        if (_currentState == GameState.END_GAME) {
            return true;
        }
        return false;
    }

    public boolean isRolling ()
    {
        if (_currentState == GameState.ROLLING_DIE) {
            return true;
        }
        return false;
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
                if (_movingToken.isLastMove ()) {
                    final Token potentialFoe = _movingToken.getMoveNextPosition ().getCurrentToken ();
                    if ((potentialFoe != null) && !_movingToken.getOwner ().equals (potentialFoe.getOwner ())) {
                        _foeToken = potentialFoe;
                        _currentState = GameState.REMOVE_FOE_TOKEN;
                    }
                }
                _movingToken.move ();
            }
        } else {
            _tokenMoved = true;
            _movingToken = null;
            _currentState = GameState.WIN_GAME;
        }
    }

    public boolean playerChanged ()
    {
        if (_playerChanged) {
            _playerChanged = false;
            return true;
        }
        return false;
    }

    private void removeFoeTokenHandler ()
    {
        if (!_foeToken.doneMoving ()) {
            if (_movingToken.isMoving ()) {
                _movingToken.move ();
            }
            if (!_foeToken.isMoving ()) {
                _foeToken.moveToHome ();
            } else {
                _foeToken.move ();
            }
            /*
             * NVTROUB-4: Need to actually set the position now since the owner
             * token wasn't null before.
             */
            _movingToken.setPosition (_movingToken.getPosition ());
        } else {
            /* Need to do this to reset doneMoving boolean */
            _movingToken.doneMoving ();
            _foeToken = null;
            _movingToken = null;
            _currentState = GameState.WIN_GAME;
        }
    }

    public void resetGame ()
    {
        _currentState = GameState.EVALUATE_PLAYERS;
    }

    public void rollDice ()
    {
        if (canRollDice ()) {
            _dice.roll ();
            _hasRolled = true;
        }
    }

    private void rollDiceHandler ()
    {
        if (_hasRolled) {
            _hasRolled = false;
            if (!_activePlayer.isHuman ()) {
                _currentState = GameState.ROLLING_DIE;
            } else {
                _currentState = GameState.SELECT_TOKEN;
            }
        }
    }

    private void rollingDiceHandler ()
    {
        if (_doneRolling) {
            _doneRolling = false;
            _currentState = GameState.SELECT_TOKEN;
        }
    }

    public void selectToken (final Token selected)
    {
        if ((_currentState == GameState.SELECT_TOKEN) && !_availableTokens.isEmpty ()) {
            if (_availableTokens.contains (selected)) {
                _movingToken = selected;
                _activePlayer.deselectAllTokens ();
                _hasSelected = true;
            }
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
                            if ((potentialToken != null) && potentialToken.getOwner ().equals (_activePlayer)) {
                                _movingToken = potentialToken;
                            } else {
                                break;
                            }
                            /*
                             * TODO: This needs to be revisited. There is a
                             * chance that this could lead to an infinite loop
                             * depending on the play field.
                             */
                        } while (true);
                    } else {
                        _movingToken = _activePlayer.getTokenAtHome ();
                    }
                    _activePlayer.deselectAllTokens ();
                    _tokenStartedMoving = true;
                    _currentState = GameState.MOVE_TOKEN;
                } else {
                    _availableTokens = _activePlayer.getMovableTokens (diceValue);
                    if (_availableTokens.isEmpty ()) {
                        _currentState = GameState.WIN_GAME;
                    }
                }
            } else {
                _availableTokens = _activePlayer.getMovableTokens (diceValue);
                if (_availableTokens.isEmpty ()) {
                    _currentState = GameState.WIN_GAME;
                }
            }
        } else {
            if ((_availableTokens.size () == 1) && !_hasSelected) {
                selectToken (_availableTokens.get (0));
            } else {
                if (!_activePlayer.isHuman () && !_hasSelected) {
                    _gameAI.moveTokens (this, _availableTokens, diceValue);
                }
                if (_hasSelected) {
                    _availableTokens.clear ();
                    _hasSelected = false;
                    _tokenStartedMoving = true;
                    _currentState = GameState.MOVE_TOKEN;
                }
            }
        }
    }

    public boolean tokenStartedMoving ()
    {
        if (_tokenStartedMoving) {
            _tokenStartedMoving = false;
            return true;
        }
        return false;
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
        case EVALUATE_PLAYERS:
            evaluatePlayersHandler ();
            break;
        case ROLL_DIE:
            rollDiceHandler ();
            break;
        case ROLLING_DIE:
            rollingDiceHandler ();
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
        case END_GAME:
        default:
            break;
        }
    }

    private void winGameHandler ()
    {
        if (_activePlayer.hasAllTokensFinished ()) {
            _activePlayer.finished ();
            int notFinished = 0;
            final Iterator <Player> players = _players.iterator ();
            while (players.hasNext ()) {
                if (!players.next ().hasFinished ()) {
                    notFinished++;
                }
            }
            if (notFinished > 1) {
                _currentState = GameState.EVALUATE_PLAYERS;
            }
            _currentState = GameState.END_GAME;
        } else {
            _currentState = GameState.EVALUATE_PLAYERS;
        }
    }
}
