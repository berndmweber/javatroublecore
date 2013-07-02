/**
 * @file: com.innovail.trouble.core - GameSettings.java
 * @date: Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;

import com.innovail.trouble.core.gameelement.PreSpot;
import com.innovail.trouble.graphics.GameMesh;
import com.innovail.trouble.utils.FieldLoader;

/**
 * 
 */
public final class GameSettings
{
    /* SETTINGS */
    private static final String    FieldFilePath           = "configs/";
    private final List <String>    _FieldFiles;
    private String                 _defaultFieldFileName;
    private String                 _fieldFileExtension;
    private boolean                _fieldFileIsInternal;
    private String                 _currentFieldName;

    private int                    _NumberOfPlayers        = 0;

    private int                    _MinimumNumberOfPlayers = 0;
    private int                    _MaximumNumberOfPlayers = 0;

    private int                    _NumberOfDice           = 0;

    private int                    _TurnOutValue           = 0;

    private int                    _TurnOutRetries         = 0;

    private Map <Integer, Integer> _NumberOfTokensPerPlayer;

    private Map <Integer, Integer> _NumberOfNormalSpots;

    private Map <Integer, Color>   _PlayerColors;

    private GameMesh               _SpotMesh;

    private GameMesh               _TokenMesh;

    private GameMesh               _DiceMesh;

    private GameMesh               _PlayerMesh;

    private List <GameMesh>        _PlayerNumberMesh;
    /* END SETTINGS */

    private static GameSettings    instance;

    public static GameSettings getInstance ()
    {
        if (instance == null) {
            instance = new GameSettings ();
        }
        return instance;
    };

    private GameSettings ()
    {
        _FieldFiles = new ArrayList <String> ();
    }

    public void addPlayerNumber (final String path, final boolean isInternal)
    {
        if ((_PlayerNumberMesh == null) || (_PlayerNumberMesh.isEmpty ())) {
            _PlayerNumberMesh = new ArrayList <GameMesh> ();
        }
        _PlayerNumberMesh.add (new GameMesh (path, isInternal));
    }

    public void addPlayerNumber (final String path, final Color color,
                                 final boolean isInternal)
    {
        if ((_PlayerNumberMesh == null) || (_PlayerNumberMesh.isEmpty ())) {
            _PlayerNumberMesh = new ArrayList <GameMesh> ();
        }
        _PlayerNumberMesh.add (new GameMesh (path, color, isInternal));
    }

    public List <PreSpot> getCurrentField ()
    {
        if (!_FieldFiles.isEmpty ()) {
            final String fileName = FieldFilePath + _currentFieldName + "." + _fieldFileExtension;
            FileHandle file;
            if (_fieldFileIsInternal) {
                file = Gdx.files.internal (fileName);
            } else {
                file = Gdx.files.external (fileName);
            }
            return FieldLoader.getField (file);
        }
        return null;
    }

    public GameMesh getDiceMesh ()
    {
        return _DiceMesh;
    }

    public String [] getFieldList ()
    {
        return (String []) _FieldFiles.toArray ();
    }

    public int getMinimumNumberOfPlayers ()
    {
        return _MinimumNumberOfPlayers;
    }

