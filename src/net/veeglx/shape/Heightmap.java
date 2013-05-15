package net.veeglx.shape;

import net.veeglx.math.PerlinNoise;

public class Heightmap 
{
	public static int[][] generate2DHeightmap(int width, int depth)
	{
		int[][] heightmap = new int[width][depth];
		
		for(int i=0; i<width; i++)
		{
			for(int j=0; j<depth; j++)
			{
				heightmap[i][j] = PerlinNoise.noise(i, j, 7);
			}
		}
		
		return heightmap;
	}
	
	
}

