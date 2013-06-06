/**
 * @file:   com.innovail.trouble.uicomponent - MenuEntryMesh.java
 * @date:   May 8, 2012
 * @author: bweber
 */
package com.innovail.trouble.uicomponent;

import com.badlogic.gdx.graphics.Color;
import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.graphics.GameFont;
import com.innovail.trouble.graphics.GameMesh;
import com.innovail.trouble.graphics.GameFont.FontType;

/**
 * 
 */
public class MenuEntryMesh extends GameMesh {

    private final String _name;
    
    /**
     * @param path
     * @param color
     * @param isInternal
     */
    public MenuEntryMesh (final String name, final String path,
                           final Color color, final boolean isInternal)
    {
        super (path, color, isInternal);
        _name = name;
    }

    /**
     * @param path
     * @param color
     */
    public MenuEntryMesh (final String name, final String path, final Color color)
    {
        super (path, color);
        _name = name;
    }

    /**
     * @param path
     * @param isInternal
     */
    public MenuEntryMesh (final String name, final String path, final boolean isInternal)
    {
        super (path, isInternal);
        _name = name;
    }

    /**
     * @param path
     */
    public MenuEntryMesh (final String name, final String path) {
        super (path);
        _name = name;
    }
    
    public MenuEntryMesh (final String [] name)
    {
        super (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (name[1]));
        _name = name[0];
    }

    public String getName ()
    {
        return _name;
    }
}
