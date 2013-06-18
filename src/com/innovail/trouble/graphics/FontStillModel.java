/**
 * @file: com.innovail.trouble.graphics - FontStillModel.java
 * @date: May 24, 2012
 * @author: bweber
 */
package com.innovail.trouble.graphics;

import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dLoader;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.chunks.G3dExporter;
import com.badlogic.gdx.graphics.g3d.model.SubMesh;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.g3d.model.still.StillSubMesh;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * 
 */
public class FontStillModel
{
    private static final String TAG = "FontStillModel";

    private final StillModel _fontModel;
    private Map <Character, String> _fontMap;
    private float _fontSpacing = 0.04f;

    private Character _referenceCharacter;

    @SuppressWarnings ("unchecked")
    public static FontStillModel importModel (final FileHandle handleSM,
                                              final FileHandle handleFSM)
    {
        final StillModel tempModel = G3dLoader.loadStillModel (handleSM);
        FontStillModel fsm = null;
        try {
            final InputStream is = handleFSM.read ();
            final ObjectInput oi = new ObjectInputStream (is);
            final Map <Character, String> fm = (HashMap <Character, String>) oi.readObject ();
            final float fs = oi.readFloat ();
            final char rc = oi.readChar ();
            oi.close ();
            fsm = new FontStillModel (tempModel, fm, rc);
            fsm.setFontSpacing (fs);
        } catch (final Exception ex) {
            Gdx.app.log (TAG, ex.getMessage ());
        }
        return fsm;
    }

    public FontStillModel (final StillModel model)
    {
        _fontModel = model;
        createFontMap ();
    }

    public FontStillModel (final StillModel model,
                           final Map <Character, String> fontMap,
                           final Character referenceCharacter)
    {
        _fontModel = model;
        _fontMap = fontMap;
        _referenceCharacter = referenceCharacter;
    }

    private void createFontMap ()
    {
        if ((_fontMap == null) || _fontMap.isEmpty ()) {
            _fontMap = new HashMap <Character, String> ();
        }
        final SubMesh [] subMeshes = _fontModel.getSubMeshes ();
        for (int i = 0; i < subMeshes.length; i++) {
            final String line = subMeshes[i].name;
            String [] tokens = line.split ("\\.");
            if (tokens[1].isEmpty ()) {
                tokens[1] = "._";
            }
            tokens = tokens[1].split ("_");
            if (tokens[0].isEmpty ()) {
                tokens[0] = "_";
            }
            _fontMap.put (tokens[0].charAt (0), line);
            if (i == 0) {
                _referenceCharacter = tokens[0].charAt (0);
            }
        }
    }

    public StillModel createStillModel (final String text)
    {
        if ((_fontMap != null) && !_fontMap.isEmpty ()) {
            final int textSize = text.length ();
            final Vector2 boxWidth = new Vector2 (Vector2.Zero);
            final List <SubMesh> characterMeshes = new ArrayList <SubMesh> ();
            final BoundingBox referenceBB = new BoundingBox ();
            final List <Float> xVectors = new ArrayList <Float> ();

            _fontModel.getSubMesh (_fontMap.get (_referenceCharacter)).getBoundingBox (referenceBB);
            /*
             * We need to add a spacer here to compensate for the loop's first
             * character
             */
            for (int i = 0; i < textSize; i++) {
                /* Skip spaces */
                if (text.charAt (i) == ' ') {
                    boxWidth.x += referenceBB.getDimensions ().x;
                    continue;
                }

                final String name = _fontMap.get (Character.valueOf (text.charAt (i)));
                final String newName = new String (UUID.randomUUID ().toString ());
                final SubMesh tempSubMesh = _fontModel.getSubMesh (name);
                if (tempSubMesh == null) {
                    return null;
                }
                final SubMesh character = new StillSubMesh (newName, tempSubMesh.getMesh ().copy (true), tempSubMesh.primitiveType, tempSubMesh.material);
                final BoundingBox currentBB = new BoundingBox ();
                character.getBoundingBox (currentBB);
                final Vector3 bbDimensions = currentBB.getDimensions ();
                xVectors.add (boxWidth.x);
                boxWidth.x += _fontSpacing + bbDimensions.x;
                if (boxWidth.y < bbDimensions.y) {
                    boxWidth.y = bbDimensions.y;
                }
                characterMeshes.add (character);
            }
            boxWidth.x -= _fontSpacing;

            final float newPos = -boxWidth.x / 2;
            for (int i = 0; i < characterMeshes.size (); i++) {
                final Mesh currentMesh = characterMeshes.get (i).getMesh ();
                final int vertArraySize = currentMesh.getNumVertices () * 6;
                final float [] tempVerts = new float [vertArraySize];
                currentMesh.getVertices (tempVerts);
                for (int j = 0; j < vertArraySize;) {
                    tempVerts[j] += newPos + xVectors.get (i);
                    /* skip Y and Z coordinates and the normals */
                    j += 6;
                }
                currentMesh.setVertices (tempVerts);
            }
            final SubMesh [] textMeshArray = new SubMesh [characterMeshes.size ()];
            characterMeshes.toArray (textMeshArray);
            final StillModel textMesh = new StillModel (textMeshArray);

            return textMesh;
        }

        return null;
    }

    public void exportModel (final FileHandle handleSM,
                             final FileHandle handleFSM)
    {
        G3dExporter.export (_fontModel, handleSM);
        try {
            final OutputStream os = handleFSM.write (false);
            final ObjectOutput oo = new ObjectOutputStream (os);
            oo.writeObject (_fontMap);
            oo.writeFloat (_fontSpacing);
            oo.writeChar (_referenceCharacter);
            oo.flush ();
            os.close ();
        } catch (final Exception ex) {
            Gdx.app.log (TAG, ex.getMessage ());
        }
    }

    public Map <Character, String> getFontMap ()
    {
        return _fontMap;
    }

    public float getFontSpacing ()
    {
        return _fontSpacing;
    }

    public Character getReferenceCharacter ()
    {
        return _referenceCharacter;
    }

    public StillModel getStillModel ()
    {
        return _fontModel;
    }

    public void setFontSpacing (final float spacing)
    {
        _fontSpacing = spacing;
    }
}
