/**
 * @file:   com.innovail.trouble.utils - GameInputAdapter.java
 * @date:   May 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * 
 */
public class GameInputAdapter implements InputProcessor {
    protected boolean _isDragged = false;
    
    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#keyDown(int)
     */
    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#keyTyped(char)
     */
    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#keyUp(int)
     */
    @Override
    public boolean keyUp (int keycode) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#scrolled(int)
     */
    @Override
    public boolean scrolled (int amount) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
     */
    @Override
    public boolean touchUp (int x, int y, int pointer, int button)
    {
        _isDragged = false;
        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#touchDown(int, int, int, int)
     */
    @Override
    public boolean touchDown (int x, int y, int pointer, int button) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#touchDragged(int, int, int)
     */
    @Override
    public boolean touchDragged (int x, int y, int pointer) {
        _isDragged = true;
        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#touchMoved(int, int)
     */
    @Override
    public boolean touchMoved (int x, int y) {
        return false;
    }

}
