package network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import npc.NPCcontroller;
import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;

public class GameServerTCP extends GameConnectionServer<UUID> 
{ 
//	private NPCcontroller npcCtrl;
	public boolean[] col = {false,false,false,false,false};

	public GameServerTCP(int localPort) throws IOException 
	{ super(localPort, ProtocolType.TCP); } 
 
	public void acceptClient(IClientInfo ci, Object o) // override 
	{
		String message = (String)o; 
		String[] messageTokens = message.split(","); 
 
		if(messageTokens.length > 0) 
		{
			if(messageTokens[0].compareTo("join") == 0) // received “join” 
			{ // format: join,localid 
				UUID clientID = UUID.fromString(messageTokens[1]); 
				addClient(ci, clientID); 
				sendJoinedMessage(clientID, true);
	//			System.out.println("server received join");
			}
		}
	}
	

   public void processPacket(Object o, InetAddress senderIP, int sndPort)
   {
      String message = (String)o;
      String[] messageTokens = message.split(",");
      
      if(messageTokens.length > 0){
         if (messageTokens[0].compareTo("bye") == 0){
            //formate bye,clientID
            UUID clientID = UUID.fromString(messageTokens[1]);
            sendByeMessage(clientID);
            removeClient(clientID);
    //        System.out.println("server received bye");
         }
         
         if(messageTokens[0].compareTo("create") == 0){
            //format create,clientID,x,y,z,select
            UUID clientID = UUID.fromString(messageTokens[1]);
            String[] pos ={messageTokens[2], messageTokens[3], messageTokens[4]};
            sendCreateMessage(clientID, pos,messageTokens[5]);
            sendWantsDetailsMessage(clientID);
   //         System.out.println("server received create");
         }
         
         if(messageTokens[0].compareTo("dsfr") == 0){
            //format dsfr,remoteID,clientID,x,y,z,select
            UUID remoteID = UUID.fromString(messageTokens[1]);
            UUID clientID = UUID.fromString(messageTokens[2]);
            String[] pos ={messageTokens[3], messageTokens[4], messageTokens[5]};
            sendDetailsMessage(remoteID, clientID, pos,messageTokens[6]);
    //        System.out.println("server received dsfr");
         }
         
         if(messageTokens[0].compareTo("move") == 0){
            //format move,clientID,x,y,z
            UUID clientID = UUID.fromString(messageTokens[1]);
            String[] pos ={messageTokens[2], messageTokens[3], messageTokens[4]};
            sendMoveMessage(clientID, pos);
  //          System.out.println("server received move");
         }
         if(messageTokens[0].compareTo("wave") == 0){
             //format wave,clientID,i
             UUID clientID = UUID.fromString(messageTokens[1]);
             
             sendWaveMessage(clientID, messageTokens[2]);
             System.out.println("server received wave");
          }
         if(messageTokens[0].compareTo("dance") == 0){
             //format dance,clientID,i
             UUID clientID = UUID.fromString(messageTokens[1]);
             
             sendDanceMessage(clientID, messageTokens[2]);
             System.out.println("server received dance");
          }
         
         if(messageTokens[0].compareTo("needNPC") == 0) 
         {  } 
        if(messageTokens[0].compareTo("collide") == 0) 
         { 
        	 //format collide, ghostID,collision
        	 if(Integer.parseInt(messageTokens[2])== 0) // false
        		 col[Integer.parseInt(messageTokens[1])] = false;
        	 else col[Integer.parseInt(messageTokens[1])] = true;
   
        	 //System.out.println("server received collide");
         } 
       }
  }
   
  

public void sendJoinedMessage(UUID clientID, boolean success)
   {
	   try
	   {
		   //format join,success or join,failure
		   String message = new String("join,");
		   if (success) { message += "success"; }
		   else { message += "failure"; }
		   
		   sendPacket(message, clientID);
	//	   System.out.println("server sent join");
	   } catch (IOException e) { e.printStackTrace(); }
   }
   
