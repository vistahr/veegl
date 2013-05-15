package net.veegl.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Oskar
 */
public class OBJLoader 
{
	

    public static Model loadModel(File f) throws LoaderException
    {
        BufferedReader reader = null;
        
		try 
		{
			reader = new BufferedReader(new FileReader(f));
		} 
			catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
        Model m = new Model();
        String line;
        
        try 
        {
        	
			while ((line = reader.readLine()) != null) 
			{

			    if (line.startsWith("v ")) // vertex
			    {
			        float x = Float.valueOf(line.split(" ")[1]);
			        float y = Float.valueOf(line.split(" ")[2]);
			        float z = Float.valueOf(line.split(" ")[3]);
			        m.getVertices().add(new Vector3f(x, y, z));
			        
			    } 
			    	else if (line.startsWith("vn ")) // normal
			    {
			        float x = Float.valueOf(line.split(" ")[1]);
			        float y = Float.valueOf(line.split(" ")[2]);
			        float z = Float.valueOf(line.split(" ")[3]);
			        m.getNormals().add(new Vector3f(x, y, z));
			        
			    } 
			    	else if (line.startsWith("vt ")) // texture
			    {
			    	 float x = Float.valueOf(line.split(" ")[1]);
				     float y = Float.valueOf(line.split(" ")[2]);
				     m.getTextures().add(new Vector2f(x, y));  
				     
			    } 
			    	else if (line.startsWith("f ")) // face
			    {

			        Vector3f vertexIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[0]), 
												                Float.valueOf(line.split(" ")[2].split("/")[0]),
												                Float.valueOf(line.split(" ")[3].split("/")[0]));
			        

			        Vector3f textureIndicies = null;
			        try
			        {
			        	textureIndicies = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[1]), 
												                Float.valueOf(line.split(" ")[2].split("/")[1]),
												                Float.valueOf(line.split(" ")[3].split("/")[1]));
			        } catch(NumberFormatException e) {
			        	// continue - no tex-coord for face is allowed (EmptyString)
			        }
			        
			       
			        Vector3f normalIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[2]), 
									                Float.valueOf(line.split(" ")[2].split("/")[2]),
									                Float.valueOf(line.split(" ")[3].split("/")[2]));
			     

			        m.getFaces().add(new Face(vertexIndices, textureIndicies, normalIndices));
			    }
			}
			
			reader.close();
			
		} 
        	catch (NumberFormatException e) 
		{
			throw new LoaderException("Error while loading model. Export again. " + e.getMessage());
			
		} 
        	catch (IOException e) 
        {
			throw new LoaderException("Error while loading model. Export again. " + e.getMessage());
		}
        
        
        
        return m;
    }
    
    
}