/**
 * @file:   com.innovail.trouble.screen - GameScreen.java
 * @date:   May 9, 2012
 * @author: bweber
 */
package com.innovail.trouble.screen;

import java.util.Iterator;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.core.GameSettings;
import com.innovail.trouble.core.TroubleApplicationState;
import com.innovail.trouble.core.TroubleGame;
import com.innovail.trouble.core.gameelement.Player;
import com.innovail.trouble.core.gameelement.Spot;
import com.innovail.trouble.core.gameelement.Token;
import com.innovail.trouble.utils.BackgroundImage;
import com.innovail.trouble.utils.GameMesh;

/**
 * 
 */
public class GameScreen implements TroubleScreen {
    private static final String TAG = "GameScreen";
    private static final String _AppPartName = TroubleApplicationState.GAME;
    
    private String _currentState = TroubleApplicationState.GAME;
    
    private boolean _filling = true;
    private float[]  _cameraPos = {0, 6, 4};
    private float[]  _cameraDir = {0, -6, -1};

    private final SpriteBatch _spriteBatch;
    private final BackgroundImage _backgroundImage;

    private Matrix4 _viewMatrix;
    private Matrix4 _transformMatrix;
    private Camera _camera;

    private TroubleGame _myGame;

    public GameScreen ()
    {
        Gdx.app.log (TAG, "GameScreen()");
        
        createInputProcessor ();

        _spriteBatch = new SpriteBatch ();
        _backgroundImage = ApplicationSettings.getInstance ().getBackgroundImage (_AppPartName);
        _backgroundImage.createTexture ().setFilter (TextureFilter.Linear, TextureFilter.Linear);

        _viewMatrix = new Matrix4 ();
        _transformMatrix = new Matrix4 ();
        
        float aspectRatio = (float) Gdx.graphics.getWidth () / (float) Gdx.graphics.getHeight ();
        _camera = new PerspectiveCamera (100, 2f * aspectRatio, 2f);
        
        _myGame = new TroubleGame ();
        _myGame.createGame ();
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
        Color currentColor = Color.WHITE;

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
        gl.glMatrixMode (GL10.GL_MODELVIEW);
        gl.glShadeModel (GL10.GL_SMOOTH);

        renderField (gl);
        renderTokens (gl);

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
        /*_spriteBatch.enableBlending ();
        _spriteBatch.setBlendFunction (GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);*/
        _spriteBatch.end ();
    }

    private void setProjectionAndCamera (GL10 gl) {
        _camera.position.set (_cameraPos);
        _camera.direction.set (_cameraDir).sub (_camera.position).nor ();
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

    private void renderField (GL10 gl)
    {
        if (_myGame != null) {
            int frontAndOrBack = GL10.GL_FRONT;
            GameMesh spotMesh = GameSettings.getInstance ().getSpotMesh ();
            Vector <Spot> spots = _myGame.getField ().getSpots ();
            Iterator <Spot> spot = spots.iterator ();
            int i = 0;
            while (spot.hasNext ()) {
                Spot currentSpot = spot.next ();
                gl.glPushMatrix ();
                gl.glTranslatef (currentSpot.getPosition ().x,
                                 currentSpot.getPosition ().y,
                                 currentSpot.getPosition ().z);
                
                Color currentColor = currentSpot.getColor ();
                gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
                //gl.glMaterialfv (frontAndOrBack, GL10.GL_SPECULAR, matSpecular, 0);
                //gl.glMaterialfv (frontAndOrBack, GL10.GL_SHININESS, matShininess, 0);
                spotMesh.getMesh ().render (GL10.GL_TRIANGLES);
                gl.glPopMatrix ();
            }
        }
    }

    private void renderTokens (GL10 gl)
    {
        if (_myGame != null) {
            GameMesh tokenMesh = GameSettings.getInstance ().getTokenMesh ();
            Vector <Player> players = _myGame.getPlayers ();
            Iterator <Player> player = players.iterator ();
            int i = 0;
            while (player.hasNext ()) {
                Player currentPlayer = player.next ();
                Vector <Token> tokens = currentPlayer.getTokens ();
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
                    tokenMesh.getMesh ().render (GL10.GL_TRIANGLES);
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
                    _cameraPos[1]++; // Y
                    Gdx.app.log (TAG, "keyDown() - +Y pos: " + _cameraPos[1]);
                    break;
                case Input.Keys.DOWN:
                    Gdx.app.log (TAG, "keyDown() - DOWN");
                    _cameraPos[1]--; // Y
                    Gdx.app.log (TAG, "keyDown() - -Y pos: " + _cameraPos[1]);
                    break;
                case Input.Keys.LEFT:
                    Gdx.app.log (TAG, "keyDown() - LEFT");
                    _cameraPos[0]--; // X
                    Gdx.app.log (TAG, "keyDown() - -X pos: " + _cameraPos[0]);
                    break;
                case Input.Keys.RIGHT:
                    Gdx.app.log (TAG, "keyDown() - RIGHT");
                    _cameraPos[0]++; // X
                    Gdx.app.log (TAG, "keyDown() - +X pos: " + _cameraPos[0]);
                    break;
                case Input.Keys.PAGE_UP:
                    Gdx.app.log (TAG, "keyDown() - PAGE_UP");
                    _cameraPos[2]--; // Z
                    Gdx.app.log (TAG, "keyDown() - -Z pos: " + _cameraPos[2]);
                    break;
                case Input.Keys.PAGE_DOWN:
                    Gdx.app.log (TAG, "keyDown() - PAGE_DOWN");
                    _cameraPos[2]++; // Z
                    Gdx.app.log (TAG, "keyDown() - +Z pos: " + _cameraPos[2]);
                    break;
                case Input.Keys.NUM_9:
                    Gdx.app.log (TAG, "keyDown() - NUM_9");
                    _cameraDir[2]--; // Z
                    Gdx.app.log (TAG, "keyDown() - -Z dir: " + _cameraDir[2]);
                    break;
                case Input.Keys.NUM_3:
                    Gdx.app.log (TAG, "keyDown() - NUM_3");
                    _cameraDir[2]++; // Z
                    Gdx.app.log (TAG, "keyDown() - +Z dir: " + _cameraDir[2]);
                    break;
                case Input.Keys.NUM_8:
                    Gdx.app.log (TAG, "keyDown() - NUM_8");
                    _cameraDir[1]++; // Y
                    Gdx.app.log (TAG, "keyDown() - +Y dir: " + _cameraDir[1]);
                    break;
                case Input.Keys.NUM_2:
                    Gdx.app.log (TAG, "keyDown() - NUM_2");
                    _cameraDir[1]--; // Y
                    Gdx.app.log (TAG, "keyDown() - -Y dir: " + _cameraDir[1]);
                    break;
                case Input.Keys.NUM_6:
                    Gdx.app.log (TAG, "keyDown() - NUM_6");
                    _cameraDir[0]++; // X
                    Gdx.app.log (TAG, "keyDown() - +X dir: " + _cameraDir[0]);
                    break;
                case Input.Keys.NUM_4:
                    Gdx.app.log (TAG, "keyDown() - NUM_4");
                    _cameraDir[0]--; // X
                    Gdx.app.log (TAG, "keyDown() - -X dir: " + _cameraDir[0]);
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
                _currentState = TroubleApplicationState.MAIN_MENU;
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
