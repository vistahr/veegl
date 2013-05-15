package net.veegl;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector3f;

import net.veegl.display.Window;
import net.veegl.engine.Physic;
import net.veegl.engine.Renderer;
import net.veegl.engine.SceneNode;
import net.veegl.engine.mode.BufferModeEnum;
import net.veegl.engine.mode.RenderModeEnum;
import net.veegl.engine.mode.ShapeModeEnum;
import net.veegl.light.Light;
import net.veegl.model.Cartesian;
import net.veegl.model.LoaderException;
import net.veegl.model.Model;
import net.veegl.model.OBJLoader;
import net.veegl.model.callback.ICallback;
import net.veegl.shader.FragmentShader;
import net.veegl.shader.Shader;
import net.veegl.shader.VertexShader;
import net.veegl.util.Camera;
import net.veegl.util.ColorPicking;
import net.veegl.util.Timer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.Color;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.collision.shapes.CylinderShapeZ;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;



public class BilliardNew 
{
	
	private SceneNode sceneNode;
	private Renderer renderEngine;
	private Physic physicEngine;
	private Window window;
	private Camera camera;
	private Timer timer;
	private Light mainLight;
	
	float dt = 0.0f;
	float dx = 0.0f;
    float dy = 0.0f;
    
	float mouseSensitivity = 0.05f;
    float movementSpeed = 10.0f;
    
    
    private Color clickedColor;
    
    
    private boolean run = false;
    private boolean grabMode = false;
    
	private BilliardNew()
	{
		// window setup
		window = new Window();
		window.setLwjglLibraryPath("lib/lwjgl/native/");
		window.createWindow("VeeGL Engine", new DisplayMode(800, 600), true, false); // (name, mode, resize, fullscreen)
		//window.createWindow("VeeGL Engine", Display.getDesktopDisplayMode(), false, true);
		
		// timer
		timer = new Timer();
		
		// engine setup
		renderEngine = new Renderer();
		
		// physics setup
		Vector3f gravity = new Vector3f(0, -10, 0);
		physicEngine = new Physic(gravity);
		
		// camera
		camera = new Camera(45, window.getRatio(), 0.1f, 100f, new float[]{2, -3, -15});

		// node holds all scenemodels
		sceneNode = new SceneNode();
		
		// sunlight
		mainLight = new Light(GL_LIGHT0, new float[]{0f, 30f, 10f, 0f}, new float[]{0.55f, 1, 0, 0});
		sceneNode.addLight(mainLight);
	}
	
	
	
