/**
 * @file:   com.innovail.trouble.core - MenuScreen.java
 * @date:   Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.io.InputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.innovail.trouble.utils.BackgroundImage;

/**
 * 
 */
public class MainMenuScreen extends TroubleScreen {
    private static final String TAG = "MainMenuScreen";
    private static final String _AppPartName = "mainMenu";
    
    private final BitmapFont _menuFont;
    private final SpriteBatch _spriteBatch;
    private final BackgroundImage _backgroundImage;
    private final Matrix4 _viewMatrix;
    private final Matrix4 _transformMatrix;
    private Mesh _logo;
    
    private FileHandle _objFile;
    
    public MainMenuScreen ()
    {
        Gdx.app.log (TAG, "MainMenuScreen()");
        _menuFont = ApplicationSettings.getInstance ().getGameFont (_AppPartName).createBitmapFont ();
        _spriteBatch = new SpriteBatch ();
        _backgroundImage = ApplicationSettings.getInstance ().getBackgroundImage (_AppPartName);
        _backgroundImage.createTexture ().setFilter (TextureFilter.Linear, TextureFilter.Linear);
        _viewMatrix = new Matrix4 ();
        _transformMatrix = new Matrix4 ();
        try {
            _objFile = Gdx.files.internal("trouble_logo_3d.obj");
            InputStream in = _objFile.read();
            _logo = ObjLoader.loadObj(in, false);
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void render (float delta)
    {
        //Gdx.app.log (TAG, "MainMenuScreen - render() - delta: " + delta);
        Gdx.gl.glClearColor (0.2f, 0.0f, 0.0f, 1.0f);
        //Gdx.gl.glClearColor (0.9f, 0.9f, 0.9f, 1.0f);
        Gdx.gl.glClear (GL10.GL_COLOR_BUFFER_BIT);
        _viewMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
/*        _spriteBatch.setProjectionMatrix(_viewMatrix);
        _spriteBatch.setTransformMatrix(_transformMatrix);
        _spriteBatch.begin();
        _spriteBatch.disableBlending();
        _spriteBatch.setColor(Color.WHITE);
        _spriteBatch.draw((Texture)_backgroundImage.getImageObject (),
                          0, 0,
                          Gdx.graphics.getWidth (),
                          Gdx.graphics.getHeight (),
                          0, 0,
                          _backgroundImage.getWidth (),
                          _backgroundImage.getHeight (),
                          false, false);
        _spriteBatch.enableBlending();
        //_spriteBatch.draw(logo, 0, 320 - 128, 480, 128, 0, 0, 512, 256, false, false);
        _spriteBatch.setBlendFunction(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        String text = "~`1!2@3#4$5%6^7&8*9(0)-_=+qQwWeErRtTyYuUiIoOpP[{]}\\|aAsSdDfFgGhHjJkKlL;:'\"zZxXcCvVbBnNmM,<.>/?";
        float textWidth = _menuFont.getBounds(text).width;
        _menuFont.draw (_spriteBatch, text,
                        Gdx.graphics.getWidth () / 2 - textWidth / 2,
                        Gdx.graphics.getHeight () / 2);
        //font.draw(spriteBatch, text, 240 - width / 2, 128);
        _spriteBatch.end();*/
        _logo.render (GL10.GL_TRIANGLES);
    }
}
