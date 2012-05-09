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
import com.innovail.trouble.screen.TroubleScreen;
import com.innovail.trouble.utils.SettingLoader;

/**
 * 
 */
public class JavaTroubleApplication extends Game {
    private static String TAG = "JavaTroubleApplication";
    //private TroubleGame myGame;
    public String currentState = TroubleApplicationState.MAIN_MENU;
    
    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#create()
     */
    @Override
    public void create () {
        Gdx.app.log (TAG, "Creating JavaTroubleApplication.");
        SettingLoader.loadSettings ();
        /*myGame = new TroubleGame ();
        myGame.createGame ();*/
        setScreen (new MainMenuScreen ());
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#render()
     */
    @Override
    public void render () {
        TroubleScreen currentScreen = getScreen ();

        if (currentState != currentScreen.getState ()) {
            currentState = currentScreen.getState ();
            switch (TroubleApplicationState.getState (currentState)) {
            case E_MAIN_MENU:
                setScreen (new MainMenuScreen ());
                break;
            case E_NEW_GAME:
                currentState = TroubleApplicationState.GAME;
            case E_GAME:
                setScreen (new GameScreen ());
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
