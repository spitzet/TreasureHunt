package a2;

import games.treasureHunt2014.TreasureHunt2014;

public class Starter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(args[0]);
		String servAdd = args[0];
	    int servPort = Integer.parseInt(args[1]);
	    new TreasureHunt2014(servAdd, servPort).start(); 
	    //new TreasureHunt2014(null, w0).start();
	}

}
