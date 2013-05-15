package net.veegl.model;


import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL15.*;
import net.veegl.engine.mode.BufferModeEnum;
import net.veegl.model.callback.ICallback;
import net.veegl.shader.Shader;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.bulletphysics.dynamics.RigidBody;


public class Model 
{

	private BufferModeEnum bufferMode = null;
	

	private int vertexBufferName 	  = -1;
	private int vertexBufferIndexName = -1;
	
	private List<Face> faces = new ArrayList<Face>();
	
	private List<Vector3f> vertices  = new ArrayList<Vector3f>();
	private List<Vector3f> normals   = new ArrayList<Vector3f>();
	private List<Vector2f> textures  = new ArrayList<Vector2f>();
	
	private FloatBuffer modelBuffer;
	private IntBuffer modelIndexBuffer;
	
	private RigidBody body;
	
	private Color pickingColor;
	
	private float scaleSize = 1f;
	
	private boolean isHoverable  = false;
	private boolean hovered 	 = false;
	
	private boolean isSelectable = false;
	private boolean selected	 = false;
	
	private ICallback hoverCallback		= null;
	private ICallback selectCallback	= null;
	private ICallback collisionCallback = null;
	
	
	private Shader shader = null;

	private boolean shadow = false;
	
	
	
    public Model() {}
    
    
    public Model(float[] vertices, float[] faces, boolean gravity) 
    {
    	loadVertices(vertices);
    	loadFaces(faces);
    } 
    
    /**
     * Copy constructor
     */
    public Model(Model m)
    {
    	this.bufferMode = m.bufferMode;
		this.vertexBufferName = m.vertexBufferName;
		this.vertexBufferIndexName = m.vertexBufferIndexName;
		this.faces = m.faces;
		this.vertices = m.vertices;
		this.normals = m.normals;
		this.modelBuffer = m.modelBuffer;
		this.modelIndexBuffer = m.modelIndexBuffer;
		this.body = m.body;
		this.pickingColor = m.pickingColor;
		this.scaleSize = m.scaleSize;
		this.isHoverable = m.isHoverable;
		this.hovered = m.hovered;
		this.isSelectable = m.isSelectable;
		this.selected = m.selected;
		this.shader = m.shader;
		this.hoverCallback = m.hoverCallback;
		this.selectCallback = m.selectCallback;
		this.collisionCallback = m.collisionCallback;
		this.shadow = m.shadow;
		this.textures = m.textures;
    }
    

	public List<Vector2f> getTextures() {
		return textures;
	}


	public ICallback getHoverCallback() 
    {
		return hoverCallback;
	}


    private void setHoverCallback(ICallback hoverCallback) 
	{
		this.hoverCallback = hoverCallback;
	}


	public ICallback getSelectCallback() 
	{
		return selectCallback;
	}


	private void setSelectCallback(ICallback selectCallback) 
	{
		this.selectCallback = selectCallback;
	}


	public ICallback getCollisionCallback() 
	{
		return collisionCallback;
	}


	public void setCollisionCallback(ICallback collisionCallback) 
	{
		this.collisionCallback = collisionCallback;
	}


	public boolean hasShader()
    {
    	if(shader == null)
    		return false;
    	
    	return true;
    }
    
    
	public Shader getShader() 
	{
		return shader;
	}


	public void setShader(Shader shader) 
	{
		this.shader = shader;
	}


	public float getScaleSize() 
	{
		return scaleSize;
	}

	/**
	 * Default is 1 (100%). To in- or decrease use floatingpoints.
	 * @param size
	 */
	public void setScaleSize(float size) 
	{
		this.scaleSize = size;
	}


	public FloatBuffer getModelBuffer() 
	{
		return modelBuffer;
	}


	public IntBuffer getModelIndexBuffer() 
	{
		return modelIndexBuffer;
	}

	/**
	 * Sets the preffered buffer. For a better performance use vbo or static buffer modes.
	 * Use Intermidiate only for debugging.
	 * @param bufferMode
	 */
	public void setBuffer(BufferModeEnum bufferMode)
    {
    	this.bufferMode = bufferMode;

    	if(bufferMode == BufferModeEnum.VBO_DYNAMIC)
	    	bufferModelDataToVB();
    }

	public BufferModeEnum getBufferMode() 
	{
		return bufferMode;
	}

	
	public int getVertexBufferName()
	{
		return vertexBufferName;
	}

	
	private void setVertexBuffer(int vertexBufferName)
	{
		this.vertexBufferName = vertexBufferName;
	}
	
    public int getVertexBufferIndexName() 
    {
		return vertexBufferIndexName;
	}