   public void sendCreateMessage(UUID clientID, String[] position, String select){
	   try{
		   //format create,clientID,x,y,z,select
		   String message = new String("create," + clientID.toString());
		   message += "," + position[0];
		   message += "," + position[1];
		   message += "," + position[2];
		   message += "," + select;
		   forwardPacketToAll(message, clientID);
	//	   System.out.println("server sent create");
	   } catch (IOException e){ e.printStackTrace(); }
   }
   
   public void sendDetailsMessage(UUID remoteID, UUID clientID, String[] position,String select){
	   try{
		   //format dsfr,clientID,x,y,z
		   String message = new String("dsfr," + clientID.toString());
		   message += "," + position[0];
		   message += "," + position[1];
		   message += "," + position[2];
		   message += "," + select;
		   sendPacket(message, remoteID);
//		   System.out.println("server sent details");
	   } catch (IOException e){ e.printStackTrace(); }
   }
   
   public void sendWantsDetailsMessage(UUID clientID){
	   try{
		   //format wsds,clientID
		   String message = new String("wsds," + clientID.toString());
		   forwardPacketToAll(message, clientID);
//		   System.out.println("server sent wsds");
	   } catch (IOException e){ e.printStackTrace(); }
   }
   
   public void sendMoveMessage(UUID clientID, String[] position){
	   try{
		   //format move,clientID,x,y,z
		   String message = new String("move," + clientID.toString());
		   message += "," + position[0];
		   message += "," + position[1];
		   message += "," + position[2];
		   forwardPacketToAll(message, clientID);
//		   System.out.println("server sent move");
	   } catch (IOException e){ e.printStackTrace(); }
   }
   public void sendWaveMessage(UUID clientID, String action) {
	   try{
		   //format wave,clientID,i
		   String message = new String("wave," + clientID.toString());
		   message += "," + action;
		   forwardPacketToAll(message, clientID);
		   System.out.println("server sent wave");
	   } catch (IOException e){ e.printStackTrace(); }
		
	}
   private void sendDanceMessage(UUID clientID, String action) {
		try{
			   //format dance,clientID,i
			   String message = new String("dance," + clientID.toString());
			   message += "," + action;
			   forwardPacketToAll(message, clientID);
			   System.out.println("server sent dance");
		   } catch (IOException e){ e.printStackTrace(); }
		
	}
   public void sendByeMessage(UUID clientID)
   {
	   try{
		   //format bye,clientID
		   String message = new String("bye," + clientID.toString());
		   forwardPacketToAll(message, clientID);
	//	   System.out.println("server sent bye");
	   } catch (IOException e){ e.printStackTrace(); }
   }

   public void sendCheckForAvatarNear() {
	// TODO Auto-generated method stub
	
   }

   public void sendNPCinfo(NPCcontroller npcCtrl)
   {
	   for (int i=0; i<npcCtrl.getNumOfNPCs(); i++) 
	   {
		   try 
		   {
			   //format mnpc,id,x,y,z
				String message = new String("mnpc," + Integer.toString(i)); 
				message += "," + (npcCtrl.getNPC(i)).getX(); 
				message += "," + (npcCtrl.getNPC(i)).getY(); 
				message += "," + (npcCtrl.getNPC(i)).getZ(); 
				sendPacketToAll(message); 
			} catch (IOException e){ e.printStackTrace(); }
		 
	   } 
	   //System.out.println("server sends mnpc" );

   }

 /*  public void sendNPCcreate(NPCcontroller npcCtrl) {
	   for (int i=0; i<npcCtrl.getNumOfNPCs(); i++) 
	   {
		   try 
		   {
			   //format createNPC,id,x,y,z
				String message = new String("createNPC," + Integer.toString(i)); 
				message += "," + (npcCtrl.getNPC(i)).getX(); 
				message += "," + (npcCtrl.getNPC(i)).getY(); 
				message += "," + (npcCtrl.getNPC(i)).getZ(); 
				sendPacketToAll(message); 
			} catch (IOException e){ e.printStackTrace(); }

		   System.out.println("server sends createNPC");
	   } 
   }
   */
}