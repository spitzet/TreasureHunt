package games.treasureHunt2014;

import sage.*;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.*;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.SceneNode;
import sage.scene.SkyBox;
import sage.scene.TriMesh;
import sage.scene.shape.*;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.scene.HUDString;
import sage.terrain.AbstractHeightMap;
import sage.terrain.HillHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;
import sage.input.*;
import sage.input.action.*;
import net.java.games.input.*;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.renderer.jogl.*;

import java.awt.event.*;
import java.util.Iterator;
import java.util.Random;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.text.DecimalFormat;

import myGameEngine.ColorController;
import myGameEngine.CrashEvent;
import myGameEngine.DanceAction;
import myGameEngine.MyDisplaySystem;
import myGameEngine.SpinController;
import myGameEngine.TPCamCtrl;
import myGameEngine.WaveAction;
import myGameEngine.XAction;
import myGameEngine.YAction;
import myGameEngine.QuitGameAction;
import net.java.games.input.Component.Identifier.*;
import network.Client;
import network.GhostAvatar;
import npc.GhostNPC;

import javax.script.ScriptEngine; 
import javax.script.ScriptEngineFactory; 
import javax.script.ScriptEngineManager; 
import javax.script.ScriptException; 
import javax.script.Invocable;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.io.*; 
import java.util.*; 

import sage.networking.IGameConnection.ProtocolType;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.IOException;

import sage.model.loader.OBJLoader;
import sage.model.loader.ogreXML.*; 
import sage.scene.Model3DTriMesh; 
import sage.audio.*;


//import com.jogamp.openal.ALFactory;

public class TreasureHunt2014 extends BaseGame {
	private int score = 0;
	
	private float time = 0;
	private HUDString scoreString,  timeString,  p1ID;
	IDisplaySystem display;
	ICamera camera;
	private Group treasures;
	IEventManager eventMgr;
	int numCrashes = 0;
	TreasureBox treasureBox;
	private IRenderer renderer;
	private IInputManager im;
	private SceneNode p1;
	private TPCamCtrl camctrl1;
	private String kbName, gpName;
	private IAction quitGame, moveYAxis, moveBackward, moveForward, moveXAxis, moveLeft, moveRight, wave, gwave, dance, gdance;
	private IInputManager.INPUT_ACTION_TYPE repeat = IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN;
	private IInputManager.INPUT_ACTION_TYPE press = IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY;
	boolean FULL_SCREEN_MODE;
	SkyBox skybox;
	TerrainBlock hillTerrain ;
	private SceneNode rootNode; 
	private ScriptEngine engine; 
	private String scriptName = "scripts" + File.separator + "PlayerName.js"; 
	private File scriptFile; 
	private boolean isConnected=false;
	private Vector3D playerPos;
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private Client thisClient;
	TextureState hobbitTextureState; // for texturing the model 
	Group model, ghostModel; // the Ogre model 
	Model3DTriMesh myObject, ghostObject; // the mesh in the Ogre model 
	Model3DTriMesh[] models;
	public int select = 0;
	IAudioManager audioMgr;
	Sound treasureSound;
	AudioResource resource;
	long lastTime = 0;
	private boolean waveGhost = false;
	private Vector<GhostAvatar> ghostList = new Vector<GhostAvatar>();
	private int numGhosts = 0;
	
	
	public TreasureHunt2014(String serverAddr, int sPort){
	      serverAddress = serverAddr;
	      serverPort = sPort;
	      serverProtocol = ProtocolType.TCP;
	      
	   }
	private IDisplaySystem createDisplaySystem()
	{
		FULL_SCREEN_MODE = false;
		GraphicsDevice settings = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		IDisplaySystem display = new MyDisplaySystem(settings.getDisplayMode().getWidth(), settings.getDisplayMode().getHeight(), settings.getDisplayMode().getBitDepth(), settings.getDisplayMode().getRefreshRate(), FULL_SCREEN_MODE, "sage.renderer.jogl.JOGLRenderer");
		System.out.print("\nWaiting for display creation..."); 
		int count = 0; 
		 
		// wait until display creation completes or a timeout occurs 
		while (!display.isCreated()) 
		{ 
			try { Thread.sleep(10); } 
			catch (InterruptedException e) 
			{ throw new RuntimeException("Display creation interrupted"); } 
		 
			count++; 
			System.out.print("+"); 
			if (count % 80 == 0) { System.out.println(); } 
		 
			if (count > 2000) // 20 seconds (approx.) 
			{
				throw new RuntimeException("Unable to create display"); 
			} 
		} 
		System.out.println(); 
		return display ; 
	}
	
