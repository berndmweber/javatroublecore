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
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * 
 */
public class GameMesh {
    private static final String TAG = "GameMesh";

    private final boolean _isInternal;
    private final Color _color;
    private final String _path;
    private final String _texturePath;
    private final Format _textureColorFormat;
    
    private Mesh _mesh;
    private Texture _texture;
    private BoundingBox _bb;
    private Mesh _bbMesh;

    public GameMesh (String path, Color color, boolean isInternal,
                      String texturePath, String textureColorFormat)
    {
        _path = path;
        _color = color;
        _isInternal = isInternal;
        _texturePath = texturePath;
        _textureColorFormat = Format.valueOf (textureColorFormat);
    }
    
    public GameMesh (String path, boolean isInternal,
                      String texturePath, String textureColorFormat)
    {
        _path = path;
        _color = Color.WHITE;
        _isInternal = isInternal;
        _texturePath = texturePath;
        _textureColorFormat = Format.valueOf (textureColorFormat);
    }

    public GameMesh (String path, Color color, boolean isInternal, String texturePath)
    {
        _path = path;
        _color = color;
        _isInternal = isInternal;
        _texturePath = texturePath;
        _textureColorFormat = Format.RGBA8888;
    }
    
    public GameMesh (String path, boolean isInternal, String texturePath)
    {
        _path = path;
        _color = Color.WHITE;
        _isInternal = isInternal;
        _texturePath = texturePath;
        _textureColorFormat = Format.RGBA8888;
    }
    
    public GameMesh (String path, Color color, boolean isInternal)
    {
        _path = path;
        _color = color;
        _isInternal = isInternal;
        _texturePath = null;
        _textureColorFormat = null;
    }

    public GameMesh (String path, Color color)
    {
        _path = path;
        _color = color;
        _isInternal = true;
        _texturePath = null;
        _textureColorFormat = null;
    }

    public GameMesh (String path, boolean isInternal)
    {
        _path = path;
        _color = Color.WHITE;
        _isInternal = isInternal;
        _texturePath = null;
        _textureColorFormat = null;
    }

    public GameMesh (String path)
    {
        _path = path;
        _color = Color.WHITE;
        _isInternal = true;
        _texturePath = null;
        _textureColorFormat = null;
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
                    _bb = _mesh.calculateBoundingBox ();
                    //Gdx.app.log (TAG, _path + ": BoundingBox - " + _bb.toString ());
                } catch (Exception ex) {
                    ex.printStackTrace ();
                }
            }
        }

        return _mesh;
    }
    
    public Texture getTexture ()
    {
        if (_texture == null) {
            if ((_texturePath != null) && !_texturePath.isEmpty ()) {
                FileHandle objFile = null;
                if (_isInternal) {
                    objFile = Gdx.files.internal (_texturePath);
                } else {
                    objFile = Gdx.files.external (_texturePath);
                }
                _texture = new Texture(objFile, _textureColorFormat, true);
                _texture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
            }
        }
        return _texture;
    }
    
    public BoundingBox getBoundingBox ()
    {
        return _bb;
    }
    
    public BoundingBox transformBoundingBox (Matrix4 transform)
    {
        _bb = _mesh.calculateBoundingBox ();
        _bb.mul (transform);
        return _bb;
    }
    
    public Mesh getBBMesh ()
    {
        if (_bbMesh != null) {
            _bbMesh.dispose ();
        }
        Vector3[] bbCorners = _bb.getCorners ();
        _bbMesh = new Mesh (true, 8, 36, new VertexAttribute(Usage.Position, 3, "a_position"));
        _bbMesh.setVertices(new float[] {bbCorners[0].x, bbCorners[0].y, bbCorners[0].z,
                                          bbCorners[1].x, bbCorners[1].y, bbCorners[1].z,
                                          bbCorners[2].x, bbCorners[2].y, bbCorners[2].z,
                                          bbCorners[3].x, bbCorners[3].y, bbCorners[3].z,
                                          bbCorners[4].x, bbCorners[4].y, bbCorners[4].z,
                                          bbCorners[5].x, bbCorners[5].y, bbCorners[5].z,
                                          bbCorners[6].x, bbCorners[6].y, bbCorners[6].z,
                                          bbCorners[7].x, bbCorners[7].y, bbCorners[7].z,
                                          });
        _bbMesh.setIndices(new short[] {0, 1, 4,
                                         1, 5, 4,
                                         1, 2, 5,
                                         2, 6, 5,
                                         2, 3, 6,
                                         3, 7, 6,
                                         3, 0, 7,
                                         0, 4, 7,
                                         4, 5, 7,
                                         5, 6, 7,
                                         3, 2, 0,
                                         2, 1, 0});
        return _bbMesh;
    }
}