	private void initApplication()
	{
		
		Shader defaultShader = new Shader();
		Shader colorPickingShader = new Shader();
		
		defaultShader.addShader(new VertexShader("res/shader/default/default.vert"));
		defaultShader.addShader(new FragmentShader("res/shader/default/default.frag"));
		defaultShader.link();
		
		
		colorPickingShader.addShader(new VertexShader("res/shader/colorpicking/cpick.vert"),new FragmentShader("res/shader/colorpicking/cpick.frag"));
		colorPickingShader.link();
		ColorPicking.setColorPickingShader(colorPickingShader);
		
		
		
		///////////////////
		// generate table
		float[] billiardVert      = {-30f, 0f, -30f, -30f, 0f, 30f,  30f, 0f, 30f,  30f, 0f, -30f};
		float[] faceVertIndexes   = {1, 2, 4,   2, 3, 4};
		Model billiardTable = new Model(billiardVert, faceVertIndexes, false);
		RigidBody billiardBody = physicEngine.createRigidBody(new BoxShape(new Vector3f(30, 0, 30)), new Vector3f(0, -1, 0), 0, new Vector3f(0, 0, 0));
		billiardTable.setBody(billiardBody);
		billiardTable.setBuffer(BufferModeEnum.VBO_DYNAMIC);
		billiardTable.setShadow(true);
		//billiardTable.setShader(defaultShader);
		
		// add to node
		sceneNode.addModel(billiardTable);
		
		
		///////////////////
		// generate balls
		int ballCount = 50;
		
		
		Model ballModel = null;
		try 
		{
			ballModel = OBJLoader.loadModel(new File("res/models/ball.obj"));
			
		} catch (LoaderException e) {
			e.printStackTrace();
		}
		
		ballModel.toIdentity();
		ballModel.setBuffer(BufferModeEnum.VBO_DYNAMIC);
		//ballModel.setShader(defaultShader);
		
		ballModel.setShadow(true);
		
		ballModel.setHoverable(true, new ICallback() {
			@Override
			public void callback() {
				renderEngine.setRenderMode(RenderModeEnum.LINES);
			}
		});
		
		
		for(int i=0; i<(ballCount*2); i+=2)
		{
			Model ballModelTmp = new Model(ballModel);
			
			ColorPicking.connect(ballModelTmp);

			// physics
			RigidBody ballBody = physicEngine.createRigidBody(new SphereShape(1), // shape
																new Vector3f((int)(Math.random()*5), 5+i, (int)(Math.random()*5)), // position
																10f,  // mass
																new Vector3f(0, 0, 0) // inertia
																);
			ballBody.setDamping(0.5f, 0.5f);
			ballBody.setFriction(0.5f);
			ballBody.setRestitution(0.3f);
			
			ballModelTmp.setBody(ballBody);
			
			// add to node
			sceneNode.addModel(ballModelTmp);
		}
		

		
		
		///////////////////
		// generate boxes
		int boxCount = 50;
		 
		Model cube = null;
		try {
			cube = OBJLoader.loadModel(new File("res/models/cube.obj"));
			
		} catch (LoaderException e1) {
			e1.printStackTrace();
		}
		cube.setBuffer(BufferModeEnum.VBO_DYNAMIC);
		cube.setShadow(true);
		
		cube.setHoverable(true, new ICallback() {
			@Override
			public void callback() {
				renderEngine.setRenderMode(RenderModeEnum.POINTS);
			}
		});
		
		
		cube.toIdentity();
		

		
		for(int i=0; i<(boxCount*2); i+=2)
		{
			Model cubeTmp = new Model(cube);
			
			ColorPicking.connect(cubeTmp);

			// physics
			RigidBody cubeBody = physicEngine.createRigidBody(new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f)), // shape
																new Vector3f((int)(Math.random()*5), 5+i, (int)(Math.random()*5)), // position
																3f, // mass
																new Vector3f(0, 0, 0)); // inertia
			cubeBody.setDamping(0.5f, 0.5f);
			cubeBody.setFriction(0.5f);
			cubeBody.setRestitution(0.3f);
			
			cubeTmp.setBody(cubeBody);
			
