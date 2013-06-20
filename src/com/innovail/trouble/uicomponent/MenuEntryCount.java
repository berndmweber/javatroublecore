/**
 * @file: com.innovail.trouble.uicomponent - MenuEntryCount.java
 * @date: Jun 19, 2013
 * @author: berndmicweber
 */
package com.innovail.trouble.uicomponent;

import java.util.HashMap;
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
public class MenuEntryCount extends MenuEntry
{
    private static final String TAG = "MenuEntryCount";

    private static final boolean _DEBUG = false;

    private static final float ManipulatorGap = 0.25f;

    private static final String SmallerKey = "-";
    private static final String LargerKey = "+";

    private int _minCount = 0;
    private int _maxCount = 0;
    private int _currentCount = 1;
    private Map <String, Vector3> _manipPosition;

    private Map <String, GameMesh> _manipulators;

    public MenuEntryCount (final String [] name)
    {
        super (name);
        initialize ();
    }

    public MenuEntryCount (final String [] name, final Parameters params)
    {
        super (name);
        if (_DEBUG) {
            Gdx.app.log (TAG, "Creating Count Entry");
        }
        initialize ();
        processParams (params);
    }

    public int getCurrentCount ()
    {
        return _currentCount;
    }

    public int getMaxCount ()
    {
        return _maxCount;
    }

    public int getMinCount ()
    {
        return _minCount;
    }

    @Override
    public boolean handleIntersect (Ray touchRay)
    {
        if (Intersector.intersectRayBoundsFast (touchRay, _manipulators.get (SmallerKey).getBoundingBox ())) {
            if (_DEBUG) {
                Gdx.app.log (TAG, "Decreasing count");
            }
            setCurrentCount (getCurrentCount() - 1);
        } else if (Intersector.intersectRayBoundsFast (touchRay, _manipulators.get (LargerKey).getBoundingBox ())) {
            if (_DEBUG) {
                Gdx.app.log (TAG, "Increasing count");
            }
            setCurrentCount (getCurrentCount() + 1);
        }
        return false;
    }

    private void initialize ()
    {
        _type = TypeEnum.COUNT;
        _manipulators = new HashMap <String, GameMesh> ();
        _manipulators.put (SmallerKey, new GameMesh (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (SmallerKey)));
        _manipulators.put (LargerKey, new GameMesh (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (LargerKey)));
    }

    @Override
    public void processParams (final Parameters params)
    {
        setMinMaxCount (params.getIntParameter ("mincount"), params.getIntParameter ("maxcount"));
    }

    @Override
    public void render (final GL11 gl, final Vector3 menuOffset, final Color color)
    {
        render (gl, menuOffset, this, _entryPosition, color);
        if ((_manipulators != null) && !_manipulators.isEmpty ()) {
            if (_minCount != _maxCount) {
                render (gl, menuOffset, _manipulators.get (SmallerKey), _manipPosition.get (SmallerKey), color);
            }
            render (gl, menuOffset, _manipulators.get (Integer.toString (_currentCount)), _manipPosition.get (Integer.toString (_currentCount)), color);
            if (_minCount != _maxCount) {
                render (gl, menuOffset, _manipulators.get (LargerKey), _manipPosition.get (LargerKey), color);
            }
        }
    }

    public void render (final GL11 gl, final Vector3 menuOffset,
                        final GameMesh mesh, final Vector3 position, final Color color)
    {
        gl.glPushMatrix ();
        gl.glTranslatef (position.x + menuOffset.x, position.y + menuOffset.y, position.z + menuOffset.z);
        gl.glColor4f (color.r, color.g, color.b, color.a);
        mesh.getMesh ().render ();
        gl.glPopMatrix ();

        final Matrix4 transform = new Matrix4 ();
        final Matrix4 tmp = new Matrix4 ();
        transform.setToTranslation (position.x + menuOffset.x, position.y + menuOffset.y, position.z + menuOffset.z);
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

    public void setCurrentCount (int count)
    {
        if (count < _minCount) {
            count = _minCount;
        } else if (count > _maxCount) {
            count = _maxCount;
        }
        _currentCount = count;
    }

    public void setMaxCount (final int maxCount)
    {
        _maxCount = maxCount;
        if (_manipulators != null) {
            for (int i = _minCount; i <= _maxCount; i++) {
                if (!_manipulators.containsKey (Integer.toString (i))) {
                    _manipulators.put (Integer.toString (i), new GameMesh (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (Integer.toString (i))));
                }
            }
        }
    }

    public void setMinCount (final int minCount)
    {
        _minCount = minCount;
        _currentCount = _minCount;
    }

    public void setMinMaxCount (int minCount, final int maxCount)
    {
        if (minCount > maxCount) {
            minCount = maxCount;
        }
        setMinCount (minCount);
        setMaxCount (maxCount);
    }

    @Override
    public void setOffset (final Vector3 offsetPosition)
    {
        BoundingBox tBB = this.getBoundingBox ();
        Vector3 tDim = tBB.getDimensions ();
        _entryPosition = new Vector3 (offsetPosition.x + tDim.x / 2, offsetPosition.y, offsetPosition.z);
        if (_manipPosition == null) {
            _manipPosition = new HashMap <String, Vector3> ();
        }
        tBB = _manipulators.get (LargerKey).getBoundingBox ();
        tDim = tBB.getDimensions ();
        float totalX = -tDim.x;
        _manipPosition.put (LargerKey, new Vector3 (-offsetPosition.x + totalX, offsetPosition.y, offsetPosition.z));

        /* TODO: First evaluate the max size, then assign positions. */
        float maxCountX = 0.0f;
        for (int i = _minCount; i <= _maxCount; i++) {
            final String currentCount = Integer.toString (i);
            tBB = _manipulators.get (currentCount).getBoundingBox ();
            tDim = tBB.getDimensions ();
            final float tempX = -ManipulatorGap - tDim.x;
            if (tempX < maxCountX) {
                maxCountX = tempX;
            }
            _manipPosition.put (currentCount, new Vector3 (-offsetPosition.x + totalX + tempX, offsetPosition.y, offsetPosition.z));
        }
        totalX += maxCountX;

        tBB = _manipulators.get (SmallerKey).getBoundingBox ();
        tDim = tBB.getDimensions ();
        totalX += -ManipulatorGap - tDim.x;
        _manipPosition.put (SmallerKey, new Vector3 (-offsetPosition.x + totalX, offsetPosition.y, offsetPosition.z));
    }
}
