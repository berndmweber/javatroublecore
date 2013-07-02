/**
 * @file: com.innovail.trouble.utils - SettingLoader.java
 * @date: Apr 14, 2012
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
import com.innovail.trouble.graphics.GameColor;

/**
 * 
 */
public class SettingLoader
{
    private static final String TAG = "SettingLoader";
    private static FileHandle   defaultSettingsFile;
    private static Element      defaultSettings;

    public static void loadSettings ()
    {
        defaultSettingsFile = Gdx.files.internal ("defaultsettings.xml");
        final XmlReader xmlReader = new XmlReader ();
        try {
            defaultSettings = xmlReader.parse (defaultSettingsFile);
            if (defaultSettings != null) {
                parseSettings ();
            }
        } catch (final IOException e) {
            Gdx.app.log (TAG, e.getMessage ());
        }
    }

    private static void parseApplicationSettings (final Element applicationSettings)
    {
        final Element assets = applicationSettings.getChildByName ("assets");
        if (assets != null) {
            final Array <Element> fonts = assets.getChildrenByName ("font");
            if ((fonts != null) && (fonts.size > 0)) {
                for (int i = 0; i < fonts.size; i++) {
                    final Element font = fonts.get (i);
                    if (font != null) {
                        ApplicationSettings.getInstance ().setGameFont (font.getAttribute ("type"),
                                                                        font.getAttribute ("file"),
                                                                        font.getAttribute ("image"),
                                                                        font.getBoolean ("is_internal"));
                    }
                }
            }
            final Array <Element> assetsA = assets.getChildrenByName ("asset");
            if ((assetsA != null) && (assetsA.size > 0)) {
                for (int i = 0; i < assetsA.size; i++) {
                    final Element asset = assetsA.get (i);
                    if (asset != null) {
                        ApplicationSettings.getInstance ().setApplicationAsset (asset.getAttribute ("name"),
                                                                                asset.getAttribute ("file"),
                                                                                asset.getBoolean ("is_internal"));
                    }
                }
            }
        }
        final Array <Element> menus = applicationSettings.getChildrenByName ("menu");
        if ((menus != null) && (menus.size > 0)) {
            for (int i = 0; i < menus.size; i++) {
                final Element menu = menus.get (i);
                Element current = menu.getChildByName ("copyright");
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
                        final Color color = new GameColor (colorE.getAttribute ("name"));
                        ApplicationSettings.getInstance ().setLogo (current.getAttribute ("file"),
                                                                    color,
                                                                    current.getBoolean ("is_internal"));
                    } else {
                        ApplicationSettings.getInstance ().setLogo (current.getAttribute ("file"),
                                                                    current.getBoolean ("is_internal"));
                    }
                }
                final Array <Element> entries = menu.getChildrenByName ("entry");
                if ((entries != null) && (entries.size > 0)) {
                    for (int j = 0; j < entries.size; j++) {
                        final Element entry = entries.get (j);
                        if (entry != null) {
                            final Parameters params = new StandardParameters ();
                            params.setParameter ("mincount",
                                                 entry.getInt ("mincount", 0));
                            params.setParameter ("maxcount",
                                                 entry.getInt ("maxcount", 0));
                            ApplicationSettings.getInstance ().setMenuEntry (menu.getAttribute ("type") + "Menu",
                                                                             entry.getInt ("index"),
                                                                             entry.getAttribute ("name"),
                                                                             entry.getAttribute ("text"),
                                                                             entry.getAttribute ("type",
                                                                                                 null),
                                                                             params);

                        }
                    }
                }
            }
        }
        final Element game = applicationSettings.getChildByName ("game");
        if (game != null) {
            Element current = game.getChildByName ("background");
            if (current != null) {
                ApplicationSettings.getInstance ().setBackgroundImage ("game",
                                                                       current.getAttribute ("file"),
                                                                       current.getInt ("width"),
                                                                       current.getInt ("height"),
                                                                       current.getBoolean ("is_internal"));
            }
            current = game.getChildByName ("backarrow");
            if (current != null) {
                final Element colorE = current.getChildByName ("color");
                if (colorE != null) {
                    final Color color = new GameColor (colorE.getAttribute ("name"));
                    ApplicationSettings.getInstance ().setBackArrow (current.getAttribute ("file"),
                                                                     color,
                                                                     current.getBoolean ("is_internal"));
                } else {
                    ApplicationSettings.getInstance ().setBackArrow (current.getAttribute ("file"),
                                                                     current.getBoolean ("is_internal"));
                }
            }
        }
    }

    private static void parseGameSettings (final Element gameSettings)
    {
        Element current = gameSettings.getChildByName ("field");
        if (current != null) {
            GameSettings.getInstance ().setFieldList (current.getAttribute ("default"),
                                                      current.getAttribute ("extension"),
                                                      current.getBoolean ("is_internal"));
        }
        final Array <Element> playersSettings = gameSettings.getChildrenByName ("players");
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
            final Array <Element> players = current.getChildrenByName ("player");
            if ((players != null) && (players.size > 0)) {
                for (int i = 0; i < players.size; i++) {
                    final Element player = players.get (i);
                    final Element colorE = player.getChildByName ("color");
                    final Color color = new GameColor (colorE.getAttribute ("name"));
                    GameSettings.getInstance ().setPlayerColor (player.getInt ("number") - 1,
                                                                color);
                }
            }
        }
        current = gameSettings.getChildByName ("playerannouncement");
        if (current != null) {
            Element colorE = current.getChildByName ("color");
            if (colorE != null) {
                final Color color = new GameColor (colorE.getAttribute ("name"));
                GameSettings.getInstance ().setPlayerMesh (current.getAttribute ("file"),
                                                           color,
                                                           current.getBoolean ("is_internal"));
            } else {
                GameSettings.getInstance ().setPlayerMesh (current.getAttribute ("file"),
                                                           current.getBoolean ("is_internal"));
            }
            final Array <Element> numbers = current.getChildrenByName ("number");
            if ((numbers != null) && (numbers.size > 0)) {
                for (int i = 0; i < numbers.size; i++) {
                    colorE = numbers.get (i).getChildByName ("color");
                    if (colorE != null) {
                        final Color color = new GameColor (colorE.getAttribute ("name"));
                        GameSettings.getInstance ().addPlayerNumber (numbers.get (i).getAttribute ("file"),
                                                                     color,
                                                                     numbers.get (i).getBoolean ("is_internal"));
                    } else {
                        GameSettings.getInstance ().addPlayerNumber (numbers.get (i).getAttribute ("file"),
                                                                     numbers.get (i).getBoolean ("is_internal"));
                    }
                }
            }
        }
        current = gameSettings.getChildByName ("spots");
        if (current != null) {
            final Element colorE = current.getChildByName ("color");
            if (colorE != null) {
                final Color color = new GameColor (colorE.getAttribute ("name"));
                GameSettings.getInstance ().setSpotMesh (current.getAttribute ("file"),
                                                         color,
                                                         current.getBoolean ("is_internal"));
            }
        }
        current = gameSettings.getChildByName ("tokens");
        if (current != null) {
            GameSettings.getInstance ().setTokenMesh (current.getAttribute ("file"),
                                                      current.getBoolean ("is_internal"));
            final Element sound = current.getChildByName ("sound");
            if (sound != null) {
                GameSettings.getInstance ().setTokenSound (sound.getAttribute ("file"),
                                                           sound.getBoolean ("is_internal"));
            }
        }
        current = gameSettings.getChildByName ("dice");
        if (current != null) {
            GameSettings.getInstance ().setDiceMesh (current.getAttribute ("file"),
                                                     current.getBoolean ("is_internal"),
                                                     current.getAttribute ("texture_file"),
                                                     current.getAttribute ("texture_color_format"));
            GameSettings.getInstance ().setNumberOfDice (current.getInt ("number"));
            final Element sound = current.getChildByName ("sound");
            if (sound != null) {
                GameSettings.getInstance ().setDiceSound (sound.getAttribute ("file"),
                                                          sound.getBoolean ("is_internal"));
            }
        }
        current = gameSettings.getChildByName ("turnout");
        if (current != null) {
            GameSettings.getInstance ().setTurnOutValue (current.getInt ("value"));
            GameSettings.getInstance ().setTurnOutRetries (current.getInt ("retries"));
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

}