			// add to node
			sceneNode.addModel(cubeTmp);
		}
		
		
		///////////////////
		// Cylinder
		
		int cylinderCount = 50;
		
		Model cylinder = null;
		try {
			cylinder = OBJLoader.loadModel(new File("res/models/cylinder.obj"));
			
		} catch (LoaderException e1) {
			e1.printStackTrace();
		}
		cylinder.setBuffer(BufferModeEnum.VBO_DYNAMIC);
		cylinder.setShadow(true);
		
		cylinder.setHoverable(true, new ICallback() {
			@Override
			public void callback() {
				renderEngine.setRenderMode(RenderModeEnum.POINTS);
			}
		});
		
		// TODO --> 
		//Map<String, Float> dim = cylinder.calcDimensions();
		//physicEngine.createRigidBody(ShapeModeEnum.CYLINDER
		
		cylinder.toIdentity();
		
		cylinder.setScaleSize(2);
		
		for(int i=0; i<(cylinderCount*2); i+=2)
		{
			Model cylinderTmp = new Model(cylinder);
			
			ColorPicking.connect(cylinderTmp);

			// physics
			RigidBody cylinderBody = physicEngine.createRigidBody(new CylinderShape(new Vector3f(0.2f, 0.2f, 1f)), // shape
																new Vector3f((int)(Math.random()*5), 5+i, (int)(Math.random()*5)), // position
																3f, // mass
																new Vector3f(0, 0, 0)); // inertia
			cylinderBody.setDamping(0.5f, 0.5f);
			cylinderBody.setFriction(0.5f);
			cylinderBody.setRestitution(0.3f);
			
			cylinderTmp.setBody(cylinderBody);
			
			// add to node
			sceneNode.addModel(cylinderTmp);
		}		
		
		
		
		
		
		// TODO Control
		try {
			Mouse.create();
			Keyboard.create();
			
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		Mouse.setGrabbed(true);
	}
	
	
	public void start()
	{
		initApplication();
		
		while (true)
		{
			renderEngine.clearBuffers();
			
			// next frame
			if(run == true)
				physicEngine.stepPhysicEngine(timer.getFps());		
	
			// render for selectmode
			renderEngine.render(sceneNode, RenderModeEnum.SELECT, false);

			inputControl();
			
			
			renderEngine.clearBuffers();
			renderEngine.render(sceneNode, RenderModeEnum.TRIANGLE, false);
			
			// render static meshes
			//renderCartesian();
			
			
			if (Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				break;
			
			
			
			// update viewport for resizing
			renderEngine.updateViewport(Display.getWidth(), Display.getHeight());
			
			// tick the timer to calc the real fps
			timer.tick();
			
			// errorhandling
			Renderer.showGLErrors();
			
			// show and swap buffers with 60frames
			window.refresh(60);	
			
			Display.setTitle("" + timer.getFps());
		}

		window.close();
	}
	
	
	private void renderCartesian()
	{
		glMatrixMode(GL_MODELVIEW);
		Cartesian.render();
	}
	

	private void inputControl()
	{
        
		// colorpicking
		ByteBuffer pixels = BufferUtils.createByteBuffer(12);
		glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL_RGB , GL_UNSIGNED_BYTE, pixels);
		pixels.rewind();
		clickedColor = new Color(pixels.get(), pixels.get(), pixels.get());
		
		// TODO
		Model hovModel = ColorPicking.getModel(clickedColor);
		if(hovModel != null) 
		{
			hovModel.setHovered(true);
			
			if(Mouse.isButtonDown(0) && !hovModel.isSelected()) 
			{
				hovModel.setSelected(true);
				
			} else if(Mouse.isButtonDown(0) && hovModel.isSelected()) 
			{
				hovModel.setSelected(false);
			}
				
		}
		
		//////////////////////////////
		// Single Keyboard Eventclicks
		while (Keyboard.next()) 
		{
		    if (Keyboard.getEventKeyState()) 
		    {
		        // pause
		        if (Keyboard.getEventKey() == Keyboard.KEY_P)
		        	run = !run;
		        
		        // get mouse control
		        if (Keyboard.getEventKey() == Keyboard.KEY_G)
		        {
		        	grabMode = !grabMode;
		        	Mouse.setGrabbed(!Mouse.isGrabbed());
		        }
		        	
		    }
		}
		
		
		//////////////////////////
		/// Movement
		
		
        dt = (timer.getDelta()) / 1000f;
        
        if (Keyboard.isKeyDown(Keyboard.KEY_W))//move forward
        {
            camera.walkForward(movementSpeed * dt);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S))//move backwards
        {
            camera.walkBackwards(movementSpeed  * dt);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A))//strafe left
        {
            camera.strafeLeft(movementSpeed * dt);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D))//strafe right
        {
            camera.strafeRight(movementSpeed * dt);
        }
        
        
        dx = Mouse.getDX();
        dy = Mouse.getDY();
        
        // move mouse only without grabmode(pointer=false)
        if(!grabMode)
        {
        	camera.yaw(dx * mouseSensitivity);
            camera.pitch(dy * mouseSensitivity);
        }
        
             
        camera.update();
	}
	
	
	public static void main(String... args)
	{
		BilliardNew app = new BilliardNew();
		app.start();
	}
	
}
