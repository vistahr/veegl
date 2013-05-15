package net.veegl.shader;

import static org.lwjgl.opengl.GL20.*;

import java.io.File;
import java.nio.ByteBuffer;

import net.veegl.system.FileUtil;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

public abstract class AbstractShaderObject
{

	private int shaderObject = 0;
	

	public AbstractShaderObject(int shaderType, File shaderFile)
	{
		createShader(shaderType);
		writeSource((FileUtil.fileToByteBuffer(shaderFile)));
		
		try {
			compile();
			
		} catch (ShaderException e) {
			e.printStackTrace();
		}
	}
	
	
	public int getShaderObject() {
		return shaderObject;
	}


	private void createShader(int shaderType)
	{
		shaderObject = glCreateShader(shaderType);
	}
	
	
	private void writeSource(ByteBuffer source)
	{
		glShaderSource(shaderObject, source);
	}
	
	
	private void compile() throws ShaderException
	{
		glCompileShader(shaderObject);
		
		if(ARBShaderObjects.glGetObjectParameteriARB(shaderObject, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) 
		{
			glDeleteShader(shaderObject);
			shaderObject = 0;
			
			throw new ShaderException("error while compile shaderfile ");
			
		}
	}
}
