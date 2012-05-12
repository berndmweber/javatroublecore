/**
 * @file:   com.innovail.trouble.core.gameelement - Dice.java
 * @date:   May 10, 2012
 * @author: bweber
 */
package com.innovail.trouble.core.gameelement;

import java.util.Vector;

import com.badlogic.gdx.math.MathUtils;

/**
 * 
 */
public class Dice {
    public static final int[] faceValues = {1, 2, 3, 4, 5, 6};
    
    private Vector <Integer> _currentFace;
    private int _numberOfDice;
    
    public Dice ()
    {
        _numberOfDice = 1;
        _currentFace = new Vector<Integer> ();
        createFaces ();
    }
    
    public Dice (int numberOfDice)
    {
        _numberOfDice = numberOfDice;
        _currentFace = new Vector<Integer> ();
        createFaces ();
    }
    
    private void createFaces ()
    {
        for (int dice = 0; dice < _numberOfDice; dice++) {
            _currentFace.add (1);
        }
    }
    
    public int[] roll ()
    {
        int [] results = roll (_numberOfDice);
        for (int i = 0; i < results.length; i++) {
            _currentFace.set (i, results[i]);
        }
        return results;
    }

    public static int[] roll (int numberOfDice)
    {
        if (numberOfDice > 0) {
            int[] results = new int [numberOfDice];
            for (int roll = 0; roll < numberOfDice; roll++) {
                results[roll] = MathUtils.random (1, 6);
            }
            return results;
        }
        return null;
    }
}
