/**
 * @file: com.innovail.trouble.utils - GameInputAdapter.java
 * @date: May 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * 
 */
public class GameInputAdapter implements InputProcessor
{
    protected static final int MIN_NUMBER_OF_DRAGS = 3;

    protected boolean _isDragged = false;
    protected int _dragEvents = 0;
    protected Vector2 _lastPosition = new Vector2 ();

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#keyDown(int)
     */
    @Override
    public boolean keyDown (final int keycode)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#keyTyped(char)
     */
    @Override
    public boolean keyTyped (final char character)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#keyUp(int)
     */
    @Override
    public boolean keyUp (final int keycode)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#mouseMoved(int, int)
     */
    @Override
    public boolean mouseMoved (final int screenX, final int screenY)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#scrolled(int)
     */
    @Override
    public boolean scrolled (final int amount)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#touchDown(int, int, int, int)
     */
    @Override
    public boolean touchDown (final int x, final int y, final int pointer,
                              final int button)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#touchDragged(int, int, int)
     */
    @Override
    public boolean touchDragged (final int x, final int y, final int pointer)
    {
        _dragEvents++;
        _isDragged = true;

        if (_dragEvents >= MIN_NUMBER_OF_DRAGS) {
            _lastPosition.set (x, y);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
     */
    @Override
    public boolean touchUp (final int x, final int y, final int pointer,
                            final int button)
    {
        _dragEvents = 0;
        _isDragged = false;
        return false;
    }

}
