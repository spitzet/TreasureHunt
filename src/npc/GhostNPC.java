package npc;

import sage.scene.SceneNode;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class GhostNPC 
{ 
	NPC body;
	private int id; 
 
	public GhostNPC(int id, Vector3D position) 
	{ 
		this.id = id; 
		this.body = new NPC(id); 
		setPosition(position); 
	} 
	
	public void setPosition(Vector3D position) 
	{ 
	
		body.setLocalTranslation(new Matrix3D());
		body.translate((float)position.getX(), (float)position.getY(), (float)position.getZ());
	//	System.out.println("set position in Ghost NPC class");
	//	System.out.println("Position after setting position in GHOST NPC is" + body.getLocalTranslation().getCol(3).toString()+ getID())	;
	}

	public SceneNode getNPC() {
		
		return (SceneNode) body;
	}

	public int getID() {
		
		return id;
	}
} 
