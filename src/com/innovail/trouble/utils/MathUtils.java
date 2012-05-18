/**
 * @file:   com.innovail.trouble.utils - MathUtils.java
 * @date:   May 18, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * 
 */
public class MathUtils extends com.badlogic.gdx.math.MathUtils {
    public static Vector3 getSpherePosition (final Vector3 centerPoint, final Vector2 angle, final float radius)
    {
        final Vector3 newPosition = new Vector3 ();
        
        newPosition.x = radius * sinDeg (angle.x) * sinDeg (angle.y);
        newPosition.y = radius * cosDeg (angle.y);
        newPosition.z = radius * cosDeg (angle.x) * sinDeg (angle.y);
        newPosition.add (centerPoint);

        return newPosition;
    }
}