	public void setVertexBufferIndexName(int vertexBufferIndexName) 
	{
		this.vertexBufferIndexName = vertexBufferIndexName;
	}


	
	public boolean hasShadow() {
		return shadow;
	}


	public void setShadow(boolean shadow) {
		this.shadow = shadow;
	}


	private static int genVBOName() 
	{
		IntBuffer vboBuffer = BufferUtils.createIntBuffer(1);
		glGenBuffers(vboBuffer);
		
		int bufferName = vboBuffer.get(0);
		
		return bufferName;
	}
	
	
	private void bufferModelDataToVB()
	{
		setVertexBuffer(genVBOName());
		setVertexBufferIndexName(genVBOName());
		
		////////////////////////////////
		// vertices & normals to Buffer
		modelBuffer = BufferUtils.createFloatBuffer((getFaces().size()) * 24); // every normal && vertex vector has 3 coords + 2 texcoords
		modelBuffer.clear();
		
		for(Face face: getFaces())
		{
			Vector3f p1 = getVertices().get((int) face.getVertexIndicies().getX()-1);
			Vector3f p2 = getVertices().get((int) face.getVertexIndicies().getY()-1);
			Vector3f p3 = getVertices().get((int) face.getVertexIndicies().getZ()-1);
			
			Vector3f n1 = getNormals().get((int) face.getNormalIndicies().getX()-1);
			Vector3f n2 = getNormals().get((int) face.getNormalIndicies().getY()-1);
			Vector3f n3 = getNormals().get((int) face.getNormalIndicies().getZ()-1);
			
			
			Vector2f t1 = null, t2 = null, t3 = null;
			if(face.getTextureIndicies() != null)
			{
				t1 = getTextures().get((int) face.getTextureIndicies().getX()-1);
				t2 = getTextures().get((int) face.getTextureIndicies().getY()-1);
				t3 = getTextures().get((int) face.getTextureIndicies().getZ()-1);
			}
			
			
			//////////////////
			////// V3, T2, N3
			modelBuffer.put(new float[]{p1.getX(), p1.getY(), p1.getZ()});
			if(t1 != null)
			{
				modelBuffer.put(new float[]{t1.getX(), t1.getY()});
			} 
				else 
			{
					modelBuffer.put(new float[]{-1, -1});
			}	
			modelBuffer.put(new float[]{n1.getX(), n1.getY(), n1.getZ()});
			
			
			//////////////////
			////// V3, T2, N3
			modelBuffer.put(new float[]{p2.getX(), p2.getY(), p2.getZ()});
			if(t2 != null)
			{
				modelBuffer.put(new float[]{t2.getX(), t2.getY()});
			} 
				else 
			{
					modelBuffer.put(new float[]{-1, -1});
			}	
			modelBuffer.put(new float[]{n2.getX(), n2.getY(), n2.getZ()});
			
			
			
			//////////////////
			////// V3, T2, N3
			modelBuffer.put(new float[]{p3.getX(), p3.getY(), p3.getZ()});
			if(t3 != null)
			{
				modelBuffer.put(new float[]{t3.getX(), t3.getY()});
			} 
				else 
			{
					modelBuffer.put(new float[]{-1, -1});
			}
			modelBuffer.put(new float[]{n3.getX(), n3.getY(), n3.getZ()});
			
		}
		
		modelBuffer.rewind();	
		glBindBuffer(GL_ARRAY_BUFFER, getVertexBufferName());
		glBufferData(GL_ARRAY_BUFFER, modelBuffer, GL_STATIC_DRAW); // TODO
		
		////////////////////////////////////////////
		// index-vertices & index-normals to Buffer
		modelIndexBuffer = BufferUtils.createIntBuffer(getFaces().size() * 3); // every face has 3 vertex && 3 normal indicies + 2 texture indicies
		modelIndexBuffer.clear();
		
		int i = 0;
		for(@SuppressWarnings("unused") Face face: getFaces())
		{
			// Indicies for VTN
			modelIndexBuffer.put(i);
			modelIndexBuffer.put(i+1);
			modelIndexBuffer.put(i+2);
			i+=3;
		}
		
		modelIndexBuffer.rewind();	
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, getVertexBufferIndexName());
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, modelIndexBuffer, GL_STATIC_DRAW);// TODO		
		
		// reset bindings
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}


	public List<Vector3f> getVertices()
	{
		return vertices;
	}


	public RigidBody getBody() {
		return body;
	}


	public boolean isSelectable() {
		return isSelectable;
	}

	/**
	 * Sets the model with a callback selectable. To delte (false), set ICallback to null.
	 * @param isSelectable
	 * @param callback
	 */
	public void setSelectable(boolean isSelectable, ICallback callback) {
		this.isSelectable = isSelectable;
		setSelectCallback(callback);
	}


	public boolean isSelected() {
		return selected;
	}


	public void setSelected(boolean selected) {
		this.selected = selected;
	}


	public boolean isHoverable() {
		return isHoverable;
	}

	/**
	 * Sets the model with a callback hovarable. To delte (false), set ICallback to null.
	 * @param isHoverable
	 * @param callback
	 */
	public void setHoverable(boolean isHoverable, ICallback callback) {
		this.isHoverable = isHoverable;
		setHoverCallback(callback);
	}


	public boolean isHovered() {
		return hovered;
	}


	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}


	public Color getPickingColor() {
		return pickingColor;
	}


	public void setPickingColor(Color pickingColor) {		
		this.pickingColor = pickingColor;
	}


	public void setBody(RigidBody body) {
		this.body = body;
	}


	public void loadVertices(float[] vertices)
	{
		Vector3f vertex, normal;
		for(int i=0; i<vertices.length; i+=3)
		{
			normal = new Vector3f();
			vertex = new Vector3f(vertices[i], vertices[i+1], vertices[i+2]);
			vertex.normalise(normal);
			this.vertices.add(vertex);
			this.normals.add(normal);
		}
	}
	
	
	public void loadFaces(float[] faces)
	{
		for(int i=0; i<faces.length; i+=3)
		{
			Vector3f index = new Vector3f(faces[i], faces[i+1], faces[i+2]);	
			this.faces.add(new Face(index, null,  index));
		}
	}


	public List<Vector3f> getNormals()
	{
		return normals;
	}


	public List<Face> getFaces()
	{
		return faces;
	}


	public void multWithMatrix(Matrix4f matrix)
	{
		Vector4f tempVector = new Vector4f();
		
		for(Vector3f v: getVertices())
		{
			Matrix4f.transform(matrix, new Vector4f(v.getX(), v.getY(), v.getZ(), 1), tempVector);
			v.set(tempVector.getX(), tempVector.getY(), tempVector.getZ());
		}
		
		for(Vector3f vn: getNormals())
		{
			Matrix4f.transform(matrix, new Vector4f(vn.getX(), vn.getY(), vn.getZ(), 1), tempVector);
			vn.set(tempVector.getX(), tempVector.getY(), tempVector.getZ());
		}
	}
	
	
	public Map<String,Float> calcDimensions() 
	{
		
		Map<String, Float> minMaxDimensions = new HashMap<String, Float>();
		
		float x_max = Float.NEGATIVE_INFINITY, 
			  x_min = Float.POSITIVE_INFINITY, 
			  y_max = Float.NEGATIVE_INFINITY, 
			  y_min = Float.POSITIVE_INFINITY, 
			  z_max = Float.NEGATIVE_INFINITY, 
			  z_min = Float.POSITIVE_INFINITY;
			
		for (Vector3f v : vertices)
		{
			x_max = (v.getX() > x_max) ? v.getX() : x_max;
			x_min = (v.getX() < x_min) ? v.getX() : x_min;
			
			y_max = (v.getY() > y_max) ? v.getY() : y_max;
			y_min = (v.getY() < y_min) ? v.getY() : y_min;
			
			z_max = (v.getZ() > z_max) ? v.getZ() : z_max;
			z_min = (v.getZ() < z_min) ? v.getZ() : z_min;
		}
		
		minMaxDimensions.put("X_MAX", x_max);
		minMaxDimensions.put("X_MIN", x_min);
		
		minMaxDimensions.put("Y_MAX", y_max);
		minMaxDimensions.put("Y_MIN", y_min);
		
		minMaxDimensions.put("Z_MAX", z_max);
		minMaxDimensions.put("Z_MIN", z_min);
		
		
		// get x,y,z max and min
		Map.Entry<String, Float> maxEntry = null;
		Map.Entry<String, Float> minEntry = null;
		for (Map.Entry<String, Float> entry : minMaxDimensions.entrySet())
		{
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
				maxEntry = entry;
			
			if (minEntry == null || entry.getValue().compareTo(minEntry.getValue()) < 0)
				minEntry = entry;
		}
		
		minMaxDimensions.put("MAX", maxEntry.getValue());
		minMaxDimensions.put("MIN", minEntry.getValue());
		
		return minMaxDimensions;
	}
	
	
	public float[] getCenter() {
		Map<String,Float> minMaxDimensions = calcDimensions();
		
		Vector3f v1 = new Vector3f(minMaxDimensions.get("X_MAX"), minMaxDimensions.get("Y_MAX"), minMaxDimensions.get("Z_MIN"));
		Vector3f v2 = new Vector3f(minMaxDimensions.get("X_MIN"), minMaxDimensions.get("Y_MIN"), minMaxDimensions.get("Z_MAX"));

		float[] center = {(v1.getX() + v2.getX()) / 2, (v1.getY() + v2.getY()) / 2, (v1.getZ() + v2.getZ()) / 2};
		
		return center;
	}
	
	
	public void toIdentity()
	{
		// centern 
		Matrix4f tm = new Matrix4f();
		tm.setIdentity();
		float[] centerPos = getCenter();
		Vector3f centerVec = new Vector3f(-centerPos[0], -centerPos[1], -centerPos[2]);
		tm.translate(centerVec);
		multWithMatrix(tm);		
	}
	
	
	public void setPosition(float[] newPosition)
	{
		toIdentity();
		// new positon
		Matrix4f tm = new Matrix4f();
		tm.setIdentity();
		tm.translate(new Vector3f(newPosition[0], newPosition[1], newPosition[2]));
		multWithMatrix(tm);
	}
	
	
	// TODO , get jbullet shape
	public Model getBoundBox()
	{
		Map<String,Float> minMaxDimensions = calcDimensions();
		
		Model boundingBox = new Model();
		
		// 8 points (0-7)
		boundingBox.vertices.add(new Vector3f(minMaxDimensions.get("X_MIN"), minMaxDimensions.get("Y_MIN"), minMaxDimensions.get("Z_MIN")));
		boundingBox.vertices.add(new Vector3f(minMaxDimensions.get("X_MAX"), minMaxDimensions.get("Y_MIN"), minMaxDimensions.get("Z_MIN")));
		boundingBox.vertices.add(new Vector3f(minMaxDimensions.get("X_MAX"), minMaxDimensions.get("Y_MIN"), minMaxDimensions.get("Z_MAX")));
		boundingBox.vertices.add(new Vector3f(minMaxDimensions.get("X_MIN"), minMaxDimensions.get("Y_MIN"), minMaxDimensions.get("Z_MAX")));		
		boundingBox.vertices.add(new Vector3f(minMaxDimensions.get("X_MIN"), minMaxDimensions.get("Y_MAX"), minMaxDimensions.get("Z_MIN")));
		boundingBox.vertices.add(new Vector3f(minMaxDimensions.get("X_MAX"), minMaxDimensions.get("Y_MAX"), minMaxDimensions.get("Z_MIN")));
		boundingBox.vertices.add(new Vector3f(minMaxDimensions.get("X_MAX"), minMaxDimensions.get("Y_MAX"), minMaxDimensions.get("Z_MAX")));
		boundingBox.vertices.add(new Vector3f(minMaxDimensions.get("X_MIN"), minMaxDimensions.get("Y_MAX"), minMaxDimensions.get("Z_MAX")));
		
		Vector3f faceNormal   = new Vector3f();
		Vector3f faceIdexes[] = new Vector3f[12];
		
		// 6 faces, with triangles = 12 faces
		faceIdexes[0] = new Vector3f(0, 4, 5);
		faceIdexes[1] = new Vector3f(0, 1, 5);
		
		faceIdexes[2] = new Vector3f(1, 5, 6);
		faceIdexes[3] = new Vector3f(1, 2, 6);
		
		faceIdexes[4] = new Vector3f(2, 3, 7);
		faceIdexes[5] = new Vector3f(2, 6, 7);
		
		faceIdexes[6] = new Vector3f(0, 4, 7);
		faceIdexes[7] = new Vector3f(7, 3, 0);
		
		faceIdexes[8] = new Vector3f(4, 5, 7);
		faceIdexes[9] = new Vector3f(5, 6, 7);
		
		faceIdexes[10] = new Vector3f(0, 3, 1);
		faceIdexes[11] = new Vector3f(1, 3, 2);
		
		// add faces with facenormals
		for(Vector3f fi: faceIdexes)
		{
			Vector3f.cross(boundingBox.vertices.get((int)fi.getX()), boundingBox.vertices.get((int)fi.getY()), faceNormal);
			faceNormal.normalise(faceNormal);
			boundingBox.faces.add(new Face(fi, null, faceNormal));
		}
		
		return boundingBox;
	}

	
	/**
	 * modelstate will be resetted every frame
	 */
	public void resetModelState()
	{
		hovered  = false;
		//selected = false;
	}
	
	
}
