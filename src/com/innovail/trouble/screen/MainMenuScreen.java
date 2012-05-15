/**
 * @file:   com.innovail.trouble.core - MenuScreen.java
 * @date:   Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.screen;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.Ray;
import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.core.TroubleApplicationState;
import com.innovail.trouble.utils.BackgroundImage;
import com.innovail.trouble.utils.GameInputAdapter;
import com.innovail.trouble.utils.MenuEntryMesh;
import com.innovail.trouble.utils.GameMesh;

/**
 * 
 */
public class MainMenuScreen implements TroubleScreen {
    private static final String TAG = "MainMenuScreen";
    private static final String _AppPartName = TroubleApplicationState.MAIN_MENU;
    
    private final boolean _DEBUG = false;

    private String _currentState = TroubleApplicationState.MAIN_MENU;

    private boolean _filling = true;
    
    private final BitmapFont _menuFont;
    private final SpriteBatch _spriteBatch;
    private final BackgroundImage _backgroundImage;

    private GameMesh _logo;
    private HashMap <String, GameMesh> _menuEntriesMap;
    private Collection <GameMesh> _menuEntries;
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
        
        createInputProcessor ();
        
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

        gl.glMatrixMode (GL10.GL_MODELVIEW);
        gl.glShadeModel (GL10.GL_SMOOTH);

        renderLogo (gl);
        renderMenu (gl);

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
        Iterator <GameMesh> currentMesh = _menuEntries.iterator ();
        float yLocation = 0.0f;
        int i = 0;
        while (currentMesh.hasNext ()) {
            GameMesh thisMesh = currentMesh.next ();
            gl.glPushMatrix ();
            gl.glTranslatef (0.0f, yLocation, 0.0f);
            gl.glRotatef (90.0f, 1.0f, 0.0f, 0.0f);
            gl.glRotatef (_yRotationAngle[i], 0.0f, 1.0f, 0.0f);
            thisMesh.getMesh ().render (GL10.GL_TRIANGLES);
            gl.glPopMatrix ();
            
            Matrix4 transform = new Matrix4();
            Matrix4 tmp = new Matrix4();
            transform.setToTranslation (0.0f, yLocation, 0.0f);
            tmp.setToRotation (1.0f, 0.0f, 0.0f, 90.0f);
            transform.mul(tmp);
            tmp.setToRotation (0.0f, 1.0f, 0.0f, _yRotationAngle[i]);
            transform.mul(tmp);
            thisMesh.transformBoundingBox (transform);
            
            if (_DEBUG) {
                Gdx.gl10.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_LINE);
                gl.glPushMatrix ();
                thisMesh.getBBMesh ().render (GL10.GL_TRIANGLES);
                gl.glPopMatrix ();
                Gdx.gl10.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_FILL);
            }
            
            yLocation -= 0.7f;
            
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

    public void createInputProcessor ()
    {
        Gdx.input.setInputProcessor (new GameInputAdapter() {

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
             * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
             */
            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {
                if (!_isDragged || (_dragEvents < MIN_NUMBER_OF_DRAGS)) {
                    Iterator <GameMesh> currentMesh = _menuEntries.iterator ();
                    int j = 0;
                    Ray touchRay = _camera.getPickRay (x, y, 0, 0, Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
                    if (_DEBUG) {
                        Gdx.app.log (TAG, "Touch position - x: " + x + " - y: " + y);
                        Gdx.app.log (TAG, "Touch ray - " + touchRay.toString ());
                    }
                    while (currentMesh.hasNext ()) {
                        MenuEntryMesh currentEntry = (MenuEntryMesh)currentMesh.next ();
                        if (touchRay != null) {
                            if (_DEBUG) {
                                Gdx.app.log (TAG, "currentEntry BB - " + currentEntry.getBoundingBox ().toString ());
                            }
                            if (Intersector.intersectRayBoundsFast (touchRay, currentEntry.getBoundingBox ())) {
                                _currentState = currentEntry.getName ();
                                Gdx.app.log (TAG, "Mesh " + j + " touched -> " + _currentState);
                                break;
                            }
                        }
                        j++;
                    }
                }
                return super.touchUp (x, y, pointer, button);
            }
        });
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
     * @see com.innovail.trouble.screen.TroubleScreen#getState()
     */
    @Override
    public String getState () {
        return _currentState;
    }
}
