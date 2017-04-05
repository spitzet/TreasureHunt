package myGameEngine;

import java.awt.Color;
import java.awt.Shape;
import java.util.Random;

import sage.scene.*;
import graphicslib3D.Vector3D;
import sage.scene.Controller;
import sage.scene.SceneNode;
import sage.scene.shape.*;

public class ColorController extends Controller
{
	//private float rate;
	private int rate;
	private int i;
	
	public ColorController()
	{
		rate = 50;
		i = 0;
	}
	public void setRate(int r) { rate = r; }

	public void update(double time)
	{	
		for (SceneNode node : controlledNodes) 
		{
			i++;
			if(i == rate)
			{
				Random rng = new Random(); 
				Color color = new Color(rng.nextFloat(),rng.nextFloat(),rng.nextFloat());
				((Rectangle)node).setColor(color);
				i = 0;
			}
		} 
	}
}
