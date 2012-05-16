/**
 * @file:   com.innovail.trouble.core - TroubleScreen.java
 * @date:   Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;

/**
 * 
 */
public abstract class TroubleScreen implements Screen {
    private static final String TAG = "TroubleScreen";

    protected static final boolean _DEBUG = false;

    protected String _currentState; 

    protected boolean _filling = true;

    protected Camera _camera;


    public TroubleScreen ()
    {
        createInputProcessor ();
    }
    
    public abstract void createInputProcessor ();
    
    public String getState ()
    {
        return _currentState;
    }
    
    public boolean DebugEnabled ()
    {
        return _DEBUG;
    }
    
    public Camera getCamera ()
    {
        return _camera;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#dispose()
     */
    @Override
    public void dispose ()
    {}

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#hide()
     */
    @Override
    public void hide ()
    {}

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#pause()
     */
    @Override
    public void pause ()
    {}

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#render(float)
     */
    @Override
    public void render (final float delta)
    {
        update (delta);
        
        final GL10 gl = Gdx.graphics.getGL10();
        final Color currentColor = Color.BLACK;

        gl.glClear (GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
        gl.glViewport (0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        renderBackground ((float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
        
        gl.glDisable (GL10.GL_DITHER);
        gl.glEnable (GL10.GL_DEPTH_TEST);
        gl.glEnable (GL10.GL_CULL_FACE);

        setProjectionAndCamera (gl);
        setLighting (gl);

        gl.glMatrixMode (GL10.GL_MODELVIEW);
        gl.glShadeModel (GL10.GL_SMOOTH);
        
        render (gl, delta);
    }
    
    protected abstract void update (final float delta);
    
    protected abstract void render (final GL10 gl, final float delta);
    
    protected abstract void renderBackground (final float width, final float height);
    
    protected void setProjectionAndCamera (final GL10 gl)
    {
        _camera.update ();
        _camera.apply (gl);
    }
    
    protected abstract void setLighting (final GL10 gl);

    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
     */
    @Override
    public void resize (final int width, final int height) {
        Gdx.app.log (TAG, "resize");
    }


    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#resume()
     */
    @Override
    public void resume ()
    {}

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#show()
     */
    @Override
    public void show ()
    {}
}
