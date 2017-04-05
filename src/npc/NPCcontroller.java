package npc;

import java.util.Random;

import network.GameServerTCP;

import sage.ai.behaviortrees.*;

public class NPCcontroller {
	
	BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR); 
	float startTime;	
	float lastUpdateTime;
	//NPC npc;
	NPC[] NPClist = new NPC[5];
	Random rn = new Random();
	GameServerTCP server;
	
	
	public void startNPControl()
	{ 
	//	startTime = System.nanoTime(); 
	//	lastUpdateTime = startTime; 
	//	setupNPC(); 
	//	setupBehaviorTree(); 
		//npcLoop();
		
	}
	
	
	public void setupNPC() 
	{
		for (int i = 0; i < NPClist.length; i++){
			NPClist[i] = new NPC(i);
			NPClist[i].randomizeLocation(rn.nextInt(50),-rn.nextInt(50)); 
			//NPClist[i].randomizeLocation(0,-40);
			
		}
		
	 }
	 
	
	 	public void updateNPCs() {
	 	
		for (int i = 0; i < NPClist.length; i++){
			
			NPClist[i].updateLocation();
//			npc.randomizeLocation(rn.nextInt(100),rn.nextInt(100)); 
		//	NPClist[i].randomizeLocation(-10, -10);
		}
		
	}
	
	

	/*
	 public void npcLoop() 
	 {
		 while (true) 
		 {
			 long frameStartTime = System.nanoTime(); 
			 float elapsedMilliSecs = (frameStartTime-lastUpdateTime)/(1000000.0f); 
			 if (elapsedMilliSecs >= 50.0f) 
			 {
				 lastUpdateTime = frameStartTime; 
				 npc.updateLocation();
				 //Server.sendNPCinfo(); 
				 bt.update(elapsedMilliSecs); 
			 } 
		 Thread.yield(); 
		 }
	} 
		 
	
	public void setupBehaviorTree() 
	{
		bt.insertAtRoot(new BTSequence(10)); 
		//bt.insertAtRoot(new BTSequence(20)); 
		//bt.insert(10, new OneSecPassed(this,npc,false)); 
		//bt.insert(10, new GetSmall(npc)); 
	//	bt.insert(10, new AvatarNear(server, this, npc, false)); 
	//	bt.insert(10, new RunAway(npc)); 
	}
*/


	public int getNumOfNPCs() {
		// TODO Auto-generated method stub
		return 5;
	}

	public NPC getNPC(int i) {
		// TODO Auto-generated method stub
		return NPClist[i];
	}
}
