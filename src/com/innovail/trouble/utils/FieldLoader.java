/**
 * @file: com.innovail.trouble.utils - FieldLoader.java
 * @date: Jul 1, 2013
 * @author: bweber
 */
package com.innovail.trouble.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import com.innovail.trouble.core.gameelement.PreSpot;
import com.innovail.trouble.core.gameelement.Spot;

/**
 * 
 */
public class FieldLoader
{
    private static final String TAG = "FieldLoader";

    private static List <PreSpot> cleanSpotList (final Map <String, PreSpot> spots)
    {
        final List <PreSpot> spotList = new ArrayList <PreSpot> ();
        final Iterator <PreSpot> spotsIt = spots.values ().iterator ();
        while (spotsIt.hasNext ()) {
            final PreSpot spot = spotsIt.next ();
            final String nextPosition = spot.getNextSpotPosition ();
            if ((nextPosition != null) && !nextPosition.isEmpty ()) {
                final PreSpot nextSpot = spots.get (nextPosition);
                spot.setNextSpot (nextSpot);
            }
            final String nextTurnOutPosition = spot.getNextTurnOutSpotPosition ();
            if ((nextTurnOutPosition != null) && !nextTurnOutPosition.isEmpty ()) {
                final PreSpot nextSpot = spots.get (nextTurnOutPosition);
                spot.setNextTurnoutSpot (nextSpot);
            }

            spotList.add (spot);
        }

        return spotList;
    }

    private static String getCurrentPosition (final int line, final char column)
    {
        return new String (Character.toString (column) + Integer.toString (line));
    }

    public static List <PreSpot> getField (final FileHandle fieldFile)
    {
        final Map <String, PreSpot> spots = parseFile (fieldFile);

        return cleanSpotList (spots);
    }

    public static int [] getPlayerInfo (final FileHandle fieldFile)
    {
        final int [] noOfPlayers = new int [2];
        try {
            final InputStream is = fieldFile.read ();
            final BufferedReader reader = new BufferedReader (new InputStreamReader (is));
            String l = reader.readLine ();
            while (l != null) {
                if (!l.isEmpty () && !l.startsWith ("#")) {
                    /* '|'-character */
                    final String [] tokens = l.split ("\\x7c");
                    if (tokens[0].equals ("P")) {
                        if ((tokens[1] != null) && !tokens[1].isEmpty ()) {
                            noOfPlayers[1] = Integer.parseInt (tokens[1]);
                        }
                        if ((tokens[2] != null) && !tokens[2].isEmpty ()) {
                            noOfPlayers[0] = Integer.parseInt (tokens[2]);
                        }
                    }
                    break;
                }
                l = reader.readLine ();
            }
            reader.close ();
            is.close ();
        } catch (final Exception e) {
            Gdx.app.log (TAG, e.getMessage ());
        }
        return noOfPlayers;
    }

    private static Map <String, PreSpot> parseFile (final FileHandle fieldFile)
    {
        char columnPosition = 'A';
        int linePosition = 1;

        try {
            final Map <String, PreSpot> spots = new HashMap <String, PreSpot> ();
            final InputStream is = fieldFile.read ();
            final BufferedReader reader = new BufferedReader (new InputStreamReader (is));
            String l = reader.readLine ();
            while (l != null) {
                if (!l.isEmpty () && !l.startsWith ("#") && !l.startsWith ("P")) {
                    /* Split on whitespace first. */
                    final String [] spotTokens = l.split ("\\s+");
                    for (final String spotToken : spotTokens) {
                        /* '|'-character */
                        final String [] tokens = spotToken.split ("\\x7c");
                        if (tokens[0].equals ("E")) {
                            columnPosition++;
                            continue;
                        } else if (tokens[0].equals ("F")) {
                            final PreSpot spot = new PreSpot (Spot.Attributes.SPOT_IS_FINISH);
                            if ((tokens[1] != null) && !tokens[1].isEmpty ()) {
                                final int playerNo = Integer.parseInt (tokens[1]);
                                spot.setPlayerNumber (playerNo - 1);
                            }
                            if ((tokens.length > 2) && (tokens[2] != null) && !tokens[2].isEmpty ()) {
                                spot.setNextSpotPosition (tokens[2]);
                            }
                            spot.setPosition (getCurrentPosition (linePosition,
                                                                  columnPosition++));
                            spots.put (spot.getPosition (), spot);
                        } else if (tokens[0].equals ("H")) {
                            final PreSpot spot = new PreSpot (Spot.Attributes.SPOT_IS_HOME);
                            if ((tokens[1] != null) && !tokens[1].isEmpty ()) {
                                final int playerNo = Integer.parseInt (tokens[1]);
                                spot.setPlayerNumber (playerNo - 1);
                            }
                            if ((tokens[2] != null) && !tokens[2].isEmpty ()) {
                                spot.setNextSpotPosition (tokens[2]);
                            }
                            spot.setPosition (getCurrentPosition (linePosition,
                                                                  columnPosition++));
                            spots.put (spot.getPosition (), spot);
                        } else if (tokens[0].equals ("N")) {
                            final PreSpot spot = new PreSpot (Spot.Attributes.SPOT_IS_NORMAL);
                            if ((tokens[1] != null) && !tokens[1].isEmpty ()) {
                                spot.setNextSpotPosition (tokens[1]);
                            }
                            spot.setPosition (getCurrentPosition (linePosition,
                                                                  columnPosition++));
                            spots.put (spot.getPosition (), spot);
                        } else if (tokens[0].equals ("S")) {
                            final PreSpot spot = new PreSpot (Spot.Attributes.SPOT_IS_START);
                            if ((tokens[1] != null) && !tokens[1].isEmpty ()) {
                                final int playerNo = Integer.parseInt (tokens[1]);
                                spot.setPlayerNumber (playerNo - 1);
                            }
                            if ((tokens[2] != null) && !tokens[2].isEmpty ()) {
                                spot.setNextSpotPosition (tokens[2]);
                            }
                            spot.setPosition (getCurrentPosition (linePosition,
                                                                  columnPosition++));
                            spots.put (spot.getPosition (), spot);
                        } else if (tokens[0].equals ("T")) {
                            final PreSpot spot = new PreSpot (Spot.Attributes.SPOT_IS_TURN_OUT);
                            if ((tokens[1] != null) && !tokens[1].isEmpty ()) {
                                final int playerNo = Integer.parseInt (tokens[1]);
                                spot.setPlayerNumber (playerNo - 1);
                            }
                            if ((tokens[2] != null) && !tokens[2].isEmpty ()) {
                                spot.setNextSpotPosition (tokens[2]);
                            }
                            if ((tokens[3] != null) && !tokens[3].isEmpty ()) {
                                spot.setNextTurnOutSpotPosition (tokens[3]);
                            }
                            spot.setPosition (getCurrentPosition (linePosition,
                                                                  columnPosition++));
                            spots.put (spot.getPosition (), spot);
                        }
                    }
                    linePosition++;
                    columnPosition = 'A';
                }
                l = reader.readLine ();
            }
            reader.close ();
            is.close ();
            return spots;
        } catch (final Exception e) {
            Gdx.app.log (TAG, e.getMessage ());
        }
        return null;
    }
}
