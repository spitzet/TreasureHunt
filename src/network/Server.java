package network;

import java.io.IOException;

import sage.ai.behaviortrees.BTCompositeType;
import sage.ai.behaviortrees.BTSequence;
import sage.ai.behaviortrees.BehaviorTree;
import npc.AvatarNear;
import npc.NPCcontroller;
import npc.RunAway;

public class Server {

	private NPCcontroller npcCtrl;
	long startTime;
	long lastUpdateTime;
	static GameServerTCP testTCPServer;
	BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR); 
	
	public Server(int id) // constructor 
	{
		startTime = System.nanoTime(); 
		lastUpdateTime = startTime; 
		npcCtrl = new NPCcontroller(); 
		npcCtrl.setupNPC(); 
	//	setupBehaviorTree(); 
	//	npcLoop(); 
	} 
	 
	public void npcLoop() // NPC control loop 
	{
		 while (true) 
		 {
			long frameStartTime = System.nanoTime(); 
			float elapMilSecs = (frameStartTime-lastUpdateTime)/(1000000.0f); 
			if (elapMilSecs >= 500.0f) 
			{
				lastUpdateTime = frameStartTime; 
				npcCtrl.updateNPCs(); 
				testTCPServer.sendNPCinfo(npcCtrl); 
				for(int i =0; i< npcCtrl.getNumOfNPCs(); i++){
					if (testTCPServer.col[i] == true){
						npcCtrl.getNPC(i).RunAway();
						//System.out.println("in server, run away true");
					}
					//else System.out.println("in server, run away false");
				}
				//bt.update(elapMilSecs);
				
			} 
			Thread.yield(); 
		}
	} 
/*	public void setupBehaviorTree() 
	{
		bt.insertAtRoot(new BTSequence(10)); 
		bt.insertAtRoot(new BTSequence(20)); 
		for (int i = 0; i < npcCtrl.getNumOfNPCs();i++){
			
//		bt.insert(10, new OneSecPassed(this,npcCtrl.getNPC(i),false)); 
		//bt.insert(10, new GetSmall(npc)); 
		bt.insert(10, new AvatarNear(testTCPServer, npcCtrl, npcCtrl.getNPC(i), false)); 
		bt.insert(10, new RunAway(npcCtrl.getNPC(i))); 
		}
	}
*/
	public static void main(String[] args) {
		System.out.println(args[0]);
		try{
			testTCPServer = new GameServerTCP(Integer.parseInt(args[0])); 
		}
		catch(IOException e){
			e.printStackTrace();
		}
		Server server = new Server(100);
		server.npcLoop();
	}

}
