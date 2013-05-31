/**
 * @file: com.innovail.trouble.graphics - FontUtil.java
 * @date: May 24, 2012
 * @author: bweber
 */
package com.innovail.trouble.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.model.SubMesh;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.g3d.model.still.StillSubMesh;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.OrderedMap;

/**
 * 
 */
public class FontStillModel {
    private final StillModel _fontModel;
    private Map <Character, String> _fontMap;
    private float _fontSpacing = 0.04f;
    private Character _referenceCharacter;

    public FontStillModel (StillModel model) {
        _fontModel = model;
        createFontMap ();
    }

    public FontStillModel (StillModel model, Map <Character, String> fontMap, Character referenceCharacter) {
        _fontModel = model;
        _fontMap = fontMap;
        _referenceCharacter = referenceCharacter;
    }

    public Map <Character, String> getFontMap () {
        return _fontMap;
    }

    public Character getReferenceCharacter () {
        return _referenceCharacter;
    }
    
    public float getFontSpacing () {
        return _fontSpacing;
    }
    
    public StillModel getStillModel () {
        return _fontModel;
    }

    public void setFontSpacing (final float spacing) {
        _fontSpacing = spacing;
    }

    private void createFontMap () {
        if ((_fontMap == null) || _fontMap.isEmpty ()) {
            _fontMap = new HashMap <Character, String> ();
        }
        SubMesh [] subMeshes = _fontModel.getSubMeshes ();
        for (int i = 0; i< subMeshes.length; i++) {
            String line = subMeshes[i].name;
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

    public StillModel createStillModel (final String text) {
        if ((_fontMap != null) && !_fontMap.isEmpty ()) {
            final int textSize = text.length ();
            final Vector2 boxWidth = new Vector2 (Vector2.Zero);
            List <SubMesh> characterMeshes = new ArrayList <SubMesh> ();
            final BoundingBox referenceBB = new BoundingBox ();
            List <Float> xVectors = new ArrayList <Float> ();

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
                final SubMesh character = (SubMesh) new StillSubMesh (newName, tempSubMesh.getMesh ().copy (true), tempSubMesh.primitiveType, tempSubMesh.material);
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

            float newPos = -boxWidth.x / 2;
            for (int i = 0; i < characterMeshes.size (); i++) {
                Mesh currentMesh = characterMeshes.get (i).getMesh ();
                int vertArraySize = currentMesh.getNumVertices () * 6;
                float[] tempVerts = new float[vertArraySize];
                currentMesh.getVertices (tempVerts);
                for (int j = 0; j < vertArraySize;) {
                    tempVerts[j] += newPos + xVectors.get (i);
                    /* skip Y and Z coordinates and the normals */
                    j += 6;
                }
                currentMesh.setVertices (tempVerts);
            }
            SubMesh [] textMeshArray = new SubMesh [characterMeshes.size ()];
            characterMeshes.toArray (textMeshArray);
            StillModel textMesh = new StillModel (textMeshArray);

            return textMesh;
        }

        return null;
    }
    
    public static void setJsonSerializer (Json json) {
        json.setSerializer (FontStillModel.class, new Json.Serializer <FontStillModel> () {

            @SuppressWarnings ("rawtypes")
            @Override
            public void write (Json json, FontStillModel object, Class knownType) {
                json.writeObjectStart ();
                json.writeValue ("fontMap", object.getFontMap ());
                json.writeValue ("referenceCharacter", object.getReferenceCharacter ());
                json.writeValue ("fontSpacing", Float.valueOf (object.getFontSpacing ()));
                json.writeObjectStart ("stillModel");
                SubMesh [] subMeshes = object.getStillModel ().getSubMeshes ();
                for (int i = 0; i < subMeshes.length; i++) {
                    json.writeObjectStart (subMeshes[i].name);
                    json.writeValue ("material", subMeshes[i].material);
                    json.writeValue ("primitiveType", subMeshes[i].primitiveType);
                    json.writeObjectStart ("mesh");
                    json.writeValue ("isStatic", Boolean.valueOf (true));
                    json.writeValue ("maxVertices", Integer.valueOf (subMeshes[i].getMesh ().getMaxVertices ()));
                    json.writeValue ("maxIndices", Integer.valueOf (subMeshes[i].getMesh ().getMaxIndices ()));
                    json.writeValue ("attributes", subMeshes[i].getMesh ().getVertexAttributes ());
                    float [] vertices = new float [subMeshes[i].getMesh ().getNumVertices () * 6];
                    subMeshes[i].getMesh ().getVertices (vertices);
                    json.writeValue ("vertices", vertices);
                    json.writeObjectEnd ();
                    json.writeObjectEnd ();
                }
                json.writeObjectEnd ();
                json.writeObjectEnd ();
            }

            @SuppressWarnings ({ "rawtypes", "unchecked" })
            @Override
            public FontStillModel read (Json json, Object jsonData, Class type) {
                ArrayList <SubMesh> subMeshes = new ArrayList <SubMesh> ();
                OrderedMap fontMap = (OrderedMap) json.readValue ("fontMap", OrderedMap.class, jsonData);
                Map <Character, String> actualFontMap = new HashMap <Character, String> ();
                OrderedMap.Keys map = fontMap.keys ();
                while (map.hasNext ()) {
                    String entry = (String) map.next ();
                    Character key = Character.valueOf (entry.charAt (0));
                    String value = json.readValue (entry, String.class, fontMap);
                    actualFontMap.put (key, value);
                }
                Character referenceCharacter = (Character) json.readValue ("referenceCharacter", Character.class, jsonData);
                float fontSpacing = ((Float) json.readValue ("fontSpacing", Float.class, jsonData)).floatValue ();
                OrderedMap stillModel = (OrderedMap) json.readValue ("stillModel", OrderedMap.class, jsonData);
                OrderedMap.Keys model = stillModel.keys ();
                while (model.hasNext ()) {
                    String name = (String) model.next ();
                    OrderedMap subMesh = (OrderedMap) json.readValue (name, OrderedMap.class, stillModel);
                    OrderedMap material = (OrderedMap) json.readValue ("material", OrderedMap.class, subMesh);
                    Material actualMaterial = new Material (json.readValue ("name", String.class, material));
                    int primitiveType = ((Integer) json.readValue ("primitiveType", Integer.class, subMesh)).intValue ();
                    OrderedMap mesh = (OrderedMap) json.readValue ("mesh", OrderedMap.class, subMesh);
                    boolean isStatic = ((Boolean) json.readValue ("isStatic", Boolean.class, mesh)).booleanValue ();
                    int maxVertices = ((Integer) json.readValue ("maxVertices", Integer.class, mesh)).intValue ();
                    int maxIndices = ((Integer) json.readValue ("maxIndices", Integer.class, mesh)).intValue ();
                    OrderedMap vertexAttributes = (OrderedMap) json.readValue ("attributes", OrderedMap.class, mesh);
                    ArrayList <OrderedMap> attributesOrdered = (ArrayList <OrderedMap>) json.readValue ("attributes", ArrayList.class, vertexAttributes);
                    ArrayList <VertexAttribute> attributes = new ArrayList <VertexAttribute> ();
                    for (int i = 0; i < attributesOrdered.size (); i++) {
                        OrderedMap currentAttribute = attributesOrdered.get (i);
                        int usage = ((Integer) json.readValue ("usage", Integer.class, currentAttribute)).intValue ();
                        int numComponents = ((Integer) json.readValue ("numComponents", Integer.class, currentAttribute)).intValue ();
                        String alias = (String) json.readValue ("alias", String.class, currentAttribute);
                        attributes.add (new VertexAttribute (usage, numComponents, alias));
                    }
                    ArrayList <Float> vertices = (ArrayList <Float>) json.readValue ("vertices", ArrayList.class, mesh);
                    float [] verticesArray = new float [vertices.size ()];
                    for (int i = 0; i < vertices.size (); i++) {
                        Float f = vertices.get (i);
                        verticesArray [i] = (f != null ? f.floatValue () : Float.NaN);
                    }
                    short [] indicesArray = new short [maxIndices];
                    for (short index = 0; index < maxIndices; index++) {
                        indicesArray [index] = index;
                    }
                    Mesh actualMesh = new Mesh (isStatic, maxVertices, maxIndices, attributes.toArray (new VertexAttribute [attributes.size ()]));
                    actualMesh.setVertices (verticesArray);
                    actualMesh.setIndices (indicesArray);
                    SubMesh actualSubMesh = new StillSubMesh (name, actualMesh, primitiveType);
                    actualSubMesh.material = actualMaterial;
                    subMeshes.add (actualSubMesh);
                }

                StillModel actualStillModel = new StillModel (subMeshes.toArray (new SubMesh [subMeshes.size ()]));
                FontStillModel fontStillModel = new FontStillModel (actualStillModel, actualFontMap, referenceCharacter);
                fontStillModel.setFontSpacing (fontSpacing);
                
                return fontStillModel;
            }
        });
    }
}