	protected void shutdown()
	{
		display.close();
		super.shutdown();
		 if(thisClient != null){
	         thisClient.sendByeMessage();
	         try {
	            thisClient.shutdown();
	         } catch (IOException e){
	            e.printStackTrace();
	         }
	      }
	}

	protected void initSystem()
	{
		super.initSystem();
		display = createDisplaySystem();
		setDisplaySystem(display);
		getDisplaySystem().setTitle("split screen");
		eventMgr = EventManager.getInstance();
		renderer = getDisplaySystem().getRenderer();
		im = getInputManager();
	}
	
	public void initGame()
	{
		initClient();
		createModels();
		initGameObjects();	
		select = promptModel();
		createPlayers();
		initTerrain();
		keyBinds();
		runScripts();
		initAudio();
		lastTime = 0;
		
		super.update(0.0f);
	}
	
	private void initAudio() {
		audioMgr = AudioManagerFactory.createAudioManager("sage.audio.joal.JOALAudioManager");
		if (!audioMgr.initialize()){
			System.out.println("Audio Manager failed to initialize!");
			return;
		}
		resource = audioMgr.createAudioResource("sounds" + File.separator + "treasure.wav", AudioResourceType.AUDIO_SAMPLE);
		treasureSound = new Sound(resource, SoundType.SOUND_EFFECT,100,true);
		treasureSound.initialize(audioMgr);
		treasureSound.setMaxDistance(100.0f);
		treasureSound.setMinDistance(10.0f);
		treasureSound.setRollOff(5.0f);
//		Vector3D treasurePos = treasureBox.getWorldTranslation().getCol(3);
//		treasureSound.setLocation(new Point3D(treasurePos.getX(),treasurePos.getY(),treasurePos.getZ()));
		treasureSound.setLocation(new Point3D(0,0,0));
		setEarParameters();
		treasureSound.play();
		
		//treasureSound.set
	}
	private void setEarParameters() {
		
		Matrix3D avDir = (Matrix3D) (p1.getWorldRotation().clone()); 
		 float camAz = camctrl1.getAzimuth(); 
		 avDir.rotateY(180.0f-camAz); 
		 Vector3D camDir = new Vector3D(0,0,1); 
		 camDir = camDir.mult(avDir); 
		 
		 audioMgr.getEar().setLocation(camera.getLocation()); 
		 audioMgr.getEar().setOrientation(camDir, new Vector3D(0,1,0));
		
	}
	private int promptModel() {
				
		//Options for the combo box dialog
		String[] choices = {"Female", "Male"};
		JFrame frame = new JFrame();
		int sel;
		//Input dialog with a combo box 
		String picked = (String)JOptionPane.showInputDialog(frame, "Choose your model:", "ComboBox Dialog", JOptionPane.QUESTION_MESSAGE
		                , null, choices, choices[0]);
		if (choices[0] == picked) sel = 0;
		else sel = 1;
	//	System.out.println("model select is " +picked + sel);
		return sel;
	}
	private void createModels()
	{
	OgreXMLParser loader = new OgreXMLParser(); 
		try 
		{
			model = loader.loadModel("models" + File.separator + "new_ava" + File.separator + "Cylinder.001.mesh.xml", 
			"models" + File.separator + "new_ava" + File.separator + "Material1.material", 
			"models" + File.separator + "new_ava" + File.separator + "Cylinder.001.skeleton.xml", "models" + File.separator, sage.texture.Texture.ApplyMode.Replace); 
			model.updateGeometricState(0, true); 
			java.util.Iterator<SceneNode> modelIterator = model.iterator(); 
			myObject = (Model3DTriMesh) modelIterator.next(); 
		 } 
		 catch (Exception e) 
		 {
			 e.printStackTrace(); 
			 System.exit(1); 
		 } 
	

		 myObject.scale(.15f,1f,.15f);
	
		 
	
		 try 
			{
				ghostModel = loader.loadModel("models" + File.separator + "Cube.mesh.xml", 
				"models" + File.separator + "Material.material", 
				"models" + File.separator + "Cube.skeleton.xml", "models" + File.separator, sage.texture.Texture.ApplyMode.Replace); 
				ghostModel.updateGeometricState(0, true); 
				java.util.Iterator<SceneNode> ghostModelIterator = ghostModel.iterator(); 
				ghostObject = (Model3DTriMesh) ghostModelIterator.next(); 
			 } 
			 catch (Exception e) 
			 {
				 e.printStackTrace(); 
				 System.exit(1); 
			 } 
			
			 ghostObject.scale(.15f,.15f,.15f);
			 
			 ghostObject.rotate(90, new Vector3D(0,1,0));
			
		 
		 //myObject.rotate(90, new Vector3D(0,1,0));
		 
		 // texture the object using a texture image file 
		 
		 
		 
	//	 models[0] = myObject;
	//	 models[1] = ghostObject;
		 
		 
	}
	
