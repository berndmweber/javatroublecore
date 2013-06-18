/**
 * @file: com.innovail.trouble.uicomponent - MenuEntryMesh.java
 * @date: May 8, 2012
 * @author: bweber
 */
package com.innovail.trouble.uicomponent;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.graphics.GameFont;
import com.innovail.trouble.graphics.GameFont.FontType;
import com.innovail.trouble.graphics.GameMesh;

/**
 * 
 */
public class MenuEntryMesh extends GameMesh
{
    public static enum TypeEnum {
        E_MESH, E_COUNT, E_COLOR,
    }

    private static final String TAG = "MenuEntryMesh";

    private static final boolean _DEBUG = false;

    private static final float ManipulatorGap = 0.25f;

    private static final String SmallerKey = "-";
    private static final String LargerKey = "+";

    private TypeEnum _type = TypeEnum.E_MESH;
    private int _minCount = 0;
    private int _maxCount = 0;
    private int _currentCount = 1;
    private Vector3 _entryPosition;
    private Map <String, Vector3> _manipPosition;

    private Map <String, GameMesh> _manipulators;

    private final String _name;

    public MenuEntryMesh (final String [] name)
    {
        super (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (name[1]));
        _name = name[0];
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

    public String getName ()
    {
        return _name;
    }

    public TypeEnum getType ()
    {
        return _type;
    }

    public void render (final GL11 gl, final Vector3 menuOffset)
    {
        render (gl, menuOffset, this, _entryPosition);
        switch (_type) {
        case E_MESH:
            return;
        case E_COUNT:
            if ((_manipulators != null) && !_manipulators.isEmpty ()) {
                render (gl, menuOffset, _manipulators.get (SmallerKey), _manipPosition.get (SmallerKey));
                render (gl, menuOffset, _manipulators.get (Integer.toString (_currentCount)), _manipPosition.get (Integer.toString (_currentCount)));
                render (gl, menuOffset, _manipulators.get (LargerKey), _manipPosition.get (LargerKey));
            }
            break;
        default:
        }
    }

    public void render (final GL11 gl, final Vector3 menuOffset,
                        final GameMesh mesh, final Vector3 position)
    {
        gl.glPushMatrix ();
        gl.glTranslatef (position.x + menuOffset.x, position.y + menuOffset.y, position.z + menuOffset.z);
        mesh.getMesh ().render ();
        gl.glPopMatrix ();

        final Matrix4 transform = new Matrix4 ();
        final Matrix4 tmp = new Matrix4 ();
        transform.setToTranslation (menuOffset.x, menuOffset.y, menuOffset.z);
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
                _manipulators.put (Integer.toString (i), new GameMesh (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (Integer.toString (i))));
            }
        }
    }

    public void setMinCount (final int minCount)
    {
        _minCount = minCount;
        _currentCount = _minCount;
    }

    public void setMinMaxCount (final int minCount, final int maxCount)
    {
        setMinCount (minCount);
        setMaxCount (maxCount);
    }

    public void setOffset (final Vector3 offsetPosition)
    {
        BoundingBox tBB = this.getBoundingBox ();
        Vector3 tDim = tBB.getDimensions ();
        _entryPosition = new Vector3 (offsetPosition.x + tDim.x / 2, offsetPosition.y, offsetPosition.z);
        switch (_type) {
        case E_COUNT:
            if (_manipPosition == null) {
                _manipPosition = new HashMap <String, Vector3> ();
            }
            tBB = _manipulators.get (LargerKey).getBoundingBox ();
            tDim = tBB.getDimensions ();
            float totalX = -tDim.x;
            _manipPosition.put (LargerKey, new Vector3 (-offsetPosition.x + totalX, offsetPosition.y, offsetPosition.z));

            final String currentCount = Integer.toString (_currentCount);
            tBB = _manipulators.get (currentCount).getBoundingBox ();
            tDim = tBB.getDimensions ();
            totalX += -ManipulatorGap - tDim.x;
            _manipPosition.put (currentCount, new Vector3 (-offsetPosition.x + totalX, offsetPosition.y, offsetPosition.z));

            tBB = _manipulators.get (SmallerKey).getBoundingBox ();
            tDim = tBB.getDimensions ();
            totalX += -ManipulatorGap - tDim.x;
            _manipPosition.put (SmallerKey, new Vector3 (-offsetPosition.x + totalX, offsetPosition.y, offsetPosition.z));
        default:
            /* Do nothing yet */
        }
    }

    public void setType (final String type)
    {
        if (type != null) {
            try {
                _type = TypeEnum.valueOf ("E_" + type.toUpperCase ());
            } catch (final Exception e) {
                _type = TypeEnum.E_MESH;
                Gdx.app.log (TAG, e.getMessage ());
            }
            switch (_type) {
            case E_COUNT:
                if (_manipulators == null) {
                    _manipulators = new HashMap <String, GameMesh> ();
                }
                _manipulators.put (SmallerKey, new GameMesh (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (SmallerKey)));
                _manipulators.put (LargerKey, new GameMesh (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (LargerKey)));
                break;
            case E_COLOR:
                break;
            default:
                /* Nothing to do here */
            }
        }
    }

    public void setType (final TypeEnum type)
    {
        _type = type;
    }
}
