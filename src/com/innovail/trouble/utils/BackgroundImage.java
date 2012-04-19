/**
 * @file:   com.innovail.trouble.utils - BackgroundImage.java
 * @date:   Apr 18, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * 
 */
public class BackgroundImage {
    private final boolean _isInternal;
    private final int _width;
    private final int _height;
    private final String _path;
    
    private Object _imageObject;

    public BackgroundImage (String path, int width, int height, boolean isInternal)
    {
        _path = path;
        _width = width;
        _height = height;
        _isInternal = isInternal;
    }
    
    public BackgroundImage (String path, int width, int height)
    {
        _path = path;
        _width = width;
        _height = height;
        _isInternal = true;
    }
    
    public String getPath ()
    {
        return _path;
    }
    
    public int getWidth ()
    {
        return _width;
    }
    
    public int getHeight ()
    {
        return _height;
    }
    
    public boolean isInternal ()
    {
        return _isInternal;
    }
    
    public void setImageObject (Object image)
    {
        _imageObject = image;
    }
    
    public Object getImageObject ()
    {
        return _imageObject;
    }
    
    public Texture createTexture ()
    {
        Texture newImageTexture = null;
        if ((_path != null) && !_path.isEmpty ()) {
            if (_isInternal == true) {
                newImageTexture = new Texture (Gdx.files.internal (_path));
            } else {
                newImageTexture = new Texture (Gdx.files.external (_path));
            }
        }
        _imageObject = (Object) newImageTexture;
        
        return newImageTexture;
    }
}
