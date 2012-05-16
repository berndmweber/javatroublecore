/**
 * @file:   com.innovail.trouble.core - TroubleApplicationState.java
 * @date:   May 9, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import com.badlogic.gdx.utils.Array;

/**
 * 
 */
public class TroubleApplicationState {

    public static final String MAIN_MENU = "mainMenu";
    public static final String NEW_GAME  = "newGame";
    public static final String SETTINGS  = "settings";
    public static final String GAME      = "game";
    
    public static enum StateEnum {
        E_MAIN_MENU,
        E_NEW_GAME,
        E_SETTINGS,
        E_GAME
    }
    
    private static final String[] _all = {MAIN_MENU, NEW_GAME, SETTINGS, GAME};

    public static final Array <String> ALL = new Array <String> (_all);
    
    /*private String _currentState;
    private StateEnum _currentEnumState;
    
    public TroubleApplicationState ()
    {
        _currentState = MAIN_MENU;
        _currentEnumState = StateEnum.E_MAIN_MENU;
    }
    
    public String getCurrentState ()
    {
        return _currentState;
    }*/
    
    public static StateEnum getState (final String state)
    {
        if (state.equals (NEW_GAME)) return StateEnum.E_NEW_GAME;
        if (state.equals (SETTINGS)) return StateEnum.E_SETTINGS;
        if (state.equals (GAME))     return StateEnum.E_GAME;

        return StateEnum.E_MAIN_MENU;
    }
}
