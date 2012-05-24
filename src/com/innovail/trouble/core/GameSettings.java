/**
 * @file:   com.innovail.trouble.core - Settings.java
 * @date:   Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;

import com.innovail.trouble.graphics.GameMesh;

/**
 * 
 */
public final class GameSettings {
    /* SETTINGS */
    private int _NumberOfPlayers = 0;
    
    private int _MinimumNumberOfPlayers = 0;
    
    private int _NumberOfDice = 0;
    
    private int _TurnOutValue = 0;
    
    private int _TurnOutRetries = 0;
    
    private Map<Integer, Integer> _NumberOfTokensPerPlayer;
    
    private Map<Integer, Integer> _NumberOfNormalSpots;
    
    private Map<Integer, Color> _PlayerColors;
    
    private GameMesh _SpotMesh;
    
    private GameMesh _TokenMesh;
    
    private GameMesh _DiceMesh;
    
    private GameMesh _PlayerMesh;
    
    private List <GameMesh> _PlayerNumberMesh;
    /* END SETTINGS */
    
    
    private static GameSettings instance;

    private GameSettings () {};

    public static GameSettings getInstance ()
    {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }
    
    public int getNumberOfPlayers ()
    {
        return _NumberOfPlayers;
    }
    
    public void setNumberOfPlayers (final int players)
    {
        _NumberOfPlayers = players;
    }
    
    public int getMinimumNumberOfPlayers ()
    {
        return _MinimumNumberOfPlayers;
    }
    
    public void setMinimumNumberOfPlayers (final int players)
    {
        _MinimumNumberOfPlayers = players;
    }
    
    public int getNumberOfTokensPerPlayer (final int players)
    {
        if ((_NumberOfTokensPerPlayer != null) && (!_NumberOfTokensPerPlayer.isEmpty ())) {
            if (_NumberOfTokensPerPlayer.containsKey (Integer.valueOf (players))) {
                return _NumberOfTokensPerPlayer.get (players).intValue ();
            }
        }
        return 4;
    }
    
    public void setNumberOfTokensPerPlayer (final int players, final int tokens)
    {
        if (_NumberOfTokensPerPlayer == null)
        {
            _NumberOfTokensPerPlayer = new HashMap <Integer,Integer> ();
        }
        _NumberOfTokensPerPlayer.put (Integer.valueOf (players), Integer.valueOf (tokens));
    }
    
    public int getNumberOfNormalSpots (final int players)
    {
        if ((_NumberOfNormalSpots != null) && (!_NumberOfNormalSpots.isEmpty ())) {
            if (_NumberOfNormalSpots.containsKey (Integer.valueOf (players))) {
                return _NumberOfNormalSpots.get (players).intValue ();
            }
        }
        return 9;
    }
    
    public void setNumberOfNormalSpots (final int players, final int spots)
    {
        if (_NumberOfNormalSpots == null)
        {
            _NumberOfNormalSpots = new HashMap <Integer, Integer> ();
        }
        _NumberOfNormalSpots.put (Integer.valueOf (players), Integer.valueOf (spots));
    }
    
   public Color getPlayerColor (final int player)
    {
        if ((_PlayerColors != null) && (!_PlayerColors.isEmpty ())) {
            if (_PlayerColors.containsKey (player)) {
                return _PlayerColors.get (Integer.valueOf (player));
            }
        }
        return new Color ();
    }
    
    public void setPlayerColor (final int player, final Color color)
    {
        if (_PlayerColors == null) {
            _PlayerColors = new HashMap <Integer, Color> ();
        }
        _PlayerColors.put (Integer.valueOf (player), color);
    }
    
    public GameMesh getSpotMesh ()
    {
        return _SpotMesh;
    }
    
    public void setSpotMesh (final String path, final Color color, final boolean isInternal)
    {
        _SpotMesh = new GameMesh (path, color, isInternal);
    }
    
    public GameMesh getTokenMesh ()
    {
        return _TokenMesh;
    }
    
    public void setTokenMesh (final String path, final boolean isInternal)
    {
        _TokenMesh = new GameMesh (path, isInternal);
    }
    
    public void setTokenSound (final String path, final boolean isInternal)
    {
        if (_TokenMesh != null) {
            _TokenMesh.setSound (path, isInternal);
        }
    }

    public GameMesh getDiceMesh ()
    {
        return _DiceMesh;
    }
    
    public void setDiceMesh (final String path, final boolean isInternal,
                              final String texturePath, final String textureColorFormat)
    {
        _DiceMesh = new GameMesh (path, isInternal, texturePath, textureColorFormat);
    }
    
    public void setDiceSound (final String path, final boolean isInternal)
    {
        if (_DiceMesh != null) {
            _DiceMesh.setSound (path, isInternal);
        }
    }
    
    public int getNumberOfDice ()
    {
        return _NumberOfDice;
    }
    
    public void setNumberOfDice (final int dice)
    {
        _NumberOfDice = dice;
    }
    
    public int getTurnOutValue ()
    {
        return _TurnOutValue;
    }
    
    public void setTurnOutValue (int turnOut)
    {
        _TurnOutValue = turnOut;
    }
    
    public int getTurnOutRetries ()
    {
        return _TurnOutRetries;
    }
    
    public void setTurnOutRetries (int retries)
    {
        _TurnOutRetries = retries;
    }

    public void setPlayerMesh (final String path, final Color color,
                                final boolean isInternal)
    {
        _PlayerMesh = new GameMesh (path, color, isInternal);
    }
    
    public void setPlayerMesh (final String path, final boolean isInternal)
    {
        _PlayerMesh = new GameMesh (path, isInternal);
    }
    
    public GameMesh getPlayerMesh ()
    {
        return _PlayerMesh;
    }
    
    public void addPlayerNumber (final String path, final Color color,
                                  final boolean isInternal)
    {
        if ((_PlayerNumberMesh == null) || (_PlayerNumberMesh.isEmpty ())) {
            _PlayerNumberMesh = new ArrayList <GameMesh> ();
        }
        _PlayerNumberMesh.add (new GameMesh (path, color, isInternal));
    }
    
    public void addPlayerNumber (final String path, final boolean isInternal)
    {
        if ((_PlayerNumberMesh == null) || (_PlayerNumberMesh.isEmpty ())) {
            _PlayerNumberMesh = new ArrayList <GameMesh> ();
        }
        _PlayerNumberMesh.add (new GameMesh (path, isInternal));
    }
    
    public GameMesh getPlayerNumberMesh (int number)
    {
        return _PlayerNumberMesh.get (number);
    }
    
    public List <GameMesh> getPlayerNumbers ()
    {
        return _PlayerNumberMesh;
    }
}
