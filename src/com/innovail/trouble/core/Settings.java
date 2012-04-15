/**
 * @file:   com.innovail.trouble.core - Settings.java
 * @date:   Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;

/**
 * 
 */
public class Settings {
    /* SETTINGS */
    private int _NumberOfPlayers = 0;
    
    private Map<Integer, Integer> _NumberOfTokensPerPlayer;
    
    private Map<Integer, Color> _PlayerColors;
    
    private Color _SpotDefaultColor;
    
    /* END SETTINGS */
    
    private static Settings instance;

    private Settings () {};

    public static Settings getInstance ()
    {
        if (instance == null) {
            instance = new Settings();
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
    
    public Color getSpotDefaultColor ()
    {
        return _SpotDefaultColor;
    }
    
    public void setSpotDefaultColor (Color color)
    {
        _SpotDefaultColor = color;
    }
}