	private void initTerrain() {
		HillHeightMap myHillHeightMap = new HillHeightMap(1000,2500,2.0f,25f,(byte)2,54132);
		
		myHillHeightMap.setHeightScale(0.1f);
		
		hillTerrain = createTerBlock(myHillHeightMap);
		
		
		//create texture and texture state to color the terrain
		TextureState grassState;
		Texture grassTexture = TextureManager.loadTexture2D("images" + File.separator + "grass.jpg");
		grassTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		grassState = (TextureState)display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
		grassState.setTexture(grassTexture, 0);
		grassState.setEnabled(true);
		
		//apply the texture to the terrain
		hillTerrain.setRenderState(grassState);
		hillTerrain.translate(0, 0.2f, 0);
		addGameWorldObject(hillTerrain);		
		
		
	}

	private TerrainBlock createTerBlock(AbstractHeightMap heightMap) {
		float heightScale = 0.4f;
		Vector3D terrainScale = new Vector3D(1, heightScale,1);
		// use the size of the height map as the size of the terrain
		int terrainSize = heightMap.getSize();
		//specify terrain origin so heightmap (0,0) is at world origin
		float cornerHeight = heightMap.getTrueHeightAtPoint(0, 0) * heightScale;
		Point3D terrainOrigin = new Point3D(-500f,-cornerHeight,-500f);
		//create a terrain block using the height map
		String name = "Terrain:" + heightMap.getClass().getSimpleName();
		TerrainBlock tb = new TerrainBlock(name,terrainSize, terrainScale,heightMap.getHeightData(),terrainOrigin);
		return tb;
	}

	private void createPlayers()
	{
		//p1 = new Pyramid("p1");
		if (select == 0 )
			p1 = myObject;
		else p1 = ghostObject;
		p1.translate(0, 2, -50);
		camera = new JOGLCamera(renderer);
		camera.setPerspectiveFrustum(90, 1, 1, 1000);
		camera.setViewport(0.0, 1.0, 0.0, 1.0);
		addGameWorldObject(p1);
		
		/*
		p2 = new Cube("p2");
		p2.translate(10, 1, -40);
		//p2.rotate(-90, new Vector3D(0, 1, 0));
		camera2 = new JOGLCamera(renderer);
		camera2.setPerspectiveFrustum(60, 2, 1, 1000);
		camera2.setViewport(0.0, 1.0, 0.55, 1.0);
		addGameWorldObject(p2);
		*/
		
		createPlayerHUDs();
		
	}
	
