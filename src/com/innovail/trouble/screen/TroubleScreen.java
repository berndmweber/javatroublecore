/**
 * @file: com.innovail.trouble.screen - TroubleScreen.java
 * @date: Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;

/**
 * 
 */
public abstract class TroubleScreen implements Screen
{
    private static final String TAG = "TroubleScreen";

    private boolean _showFrontAndBack = false;

    protected static final boolean _DEBUG = false;
    protected static final int _MIN = 0;
    protected static final int _MAX = 1;

    protected String _currentState;

    protected boolean _filling = true;

    protected Camera _camera;

    public TroubleScreen ()
    {
        init (true);
    }

    public abstract void createInputProcessor ();

    public boolean DebugEnabled ()
    {
        return _DEBUG;
    }

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.Screen#dispose()
     */
    @Override
    public void dispose ()
    {}

    public Camera getCamera ()
    {
        return _camera;
    }

    public String getState ()
    {
        return _currentState;
    }

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.Screen#hide()
     */
    @Override
    public void hide ()
    {}

    public void init ()
    {
        init (false);
    }

    protected void init (final boolean full)
    {
        if (!full) {
            setOwnState ();
        }
        createInputProcessor ();
    }

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.Screen#pause()
     */
    @Override
    public void pause ()
    {}

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.Screen#render(float)
     */
    @Override
    public void render (final float delta)
    {
        update (delta);

        final GL11 gl = Gdx.graphics.getGL11 ();
        final Color currentColor = Color.BLACK;

        gl.glClear (GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor (currentColor.r, currentColor.g, currentColor.b, currentColor.a);

        renderBackground (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());

        gl.glEnable (GL10.GL_DITHER);
        gl.glEnable (GL10.GL_DEPTH_TEST);
        if (!_showFrontAndBack) {
            gl.glEnable (GL10.GL_CULL_FACE);
            gl.glShadeModel (GL10.GL_SMOOTH);
        }
        gl.glPolygonMode (GL10.GL_FRONT, GL10.GL_FILL);

        setProjectionAndCamera (gl);
        setLighting (gl);

        gl.glMatrixMode (GL10.GL_MODELVIEW);

        render (gl, delta);
    }

    protected abstract void render (final GL11 gl, final float delta);

    protected abstract void renderBackground (final float width,
                                              final float height);

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
     */
    @Override
    public void resize (final int width, final int height)
    {
        Gdx.app.log (TAG, "resize");
    }

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.Screen#resume()
     */
    @Override
    public void resume ()
    {}

    protected void setLighting (final GL11 gl)
    {
        setLighting (gl, Color.WHITE);
    }

    protected void setLighting (final GL11 gl, final Color light)
    {
        final Color lightColor = light;
        final int frontAndOrBack = GL10.GL_FRONT_AND_BACK;

        final float [] specular0 = { lightColor.r, lightColor.g, lightColor.b,
                                    lightColor.a };
        final float [] position1 = { -2.0f, -2.0f, 1.0f, 0.0f };
        final float [] ambient1 = { 1.0f, 1.0f, 1.0f, 1.0f };
        final float [] diffuse1 = { 1.0f, 1.0f, 1.0f, 1.0f };
        final float [] specular1 = { 1.0f, 1.0f, 1.0f, 1.0f };

        final float [] matSpecular = { 1.0f, 1.0f, 1.0f, 1.0f };
        final float [] matShininess = { 7.0f, 0.0f, 0.0f, 0.0f };

        gl.glLightfv (GL10.GL_LIGHT0, GL10.GL_SPECULAR, specular0, 0);

        gl.glLightfv (GL10.GL_LIGHT1, GL10.GL_AMBIENT, ambient1, 0);
        gl.glLightfv (GL10.GL_LIGHT1, GL10.GL_DIFFUSE, diffuse1, 0);
        gl.glLightfv (GL10.GL_LIGHT1, GL10.GL_SPECULAR, specular1, 0);
        gl.glLightfv (GL10.GL_LIGHT1, GL10.GL_POSITION, position1, 0);

        gl.glMaterialfv (frontAndOrBack, GL10.GL_SPECULAR, matSpecular, 0);
        gl.glMaterialfv (frontAndOrBack, GL10.GL_SHININESS, matShininess, 0);

        gl.glEnable (GL10.GL_LIGHT0);
        gl.glEnable (GL10.GL_LIGHT1);
        gl.glEnable (GL10.GL_LIGHTING);
        gl.glEnable (GL10.GL_COLOR_MATERIAL);
        gl.glEnable (GL10.GL_BLEND);
    }

    public abstract void setOwnState ();

    protected void setProjectionAndCamera (final GL11 gl)
    {
        _camera.update ();
        _camera.apply (gl);
    }

    /*
     * (non-Javadoc)
     * @see com.badlogic.gdx.Screen#show()
     */
    @Override
    public void show ()
    {}

    protected void showFrontAndBack ()
    {
        _showFrontAndBack = true;
    }

    protected abstract void update (final float delta);
}
