package net.veegl.input;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Control 
{

	public Control()
	{
		try {
			Keyboard.create();
			Mouse.create();
			
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
}
