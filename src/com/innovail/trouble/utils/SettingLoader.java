/**
 * @file:   com.innovail.trouble.utils - SettingLoader.java
 * @date:   Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.innovail.trouble.core.Settings;

/**
 * 
 */
public class SettingLoader {
    private static String TAG = "SettingLoader";
    private static FileHandle defaultSettingsFile;
    private static Element defaultSettings;
    
    public static void loadSettings ()
    {
        defaultSettingsFile = Gdx.files.internal ("defaultsettings.xml");
        XmlReader xmlReader = new XmlReader();
        try {
            defaultSettings = xmlReader.parse (defaultSettingsFile);
            if (defaultSettings != null) {
                parseSettings ();
            }
        } catch (IOException e) {
            Gdx.app.log (TAG, e.getMessage ());
        }
    }

    private static void parseSettings ()
    {
        Element gameSettings = defaultSettings.getChildByName ("game");
        if (gameSettings != null) {
            parseGameSettings (gameSettings);
        }
    }
    
    private static void parseGameSettings (Element gameSettings)
    {
        Element numberOfPlayers = gameSettings.getChildByName ("numberofplayers");
        if (numberOfPlayers != null) {
            Settings.getInstance ().setNumberOfPlayers (numberOfPlayers.getInt ("default"));
        }
        numberOfPlayers = null;
        Element tokens = gameSettings.getChildByName ("tokens");
        if (tokens != null) {
            Array<Element> defaultTokens = tokens.getChildrenByName ("players");
            if ((defaultTokens != null) && (defaultTokens.size > 0)) {
                for (int i = 0; i < defaultTokens.size; i++) {
                    Element defaultPlayers = defaultTokens.get (i);
                    Settings.getInstance ().setNumberOfTokensPerPlayer (defaultPlayers.getInt ("number"),
                                                                        Integer.parseInt (defaultPlayers.getText ()));
                }
            }
        }
        tokens = null;
        Element playerDefaults = gameSettings.getChildByName ("playerdefaults");
        if (playerDefaults != null) {
            Array<Element> players = playerDefaults.getChildrenByName ("player");
            if ((players != null) && (players.size > 0)) {
                for (int i = 0; i < players.size; i++) {
                    Element player = players.get (i);
                    Element colorE = player.getChildByName ("color");
                    Color color = new Color ();
                    color.set (colorE.getFloat ("r"), colorE.getFloat ("g"), colorE.getFloat ("b"), colorE.getFloat ("a"));
                    Settings.getInstance ().setPlayerColor (player.getInt ("number"), color);
                }
            }
        }
        playerDefaults = null;
        Element spots = gameSettings.getChildByName ("spots");
        if (spots != null) {
            Element colorE = spots.getChildByName ("color");
            if (colorE != null) {
                Color color = new Color ();
                color.set (colorE.getFloat ("r"), colorE.getFloat ("g"), colorE.getFloat ("b"), colorE.getFloat ("a"));
                Settings.getInstance ().setSpotDefaultColor (color);
            }
        }
        spots = null;
    }
    
}
