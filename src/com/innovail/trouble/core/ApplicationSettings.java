/**
 * @file:   com.innovail.trouble.core - ApplicationSettings.java
 * @date:   Apr 17, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.HashMap;
import java.util.Map;

import com.innovail.trouble.utils.BackgroundImage;
import com.innovail.trouble.utils.GameFont;

/**
 * 
 */
public class ApplicationSettings {
    /* SETTINGS */
    private Map<String, GameFont> _GameFonts;
    
    private Map<String, BackgroundImage> _BackgroundImages;
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
 }
