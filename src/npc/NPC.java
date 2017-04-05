package npc;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
//import myGameEngine.CrashEvent;
//import sage.event.IEventListener;
//import sage.event.IGameEvent;
import sage.scene.shape.Cube;

public class NPC extends Cube //implements IEventListener
{
	private int id;
	private float amount = 0.0045f;
	private float race = 1.0f;
	
	public NPC()
	{
		super();
		this.scale(0.5f, 0.5f, 0.5f);
	}
	
	public NPC(int id) {
		super();
		//this.scale(3f, 3f, 3f);
		this.id = id;
		
	}

	public void randomizeLocation(int x, int z) {
		this.translate(x, 2.0f, z);		
	}

	public void updateLocation() {
		this.translate(amount,0,amount);
	//	System.out.println("update location in NPC class"+this.getLocalTranslation().getCol(3).toString());
	}

	public float getX() {
		
		return (float)this.getLocalTranslation().getCol(3).getX();
	}

	public float getY() {
		
		return (float)this.getLocalTranslation().getCol(3).getY();
	}

	public float getZ() {
		
		return (float)this.getLocalTranslation().getCol(3).getZ();
	}

	public void RunAway() {
		this.translate(race,0,race);
		
	}
	public int getID(){
		return id;
	}
/*	public void setAmount(float a){
		amount = a;
	}
	public void setRace(float race){
		this.race = race;
	}
	public float getAmount(){
		return amount;
	}
	public float getRace(){
		return race;
	}
	*/
}
