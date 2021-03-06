/**
 * @file: com.innovail.trouble.screen - MainMenuScreen.java
 * @date: Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.screen;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.Ray;

import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.core.TroubleApplicationState;
import com.innovail.trouble.graphics.GameFont;
import com.innovail.trouble.graphics.GameFont.FontType;
import com.innovail.trouble.graphics.GameMesh;
import com.innovail.trouble.uicomponent.BackgroundImage;
import com.innovail.trouble.uicomponent.MenuEntry;
import com.innovail.trouble.utils.GameInputAdapter;

/**
 * 
 */
public class MainMenuScreen extends TroubleScreen
{
    private static final String TAG = "MainMenuScreen";
    private static final String AppPartName = TroubleApplicationState.MAIN_MENU;

    private final BitmapFont _menuFont;
    private final SpriteBatch _spriteBatch;
    private final BackgroundImage _backgroundImage;

    private final GameMesh _logo;
    private final List <GameMesh> _menuEntriesList;

    private final float [] _yRotationAngle;
    private final int [] _yRotationDirection;
    private static final float _RotationMaxAngle = 2.0f;
    private static final float _RotationAngleIncrease = 0.2f;
    private static float _rotationDelta = 0.0f;
    private static final float _RotationMaxDelta = 0.05f;

    private final Matrix4 _viewMatrix;
    private final Matrix4 _transformMatrix;

