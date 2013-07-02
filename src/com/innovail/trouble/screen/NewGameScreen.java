/**
 * @file: com.innovail.trouble.screen -NewGameScreen.java
 * @date: Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.screen;

import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.core.GameSettings;
import com.innovail.trouble.core.TroubleApplicationState;
import com.innovail.trouble.graphics.GameColor;
import com.innovail.trouble.graphics.GameFont;
import com.innovail.trouble.graphics.GameFont.FontType;
import com.innovail.trouble.graphics.GameMesh;
import com.innovail.trouble.uicomponent.BackgroundImage;
import com.innovail.trouble.uicomponent.MenuEntry;
import com.innovail.trouble.uicomponent.MenuEntryColorSelector;
import com.innovail.trouble.uicomponent.MenuEntryCount;
import com.innovail.trouble.uicomponent.MenuEntryTextSelector;
import com.innovail.trouble.utils.GameInputAdapter;

/**
 * 
 */
public class NewGameScreen extends TroubleScreen
{
    private static final String   TAG         = "NewGameScreen";
    private static final String   AppPartName = TroubleApplicationState.NEW_GAME;

    private final BitmapFont      _menuFont;
    private final SpriteBatch     _spriteBatch;
    private final BackgroundImage _backgroundImage;

    private final GameMesh        _logo;
    private final List <GameMesh> _menuEntriesList;

    private final Matrix4         _viewMatrix;
    private final Matrix4         _transformMatrix;

    private static final Vector3  _MenuOffset = new Vector3 (-6.0f, 0.0f, -1.5f);

    StillModel                    text;

