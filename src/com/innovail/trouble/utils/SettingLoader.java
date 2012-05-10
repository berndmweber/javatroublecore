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
import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.core.GameSettings;

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
        Element applicationSettings = defaultSettings.getChildByName ("application");
        if (applicationSettings != null) {
            parseApplicationSettings (applicationSettings);
        }
        applicationSettings = null;
        Element gameSettings = defaultSettings.getChildByName ("game");
        if (gameSettings != null) {
            parseGameSettings (gameSettings);
        }
        gameSettings = null;
    }
    
    private static void parseApplicationSettings (Element applicationSettings)
    {
        Array<Element> menus = applicationSettings.getChildrenByName ("menu");
        if ((menus != null) && (menus.size > 0)) {
            for (int i = 0; i < menus.size; i++) {
                Element menu = menus.get (i);
                Element font = menu.getChildByName ("font");
                if (font != null) {
                    ApplicationSettings.getInstance ().setGameFont (menu.getAttribute ("type") + "Menu",
                                                                    font.getAttribute ("file"),
                                                                    font.getAttribute ("image"),
                                                                    font.getBoolean ("is_internal"));
                }
                font = null;
                Element copyright = menu.getChildByName ("copyright");
                if (copyright != null) {
                    ApplicationSettings.getInstance ().setCopyRightNotice (copyright.getText ());
                }
                copyright = null;
                Element background = menu.getChildByName ("background");
                if (background != null) {
                    ApplicationSettings.getInstance ().setBackgroundImage (menu.getAttribute ("type") + "Menu",
                                                                           background.getAttribute ("file"),
                                                                           background.getInt ("width"),
                                                                           background.getInt ("height"),
                                                                           background.getBoolean ("is_internal"));
                }
                background = null;
                Element logo = menu.getChildByName ("logo");
                if (logo != null) {
                    Element colorE = logo.getChildByName ("color");
                    if (colorE != null) {
                        Color color = new Color ();
                        color.set (colorE.getFloat ("r"), colorE.getFloat ("g"), colorE.getFloat ("b"), colorE.getFloat ("a"));
                        ApplicationSettings.getInstance ().setLogo (logo.getAttribute ("file"), color, logo.getBoolean ("is_internal"));
                    } else {
                        ApplicationSettings.getInstance ().setLogo (logo.getAttribute ("file"), logo.getBoolean ("is_internal"));
                    }
                }
                logo = null;
                Array<Element> entries = menu.getChildrenByName ("entry");
                if ((entries != null) && (entries.size > 0)) {
                    for (int j = 0; j < entries.size; j++) {
                        Element entry = entries.get (j);
                        if (entry != null) {
                            ApplicationSettings.getInstance ().setMenuEntry (menu.getAttribute ("type") + "Menu",
                                                                             entry.getAttribute ("name"),
                                                                             entry.getAttribute ("file"),
                                                                             entry.getBoolean ("is_internal"));
                        }
                    }
                }
                entries = null;
                menu = null;
            }
        }
        menus = null;
        Element game = applicationSettings.getChildByName ("game");
        if (game != null) {
            Element background = game.getChildByName ("background");
            if (background != null) {
                ApplicationSettings.getInstance ().setBackgroundImage ("game",
                                                                       background.getAttribute ("file"),
                                                                       background.getInt ("width"),
                                                                       background.getInt ("height"),
                                                                       background.getBoolean ("is_internal"));
            }
            background = null;
        }
        game = null;
    }
    
    private static void parseGameSettings (Element gameSettings)
    {
        Element numberOfPlayers = gameSettings.getChildByName ("numberofplayers");
        if (numberOfPlayers != null) {
            GameSettings.getInstance ().setNumberOfPlayers (numberOfPlayers.getInt ("default"));
            GameSettings.getInstance ().setMinimumNumberOfPlayers (numberOfPlayers.getInt ("minimum"));
        }
        numberOfPlayers = null;
        Array<Element> playersSettings = gameSettings.getChildrenByName ("players");
        if ((playersSettings != null) && (playersSettings.size > 0)) {
            for (int i = 0; i < playersSettings.size; i++) {
                Element players = playersSettings.get (i);
                Element defaultTokens = players.getChildByName ("tokens");
                GameSettings.getInstance ().setNumberOfTokensPerPlayer (players.getInt ("number"),
                                                                    defaultTokens.getInt ("number"));
                Element defaultNormalSpots = players.getChildByName ("spots");
                GameSettings.getInstance ().setNumberOfNormalSpots (players.getInt ("number"),
                                                                defaultNormalSpots.getInt ("normal"));
            }
        }
        playersSettings = null;
        Element playerDefaults = gameSettings.getChildByName ("playerdefaults");
        if (playerDefaults != null) {
            Array<Element> players = playerDefaults.getChildrenByName ("player");
            if ((players != null) && (players.size > 0)) {
                for (int i = 0; i < players.size; i++) {
                    Element player = players.get (i);
                    Element colorE = player.getChildByName ("color");
                    Color color = new Color ();
                    color.set (colorE.getFloat ("r"), colorE.getFloat ("g"), colorE.getFloat ("b"), colorE.getFloat ("a"));
                    GameSettings.getInstance ().setPlayerColor (player.getInt ("number"), color);
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
                GameSettings.getInstance ().setSpotMesh (spots.getAttribute ("file"),
                                                         color,
                                                         spots.getBoolean ("is_internal"));
            }
        }
        spots = null;
        Element tokens = gameSettings.getChildByName ("tokens");
        if (tokens != null) {
            GameSettings.getInstance ().setTokenMesh (tokens.getAttribute ("file"), tokens.getBoolean ("is_internal"));
        }
        tokens = null;
    }
    
}
