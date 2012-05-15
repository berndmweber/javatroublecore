/**
 * @file:   com.innovail.trouble.utils - GamePerspectiveCamera.java
 * @date:   May 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

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
    
    private float _radius = 0.0f;
    private Vector3 _lookAtPoint;
    private Vector2 _rotationAngle;
    
    /**
     * @param fieldOfView
     * @param viewportWidth
     * @param viewportHeight
     */
    public GamePerspectiveCamera (float fieldOfView, float viewportWidth,
                                   float viewportHeight)
    {
        super (fieldOfView, viewportWidth, viewportHeight);
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
            _rotationAngle.add (angleIncrease);
            
            newPosition.x = _radius *
                            MathUtils.sinDeg (_rotationAngle.x) *
                            MathUtils.sinDeg (_rotationAngle.y);
            newPosition.y = _radius * MathUtils.cosDeg (_rotationAngle.y);
            newPosition.z = _radius *
                            MathUtils.cosDeg (_rotationAngle.x) *
                            MathUtils.sinDeg (_rotationAngle.y);
            newPosition.add (_lookAtPoint);
            
            if (DEBUG) {
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
}
