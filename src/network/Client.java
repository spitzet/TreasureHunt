
package network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;
import java.util.Iterator;

import npc.GhostNPC;
import sage.networking.client.GameConnectionClient;
import games.treasureHunt2014.TreasureHunt2014;
import graphicslib3D.Vector3D;


public class Client extends GameConnectionClient
{
	private TreasureHunt2014 game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	private Vector<GhostNPC> ghostNPCs ;
   
	public Client(InetAddress remAddr, int remPort, ProtocolType pType, TreasureHunt2014 g) throws IOException{
		super(remAddr, remPort, pType);
		game = g;
		id = UUID.randomUUID();
		ghostAvatars = new Vector<GhostAvatar>();
		ghostNPCs = new Vector<GhostNPC>();
	}
   
	protected void processPacket(Object msg)
	{
		String message = (String)msg;
		String[] messageTokens = message.split(",");
		if(messageTokens[0].compareTo("join")==0)
		{
			//format join,success or join,failure
			if(messageTokens[1].compareTo("success")==0){
				game.setIsConnected(true);
				sendCreateMessage(game.getPlayerPosition(),game.select);
		   }
		   else if(messageTokens[1].compareTo("failure")==0){
			   game.setIsConnected(false);
		   }
	//		System.out.println("client received join");
	   }
      
		if(messageTokens[0].compareTo("bye")==0){
			//format bye,remoteID
			UUID ghostID = UUID.fromString(messageTokens[1]);
			removeGhostAvatar(ghostID);
		//	System.out.println("client received bye");
		}
      
		if(messageTokens[0].compareTo("create")==0){
			//format create,remoteID,x,y,z,select
			Vector3D ghostPosition = new Vector3D(Double.parseDouble(messageTokens[2]), Double.parseDouble(messageTokens[3]), Double.parseDouble(messageTokens[4]));
			createGhostAvatar(messageTokens[1], ghostPosition,messageTokens[5]);
			//System.out.println("client received create");
		}
      
		if(messageTokens[0].compareTo("dsfr")==0){
			//format dsfr,remoteID,x,y,z,select
			Vector3D ghostPosition = new Vector3D(Double.parseDouble(messageTokens[2]), Double.parseDouble(messageTokens[3]), Double.parseDouble(messageTokens[4]));
			createGhostAvatar(messageTokens[1], ghostPosition,messageTokens[5]);
			//System.out.println("client received dsfr");
		}
      
		if(messageTokens[0].compareTo("wsds")==0){
			//format wsds,remoteID,select
			UUID ghostID = UUID.fromString(messageTokens[1]);
			sendDetailsMessage(ghostID, game.getPlayerPosition(),game.select);
			//System.out.println("client received wsds");
		}
	   
		if(messageTokens[0].compareTo("move")==0){
			//format move,remoteID,x,y,z
			UUID ghostID = UUID.fromString(messageTokens[1]);
			Vector3D ghostPosition = new Vector3D(Double.parseDouble(messageTokens[2]), Double.parseDouble(messageTokens[3]), Double.parseDouble(messageTokens[4]));
			updateGhostAvatar(ghostID, ghostPosition);
			//System.out.println("client received move");
		}
		if(messageTokens[0].compareTo("wave")==0){
			//format wave,remoteID,i
			UUID ghostID = UUID.fromString(messageTokens[1]);			
			waveGhostAvatar(ghostID,Integer.parseInt(messageTokens[2]));
			//System.out.println("client received wave");
		}
		if(messageTokens[0].compareTo("dance")==0){
			//format dance,remoteID,i
			UUID ghostID = UUID.fromString(messageTokens[1]);			
			danceGhostAvatar(ghostID,Integer.parseInt(messageTokens[2]));
			//System.out.println("client received dance");
		}
		if(messageTokens[0].compareTo("mnpc") == 0){ 
			// format mnpc,ghostNPC_ID, x,y,z
			 int ghostID = Integer.parseInt(messageTokens[1]); 
			 Vector3D ghostPosition = new Vector3D(); 
			 ghostPosition.setX(Double.parseDouble(messageTokens[2])); 
			 ghostPosition.setY(Double.parseDouble(messageTokens[3])); 
			 ghostPosition.setZ(Double.parseDouble(messageTokens[4])); 
			 updateGhostNPC(ghostID, ghostPosition); 
		//	 System.out.println("client received mnpc");
			 checkCollision(ghostID,ghostPosition);
		 } 
		

	}
	   
	

	public void sendCreateMessage(Vector3D pos, int select){
		try
		{
			//format create,clientID,x,y,z,select
			String message = new String("create," + id.toString());
			message += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ()+"," + select;
			sendPacket(message);
		//	System.out.println("client sent create");
		} catch (IOException e){ e.printStackTrace(); }
	}
	   
	public void sendJoinMessage(){
		try
		{
			//format join,clientID
			sendPacket(new String("join," + id.toString()));
		//	System.out.println("client sent join");
		} catch (IOException e){ e.printStackTrace(); }
	}
	   
	public void sendByeMessage(){
		try
		{
			//format bye,clientID
			//sendPacket(new String("bye," + id.toString()));
			sendPacket(new String("bye," + id.toString()));
		//	System.out.println("client sent bye");
		} catch (IOException e){ e.printStackTrace(); }
	}
	   