    public NewGameScreen ()
    {
        super ();

        Gdx.app.log (TAG, "NewGameScreen()");

        _spriteBatch = new SpriteBatch ();
        _menuFont = ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.BITMAP)).getBitmapFont ();
        _backgroundImage = ApplicationSettings.getInstance ().getBackgroundImage (TroubleApplicationState.MAIN_MENU);

        _logo = ApplicationSettings.getInstance ().getMenuEntries (TroubleApplicationState.MAIN_MENU).get (AppPartName);
        _menuEntriesList = ApplicationSettings.getInstance ().getMenuEntryList (AppPartName);

        final Iterator <GameMesh> currentMesh = _menuEntriesList.iterator ();
        while (currentMesh.hasNext ()) {
            final MenuEntry currentMEMesh = (MenuEntry) currentMesh.next ();
            if (currentMEMesh.getName ().equals ("field")) {
                for (final String field : GameSettings.getInstance ().getFieldList ()) {
                    ((MenuEntryTextSelector) currentMEMesh).addEntry (field);
                }
                ((MenuEntryTextSelector) currentMEMesh).setCurrentSelection (0);
            }
            if (currentMEMesh.getName ().equals ("playerColor")) {
                final GameColor playerColor = (GameColor) GameSettings.getInstance ().getPlayerColor (0);
                ((MenuEntryColorSelector) currentMEMesh).setCurrentSelection (GameColor.getColorName (playerColor));
            }
            if (currentMEMesh.getName ().equals ("players")) {
                final int currentPlayers = GameSettings.getInstance ().getNumberOfPlayers ();
                ((MenuEntryCount) currentMEMesh).setCurrentCount (currentPlayers);
                final int minPlayers = GameSettings.getInstance ().getMinimumNumberOfPlayers ();
                ((MenuEntryCount) currentMEMesh).setMinCount (minPlayers);
                final int maxPlayers = GameSettings.getInstance ().getMaximumNumberOfPlayers ();
                ((MenuEntryCount) currentMEMesh).setMaxCount (maxPlayers);
            }
            currentMEMesh.setOffset (_MenuOffset);
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
                        Gdx.gl11.glPolygonMode (GL10.GL_FRONT_AND_BACK,
                                                GL10.GL_LINE);
                        _filling = false;
                    } else {
                        Gdx.app.log (TAG, "keyDown() - Filling");
                        Gdx.gl11.glPolygonMode (GL10.GL_FRONT_AND_BACK,
                                                GL10.GL_FILL);
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
                if (!_isDragged || (_dragEvents < MIN_NUMBER_OF_DRAGS)) {
                    final Iterator <GameMesh> currentMesh = _menuEntriesList.iterator ();
                    int j = 0;
                    final Ray touchRay = _camera.getPickRay (x,
                                                             y,
                                                             0,
                                                             0,
                                                             Gdx.graphics.getWidth (),
                                                             Gdx.graphics.getHeight ());
                    if (_DEBUG) {
                        Gdx.app.log (TAG,
                                     "Touch position - x: " + x + " - y: " + y);
                        Gdx.app.log (TAG, "Touch ray - " + touchRay.toString ());
                    }
                    while (currentMesh.hasNext ()) {
                        final MenuEntry currentEntry = (MenuEntry) currentMesh.next ();
                        if (touchRay != null) {
                            if (_DEBUG) {
                                Gdx.app.log (TAG,
                                             "currentEntry BB - " + currentEntry.getBoundingBox ().toString ());
                            }
                            if (currentEntry.handleIntersect (touchRay)) {
                                if (currentEntry.getName ().equals ("playerColor")) {
                                    final Color playerColor = GameColor.getColor (((MenuEntryColorSelector) currentEntry).getSelected ());
                                    GameSettings.getInstance ().setPlayerColor (0,
                                                                                playerColor);
                                } else if (currentEntry.getName ().equals ("players")) {
                                    final int players = ((MenuEntryCount) currentEntry).getCurrentCount ();
                                    GameSettings.getInstance ().setNumberOfPlayers (players);
                                } else if (currentEntry.getName ().equals ("field")) {
                                    /* TODO: Handle field selection */
                                } else {
                                    _currentState = currentEntry.getName ();
                                }
                                Gdx.app.log (TAG,
                                             "Mesh " + j + " touched -> " + _currentState);
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
        _spriteBatch.draw ((Texture) _backgroundImage.getImageObject (), 0, 0,
                           width, height, 0, 0, _backgroundImage.getWidth (),
                           _backgroundImage.getHeight (), false, false);
        _spriteBatch.enableBlending ();
        _spriteBatch.setBlendFunction (GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        final String text = ApplicationSettings.getInstance ().getCopyRightNotice ();
        final float textWidth = _menuFont.getBounds (text).width;
        final float textHeight = _menuFont.getBounds (text).height;
        _menuFont.draw (_spriteBatch, text,
                        (Gdx.graphics.getWidth () / 2) - (textWidth / 2),
                        textHeight + 5);
        _spriteBatch.end ();
    }

    private void renderLogo (final GL11 gl)
    {
        final Color currentColor = ApplicationSettings.getInstance ().getLogo ().getColor ();

        final int frontAndOrBack = GL10.GL_FRONT;
        final float [] matSpecular = { 1.0f, 1.0f, 1.0f, 1.0f };
        final float [] matShininess = { 7.0f, 0.0f, 0.0f, 0.0f };

        gl.glPushMatrix ();
        gl.glTranslatef (0.0f, 1.0f, 0.3f);
        gl.glColor4f (currentColor.r, currentColor.g, currentColor.b,
                      currentColor.a);
        gl.glMaterialfv (frontAndOrBack, GL10.GL_SPECULAR, matSpecular, 0);
        gl.glMaterialfv (frontAndOrBack, GL10.GL_SHININESS, matShininess, 0);
        _logo.getMesh ().render ();
        gl.glPopMatrix ();
    }

    private void renderMenu (final GL11 gl)
    {
        final Color currentColor = ApplicationSettings.getInstance ().getLogo ().getColor ();

        final Iterator <GameMesh> currentMesh = _menuEntriesList.iterator ();
        final Vector3 menuOffset = new Vector3 (0.0f, 0.0f, 0.0f);
        while (currentMesh.hasNext ()) {
            final MenuEntry currentMEMesh = (MenuEntry) currentMesh.next ();
            if (!currentMesh.hasNext ()) {
                menuOffset.y -= 0.7f;
            }
            currentMEMesh.render (gl, menuOffset, currentColor);
            menuOffset.y -= 0.7f;
        }
    }

    @Override
    protected void setLighting (final GL11 gl)
    {
        setLighting (gl,
                     ApplicationSettings.getInstance ().getLogo ().getColor ());
    }

    @Override
    public void setOwnState ()
    {
        _currentState = TroubleApplicationState.NEW_GAME;
    }

    @Override
    protected void update (final float delta)
    {
        /* Nothing to do here. */
    }
}
