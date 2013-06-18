/**
 * @file: com.innovail.trouble.graphics - GameFont.java
 * @date: Apr 17, 2012
 * @author: bweber
 */
package com.innovail.trouble.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.innovail.trouble.utils.FontObjLoader;

/**
 * 
 */
public class GameFont
{
    public static enum FontType {
        BITMAP, MESH
    }

    private static final String TAG = "GameFont";;

    private final String _fontFilePath;
    private final String _fontImagePath;
    private final boolean _isInternal;
    private final FontType _fontType;

    private boolean _flipFont = false;
    private BitmapFont _bitmapFont = null;

    private FontStillModel _meshFont = null;

    public static String typeToString (final FontType type)
    {
        return type.toString ().toLowerCase ();
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

    public void createMeshFontPictureFile ()
    {
        try {
            if (_meshFont != null) {
                final FileHandle smFile = Gdx.files.external (_fontImagePath);
                final String fsmPath = smFile.pathWithoutExtension ().concat (".fsm");
                final FileHandle fsmFile = Gdx.files.external (fsmPath);

                _meshFont.exportModel (smFile, fsmFile);
            }
        } catch (final Exception ex) {
            Gdx.app.log (TAG, ex.getMessage ());
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
                _bitmapFont = new BitmapFont (Gdx.files.internal (_fontFilePath), Gdx.files.internal (_fontImagePath), _flipFont);
            } else {
                _bitmapFont = new BitmapFont (Gdx.files.external (_fontFilePath), Gdx.files.external (_fontImagePath), _flipFont);
            }
        }
        return _bitmapFont;
    }

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
                } catch (final Exception e) {
                    Gdx.app.log (TAG, e.getMessage ());
                }
            } else {
                try {
                    FileHandle smFile = null;
                    FileHandle fsmFile = null;
                    if (_isInternal) {
                        smFile = Gdx.files.internal (_fontImagePath);
                        final String fsmPath = smFile.pathWithoutExtension ().concat (".fsm");
                        fsmFile = Gdx.files.internal (fsmPath);
                    } else {
                        smFile = Gdx.files.external (_fontImagePath);
                        final String fsmPath = smFile.pathWithoutExtension ().concat (".fsm");
                        fsmFile = Gdx.files.external (fsmPath);
                    }

                    _meshFont = FontStillModel.importModel (smFile, fsmFile);
                } catch (final Exception ex) {
                    Gdx.app.log (TAG, ex.getMessage ());
                }
            }
        }
        return _meshFont;
    }

    public String getStringType ()
    {
        return _fontType.toString ().toLowerCase ();
    }

    public FontType getType ()
    {
        return _fontType;
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
}
