/**
 * @file:   com.innovail.trouble.uicomponent - NumberSelector.java
 * @date:   May 23, 2012
 * @author: bweber
 */
package com.innovail.trouble.uicomponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import com.innovail.trouble.graphics.GameMesh;

/**
 * 
 */
public class NumberSelector extends MenuEntryMesh {
    private static GameMesh _leftArrow;
    private static GameMesh _rightArrow;
    private static GameMesh _minusSign;
    private static List <GameMesh> _numberField;
    
    private BoundingBox _leftArrowBB;
    private BoundingBox _rightArrowBB;
    
    private Vector3 _position = new Vector3 (Vector3.Zero);
    private Vector3 _leftSelectorOffset = new Vector3 (2.0f, 0.0f, 0.0f);
    private Vector3 _minusOffset = new Vector3 (0.5f, 0.0f, 0.0f);
    private Vector3 _numberOffset = new Vector3 (0.5f, 0.0f, 0.0f);
    private Vector3 _rightSelectorOffset = new Vector3 (0.5f, 0.0f, 0.0f);

    
    private final int _minNumber;
    private final int _maxNumber;
    private int _currentNumber = 0;

    /**
     * @param name
     * @param path
     * @param isInternal
     */
    public NumberSelector (final String name, final int minNumber,
                            final int maxNumber, final String path,
                            final boolean isInternal)
    {
        super (name, path, isInternal);
        _minNumber = minNumber;
        _maxNumber = maxNumber;
    }

    /**
     * @param name
     * @param path
     * @param color
     * @param isInternal
     */
    public NumberSelector (final String name, final int minNumber,
                            final int maxNumber, final String path,
                            final Color color, boolean isInternal)
    {
        super (name, path, color, isInternal);
        _minNumber = minNumber;
        _maxNumber = maxNumber;
        setAssetsColor (color);
    }

    /**
     * @param name
     * @param path
     * @param color
     */
    public NumberSelector (final String name, final int minNumber,
                            final int maxNumber, final String path,
                            final Color color)
    {
        super (name, path, color);
        _minNumber = minNumber;
        _maxNumber = maxNumber;
        setAssetsColor (color);
    }

    /**
     * @param name
     * @param path
     */
    public NumberSelector (final String name, final int minNumber,
                            final int maxNumber, final String path)
    {
        super (name, path);
        _minNumber = minNumber;
        _maxNumber = maxNumber;
    }
    
    public static void setLeftArrowMesh (final String path,
                                           final boolean isInternal)
    {
        if (_leftArrow == null) {
            _leftArrow = new GameMesh (path, isInternal);
        }
    }
    
    public static void setMinusMesh (final String path, final boolean isInternal)
    {
        if (_minusSign == null) {
            _minusSign = new GameMesh (path, isInternal);
        }
    }

    public static void setRightArrowMesh (final String path,
                                            final boolean isInternal)
    {
        if (_rightArrow == null) {
            _rightArrow = new GameMesh (path, isInternal);
        }
    }
    
    public static void addNumber (final int index, final String path,
                                    final boolean isInternal)
    {
        if (_numberField == null) {
            _numberField = new ArrayList <GameMesh> ();
        }
        if (_numberField.get (index) == null) {
            _numberField.add (index, new GameMesh (path, isInternal));
        }
    }
    
    private void setAssetsColor (final Color color)
    {
        if (_leftArrow != null) {
            _leftArrow.setColor (color);
        }
        if (_rightArrow != null) {
            _rightArrow.setColor (color);
        }
        if (_minusSign != null) {
            _minusSign.setColor (color);
        }
        if ((_numberField != null) && !_numberField.isEmpty ()) {
            Iterator <GameMesh> number = _numberField.iterator ();
            while (number.hasNext ()) {
                number.next ().setColor (color);
            }
        }
    }
    
    public void setPosition (final Vector3 position)
    {
        _position = position;
    }
    
    public BoundingBox getLeftArrowBB ()
    {
        return _leftArrowBB;
    }

    public BoundingBox getRightArrowBB ()
    {
        return _rightArrowBB;
    }
    
    public void hitLeftArrow ()
    {
        if (_currentNumber > _minNumber) {
            _currentNumber--;
        }
    }
    
    public void hitRightArrow ()
    {
        if (_currentNumber < _maxNumber) {
            _currentNumber++;
        }
    }
    
    public void render (final GL11 gl, final float delta)
    {
        
    }
}
