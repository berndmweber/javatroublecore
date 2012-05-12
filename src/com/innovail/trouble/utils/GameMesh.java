/**
 * @file:   com.innovail.trouble.utils - GameMesh.java
 * @date:   May 8, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

import java.io.InputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;

/**
 * 
 */
public class GameMesh {
    private final boolean _isInternal;
    private final Color _color;
    private final String _path;
    
    private Mesh _mesh;

    public GameMesh (String path, Color color, boolean isInternal)
    {
        _path = path;
        _color = color;
        _isInternal = isInternal;
    }
    
    public GameMesh (String path, Color color)
    {
        _path = path;
        _color = color;
        _isInternal = true;
    }

    public GameMesh (String path, boolean isInternal)
    {
        _path = path;
        _color = Color.WHITE;
        _isInternal = isInternal;
    }

    public GameMesh (String path)
    {
        _path = path;
        _color = Color.WHITE;
        _isInternal = true;
    }
    
    public boolean isInternal ()
    {
        return _isInternal;
    }

    public Color getColor ()
    {
        return _color;
    }
    
    public String getPath ()
    {
        return _path;
    }
    
    public Mesh getMesh ()
    {
        if (_mesh == null) {
            if ((_path != null) && !_path.isEmpty ()) {
                try {
                    FileHandle objFile = null;
                    if (_isInternal) {
                        objFile = Gdx.files.internal (_path);
                    } else {
                        objFile = Gdx.files.external (_path);
                    }
                    InputStream in = objFile.read ();
                    _mesh = ObjLoader.loadObj (in, true);
                    in.close ();
                } catch (Exception ex) {
                    ex.printStackTrace ();
                }
            }
        }

        return _mesh;
    }
}