	private void createPlayerHUDs()
	{
		p1ID = new HUDString("Player 1");
		p1ID.setName("Player1ID");
		p1ID.setLocation(0.01, 0.15);
		//p1ID.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		p1ID.setColor(Color.red);
		//p1ID.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		//camera.addToHUD(p1ID);
		//runScripts();
		
		timeString = new HUDString("Time = " +time);
		timeString.setLocation(0.01,0.10);
		camera.addToHUD(timeString);
		scoreString = new HUDString("Score = " + score);
		scoreString.setLocation(0.01,0.05);
		camera.addToHUD(scoreString);
		
		/*
		p2ID = new HUDString("Player 2");
		p2ID.setName("Player2ID");
		p2ID.setLocation(0.01, 0.15);
		p2ID.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		p2ID.setColor(Color.yellow);
		p2ID.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		camera2.addToHUD(p2ID);
		
		timeString2 = new HUDString("Time = " +time);
		timeString2.setLocation(0.01,0.10);
		timeString2.setColor(Color.yellow);
		camera2.addToHUD(timeString2);
		scoreString2 = new HUDString("Score = " + score);
		scoreString2.setLocation(0.01,0.05);
		scoreString2.setColor(Color.yellow);
		camera2.addToHUD(scoreString2);
		*/
	}
	
	private void keyBinds()
	{
		//get and print the controller name for use with gpname as well as the keyboard name
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] cs = ce.getControllers();
		for (int i = 0; i < cs.length; i++){
			System.out.println(cs[i].getName() + i);
		}
		
		//System.out.println("lol");
		kbName = im.getKeyboardName();
		//	System.out.println(im.getKeyboardName());
		//kbName = cs[0].getName();
		//gpName = cs[3].getName() ;
		//System.out.print(gpName);
		//System.out.print(kbName);
		//gpName = "Controller (XBOX 360 For Windows)";
		gpName = "XBOX 360 For Windows (Controller)";
		
		camctrl1 = new TPCamCtrl(camera, p1, im, kbName, gpName);
		//camctrl2 = new TPCamCtrl(camera, p2, im, gpName);
		
		// set GameAction and associate GameAction with the controls
		//player 1 (keyboard)
		moveBackward = new YAction(p1,0.01f,-1,hillTerrain, thisClient);
		im.associateAction(kbName, Key.S,moveBackward,repeat);
		moveForward = new YAction(p1,0.01f,1,hillTerrain, thisClient);
		im.associateAction(kbName, Key.W,moveForward,repeat);
		moveLeft = new XAction(p1, 0.005f,-1,hillTerrain,thisClient);
		im.associateAction(kbName,Key.A,moveLeft,repeat);
		moveRight = new XAction(p1, 0.005f,1,hillTerrain,thisClient);
		im.associateAction(kbName,Key.D,moveRight,repeat);
		
		quitGame = new QuitGameAction(this);
		im.associateAction(kbName, Key.ESCAPE, quitGame, press);
		
