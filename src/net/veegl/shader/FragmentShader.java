package net.veegl.shader;

import java.io.File;
import static org.lwjgl.opengl.GL20.*;

public class FragmentShader extends AbstractShaderObject
{

	public FragmentShader(String shaderFile) {
		super(GL_FRAGMENT_SHADER, new File(shaderFile));
	}

}
