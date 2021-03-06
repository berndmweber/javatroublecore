/**
 * @file: com.innovail.trouble.core - ApplicationSettings.java
 * @date: Apr 17, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

import com.innovail.trouble.graphics.GameFont;
import com.innovail.trouble.graphics.GameMesh;
import com.innovail.trouble.uicomponent.BackgroundImage;
import com.innovail.trouble.uicomponent.MenuEntryFactory;
import com.innovail.trouble.utils.Parameters;

/**
 * 
 */
public final class ApplicationSettings
{
    /* SETTINGS */
    private Map <String, GameFont> _GameFonts;

    private Map <String, BackgroundImage> _BackgroundImages;

    private String _CopyRightNotice;

    private Map <String, GameMesh> _ApplicationAssets;

    private GameMesh _Logo;

    private Map <String, Map <String, GameMesh>> _MenuEntries;
    private Map <String, List <GameMesh>> _MenuEntryList;

    private GameMesh _BackArrow;
    /* END SETTINGS */

    private static ApplicationSettings instance;

    public static ApplicationSettings getInstance ()
    {
        if (instance == null) {
            instance = new ApplicationSettings ();
        }
        return instance;
    };

    private ApplicationSettings ()
    {}

    public GameMesh getApplicationAsset (final String name)
    {
        if ((_ApplicationAssets != null) && (!_ApplicationAssets.isEmpty ())) {
            if (_ApplicationAssets.containsKey (name)) {
                return _ApplicationAssets.get (name);
            }
        }
        return null;
    }

    public GameMesh getBackArrow ()
    {
        return _BackArrow;
    }

    public BackgroundImage getBackgroundImage (final String appPart)
    {
        if ((_BackgroundImages != null) && !_BackgroundImages.isEmpty ()) {
            if (_BackgroundImages.containsKey (appPart)) {
                return _BackgroundImages.get (appPart);
            }
        }
        return null;
    }

    public String getCopyRightNotice ()
    {
        return _CopyRightNotice;
    }

    public GameFont getGameFont (final String type)
    {
        if ((_GameFonts != null) && !_GameFonts.isEmpty ()) {
            if (_GameFonts.containsKey (type)) {
                return _GameFonts.get (type);
            }
        }
        return null;
    }

    public GameMesh getLogo ()
    {
        return _Logo;
    }

    public Map <String, GameMesh> getMenuEntries (final String appPart)
    {
        if ((_MenuEntries != null) && !_MenuEntries.isEmpty ()) {
            if (_MenuEntries.containsKey (appPart)) {
                return _MenuEntries.get (appPart);
            }
        }
        return null;
    }

    public GameMesh getMenuEntry (final String appPart, final String name)
    {
        if ((_MenuEntries != null) && !_MenuEntries.isEmpty ()) {
            if (_MenuEntries.containsKey (appPart)) {
                final Map <String, GameMesh> currentMap = _MenuEntries.get (appPart);
                if ((currentMap != null) && !currentMap.isEmpty ()) {
                    if (currentMap.containsKey (name)) {
                        return currentMap.get (name);
                    }
                }
            }
        }
        return null;
    }

    public List <GameMesh> getMenuEntryList (final String appPart)
    {
        if ((_MenuEntryList != null) && !_MenuEntryList.isEmpty ()) {
            if (_MenuEntryList.containsKey (appPart)) {
                return _MenuEntryList.get (appPart);
            }
        }
        return null;
    }

    public void setApplicationAsset (final String name, final String path,
                                     final boolean isInternal)
    {
        if (_ApplicationAssets == null) {
            _ApplicationAssets = new HashMap <String, GameMesh> ();
        }
        _ApplicationAssets.put (name, new GameMesh (path, isInternal));
    }

    public void setApplicationAsset (final String name, final String path,
                                     final Color color, final boolean isInternal)
    {
        if (_ApplicationAssets == null) {
            _ApplicationAssets = new HashMap <String, GameMesh> ();
        }
        _ApplicationAssets.put (name, new GameMesh (path, color, isInternal));
    }

    public void setBackArrow (final String path, final boolean isInternal)
    {
        _BackArrow = new GameMesh (path, isInternal);
    }

    public void setBackArrow (final String path, final Color color,
                              final boolean isInternal)
    {
        _BackArrow = new GameMesh (path, color, isInternal);
    }

    public void setBackgroundImage (final String appPart, final String path,
                                    final int width, final int height,
                                    final boolean isInternal)
    {
        if (_BackgroundImages == null) {
            _BackgroundImages = new HashMap <String, BackgroundImage> ();
        }
        final BackgroundImage tempBI = new BackgroundImage (path, width, height, isInternal);
        tempBI.createTexture ().setFilter (TextureFilter.Linear, TextureFilter.Linear);
        _BackgroundImages.put (appPart, tempBI);
    }

    public void setCopyRightNotice (final String notice)
    {
        _CopyRightNotice = notice;
    }

    public void setGameFont (final String fontType, final String fontFilePath,
                             final String fontImagePath,
                             final boolean isInternal)
    {
        if (_GameFonts == null) {
            _GameFonts = new HashMap <String, GameFont> ();
        }
        _GameFonts.put (fontType, new GameFont (fontType, fontFilePath, fontImagePath, isInternal));
    }

    public void setLogo (final String path, final boolean isInternal)
    {
        _Logo = new GameMesh (path, isInternal);
    }

    public void setLogo (final String path, final Color color,
                         final boolean isInternal)
    {
        _Logo = new GameMesh (path, color, isInternal);
    }

    public void setMenuEntry (final String appPart, final int index,
                              final String entryName, final String displayText)
    {
        setMenuEntry (appPart, index, entryName, displayText, null, null);
    }

    public void setMenuEntry (final String appPart, final int index,
                              final String entryName, final String displayText,
                              final String type, final Parameters params)
    {
        if (_MenuEntries == null) {
            _MenuEntries = new HashMap <String, Map <String, GameMesh>> ();
        }
        if (_MenuEntryList == null) {
            _MenuEntryList = new HashMap <String, List <GameMesh>> ();
        }
        if (!_MenuEntries.containsKey (appPart)) {
            _MenuEntries.put (appPart, new HashMap <String, GameMesh> ());
        }
        if (!_MenuEntryList.containsKey (appPart)) {
            _MenuEntryList.put (appPart, new ArrayList <GameMesh> ());
        }
        final Map <String, GameMesh> currentMap = _MenuEntries.get (appPart);
        final String [] name = new String [] { entryName, displayText };
        currentMap.put (entryName, MenuEntryFactory.createMenuEntry (name, type, params));
        final List <GameMesh> currentList = _MenuEntryList.get (appPart);
        currentList.add (index, currentMap.get (entryName));
    }
}
