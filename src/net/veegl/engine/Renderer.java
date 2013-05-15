package net.veegl.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.List;

import net.veegl.engine.mode.BufferModeEnum;
import net.veegl.engine.mode.RenderModeEnum;
import net.veegl.light.Light;
import net.veegl.model.Face;
import net.veegl.model.Model;
import net.veegl.model.Shadow;
import net.veegl.shader.Shader;
import net.veegl.util.ColorPicking;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.linearmath.Transform;



public class Renderer 
{
	
	private RenderModeEnum renderMode; // veegl rendermode
	private int openGLRenderMode;// opengl rendermode
	
	
	
	public Renderer()
	{
		System.out.println("GL_VERSION: " + glGetString(GL_VERSION) + ", GL_SHADING_LANGUAGE_VERSION: " + glGetString(GL_SHADING_LANGUAGE_VERSION)); // info
		
		glClearColor(0f, 0f, 0f, 0f);
		
		glClearStencil(0);
		
		glEnable(GL_CULL_FACE); // only backsides - cause of CCW
		glCullFace(GL_BACK);
		
		glDisable(GL_NORMALIZE);

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_STENCIL_TEST);
	}
	


	public RenderModeEnum getRenderMode()
	{
		return renderMode;
	}
	
	
	public void setRenderMode(RenderModeEnum mode)
	{
		if(mode == RenderModeEnum.TRIANGLE)
		{
			renderMode 		 = RenderModeEnum.TRIANGLE;
			openGLRenderMode = GL_TRIANGLES;
			
			glEnable(GL_LIGHTING);
			glEnable(GL_TEXTURE_2D);
			glEnable(GL_BLEND);
			
		} else if(mode == RenderModeEnum.SELECT)
		{
			renderMode 		 = RenderModeEnum.SELECT;
			openGLRenderMode = GL_TRIANGLES;
			
			glDisable(GL_LIGHTING);
			glDisable(GL_TEXTURE_2D);
		    glDisable(GL_BLEND);
			
			
		} else if(mode == RenderModeEnum.LINES) 
		{
			renderMode 		 = RenderModeEnum.LINES;
			openGLRenderMode = GL_LINES;
			
			glDisable(GL_LIGHTING);
			glDisable(GL_TEXTURE_2D);
		    glDisable(GL_BLEND);
			
		} else if(mode == RenderModeEnum.POINTS) 
		{
			renderMode 		 = RenderModeEnum.POINTS;
			openGLRenderMode = GL_POINTS;
			
			glDisable(GL_LIGHTING);
			glDisable(GL_TEXTURE_2D);
		    glDisable(GL_BLEND);
		}
	}
	
	
	/**
	 * sets colorpickingcolor with cpickshader
	 * @param model
	 */
	private void colorPicking(Model model)
	{
		Color mCol = model.getPickingColor();
		
		// set color -  only for colorpicking!
		if((model.isSelectable() || model.isHoverable()) && mCol != null)
		{
			// use colorpickingshader and set the modelcolor
			ColorPicking.getColorPickingShader().activate();
			
			glUniform3f(glGetUniformLocation(ColorPicking.getColorPickingShader().getShaderObject(), "color"), 
					ColorPicking.intColorToFloat(mCol.getRed()), 
					ColorPicking.intColorToFloat(mCol.getGreen()), 
					ColorPicking.intColorToFloat(mCol.getBlue()));
		}
	}
	
	
	/**
	 * Transform by Jbullet motionState & scale model by scaleSize
	 * @param model
	 */
	private void transformModel(Model model)
	{
		FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
		Transform transResult 	 = new Transform();
		float[] transFloat  	 = new float[16];
		
		// resize model
		glScalef(model.getScaleSize(), model.getScaleSize(), model.getScaleSize()); // TODO - no fixed pipeline
		
		/////////////////////////////////////
		// do transformation, if model is dynamic
		if(model.getBody() != null)
		{
			transResult.setIdentity();
			model.getBody().getMotionState().getWorldTransform(transResult);
			transResult.getOpenGLMatrix(transFloat);

			matrixBuffer.clear();
			matrixBuffer.put(transFloat);
			matrixBuffer.flip();
			
			glMultMatrix(matrixBuffer); // TODO OpenGL >= 3.0 uniform to shader
		}	
	}
	
	
	
	private Shadow generateShadowVolume(Model model, List<Light> lights)
	{
		Shadow shadow = new Shadow();
		
		for(Light l: lights)
		{
			Vector3f lightVec = new Vector3f(l.getPosition()[0], l.getPosition()[1], l.getPosition()[2]);
			
			
			for(Face face: model.getFaces())
			{
				Vector3f[] facePoints = new Vector3f[3];
				facePoints[0] = model.getVertices().get((int) face.getVertexIndicies().getX()-1);
				facePoints[1] = model.getVertices().get((int) face.getVertexIndicies().getY()-1);
				facePoints[2] = model.getVertices().get((int) face.getVertexIndicies().getZ()-1);
				

				////////////////////
				//// calc facenormal
				Vector3f[] vRes 		= new Vector3f[]{new Vector3f(), new Vector3f()};
				
				Vector3f.sub(facePoints[1], facePoints[0], vRes[0]);
				Vector3f.sub(facePoints[2], facePoints[0], vRes[1]);
				
				Vector3f n = new Vector3f();
				Vector3f.cross(vRes[0], vRes[1], n);
				
				n.normalise();
				
				
				// check if lightvector dont corresponds with vertex
				float corrAngle = Vector3f.dot(n, lightVec);
				if(corrAngle <= -5 || corrAngle >= 5)
					continue;

				Vector3f resVec = new Vector3f();
				
				int scale = 1000;
				for(Vector3f v: facePoints)
				{
					float[] vf = {v.getX(), v.getY(), v.getZ()};
					shadow.loadVertices(vf);
					Vector3f.sub(lightVec, n, resVec);
					resVec.negate();
					resVec.scale(scale);
					
					float[] vf2 = {resVec.getX(), resVec.getY(), resVec.getZ()};
					shadow.loadVertices(vf2);
				}
				

			}
		}
		
		return shadow;
	}
	
	
	
	private void renderShadowVolume(Shadow shadow)
	{
		if(shadow != null)
		{
			glBegin(GL_TRIANGLE_STRIP);
			{
				for(Vector3f v: shadow.getVertices())
				{
					glVertex3f(v.getX(), v.getY(), v.getZ());
				}
			}
			glEnd();
		}
	}

	
	/**
	 * MAIN rendermethod transform first all models. 
	 * 
	 * @param sceneNode
	 * @param mode
	 * @param bounds
	 */
	public void render(SceneNode sceneNode, RenderModeEnum mode, boolean bounds)
	{

		glMatrixMode(GL_MODELVIEW);
	
		for(Model model: sceneNode.getModels())
		{
			Shadow shadow = null;
			
			
			setRenderMode(mode);
			
			
			// TODO - do in shader - no fixed pipeline
			glLoadIdentity();
			transformModel(model);
			
			
			if(mode == RenderModeEnum.SELECT)
			{
				colorPicking(model);
				
			} else 
			{
				
				//////////////
				/// SHADERS
				
				// deactivate all shaders
				Shader.deactivateAll();
				
				// shaderactivation per model
				if(model.hasShader())
					model.getShader().activate();
				
				
				//////////////
				/// CALLABCKS
				
				// hovercallback
				if(model.isHovered() && model.getHoverCallback() != null) 
					model.getHoverCallback().callback();
				
				// selectcallback
				if(model.isSelected() && model.getSelectCallback() != null) 
					model.getSelectCallback().callback();
			}
			
			
			
			// generate shadows
			if(model.hasShadow() && mode != RenderModeEnum.SELECT)
				shadow = generateShadowVolume(model, sceneNode.getLights());
			
			
			
			// render vbo ------ Start
			if(model.getBufferMode() == BufferModeEnum.VBO_DYNAMIC) 
			{
				
				
				glEnableClientState(GL_VERTEX_ARRAY);
				glEnableClientState(GL_TEXTURE_COORD_ARRAY);
				glEnableClientState(GL_NORMAL_ARRAY);
				{

					glBindBuffer(GL_ARRAY_BUFFER, model.getVertexBufferName());
					
					int stride = 32;
					glVertexPointer(3, GL_FLOAT, stride, 0);
					glTexCoordPointer(2, GL_FLOAT, stride, 12);
					glNormalPointer(GL_FLOAT, stride, 20);
					
					glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, model.getVertexBufferIndexName());
					
					
					if(shadow != null)
					{
						glDrawElements(openGLRenderMode, model.getModelIndexBuffer().capacity(), GL_UNSIGNED_INT, 0);
						/*
						////////////////////////
						// Shadowtest
						
						// store current OpenGL state
						glPushAttrib(GL_DEPTH_BUFFER_BIT | GL_LIGHTING_BIT | GL_STENCIL_BUFFER_BIT);

						// draw the model with the light disabled
						glDisable(GL_LIGHTING);
						glDrawElements(openGLRenderMode, model.getModelIndexBuffer().capacity(), GL_UNSIGNED_INT, 0);
						glEnable(GL_LIGHTING);

						// store current OpenGL state
						glPushAttrib(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_POLYGON_BIT | GL_STENCIL_BUFFER_BIT);

						glColorMask(false, false, false, false); // do not write to the color buffer
						glDepthMask(false); // do not write to the depth (Z) buffer
						glEnable(GL_CULL_FACE); // cull faces (back or front)
						glEnable(GL_STENCIL_TEST); // enable stencil testing

						// set the reference stencil value to 0
						glStencilFunc(GL_ALWAYS, 0, ~0);

						// increment the stencil value on Z fail
						glStencilOp(GL_KEEP, GL_INCR, GL_KEEP);

						// draw only the back faces of the shadow volume
						glCullFace(GL_FRONT);
						renderShadowVolume(shadow);

						// decrement the stencil value on Z fail
						glStencilOp(GL_KEEP, GL_DECR, GL_KEEP);

						// draw only the front faces of the shadow volume
						glCullFace(GL_BACK);
						renderShadowVolume(shadow);

						// restore OpenGL state
						glPopAttrib();

						// re-draw the model with the light enabled only where
						// it has previously been drawn
						glDepthFunc(GL_EQUAL);

						// update the color only where the stencil value is 0
						glEnable(GL_STENCIL_TEST);
						glStencilFunc(GL_EQUAL, 0, ~0);
						glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);

						glDrawElements(openGLRenderMode, model.getModelIndexBuffer().capacity(), GL_UNSIGNED_INT, 0);

						// restore OpenGL state
						glPopAttrib();
						*/
						
					} else {
						glDrawElements(openGLRenderMode, model.getModelIndexBuffer().capacity(), GL_UNSIGNED_INT, 0);
					}

				}
				glDisableClientState(GL_VERTEX_ARRAY);
				glDisableClientState(GL_TEXTURE_COORD_ARRAY);
				glDisableClientState(GL_NORMAL_ARRAY);
				
				// reset bindings
				glBindBuffer(GL_ARRAY_BUFFER, 0);
				glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			}
			
			
			
			// reset modelstate every frame, when not in selectmode
			if(mode != RenderModeEnum.SELECT)
				model.resetModelState();		
		}
	}
	

	public void updateViewport(int width, int height)
	{
		glViewport(0, 0, width, height);
	}

	
	public void clearBuffers()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	}
	
	
	public static void showGLErrors()
	{
		int errorCode = glGetError();
		if (errorCode != GL_NO_ERROR)
			System.out.println("GL_ERROR " + errorCode);
	}
	
}
