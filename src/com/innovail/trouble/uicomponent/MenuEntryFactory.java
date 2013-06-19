/**
 * @file: com.innovail.trouble.uicomponent - MenuEntryFactory.java
 * @date: Jun 19, 2013
 * @author: berndmicweber
 */
package com.innovail.trouble.uicomponent;

import com.innovail.trouble.utils.Parameters;

/**
 * 
 */
public class MenuEntryFactory
{
    private static MenuEntryFactory _instance;

    public static MenuEntry createMenuEntry (final String [] name,
                                             final String type,
                                             final Parameters params)
    {
        MenuEntry.TypeEnum tType = MenuEntry.TypeEnum.MESH;
        if (params != null) {
            try {
                tType = MenuEntry.TypeEnum.valueOf (type.toUpperCase ());
            } catch (final Exception e) {
                tType = MenuEntry.TypeEnum.MESH;
            }
        }
        switch (tType) {
        case COUNT:
            return new MenuEntryCount (name, params);
        case MESH:
        default:
            return new MenuEntryMesh (name);
        }
    }

    public static MenuEntryFactory getInstance ()
    {
        if (_instance == null) {
            _instance = new MenuEntryFactory ();
        }
        return _instance;
    }

    /**
     * 
     */
    private MenuEntryFactory ()
    {}
}