    public MainMenuScreen ()
    {
        super ();

        Gdx.app.log (TAG, "MainMenuScreen()");

        _spriteBatch = new SpriteBatch ();
        _menuFont = ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.BITMAP)).getBitmapFont ();
        _backgroundImage = ApplicationSettings.getInstance ().getBackgroundImage (AppPartName);

        _logo = ApplicationSettings.getInstance ().getLogo ();

        _menuEntriesList = ApplicationSettings.getInstance ().getMenuEntryList (AppPartName);
        _yRotationAngle = new float [_menuEntriesList.size ()];
        _yRotationDirection = new int [_menuEntriesList.size ()];
        final Random rand = new Random ();
        for (int i = 0; i < _menuEntriesList.size (); i++) {
            _yRotationAngle[i] = rand.nextFloat ();
            _yRotationAngle[i] *= rand.nextBoolean () ? 1.0f : -1.0f;
            _yRotationDirection[i] = rand.nextBoolean () ? 1 : -1;
        }

        _viewMatrix = new Matrix4 ();
        _transformMatrix = new Matrix4 ();

        final float aspectRatio = (float) Gdx.graphics.getWidth () / (float) Gdx.graphics.getHeight ();
        _camera = new PerspectiveCamera (100, 2f * aspectRatio, 2f);
        _camera.position.set (0, 0, 2);
        _camera.direction.set (0, 0, -4).sub (_camera.position).nor ();

        showFrontAndBack ();
    }

    @Override
    public void createInputProcessor ()
    {
        Gdx.input.setInputProcessor (new GameInputAdapter () {

            /*
             * (non-Javadoc)
             * @see com.badlogic.gdx.InputProcessor#keyDown(int)
             */
            @Override
            public boolean keyDown (final int keycode)
            {
                boolean rv = true;

                switch (keycode) {
                case Input.Keys.SPACE:
                    Gdx.app.log (TAG, "keyDown() - SPACE");
                    break;
                case Input.Keys.R:
                    if (_filling) {
                        Gdx.app.log (TAG, "keyDown() - wireframing");
                        Gdx.gl11.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_LINE);
                        _filling = false;
                    } else {
                        Gdx.app.log (TAG, "keyDown() - Filling");
                        Gdx.gl11.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_FILL);
                        _filling = true;
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

            /*
             * (non-Javadoc)
             * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
             */
            @Override
            public boolean touchUp (final int x, final int y,
                                    final int pointer, final int button)
            {
                if (!_isDragged || (_dragEvents < MIN_NUMBER_OF_DRAGS)) {
                    final Iterator <GameMesh> currentMesh = _menuEntriesList.iterator ();
                    int j = 0;
                    final Ray touchRay = _camera.getPickRay (x, y, 0, 0, Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
                    if (_DEBUG) {
                        Gdx.app.log (TAG, "Touch position - x: " + x + " - y: " + y);
                        Gdx.app.log (TAG, "Touch ray - " + touchRay.toString ());
                    }
                    while (currentMesh.hasNext ()) {
                        final MenuEntry currentEntry = (MenuEntry) currentMesh.next ();
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

    @Override
    protected void render (final GL11 gl, final float delta)
    {
        _rotationDelta += delta;

        Gdx.gl11.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_FILL);

        renderLogo (gl);
        renderMenu (gl);

        gl.glDisable (GL10.GL_CULL_FACE);
        gl.glDisable (GL10.GL_DEPTH_TEST);
    }

    @Override
    protected void renderBackground (final float width, final float height)
    {
        _viewMatrix.setToOrtho2D (0.0f, 0.0f, width, height);
        _spriteBatch.setProjectionMatrix (_viewMatrix);
        _spriteBatch.setTransformMatrix (_transformMatrix);
        _spriteBatch.begin ();
        _spriteBatch.disableBlending ();
        _spriteBatch.setColor (Color.WHITE);
        _spriteBatch.draw ((Texture) _backgroundImage.getImageObject (), 0, 0, width, height, 0, 0, _backgroundImage.getWidth (), _backgroundImage.getHeight (), false, false);
        _spriteBatch.enableBlending ();
        _spriteBatch.setBlendFunction (GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        final String text = ApplicationSettings.getInstance ().getCopyRightNotice ();
        final float textWidth = _menuFont.getBounds (text).width;
        final float textHeight = _menuFont.getBounds (text).height;
        _menuFont.draw (_spriteBatch, text, Gdx.graphics.getWidth () / 2 - textWidth / 2, textHeight + 5);
        _spriteBatch.end ();
    }

    private void renderLogo (final GL11 gl)
    {
        final Color currentColor = _logo.getColor ();

        gl.glPushMatrix ();
        gl.glTranslatef (0.0f, 1.0f, 0.3f);
        gl.glRotatef (90.0f, 1.0f, 0.0f, 0.0f);
        gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
        _logo.getMesh ().render ();
        gl.glPopMatrix ();
    }

    private void renderMenu (final GL11 gl)
    {
        final Iterator <GameMesh> currentMesh = _menuEntriesList.iterator ();
        float yLocation = 0.0f;
        int i = 0;
        while (currentMesh.hasNext ()) {
            final GameMesh thisMesh = currentMesh.next ();
            gl.glPushMatrix ();
            gl.glTranslatef (0.0f, yLocation, 0.0f);
            gl.glRotatef (_yRotationAngle[i], 0.0f, 0.0f, 1.0f);
            thisMesh.getMesh ().render ();
            gl.glPopMatrix ();

            final Matrix4 transform = new Matrix4 ();
            final Matrix4 tmp = new Matrix4 ();
            transform.setToTranslation (0.0f, yLocation, 0.0f);
            tmp.setToRotation (0.0f, 0.0f, 1.0f, _yRotationAngle[i]);
            transform.mul (tmp);
            thisMesh.transformBoundingBox (transform);

            if (_DEBUG) {
                Gdx.gl11.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_LINE);
                gl.glPushMatrix ();
                thisMesh.getBBMesh ().render (GL10.GL_TRIANGLES);
                gl.glPopMatrix ();
                Gdx.gl11.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_FILL);
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

    @Override
    protected void setLighting (final GL11 gl)
    {
        setLighting (gl, _logo.getColor ());
    }

    @Override
    public void setOwnState ()
    {
        _currentState = TroubleApplicationState.MAIN_MENU;
    }

    @Override
    protected void update (final float delta)
    {
        /* Nothing to do here. */
    }
}
