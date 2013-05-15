package net.veegl.light;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import javax.vecmath.Quat4f;

import net.veegl.util.ColorPicking;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.bulletphysics.linearmath.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;


public class Light 
{
	
	private FloatBuffer ligthBuffer = BufferUtils.createFloatBuffer(4);
	
	private float[] position;
	private float[] rotation;
	
	private int lightNumber;
	
	private boolean enable;
	
	
	private float[] ambient; // TODO
	private float[] diffuse; // TODO
	private float[] specular; // TODO
	

	
	/**
	 * Sets the positionvector and the rotation degrees and the angle. The degrees are
	 * divided with 100.
	 * @param lightNumber
	 * @param position
	 * @param rotation
	 */
	public Light(int lightNumber, float[] position, float[] rotation)
	{
		this.lightNumber = lightNumber;
		
		setLightPosition(position, rotation);

		init();
		setUp();
		setEnable(true); // default
	}
	
	
	private void setLightPosition(float[] position, float[] rotation)
	{
		Matrix4f transMtrx = new Matrix4f();
		transMtrx.setIdentity();
		
		transMtrx.rotate(rotation[0], new Vector3f(rotation[1], rotation[2], rotation[3]));
		
		Vector4f posVec = new Vector4f(); 
		Matrix4f.transform(transMtrx, new Vector4f(position[0], position[1], position[2], 1), posVec);
		
		this.position	 = new float[] {posVec.getX(), posVec.getY(), posVec.getZ(), position[3]};
		this.rotation	 = rotation;
	}
	
	
	private void init()
	{
		glMatrixMode(GL_MODELVIEW);
		
		glPushMatrix();
		glLoadIdentity();
		
		// transformation
		glRotatef(rotation[0], rotation[1], rotation[2], rotation[3]); 
		
		// position
		ligthBuffer.clear();
		ligthBuffer.put(position);
		ligthBuffer.flip();
		glLight(lightNumber, GL_POSITION, ligthBuffer);
		
		glPopMatrix();
	}
	
	
	private void setUp()
	{
		ligthBuffer.clear();
		ligthBuffer.put(new float[] {0.0f, 0.0f, 0.0f, 0.0f});
		ligthBuffer.flip();
		glLight(lightNumber, GL_AMBIENT, ligthBuffer);
		
		// TODO Intbuffer
		ligthBuffer.clear();
		ligthBuffer.put(new float[] {ColorPicking.intColorToFloat(245), ColorPicking.intColorToFloat(238), ColorPicking.intColorToFloat(220), 0});
		ligthBuffer.flip();
		glLight(lightNumber, GL_DIFFUSE, ligthBuffer); 
		
		ligthBuffer.clear();
		ligthBuffer.put(new float[] {0f, 0f, 0f, 0f});
		ligthBuffer.flip();
		glLight(lightNumber,GL_SPECULAR,ligthBuffer);
	}


	public void setEnable(boolean enable) 
	{
		this.enable = enable;
		
		if(enable) 
		{
			glEnable(lightNumber);
			
		} else 
		{
			glDisable(lightNumber);
		}
		
	}


	public boolean isEnable() 
	{
		return enable;
	}


	public float[] getPosition() {
		return position;
	}
	
	
	
}
