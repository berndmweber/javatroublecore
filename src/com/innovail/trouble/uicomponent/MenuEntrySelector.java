/**
 * @file: com.innovail.trouble.uicomponent - MenuEntrySelector.java
 * @date: Jun 20, 2013
 * @author: berndmicweber
 */
package com.innovail.trouble.uicomponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.graphics.GameFont;
import com.innovail.trouble.graphics.GameFont.FontType;
import com.innovail.trouble.graphics.GameMesh;
import com.innovail.trouble.utils.Parameters;

/**
 * 
 */
public abstract class MenuEntrySelector extends MenuEntry
{
    private static final String      TAG               = "MenuEntrySelector";

    protected static final boolean   _DEBUG            = false;

    protected static final float     ManipulatorGap    = 0.25f;

    protected static final String    LastKey           = "<";
    protected static final String    NextKey           = ">";

    protected List <String>          _selections;
    protected int                    _currentSelection = 0;
    protected Map <String, Vector3>  _manipPosition;

    protected Map <String, GameMesh> _manipulators;

    public MenuEntrySelector (final String [] name)
    {
        super (name);
        initialize ();
    }

    public MenuEntrySelector (final String [] name, final Parameters params)
    {
        super (name);
        if (_DEBUG) {
            Gdx.app.log (TAG, "Creating Selector Entry");
        }
        initialize ();
        processParams (params);
    }

    public int getCurrentSelection ()
    {
        return _currentSelection;
    }

    public String getSelected ()
    {
        return _selections.get (getCurrentSelection ());
    }

    @Override
    public boolean handleIntersect (final Ray touchRay)
    {
        if (_DEBUG) {
            Gdx.app.log (TAG, "Handling selection");
        }
        if (Intersector.intersectRayBoundsFast (touchRay,
                                                _manipulators.get (LastKey).getBoundingBox ())) {
            setCurrentSelection (getCurrentSelection () - 1);
            return true;
        } else if (Intersector.intersectRayBoundsFast (touchRay,
                                                       _manipulators.get (NextKey).getBoundingBox ())) {
            setCurrentSelection (getCurrentSelection () + 1);
            return true;
        }
        return false;
    }

    private void initialize ()
    {
        _manipulators = new HashMap <String, GameMesh> ();
        _manipulators.put (LastKey,
                           new GameMesh (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (LastKey)));
        _manipulators.put (NextKey,
                           new GameMesh (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (NextKey)));
    }

    @Override
    public void processParams (final Parameters params)
    {
        _selections = new ArrayList <String> ();
    }

    @Override
    public void render (final GL11 gl, final Vector3 menuOffset,
                        final Color color)
    {
        render (gl, menuOffset, this, _entryPosition, color);
        if ((_manipulators != null) && !_manipulators.isEmpty ()) {
            if (_selections.size () > 1) {
                render (gl, menuOffset, _manipulators.get (LastKey),
                        _manipPosition.get (LastKey), color);
            }
            render (gl, menuOffset,
                    _manipulators.get (_selections.get (_currentSelection)),
                    _manipPosition.get (_selections.get (_currentSelection)),
                    color);
            if (_selections.size () > 1) {
                render (gl, menuOffset, _manipulators.get (NextKey),
                        _manipPosition.get (NextKey), color);
            }
        }
    }

    public void render (final GL11 gl, final Vector3 menuOffset,
                        final GameMesh mesh, final Vector3 position,
                        final Color color)
    {
        gl.glPushMatrix ();
        gl.glTranslatef (position.x + menuOffset.x, position.y + menuOffset.y,
                         position.z + menuOffset.z);
        gl.glColor4f (color.r, color.g, color.b, color.a);
        mesh.getMesh ().render ();
        gl.glPopMatrix ();

        final Matrix4 transform = new Matrix4 ();
        final Matrix4 tmp = new Matrix4 ();
        transform.setToTranslation (position.x + menuOffset.x,
                                    position.y + menuOffset.y,
                                    position.z + menuOffset.z);
        transform.mul (tmp);
        mesh.transformBoundingBox (transform);

        if (_DEBUG) {
            Gdx.gl11.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_LINE);
            gl.glPushMatrix ();
            mesh.getBBMesh ().render (GL10.GL_TRIANGLES);
            gl.glPopMatrix ();
            Gdx.gl11.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_FILL);
        }
    }

    public void setCurrentSelection (int selection)
    {
        if (selection < 0) {
            selection = 0;
        } else if (selection >= _selections.size ()) {
            selection = _selections.size () - 1;
        }
        _currentSelection = selection;
        if (_DEBUG) {
            Gdx.app.log (TAG,
                         "Current Selection: " + String.valueOf (_currentSelection));
        }
    }

    @Override
    public void setOffset (final Vector3 offsetPosition)
    {
        BoundingBox tBB = this.getBoundingBox ();
        Vector3 tDim = tBB.getDimensions ();
        _entryPosition = new Vector3 (offsetPosition.x + (tDim.x / 2), offsetPosition.y, offsetPosition.z);
        if (_manipPosition == null) {
            _manipPosition = new HashMap <String, Vector3> ();
        }
        tBB = _manipulators.get (NextKey).getBoundingBox ();
        tDim = tBB.getDimensions ();
        float totalX = -tDim.x / 2;
        _manipPosition.put (NextKey,
                            new Vector3 (-offsetPosition.x + totalX, offsetPosition.y, offsetPosition.z));

        totalX -= ManipulatorGap;
        final int selectionsSize = _selections.size ();
        float maxSizeX = 0.0f;
        final float [] xSize = new float [selectionsSize];
        for (int i = 0; i < selectionsSize; i++) {
            final String currentSelection = _selections.get (i);
            tBB = _manipulators.get (currentSelection).getBoundingBox ();
            tDim = tBB.getDimensions ();
            xSize[i] = tDim.x;
            if (tDim.x > maxSizeX) {
                maxSizeX = tDim.x;
            }
        }
        for (int i = 0; i < selectionsSize; i++) {
            final String currentSelection = _selections.get (i);
            final float actualX = (maxSizeX / 2) + (xSize[i] / 2);
            _manipPosition.put (currentSelection,
                                new Vector3 ((-offsetPosition.x + totalX) - (actualX / 2), offsetPosition.y, offsetPosition.z));
        }
        totalX -= maxSizeX;

        tBB = _manipulators.get (LastKey).getBoundingBox ();
        tDim = tBB.getDimensions ();
        totalX -= ((ManipulatorGap / 2) + (tDim.x / 2));
        _manipPosition.put (LastKey,
                            new Vector3 (-offsetPosition.x + totalX, offsetPosition.y, offsetPosition.z));
    }

    protected void setSelection (final String entry)
    {
        if ((_selections != null) && !_selections.contains (entry)) {
            _selections.add (entry);
        }
    }
}
