package net.veegl.util;

import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

public class Camera 
{
	
	private float ratio;
	private float minZ;
	private float maxZ;
	private int viewAngle;
	
    private Vector3f positionVec = null;
    
    private float yaw   = 0.0f;
    private float pitch = 0.0f;
	
    
    
	public Camera(int viewAngle, float ratio, float minZ, float maxZ, float[] position)
	{
		positionVec = new Vector3f(position[0], position[1], position[2]);
		
		this.ratio 		= ratio;
		this.viewAngle 	= viewAngle;
		this.maxZ 		= maxZ;
		this.minZ 		= minZ;
	}
	
	
	private void updatePerspective()
	{
		GLU.gluPerspective(viewAngle, ratio, minZ, maxZ);
	}	

	
	public void changeVisualRange(float minZ, float maxZ)
	{
		this.maxZ = maxZ;
		this.minZ = minZ;
	}
	
	
	public void changeViewAngle(int viewAngle) 
	{
		this.viewAngle = viewAngle;
	}
	
	
	public Vector3f getPosition() {
		return positionVec;
	}

	
	public void yaw(float amount)
	{
	    yaw += amount;
	}
	 

	public void pitch(float amount)
	{
	    pitch -= amount;
	}
	
	
	public void walkForward(float distance)
	{
	    positionVec.x -= distance * (float)Math.sin(Math.toRadians(yaw));
	    positionVec.z += distance * (float)Math.cos(Math.toRadians(yaw));
	}
	 
	
	public void walkBackwards(float distance)
	{
	    positionVec.x += distance * (float)Math.sin(Math.toRadians(yaw));
	    positionVec.z -= distance * (float)Math.cos(Math.toRadians(yaw));
	}
	 
	
	public void strafeLeft(float distance)
	{
	    positionVec.x -= distance * (float)Math.sin(Math.toRadians(yaw-90));
	    positionVec.z += distance * (float)Math.cos(Math.toRadians(yaw-90));
	}
	 
	
	public void strafeRight(float distance)
	{
	    positionVec.x -= distance * (float)Math.sin(Math.toRadians(yaw+90));
	    positionVec.z += distance * (float)Math.cos(Math.toRadians(yaw+90));
	}
	
	
	public void update()
    {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		
		updatePerspective();
		
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        
        glTranslatef(positionVec.x, positionVec.y, positionVec.z);
        

    }
	
}