package net.veegl.model;

import org.lwjgl.util.vector.Vector3f;

public class Face 
{
	/////////////////////////////////////////////////////
	////// three indices, not vertices or normals!
    private Vector3f vertexIndicies  = new Vector3f();
    private Vector3f normalIndicies  = new Vector3f();
    private Vector3f textureIndicies = new Vector3f();
    
    
    public Vector3f getVertexIndicies() {
		return vertexIndicies;
	}


	public Vector3f getTextureIndicies() {
		return textureIndicies;
	}

	
	public Vector3f getNormalIndicies() {
		return normalIndicies;
	}


	public Face(Vector3f vertexIndex, Vector3f textureIndex, Vector3f normalIndex) {
        this.vertexIndicies  = vertexIndex;
        this.textureIndicies = textureIndex;
        this.normalIndicies  = normalIndex;
    }
	
	
    
}