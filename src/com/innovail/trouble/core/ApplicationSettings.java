/**
 * @file:   com.innovail.trouble.core - ApplicationSettings.java
 * @date:   Apr 17, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.innovail.trouble.utils.BackgroundImage;
import com.innovail.trouble.utils.GameFont;
import com.innovail.trouble.utils.MenuEntryMesh;
import com.innovail.trouble.utils.GameMesh;

/**
 * 
 */
public final class ApplicationSettings {
    /* SETTINGS */
    private Map<String, GameFont> _GameFonts;
    
    private Map<String, BackgroundImage> _BackgroundImages;
    
    private String _CopyRightNotice;
    
    private GameMesh _Logo;
    
    private Map<String, Map<String, GameMesh>> _MenuEntries;
    /* END SETTINGS */
    
    private static ApplicationSettings instance;

    private ApplicationSettings () {};

    public static ApplicationSettings getInstance ()
    {
        if (instance == null) {
            instance = new ApplicationSettings();
        }
        return instance;
    }
    
    public void setGameFont (final String appPart, final String fontFilePath,
                              final String fontImagePath, final boolean isInternal)
    {
        if (_GameFonts == null) {
            _GameFonts = new HashMap<String, GameFont> ();
        }
        _GameFonts.put (appPart, new GameFont (fontFilePath, fontImagePath, isInternal));
    }
    
    public GameFont getGameFont (final String appPart)
    {
        if ((_GameFonts != null) && !_GameFonts.isEmpty ()) {
            if (_GameFonts.containsKey (appPart)) {
                return _GameFonts.get (appPart);
            }
        }
        return null;
    }
    
    public void setBackgroundImage (final String appPart, final String path,
                                     final int width, final int height,
                                     final boolean isInternal)
    {
        if (_BackgroundImages == null) {
            _BackgroundImages = new HashMap<String, BackgroundImage> ();
        }
        _BackgroundImages.put (appPart, new BackgroundImage (path, width, height, isInternal));
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
    
    public void setMenuEntry (final String appPart, final String entryName,
                               final String path, final boolean isInternal)
    {
        if (_MenuEntries == null) {
            _MenuEntries = new HashMap <String, Map <String, GameMesh>> ();
        }
        if (!_MenuEntries.containsKey (appPart)) {
            _MenuEntries.put (appPart, new HashMap <String, GameMesh> ());
        }
        final Map <String, GameMesh> currentMap = _MenuEntries.get (appPart);
        currentMap.put (entryName, new MenuEntryMesh (entryName, path, isInternal));
    }

    public void setMenuEntry (final String appPart, final String entryName,
                               final String path, final Color color,
                               final boolean isInternal)
    {
        if (_MenuEntries == null) {
            _MenuEntries = new HashMap <String, Map <String, GameMesh>> ();
        }
        if (!_MenuEntries.containsKey (appPart)) {
            _MenuEntries.put (appPart, new HashMap <String, GameMesh> ());
        }
        final Map <String, GameMesh> currentMap = _MenuEntries.get (appPart);
        currentMap.put (entryName, new MenuEntryMesh (entryName, path, color, isInternal));
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

    public Map <String, GameMesh> getMenuEntries (final String appPart)
    {
        if ((_MenuEntries != null) && !_MenuEntries.isEmpty ()) {
            if (_MenuEntries.containsKey (appPart)) {
                return _MenuEntries.get (appPart);
            }
        }
        return null;
    }

    public void setLogo (final String path, final Color color,
                          final boolean isInternal)
    {
        _Logo = new GameMesh (path, color, isInternal);
    }
    
    public void setLogo (final String path, final boolean isInternal)
    {
        _Logo = new GameMesh (path, isInternal);
    }
    
    public GameMesh getLogo ()
    {
        return _Logo;
    }
    
    public String getCopyRightNotice ()
    {
        return _CopyRightNotice;
    }
    
    public void setCopyRightNotice (final String notice)
    {
        _CopyRightNotice = notice;
    }
 }
