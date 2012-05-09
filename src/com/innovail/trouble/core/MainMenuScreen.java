/**
 * @file:   com.innovail.trouble.core - MenuScreen.java
 * @date:   Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.innovail.trouble.utils.BackgroundImage;
import com.innovail.trouble.utils.MenuMesh;

/**
 * 
 */
public class MainMenuScreen extends TroubleScreen {
    private static final String TAG = "MainMenuScreen";
    private static final String _AppPartName = "mainMenu";
    //private Random rand;
    
    private final BitmapFont _menuFont;
    private final SpriteBatch _spriteBatch;
    private final BackgroundImage _backgroundImage;

    private MenuMesh _logo;
    private HashMap <String, MenuMesh> _menuEntriesMap;
    private Collection <MenuMesh> _menuEntries;
    private float[] _yRotationAngle;
    private int[] _yRotationDirection;
    private final float _RotationMaxAngle = 2.0f;
    private final float _RotationAngleIncrease = 0.2f;
    private float _rotationDelta = 0.0f;
    private final float _RotationMaxDelta = 0.05f;

    private Matrix4 _viewMatrix;
    private Matrix4 _transformMatrix;
    private Camera _camera;
    
    public MainMenuScreen ()
    {
        Gdx.app.log (TAG, "MainMenuScreen()");
        _spriteBatch = new SpriteBatch ();
        _menuFont = ApplicationSettings.getInstance ().getGameFont (_AppPartName).createBitmapFont ();
        _backgroundImage = ApplicationSettings.getInstance ().getBackgroundImage (_AppPartName);
        _backgroundImage.createTexture ().setFilter (TextureFilter.Linear, TextureFilter.Linear);
        
        _logo = ApplicationSettings.getInstance ().getLogo ();
        _menuEntriesMap = ApplicationSettings.getInstance ().getMenuEntries (_AppPartName);
        _menuEntries = _menuEntriesMap.values ();
        _yRotationAngle = new float[_menuEntriesMap.size ()];
        _yRotationDirection = new int[_menuEntriesMap.size ()];
        Random rand = new Random ();
        for (int i = 0; i < _menuEntriesMap.size (); i++) {
            _yRotationAngle[i] = rand.nextFloat ();
            _yRotationAngle[i] *= rand.nextBoolean () ? 1.0f : -1.0f;
            _yRotationDirection[i] = rand.nextBoolean () ? 1 : -1;
        }

        _viewMatrix = new Matrix4 ();
        _transformMatrix = new Matrix4 ();
        
        float aspectRatio = (float) Gdx.graphics.getWidth () / (float) Gdx.graphics.getHeight ();
        _camera = new PerspectiveCamera (100, 2f * aspectRatio, 2f);
    }
    
    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
     */
    @Override
    public void resize (int width, int height) {
        Gdx.app.log (TAG, "resize");
    }

