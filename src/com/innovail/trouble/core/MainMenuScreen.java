/**
 * @file:   com.innovail.trouble.core - MenuScreen.java
 * @date:   Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.io.InputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.innovail.trouble.utils.BackgroundImage;

/**
 * 
 */
public class MainMenuScreen extends TroubleScreen {
    private static final String TAG = "MainMenuScreen";
    private static final String _AppPartName = "mainMenu";
    
    private final BitmapFont _menuFont;
    private final SpriteBatch _spriteBatch;
    private final BackgroundImage _backgroundImage;
    
    private Matrix4 _viewMatrix;
    private Matrix4 _transformMatrix;
    private Mesh _logo;
    private Camera _camera;
    
    private FileHandle _objFile;
    
    public MainMenuScreen ()
    {
        Gdx.app.log (TAG, "MainMenuScreen()");
        _spriteBatch = new SpriteBatch ();
        _menuFont = ApplicationSettings.getInstance ().getGameFont (_AppPartName).createBitmapFont ();
        _backgroundImage = ApplicationSettings.getInstance ().getBackgroundImage (_AppPartName);
        _backgroundImage.createTexture ().setFilter (TextureFilter.Linear, TextureFilter.Linear);
        
        _viewMatrix = new Matrix4 ();
        _transformMatrix = new Matrix4 ();
        
        float aspectRatio = (float) Gdx.graphics.getWidth () / (float) Gdx.graphics.getHeight ();
        _camera = new PerspectiveCamera(100, 2f * aspectRatio, 2f);
        
        try {
            _objFile = Gdx.files.internal("trouble_logo_3d.obj");
            InputStream in = _objFile.read();
            _logo = ObjLoader.loadObj(in, false);
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        Gdx.app.log (TAG, "resize");
    }

    @Override
    public void render (float delta)
    {
        GL10 gl = Gdx.graphics.getGL10();

        gl.glClear (GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor (Color.RED.r, Color.RED.g, Color.RED.b, Color.RED.a);
        gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        renderBackground (Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        gl.glDisable(GL10.GL_DITHER);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_CULL_FACE);

        setProjectionAndCamera(gl);
        //setLighting(gl);

        gl.glEnable(GL10.GL_TEXTURE_2D);

        renderLogo(gl);

        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glDisable(GL10.GL_DEPTH_TEST);

        _spriteBatch.setProjectionMatrix(_viewMatrix);
        _spriteBatch.setTransformMatrix(_transformMatrix);

    }

    private void renderBackground (float width, float height)
    {
        _viewMatrix.setToOrtho2D(0.0f, 0.0f, width, height);
        _spriteBatch.setProjectionMatrix(_viewMatrix);
        _spriteBatch.setTransformMatrix(_transformMatrix);
        _spriteBatch.begin();
        _spriteBatch.disableBlending();
        _spriteBatch.setColor(Color.WHITE);
        _spriteBatch.draw((Texture)_backgroundImage.getImageObject (),
                          0, 0, width, height,
                          0, 0,
                          _backgroundImage.getWidth (),
                          _backgroundImage.getHeight (),
                          false, false);
        _spriteBatch.enableBlending();
        _spriteBatch.setBlendFunction(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        String text = "~`1!2@3#4$5%6^7&8*9(0)-_=+qQwWeErRtTyYuUiIoOpP[{]}\\|aAsSdDfFgGhHjJkKlL;:'\"zZxXcCvVbBnNmM,<.>/?";
        float textWidth = _menuFont.getBounds(text).width;
        _menuFont.draw (_spriteBatch, text,
                        Gdx.graphics.getWidth () / 2 - textWidth / 2,
                        Gdx.graphics.getHeight () / 2);
        _spriteBatch.end();
    }
    
    private void setProjectionAndCamera (GL10 gl) {
        _camera.position.set(0, 0, 2);
        _camera.direction.set(0, 0, -4).sub(_camera.position).nor();
        _camera.update();
        _camera.apply(gl);
    }
    
    private void setLighting (GL10 gl) {
        float[] direction = {1, 0.5f, 0, 0};
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, direction, 0);
        gl.glEnable(GL10.GL_COLOR_MATERIAL);
    }

    private void renderLogo (GL10 gl)
    {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -0.9f);
        gl.glRotatef (90.0f, 1.0f, 0.0f, 0.0f);
        _logo.render (GL10.GL_TRIANGLES);
        gl.glPopMatrix();
     }

}
