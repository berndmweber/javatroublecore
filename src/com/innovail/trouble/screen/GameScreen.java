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
import com.badlogic.gdx.graphics.GL11;
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
import com.innovail.trouble.graphics.GameMesh;
import com.innovail.trouble.graphics.GamePerspectiveCamera;
import com.innovail.trouble.uicomponent.BackgroundImage;
import com.innovail.trouble.utils.GameInputAdapter;
import com.innovail.trouble.utils.MathUtils;

/**
 * 
 */
public class GameScreen extends TroubleScreen {
    private static final String TAG = "GameScreen";
    private static final String AppPartName = TroubleApplicationState.GAME;
    
    private static final float[] NoMat = {0.0f, 0.0f, 0.0f, 1.0f};
    private static final int FrontAndOrBack = GL11.GL_FRONT_AND_BACK;
    
    private static final float _UP = 1.0f;
    private static final float _DOWN = -1.0f;
    
    private final SpriteBatch _spriteBatch;
    private final BackgroundImage _backgroundImage;
    
    private final GameMesh _playerMesh;
    private final List <GameMesh> _playerNumberMesh;
    private final GameMesh _backArrowMesh;
    
    private final GameMesh _spotMesh;
    private final GameMesh _diceMesh;
    private final GameMesh _tokenMesh;

    private final Matrix4 _viewMatrix;
    private final Matrix4 _transformMatrix;
    private Ray _touchRay;
    
    private static final float _RotationAngleIncrease = 0.25f;

    private static final Vector3 _CameraLookAtPoint = new Vector3 (0.0f, 0.0f, 2.5f);
    private static final Vector3 _CameraPos = new Vector3 (0.0f, 7.0f, 11.0f);
    private Vector2 _cameraRotationAngleIncrease;
    
    private static final float[] _OverlayMaxAngles = {66.0f, 90.0f};
    private static final float[] _OverlayMaxAlphas = {0.0f, 1.0f};
    private static final float _AlphaValueIncrease = 0.5f;
    private static final float _OverlayMaxVisibleTime = 0.7f;
    private final float _overlayRadius;
    private Vector3 _overlayPosition; 
    private Vector2 _overlayAngle;
    private boolean _showOverlay = false;
    private float _overlayAdditionalAngle = _OverlayMaxAngles[_MIN];
    private float _overlayAlpha = _OverlayMaxAlphas[_MAX];
    private float _overlayVisibleTime = 0.0f;
    
    private static final float _SelectedYOffset = 0.05f;
    private static final float[] _WobbleMaxAngles = {-0.5f, 0.5f};
    private Vector2 _wobbleAngle;
    private Vector2 _wobbleDirection;

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
        _playerMesh = GameSettings.getInstance ().getPlayerMesh ();
        _playerNumberMesh = GameSettings.getInstance ().getPlayerNumbers ();
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
        
        _wobbleAngle = new Vector2 (Vector2.Zero);
        _wobbleDirection = new Vector2 (_UP, _DOWN);
        
        _myGame = new TroubleGame ();
        _myGame.createGame ();

        _spotMesh = GameSettings.getInstance ().getSpotMesh ();
        _spots = _myGame.getField ().getSpots ();
        _tokenMesh = GameSettings.getInstance ().getTokenMesh ();
        _players = _myGame.getPlayers ();

