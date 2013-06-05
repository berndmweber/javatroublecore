/**
 * @file:   com.innovail.trouble.core - Token.java
 * @date:   Apr 14, 2012
 * @author: bweber
 */
package com.innovail.trouble.core.gameelement;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/**
 * 
 */
public class Token {
    private static final int NUMBER_OF_STEPS = 20;
    
    private final Player _owner;
    private Spot _position;
    private Spot _potentialPosition;
    
    private Spot _oldPosition;
    private Spot _nextPosition;
    
    private boolean _isSelected = false;
    private boolean _isMoving = false;
    private boolean _doneMoving = false;
    
    private int _moveStepsLeft = 0;

    public Token (final Player player)
    {
        _owner = player;
    }
    
    public Player getOwner ()
    {
        return _owner;
    }
    
    public Spot getPosition ()
    {
        return _position;
    }
    
    public void setPosition (final Spot spot)
    {
        /* When tokens get assigned the first time. */
        if (_position != null) {
            _position.releaseToken ();
        }
        _position = spot;
        _position.positionToken (this);
    }
    
    public Spot getTargetPosition (final int moves)
    {
        Spot current = _position;
        for (int i = 0; i < moves; i++) {
            if (current != null) {
                current = current.getNextSpot (_owner);
            } else {
                break;
            }
        }
        return current;
    }
    
    public void setPotentialPosition (final Spot potential)
    {
        _potentialPosition = potential;
    }
    
    public void resetPotentialPosition ()
    {
        if (_potentialPosition != null) {
            _potentialPosition.setPotentialToken (null);
        }
        _potentialPosition = null;
    }
    
    public Spot getMoveNextPosition ()
    {
        return _nextPosition;
    }
    
    public Spot getMoveOldPosition ()
    {
        return _oldPosition;
    }
    
    public boolean isMoving ()
    {
        return _isMoving;
    }
    
    public Vector3 getCurrentMovePosition ()
    {
        Vector3 currentPosition = new Vector3 (_oldPosition.getPosition ());
        
        currentPosition.sub (_nextPosition.getPosition ());
        currentPosition.div ((float)NUMBER_OF_STEPS);
        currentPosition.mul (_moveStepsLeft);
        currentPosition.add (_nextPosition.getPosition ());
        
        return currentPosition;
    }
    
    public boolean doneMoving ()
    {
        if (_doneMoving) {
            _doneMoving = false;
            return true;
        }
        return false;
    }
    
    public void moveToStart ()
    {
        Spot current = _position;
        _oldPosition = _position;
        do {
            current = current.getNextSpot ();
        } while (!current.isStart ());
        _nextPosition = current;
        setPosition (current);
        setSelected (true);
        _isMoving = true;
        _moveStepsLeft = NUMBER_OF_STEPS;
    }
    
    public void moveToHome ()
    {
        Spot current = _position;
        _oldPosition = _position;
        final Iterator <Spot> spots = _owner.getOwnerSpots ().iterator ();
        while (spots.hasNext ()) {
            final Spot spot = spots.next ();
            if (spot.isHome () && (spot.getCurrentToken () == null)) {
                current = spot;
                break;
            }
        }
        _nextPosition = current;
        setPosition (current);
        setSelected (true);
        _isMoving = true;
        _moveStepsLeft = NUMBER_OF_STEPS;
    }
    
    public void moveTo (final int spots)
    {
        Spot current = _position;
        _oldPosition = _position;
        for (int i = 0; i < spots; i++) {
            current = current.getNextSpot (_owner);
            if (i == 0) {
                _nextPosition = current;
            }
        }
        setPosition (current);
        setSelected (true);
        _isMoving = true;
        _moveStepsLeft = NUMBER_OF_STEPS;
    }
    
    public void move ()
    {
        if (--_moveStepsLeft == 1) {
            if (!_nextPosition.equals (_position)) {
                _oldPosition = _nextPosition;
                _nextPosition = _nextPosition.getNextSpot (_owner);
                _moveStepsLeft = NUMBER_OF_STEPS;
            } else {
                setSelected (false);
                _isMoving = false;
                _doneMoving = true;
            }
        }
    }
    
    public boolean isLastMove ()
    {
        if (_nextPosition.equals (_position) && (_moveStepsLeft == NUMBER_OF_STEPS)) {
            return true;
        }
        return false;
    }
    
    public int getMoveStep ()
    {
        return _moveStepsLeft;
    }
    
    public Color getColor ()
    {
        return _owner.getColor ();
    }
    
    public void setSelected (boolean selected)
    {
        _isSelected = selected;
    }
    
    public boolean isSelected ()
    {
        return _isSelected;
    }
}
