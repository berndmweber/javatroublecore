/**
 * @file:   com.innovail.trouble.core - TroubleScreen.java
 * @date:   Apr 15, 2012
 * @author: bweber
 */
package com.innovail.trouble.screen;

import com.badlogic.gdx.Screen;

/**
 * 
 */
public abstract interface TroubleScreen extends Screen {

    void createInputProcessor ();
    
    String getState ();
}
