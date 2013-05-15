package net.veeglx.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;


public class MatrixUtil
{

	public static FloatBuffer matrixToFloatBuffer(Matrix4f matrix)
	{
		FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
		{
			matrixBuffer.put(matrix.m00);
			matrixBuffer.put(matrix.m01);
			matrixBuffer.put(matrix.m02);
			matrixBuffer.put(matrix.m03);
			
			matrixBuffer.put(matrix.m10);
			matrixBuffer.put(matrix.m11);
			matrixBuffer.put(matrix.m12);
			matrixBuffer.put(matrix.m13);
			
			matrixBuffer.put(matrix.m20);
			matrixBuffer.put(matrix.m21);
			matrixBuffer.put(matrix.m22);
			matrixBuffer.put(matrix.m23);
			
			matrixBuffer.put(matrix.m30);
			matrixBuffer.put(matrix.m31);
			matrixBuffer.put(matrix.m32);
			matrixBuffer.put(matrix.m33);
		}
		matrixBuffer.flip();
		
		return matrixBuffer;
	}
	
}
