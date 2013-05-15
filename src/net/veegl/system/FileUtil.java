package net.veegl.system;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;


public class FileUtil
{

	
	public static ByteBuffer fileToByteBuffer(File file) 
	{
		int fileLength = (int)file.length();
		byte[] byteSource = new byte[fileLength];
		
		
		InputStream is;
		try {
			is = new FileInputStream(file);
			is.read(byteSource);
			is.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ByteBuffer bb = BufferUtils.createByteBuffer(fileLength);
		bb.put(byteSource);
		bb.flip();
		
		return bb;
	}
	
	
}