		wave = new WaveAction(p1,thisClient);
		im.associateAction(kbName, Key.J,wave,press);
		dance = new DanceAction(p1,thisClient);
		im.associateAction(kbName, Key.K,dance,press);
		//System.out.println("asdf");
		
		
		//player 2 (gamepad)
		moveYAxis = new YAction(p1,0.01f,0,hillTerrain, thisClient);
		im.associateAction(gpName,Axis.Y,moveYAxis,repeat);
		moveXAxis = new XAction(p1,0.01f,0,hillTerrain, thisClient);
		im.associateAction(gpName,Axis.X,moveXAxis,repeat);
		gwave = new WaveAction(p1,thisClient);
		im.associateAction(gpName, Button._0, gwave, press);
		gdance = new DanceAction(p1,thisClient);
		im.associateAction(gpName, Button._1, gdance, press);
		
		
	}
	
	private void runScripts()
	{ 
		 ScriptEngineManager factory = new ScriptEngineManager(); 
		 List<ScriptEngineFactory> list = factory.getEngineFactories(); 
		 engine = factory.getEngineByName("js"); 
		 scriptFile = new File(scriptName);
		 
		 try 
		 {
			 FileReader fileReader = new FileReader(scriptFile); 
			 engine.eval(fileReader); 
			 fileReader.close(); 
		 } 
		 catch (FileNotFoundException e1) 
		 { System.out.println(scriptFile + " not found " + e1); } 
		 catch (IOException e2) 
		 { System.out.println("IO problem with " + scriptFile + e2); } 
		 catch (ScriptException e3) 
		 { System.out.println("ScriptException in " + scriptFile + e3); } 
		 catch (NullPointerException e4) 
		 { System.out.println ("Null ptr exception reading " + scriptFile + e4); } 

		 //camera.removeFromHUD(rootNode);
		 rootNode = (SceneNode) engine.get("rootNode"); 
		 //camera.addToHUD(rootNode);
		 //addGameWorldObject(rootNode); 
	}
	
	protected void render()
	{
		renderer.setCamera(camera);
		super.render();
		/*
		renderer.setCamera(camera2);
		super.render();
		*/
		//System.out.println("one");
	}
	private void initClient(){
		// items as before, plus initializing network: 
	      try{ thisClient = new Client(InetAddress.getByName(serverAddress),serverPort, serverProtocol, this);
	      } catch (UnknownHostException e) {
	         e.printStackTrace();
	      } catch (IOException e){
	         e.printStackTrace();
	      }
	      //join the server
	      if(thisClient != null){
	         thisClient.sendJoinMessage();
	      }   
	   }
	//the update method is called automatically every game tick
	public void update(float elapsedTimeMS){
		if(thisClient != null) thisClient.processPackets();
		// update the position of skybox at the camera location
		Point3D camLoc = camera.getLocation();
		Matrix3D camTranslation = new Matrix3D();
		camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());
		skybox.setLocalTranslation(camTranslation);
		
		Iterator iter = treasures.iterator();
		while(iter.hasNext())
		{				
			SceneNode temp = (SceneNode)iter.next();
			if (temp.getWorldBound().intersects(p1.getWorldBound()))
			{
				numCrashes++;
				score++;
				CrashEvent newCrash = new CrashEvent(numCrashes);
				eventMgr.triggerEvent(newCrash);
				iter.remove();
			}
	/*		if (temp.getWorldBound().intersects(p2.getWorldBound()))
			{
				numCrashes++;
				score2++;
				CrashEvent newCrash = new CrashEvent(numCrashes);
				eventMgr.triggerEvent(newCrash);
				iter.remove();
			}
	*/
		}
			
		//udpate the HUD
		time += elapsedTimeMS;
		DecimalFormat df = new DecimalFormat("0.0");
		
		scoreString.setText("Score = " + score);
		timeString.setText("Time = " +df.format(time/1000));
	//	scoreString2.setText("Score = " + score2);
	//	timeString2.setText("Time = " +df.format(time/1000));
		
		
		// tell BaseGame to update game world state
		super.update(elapsedTimeMS);
		//myObject.updateAnimation(elapsedTimeMS);
		((Model3DTriMesh)p1).updateAnimation(elapsedTimeMS);
		
		// update ghost avatar's animation
		//if (waveGhost){
		//System.out.println(numGhosts);
		if(numGhosts > 0)
		{
			Iterator iter2 = ghostList.iterator();
			while(iter2.hasNext())
			{				
				GhostAvatar temp = (GhostAvatar)iter2.next();
				(temp.getAvatar()).updateAnimation(elapsedTimeMS);
			}
		}
		//}
		
		camctrl1.update(elapsedTimeMS);
		setEarParameters();
		
		
		
		// check if the script has been modified 
		long modTime = scriptFile.lastModified(); 
			 
		if (modTime > lastTime) 
		{ 
			lastTime = modTime; 
			//System.out.println("lel");
			camera.removeFromHUD(rootNode);
			this.runScripts(); 
			//removeGameWorldObject(rootNode);
			
			rootNode = (SceneNode) engine.get("rootNode"); 
			camera.addToHUD(rootNode); 
		} 

		//runScripts();
			
	}
	
	//initialize the game objects
	private void initGameObjects(){
		
		
		//set up the skybox
		skybox = new SkyBox("sky box",50f,50f,50f);
		//load skybox textures
		Texture northTex = TextureManager.loadTexture2D("images" + File.separator + "back.jpg");
		Texture southTex = TextureManager.loadTexture2D("images" + File.separator + "front.jpg");
		Texture eastTex = TextureManager.loadTexture2D("images" + File.separator + "left.jpg");
		Texture westTex = TextureManager.loadTexture2D("images" + File.separator + "right.jpg");
		Texture upTex = TextureManager.loadTexture2D("images" + File.separator + "top.jpg");
			
		//attach textures to skybox
		skybox.setTexture(SkyBox.Face.North,northTex);
		skybox.setTexture(SkyBox.Face.South,southTex);
		skybox.setTexture(SkyBox.Face.East, eastTex);
		skybox.setTexture(SkyBox.Face.West, westTex);
		skybox.setTexture(SkyBox.Face.Up, upTex);
				
				
		// add to group
		addGameWorldObject(skybox);
		
		treasures = new Group("root");
		SceneNode temp = new Cube();
		Random rng = new Random();
		float tx;
		float ty = 1f;
		float tz;
		for (int i = 0; i < 150; i++)
		{
			tx = (rng.nextFloat()*2 -1)*50f;
			tz = (rng.nextFloat()*2 -1)*50f;
			if (i < 50 ) temp = new MyShape();
			else if (i < 100) {
				temp = new Cylinder(true);
				((Cylinder)temp).setColor(Color.magenta);
			}
			else
			{
				temp = new Sphere();
				((Sphere)temp).setColor(Color.yellow);
			}	
			temp.translate(tx, ty, tz);
			treasures.addChild(temp);
		}
			//scale the game objects
		treasures.scale(0.5f, 0.5f,0.5f);
		
		// update from SAGE 627
		treasures.setIsTransformSpaceParent(true);
		for (int i = 0; i < 150; i++)
		{
			if (i < 50 ) temp = new MyShape();
			else if (i < 100) temp = new Cylinder(true);
			else
			{
				temp = new Sphere();
			}	
			temp.setIsTransformSpaceParent(true);
		}
		
		//treasures.updateLocalBound();
		// add the treasure box to the game world
		addGameWorldObject(treasures); 
		
		SpinController sc = new SpinController();
		sc.addControlledNode(treasures);
		treasures.addController(sc);				
		
		treasureBox = new TreasureBox();
		treasureBox.translate(0, 1, 0);
		addGameWorldObject(treasureBox);
		
		
		// xyz axises
		Point3D origin = new Point3D(0,0,0); 
		Point3D xEnd = new Point3D(1000,0,0); 
		Point3D yEnd = new Point3D(0,1000,0); 
		Point3D zEnd = new Point3D(0,0,1000); 
		Line xAxis = new Line (origin, xEnd, Color.red, 2); 
		Line yAxis = new Line (origin, yEnd, Color.green, 2); 
		Line zAxis = new Line (origin, zEnd, Color.blue, 2); 
		addGameWorldObject(xAxis); 
		addGameWorldObject(yAxis); 
		addGameWorldObject(zAxis); 
		
		Rectangle ground = new Rectangle();
		ground.scale(1000f, 1000f, 1f);
		ground.rotate(90, new Vector3D(1, 0, 0));
		ground.setColor(Color.cyan);
		
		//ColorController cc = new ColorController();
		//cc.addControlledNode(ground);
		//ground.addController(cc);
		
		addGameWorldObject(ground);
		// configure game display
		display.setTitle("My game");
		
		//add a listener to the event manager
		eventMgr.addListener(treasureBox, CrashEvent.class);
		
		/*
		OgreXMLParser loader = new OgreXMLParser(); 
		try 
		{
			model = loader.loadModel("./models/new_ava/Cylinder.001.mesh.xml", 
			"./models/new_ava/Material1.material", 
			"./models/new_ava/Cylinder.001.skeleton.xml"); 
			model.updateGeometricState(0, true); 
			java.util.Iterator<SceneNode> modelIterator = model.iterator(); 
			myObject = (Model3DTriMesh) modelIterator.next(); 
		 } 
		 catch (Exception e) 
		 {
			 e.printStackTrace(); 
			 System.exit(1); 
		 } 
		
		 addGameWorldObject(myObject); 
		 myObject.scale(.15f,1f,.15f);
		 
		 //myObject.rotate(90, new Vector3D(0,1,0));
		 
		 // texture the object using a texture image file 
		 
		 Texture hobTexture = TextureManager.loadTexture2D("./models/ava_2.png"); 
		 hobTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace); 
		 hobbitTextureState = (TextureState)display.getRenderer().createRenderState(RenderState.RenderStateType.Texture); 
		 hobbitTextureState.setTexture(hobTexture,0); 
		 hobbitTextureState.setEnabled(true); 
		 myObject.setRenderState(hobbitTextureState); 
		 myObject.updateRenderStates();
		 */
		//addGameWorldObject(myObject); 
		//addGameWorldObject(ghostObject); 
		 
	}
	//methods for handling packets
	public void setIsConnected(boolean con){
	      isConnected = con;
	   }
	
	public Vector3D getPlayerPosition(){
	      Vector3D curLocVec = p1.getLocalTranslation().getCol(3);
	      return curLocVec;
	  }
	   
	public void addGhostAvatar(GhostAvatar ghost){
	      addGameWorldObject(ghost.getAvatar());
	   }
	   
	public void removeGhostAvatar(GhostAvatar ghost){
	      this.removeGameWorldObject(ghost.getAvatar());
	   }
	public void addGhostNPCtoGameWorld(GhostNPC newNPC) {
		this.addGameWorldObject(newNPC.getNPC());
		
	}
	public void setWave(boolean w,GhostAvatar ghost){
		int flag = 0;
		//waveGhost = w;
		if(numGhosts == 0)
		{
			ghostList.add(ghost);
			numGhosts++;
		}
		else
		{
		//String gid = ghost.getID();
			Iterator iter = ghostList.iterator();
			
			while(iter.hasNext())
			{				
				GhostAvatar temp = (GhostAvatar)iter.next();
				if((temp.getID()).equals(ghost.getID()))
				{
					flag = 1;
					break;
				}
			}
			if(flag == 0)
			{
				ghostList.add(ghost);
				numGhosts++;
			}
		}
	}
	/*
	public void setDance(boolean b, GhostAvatar ghost) {
		int flag = 0;
		//waveGhost = w;
		if(numGhosts == 0)
		{
			ghostList.add(ghost);
			numGhosts++;
		}
		else
		{
		//String gid = ghost.getID();
			Iterator iter = ghostList.iterator();
			
			while(iter.hasNext())
			{				
				GhostAvatar temp = (GhostAvatar)iter.next();
				if((temp.getID()).equals(ghost.getID()))
				{
					flag = 1;
					break;
				}
			}
			if(flag == 0)
			{
				ghostList.add(ghost);
				numGhosts++;
			}
		}
		
	}
	*/
}