        /* Let's do this early so that the resources are available */
        _diceMesh.getSound ();
        _tokenMesh.getSound ();
    }
    
    protected void update (final float delta)
    {
        _myGame.updateGame ();
        if (_myGame.isFinished ()) {
            _currentState = TroubleApplicationState.MAIN_MENU;
        }
        if (_myGame.playerChanged()) {
            _overlayVisibleTime = 0.0f;
            _overlayAlpha = _OverlayMaxAlphas[_MAX];
        } else {
            _overlayVisibleTime += delta;
        }
        if (_myGame.tokenStartedMoving ()) {
            _tokenMesh.getSound ().play ();
        }
    }
    
    protected void render (final GL11 gl, final float delta)
    {
        calculateWobbleAngles (delta);
        
        renderOverlay (gl);
        
        renderField (gl);
        renderTokens (gl);
        
        renderAnnouncement (gl, delta);

        gl.glEnable (GL11.GL_TEXTURE_2D);
        renderDice (gl);
        gl.glDisable (GL11.GL_TEXTURE_2D);
        
        gl.glDisable (GL11.GL_CULL_FACE);
        gl.glDisable (GL11.GL_DEPTH_TEST);
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
    
    private void renderOverlay (final GL11 gl)
    {
        gl.glPushMatrix ();
        gl.glTranslatef (_overlayPosition.x, _overlayPosition.y, _overlayPosition.z);
        gl.glRotatef (_overlayAngle.x, 0.0f, 1.0f, 0.0f);
        gl.glRotatef (_overlayAngle.y + _overlayAdditionalAngle, 1.0f, 0.0f, 0.0f);
        final Color currentColor = _backArrowMesh.getColor ();
        gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
        _backArrowMesh.getMesh ().render ();
        gl.glPopMatrix ();
        if (_DEBUG) {
            Gdx.gl11.glPolygonMode (GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            gl.glPushMatrix ();
            _backArrowMesh.getBBMesh ().render (GL11.GL_TRIANGLES);
            gl.glPopMatrix ();
            Gdx.gl11.glPolygonMode (GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }
        
        if (_showOverlay && (_overlayAdditionalAngle < _OverlayMaxAngles[_MAX])) {
            _overlayAdditionalAngle += 3 * _RotationAngleIncrease;
            if (_overlayAdditionalAngle > _OverlayMaxAngles[_MAX]) {
                _overlayAdditionalAngle = _OverlayMaxAngles[_MAX];
            }
            calculateOverlayBoundingBox ();
        } else if (!_showOverlay && (_overlayAdditionalAngle > _OverlayMaxAngles[_MIN])) {
            _overlayAdditionalAngle -= 3 * _RotationAngleIncrease;
            if (_overlayAdditionalAngle < _OverlayMaxAngles[_MIN]) {
                _overlayAdditionalAngle = _OverlayMaxAngles[_MIN];
            }
            calculateOverlayBoundingBox ();
        }
    }
    
    private void renderAnnouncement (final GL11 gl, final float delta)
    {
        final Color currentColor = _myGame.getActivePlayer().getColor ();
        
        subRenderOverlay (gl, _playerMesh, currentColor);
        subRenderOverlay (gl, _playerNumberMesh.get (_myGame.getActivePlayer().getNumber ()), currentColor);
        
        if (_overlayVisibleTime > _OverlayMaxVisibleTime) {
            if (_overlayAlpha > _OverlayMaxAlphas[_MIN]) {
                _overlayAlpha -= _AlphaValueIncrease * delta;
            }
        }
    }
    
    private void subRenderOverlay (GL11 gl, GameMesh mesh, Color color)
    {
        gl.glPushMatrix ();
        gl.glTranslatef (_overlayPosition.x, _overlayPosition.y, _overlayPosition.z);
        gl.glRotatef (_overlayAngle.x, 0.0f, 1.0f, 0.0f);
        gl.glRotatef (_overlayAngle.y - _OverlayMaxAngles[_MAX], 1.0f, 0.0f, 0.0f);
        gl.glColor4f (color.r, color.g, color.b, _overlayAlpha);
        gl.glEnable (GL11.GL_BLEND);
        gl.glDepthMask (false);
        if (_overlayAlpha == _OverlayMaxAlphas[_MAX]) {
            gl.glBlendFunc (GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            gl.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        }
        mesh.getMesh ().render ();
        gl.glDepthMask (true);
        gl.glDisable (GL11.GL_BLEND);
        gl.glPopMatrix ();
    }

    protected void setLighting (final GL11 gl) {
        final float[] position1 = {10.0f, 5.0f, 1.0f, 1.0f};
        final float[] ambient1 = {1.0f, 1.0f, 1.0f, 1.0f};
        final float[] diffuse1 = {1.0f, 1.0f, 1.0f, 1.0f};
        final float[] specular1 = {1.0f, 1.0f, 1.0f, 1.0f};

        gl.glLightfv (GL11.GL_LIGHT0, GL11.GL_AMBIENT, ambient1, 0);
        gl.glLightfv (GL11.GL_LIGHT0, GL11.GL_DIFFUSE, diffuse1, 0);
        gl.glLightfv (GL11.GL_LIGHT0, GL11.GL_SPECULAR, specular1, 0);
        gl.glLightfv (GL11.GL_LIGHT0, GL11.GL_POSITION, position1, 0);

        gl.glEnable (GL11.GL_COLOR_MATERIAL);

        gl.glEnable (GL11.GL_LIGHTING);
        gl.glEnable (GL11.GL_LIGHT0);

    }

    private void renderDice (final GL11 gl)
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
        _diceMesh.getMesh ().render ();
        gl.glPopMatrix ();
        
        _diceMesh.transformBoundingBox (transform);
    }

    private void renderField (final GL11 gl)
    {
        if (_myGame != null) {
            _spot = _spots.iterator ();
            while (_spot.hasNext ()) {
                final Spot currentSpot = _spot.next ();
                final Vector3 currentPosition = new Vector3 (currentSpot.getPosition ());
                gl.glPushMatrix ();
                final Color currentColor = currentSpot.getColor ();
                if ((currentSpot.getPotentialToken () != null) ||
                    ((currentSpot.getCurrentToken () != null) &&
                     (currentSpot.getCurrentToken ().isSelected () &&
                      currentSpot.getCurrentToken ().isMoving ()))) {
                    currentPosition.y += _SelectedYOffset;
                    final float[] matEmission = {currentColor.r == 1.0f ? currentColor.r : 0.3f,
                            currentColor.g == 1.0f ? currentColor.g : 0.3f,
                            currentColor.b == 1.0f ? currentColor.b : 0.3f,
                            1.0f};
                    gl.glMaterialfv (FrontAndOrBack, GL11.GL_EMISSION, matEmission, 0);
                    gl.glRotatef (_wobbleAngle.x, 1.0f, 0.0f, 0.0f);
                    gl.glRotatef (_wobbleAngle.y, 0.0f, 0.0f, 1.0f);
                }
                gl.glTranslatef (currentPosition.x,
                                 currentPosition.y,
                                 currentPosition.z);
                gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
                _spotMesh.getMesh ().render ();
                gl.glPopMatrix ();
                gl.glMaterialfv (FrontAndOrBack, GL11.GL_EMISSION, NoMat, 0);
            }
        }
    }

    private void renderTokens (final GL11 gl)
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
                        final Vector3 currentPosition = new Vector3 (currentSpot.getPosition ());
                        if (currentToken.isSelected ()) {
                            currentPosition.y += _SelectedYOffset;
                        }
                        gl.glTranslatef (currentPosition.x,
                                         currentPosition.y,
                                         currentPosition.z);
                    } else {
                        final Vector3 currentPosition = currentToken.getCurrentMovePosition ();
                        currentPosition.y += _SelectedYOffset;
                        gl.glTranslatef (currentPosition.x,
                                         currentPosition.y,
                                         currentPosition.z);
                    }
                    final Color currentColor = currentPlayer.getColor ();
                    gl.glColor4f (currentColor.r, currentColor.g, currentColor.b, currentColor.a);
                    
                    if (currentToken.isSelected ()) {
                        gl.glRotatef (_wobbleAngle.x, 1.0f, 0.0f, 0.0f);
                        gl.glRotatef (_wobbleAngle.y, 0.0f, 0.0f, 1.0f);
                        final float[] matEmission = {currentColor.r == 1.0f ? currentColor.r : 0.3f,
                                                      currentColor.g == 1.0f ? currentColor.g : 0.3f,
                                                      currentColor.b == 1.0f ? currentColor.b : 0.3f,
                                                      1.0f};
                        gl.glMaterialfv (FrontAndOrBack, GL11.GL_EMISSION, matEmission, 0);
                    }
                    
                    _tokenMesh.getMesh ().render ();
                    gl.glPopMatrix ();
                    gl.glMaterialfv (FrontAndOrBack, GL11.GL_EMISSION, NoMat, 0);
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

    private void calculateWobbleAngles (final float delta)
    {
        if (_wobbleDirection.x == _UP) {
            if (_wobbleAngle.x < _WobbleMaxAngles[_MAX]) {
                _wobbleAngle.x += 2 * delta;
            } else {
                _wobbleDirection.x = _DOWN;
            }
        } else {
            if (_wobbleAngle.x > _WobbleMaxAngles[_MIN]) {
                _wobbleAngle.x -= 2 * delta;
            } else {
                _wobbleDirection.x = _UP;
            }
        }
        if (_wobbleDirection.y == _UP) {
            if (_wobbleAngle.y < _WobbleMaxAngles[_MAX]) {
                _wobbleAngle.y += 2 * delta;
            } else {
                _wobbleDirection.y = _DOWN;
            }
        } else {
            if (_wobbleAngle.y > _WobbleMaxAngles[_MIN]) {
                _wobbleAngle.y -= 2 * delta;
            } else {
                _wobbleDirection.y = _UP;
            }
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
                        Gdx.gl11.glPolygonMode (GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                        _filling = false;
                    } else {
                        Gdx.app.log (TAG, "keyDown() - Filling");
                        Gdx.gl11.glPolygonMode (GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
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
                            if (_myGame.canRollDice ()) {
                                _diceMesh.getSound ().play ();
                                _myGame.rollDice ();
                            }
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
