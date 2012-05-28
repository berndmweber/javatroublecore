/**
 * @file:   com.innovail.trouble.utils - GameFont.java
 * @date:   Apr 17, 2012
 * @author: bweber
 */
package com.innovail.trouble.graphics;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.innovail.trouble.utils.FontObjLoader;

/**
 * 
 */
public class GameFont {
    private static final String TAG = "GameFont";

    public static enum FontType {
        BITMAP,
        MESH
    };
    
    private final String _fontFilePath;
    private final String _fontImagePath;
    private final boolean _isInternal;
    private final FontType _fontType;
    private boolean _flipFont = false;
    
    private BitmapFont _bitmapFont = null;
    
    public GameFont (final String fontType, final String fontFilePath,
                      final String fontImagePath, final boolean isInternal)
    {
        _fontType = FontType.valueOf (fontType.toUpperCase ());
        _fontFilePath = fontFilePath;
        _fontImagePath = fontImagePath;
        _isInternal = isInternal;
        if (_fontType == FontType.MESH) {
            getMeshFont ();
        }
    }
    
    public GameFont (final String fontType, final String fontFilePath,
                      final String fontImagePath)
    {
        _fontType = FontType.valueOf (fontType.toUpperCase ());
        _fontFilePath = fontFilePath;
        _fontImagePath = fontImagePath;
        _isInternal = true;
        if (_fontType == FontType.MESH) {
            getMeshFont ();
        }
    }
    
    public void flipFont ()
    {
        _flipFont = true;
    }
    
    public BitmapFont getBitmapFont ()
    {
        if ((_bitmapFont == null) && (_fontType == FontType.BITMAP)) {
            if (_isInternal) {
                _bitmapFont =  new BitmapFont (Gdx.files.internal (_fontFilePath),
                                                Gdx.files.internal (_fontImagePath),
                                                _flipFont);
            } else {
                _bitmapFont = new BitmapFont (Gdx.files.external (_fontFilePath),
                                               Gdx.files.external (_fontImagePath),
                                               _flipFont);
            }
        }
        return _bitmapFont;
    }
    
    @SuppressWarnings ("unchecked")
    public boolean getMeshFont ()
    {
        if ((FontUtil.getFontMap () == null) && (_fontType == FontType.MESH)) {
            Map <Character, Mesh> font = null;
            if (_fontImagePath == null) {
                try {
                    FileHandle inFile = null;
                    if (_isInternal) {
                        inFile = Gdx.files.internal (_fontFilePath);
                    } else {
                        inFile = Gdx.files.external (_fontFilePath);
                    }
                    final InputStream in = inFile.read ();
                    font = FontObjLoader.loadObj (in);
                    in.close ();
                } catch (Exception e) {
                    Gdx.app.log (TAG, e.getMessage ());
                }
            } else {
                try {
                    Map <Character, float[]> fontV = null;
                    FileHandle objFile = null;
                    if (_isInternal) {
                        objFile = Gdx.files.internal (_fontImagePath);
                    } else {
                        objFile = Gdx.files.external (_fontImagePath);
                    }
                    InputStream is = null;
                    is = objFile.read ();
                    ObjectInputStream oi = new ObjectInputStream (is);
                    fontV = (HashMap <Character, float[]>) oi.readObject ();
                    is.close ();
                
                    font = new HashMap <Character, Mesh> ();
                    List <VertexAttribute> attributes = new ArrayList <VertexAttribute> ();
                    attributes.add (new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
                    attributes.add (new VertexAttribute(Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE));
                    Iterator <Character> it = fontV.keySet ().iterator ();
                    while (it.hasNext ()) {
                        Character c = it.next ();
                        float [] vertices = fontV.get (c);
                        Mesh m = new Mesh (true, vertices.length / 2, 0, attributes.toArray (new VertexAttribute [attributes.size ()]));
                        m.setVertices (vertices);
                        font.put (c, m);
                    }
                } catch (Exception ex) {
                    Gdx.app.log (TAG, ex.getMessage ());
                }
            }
            FontUtil.setFontMap (font);
        }
        return (FontUtil.getFontMap () != null);
    }
    
    @SuppressWarnings ("unchecked")
    public void createMeshFontPictureFile ()
    {
        try {
            Map <Character, Mesh> font = null;
            Map <Character, float[]> fontV = null;
            FileHandle objFile = null;
            FileHandle inFile = null;

            objFile = Gdx.files.external (_fontImagePath);
            if (_isInternal) {
                inFile = Gdx.files.internal (_fontFilePath);
            } else {
                inFile = Gdx.files.external (_fontFilePath);
            }
            final InputStream in = inFile.read ();
            font = FontObjLoader.loadObj (in);
            in.close ();
            
            final OutputStream os = objFile.write (false);
            final ObjectOutput oo = new ObjectOutputStream (os);
            fontV = new HashMap <Character, float[]> ();
            Iterator <Character> it = font.keySet ().iterator ();
            while (it.hasNext ()) {
                Character c = it.next ();
                Mesh m = font.get (c);
                int vertLen = m.getNumVertices () * 6;
                float [] vertices = new float [vertLen];
                m.getVertices (vertices);
                fontV.put (c, vertices);
            }
            oo.writeObject ((HashMap<Character, float[]>) fontV);
            oo.flush ();
            os.close ();
        } catch (Exception ex) {
            Gdx.app.log (TAG, ex.getMessage ());
        }
    }
    
    public boolean hasFont ()
    {
        switch (_fontType) {
        case BITMAP:
            if (_bitmapFont != null) {
                return true;
            }
            break;
        case MESH:
            return (FontUtil.getFontMap () != null);
        }
        return false;
    }
    
    public FontType getType ()
    {
        return _fontType;
    }
    
    public String getStringType ()
    {
        return _fontType.toString ().toLowerCase ();
    }
    
    public static String typeToString (final FontType type)
    {
        return type.toString ().toLowerCase ();
    }
}
