/**
 * @file:   com.innovail.trouble.screen - GameScreen.java
 * @date:   May 9, 2012
 * @author: bweber
 */
package com.innovail.trouble.screen;

import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.core.GameSettings;
import com.innovail.trouble.core.TroubleApplicationState;
import com.innovail.trouble.core.TroubleGame;
import com.innovail.trouble.core.gameelement.Player;
import com.innovail.trouble.core.gameelement.Spot;
import com.innovail.trouble.core.gameelement.Token;
import com.innovail.trouble.utils.BackgroundImage;
import com.innovail.trouble.utils.GameInputAdapter;
import com.innovail.trouble.utils.GameMesh;
import com.innovail.trouble.utils.GamePerspectiveCamera;

/**
 * 
 */
public class GameScreen implements TroubleScreen {
    private static final String TAG = "GameScreen";
    private static final String AppPartName = TroubleApplicationState.GAME;
    
    private static final boolean _DEBUG = false;

    private String _currentState = TroubleApplicationState.GAME;
    
    private boolean _filling = true;

    private final SpriteBatch _spriteBatch;
    private final BackgroundImage _backgroundImage;
    
    private final GameMesh _spotMesh;
    private final GameMesh _diceMesh;
    private final GameMesh _tokenMesh;

    private final Matrix4 _viewMatrix;
    private final Matrix4 _transformMatrix;
    private final Camera _camera;
    private Ray _touchRay;
    
    private Vector3 _cameraLookAtPoint;
    private Vector2 _cameraRotationAngleIncrease;
    private final Vector3 _cameraPos;

    private static final float _RotationAngleIncrease = 0.25f;
    private float _rotationDelta = 0.0f;

    private TroubleGame _myGame;
    private List <Spot> _spots;
    private Iterator <Spot> _spot;
    private List <Player> _players;
    private Iterator <Player> _player;

    public GameScreen ()
    {
        Gdx.app.log (TAG, "GameScreen()");
        
        createInputProcessor ();

        _spriteBatch = new SpriteBatch ();
        _backgroundImage = ApplicationSettings.getInstance ().getBackgroundImage (AppPartName);
        _backgroundImage.createTexture ().setFilter (TextureFilter.Linear, TextureFilter.Linear);
        _diceMesh = GameSettings.getInstance ().getDiceMesh ();

        _viewMatrix = new Matrix4 ();
        _transformMatrix = new Matrix4 ();
        
        final float aspectRatio = (float) Gdx.graphics.getWidth () / (float) Gdx.graphics.getHeight ();
        _camera = new GamePerspectiveCamera (67, 2f * aspectRatio, 2f);
        _cameraPos = new Vector3 (0.0f, 7.0f, 11.0f);
        _cameraLookAtPoint = new Vector3 (0.0f, 0.0f, 2.5f);
        ((GamePerspectiveCamera)_camera).lookAtPosition (_cameraLookAtPoint, _cameraPos);
        _cameraRotationAngleIncrease = new Vector2 ();
        _cameraRotationAngleIncrease.set (Vector2.Zero);
        
        _myGame = new TroubleGame ();
        _myGame.createGame ();

        _spotMesh = GameSettings.getInstance ().getSpotMesh ();
        _spots = _myGame.getField ().getSpots ();
        _tokenMesh = GameSettings.getInstance ().getTokenMesh ();
        _players = _myGame.getPlayers ();
}

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#render(float)
     */
    @Override
    public void render (float delta) {
        _rotationDelta += delta;
        final GL10 gl = Gdx.graphics.getGL10();
        final Color currentColor = Color.WHITE;

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

        renderField (gl);
        renderTokens (gl);

        gl.glEnable (GL10.GL_TEXTURE_2D);
        renderDice (gl);
        gl.glDisable (GL10.GL_TEXTURE_2D);
        
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
        /*_spriteBatch.enableBlending ();
        _spriteBatch.setBlendFunction (GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);*/
        _spriteBatch.end ();
    }

