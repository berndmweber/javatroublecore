/**
 * @file: com.innovail.trouble.uicomponent - MenuEntryColorSelector.java
 * @date: Jun 20, 2013
 * @author: berndmicweber
 */
package com.innovail.trouble.uicomponent;

import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.math.Vector3;

import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.graphics.GameColor;
import com.innovail.trouble.graphics.GameFont;
import com.innovail.trouble.graphics.GameFont.FontType;
import com.innovail.trouble.graphics.GameMesh;
import com.innovail.trouble.utils.Parameters;

/**
 * 
 */
public class MenuEntryColorSelector extends MenuEntrySelector
{

    /**
     * @param name
     */
    public MenuEntryColorSelector (final String [] name)
    {
        super (name);
    }

    /**
     * @param name
     * @param params
     */
    public MenuEntryColorSelector (final String [] name, final Parameters params)
    {
        super (name, params);
    }

    @Override
    public void processParams (final Parameters params)
    {
        super.processParams (params);

        final Map <String, Color> colorMap = GameColor.getColorMap ();
        final Iterator <String> colorNames = colorMap.keySet ().iterator ();
        while (colorNames.hasNext ()) {
            final String currentColor = colorNames.next ();
            setSelection (currentColor);
            if (!_manipulators.containsKey (currentColor)) {
                _manipulators.put (currentColor, new GameMesh (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel ("#")));
            }
        }
    }

    @Override
    public void render (final GL11 gl, final Vector3 menuOffset,
                        final Color color)
    {
        render (gl, menuOffset, this, _entryPosition, color);
        if ((_manipulators != null) && !_manipulators.isEmpty ()) {
            if (_selections.size () > 1) {
                render (gl, menuOffset, _manipulators.get (LastKey), _manipPosition.get (LastKey), color);
            }
            render (gl, menuOffset, _manipulators.get (_selections.get (_currentSelection)), _manipPosition.get (_selections.get (_currentSelection)), GameColor.getColor (_selections.get (_currentSelection)));
            if (_selections.size () > 1) {
                render (gl, menuOffset, _manipulators.get (NextKey), _manipPosition.get (NextKey), color);
            }
        }
    }

    public void setCurrentSelection (final String color)
    {
        setCurrentSelection (_selections.indexOf (color));
    }

}
