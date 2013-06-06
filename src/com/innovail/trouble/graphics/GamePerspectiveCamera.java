/**
 * @file:   com.innovail.trouble.graphics - GamePerspectiveCamera.java
 * @date:   May 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.graphics;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.innovail.trouble.utils.MathUtils;

/**
 * 
 */
public class GamePerspectiveCamera extends PerspectiveCamera {
    private static final String TAG = "GamePerspectiveCamera";
    private static final boolean DEBUG = false;
    
    private static final int _MIN = 0;
    private static final int _MAX = 1;
    private static final int _X = 0;
    private static final int _Y = 1;
    
    private float _radius = 0.0f;
    private Vector3 _lookAtPoint;
    private Vector2 _rotationAngle;
    
    private final List<Vector2> _cutOffAngle;
    private boolean[] _wrapAround;
    
    /**
     * @param fieldOfView
     * @param viewportWidth
     * @param viewportHeight
     */
    public GamePerspectiveCamera (final float fieldOfView,
                                   final float viewportWidth,
                                   final float viewportHeight)
    {
        super (fieldOfView, viewportWidth, viewportHeight);

        _cutOffAngle = new ArrayList <Vector2> ();
        _cutOffAngle.add (new Vector2 (0.0f, 10.0f)); // MIN
        _cutOffAngle.add (new Vector2 (359.99f, 80.0f)); // MAX
        
        _wrapAround = new boolean [2];
        _wrapAround[_X] = true; // X
        _wrapAround[_Y] = false; // Y
    }

    public Vector2 lookAtPosition (final Vector3 lookAtPoint, final Vector3 from)
    {
        final Vector3 normFrom = new Vector3 (from);
        normFrom.sub (lookAtPoint);
        final float sqr = normFrom.x * normFrom.x +
                           normFrom.y * normFrom.y +
                           normFrom.z * normFrom.z;
        _radius = (float)Math.sqrt (sqr);

        if (_rotationAngle == null) {
            _rotationAngle = new Vector2 ();
        }
        _rotationAngle.x = MathUtils.radiansToDegrees * MathUtils.atan2 (normFrom.x, normFrom.z);
        _rotationAngle.y = MathUtils.radiansToDegrees * (float)Math.acos (normFrom.y / _radius);

        _lookAtPoint = new Vector3 (lookAtPoint);
        position.set (from);
        lookAt (lookAtPoint.x, lookAtPoint.y, lookAtPoint.z);

        if (DEBUG) {
            Gdx.app.log (TAG, "radius: " + _radius);
            Gdx.app.log (TAG, "angle.x: " + _rotationAngle.x);
            Gdx.app.log (TAG, "angle.y: " + _rotationAngle.y);
            Gdx.app.log (TAG, "position: " + position.toString ());
            Gdx.app.log (TAG, "direction: " + direction.toString ());
        }
        
        return _rotationAngle;
    }
    
    public void rotateAroundLookAtPoint (final Vector2 angleIncrease)
    {
        if (_radius != 0.0f) {
            final Vector3 newPosition;// = new Vector3 ();
            final Vector2 newAngle = new Vector2 (_rotationAngle);
            newAngle.add (angleIncrease);
            if ((newAngle.x > _cutOffAngle.get (_MIN).x) &&
                (newAngle.x < _cutOffAngle.get (_MAX).x))
            {
                _rotationAngle.x = newAngle.x;
            } else {
                if (_wrapAround[_X]) {
                    if (newAngle.x < _cutOffAngle.get (_MIN).x) {
                        newAngle.x += _cutOffAngle.get (_MAX).x;
                    } else {
                        newAngle.x -= _cutOffAngle.get (_MAX).x;
                    }
                    _rotationAngle.x = newAngle.x;
                }
            }
            if ((newAngle.y > _cutOffAngle.get (_MIN).y) &&
                (newAngle.y < _cutOffAngle.get (_MAX).y))
            {
                _rotationAngle.y = newAngle.y;
            } else {
                if (_wrapAround[_Y]) {
                    if (newAngle.y < _cutOffAngle.get (_MIN).y) {
                        newAngle.y += _cutOffAngle.get (_MAX).y;
                    } else {
                        newAngle.y -= _cutOffAngle.get (_MAX).y;
                    }
                    _rotationAngle.y = newAngle.y;
                }
            }
            
            newPosition = MathUtils.getSpherePosition (_lookAtPoint, _rotationAngle, _radius);
            
            if (DEBUG) {
                Gdx.app.log (TAG, "X angle: " + _rotationAngle.x);
                Gdx.app.log (TAG, "Y angle: " + _rotationAngle.y);

                Gdx.app.log (TAG, "old position: " + position.toString ());
                Gdx.app.log (TAG, "old direction: " + direction.toString ());
            }

            position.set (newPosition);
            lookAt (_lookAtPoint.x, _lookAtPoint.y, _lookAtPoint.z);

            if (DEBUG) {
                Gdx.app.log (TAG, "new position: " + position.toString ());
                Gdx.app.log (TAG, "new direction: " + direction.toString ());
            }
        }
    }
    
    public void setCutoffAngle (final Vector2 minCutOff, final Vector2 maxCutOff)
    {
        _cutOffAngle.set (_MIN, minCutOff);
        _cutOffAngle.set (_MAX, maxCutOff);
    }
    
    public List<Vector2> getCutoffAngle ()
    {
        return _cutOffAngle;
    }
    
    public void setWrapAround (final boolean X, final boolean Y)
    {
        _wrapAround[_X] = X;
        _wrapAround[_Y] = Y;
    }

    public boolean[] getWrapAround ()
    {
        return _wrapAround;
    }
    
    public Vector3 getCurrentPosition ()
    {
        return position;
    }
    
    public Vector2 getCurrentAngle ()
    {
        return _rotationAngle;
    }
    
    public Vector2 getOverlayAngle ()
    {
        Vector2 overlayAngle = new Vector2 (_rotationAngle.x, _rotationAngle.y);
        return overlayAngle;
    }
    
    public float getRadius ()
    {
        return _radius;
    }
    
    public float getOverlayRadius ()
    {
        return _radius - 2.0f;
    }
}
