/**
 * @file:   com.innovail.trouble.graphics - GameColor.java
 * @date:   Jun 20, 2013
 * @author: berndmicweber
 */
package com.innovail.trouble.graphics;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;

/**
 * 
 */
public class GameColor extends Color
{
    private static final Map <String, Color> _ColorMap;
    static
    {
        _ColorMap = new HashMap <String, Color> ();
        _ColorMap.put ("black", Color.BLACK);
        _ColorMap.put ("blue", Color.BLUE);
        _ColorMap.put ("clear", Color.CLEAR);
        _ColorMap.put ("cyan", Color.CYAN);
        _ColorMap.put ("dark_blue", new Color (0.0f, 0.0f, 0.25f, 1.0f));
        _ColorMap.put ("dark_clear_blue", new Color (0.0f, 0.0f, 0.25f, 0.5f));
        _ColorMap.put ("dark_cyan", new Color(0.0f, 0.25f, 0.25f, 1.0f));
        _ColorMap.put ("dark_green", new Color(0.0f, 0.25f, 0.0f, 1.0f));
        _ColorMap.put ("dark_grey", Color.DARK_GRAY);
        _ColorMap.put ("dark_magenta", new Color(0.25f, 0.0f, 0.25f, 1.0f));
        _ColorMap.put ("dark_red", new Color (0.25f, 0.0f, 0.0f, 1.0f));
        _ColorMap.put ("dark_yellow", new Color(0.25f, 0.25f, 0.0f, 1.0f));
        _ColorMap.put ("gray", Color.GRAY);
        _ColorMap.put ("green", Color.GREEN);
        _ColorMap.put ("light_grey", Color.LIGHT_GRAY);
        _ColorMap.put ("magenta", Color.MAGENTA);
        _ColorMap.put ("orange", Color.ORANGE);
        _ColorMap.put ("pink", Color.PINK);
        _ColorMap.put ("red", Color.RED);
        _ColorMap.put ("white", Color.WHITE);
        _ColorMap.put ("yellow", Color.YELLOW);
    }
    
    /**
     * 
     */
    public GameColor ()
    {
        super ();
    }

    /**
     * @param color
     */
    public GameColor (Color color)
    {
        super (color);
    }

    /**
     * @param r
     * @param g
     * @param b
     * @param a
     */
    public GameColor (float r, float g, float b, float a)
    {
        super (r, g, b, a);
    }
    
    public GameColor (String name)
    {
        super (getColor (name));
    }

    public static Color getColor (String name)
    {
        if (_ColorMap.containsKey (name.toLowerCase ())) {
            return _ColorMap.get (name.toLowerCase ());
        }
        return Color.BLACK;
    }
}
