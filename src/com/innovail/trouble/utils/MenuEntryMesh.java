/**
 * @file:   com.innovail.trouble.utils - MenuEntryMesh.java
 * @date:   May 8, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * 
 */
public class MenuEntryMesh extends MenuMesh {

    private final String _name;
    private Rectangle _touchArea;
    
    /**
     * @param path
     * @param color
     * @param isInternal
     */
    public MenuEntryMesh (String name, String path, Color color, boolean isInternal) {
        super (path, color, isInternal);
        _name = name;
    }

    /**
     * @param path
     * @param color
     */
    public MenuEntryMesh (String name, String path, Color color) {
        super (path, color);
        _name = name;
    }

    /**
     * @param path
     * @param isInternal
     */
    public MenuEntryMesh (String name, String path, boolean isInternal) {
        super (path, isInternal);
        _name = name;
    }

    /**
     * @param path
     */
    public MenuEntryMesh (String name, String path) {
        super (path);
        _name = name;
    }

    public String getName ()
    {
        return _name;
    }
    
    public void setTouchRectangle (float x, float y, float width, float height)
    {
        if (_touchArea == null) {
            _touchArea = new Rectangle (x, y, width, height);
        }
    }
    
    public Vector2 getPosition ()
    {
        Vector2 pos = new Vector2 ();
        pos.x = _touchArea.x;
        pos.y = _touchArea.y;
        return pos;
    }
    
    public Vector2 getOutline ()
    {
        Vector2 outline = new Vector2 ();
        outline.x = _touchArea.width;
        outline.y = _touchArea.height;
        return outline;
    }
    
    public Rectangle getTouchRectangle ()
    {
        return _touchArea;
    }
    
    public boolean isTouched (float x, float y)
    {
        boolean inside = false;
        if ((x > _touchArea.x) && (x < (_touchArea.width + _touchArea.x))) {
            inside = true;
        }
        if ((y > _touchArea.y) && (y < (_touchArea.height + _touchArea.y))) {
            if (inside) {
                return true;
            }
        }
        return false;
    }
}
