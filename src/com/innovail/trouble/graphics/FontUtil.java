/**
 * @file:   com.innovail.trouble.graphics - FontUtil.java
 * @date:   May 24, 2012
 * @author: bweber
 */
package com.innovail.trouble.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * 
 */
public class FontUtil {
    private static Map <Character, Mesh> _fontMap;
    private static float _fontSpacing = 0.1f;
    
    public static void setFontMap (final Map<Character, Mesh> fontMap)
    {
        _fontMap = fontMap;
    }
    
    public static void setFontSpacing (final float spacing)
    {
        _fontSpacing = spacing;
    }
    
    public static Mesh createMesh (final String text)
    {
        if ((_fontMap != null) && !_fontMap.isEmpty ()) {
            final int textSize = text.length ();
            final Vector2 boxWidth = new Vector2 (Vector2.Zero);
            List <BoundingBox> characterBB = new ArrayList <BoundingBox> ();
            float [][] vertices = new float [textSize][];
            int verticesLength = 0;
            
            /* We need to add a spacer here to compensate for the loop's first character */
            boxWidth.x -= _fontSpacing;
            for (int i = 0; i < textSize; i++) {
                BoundingBox currentBB = _fontMap.get (Character.valueOf (text.charAt (i))).calculateBoundingBox ();
                characterBB.add (currentBB);
                final Vector3 bbDimensions = currentBB.getDimensions ();
                boxWidth.x += _fontSpacing + bbDimensions.x;
                if (boxWidth.y < bbDimensions.y) {
                    boxWidth.y = bbDimensions.y;
                }
            }
            float newPos = -boxWidth.x / 2;
            for (int i = 0; i < textSize; i++) {
                final Mesh character = _fontMap.get (Character.valueOf (text.charAt (i)));
                int vertArraySize = character.getNumVertices () * 6;
                float [] tempVerts = new float [vertArraySize];
                character.getVertices (tempVerts);
                for (int j = 0; j < vertArraySize;) {
                    tempVerts[j] += newPos;
                    /* skip Y and Z coordinates and the normals */
                    j += 6;
                    newPos += characterBB.get (i).getDimensions ().x + _fontSpacing;
                }
                vertices [i] = tempVerts;
                verticesLength += tempVerts.length;
            }
            
            List <VertexAttribute> attributes = new ArrayList <VertexAttribute>();
            attributes.add (new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
            attributes.add (new VertexAttribute(Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE));
            Mesh textMesh = new Mesh (true, verticesLength / 2, 0, attributes.toArray (new VertexAttribute [attributes.size ()]));
            float [] newVertexArray = new float [verticesLength];
            int j = 0;
            for (int i = 0; i < textSize; i++) {
                for (float vertex : vertices [i]) {
                    newVertexArray [j++] = vertex;
                }
            }
            textMesh.setVertices (newVertexArray);
            return textMesh;
        }
        
        return null;
    }

    public static Mesh normalizeMeshPosition (Mesh font)
    {
        BoundingBox meshBB = font.calculateBoundingBox ();
        Vector3 centerVector = meshBB.getCenter ();
        centerVector.mul (-1.0f);
        
        /* seems to be the number of faces so times 6 (3 vertices, 3 normals) */
        int vertArraySize = font.getNumVertices () * 6;
        float[] tempVerts = new float [vertArraySize];
        font.getVertices (tempVerts);
        
        for (int i = 0; i < vertArraySize; i++) {
            tempVerts[i++] += centerVector.x;
            tempVerts[i++] += centerVector.y;
            tempVerts[i] += centerVector.z;
            /* skip the normals */
            i += 3;
        }
        
        font.setVertices (tempVerts);
        return font;
    }

}
