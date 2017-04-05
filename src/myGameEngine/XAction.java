package myGameEngine;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import network.Client;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;
import sage.terrain.*;

public class XAction extends AbstractInputAction {

	private SceneNode avatar;
	private float speed; 
	private int direction;
	private TerrainBlock terrain;
	private Client client;
	
	public XAction(SceneNode a, float s, int dir, TerrainBlock terrain, Client client) 
	{ 
		avatar = a; 
		speed = s; 
		direction = dir;
		this.terrain = terrain;
		this.client = client;
	} 
	public void performAction(float time, net.java.games.input.Event e) 
	{ 
		Matrix3D rot = avatar.getLocalRotation();
		Vector3D dir = new Vector3D(1,0,0);
		dir = dir.mult(rot);
		dir.scale((double)(speed * time));
		if (e.getValue() < -0.2 || direction < 0)
		{
			avatar.translate((float)dir.getX(), (float)dir.getY(), (float)dir.getZ());
		} 
		else
		{
			if (e.getValue() > 0.2 || direction > 0) 
			{
			avatar.translate(-(float)dir.getX(), -(float)dir.getY(), -(float)dir.getZ());
			}
		}
		updateVerticalPosition();
		
		Vector3D newPosition = avatar.getLocalTranslation().getCol(3);
		if (client != null){
			   client.sendMoveMessage(newPosition);
		}
	}
	private void updateVerticalPosition() {
		Point3D avLoc = new Point3D(avatar.getLocalTranslation().getCol(3));
		float x = (float)avLoc.getX();
		float z = (float)avLoc.getZ();
		float terHeight = terrain.getHeightFromWorld(new Point3D(x,0,z));
	//	float terHeight = terrain.getHeight(x,z);
		float desiredHeight = terHeight + (float)terrain.getOrigin().getY() +1.0f ;
		
		avatar.getLocalTranslation().setElementAt(1, 3, desiredHeight);
		
		
	}
	


}
