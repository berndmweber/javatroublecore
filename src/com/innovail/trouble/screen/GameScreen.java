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
import com.innovail.trouble.utils.MathUtils;

/**
 * 
 */
public class GameScreen extends TroubleScreen {
    private static final String TAG = "GameScreen";
    private static final String AppPartName = TroubleApplicationState.GAME;
    
    private static final float[] NoMat = {0.0f, 0.0f, 0.0f, 1.0f};
    private static final int FrontAndOrBack = GL10.GL_FRONT;

    private final SpriteBatch _spriteBatch;
    private final BackgroundImage _backgroundImage;
    
    private final GameMesh _backArrowMesh;
    private final GameMesh _spotMesh;
    private final GameMesh _diceMesh;
    private final GameMesh _tokenMesh;

    private final Matrix4 _viewMatrix;
    private final Matrix4 _transformMatrix;
    private Ray _touchRay;
    
    private static final Vector3 _CameraLookAtPoint = new Vector3 (0.0f, 0.0f, 2.5f);
    private static final Vector3 _CameraPos = new Vector3 (0.0f, 7.0f, 11.0f);
    private Vector2 _cameraRotationAngleIncrease;
    
    private final float _overlayRadius;
    private Vector3 _overlayPosition; 
    private Vector2 _overlayAngle;
    private boolean _showOverlay = false;
    private final float[] _OverlayMaxAngles = {66.0f, 90.0f};
    private float _overlayAdditionalAngle = _OverlayMaxAngles[_MIN];

    private static final float _RotationAngleIncrease = 0.25f;

    private final TroubleGame _myGame;
    private final List <Spot> _spots;
    private Iterator <Spot> _spot;
    private final List <Player> _players;
    private Iterator <Player> _player;

    public GameScreen ()
    {
        super ();
        
        Gdx.app.log (TAG, "GameScreen()");
        
        _currentState = TroubleApplicationState.GAME;
        
        _spriteBatch = new SpriteBatch ();
        _backgroundImage = ApplicationSettings.getInstance ().getBackgroundImage (AppPartName);
        _backgroundImage.createTexture ().setFilter (TextureFilter.Linear, TextureFilter.Linear);
        _backArrowMesh = ApplicationSettings.getInstance ().getBackArrow ();
        _diceMesh = GameSettings.getInstance ().getDiceMesh ();

        _viewMatrix = new Matrix4 ();
        _transformMatrix = new Matrix4 ();
        
        final float aspectRatio = (float) Gdx.graphics.getWidth () / (float) Gdx.graphics.getHeight ();
        _camera = new GamePerspectiveCamera (67, 2f * aspectRatio, 2f);
        ((GamePerspectiveCamera)_camera).lookAtPosition (_CameraLookAtPoint, _CameraPos);
        _cameraRotationAngleIncrease = new Vector2 ();
        _cameraRotationAngleIncrease.set (Vector2.Zero);
        _overlayRadius = ((GamePerspectiveCamera)_camera).getOverlayRadius ();
        _overlayAngle = ((GamePerspectiveCamera)_camera).getOverlayAngle ();
        _overlayPosition = MathUtils.getSpherePosition (_CameraLookAtPoint,
                                                        _overlayAngle,
                                                        _overlayRadius);
        /* Need to do this early to be able to calculate the right bounding box. */
        _backArrowMesh.getMesh ();
        calculateOverlayBoundingBox ();
        
        _myGame = new TroubleGame ();
        _myGame.createGame ();

        _spotMesh = GameSettings.getInstance ().getSpotMesh ();
        _spots = _myGame.getField ().getSpots ();
        _tokenMesh = GameSettings.getInstance ().getTokenMesh ();
        _players = _myGame.getPlayers ();
    }
    
    protected void update (final float delta)
    {
        _myGame.updateGame ();
        if (_myGame.isFinished ()) {
            _currentState = TroubleApplicationState.MAIN_MENU;
        }
    }
    
