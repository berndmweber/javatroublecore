/**
 * @file:   com.innovail.trouble.core - Settings.java
 * @date:   Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.innovail.trouble.utils.GameMesh;

/**
 * 
 */
public class GameSettings {
    /* SETTINGS */
    private int _NumberOfPlayers = 0;
    
    private int _MinimumNumberOfPlayers = 0;
    
    private Map<Integer, Integer> _NumberOfTokensPerPlayer;
    
    private Map<Integer, Integer> _NumberOfNormalSpots;
    
    private Map<Integer, Color> _PlayerColors;
    
    private GameMesh _SpotMesh;
    
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
    
    public void setNumberOfPlayers (int players)
    {
        _NumberOfPlayers = players;
    }
    
    public int getMinimumNumberOfPlayers ()
    {
        return _MinimumNumberOfPlayers;
    }
    
    public void setMinimumNumberOfPlayers (int players)
    {
        _MinimumNumberOfPlayers = players;
    }
    
    public int getNumberOfTokensPerPlayer (int players)
    {
        if ((_NumberOfTokensPerPlayer != null) && (!_NumberOfTokensPerPlayer.isEmpty ())) {
            if (_NumberOfTokensPerPlayer.containsKey (new Integer(players))) {
                return _NumberOfTokensPerPlayer.get (players).intValue ();
            }
        }
        return 4;
    }
    
    public void setNumberOfTokensPerPlayer (int players, int tokens)
    {
        if (_NumberOfTokensPerPlayer == null)
        {
            _NumberOfTokensPerPlayer = new HashMap<Integer,Integer> ();
        }
        _NumberOfTokensPerPlayer.put (new Integer (players), new Integer (tokens));
    }
    
    public int getNumberOfNormalSpots (int players)
    {
        if ((_NumberOfNormalSpots != null) && (!_NumberOfNormalSpots.isEmpty ())) {
            if (_NumberOfNormalSpots.containsKey (new Integer(players))) {
                return _NumberOfNormalSpots.get (players).intValue ();
            }
        }
        return 9;
    }
    
    public void setNumberOfNormalSpots (int players, int spots)
    {
        if (_NumberOfNormalSpots == null)
        {
            _NumberOfNormalSpots = new HashMap<Integer,Integer> ();
        }
        _NumberOfNormalSpots.put (new Integer (players), new Integer (spots));
    }
    
   public Color getPlayerColor (int player)
    {
        if ((_PlayerColors != null) && (!_PlayerColors.isEmpty ())) {
            if (_PlayerColors.containsKey (player)) {
                return _PlayerColors.get (new Integer(player));
            }
        }
        return new Color ();
    }
    
    public void setPlayerColor (int player, Color color)
    {
        if (_PlayerColors == null) {
            _PlayerColors = new HashMap<Integer,Color> ();
        }
        _PlayerColors.put (new Integer(player), color);
    }
    
    public GameMesh getSpotMesh ()
    {
        return _SpotMesh;
    }
    
    public void setSpotMesh (String path, Color color, boolean isInternal)
    {
        _SpotMesh = new GameMesh (path, color, isInternal);
    }
}
