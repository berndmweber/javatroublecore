/**
 * @file:   com.innovail.trouble.core - TroubleApplicationState.java
 * @date:   May 9, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import com.badlogic.gdx.utils.Array;
import com.innovail.trouble.screen.GameScreen;
import com.innovail.trouble.screen.LoadingScreen;
import com.innovail.trouble.screen.MainMenuScreen;
import com.innovail.trouble.screen.NewGameScreen;

/**
 * 
 */
public class TroubleApplicationState {

    public static final String GAME      = "game";
    public static final String LOADING   = "loading";
    public static final String MAIN_MENU = "mainMenu";
    public static final String NEW_GAME  = "newGameMenu";
    public static final String SETTINGS  = "settings";

    public static enum StateEnum {
        E_GAME,
        E_LOADING,
        E_MAIN_MENU,
        E_NEW_GAME,
        E_SETTINGS,
    }

    private static final String[] _all = {GAME, LOADING, MAIN_MENU, NEW_GAME, SETTINGS};

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
        if (state.equals (GAME))     return StateEnum.E_GAME;
        if (state.equals (LOADING))  return StateEnum.E_LOADING;
        if (state.equals (NEW_GAME)) return StateEnum.E_NEW_GAME;
        if (state.equals (SETTINGS)) return StateEnum.E_SETTINGS;

        return StateEnum.E_MAIN_MENU;
    }

    @SuppressWarnings ("rawtypes")
    public static Class getScreenClass (final StateEnum state)
    {
        switch (state) {
        case E_GAME:
            return GameScreen.class;
        case E_LOADING:
            return LoadingScreen.class;
        case E_NEW_GAME:
            return NewGameScreen.class;
        case E_SETTINGS:
        case E_MAIN_MENU:
        default:
            return MainMenuScreen.class;
        }
    }
}
