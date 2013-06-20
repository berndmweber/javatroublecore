/**
 * @file: com.innovail.trouble.uicomponent - MenuEntryMesh.java
 * @date: May 8, 2012
 * @author: bweber
 */
package com.innovail.trouble.uicomponent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

/**
 * 
 */
public class MenuEntryMesh extends MenuEntry
{
    private static final String TAG = "MenuEntryMesh";

    private static final boolean _DEBUG = false;

    public MenuEntryMesh (final String [] name)
    {
        super (name);
        if (_DEBUG) {
            Gdx.app.log (TAG, "Creating Entry");
        }
    }

    @Override
    public boolean handleIntersect (final Ray touchRay)
    {
        if (Intersector.intersectRayBoundsFast (touchRay, this.getBoundingBox ())) {
            return true;
        }
        return false;
    }

    @Override
    public void render (final GL11 gl, final Vector3 menuOffset)
    {
        gl.glPushMatrix ();
        gl.glTranslatef (_entryPosition.x + menuOffset.x, _entryPosition.y + menuOffset.y, _entryPosition.z + menuOffset.z);
        this.getMesh ().render ();
        gl.glPopMatrix ();

        final Matrix4 transform = new Matrix4 ();
        final Matrix4 tmp = new Matrix4 ();
        transform.setToTranslation (_entryPosition.x + menuOffset.x, _entryPosition.y + menuOffset.y, _entryPosition.z + menuOffset.z);
        transform.mul (tmp);
        this.transformBoundingBox (transform);

        if (_DEBUG) {
            Gdx.gl11.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_LINE);
            gl.glPushMatrix ();
            this.getBBMesh ().render (GL10.GL_TRIANGLES);
            gl.glPopMatrix ();
            Gdx.gl11.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_FILL);
        }
    }

    @Override
    public void setOffset (final Vector3 offsetPosition)
    {
        final BoundingBox tBB = this.getBoundingBox ();
        final Vector3 tDim = tBB.getDimensions ();
        _entryPosition = new Vector3 (offsetPosition.x + tDim.x / 2, offsetPosition.y, offsetPosition.z);
    }
}
