/**
 * @file:   com.innovail.trouble.core - MenuScreen.java
 * @date:   Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.screen;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.core.TroubleApplicationState;
import com.innovail.trouble.graphics.FontUtil;
import com.innovail.trouble.graphics.GameFont;
import com.innovail.trouble.graphics.GameMesh;
import com.innovail.trouble.graphics.GameFont.FontType;
import com.innovail.trouble.uicomponent.BackgroundImage;
import com.innovail.trouble.utils.FontObjLoader;
import com.innovail.trouble.utils.GameInputAdapter;

/**
 * 
 */
public class NewGameScreen extends TroubleScreen {
    private static final String TAG = "NewGameScreen";
    private static final String AppPartName = TroubleApplicationState.NEW_GAME;
    
    private final BitmapFont _menuFont;
    private final SpriteBatch _spriteBatch;
    private final BackgroundImage _backgroundImage;

    private final GameMesh _logo;
    private final List <GameMesh> _menuEntriesList;
    private final GameMesh _selectionArrow;
    
    private final Matrix4 _viewMatrix;
    private final Matrix4 _transformMatrix;
    
    //private static final Vector3 _MenuOffset = new Vector3 (-2.0f, 0.5f, 0.0f);
    private static final Vector3 _MenuOffset = new Vector3 (0.0f, 0.0f, -1.0f);
    
    Mesh text;
    
    public NewGameScreen ()
    {
        super ();
        
        Gdx.app.log (TAG, "NewGameScreen()");
        
        try {
            FileHandle objFile = null;
            Map <Character, Mesh> font = null;
            Map <Character, float[]> fontV = null;
            objFile = Gdx.files.external ("fontmeshes");
            InputStream is = null;
            try {
                is = objFile.read ();
            } catch (Exception e) {
                FileHandle inFile = Gdx.files.internal ("yusuke_all_single_characters.obj");
                final InputStream in = inFile.read ();
                font = FontObjLoader.loadObj (in);
                in.close ();
                
                final OutputStream os = objFile.write (false);
                final ObjectOutput oo = new ObjectOutputStream (os);
                fontV = new HashMap <Character, float[]> ();
                Iterator <Character> it = font.keySet ().iterator ();
                while (it.hasNext ()) {
                    Character c = it.next ();
                    Mesh m = font.get (c);
                    int vertLen = m.getNumVertices () * 6;
                    float [] vertices = new float [vertLen];
                    m.getVertices (vertices);
                    fontV.put (c, vertices);
                }
                oo.writeObject ((HashMap<Character, float[]>) fontV);
                oo.flush ();
                os.close ();
                font = null;
                fontV = null;
                
                is = objFile.read ();
            }
            
            ObjectInputStream oi = new ObjectInputStream (is);
            fontV = (HashMap <Character, float[]>) oi.readObject ();
            is.close ();
            
            font = new HashMap <Character, Mesh> ();
            List <VertexAttribute> attributes = new ArrayList <VertexAttribute> ();
            attributes.add (new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
            attributes.add (new VertexAttribute(Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE));
            Iterator <Character> it = fontV.keySet ().iterator ();
            while (it.hasNext ()) {
                Character c = it.next ();
                float [] vertices = fontV.get (c);
                Mesh m = new Mesh (true, vertices.length / 2, 0, attributes.toArray (new VertexAttribute [attributes.size ()]));
                m.setVertices (vertices);
                font.put (c, m);
            }
            FontUtil.setFontMap (font);
        } catch (Exception ex) {
            Gdx.app.log (TAG, ex.getMessage ());
        }
        text = FontUtil.createMesh ("Crazy stuff happening!");
        
        _currentState = TroubleApplicationState.NEW_GAME;
        
        _spriteBatch = new SpriteBatch ();
        _menuFont = ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString(FontType.BITMAP)).getBitmapFont ();
        _backgroundImage = ApplicationSettings.getInstance ().getBackgroundImage (TroubleApplicationState.MAIN_MENU);
        
        _logo = ApplicationSettings.getInstance ().getMenuEntries (TroubleApplicationState.MAIN_MENU).get (AppPartName);
        _menuEntriesList = ApplicationSettings.getInstance ().getMenuEntryList (AppPartName);
       
        _selectionArrow = ApplicationSettings.getInstance ().getApplicationAsset ("selectionArrow");
        _viewMatrix = new Matrix4 ();
        _transformMatrix = new Matrix4 ();
        
        final float aspectRatio = (float) Gdx.graphics.getWidth () /
                                   (float) Gdx.graphics.getHeight ();
        _camera = new PerspectiveCamera (100, 2f * aspectRatio, 2f);
        _camera.position.set (0, 0, 2);
        _camera.direction.set (0, 0, -4).sub (_camera.position).nor ();

        showFrontAndBack ();
    }
    
    protected void update (final float delta)
    {
        /* Nothing to do here. */
    }
    
