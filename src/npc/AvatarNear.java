package npc;

import graphicslib3D.Point3D;
import network.GameServerTCP;
import sage.ai.behaviortrees.BTCondition;

public class AvatarNear extends BTCondition 
{ 
	GameServerTCP server;
	NPCcontroller npcc;
	NPC npc;
	
	public AvatarNear(GameServerTCP s, NPCcontroller c, NPC n, boolean toNegate) 
	{
		super(toNegate); 
		server = s; 
		npcc = c; 
		npc = n; 
	} 
 
	protected boolean check() 
	{
	//	Point3D npcP = new Point3D(npc.getX(),npc.getY(),npc.getZ()); 
	//	server.sendCheckForAvatarNear(); 
		System.out.println("inside avatar near " +server.col[npc.getID()]);
		return server.col[npc.getID()]; 
	}
} 