	public void sendDetailsMessage(UUID remID, Vector3D pos,int select){
		try
		{
			//format dsfr,remoteID,clientID,x,y,z,select
			String message = new String("dsfr," + remID + "," + id.toString());
			message += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ()+","+ select;
			sendPacket(message);
	//		System.out.println("client sent details");
		} catch (IOException e){ e.printStackTrace(); }
	}
	   
	public void sendMoveMessage(Vector3D pos){
		try
		{
			//format move,clientID,x,y,z
			String message = new String("move," + id.toString());
			message += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
			sendPacket(message);
		//	System.out.println("client sent move");
		} catch (IOException e){ e.printStackTrace(); }
	      
	}

	public void sendWaveMessage(int i) {
		try
		{
			//format wave,clientID,i
			String message = new String("wave," + id.toString());
			message += "," +i;
			sendPacket(message);
			//System.out.println("client sent wave");
		} catch (IOException e){ e.printStackTrace(); }
		
	}
	
	
	public void removeGhostAvatar(UUID gID){
		GhostAvatar cur = null;
		GhostAvatar flag = null;
		Iterator<GhostAvatar> iter= ghostAvatars.iterator();
		while (iter.hasNext()){
			cur = iter.next();
			if (gID.equals(UUID.fromString(cur.getID()))){
				flag = cur;
				break;
			}
		}
		if (flag != null){
			game.removeGhostAvatar(flag);
			ghostAvatars.remove(flag);
		}
	}
	   
	public void createGhostAvatar(String ghostID, Vector3D pos, String select){
		GhostAvatar newGhost = new GhostAvatar(ghostID, pos, Integer.parseInt(select));
		ghostAvatars.add(newGhost);
		game.addGhostAvatar(newGhost);
	}
	   
	public void updateGhostAvatar(UUID gID, Vector3D pos){
		GhostAvatar cur;
		Iterator<GhostAvatar> iter = ghostAvatars.iterator();
		while (iter.hasNext()){
			cur = iter.next();
			if (gID.equals(UUID.fromString(cur.getID()))){
				cur.setPosition(pos);
				break;
			}
		}
	}
	public void waveGhostAvatar(UUID gID, int i){
		GhostAvatar cur;
	//	String message = (String)string;
	//	String[] messageTokens = message.split(",");
		Iterator<GhostAvatar> iter = ghostAvatars.iterator();
		while (iter.hasNext()){
			cur = iter.next();
			if (gID.equals(UUID.fromString(cur.getID()))){
		///		if (messageTokens[0].compareTo("wave") ==0){
					
			
				cur.performAnimation(i);
		//		System.out.println("inside wave ghost avatar method");
				if ( (i+1) %2 == 0) game.setWave(true,cur);
				else game.setWave(false,cur);
				break;
			}
		}
	}

	private void danceGhostAvatar(UUID gID, int i) {
		GhostAvatar cur;
		//	String message = (String)string;
		//	String[] messageTokens = message.split(",");
			Iterator<GhostAvatar> iter = ghostAvatars.iterator();
			while (iter.hasNext()){
				cur = iter.next();
				if (gID.equals(UUID.fromString(cur.getID()))){
			///		if (messageTokens[0].compareTo("wave") ==0){
						
				
					cur.performAnimationDance(i);
			//		System.out.println("inside wave ghost avatar method");
					if ( (i+1) %2 == 0) game.setWave(true,cur);
					else game.setWave(false,cur);
					break;
				}
			}
		
	}

	   
	public Vector<GhostAvatar> getGhostAvatars() { return ghostAvatars;	}
	
	private void updateGhostNPC(int id, Vector3D position/*, int danger*/) 
	 { 
		if (ghostNPCs.size() < 5 ){
			 GhostNPC newNPC = new GhostNPC(id, position); 
		
			 ghostNPCs.add(newNPC); 
			 game.addGhostNPCtoGameWorld(newNPC); 
	//		 System.out.println("client created ghostNPC");
		}
		else if (ghostNPCs.size()>id){ 
			ghostNPCs.get(id).setPosition(position); 
	//		System.out.println("client updated ghostNPC"+id);
		} 
		
	
	//	System.out.println("client updated ghostNPC");
	 } 
	private void checkCollision(int ghostID, Vector3D ghostPosition) {
		Vector3D avatarPos = game.getPlayerPosition();
		double tx = Math.abs(avatarPos.getX()-ghostPosition.getX());
		double tz = Math.abs(avatarPos.getZ()-ghostPosition.getZ());
		//System.out.println(tx + " " + tz);
		if (tx < 10.0f && tz < 10.0f){
			sendCollideMessage(ghostID,1);
		}
		else sendCollideMessage(ghostID, 0);
		
	}
	private void sendCollideMessage(int ghostID,int col) {
		//format collide, ghostID,col
		try
		{
			
			String message = new String("collide," + ghostID+","+col);
			
			sendPacket(message);
			//System.out.println("client sent collide" + col);
		} catch (IOException e){ e.printStackTrace(); }
		
	}

	public void sendDanceMessage(int i) {
		try
		{
			//format dance,clientID,i
			String message = new String("dance," + id.toString());
			message += "," +i;
			sendPacket(message);
			//System.out.println("client sent dance");
		} catch (IOException e){ e.printStackTrace(); }
		
	}
	
	/* public void askForNPCinfo() { 
		 
		 // format needNPC, id
		 try { 
			 sendPacket(new String("needNPC," + id.toString())); 
		 } 
		 catch (IOException e) { e.printStackTrace();	 }
		 } 
*/

	
}