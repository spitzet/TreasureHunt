package npc;

import sage.ai.behaviortrees.BTAction;
import sage.ai.behaviortrees.BTStatus;

public class RunAway extends BTAction 
{ 
	NPC npc;
	public RunAway(NPC n) { npc = n; } 
 
	protected BTStatus update(float elapsedTime) 
	{
		npc.RunAway(); 
		return BTStatus.BH_SUCCESS;
	}
}