    public int getNumberOfDice ()
    {
        return _NumberOfDice;
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

    public int getNumberOfPlayers ()
    {
        return _NumberOfPlayers;
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

    public Color getPlayerColor (final int player)
    {
        if ((_PlayerColors != null) && (!_PlayerColors.isEmpty ())) {
            if (_PlayerColors.containsKey (player)) {
                return _PlayerColors.get (Integer.valueOf (player));
            }
        }
        return new Color ();
    }

    public GameMesh getPlayerMesh ()
    {
        return _PlayerMesh;
    }

    public GameMesh getPlayerNumberMesh (final int number)
    {
        return _PlayerNumberMesh.get (number);
    }

    public List <GameMesh> getPlayerNumbers ()
    {
        return _PlayerNumberMesh;
    }

    public GameMesh getSpotMesh ()
    {
        return _SpotMesh;
    }

    public GameMesh getTokenMesh ()
    {
        return _TokenMesh;
    }

    public int getTurnOutRetries ()
    {
        return _TurnOutRetries;
    }

    public int getTurnOutValue ()
    {
        return _TurnOutValue;
    }

    public void setDiceMesh (final String path, final boolean isInternal,
                             final String texturePath,
                             final String textureColorFormat)
    {
        _DiceMesh = new GameMesh (path, isInternal, texturePath, textureColorFormat);
    }

    public void setDiceSound (final String path, final boolean isInternal)
    {
        if (_DiceMesh != null) {
            _DiceMesh.setSound (path, isInternal);
        }
    }

    public void setFieldList (final String defaultField,
                              final String extension, final boolean isInternal)
    {
        if ((extension != null) && !extension.isEmpty ()) {
            _fieldFileExtension = extension;
            _fieldFileIsInternal = isInternal;
            FileHandle [] list;
            if (isInternal) {
                list = Gdx.files.internal (GameApplication.InternalPathPrefix + FieldFilePath).list (extension);
            } else {
                list = Gdx.files.external (FieldFilePath).list (extension);
            }
            for (final FileHandle file : list) {
                _FieldFiles.add (file.nameWithoutExtension ());
            }
            if ((defaultField != null) && !defaultField.isEmpty ()) {
                _defaultFieldFileName = defaultField;
                _currentFieldName = _defaultFieldFileName;
            }
        }
        if (!_FieldFiles.isEmpty ()) {
            final String fileName = FieldFilePath + _defaultFieldFileName + "." + _fieldFileExtension;
            FileHandle file;
            if (_fieldFileIsInternal) {
                file = Gdx.files.internal (fileName);
            } else {
                file = Gdx.files.external (fileName);
            }
            final int [] noOfPlayers = FieldLoader.getPlayerInfo (file);
            _MinimumNumberOfPlayers = noOfPlayers[0];
            _MaximumNumberOfPlayers = noOfPlayers[1];
        }
    }

    public void setMinimumNumberOfPlayers (final int players)
    {
        _MinimumNumberOfPlayers = players;
    }

    public void setNumberOfDice (final int dice)
    {
        _NumberOfDice = dice;
    }

    public void setNumberOfNormalSpots (final int players, final int spots)
    {
        if (_NumberOfNormalSpots == null) {
            _NumberOfNormalSpots = new HashMap <Integer, Integer> ();
        }
        _NumberOfNormalSpots.put (Integer.valueOf (players),
                                  Integer.valueOf (spots));
    }

    public void setNumberOfPlayers (final int players)
    {
        _NumberOfPlayers = players;
    }

    public void setNumberOfTokensPerPlayer (final int players, final int tokens)
    {
        if (_NumberOfTokensPerPlayer == null) {
            _NumberOfTokensPerPlayer = new HashMap <Integer, Integer> ();
        }
        _NumberOfTokensPerPlayer.put (Integer.valueOf (players),
                                      Integer.valueOf (tokens));
    }

    public void setPlayerColor (final int player, final Color color)
    {
        if (_PlayerColors == null) {
            _PlayerColors = new HashMap <Integer, Color> ();
        }
        _PlayerColors.put (Integer.valueOf (player), color);
    }

    public void setPlayerMesh (final String path, final boolean isInternal)
    {
        _PlayerMesh = new GameMesh (path, isInternal);
    }

    public void setPlayerMesh (final String path, final Color color,
                               final boolean isInternal)
    {
        _PlayerMesh = new GameMesh (path, color, isInternal);
    }

    public void setSpotMesh (final String path, final Color color,
                             final boolean isInternal)
    {
        _SpotMesh = new GameMesh (path, color, isInternal);
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

    public void setTurnOutRetries (final int retries)
    {
        _TurnOutRetries = retries;
    }

    public void setTurnOutValue (final int turnOut)
    {
        _TurnOutValue = turnOut;
    }
}
