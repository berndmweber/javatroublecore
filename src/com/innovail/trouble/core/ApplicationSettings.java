/**
 * @file:   com.innovail.trouble.core - ApplicationSettings.java
 * @date:   Apr 17, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.HashMap;
import java.util.Map;

import com.innovail.trouble.utils.GameFont;

/**
 * 
 */
public class ApplicationSettings {
    /* SETTINGS */
    private Map<String, GameFont> _GameFonts;
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
 }