    private void setProjectionAndCamera (GL10 gl) {
        _camera.update ();
        _camera.apply (gl);
    }
    
    private void setLighting (GL10 gl) {
        gl.glEnable (GL10.GL_LIGHTING);
        gl.glLightModelf (GL10.GL_LIGHT_MODEL_TWO_SIDE, 0.0f);
        
        gl.glEnable (GL10.GL_LIGHT0);
        
        gl.glEnable (GL10.GL_COLOR_MATERIAL);
        gl.glEnable (GL10.GL_BLEND);
    }

    private void renderDice (GL10 gl)
    {
        final Matrix4 transform = new Matrix4();
        final Matrix4 tmp = new Matrix4();

        _diceMesh.getTexture ().bind ();
        gl.glPushMatrix ();
        gl.glTranslatef (0.0f, 0.0f, 7.0f);
        transform.setToTranslation (0.0f, 0.0f, 7.0f);
        if (_myGame != null) {
            float [] angle = _myGame.getDice ().getFaceAngle (0);
            gl.glRotatef (angle[0], angle[1], angle[2], angle[3]);
            tmp.setToRotation (angle[1], angle[2], angle[3], angle[0]);
            transform.mul(tmp);
        }
        
        Color currentColor = Color.WHITE;
        gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
        //gl.glMaterialfv (frontAndOrBack, GL10.GL_SPECULAR, matSpecular, 0);
        //gl.glMaterialfv (frontAndOrBack, GL10.GL_SHININESS, matShininess, 0);
        _diceMesh.getMesh ().render (GL10.GL_TRIANGLES);
        gl.glPopMatrix ();
        
        _diceMesh.transformBoundingBox (transform);
    }

    private void renderField (GL10 gl)
    {
        if (_myGame != null) {
            //int frontAndOrBack = GL10.GL_FRONT;
            _spot = _spots.iterator ();
            while (_spot.hasNext ()) {
                Spot currentSpot = _spot.next ();
                gl.glPushMatrix ();
                gl.glTranslatef (currentSpot.getPosition ().x,
                                 currentSpot.getPosition ().y,
                                 currentSpot.getPosition ().z);
                
                Color currentColor = currentSpot.getColor ();
                gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
                //gl.glMaterialfv (frontAndOrBack, GL10.GL_SPECULAR, matSpecular, 0);
                //gl.glMaterialfv (frontAndOrBack, GL10.GL_SHININESS, matShininess, 0);
                _spotMesh.getMesh ().render (GL10.GL_TRIANGLES);
                gl.glPopMatrix ();
            }
        }
    }

