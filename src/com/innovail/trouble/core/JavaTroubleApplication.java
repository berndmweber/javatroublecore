/**
 * @file:   com.innovail.trouble.core - JavaTroubleApplication.java
 * @date:   Feb 26, 2012
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;

/**
 * 
 */
public class JavaTroubleApplication implements ApplicationListener {
    private Random rand;
    
    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#create()
     */
    @Override
    public void create () {
        rand = new Random();
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
     */
    @Override
    public void resize (int width, int height) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#render()
     */
    @Override
    public void render () {
        Gdx.gl.glClearColor (rand.nextFloat (), rand.nextFloat (), rand.nextFloat (), 1.0f);
        Gdx.gl.glClear (GL10.GL_COLOR_BUFFER_BIT);
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#pause()
     */
    @Override
    public void pause () {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#resume()
     */
    @Override
    public void resume () {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#dispose()
     */
    @Override
    public void dispose () {
        // TODO Auto-generated method stub

    }

}
