/**
 * @file: com.innovail.trouble.screen - LoadingScreen.java
 * @date: Jun 06, 2013
 * @author: bweber
 */
package com.innovail.trouble.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.core.TroubleApplicationState;
import com.innovail.trouble.graphics.GameFont;
import com.innovail.trouble.graphics.GameFont.FontType;
import com.innovail.trouble.graphics.GameMesh;
import com.innovail.trouble.uicomponent.BackgroundImage;
import com.innovail.trouble.utils.GameInputAdapter;

/**
 * 
 */
public class LoadingScreen extends TroubleScreen
{
    private static final String TAG = "LoadingScreen";
    private static final String AppPartName = TroubleApplicationState.LOADING;

    private static float _displayDelta = 0.0f;
    private static final float _DotMaxDelta = 0.4f;

    private final SpriteBatch _spriteBatch;
    private final BackgroundImage _backgroundImage;

    private final GameMesh _logo;

    private final Matrix4 _viewMatrix;
    private final Matrix4 _transformMatrix;

    private static final Vector3 _MenuOffset = new Vector3 (0.0f, 0.0f, -1.0f);
    private static Vector3 _Dot1Offset;
    private static Vector3 _Dot2Offset;

    private final StillModel _loadingAnouncement;
    private final StillModel _loadingDot1;
    private final StillModel _loadingDot2;

    public LoadingScreen ()
    {
        super ();

        Gdx.app.log (TAG, "LoadingScreen()");

        _loadingAnouncement = ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel ("Loading.");
        _loadingDot1 = ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (".");
        _loadingDot2 = ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (".");

        final BoundingBox laBB = new BoundingBox ();
        _loadingAnouncement.getBoundingBox (laBB);
        final Vector3 laDim = laBB.getDimensions ();
        _Dot1Offset = new Vector3 (_MenuOffset.x + laDim.x / 2 + 0.175f, _MenuOffset.y, _MenuOffset.z);
        _Dot2Offset = new Vector3 (_MenuOffset.x + laDim.x / 2 + (2 * 0.2f), _MenuOffset.y, _MenuOffset.z);

        _spriteBatch = new SpriteBatch ();
        _backgroundImage = ApplicationSettings.getInstance ().getBackgroundImage (TroubleApplicationState.MAIN_MENU);

        _logo = ApplicationSettings.getInstance ().getLogo ();
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
                return super.touchUp (x, y, pointer, button);
            }
        });
    }

    @Override
    protected void render (final GL11 gl, final float delta)
    {
        _displayDelta += delta;

        Gdx.gl11.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_FILL);

        renderLogo (gl);
        renderAnouncement (gl);

        gl.glDisable (GL10.GL_CULL_FACE);
        gl.glDisable (GL10.GL_DEPTH_TEST);
    }

    private void renderAnouncement (final GL11 gl)
    {
        if (_loadingAnouncement != null) {
            final float yLocation = 0.0f;
            gl.glPushMatrix ();
            gl.glTranslatef (_MenuOffset.x, yLocation + _MenuOffset.y, _MenuOffset.z);
            _loadingAnouncement.render ();
            gl.glPopMatrix ();
            if (_displayDelta > _DotMaxDelta) {
                gl.glPushMatrix ();
                gl.glTranslatef (_Dot1Offset.x, yLocation + _Dot1Offset.y, _Dot1Offset.z);
                _loadingDot1.render ();
                gl.glPopMatrix ();
            }
            if (_displayDelta > (2 * _DotMaxDelta)) {
                gl.glPushMatrix ();
                gl.glTranslatef (_Dot2Offset.x, yLocation + _Dot2Offset.y, _Dot2Offset.z);
                _loadingDot2.render ();
                gl.glPopMatrix ();
            }
            if (_displayDelta > (3 * _DotMaxDelta)) {
                _displayDelta = 0.0f;
            }
        }
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

    @Override
    public void setOwnState ()
    {
        _currentState = AppPartName;
    }

    @Override
    protected void update (final float delta)
    {}
}
