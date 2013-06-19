/**
 * @file: com.innovail.trouble.utils - AbstractParameters.java
 * @date: Jun 18, 2013
 * @author: berndmicweber
 */
package com.innovail.trouble.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public abstract class AbstractParameters implements Parameters
{
    private final Map <String, Object> _Parameters;

    /**
     * 
     */
    public AbstractParameters ()
    {
        _Parameters = new HashMap <String, Object> ();
    }

    /*
     * (non-Javadoc)
     * @see com.innovail.trouble.utils.Parameters#getBooleanParameter(java.lang.String
     * )
     */
    @Override
    public boolean getBooleanParameter (final String name)
    {
        if (_Parameters.containsKey (name)) {
            final Object tempVal = _Parameters.get (name);
            if (tempVal.getClass ().equals (Boolean.class)) {
                return ((Boolean) tempVal).booleanValue ();
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.innovail.trouble.utils.Parameters#getDoubleParameter(java.lang.String
     * )
     */
    @Override
    public double getDoubleParameter (final String name)
    {
        if (_Parameters.containsKey (name)) {
            final Object tempVal = _Parameters.get (name);
            if (tempVal.getClass ().equals (Double.class)) {
                return ((Double) tempVal).doubleValue ();
            }
        }
        return 0.0d;
    }

    /*
     * (non-Javadoc)
     * @see com.innovail.trouble.utils.Parameters#getFloatParameter(java.lang.String)
     */
    @Override
    public float getFloatParameter (final String name)
    {
        if (_Parameters.containsKey (name)) {
            final Object tempVal = _Parameters.get (name);
            if (tempVal.getClass ().equals (Float.class)) {
                return ((Float) tempVal).floatValue ();
            }
        }
        return 0.0f;
    }

    /*
     * (non-Javadoc)
     * @see com.innovail.trouble.utils.Parameters#getIntParameter(java.lang.String)
     */
    @Override
    public int getIntParameter (final String name)
    {
        if (_Parameters.containsKey (name)) {
            final Object tempVal = _Parameters.get (name);
            if (tempVal.getClass ().equals (Integer.class)) {
                return ((Integer) tempVal).intValue ();
            }
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see com.innovail.trouble.utils.Parameters#getParameter(java.lang.String)
     */
    @SuppressWarnings ("unchecked")
    @Override
    public <T> T getParameter (final String name, T object)
    {
        if (_Parameters.containsKey (name)) {
            final Object tempVal = _Parameters.get (name);
            if (tempVal.getClass ().equals (object.getClass ())) {
                object = (T) tempVal;
                return object;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.innovail.trouble.utils.Parameters#getStringParameter(java.lang.String
     * )
     */
    @Override
    public String getStringParameter (final String name)
    {
        if (_Parameters.containsKey (name)) {
            final Object tempVal = _Parameters.get (name);
            if (tempVal.getClass ().equals (String.class)) {
                return (String) tempVal;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.innovail.trouble.utils.Parameters#setParameter(java.lang.String,
     * boolean)
     */
    @Override
    public void setParameter (final String name, final boolean value)
    {
        _Parameters.put (name, Boolean.valueOf (value));
    }

    /*
     * (non-Javadoc)
     * @see com.innovail.trouble.utils.Parameters#setParameter(java.lang.String,
     * double)
     */
    @Override
    public void setParameter (final String name, final double value)
    {
        _Parameters.put (name, Double.valueOf (value));
    }

    /*
     * (non-Javadoc)
     * @see com.innovail.trouble.utils.Parameters#setParameter(java.lang.String,
     * float)
     */
    @Override
    public void setParameter (final String name, final float value)
    {
        _Parameters.put (name, Float.valueOf (value));
    }

    /*
     * (non-Javadoc)
     * @see com.innovail.trouble.utils.Parameters#setParameter(java.lang.String,
     * int)
     */
    @Override
    public void setParameter (final String name, final int value)
    {
        _Parameters.put (name, Integer.valueOf (value));
    }

    /*
     * (non-Javadoc)
     * @see com.innovail.trouble.utils.Parameters#setParameter(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void setParameter (final String name, final Object value)
    {
        _Parameters.put (name, value);
    }

    /*
     * (non-Javadoc)
     * @see com.innovail.trouble.utils.Parameters#setParameter(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void setParameter (final String name, final String value)
    {
        _Parameters.put (name, value);
    }

}