    @Override
    public void render (float delta)
    {
        _rotationDelta += delta;
        GL10 gl = Gdx.graphics.getGL10();
        Color currentColor = Color.BLACK;

        gl.glClear (GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
        gl.glViewport (0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        renderBackground (Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        gl.glDisable (GL10.GL_DITHER);
        gl.glEnable (GL10.GL_DEPTH_TEST);
        gl.glEnable (GL10.GL_CULL_FACE);

        setProjectionAndCamera (gl);
        setLighting (gl);

        //gl.glEnable (GL10.GL_TEXTURE_2D);
        //gl.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_LINE);
        gl.glMatrixMode (GL10.GL_MODELVIEW);
        gl.glShadeModel (GL10.GL_SMOOTH);

        renderLogo (gl);
        renderMenu (gl);

        //gl.glDisable (GL10.GL_TEXTURE_2D);
        gl.glDisable (GL10.GL_CULL_FACE);
        gl.glDisable (GL10.GL_DEPTH_TEST);
    }

    private void renderBackground (float width, float height)
    {
        _viewMatrix.setToOrtho2D (0.0f, 0.0f, width, height);
        _spriteBatch.setProjectionMatrix (_viewMatrix);
        _spriteBatch.setTransformMatrix (_transformMatrix);
        _spriteBatch.begin ();
        _spriteBatch.disableBlending ();
        _spriteBatch.setColor (Color.WHITE);
        _spriteBatch.draw ((Texture)_backgroundImage.getImageObject (),
                          0, 0, width, height,
                          0, 0,
                          _backgroundImage.getWidth (),
                          _backgroundImage.getHeight (),
                          false, false);
        _spriteBatch.enableBlending ();
        _spriteBatch.setBlendFunction (GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        String text = ApplicationSettings.getInstance ().getCopyRightNotice ();
        float textWidth = _menuFont.getBounds (text).width;
        float textHeight = _menuFont.getBounds (text).height;
        _menuFont.draw (_spriteBatch, text,
                        Gdx.graphics.getWidth () / 2 - textWidth / 2,
                        textHeight + 5);
        _spriteBatch.end ();
    }
    
    private void setProjectionAndCamera (GL10 gl) {
        _camera.position.set (0, 0, 2);
        _camera.direction.set (0, 0, -4).sub (_camera.position).nor ();
        _camera.update ();
        _camera.apply (gl);
    }
    
    private void setLighting (GL10 gl) {
        Color lightColor = _logo.getColor ();
        float[] specular0 = {lightColor.r, lightColor.g, lightColor.b, lightColor.a};
        float[] position1 = {-2.0f, -2.0f, 1.0f, 0.0f};
        float[] ambient1 = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] diffuse1 = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] specular1 = {1.0f, 1.0f, 1.0f, 1.0f};
        
        gl.glEnable (GL10.GL_LIGHTING);
        gl.glLightModelf (GL10.GL_LIGHT_MODEL_TWO_SIDE, 0.0f);
        
        gl.glEnable (GL10.GL_LIGHT0);
        gl.glLightfv (GL10.GL_LIGHT0, GL10.GL_SPECULAR, specular0, 0);
        
        gl.glEnable (GL10.GL_LIGHT1);
        gl.glLightfv (GL10.GL_LIGHT1, GL10.GL_AMBIENT, ambient1, 0);
        gl.glLightfv (GL10.GL_LIGHT1, GL10.GL_DIFFUSE, diffuse1, 0);
        gl.glLightfv (GL10.GL_LIGHT1, GL10.GL_SPECULAR, specular1, 0);
        gl.glLightfv (GL10.GL_LIGHT1, GL10.GL_POSITION, position1, 0);
        
        gl.glEnable (GL10.GL_COLOR_MATERIAL);
        gl.glEnable (GL10.GL_BLEND);
    }

    private void renderLogo (GL10 gl)
    {
        Color currentColor = _logo.getColor ();
        int frontAndOrBack = GL10.GL_FRONT;
        float[] matSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] matShininess = {7.0f};
        
        gl.glPushMatrix ();
        gl.glTranslatef (0.0f, 1.0f, 0.3f);
        gl.glRotatef (90.0f, 1.0f, 0.0f, 0.0f);
        gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
        gl.glMaterialfv (frontAndOrBack, GL10.GL_SPECULAR, matSpecular, 0);
        gl.glMaterialfv (frontAndOrBack, GL10.GL_SHININESS, matShininess, 0);
        _logo.getMesh ().render (GL10.GL_TRIANGLES);
        gl.glPopMatrix ();
        //gl.glDisable (GL10.GL_COLOR_MATERIAL);
     }
    
    private void renderMenu (GL10 gl)
    {
        Iterator <MenuMesh> currentMesh = _menuEntries.iterator ();
        float yLocation = 0.0f;
        int i = 0;
        while (currentMesh.hasNext ()) {
            gl.glPushMatrix ();
            gl.glTranslatef (0.0f, yLocation, 0.0f);
            yLocation -= 0.7f;
            gl.glRotatef (90.0f, 1.0f, 0.0f, 0.0f);
            gl.glRotatef (_yRotationAngle[i], 0.0f, 1.0f, 0.0f);
            currentMesh.next ().getMesh ().render (GL10.GL_TRIANGLES);
            gl.glPopMatrix ();
            if (_rotationDelta >= _RotationMaxDelta) {
                if (_yRotationDirection[i] == 1) {
                    _yRotationAngle[i] += _RotationAngleIncrease;
                    if (_yRotationAngle[i] >= _RotationMaxAngle) {
                        _yRotationDirection[i] = -1;
                    }
                } else {
                    _yRotationAngle[i] -= _RotationAngleIncrease;
                    if (_yRotationAngle[i] <= -_RotationMaxAngle) {
                        _yRotationDirection[i] = 1;
                    }
                }
            }
            i++;
        }
        if (_rotationDelta >= _RotationMaxDelta) {
            _rotationDelta = 0.0f;
        }
    }

}
