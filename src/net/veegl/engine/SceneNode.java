package net.veegl.engine;

import java.util.ArrayList;
import java.util.List;


import net.veegl.light.Light;
import net.veegl.model.Model;



public class SceneNode 
{
	
	private List<Model> models;
	private List<Light> lights;
	
	
	public SceneNode()
	{
		models = new ArrayList<Model>();
		lights = new ArrayList<Light>();
	}
	
	
	public void addModel(Model model)
	{
		models.add(model);
	}

	public List<Model> getModels() {
		return models;
	}
	
	
	public void addLight(Light light)
	{
		lights.add(light);
	}

	public List<Light> getLights() {
		return lights;
	}
	
}
