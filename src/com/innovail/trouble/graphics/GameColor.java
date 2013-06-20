/**
 * @file: com.innovail.trouble.graphics - GameColor.java
 * @date: Jun 20, 2013
 * @author: berndmicweber
 */
package com.innovail.trouble.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Color;

/**
 * 
 */
public class GameColor extends Color
{
    private static final Map <String, Color> _ColorMap;
    static {
        _ColorMap = new HashMap <String, Color> ();
        _ColorMap.put ("black", Color.BLACK);
        _ColorMap.put ("blue", Color.BLUE);
        _ColorMap.put ("clear", Color.CLEAR);
        _ColorMap.put ("cyan", Color.CYAN);
        _ColorMap.put ("dark_blue", new Color (0.0f, 0.0f, 0.25f, 1.0f));
        _ColorMap.put ("dark_clear_blue", new Color (0.0f, 0.0f, 0.25f, 0.5f));
        _ColorMap.put ("dark_cyan", new Color (0.0f, 0.25f, 0.25f, 1.0f));
        _ColorMap.put ("dark_green", new Color (0.0f, 0.25f, 0.0f, 1.0f));
        _ColorMap.put ("dark_grey", Color.DARK_GRAY);
        _ColorMap.put ("dark_magenta", new Color (0.25f, 0.0f, 0.25f, 1.0f));
        _ColorMap.put ("dark_red", new Color (0.25f, 0.0f, 0.0f, 1.0f));
        _ColorMap.put ("dark_yellow", new Color (0.25f, 0.25f, 0.0f, 1.0f));
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

    public static Color getColor (final String name)
    {
        if (_ColorMap.containsKey (name.toLowerCase ())) {
            return _ColorMap.get (name.toLowerCase ());
        }
        return Color.BLACK;
    }

    public static Map <String, Color> getColorMap ()
    {
        return _ColorMap;
    }

    public static String getColorName (final Color color)
    {
        for (final Entry <String, Color> entry : _ColorMap.entrySet ()) {
            final Color currentColor = entry.getValue ();
            if (color.equals (currentColor)) {
                return entry.getKey ();
            }
        }
        return null;
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
    public GameColor (final Color color)
    {
        super (color);
    }

    /**
     * @param r
     * @param g
     * @param b
     * @param a
     */
    public GameColor (final float r, final float g, final float b, final float a)
    {
        super (r, g, b, a);
    }

    public GameColor (final String name)
    {
        super (getColor (name));
    }

    @Override
    public boolean equals (final Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || (getClass () != o.getClass () && Color.class != o.getClass ())) {
            return false;
        }
        final Color color = (Color) o;
        return toIntBits () == color.toIntBits ();
    }
}
