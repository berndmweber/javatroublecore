/**
 * @file:   com.innovail.trouble.core - ApplicationSettings.java
 * @date:   Apr 17, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.awt.Menu;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.innovail.trouble.utils.BackgroundImage;
import com.innovail.trouble.utils.GameFont;
import com.innovail.trouble.utils.MenuEntryMesh;
import com.innovail.trouble.utils.MenuMesh;

/**
 * 
 */
public class ApplicationSettings {
    /* SETTINGS */
    private Map<String, GameFont> _GameFonts;
    
    private Map<String, BackgroundImage> _BackgroundImages;
    
    private String _CopyRightNotice;
    
    private MenuMesh _Logo;
    
    private Map<String, Map<String, MenuMesh>> _MenuEntries;
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
    
    public void setGameFont (String appPart, String fontFilePath,
                             String fontImagePath, boolean isInternal)
    {
        if (_GameFonts == null) {
            _GameFonts = new HashMap<String, GameFont> ();
        }
        _GameFonts.put (appPart, new GameFont (fontFilePath, fontImagePath, isInternal));
    }
    
    public GameFont getGameFont (String appPart)
    {
        if ((_GameFonts != null) && !_GameFonts.isEmpty ()) {
            if (_GameFonts.containsKey (appPart)) {
                return _GameFonts.get (appPart);
            }
        }
        return null;
    }
    
    public void setBackgroundImage (String appPart, String path, int width,
                                    int height, boolean isInternal)
    {
        if (_BackgroundImages == null) {
            _BackgroundImages = new HashMap<String, BackgroundImage> ();
        }
        _BackgroundImages.put (appPart, new BackgroundImage (path, width, height, isInternal));
    }
    
    public BackgroundImage getBackgroundImage (String appPart)
    {
        if ((_BackgroundImages != null) && !_BackgroundImages.isEmpty ()) {
            if (_BackgroundImages.containsKey (appPart)) {
                return _BackgroundImages.get (appPart);
            }
        }
        return null;
    }
    
    public void setMenuEntry (String appPart, String entryName, String path,
                               boolean isInternal)
    {
        if (_MenuEntries == null) {
            _MenuEntries = new HashMap <String, Map <String, MenuMesh>> ();
        }
        if (!_MenuEntries.containsKey (appPart)) {
            _MenuEntries.put (appPart, new HashMap <String, MenuMesh> ());
        }
        HashMap <String, MenuMesh> currentMap = (HashMap <String, MenuMesh>) _MenuEntries.get (appPart);
        currentMap.put (entryName, new MenuEntryMesh (entryName, path, isInternal));
    }

    public void setMenuEntry (String appPart, String entryName, String path,
                               Color color, boolean isInternal)
    {
        if (_MenuEntries == null) {
            _MenuEntries = new HashMap <String, Map <String, MenuMesh>> ();
        }
        if (!_MenuEntries.containsKey (appPart)) {
            _MenuEntries.put (appPart, new HashMap <String, MenuMesh> ());
        }
        HashMap <String, MenuMesh> currentMap = (HashMap <String, MenuMesh>) _MenuEntries.get (appPart);
        currentMap.put (entryName, new MenuEntryMesh (entryName, path, color, isInternal));
    }
    
    public MenuMesh getMenuEntry (String appPart, String name)
    {
        if ((_MenuEntries != null) && !_MenuEntries.isEmpty ()) {
            if (_MenuEntries.containsKey (appPart)) {
                 Map <String, MenuMesh> currentMap = _MenuEntries.get (appPart);
                 if ((currentMap != null) && !currentMap.isEmpty ()) {
                     if (currentMap.containsKey (name)) {
                         return currentMap.get (name);
                     }
                 }
            }
        }
        return null;
    }

    public HashMap <String, MenuMesh> getMenuEntries (String appPart)
    {
        if ((_MenuEntries != null) && !_MenuEntries.isEmpty ()) {
            if (_MenuEntries.containsKey (appPart)) {
                return (HashMap <String, MenuMesh>) _MenuEntries.get (appPart);
            }
        }
        return null;
    }

    public void setLogo (String path, Color color, boolean isInternal)
    {
        _Logo = new MenuMesh (path, color, isInternal);
    }
    
    public void setLogo (String path, boolean isInternal)
    {
        _Logo = new MenuMesh (path, isInternal);
    }
    
    public MenuMesh getLogo ()
    {
        return _Logo;
    }
    
    public String getCopyRightNotice ()
    {
        return _CopyRightNotice;
    }
    
    public void setCopyRightNotice (String notice)
    {
        _CopyRightNotice = notice;
    }
 }
