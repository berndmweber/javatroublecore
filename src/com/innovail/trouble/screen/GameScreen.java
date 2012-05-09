/**
 * @file:   com.innovail.trouble.screen - GameScreen.java
 * @date:   May 9, 2012
 * @author: bweber
 */
package com.innovail.trouble.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.innovail.trouble.core.TroubleApplicationState;

/**
 * 
 */
public class GameScreen implements TroubleScreen {
    private static final String TAG = "GameScreen";
    private static final String _AppPartName = TroubleApplicationState.GAME;
    
    private String _currentState = TroubleApplicationState.GAME;
    
    private boolean _filling = true;

    public GameScreen ()
    {
        Gdx.app.log (TAG, "GameScreen()");
        
        createInputProcessor ();
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#dispose()
     */
    @Override
    public void dispose () {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#hide()
     */
    @Override
    public void hide () {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#pause()
     */
    @Override
    public void pause () {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#render(float)
     */
    @Override
    public void render (float delta) {
        GL10 gl = Gdx.graphics.getGL10();
        Color currentColor = Color.RED;

        gl.glClear (GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
        gl.glViewport (0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#resize(int, int)
     */
    @Override
    public void resize (int width, int height) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#resume()
     */
    @Override
    public void resume () {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#show()
     */
    @Override
    public void show () {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.innovail.trouble.screen.TroubleScreen#createInputProcessor()
     */
    @Override
    public void createInputProcessor () {
        Gdx.input.setInputProcessor (new InputProcessor() {
            /* (non-Javadoc)
             * @see com.badlogic.gdx.InputProcessor#keyDown(int)
             */
            @Override
            public boolean keyDown (int keycode) {
                boolean rv = true;
                
                switch (keycode) {
                case Input.Keys.SPACE:
                    Gdx.app.log (TAG, "keyDown() - SPACE");
                    break;
                case Input.Keys.R:
                    if (!_filling) {
                        Gdx.app.log (TAG, "keyDown() - Filling");
                        Gdx.gl10.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_FILL);
                        _filling = true;
                    } else {
                        Gdx.app.log (TAG, "keyDown() - wireframing");
                        Gdx.gl10.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_LINE);
                        _filling = false;
                    }
                    break;
                case Input.Keys.UP:
                    Gdx.app.log (TAG, "keyDown() - UP");
                    break;
                case Input.Keys.DOWN:
                    Gdx.app.log (TAG, "keyDown() - DOWN");
                    _currentState = TroubleApplicationState.MAIN_MENU;
                    break;
                case Input.Keys.LEFT:
                    Gdx.app.log (TAG, "keyDown() - LEFT");
                    break;
                case Input.Keys.RIGHT:
                    Gdx.app.log (TAG, "keyDown() - RIGHT");
                    break;
                default:
                    rv = false;
                }
                return rv;
            }

            /* (non-Javadoc)
             * @see com.badlogic.gdx.InputProcessor#keyUp(int)
             */
            @Override
            public boolean keyUp (int keycode) {
                // TODO Auto-generated method stub
                return false;
            }

            /* (non-Javadoc)
             * @see com.badlogic.gdx.InputProcessor#keyTyped(char)
             */
            @Override
            public boolean keyTyped (char character) {
                // TODO Auto-generated method stub
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
             * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
             */
            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {
                // TODO Auto-generated method stub
                return false;
            }

            /* (non-Javadoc)
             * @see com.badlogic.gdx.InputProcessor#touchDragged(int, int, int)
             */
            @Override
            public boolean touchDragged (int x, int y, int pointer) {
                // TODO Auto-generated method stub
                return false;
            }

            /* (non-Javadoc)
             * @see com.badlogic.gdx.InputProcessor#touchMoved(int, int)
             */
            @Override
            public boolean touchMoved (int x, int y) {
                // TODO Auto-generated method stub
                return false;
            }

            /* (non-Javadoc)
             * @see com.badlogic.gdx.InputProcessor#scrolled(int)
             */
            @Override
            public boolean scrolled (int amount) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }

    /* (non-Javadoc)
     * @see com.innovail.trouble.screen.TroubleScreen#getState()
     */
    @Override
    public String getState () {
        return _currentState;
    }

}
