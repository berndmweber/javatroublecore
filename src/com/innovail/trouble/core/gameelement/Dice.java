/**
 * @file:   com.innovail.trouble.core.gameelement - Dice.java
 * @date:   May 10, 2012
 * @author: bweber
 */
package com.innovail.trouble.core.gameelement;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

/**
 * 
 */
public class Dice {
    private static final String TAG = "Dice";
    public static final int[] faceValues = {1, 2, 3, 4, 5, 6};
    public static final float[][] faceAngles = {{-90.0f, 1.0f, 0.0f, 0.0f},
                                                   { 90.0f, 0.0f, 0.0f, 1.0f},
                                                   {  0.0f, 0.0f, 0.0f, 0.0f},
                                                   {180.0f, 1.0f, 0.0f, 0.0f},
                                                   {-90.0f, 0.0f, 0.0f, 1.0f},
                                                   { 90.0f, 1.0f, 0.0f, 0.0f}};
    
    private List <Integer> _currentFace;
    private int _numberOfDice;
    
    public Dice ()
    {
        _numberOfDice = 1;
        _currentFace = new ArrayList <Integer> ();
        createFaces ();
    }
    
    public Dice (int numberOfDice)
    {
        _numberOfDice = numberOfDice;
        _currentFace = new ArrayList <Integer> ();
        createFaces ();
    }
    
    private void createFaces ()
    {
        for (int dice = 0; dice < _numberOfDice; dice++) {
            _currentFace.add (1);
        }
    }
    
    public float[] getFaceAngle (int die)
    {
        /* We need to subtract one since the die faces start at 1 but the array starts at 0. */
        return faceAngles[_currentFace.get (die) - 1];
    }
    
    public int getNumberOfDice ()
    {
        return _numberOfDice;
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
                Gdx.app.log (TAG, "Die " + roll + " rolled: " + results[roll]);
            }
            return results;
        }
        return null;
    }
}
