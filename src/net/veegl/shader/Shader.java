package net.veegl.shader;


import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;


public class Shader
{

	private int shaderObject; // programobjct
	

	public Shader()
	{
		shaderObject = glCreateProgram();
	}
	
	/**
	 * Get Shaderprogramobject
	 * @return
	 */
	public int getShaderObject() 
	{
		return shaderObject;
	}	
	
	
	public void addShader(VertexShader vertShader, FragmentShader fragShader)
	{
		attach(vertShader, fragShader);
	}

	
	public void addShader(VertexShader vertShader)
	{
		attach(vertShader, null);
	}

	
	public void addShader(FragmentShader fragShader)
	{
		attach(null, fragShader);
	}
	
	
	/**
	 * attach the vertex- & fragmentshader with the shaderprogram
	 * @param vertShader
	 * @param fragShader
	 * @throws ShaderException
	 */
	private void attach(VertexShader vertShader, FragmentShader fragShader)
	{
		
		if(vertShader != null && vertShader.getShaderObject() != 0)
			glAttachShader(shaderObject, vertShader.getShaderObject()); 
		
		if(fragShader != null && fragShader.getShaderObject() != 0)
			glAttachShader(shaderObject, fragShader.getShaderObject()); 
		
		if((vertShader == null && fragShader == null))
			System.out.println("no shader for linking found");
	}

	
	/**
	 * activate shader in GPU
	 * @throws ShaderException 
	 */
	public void activate()
	{
		glValidateProgram(shaderObject);
		
		if(glGetProgram(shaderObject, GL_LINK_STATUS) == GL_FALSE) 
			System.out.println("shader validation failed. not link()-ed?");
		
		glUseProgram(shaderObject);
	}
	
	
	/**
	 * Link the shaderprogram. Only once!
	 * @throws ShaderException
	 */
	public void link()
	{
		glLinkProgram(shaderObject);
		
		if(glGetProgram(shaderObject, GL_LINK_STATUS) == GL_FALSE)
			System.out.println("link shader error");
	}

	
	/**
	 * deactivate all shaders
	 */
	public static void deactivateAll()
	{
		glUseProgram(0);
	}
	

	
}
