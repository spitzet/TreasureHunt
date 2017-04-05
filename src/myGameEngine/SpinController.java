package myGameEngine;

import graphicslib3D.Vector3D;
import sage.scene.Controller;
import sage.scene.SceneNode;

public class SpinController extends Controller
{
	private float rotationRate;
	
	public SpinController()	{ rotationRate = 5;	}
	public void changeDirection() { rotationRate *= -1; }
	public void setRotationRate(int r) { rotationRate = r; }

	public void update(double time)
	{		
		for (SceneNode node : controlledNodes) 
		{ 
			node.rotate(rotationRate, new Vector3D(0, 1, 0));
		} 
	}
}