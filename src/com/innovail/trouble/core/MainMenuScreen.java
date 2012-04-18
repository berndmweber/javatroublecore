/**
 * @file:   com.innovail.trouble.core - MenuScreen.java
 * @date:   Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

/**
 * 
 */
public class MainMenuScreen extends TroubleScreen {
    private static String TAG = "MainMenuScreen";
    private final BitmapFont _menuFont;
    private final SpriteBatch _spriteBatch;
    private final Matrix4 _viewMatrix;
    private final Matrix4 _transformMatrix;
    
    public MainMenuScreen ()
    {
        Gdx.app.log (TAG, "MainMenuScreen()");
        _menuFont = ApplicationSettings.getInstance ().getGameFont ("mainMenu").createBitmapFont ();
        _spriteBatch = new SpriteBatch ();
        _viewMatrix = new Matrix4();
        _transformMatrix = new Matrix4();
    }
    
    @Override
    public void render (float delta)
    {
        //Gdx.app.log (TAG, "MainMenuScreen - render() - delta: " + delta);
        //Gdx.gl.glClearColor (0.2f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClearColor (0.9f, 0.9f, 0.9f, 1.0f);
        Gdx.gl.glClear (GL10.GL_COLOR_BUFFER_BIT);
        _viewMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
        _spriteBatch.setProjectionMatrix(_viewMatrix);
        _spriteBatch.setTransformMatrix(_transformMatrix);
        _spriteBatch.begin();
        _spriteBatch.disableBlending();
        _spriteBatch.setColor(Color.WHITE);
        //_spriteBatch.draw(background, 0, 0, 480, 320, 0, 0, 512, 512, false, false);
        _spriteBatch.enableBlending();
        //_spriteBatch.draw(logo, 0, 320 - 128, 480, 128, 0, 0, 512, 256, false, false);
        _spriteBatch.setBlendFunction(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        String text = "~`1!2@3#4$5%6^7&8*9(0)-_=+qQwWeErRtTyYuUiIoOpP[{]}\\|aAsSdDfFgGhHjJkKlL;:'\"zZxXcCvVbBnNmM,<.>/?";
        float textWidth = _menuFont.getBounds(text).width;
        _menuFont.draw (_spriteBatch, text,
                        Gdx.graphics.getWidth () / 2 - textWidth / 2,
                        Gdx.graphics.getHeight () / 2);
        //font.draw(spriteBatch, text, 240 - width / 2, 128);
        _spriteBatch.end();
    }
}
