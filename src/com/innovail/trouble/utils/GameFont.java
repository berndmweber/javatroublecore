/**
 * @file:   com.innovail.trouble.utils - GameFont.java
 * @date:   Apr 17, 2012
 * @author: bweber
 */
package com.innovail.trouble.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * 
 */
public class GameFont {
    private final String _fontFilePath;
    private final String _fontImagePath;
    private final boolean _isInternal;
    private boolean _flipFont = false;
    private BitmapFont _relatedFont = null;
    
    public GameFont (final String fontFilePath, final String fontImagePath,
                      final boolean isInternal)
    {
        _fontFilePath = fontFilePath;
        _fontImagePath = fontImagePath;
        _isInternal = isInternal;
    }
    
    public GameFont (final String fontFilePath, final String fontImagePath)
    {
        _fontFilePath = fontFilePath;
        _fontImagePath = fontImagePath;
        _isInternal = true;
    }
    
    public void flipFont ()
    {
        _flipFont = true;
    }
    
    public BitmapFont createBitmapFont ()
    {
        if (_relatedFont == null) {
            if (_isInternal) {
                _relatedFont =  new BitmapFont (Gdx.files.internal (_fontFilePath),
                                                Gdx.files.internal (_fontImagePath),
                                                _flipFont);
            } else {
                _relatedFont = new BitmapFont (Gdx.files.external (_fontFilePath),
                                               Gdx.files.external (_fontImagePath),
                                               _flipFont);
            }
        }
        return _relatedFont;
    }
    
    public BitmapFont getFont ()
    {
        return _relatedFont;
    }
    
    public boolean hasFont ()
    {
        if (_relatedFont != null) {
            return true;
        }
        return false;
    }
}