    protected void render (final GL11 gl, final float delta)
    {
        Gdx.gl11.glPolygonMode (GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        renderLogo (gl);
        renderMenu (gl);

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
        _spriteBatch.enableBlending ();
        _spriteBatch.setBlendFunction (GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        final String text = ApplicationSettings.getInstance ().getCopyRightNotice ();
        final float textWidth = _menuFont.getBounds (text).width;
        final float textHeight = _menuFont.getBounds (text).height;
        _menuFont.draw (_spriteBatch, text,
                        Gdx.graphics.getWidth () / 2 - textWidth / 2,
                        textHeight + 5);
        _spriteBatch.end ();
    }
    
    protected void setLighting (final GL11 gl)
    {
        final Color lightColor = Color.BLUE;
        final float[] specular0 = {lightColor.r, lightColor.g, lightColor.b, lightColor.a};
        final float[] position1 = {-2.0f, -2.0f, 1.0f, 0.0f};
        final float[] ambient1 = {1.0f, 1.0f, 1.0f, 1.0f};
        final float[] diffuse1 = {1.0f, 1.0f, 1.0f, 1.0f};
        final float[] specular1 = {1.0f, 1.0f, 1.0f, 1.0f};
        
        gl.glEnable (GL11.GL_LIGHTING);
        
        gl.glEnable (GL11.GL_LIGHT0);
        gl.glLightfv (GL11.GL_LIGHT0, GL11.GL_SPECULAR, specular0, 0);
        
        gl.glEnable (GL11.GL_LIGHT1);
        gl.glLightfv (GL11.GL_LIGHT1, GL11.GL_AMBIENT, ambient1, 0);
        gl.glLightfv (GL11.GL_LIGHT1, GL11.GL_DIFFUSE, diffuse1, 0);
        gl.glLightfv (GL11.GL_LIGHT1, GL11.GL_SPECULAR, specular1, 0);
        gl.glLightfv (GL11.GL_LIGHT1, GL11.GL_POSITION, position1, 0);
        
        gl.glEnable (GL11.GL_COLOR_MATERIAL);
        gl.glEnable (GL11.GL_BLEND);
    }

    private void renderLogo (final GL11 gl)
    {
        final int frontAndOrBack = GL11.GL_FRONT;
        final float[] matSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
        final float[] matShininess = {7.0f};
        
        gl.glPushMatrix ();
        gl.glTranslatef (0.0f, 1.0f, 0.3f);
        gl.glMaterialfv (frontAndOrBack, GL11.GL_SPECULAR, matSpecular, 0);
        gl.glMaterialfv (frontAndOrBack, GL11.GL_SHININESS, matShininess, 0);
        _logo.getMesh ().render ();
        gl.glPopMatrix ();
    }

    private void renderMenu (final GL11 gl)
    {
        //final Iterator <GameMesh> currentMesh = _menuEntriesList.iterator ();
        if (text != null) {
        //final Iterator <Mesh> currentMesh = font.values ().iterator ();
        float yLocation = 0.0f;
        int i = 0;
        //while (currentMesh.hasNext ()) {
            //final Mesh thisMesh = currentMesh.next ();
            gl.glPushMatrix ();
            gl.glTranslatef (_MenuOffset.x, yLocation + _MenuOffset.y, _MenuOffset.z);
            //gl.glRotatef (_yRotationAngle[i], 0.0f, 0.0f, 1.0f);
            text.render (GL11.GL_TRIANGLES);
            gl.glPopMatrix ();
            
            /*final Matrix4 transform = new Matrix4();
            final Matrix4 tmp = new Matrix4();
            transform.setToTranslation (0.0f, yLocation, 0.0f);
            //tmp.setToRotation (0.0f, 0.0f, 1.0f, _yRotationAngle[i]);
            //transform.mul(tmp);
            thisMesh.transformBoundingBox (transform);
            
            if (_DEBUG) {
                Gdx.gl11.glPolygonMode (GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                gl.glPushMatrix ();
                thisMesh.getBBMesh ().render (GL11.GL_TRIANGLES);
                gl.glPopMatrix ();
                Gdx.gl11.glPolygonMode (GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }
            
            yLocation -= 0.7f;*/
        //}
        }
    }

    private void renderAsset (String name) {
        
    }
    
    public void createInputProcessor ()
    {
        Gdx.input.setInputProcessor (new GameInputAdapter() {

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
                default:
                    rv = false;
                }
                return rv;
            }

            /* (non-Javadoc)
             * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
             */
            @Override
            public boolean touchUp (final int x, final int y, final int pointer, final int button) {
                if (!_isDragged || (_dragEvents < MIN_NUMBER_OF_DRAGS)) {
                    /*final Iterator <GameMesh> currentMesh = _menuEntries.iterator ();
                    int j = 0;
                    final Ray touchRay = _camera.getPickRay (x, y, 0, 0, Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
                    if (_DEBUG) {
                        Gdx.app.log (TAG, "Touch position - x: " + x + " - y: " + y);
                        Gdx.app.log (TAG, "Touch ray - " + touchRay.toString ());
                    }
                    while (currentMesh.hasNext ()) {
                        final MenuEntryMesh currentEntry = (MenuEntryMesh)currentMesh.next ();
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
                    }*/
                }
                _currentState = TroubleApplicationState.GAME;
                return super.touchUp (x, y, pointer, button);
            }
        });
    }
}
