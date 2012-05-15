/**
 * @file:   com.innovail.trouble.utils - GameInputAdapter.java
 * @date:   May 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * 
 */
public class GameInputAdapter implements InputProcessor {
    protected final int MIN_NUMBER_OF_DRAGS = 3;
    
    protected boolean _isDragged = false;
    protected int _dragEvents = 0;
    protected Vector2 _lastPosition = new Vector2 ();
    
    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#keyDown(int)
     */
    @Override
    public boolean keyDown (int keycode)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#keyTyped(char)
     */
    @Override
    public boolean keyTyped (char character)
    {
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
    public boolean scrolled (int amount)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
     */
    @Override
    public boolean touchUp (int x, int y, int pointer, int button)
    {
        _dragEvents = 0;
        _isDragged = false;
        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#touchDown(int, int, int, int)
     */
    @Override
    public boolean touchDown (int x, int y, int pointer, int button)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#touchDragged(int, int, int)
     */
    @Override
    public boolean touchDragged (int x, int y, int pointer)
    {
        _dragEvents++;
        _isDragged = true;
        
        if (_dragEvents >= MIN_NUMBER_OF_DRAGS) {
            _lastPosition.set (x, y);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#touchMoved(int, int)
     */
    @Override
    public boolean touchMoved (int x, int y)
    {
        return false;
    }

}
