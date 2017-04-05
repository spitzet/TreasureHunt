package myGameEngine;

import sage.event.AbstractGameEvent;
import sage.event.*;
import java.util.UUID;

public class CrashEvent extends AbstractGameEvent {
	
	private int whichCrash;
	public CrashEvent(int n){ whichCrash = n; }	
	public int getWhichCrash(){	return whichCrash; }

}
