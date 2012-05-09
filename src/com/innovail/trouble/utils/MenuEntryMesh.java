/**
 * @file:   com.innovail.trouble.utils - MenuEntryMesh.java
 * @date:   May 8, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

import com.badlogic.gdx.graphics.Color;

/**
 * 
 */
public class MenuEntryMesh extends MenuMesh {

    private final String _name;
    
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
}