    protected void render (final GL10 gl, final float delta)
    {
        renderOverlay (gl);
        
        renderField (gl);
        renderTokens (gl);

        gl.glEnable (GL10.GL_TEXTURE_2D);
        renderDice (gl);
        gl.glDisable (GL10.GL_TEXTURE_2D);
        
        gl.glDisable (GL10.GL_CULL_FACE);
        gl.glDisable (GL10.GL_DEPTH_TEST);
    }

    protected void renderBackground (final float width, final float height)
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
        _spriteBatch.end ();
    }
    
    private void renderOverlay (final GL10 gl)
    {
        gl.glPushMatrix ();
        gl.glTranslatef (_overlayPosition.x, _overlayPosition.y, _overlayPosition.z);
        gl.glRotatef (_overlayAngle.x, 0.0f, 1.0f, 0.0f);
        gl.glRotatef (_overlayAngle.y + _overlayAdditionalAngle, 1.0f, 0.0f, 0.0f);
        final Color currentColor = _backArrowMesh.getColor ();
        gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
        _backArrowMesh.getMesh ().render (GL10.GL_TRIANGLES);
        gl.glPopMatrix ();
        if (_DEBUG) {
            Gdx.gl10.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_LINE);
            gl.glPushMatrix ();
            _backArrowMesh.getBBMesh ().render (GL10.GL_TRIANGLES);
            gl.glPopMatrix ();
            Gdx.gl10.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_FILL);
        }
        
        if (_showOverlay && (_overlayAdditionalAngle < _OverlayMaxAngles[_MAX])) {
            _overlayAdditionalAngle += 2 * _RotationAngleIncrease;
            if (_overlayAdditionalAngle > _OverlayMaxAngles[_MAX]) {
                _overlayAdditionalAngle = _OverlayMaxAngles[_MAX];
            }
            calculateOverlayBoundingBox ();
        } else if (!_showOverlay && (_overlayAdditionalAngle > _OverlayMaxAngles[_MIN])) {
            _overlayAdditionalAngle -= 2 * _RotationAngleIncrease;
            if (_overlayAdditionalAngle < _OverlayMaxAngles[_MIN]) {
                _overlayAdditionalAngle = _OverlayMaxAngles[_MIN];
            }
            calculateOverlayBoundingBox ();
        }
    }

    protected void setLighting (final GL10 gl) {
        gl.glEnable (GL10.GL_LIGHTING);
        gl.glLightModelf (GL10.GL_LIGHT_MODEL_TWO_SIDE, 0.0f);
        
        gl.glEnable (GL10.GL_LIGHT0);
        
        gl.glEnable (GL10.GL_COLOR_MATERIAL);
        gl.glEnable (GL10.GL_BLEND);
    }

    private void renderDice (final GL10 gl)
    {
        final Matrix4 transform = new Matrix4();
        final Matrix4 tmp = new Matrix4();

        _diceMesh.getTexture ().bind ();
        gl.glPushMatrix ();
        gl.glTranslatef (0.0f, 0.0f, 7.0f);
        transform.setToTranslation (0.0f, 0.0f, 7.0f);
        if (_myGame != null) {
            final float [] angle = _myGame.getDice ().getFaceAngle (0);
            gl.glRotatef (angle[0], angle[1], angle[2], angle[3]);
            tmp.setToRotation (angle[1], angle[2], angle[3], angle[0]);
            transform.mul(tmp);
        }
        
        final Color currentColor = Color.WHITE;
        gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
        _diceMesh.getMesh ().render (GL10.GL_TRIANGLES);
        gl.glPopMatrix ();
        
        _diceMesh.transformBoundingBox (transform);
    }

    private void renderField (final GL10 gl)
    {
        if (_myGame != null) {
            _spot = _spots.iterator ();
            while (_spot.hasNext ()) {
                final Spot currentSpot = _spot.next ();
                gl.glPushMatrix ();
                gl.glTranslatef (currentSpot.getPosition ().x,
                                 currentSpot.getPosition ().y,
                                 currentSpot.getPosition ().z);
                final Color currentColor = currentSpot.getColor ();
                if ((currentSpot.getPotentialToken () != null) ||
                    ((currentSpot.getCurrentToken () != null) &&
                     (currentSpot.getCurrentToken ().isSelected () &&
                      currentSpot.getCurrentToken ().isMoving ()))) {
                    final float[] matEmission = {currentColor.r == 1.0f ? currentColor.r : 0.3f,
                            currentColor.g == 1.0f ? currentColor.g : 0.3f,
                            currentColor.b == 1.0f ? currentColor.b : 0.3f,
                            1.0f};
                    gl.glMaterialfv (FrontAndOrBack, GL10.GL_EMISSION, matEmission, 0);
                }
                gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
                _spotMesh.getMesh ().render (GL10.GL_TRIANGLES);
                gl.glPopMatrix ();
                gl.glMaterialfv (FrontAndOrBack, GL10.GL_EMISSION, NoMat, 0);
            }
        }
    }

    private void renderTokens (final GL10 gl)
    {
        if (_myGame != null) {
            _player = _players.iterator ();
            while (_player.hasNext ()) {
                final Player currentPlayer = _player.next ();
                final List <Token> tokens = currentPlayer.getTokens ();
                final Iterator <Token> token = tokens.iterator ();
                while (token.hasNext ()) {
                    final Token currentToken = token.next ();
                    gl.glPushMatrix ();
                    
                    if (!currentToken.isMoving ()) {
                        final Spot currentSpot = currentToken.getPosition ();
                        gl.glTranslatef (currentSpot.getPosition ().x,
                                         currentSpot.getPosition ().y,
                                         currentSpot.getPosition ().z);
                    } else {
                        final Vector3 currentPosition = currentToken.getCurrentMovePosition ();
                        gl.glTranslatef (currentPosition.x,
                                         currentPosition.y,
                                         currentPosition.z);
                    }
                    final Color currentColor = currentPlayer.getColor ();
                    gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
                    
                    if (currentToken.isSelected ()) {
                        final float[] matEmission = {currentColor.r == 1.0f ? currentColor.r : 0.3f,
                                                      currentColor.g == 1.0f ? currentColor.g : 0.3f,
                                                      currentColor.b == 1.0f ? currentColor.b : 0.3f,
                                                      1.0f};
                        gl.glMaterialfv (FrontAndOrBack, GL10.GL_EMISSION, matEmission, 0);
                    }
                    
                    _tokenMesh.getMesh ().render (GL10.GL_TRIANGLES);
                    gl.glPopMatrix ();
                    gl.glMaterialfv (FrontAndOrBack, GL10.GL_EMISSION, NoMat, 0);
                }
            }
        }
    }
    
    private void calculateOverlayBoundingBox ()
    {
        _overlayAngle = ((GamePerspectiveCamera)_camera).getOverlayAngle ();
        _overlayPosition = MathUtils.getSpherePosition (_CameraLookAtPoint,
                                                        _overlayAngle,
                                                        _overlayRadius);
        final Matrix4 transform = new Matrix4();
        final Matrix4 tmp = new Matrix4();
        transform.setToTranslation (_overlayPosition.x,
                                    _overlayPosition.y,
                                    _overlayPosition.z);
        tmp.setToRotation (0.0f, 1.0f, 0.0f, _overlayAngle.x);
        transform.mul(tmp);
        tmp.setToRotation (1.0f, 0.0f, 0.0f, _overlayAngle.y + _overlayAdditionalAngle);
        transform.mul(tmp);
        _backArrowMesh.transformBoundingBox (transform);

        if (_DEBUG) {
            Gdx.app.log (TAG, "Retrieved angles: " + _overlayAngle.toString ());
            Gdx.app.log (TAG, "New Overlay position: " + _overlayPosition.toString ());
        }
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
            public boolean keyDown (final int keycode) {
                boolean rv = true;
                
                switch (keycode) {
                case Input.Keys.SPACE:
                    Gdx.app.log (TAG, "keyDown() - SPACE");
                    break;
                case Input.Keys.R:
                    if (_filling) {
                        Gdx.app.log (TAG, "keyDown() - wireframing");
                        Gdx.gl10.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_LINE);
                        _filling = false;
                    } else {
                        Gdx.app.log (TAG, "keyDown() - Filling");
                        Gdx.gl10.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_FILL);
                        _filling = true;
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
            public boolean touchUp (final int x, final int y, final int pointer, final int button) {
                if (!_isDragged || (_dragEvents < MIN_NUMBER_OF_DRAGS)) {
                    _touchRay = _camera.getPickRay (x, y, 0, 0, Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
                    if (_DEBUG) {
                        Gdx.app.log (TAG, "Touch position - x: " + x + " - y: " + y);
                        Gdx.app.log (TAG, "Touch ray - " + _touchRay.toString ());
                    }
                    if (_touchRay != null) {
                        if (_DEBUG) {
                            Gdx.app.log (TAG, "currentEntry BB - " + _diceMesh.getBoundingBox ().toString ());
                        }
                        if (Intersector.intersectRayBoundsFast (_touchRay, _backArrowMesh.getBoundingBox ())) {
                            _currentState = TroubleApplicationState.MAIN_MENU;
                        } else if (Intersector.intersectRayBoundsFast (_touchRay, _diceMesh.getBoundingBox ())) {
                            _myGame.rollDice ();
                        } else if (!_myGame.getAvailableTokens ().isEmpty ()) {
                            final Iterator <Token> tokens = _myGame.getAvailableTokens ().iterator ();
                            while (tokens.hasNext ()) {
                                final Token token = tokens.next ();
                                final Spot currentSpot = token.getPosition ();
                                final Matrix4 transform = new Matrix4();
                                transform.setToTranslation (currentSpot.getPosition ().x,
                                                            currentSpot.getPosition ().y,
                                                            currentSpot.getPosition ().z);
                                _tokenMesh.transformBoundingBox (transform);
                                if (Intersector.intersectRayBoundsFast (_touchRay, _tokenMesh.getBoundingBox ())) {
                                    _myGame.selectToken (token);
                                    break;
                                }
                            }
                        } else {
                            if (_showOverlay) {
                                _showOverlay = false;
                            } else {
                                _showOverlay = true;
                            }
                        }
                    }
                }
                return super.touchUp (x, y, pointer, button);
            }

            /* (non-Javadoc)
             * @see com.badlogic.gdx.InputProcessor#touchDragged(int, int, int)
             */
            @Override
            public boolean touchDragged (final int x, final int y, final int pointer) {
                if (_dragEvents >= MIN_NUMBER_OF_DRAGS) {
                    if (_DEBUG) {
                        Gdx.app.log (TAG, "Touch dragged position - x: " + x + " - y: " + y);
                    }
                    _axisDiff.set (_lastPosition);
                    _axisDiff.sub (x, y);
                    if ((_axisDiff.x < _MaxAxisIncrease) && (_axisDiff.y != _MaxAxisIncrease)) {
                        _cameraRotationAngleIncrease.x = _axisDiff.x * _RotationAngleIncrease;
                        _cameraRotationAngleIncrease.y = _axisDiff.y * _RotationAngleIncrease;
                    }
                    ((GamePerspectiveCamera)_camera).rotateAroundLookAtPoint (_cameraRotationAngleIncrease);
                    _cameraRotationAngleIncrease.set (Vector2.Zero);
                    calculateOverlayBoundingBox ();
                }
                return super.touchDragged (x, y, pointer);
            }
        });
    }
}
