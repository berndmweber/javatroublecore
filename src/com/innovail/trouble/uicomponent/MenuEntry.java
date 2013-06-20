/**
 * @file: com.innovail.trouble.uicomponent - MenuEntry.java
 * @date: Jun 19, 2013
 * @author: berndmicweber
 */
package com.innovail.trouble.uicomponent;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import com.innovail.trouble.core.ApplicationSettings;
import com.innovail.trouble.graphics.GameFont;
import com.innovail.trouble.graphics.GameFont.FontType;
import com.innovail.trouble.graphics.GameMesh;
import com.innovail.trouble.utils.Parameters;

/**
 * 
 */
public abstract class MenuEntry extends GameMesh
{
    public static enum TypeEnum {
        MESH, COUNT, COLOR,
    }

    public static TypeEnum getEnum (final String type)
    {
        return TypeEnum.valueOf (type.toUpperCase ());
    }

    protected final String _name;
    protected TypeEnum _type = TypeEnum.MESH;

    protected Vector3 _entryPosition;

    /**
     * 
     */
    public MenuEntry (final String [] name)
    {
        super (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (name[1]));
        _name = name[0];
    }

    public MenuEntry (final String [] name, final Parameters params)
    {
        super (ApplicationSettings.getInstance ().getGameFont (GameFont.typeToString (FontType.MESH)).getMeshFont ().createStillModel (name[1]));
        _name = name[0];
        processParams (params);
    }

    public String getName ()
    {
        return _name;
    }

    public TypeEnum getType ()
    {
        return _type;
    }

    public abstract boolean handleIntersect (Ray touchRay);

    public void processParams (final Parameters params)
    {}

    public abstract void render (final GL11 gl, final Vector3 menuOffset,
                                 final Color color);

    public abstract void setOffset (final Vector3 offsetPosition);
}
