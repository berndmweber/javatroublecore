/**
 * @file:   com.innovail.trouble.utils - GamePerspectiveCamera.java
 * @date:   May 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * 
 */
public class GamePerspectiveCamera extends PerspectiveCamera {
    private static final String TAG = "GamePerspectiveCamera";
    private final boolean DEBUG = false;
    
    private final int _MIN = 0;
    private final int _MAX = 1;
    private final int _X = 0;
    private final int _Y = 1;
    
    private float _radius = 0.0f;
    private Vector3 _lookAtPoint;
    private Vector2 _rotationAngle;
    
    private Vector<Vector2> _cutOffAngle;
    private boolean[] _wrapAround;
    
    /**
     * @param fieldOfView
     * @param viewportWidth
     * @param viewportHeight
     */
    public GamePerspectiveCamera (float fieldOfView, float viewportWidth,
                                   float viewportHeight)
    {
        super (fieldOfView, viewportWidth, viewportHeight);

        _cutOffAngle = new Vector<Vector2> ();
        _cutOffAngle.add (new Vector2 (0.0f, 10.0f)); // MIN
        _cutOffAngle.add (new Vector2 (359.99f, 80.0f)); // MAX
        
        _wrapAround = new boolean [2];
        _wrapAround[_X] = true; // X
        _wrapAround[_Y] = false; // Y
    }

    public Vector2 lookAtPosition (Vector3 lookAtPoint, Vector3 from)
    {
        Vector3 normFrom = new Vector3 (from);
        normFrom.sub (lookAtPoint);
        float sqr = normFrom.x * normFrom.x +
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
    
    public void rotateAroundLookAtPoint (Vector2 angleIncrease)
    {
        if (_radius != 0.0f) {
            Vector3 newPosition = new Vector3 ();
            Vector2 newAngle = new Vector2 (_rotationAngle);
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
            
            newPosition.x = _radius *
                            MathUtils.sinDeg (_rotationAngle.x) *
                            MathUtils.sinDeg (_rotationAngle.y);
            newPosition.y = _radius * MathUtils.cosDeg (_rotationAngle.y);
            newPosition.z = _radius *
                            MathUtils.cosDeg (_rotationAngle.x) *
                            MathUtils.sinDeg (_rotationAngle.y);
            newPosition.add (_lookAtPoint);
            
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
    
    public void setCutoffAngle (Vector2 minCutOff, Vector2 maxCutOff)
    {
        _cutOffAngle.set (_MIN, minCutOff);
        _cutOffAngle.set (_MAX, maxCutOff);
    }
    
    public Vector<Vector2> getCutoffAngle ()
    {
        return _cutOffAngle;
    }
    
    public void setWrapAround (boolean X, boolean Y)
    {
        _wrapAround[_X] = X;
        _wrapAround[_Y] = Y;
    }

    public boolean[] getWrapAround ()
    {
        return _wrapAround;
    }
}
