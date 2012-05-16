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
    private static final String TAG = "SettingLoader";
    private static FileHandle defaultSettingsFile;
    private static Element defaultSettings;
    
    public static void loadSettings ()
    {
        defaultSettingsFile = Gdx.files.internal ("defaultsettings.xml");
        final XmlReader xmlReader = new XmlReader();
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
        Element settings = defaultSettings.getChildByName ("application");
        if (settings != null) {
            parseApplicationSettings (settings);
        }
        settings = defaultSettings.getChildByName ("game");
        if (settings != null) {
            parseGameSettings (settings);
        }
    }
    
    private static void parseApplicationSettings (final Element applicationSettings)
    {
        final Array<Element> menus = applicationSettings.getChildrenByName ("menu");
        if ((menus != null) && (menus.size > 0)) {
            for (int i = 0; i < menus.size; i++) {
                final Element menu = menus.get (i);
                Element current = menu.getChildByName ("font");
                if (current != null) {
                    ApplicationSettings.getInstance ().setGameFont (menu.getAttribute ("type") + "Menu",
                                                                    current.getAttribute ("file"),
                                                                    current.getAttribute ("image"),
                                                                    current.getBoolean ("is_internal"));
                }
                current = menu.getChildByName ("copyright");
                if (current != null) {
                    ApplicationSettings.getInstance ().setCopyRightNotice (current.getText ());
                }
                current = menu.getChildByName ("background");
                if (current != null) {
                    ApplicationSettings.getInstance ().setBackgroundImage (menu.getAttribute ("type") + "Menu",
                                                                           current.getAttribute ("file"),
                                                                           current.getInt ("width"),
                                                                           current.getInt ("height"),
                                                                           current.getBoolean ("is_internal"));
                }
                current = menu.getChildByName ("logo");
                if (current != null) {
                    final Element colorE = current.getChildByName ("color");
                    if (colorE != null) {
                        final Color color = new Color ();
                        color.set (colorE.getFloat ("r"), colorE.getFloat ("g"), colorE.getFloat ("b"), colorE.getFloat ("a"));
                        ApplicationSettings.getInstance ().setLogo (current.getAttribute ("file"), color, current.getBoolean ("is_internal"));
                    } else {
                        ApplicationSettings.getInstance ().setLogo (current.getAttribute ("file"), current.getBoolean ("is_internal"));
                    }
                }
                final Array<Element> entries = menu.getChildrenByName ("entry");
                if ((entries != null) && (entries.size > 0)) {
                    for (int j = 0; j < entries.size; j++) {
                        final Element entry = entries.get (j);
                        if (entry != null) {
                            ApplicationSettings.getInstance ().setMenuEntry (menu.getAttribute ("type") + "Menu",
                                                                             entry.getAttribute ("name"),
                                                                             entry.getAttribute ("file"),
                                                                             entry.getBoolean ("is_internal"));
                        }
                    }
                }
            }
        }
        final Element game = applicationSettings.getChildByName ("game");
        if (game != null) {
            final Element background = game.getChildByName ("background");
            if (background != null) {
                ApplicationSettings.getInstance ().setBackgroundImage ("game",
                                                                       background.getAttribute ("file"),
                                                                       background.getInt ("width"),
                                                                       background.getInt ("height"),
                                                                       background.getBoolean ("is_internal"));
            }
        }
    }
    
    private static void parseGameSettings (final Element gameSettings)
    {
        Element current = gameSettings.getChildByName ("numberofplayers");
        if (current != null) {
            GameSettings.getInstance ().setNumberOfPlayers (current.getInt ("default"));
            GameSettings.getInstance ().setMinimumNumberOfPlayers (current.getInt ("minimum"));
        }
        final Array<Element> playersSettings = gameSettings.getChildrenByName ("players");
        if ((playersSettings != null) && (playersSettings.size > 0)) {
            for (int i = 0; i < playersSettings.size; i++) {
                final Element players = playersSettings.get (i);
                final Element defaultTokens = players.getChildByName ("tokens");
                GameSettings.getInstance ().setNumberOfTokensPerPlayer (players.getInt ("number"),
                                                                        defaultTokens.getInt ("number"));
                final Element defaultNormalSpots = players.getChildByName ("spots");
                GameSettings.getInstance ().setNumberOfNormalSpots (players.getInt ("number"),
                                                                    defaultNormalSpots.getInt ("normal"));
            }
        }
        current = gameSettings.getChildByName ("playerdefaults");
        if (current != null) {
            final Array<Element> players = current.getChildrenByName ("player");
            if ((players != null) && (players.size > 0)) {
                for (int i = 0; i < players.size; i++) {
                    final Element player = players.get (i);
                    final Element colorE = player.getChildByName ("color");
                    final Color color = new Color ();
                    color.set (colorE.getFloat ("r"), colorE.getFloat ("g"), colorE.getFloat ("b"), colorE.getFloat ("a"));
                    GameSettings.getInstance ().setPlayerColor (player.getInt ("number") - 1, color);
                }
            }
        }
        current = gameSettings.getChildByName ("spots");
        if (current != null) {
            final Element colorE = current.getChildByName ("color");
            if (colorE != null) {
                final Color color = new Color ();
                color.set (colorE.getFloat ("r"), colorE.getFloat ("g"), colorE.getFloat ("b"), colorE.getFloat ("a"));
                GameSettings.getInstance ().setSpotMesh (current.getAttribute ("file"),
                                                         color,
                                                         current.getBoolean ("is_internal"));
            }
        }
        current = gameSettings.getChildByName ("tokens");
        if (current != null) {
            GameSettings.getInstance ().setTokenMesh (current.getAttribute ("file"), current.getBoolean ("is_internal"));
        }
        current = gameSettings.getChildByName ("dice");
        if (current != null) {
            GameSettings.getInstance ().setDiceMesh (current.getAttribute ("file"),
                                                     current.getBoolean ("is_internal"),
                                                     current.getAttribute ("texture_file"),
                                                     current.getAttribute ("texture_color_format"));
            GameSettings.getInstance ().setNumberOfDice (current.getInt ("number"));
        }
    }
    
}
