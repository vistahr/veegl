package net.veegl.display;

import net.veegl.system.SystemUtil;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;


public class Window 
{
	
	public void createWindow(String title, DisplayMode mode, boolean resizeable, boolean fullscreen, PixelFormat pixelformat)
	{
		try
		{
			Display.setDisplayMode(mode);
			Display.setTitle(title);
			Display.setInitialBackground(0f, 0f, 0f);
			Display.setResizable(resizeable);
			
			Display.setFullscreen(fullscreen);
			Display.setVSyncEnabled(fullscreen);
			
			Display.create(new PixelFormat(0, 24, 8));
		} 
		catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}	
	
	public void createWindow(String title, DisplayMode mode, boolean resizeable, boolean fullscreen)
	{
		createWindow(title, mode, resizeable, fullscreen, new PixelFormat(0, 24, 8));
	}

	
	public void setLwjglLibraryPath(String libPath)
	{
		String nativeFolder = null;
		
		if(SystemUtil.isMac()) {
			nativeFolder = "macosx";
			
		} else if(SystemUtil.isWindows()) {
			nativeFolder = "windows";
			
		} else if(SystemUtil.isUnix()) {
			nativeFolder = "linux";
			
		} else if(SystemUtil.isSolaris()) {
			nativeFolder = "solaris";
			
		}
		
		if(nativeFolder == null)
			throw new IllegalStateException("System not supported");
		
		SystemUtil.setLibraryPath(libPath + nativeFolder);
	}
	
	
	public void refresh(int frameRate)
	{
		Display.update();
		Display.sync(frameRate);
	}
	
	
	public void close()
	{
		Display.destroy();
	}
	
	
	public float getRatio()
	{
		return (float)((float)Display.getWidth() / (float)Display.getHeight());
	}
	
}
