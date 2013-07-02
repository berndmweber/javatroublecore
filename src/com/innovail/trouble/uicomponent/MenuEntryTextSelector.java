/**
 * @file: com.innovail.trouble.uicomponent - MenuEntryTextSelector.java
 * @date: Jul 2, 2013
 * @author: bweber
 */
package com.innovail.trouble.uicomponent;

import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.graphics.GameFont;
import com.innovail.trouble.graphics.GameFont.FontType;
import com.innovail.trouble.graphics.GameMesh;
import com.innovail.trouble.utils.Parameters;

/**
 * 
 */
public class MenuEntryTextSelector extends MenuEntrySelector
{

    /**
     * @param name
     */
    public MenuEntryTextSelector (final String [] name)
    {
        super (name);
    }

    /**
     * @param name
     * @param params
     */
    public MenuEntryTextSelector (final String [] name, final Parameters params)
    {
        super (name, params);
    }

    public void addEntry (final String entry)
    {
        setSelection (entry);
    }

    @Override
    protected void setSelection (final String entry)
    {
        super.setSelection (entry);
        if (!_manipulators.containsKey (entry)) {
            _manipulators.put (entry,
                               new GameMesh (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (entry)));
        }
    }
}
