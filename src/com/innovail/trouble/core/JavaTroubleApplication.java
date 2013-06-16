/**
 * @file:   com.innovail.trouble.core - JavaTroubleApplication.java
 * @date:   Feb 26, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import com.innovail.trouble.core.TroubleApplicationState.StateEnum;
import com.innovail.trouble.screen.TroubleScreen;
import com.innovail.trouble.utils.SettingLoader;

/**
 * 
 */
public class JavaTroubleApplication extends Game {
    private static final String TAG = "JavaTroubleApplication";

    public String currentState = TroubleApplicationState.LOADING;

    private static final float _DotMaxDelta = 0.4f;
    private static float _displayDelta = 0.0f;

    private final TroubleScreen [] _screenList = new TroubleScreen [StateEnum.values ().length];

    private boolean _screensLoaded = false;
    private int _screenIterator = 0;

    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#create()
     */
    @Override
    public void create () {
        Gdx.app.log (TAG, "Creating JavaTroubleApplication.");
        SettingLoader.loadSettings ();
        setScreen (getScreen (TroubleApplicationState.getState (currentState)));
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#render()
     */
    @Override
    public void render () {
        final float currentDelta = Gdx.graphics.getDeltaTime ();
        final TroubleScreen currentScreen = getScreen ();

        _displayDelta += currentDelta;

        if (!currentState.equals (currentScreen.getState ())) {
            currentState = currentScreen.getState ();
            switch (TroubleApplicationState.getState (currentState)) {
            case E_GAME:
                setScreen (getScreen (StateEnum.E_GAME));
                break;
            case E_LOADING:
                setScreen (getScreen (StateEnum.E_LOADING));
                break;
            case E_NEW_GAME:
                setScreen (getScreen (StateEnum.E_NEW_GAME));
                break;
            case E_SETTINGS:
            case E_MAIN_MENU:
            default:
                setScreen (getScreen (StateEnum.E_MAIN_MENU));
                break;
            }
        }

        /* update the screen */
        currentScreen.render (currentDelta);

        if (!_screensLoaded && (_displayDelta > _DotMaxDelta)) {
            getScreen (TroubleApplicationState.getState (TroubleApplicationState.ALL.get (_screenIterator++)), false);
            if (_screenIterator == StateEnum.values ().length) {
                _screensLoaded = true;
                setScreen (getScreen (StateEnum.E_MAIN_MENU));
            }
            _displayDelta = 0;
        }
    }

    /**
    */
    public TroubleScreen getScreen () {
        return (TroubleScreen) super.getScreen ();
    }

    private TroubleScreen getScreen (StateEnum gameState) {
        return getScreen (gameState, true);
    }

    private TroubleScreen getScreen (StateEnum gameState, boolean init) {
        TroubleScreen currentScreen = _screenList [gameState.ordinal ()];
        if (currentScreen == null) {
            try {
                _screenList [gameState.ordinal ()] = currentScreen = (TroubleScreen) TroubleApplicationState.getScreenClass (gameState).newInstance ();
            } catch (Exception e) {
                Gdx.app.log (TAG, e.getMessage ());
            }
        }
        if (init) {
            currentScreen.init ();
        }
        return currentScreen;
    }
}
