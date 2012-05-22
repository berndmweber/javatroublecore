/**
 * @file:   com.innovail.trouble.core - JavaTroubleApplication.java
 * @date:   Feb 26, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import com.innovail.trouble.screen.GameScreen;
import com.innovail.trouble.screen.MainMenuScreen;
import com.innovail.trouble.screen.NewGameScreen;
import com.innovail.trouble.screen.TroubleScreen;
import com.innovail.trouble.utils.SettingLoader;

/**
 * 
 */
public class JavaTroubleApplication extends Game {
    private static final String TAG = "JavaTroubleApplication";

    public String currentState = TroubleApplicationState.MAIN_MENU;
    
    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#create()
     */
    @Override
    public void create () {
        Gdx.app.log (TAG, "Creating JavaTroubleApplication.");
        SettingLoader.loadSettings ();
        setScreen (new MainMenuScreen ());
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#render()
     */
    @Override
    public void render () {
        final TroubleScreen currentScreen = getScreen ();

        if (currentState != currentScreen.getState ()) {
            currentState = currentScreen.getState ();
            switch (TroubleApplicationState.getState (currentState)) {
            case E_NEW_GAME:
                setScreen (new NewGameScreen ());
                break;
            case E_GAME:
                setScreen (new GameScreen ());
                break;
            case E_SETTINGS:
            case E_MAIN_MENU:
            default:
                setScreen (new MainMenuScreen ());
                break;
            }
        }

        // update the screen
        currentScreen.render (Gdx.graphics.getDeltaTime ());
    }

    /**
    */
    public TroubleScreen getScreen () {
        return (TroubleScreen)super.getScreen ();
    }
}
