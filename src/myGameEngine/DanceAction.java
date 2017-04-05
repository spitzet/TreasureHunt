package myGameEngine;

import java.awt.Event;

import network.Client;
import sage.input.action.AbstractInputAction;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.terrain.TerrainBlock;

	public class DanceAction extends AbstractInputAction
	{ 
		private Model3DTriMesh avatar; 
		private int i;
		private Client client;
		
		public DanceAction(SceneNode a, Client client)
		{
			avatar = (Model3DTriMesh)a;
			i = 0;
			this.client = client;
		}
	
		public DanceAction(Model3DTriMesh n) 
		{
			avatar = n;
			i = 0;
		} 

	public void performAction(float arg0, net.java.games.input.Event arg1) {
		if(i%2 == 0) avatar.startAnimation("dance");
		else avatar.stopAnimation();
		i++;
		if (client != null){
			   client.sendDanceMessage(i);
		}
	}
	
}
