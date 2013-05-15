package net.veegl.model;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.Color;


public class Cartesian
{
	private static int cartesianDsplList = -1;
	
	
	private static void createCartesianDisplayList()
	{
		cartesianDsplList = glGenLists(1);
		
		Color blue   = new Color(Color.BLUE);
		Color yellow = new Color(Color.YELLOW);
		Color red    = new Color(Color.RED);
		
		glNewList(cartesianDsplList, GL_COMPILE);
		{
			
			glPushMatrix();
				glLoadIdentity();
				glLineWidth(1);
				
				// x
				glColor3ub(blue.getRedByte(), blue.getGreenByte(), blue.getBlueByte());
				glBegin(GL_LINES);
					glVertex3f(5, 0, 0);
					glVertex3f(-5, 0, 0);
				glEnd();
				
				// y
				glColor3ub(red.getRedByte(), red.getGreenByte(), red.getBlueByte());
				glBegin(GL_LINES);
					glVertex3f(0, 5, 0);
					glVertex3f(0, 0, 0);
				glEnd();
			
				// z
				glColor3ub(yellow.getRedByte(), yellow.getGreenByte(), yellow.getBlueByte());
				glBegin(GL_LINES);
					glVertex3f(0, 0, 5);
					glVertex3f(0, 0, -5);
				glEnd();
				
			glPopMatrix();
			
			
		}
		glEndList();
		
	}
	
	
	public static void render()
	{
		if(cartesianDsplList == -1)
			createCartesianDisplayList();
		
		glCallList(cartesianDsplList);
	}
	
}
