package net.veegl.shader;

import java.io.File;
import static org.lwjgl.opengl.GL20.*;


public class VertexShader extends AbstractShaderObject
{

	public VertexShader(String shaderFile) {
		super(GL_VERTEX_SHADER, new File(shaderFile));
	}
	
}
