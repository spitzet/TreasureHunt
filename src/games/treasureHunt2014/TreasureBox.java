package games.treasureHunt2014;

import myGameEngine.CrashEvent;
import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.scene.shape.Cube;

public class TreasureBox extends Cube implements IEventListener
{
	private float tx= 1.1f;
	private float ty = 1.1f;
	private float tz = 1.1f;
	
	public TreasureBox()
	{
		super();
		this.scale(0.7f, 0.7f, 0.7f);
	}

	//handle the event when there is a collision
	public boolean handleEvent(IGameEvent event)
	{
		this.scale(tx, ty, tz);
		return true;
	}
}
