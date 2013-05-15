package net.veegl.util;

import java.util.HashMap;
import java.util.Map;

import net.veegl.model.Model;
import net.veegl.shader.Shader;

import org.lwjgl.util.Color;

public class ColorPicking 
{
	
	
	private static Map<Color, Model> pickColors = new HashMap<Color, Model>();
	
	private static Shader colorPickingShader;
	
	
	public static Shader getColorPickingShader() {
		return colorPickingShader;
	}


	public static void setColorPickingShader(Shader colorPickingShader) {
		ColorPicking.colorPickingShader = colorPickingShader;
	}


	// TODO - not unique
	private static Color generateUniqueColor()
	{
		int r, g, b;

		// rgb values between 2 and 254. basecolors excluded
		r = (int) (Math.random() * 253) + 1;
		g = (int) (Math.random() * 253) + 1;
		b = (int) (Math.random() * 253) + 1;
		
		return new Color(r, g, b);
	}
	
	
	/**
	 * connects a selectable model with an unique pickingcolor
	 */
	public static void connect(Model model)
	{
		if(model.isSelectable() || model.isHoverable())
		{
			Color uCol = generateUniqueColor();
			
			pickColors.put(uCol, model);
			model.setPickingColor(uCol);
		}
			
	}
	
	
	public static boolean hasModel(Color pickingColor)
	{
		if(pickColors.containsKey(pickingColor))
			return true;
		
		return false;
	}
	
	
	public static Model getModel(Color pickingColor)
	{
		if(hasModel(pickingColor))
			return pickColors.get(pickingColor);
		
		return null;
	}
	
	
	public static float intColorToFloat(int color)
	{
		return (float) (color/255f);
	}
	
}
