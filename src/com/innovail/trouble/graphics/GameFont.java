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
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Json;
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
    private FontStillModel _meshFont = null;
    
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
    public FontStillModel getMeshFont ()
    {
        if ((_meshFont == null) && (_fontType == FontType.MESH)) {
            if ((_fontImagePath == null) || _fontImagePath.isEmpty ()) {
                try {
                    FileHandle inFile = null;
                    if (_isInternal) {
                        inFile = Gdx.files.internal (_fontFilePath);
                    } else {
                        inFile = Gdx.files.external (_fontFilePath);
                    }
                    _meshFont = FontObjLoader.loadObj (inFile);
                    createMeshFontPictureFile ();
                } catch (Exception e) {
                    Gdx.app.log (TAG, e.getMessage ());
                }
            } else {
                try {
                    FileHandle objFile = null;
                    if (_isInternal) {
                        objFile = Gdx.files.internal (_fontImagePath);
                    } else {
                        objFile = Gdx.files.external (_fontImagePath);
                    }
                    
                    Json json = new Json ();
                    FontStillModel.setJsonSerializer (json);
                    _meshFont = json.fromJson (FontStillModel.class, objFile);
                } catch (Exception ex) {
                    Gdx.app.log (TAG, ex.getMessage ());
                }
            }
        }
        return _meshFont;
    }
    
    @SuppressWarnings ("unchecked")
    public void createMeshFontPictureFile ()
    {
        try {
            if (_meshFont != null) {
                FileHandle objFile = null;

                objFile = Gdx.files.external (_fontImagePath);

                Json json = new Json ();
                FontStillModel.setJsonSerializer (json);
                json.toJson (_meshFont, objFile);
            }
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
            if (_meshFont != null) {
                return true;
            }
            break;
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
