/**
 * @file: com.innovail.trouble.graphics - GameMesh.java
 * @date: May 8, 2012
 * @author: bweber
 */
package com.innovail.trouble.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.ModelLoaderHints;
import com.badlogic.gdx.graphics.g3d.loaders.ModelLoaderRegistry;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * 
 */
public class GameMesh
{
    private static final String TAG = "GameMesh";

    private final boolean _isInternal;
    private boolean _isVFlipped = false;
    private Color _color;
    private final String _path;
    private final String _texturePath;
    private final Format _textureColorFormat;
    private String _soundPath;
    private boolean _soundIsInternal;

    private StillModel _mesh;
    private Texture _texture;
    private Sound _sound;
    private final BoundingBox _bb = new BoundingBox ();
    private Mesh _bbMesh;

    public GameMesh (final StillModel preCompiled)
    {
        _mesh = preCompiled;
        _path = null;
        _color = Color.WHITE;
        _isInternal = true;
        _texturePath = null;
        _textureColorFormat = null;

        _mesh.getBoundingBox (_bb);
    }

    public GameMesh (final String path)
    {
        _path = path;
        _color = Color.WHITE;
        _isInternal = true;
        _texturePath = null;
        _textureColorFormat = null;
    }

    public GameMesh (final String path, final boolean isInternal)
    {
        _path = path;
        _color = Color.WHITE;
        _isInternal = isInternal;
        _texturePath = null;
        _textureColorFormat = null;
    }

    public GameMesh (final String path, final boolean isInternal,
                     final String texturePath)
    {
        _path = path;
        _color = Color.WHITE;
        _isInternal = isInternal;
        _texturePath = texturePath;
        _textureColorFormat = Format.RGBA8888;
    }

    public GameMesh (final String path, final boolean isInternal,
                     final String texturePath, final String textureColorFormat)
    {
        _path = path;
        _color = Color.WHITE;
        _isInternal = isInternal;
        _texturePath = texturePath;
        _textureColorFormat = Format.valueOf (textureColorFormat);
    }

    public GameMesh (final String path, final Color color)
    {
        _path = path;
        _color = color;
        _isInternal = true;
        _texturePath = null;
        _textureColorFormat = null;
    }

    public GameMesh (final String path, final Color color,
                     final boolean isInternal)
    {
        _path = path;
        _color = color;
        _isInternal = isInternal;
        _texturePath = null;
        _textureColorFormat = null;
    }

    public GameMesh (final String path, final Color color,
                     final boolean isInternal, final String texturePath)
    {
        _path = path;
        _color = color;
        _isInternal = isInternal;
        _texturePath = texturePath;
        _textureColorFormat = Format.RGBA8888;
    }

    public GameMesh (final String path, final Color color,
                     final boolean isInternal, final String texturePath,
                     final String textureColorFormat)
    {
        _path = path;
        _color = color;
        _isInternal = isInternal;
        _texturePath = texturePath;
        _textureColorFormat = Format.valueOf (textureColorFormat);
    }

    public Mesh getBBMesh ()
    {
        if (_bbMesh != null) {
            _bbMesh.dispose ();
        }
        final Vector3 [] bbCorners = _bb.getCorners ();
        _bbMesh = new Mesh (true, 8, 36, new VertexAttribute (Usage.Position, 3, "a_position"));
        _bbMesh.setVertices (new float [] { bbCorners[0].x, bbCorners[0].y,
                                           bbCorners[0].z, bbCorners[1].x,
                                           bbCorners[1].y, bbCorners[1].z,
                                           bbCorners[2].x, bbCorners[2].y,
                                           bbCorners[2].z, bbCorners[3].x,
                                           bbCorners[3].y, bbCorners[3].z,
                                           bbCorners[4].x, bbCorners[4].y,
                                           bbCorners[4].z, bbCorners[5].x,
                                           bbCorners[5].y, bbCorners[5].z,
                                           bbCorners[6].x, bbCorners[6].y,
                                           bbCorners[6].z, bbCorners[7].x,
                                           bbCorners[7].y, bbCorners[7].z, });
        _bbMesh.setIndices (new short [] { 0, 1, 4, 1, 5, 4, 1, 2, 5, 2, 6, 5,
                                          2, 3, 6, 3, 7, 6, 3, 0, 7, 0, 4, 7,
                                          4, 5, 7, 5, 6, 7, 3, 2, 0, 2, 1, 0 });
        return _bbMesh;
    }

    public BoundingBox getBoundingBox ()
    {
        return _bb;
    }

    public Color getColor ()
    {
        return _color;
    }

    public StillModel getMesh ()
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
                    _mesh = ModelLoaderRegistry.loadStillModel (objFile, new ModelLoaderHints (_isVFlipped));
                    _mesh.getBoundingBox (_bb);
                    // Gdx.app.log (TAG, _path + ": BoundingBox - " +
                    // _bb.toString ());
                } catch (final Exception ex) {
                    Gdx.app.log (TAG, ex.getMessage ());
                }
            }
        }

        return _mesh;
    }

    public String getPath ()
    {
        return _path;
    }

    public Sound getSound ()
    {
        if (_sound == null) {
            if (_soundIsInternal) {
                _sound = Gdx.audio.newSound (Gdx.files.internal (_soundPath));
            } else {
                _sound = Gdx.audio.newSound (Gdx.files.external (_soundPath));
            }
        }
        return _sound;
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
                _texture = new Texture (objFile, _textureColorFormat, true);
                _texture.setFilter (TextureFilter.MipMap, TextureFilter.Linear);
                _isVFlipped = true;
            }
        }
        return _texture;
    }

    public boolean isInternal ()
    {
        return _isInternal;
    }

    public void setColor (final Color color)
    {
        _color = color;
    }

    public void setSound (final String path)
    {
        setSound (path, true);
    }

    public void setSound (final String path, final boolean isInternal)
    {
        _soundPath = path;
        _soundIsInternal = isInternal;
    }

    public BoundingBox transformBoundingBox (final Matrix4 transform)
    {
        _mesh.getBoundingBox (_bb);
        // Gdx.app.log (TAG, _path + ": BoundingBox - " + _bb.toString ());
        _bb.mul (transform);
        return _bb;
    }
}
