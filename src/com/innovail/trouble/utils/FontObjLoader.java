/**
 * @file:   com.innovail.trouble.utils - FontObjLoader.java
 * @date:   May 23, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.ModelLoaderHints;
import com.badlogic.gdx.graphics.g3d.loaders.ModelLoaderRegistry;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.g3d.model.still.StillSubMesh;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import com.innovail.trouble.graphics.FontStillModel;

/**
 * 
 */
public class FontObjLoader {
    
    public static FontStillModel loadObj (final FileHandle handle)
    {
        return loadObj (handle, false);
    }
    
    public static FontStillModel loadObj (final FileHandle handle, final boolean flipV)
    {
        return loadObj (handle, flipV, false);
    }
    
    public static FontStillModel loadObj (final FileHandle handle, final boolean flipV, final boolean useIndices)
    {
        StillModel tempStillModel = ModelLoaderRegistry.loadStillModel (handle, new ModelLoaderHints (flipV));
        FontStillModel font = new FontStillModel (loadFontFromStillModel (tempStillModel));

        return font;
    }
    
    public static StillModel loadFontFromStillModel (final StillModel model)
    {
        float referenceEdge = 0.0f;
        StillSubMesh [] subMeshes = model.subMeshes; 
        // The first submesh is the reference character
        StillSubMesh reference = subMeshes[0];
        BoundingBox referenceBB = new BoundingBox ();
        
        reference.getBoundingBox (referenceBB);
        referenceEdge = referenceBB.getMin ().y * -1.0f;
        // Go through the remaining submeshes and normalize their position in reference to the first submesh
        for (int i = 1; i < subMeshes.length; i++) {
            subMeshes[i].setMesh (normalizeMeshPosition (subMeshes[i].getMesh (), referenceEdge));
        }

        return model;
    }
    
    public static Mesh normalizeMeshPosition (final Mesh font, final float referenceEdge)
    {
        final BoundingBox meshBB = font.calculateBoundingBox ();
        final Vector3 minVector = meshBB.getMin ();
        minVector.mul (-1.0f);
        
        /* seems to be the number of faces so times 6 (3 vertices, 3 normals) */
        final int vertArraySize = font.getNumVertices () * 6;
        final float[] tempVerts = new float [vertArraySize];
        font.getVertices (tempVerts);
        
        /* We want to lower/left align the character to the 0/0/0 coordinate */
        for (int i = 0; i < vertArraySize; i++) {
            tempVerts[i++] += minVector.x;
            /* The Y coordinate needs to be aligned with the reference edge */
            tempVerts[i++] += referenceEdge;
            tempVerts[i] += minVector.z;
            /* skip the normals */
            i += 3;
        }
        
        font.setVertices (tempVerts);
        return font;
    }
}