    private void renderTokens (GL10 gl)
    {
        if (_myGame != null) {
            _player = _players.iterator ();
            while (_player.hasNext ()) {
                Player currentPlayer = _player.next ();
                List <Token> tokens = currentPlayer.getTokens ();
                Iterator <Token> token = tokens.iterator ();
                while (token.hasNext ()) {
                    Token currentToken = token.next ();
                    Spot currentSpot = currentToken.getPosition ();
                    gl.glPushMatrix ();
                    gl.glTranslatef (currentSpot.getPosition ().x,
                                     currentSpot.getPosition ().y,
                                     currentSpot.getPosition ().z);
                    gl.glRotatef (90.0f, 1.0f, 0.0f, 0.0f);
                    Color currentColor = currentPlayer.getColor ();
                    gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
                    _tokenMesh.getMesh ().render (GL10.GL_TRIANGLES);
                    gl.glPopMatrix ();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#resize(int, int)
     */
    @Override
    public void resize (int width, int height) {
        // TODO Auto-generated method stub
        
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
     * @see com.innovail.trouble.screen.TroubleScreen#createInputProcessor()
     */
    @Override
    public void createInputProcessor () {
        Gdx.input.setInputProcessor (new GameInputAdapter () {
            private static final float _MaxAxisIncrease = 10.0f;
            private Vector2 _axisDiff = new Vector2 ();
            
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
                    _cameraRotationAngleIncrease.y--; // Y
                    Gdx.app.log (TAG, "keyDown() - -Y angle: " + _cameraRotationAngleIncrease.y);
                    break;
                case Input.Keys.DOWN:
                    Gdx.app.log (TAG, "keyDown() - DOWN");
                    _cameraRotationAngleIncrease.y++; // Y
                    Gdx.app.log (TAG, "keyDown() - +Y angle: " + _cameraRotationAngleIncrease.y);
                    break;
                case Input.Keys.LEFT:
                    Gdx.app.log (TAG, "keyDown() - LEFT");
                    _cameraRotationAngleIncrease.x--; // X
                    Gdx.app.log (TAG, "keyDown() - -X angle: " + _cameraRotationAngleIncrease.x);
                    break;
                case Input.Keys.RIGHT:
                    Gdx.app.log (TAG, "keyDown() - RIGHT");
                    _cameraRotationAngleIncrease.x++; // X
                    Gdx.app.log (TAG, "keyDown() - +X angle: " + _cameraRotationAngleIncrease.x);
                    break;
                default:
                    rv = false;
                }
                ((GamePerspectiveCamera)_camera).rotateAroundLookAtPoint (_cameraRotationAngleIncrease);
                _cameraRotationAngleIncrease.set (Vector2.Zero);
                return rv;
            }

            /* (non-Javadoc)
             * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
             */
            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {
                if (!_isDragged || (_dragEvents < MIN_NUMBER_OF_DRAGS)) {
                    _touchRay = _camera.getPickRay (x, y, 0, 0, Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
                    if (_DEBUG) {
                        Gdx.app.log (TAG, "Touch position - x: " + x + " - y: " + y);
                        Gdx.app.log (TAG, "Touch ray - " + _touchRay.toString ());
                    }
                    //while (currentMesh.hasNext ()) {
                        //MenuEntryMesh currentEntry = (MenuEntryMesh)currentMesh.next ();
                        if (_touchRay != null) {
                            if (_DEBUG) {
                                Gdx.app.log (TAG, "currentEntry BB - " + _diceMesh.getBoundingBox ().toString ());
                            }
                            if (Intersector.intersectRayBoundsFast (_touchRay, _diceMesh.getBoundingBox ())) {
                                int[] result = _myGame.getDice ().roll ();
                                Gdx.app.log (TAG, "Die touched -> " + result[0]);
                                //Gdx.app.log (TAG, "Mesh " + j + " touched -> " + _currentState);
                                //break;
                            } else {
                                _currentState = TroubleApplicationState.MAIN_MENU;
                            }
                        }
                        //j++;
                    //}
                    //_currentState = TroubleApplicationState.MAIN_MENU;
                }
                return super.touchUp (x, y, pointer, button);
            }

            /* (non-Javadoc)
             * @see com.badlogic.gdx.InputProcessor#touchDragged(int, int, int)
             */
            @Override
            public boolean touchDragged (int x, int y, int pointer) {
                if (_dragEvents >= MIN_NUMBER_OF_DRAGS) {
                    Gdx.app.log (TAG, "Touch dragged position - x: " + x + " - y: " + y);
                    _axisDiff.set (_lastPosition);
                    _axisDiff.sub (x, y);
                    if ((_axisDiff.x < _MaxAxisIncrease) && (_axisDiff.y != _MaxAxisIncrease)) {
                        _cameraRotationAngleIncrease.x = _axisDiff.x * _RotationAngleIncrease;
                        _cameraRotationAngleIncrease.y = _axisDiff.y * _RotationAngleIncrease;
                    }
                    ((GamePerspectiveCamera)_camera).rotateAroundLookAtPoint (_cameraRotationAngleIncrease);
                    _cameraRotationAngleIncrease.set (Vector2.Zero);
                }
                return super.touchDragged (x, y, pointer);
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
