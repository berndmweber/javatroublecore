/**
 * @file: com.innovail.trouble.utils - Parameters.java
 * @date: Jun 18, 2013
 * @author: berndmicweber
 */
package com.innovail.trouble.utils;

/**
 * 
 */
public interface Parameters
{
    public boolean getBooleanParameter (String name);

    public double getDoubleParameter (String name);

    public float getFloatParameter (String name);

    public int getIntParameter (String name);

    public <T> T getParameter (String name, T object);

    public String getStringParameter (String name);

    public void setParameter (String name, boolean value);

    public void setParameter (String name, double value);

    public void setParameter (String name, float value);

    public void setParameter (String name, int value);

    public void setParameter (String name, Object value);

    public void setParameter (String name, String value);
}
