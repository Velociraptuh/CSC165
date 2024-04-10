/*
Malkylm Wright
CSC 165-02
Scott Gordon

Assignment 2 - Dolphin Tour:

Controls for Dolphin:
Keyboard:
	W/2 - Moves Dolphin Forward
	S/3 - Moves Dolphin Backward
	A - Yaws Dolphin Left
	D - Yaws Dolphin Right
	Up Arrow - Pitches Dolphin Forward
	Down Arrow - Pitches Dolphin Downward


Controls for Camera:
Keyboard: (Sky/Overhead Camera)
	I - Zooms the sky camera in
	O - Zooms the sky camera out
	U - Pans sky camera North
	H - Pans sky camera West
	J - Pans sky camera South
	K - Pans sky camera East
	1  - Toggles Visibility of World Axes
Controller DualSense: (Orbital Controller)
	Right Joy Stick Up and Down - Radial motion, zooms in and out
	Right Joy Stick Left and Right - Azimuthal motion, rotates around avatar
	Left Joy Stick Up and Down - Elevation, increases elevation above/below avatar

In this version, the lava pool is bigger, objects rotate upon visiting, and the chest blinks upon collection of all items.


*/
//Package
package a2;

//Imports
import tage.*;
import tage.shapes.*;

import tage.input.*;
import tage.input.action.*;
import tage.nodeControllers.InvisibilityController;
import tage.nodeControllers.RotationController;
import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;

import java.lang.Math;
import java.util.Random;
//import java.awt.*;
import java.awt.event.*;
import java.io.*;
//import java.util.*;
import javax.swing.*;
import org.joml.*;
//import org.w3c.dom.events.Event;
import java.net.InetAddress;
import java.net.UnknownHostException;
import tage.networking.IGameConnection.ProtocolType;

public class MyGame extends VariableFrameRateGame
{
	//Fields
	private static Engine engine; //Game engine object
	

	private boolean paused=false, onDolphin=true, colliding=false, visitedObj1=false, visitedObj2=false, visitedObj3=false, visitedObj4=false, gameOver=false, allVisited=false, touchingLava=false, axisLines=true, visitLock1=false, visitLock2=false, visitLock3=false, visitLock4=false, isClientConnected=false;
	private int counter=0, score=0, holder=0;
	private float rotationAmount = 0, moveSpd = 2, turnSpd = 2, toggleSpd;
	private double lastFrameTime, currFrameTime, elapsTime, timePassed;
	private Random rand = new Random();

	//Input Stuff
	private InputManager im;
	private GhostManager gm;

	//Orbit stuff
	private Camera camera;
	private Camera secondaryCam;
	private CameraOrbit3D orbitController;
	private CameraController sCamController;

	//Node Controllers
	private NodeController ic, rc1, rc2, rc3, rc4;
	//private InvisibilityController ic;

	//Viewport Stuff
	Viewport mainVP;
	Viewport secondVP;
	private int mainVLeft;
	private int mainVRight;
	private int mainVBot;
	private int mainVTop;
	private int secVLeft;
	private int secVRight;
	private int secVBot;
	private int secVTop;

	private GameObject dol;
	//GameObject for ground, and terrain
	private GameObject ground, terrain;
	//GameObjects for scattered items
	private GameObject objCube, objSphere, objTorus, objPlane, objDiamond, objLava, objChest, objXLine, objYLine, objZLine, objGhost;
	//Refridgerator magnets
	private GameObject objMagnet1, objMagnet2, objMagnet3, objMagnet4;
	//Testing Player models
	private GameObject player, player2, player3, player4, player5;
	

	private ObjShape dolS;
	//Shape for ground and terrain
	private ObjShape groundS, terrainS;
	//ObjShapes for scattered items
	private ObjShape cubeS, sphereS, torusS, planeS, diamondS, lavaS, chestS, axisS, xS, yS, zS, ghostS;
	private ObjShape magnetS;
	//Shape for player model
	private ObjShape playerS;

	private TextureImage doltx;
	//Texture image for ground, terrain, and terrain height, and testing texture
	private TextureImage groundTx, terrainTx, hillsTx, grass2Tx;
	//Texture image for scattered items
	private TextureImage cubeTx, sphereTx, torusTx, planeTx, diamondTx, lavaTx, chestTx, xTx, yTx, zTx, ghostTx;
	private TextureImage magnet1Tx, magnet2Tx, magnet3Tx, magnet4Tx;
	//Texture Image for testing player models
	private TextureImage playerTx, player2Tx, player3Tx, player4Tx, player5Tx;
	
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;

	private Light light1;
	
	//Skybox
	private int fluffyClouds;
	
	//Terrain following
	private Vector3f lastLocation;

	public MyGame(String serverAddress, int serverPort, String protocol) {
		super(); 
		gm = new GhostManager(this);
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		if (protocol.toUpperCase().compareTo("TCP") == 0)
			this.serverProtocol = ProtocolType.TCP;
		else
			this.serverProtocol = ProtocolType.UDP;
	}
	

	public static void main(String[] args)
	{	MyGame game = new MyGame(args[0], Integer.parseInt(args[1]), args[2]); //Creates game object
		engine = new Engine(game); //Puts game into engine object created earlier
		game.initializeSystem(); //Runs game initalization step
		game.game_loop(); //Stars game loop (update)
	}

	@Override
	/**Load all game shapes into engine */
	public void loadShapes()
	{	dolS = new ImportedModel("dolphinHighPoly.obj");
		cubeS = new Cube();
		magnetS = new Cube();
		groundS = new Plane();
		xS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(400f, 0f, 0f));
		yS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 400f, 0f));
		zS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 400f));
		chestS = new Cube();
		torusS = new Torus();
		planeS = new Plane();
		sphereS = new Sphere();
		lavaS = new Plane();
		diamondS = new ManualDiamond();
		ghostS = new ImportedModel("dolphinHighPoly.obj");
		playerS = new ImportedModel("ninja_lowpoly.obj");
		terrainS = new TerrainPlane(1000);
	}

	@Override
	/**Load all textures into engine */
	public void loadTextures()
	{	doltx = new TextureImage("Dolphin_HighPolyUV.png");
		cubeTx = new TextureImage("cubeTexture.jpg");
		torusTx = new TextureImage("flowersTexture.jpg");
		planeTx = new TextureImage("canTexture.png");
		sphereTx = new TextureImage("faceTexture.png");
		groundTx = new TextureImage("waterTexture.png");
		magnet1Tx = new TextureImage("magnet1Texture.png");
		magnet2Tx = new TextureImage("magnet2Texture.png");
		magnet3Tx = new TextureImage("magnet3Texture.png");
		magnet4Tx = new TextureImage("magnet4Texture.png");
		lavaTx = new TextureImage("lavaTexture.png");
		chestTx = new TextureImage("chest.png");
		xTx = new TextureImage("xAxis.png");
		yTx = new TextureImage("yAxis.png");
		zTx = new TextureImage("zAxis.png");
		diamondTx = new TextureImage("goldTexture.jpg");
		ghostTx = new TextureImage("redDolphin.jpg");
		hillsTx = new TextureImage("hills.png");
		playerTx = new TextureImage("Ninja_uvmap_test.png");
		player2Tx = new TextureImage("Ninja_uvmap_test2.png");
		player3Tx = new TextureImage("Ninja_uvmap_test3.png");
		player4Tx = new TextureImage("Ninja_uvmap_test4.png");
		player5Tx = new TextureImage("Ninja_uvmap_test5.png");
		grass2Tx = new TextureImage("grass2.jpg");
	}

	@Override
	/**Build the declared objects */
	public void buildObjects()
	{	Matrix4f initialTranslation, initialScale, initialRotation, rotateX, rotateY, rotateZ;
		Vector3f xAxis, yAxis, zAxis;
	 

		// build dolphin in the center of the window
		dol = new GameObject(GameObject.root(), dolS, doltx);
		initialTranslation = (new Matrix4f()).translation(0,1f,0);
		initialScale = (new Matrix4f()).scaling(3.0f);
		initialRotation = (new Matrix4f()).rotation(0, 0, 0, 0);
		dol.setLocalTranslation(initialTranslation);
		dol.setLocalScale(initialScale);
		dol.setLocalRotation(initialRotation);
		
		//Ground Object
		ground = new GameObject(GameObject.root(), groundS, groundTx);
		initialTranslation = (new Matrix4f()).translation(0, 0, 0);
		initialScale = (new Matrix4f()).scaling(100.0f);
		ground.setLocalTranslation(initialTranslation);
		ground.setLocalScale(initialScale);

		//Build cube 
		objCube = new GameObject(GameObject.root(), cubeS, cubeTx); //Create new game object
		initialTranslation = (new Matrix4f()).translation(getSpawnCoord(), 2, getSpawnCoord()); //Create the initial transformation matrices (in this sense its the initial translation and the initial scaling of the cube)
		initialScale = (new Matrix4f()).scaling(2f);
		objCube.setLocalTranslation(initialTranslation); //Actually set the different transformations
		objCube.setLocalScale(initialScale);

		//Axis spawn
		objXLine = new GameObject(GameObject.root(), xS, xTx);
		(objXLine.getRenderStates()).setColor(new Vector3f(1f, 0f, 0f));
		//initialScale = (new Matrix4f()).scaling(400f, 0.05f, 0.05f);
		//objXLine.setLocalScale(initialScale);

		objYLine = new GameObject(GameObject.root(), yS, yTx);
		(objYLine.getRenderStates()).setColor(new Vector3f(0f, 0f, 1f));
		//initialScale = (new Matrix4f()).scaling(0.05f, 400f, 0.05f);
		//objYLine.setLocalScale(initialScale);

		objZLine = new GameObject(GameObject.root(), zS, zTx);
		(objZLine.getRenderStates()).setColor(new Vector3f(0f, 1f, 0f));
		//initialScale = (new Matrix4f()).scaling(0.05f, 0.05f, 400f);
		//objZLine.setLocalScale(initialScale);

		//Chest Object
		objChest = new GameObject(GameObject.root(), chestS, chestTx);
		initialTranslation = (new Matrix4f()).translation(2, 0.5f, 2);
		initialScale = (new Matrix4f()).scale(0.5f);
		objChest.setLocalTranslation(initialTranslation);
		objChest.setLocalScale(initialScale);

		//Magnet seriess
		objMagnet1 = new GameObject(GameObject.root(), magnetS, magnet1Tx);
		initialTranslation = (new Matrix4f()).translation(2, 0, 2);
		initialScale = (new Matrix4f()).scaling(0.08f, 0.1f, 0.025f);
		objMagnet1.setLocalTranslation(initialTranslation);
		objMagnet1.setLocalScale(initialScale);

		objMagnet2 = new GameObject(GameObject.root(), magnetS, magnet2Tx);
		initialTranslation = (new Matrix4f()).translation(2, 0, 2);
		initialScale = (new Matrix4f()).scaling(0.08f, 0.1f, 0.025f);
		objMagnet2.setLocalTranslation(initialTranslation);
		objMagnet2.setLocalScale(initialScale);

		objMagnet3 = new GameObject(GameObject.root(), magnetS, magnet3Tx);
		initialTranslation = (new Matrix4f()).translation(2, 0, 2);
		initialScale = (new Matrix4f()).scaling(0.08f, 0.1f, 0.025f);
		objMagnet3.setLocalTranslation(initialTranslation);
		objMagnet3.setLocalScale(initialScale);

		objMagnet4 = new GameObject(GameObject.root(), magnetS, magnet4Tx);
		initialTranslation = (new Matrix4f()).translation(2, 0, 2);
		initialScale = (new Matrix4f()).scaling(0.08f, 0.1f, 0.025f);
		objMagnet4.setLocalTranslation(initialTranslation);
		objMagnet4.setLocalScale(initialScale);

		//Torus
		objTorus = new GameObject(GameObject.root(), torusS, torusTx);
		initialTranslation = (new Matrix4f()).translation(getSpawnCoord(), 0.25f, getSpawnCoord());
		initialScale = (new Matrix4f()).scaling(1f);
		objTorus.setLocalTranslation(initialTranslation);
		objTorus.setLocalScale(initialScale);

		//Plane
		objPlane = new GameObject(GameObject.root(), planeS, planeTx);
		initialTranslation = (new Matrix4f()).translation(getSpawnCoord(), -10, getSpawnCoord());
		xAxis = objPlane.getLocalRightVector();
		yAxis = objPlane.getLocalUpVector();
		Matrix4f planeRotation; //This is how rotation is done
		rotateX = (new Matrix4f()).rotation((float)Math.toRadians(270), xAxis);
		float randTurn = (float)(Math.toRadians(rand.nextInt(360)));
		rotateY = (new Matrix4f()).rotation(randTurn, yAxis);
		planeRotation = rotateY.mul(rotateX.mul(objPlane.getLocalRotation())); 
		initialScale = (new Matrix4f()).scaling(3f);
		objPlane.setLocalTranslation(initialTranslation);
		objPlane.setLocalRotation(planeRotation);
		objPlane.setLocalScale(initialScale);

		//Sphere
		objSphere = new GameObject(GameObject.root(), sphereS, sphereTx);
		initialTranslation = (new Matrix4f()).translation(getSpawnCoord(), 3, getSpawnCoord());
		initialScale = (new Matrix4f()).scaling(3f);
		objSphere.setLocalTranslation(initialTranslation);
		objSphere.setLocalScale(initialScale);

		//Lava Object
		objLava = new GameObject(GameObject.root(), lavaS, lavaTx);
		initialTranslation = (new Matrix4f()).translation(getSpawnCoord(), 0.1f, getSpawnCoord());
		initialScale = (new Matrix4f()).scaling(3.5f);
		objLava.setLocalTranslation(initialTranslation);
		objLava.setLocalScale(initialScale);


		//Diamond Object, which is the child object of the dolphin
		objDiamond = new GameObject(GameObject.root(), diamondS, diamondTx);
		initialScale = (new Matrix4f()).scaling(0.5f, 1f, 0.5f);
		initialScale = (new Matrix4f()).scaling(0.15f);
		objDiamond.setLocalScale(initialScale);
		objDiamond.setParent(dol);
		objDiamond.propagateTranslation(true);
		objDiamond.propagateRotation(false);
		
		//Terrain texture and height generation
		terrain = new GameObject(GameObject.root(), terrainS, grass2Tx);
		initialTranslation = (new Matrix4f()).translation(0f,0f,0f);
		terrain.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scale(40f, 1f, 40f);
		terrain.setLocalScale(initialScale);
		terrain.setHeightMap(hillsTx);
		
		terrain.getRenderStates().setTiling(1);
		terrain.getRenderStates().setTileFactor(10);
		
		//Testing Player avatars
		player = new GameObject(GameObject.root(), playerS, playerTx);
		initialTranslation = (new Matrix4f()).translation(5,4,5);
		initialScale = (new Matrix4f()).scaling(0.9f);
		player.setLocalTranslation(initialTranslation);
		player.setLocalScale(initialScale);
		
		player2 = new GameObject(GameObject.root(), playerS, player2Tx);
		initialTranslation = (new Matrix4f()).translation(8,4,5);
		initialScale = (new Matrix4f()).scaling(0.9f);
		player2.setLocalTranslation(initialTranslation);
		player2.setLocalScale(initialScale);
		
		player3 = new GameObject(GameObject.root(), playerS, player3Tx);
		initialTranslation = (new Matrix4f()).translation(11,4,5);
		initialScale = (new Matrix4f()).scaling(0.9f);
		player3.setLocalTranslation(initialTranslation);
		player3.setLocalScale(initialScale);
		
		player4 = new GameObject(GameObject.root(), playerS, player4Tx);
		initialTranslation = (new Matrix4f()).translation(14,4,5);
		initialScale = (new Matrix4f()).scaling(0.9f);
		player4.setLocalTranslation(initialTranslation);
		player4.setLocalScale(initialScale);
		
		player5 = new GameObject(GameObject.root(), playerS, player5Tx);
		initialTranslation = (new Matrix4f()).translation(17,4,5);
		initialScale = (new Matrix4f()).scaling(0.9f);
		player5.setLocalTranslation(initialTranslation);
		player5.setLocalScale(initialScale);
	}
	
	/**Gets the single value coordinate to return to the translation function. This increases an offset padding so shapes don't spawn in the middle of the screen */
	public float getSpawnCoord() {
		float x = rand.nextFloat(14);
		x += 5;
		int n = rand.nextInt(5);
		if(n%2 == 0){
			x *= -1;
		}
		return x;
	}

	@Override
	/**Initialize all lights in game */
	public void initializeLights()
	{	Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		light1 = new Light();
		light1.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		(engine.getSceneGraph()).addLight(light1);
	}
	
	@Override
	/**Initialize skybox */
	public void loadSkyBoxes(){
		fluffyClouds = (engine.getSceneGraph()).loadCubeMap("fluffyClouds");
		(engine.getSceneGraph()).setActiveSkyBoxTexture(fluffyClouds);
		//(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}

	@Override
	/**Initialize game settings */
	public void initializeGame()
	{	lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		elapsTime = 0.0;
		(engine.getRenderSystem()).setWindowDimensions(1900,1000);

		// ------------- Input Section -------------------
		im = engine.getInputManager();
		lastLocation = dol.getWorldLocation(); //Terrain following check


		// ------------- positioning the camera -------------
		camera = (engine.getRenderSystem().getViewport("PRIMARY").getCamera());
		secondaryCam = (engine.getRenderSystem().getViewport("SECONDARY").getCamera());
		// ------------- more camera stuff -------------
		String gpName = im.getFirstGamepadName();
		
		orbitController = new CameraOrbit3D(camera, dol, gpName, engine);
		sCamController = new CameraController(secondaryCam, engine);


		FwdAction fwdAc = new FwdAction(this); //add protclient
		YawAction turnAc = new YawAction(this);
		PitchAction pitchAc = new PitchAction(this);

		ground.getRenderStates().setTiling(1);
		ground.getRenderStates().setTileFactor(7);

		//----------------InvisibilityController- Node Controllers ------------------
		ic = new InvisibilityController(engine, 3f);
		rc1 = new RotationController(engine, new Vector3f(0, 1, 0), 0.01f);
		rc2 = new RotationController(engine, new Vector3f(0, 1, 0), 0.01f);
		rc3 = new RotationController(engine, new Vector3f(0, 1, 0), 0.01f);
		rc4 = new RotationController(engine, new Vector3f(0, 1, 0), 0.01f);
		ic.addTarget(objChest);
		rc1.addTarget(objCube);
		rc2.addTarget(objPlane);
		rc3.addTarget(objSphere);
		rc4.addTarget(objTorus);
		(engine.getSceneGraph()).addNodeController(ic);
		(engine.getSceneGraph()).addNodeController(rc1);
		(engine.getSceneGraph()).addNodeController(rc2);
		(engine.getSceneGraph()).addNodeController(rc3);
		(engine.getSceneGraph()).addNodeController(rc4);

		setupNetworking();
		
	}



	@Override
	public void createViewports(){
		(engine.getRenderSystem()).addViewport("PRIMARY", 0, 0, 1f, 1f);
		(engine.getRenderSystem()).addViewport("SECONDARY", 0, 0.75f, 0.25f, 0.25f);
		mainVP = (engine.getRenderSystem()).getViewport("PRIMARY");
		secondVP = (engine.getRenderSystem()).getViewport("SECONDARY");
		secondVP.setHasBorder(true);
		secondVP.setBorderWidth(4);
		secondVP.setBorderColor(0.5f, 0.25f, 0.5f);
		mainVLeft = (int)mainVP.getActualLeft();
		mainVRight = (int)mainVP.getActualWidth();
		mainVBot = (int)mainVP.getActualBottom();
		mainVTop = (int)mainVP.getActualHeight();
		secVLeft = (int)secondVP.getActualLeft();
		secVRight = (int)secondVP.getActualWidth();
		secVBot = (int)secondVP.getActualBottom();
		secVTop = (int)secondVP.getActualHeight();
	}

	@Override
	/**Continuously run game loop */
	public void update()
	{	
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
 
		
		timePassed = (currFrameTime - lastFrameTime) / 1000.0;
		
		elapsTime += timePassed;

		//System.out.println(elapsTime);
		//System.out.println(timePassed);

		Vector3f loc, newLocation, fwd, up, right;
		Matrix4f currRoto, newRoto, xRot, yRot, zRot, New4fLoco;

		orbitController.updateCameraPosition();
	
		

		if(!touchingLava)
			im.update((float)elapsTime);
		
		loc = dol.getWorldLocation();
		float height = terrain.getHeight(loc.x(), loc.z());
		dol.setLocalLocation(new Vector3f(loc.x(), height, loc.z()));
		

		loc = dol.getWorldLocation();

		//Dolphins Diamond
		
		//objDiamond.setLocalLocation(loc);
		New4fLoco = objDiamond.getLocalTranslation();
		New4fLoco.translation(0, 1.75f, 0);
		//New4fLoco.mul(1.75f);
		//newLocation = loc.add(up.x(), up.y(), up.z());
		objDiamond.setLocalTranslation(New4fLoco);
		objDiamond.setLocalRotation((new Matrix4f()).rotation((float)elapsTime, 0, 1, 0));
		
		//Collision logic
		checkCollisionObjects();


		//Magnet placing
		if(visitedObj1){
			if(visitLock1==false){
				visitLock1 = true;
				rc1.toggle();
			}
			loc = dol.getWorldLocation(); //Aligns the vectors with dolphin
			fwd = dol.getLocalForwardVector();
			up = dol.getLocalUpVector();
			right = dol.getLocalRightVector();
			up.mul(1f); //Moves the vectors
			right.mul(-0.1f);
			newLocation = loc.add(right.x(), right.y(), right.z()).add(up.x(), up.y(), up.z());//Applies changes
			objMagnet1.setLocalLocation(newLocation); //Sets changes
			yRot = (new Matrix4f()).rotation((float)Math.toRadians(90), up); //Same run through for rotations
			currRoto = dol.getLocalRotation();
			newRoto = yRot.mul(currRoto);
			objMagnet1.setLocalRotation(newRoto);
		}
		
		if(visitedObj2){
			if(visitLock2==false){
				visitLock2=true;
				rc2.toggle();
			}
			loc = dol.getWorldLocation();
			fwd = dol.getLocalForwardVector();
			up = dol.getLocalUpVector();
			right = dol.getLocalRightVector();
			up.mul(1f);
			right.mul(0.1f);
			newLocation = loc.add(right.x(), right.y(), right.z()).add(up.x(), up.y(), up.z());
			objMagnet2.setLocalLocation(newLocation);
			yRot = (new Matrix4f()).rotation((float)Math.toRadians(90), up);
			currRoto = dol.getLocalRotation();
			newRoto = yRot.mul(currRoto);
			objMagnet2.setLocalRotation(newRoto);
		}
		
		if(visitedObj3){
			if(visitLock3==false){
				visitLock3 = true;
				rc3.toggle();
			}
			loc = dol.getWorldLocation();
			fwd = dol.getLocalForwardVector();
			up = dol.getLocalUpVector();
			right = dol.getLocalRightVector();
			up.mul(0.5f);
			right.mul(-0.2f);
			fwd.mul(-0.3f);
			newLocation = loc.add(right.x(), right.y(), right.z()).add(up.x(), up.y(), up.z()).add(fwd.x(), fwd.y(), fwd.z());
			objMagnet3.setLocalLocation(newLocation);
			yRot = (new Matrix4f()).rotation((float)Math.toRadians(0), up);
			currRoto = dol.getLocalRotation();
			newRoto = yRot.mul(currRoto);
			objMagnet3.setLocalRotation(newRoto);
		}

		if(visitedObj4){
			if(visitLock4 == false){
				visitLock4 = true;
				rc4.toggle();
			}
			loc = dol.getWorldLocation();
			fwd = dol.getLocalForwardVector();
			up = dol.getLocalUpVector();
			right = dol.getLocalRightVector();
			up.mul(0.5f);
			right.mul(0.2f);
			fwd.mul(-0.3f);
			newLocation = loc.add(right.x(), right.y(), right.z()).add(up.x(), up.y(), up.z()).add(fwd.x(), fwd.y(), fwd.z());
			objMagnet4.setLocalLocation(newLocation);
			yRot = (new Matrix4f()).rotation((float)Math.toRadians(0), up);
			currRoto = dol.getLocalRotation();
			newRoto = yRot.mul(currRoto);
			objMagnet4.setLocalRotation(newRoto);
		}

		if(getDistanceGameObjs(dol, objLava) < 2){
			touchingLava = true;
		}


		checkGameState();
		processNetworking((float)elapsTime);

		// build and set HUD
		int elapsTimeSec = Math.round((float)elapsTime);
		
		
	
		String elapsTimeStr = Integer.toString(elapsTimeSec);
		String dispScore = Integer.toString(score);
		String counterStr = Integer.toString(counter);
		String dispStr1 = "Time = " + elapsTimeStr;//"Score = " + score;
		String dispStr2 = ""; 
		String dispStr3 = "" + getAvatar().getWorldLocation();

		if(gameOver && !touchingLava){
			dispStr2 = "You win!";//"Time = " + elapsTimeStr;
		}else if(gameOver && touchingLava){
			dispStr2 = "Game Over, touching lava!";
		}else{
			dispStr2 = "Score = " + dispScore;
		}
	
		Vector3f hud1Color = new Vector3f(1,1,1);
		Vector3f hud2Color = new Vector3f(1,1,1);
		Vector3f hud3Color = new Vector3f(1,1,1);

		//Vector3f hud4Color = new Vector3f(1,1,1);
		//Vector3f hud5Color = new Vector3f(1,1,1);
		(engine.getHUDmanager()).setHUD1(dispStr1, hud1Color, mainVLeft + mainVRight/8, 20);
		(engine.getHUDmanager()).setHUD2(dispStr2, hud2Color, mainVLeft + 2*(mainVRight/8), 20);
		(engine.getHUDmanager()).setHUD3(dispStr3, hud3Color, secVLeft + secVRight/7, mainVTop-secVBot);
		//(engine.getHUDmanager()).setHUD4(dispStr4, hud4Color, 500, 50);
		//(engine.getHUDmanager()).setHUD5(dispStr5, hud5Color, 15, 50);
		
		
		
	}

	/**Gets distance between dolphin and camera */
	public float getDistDolCam(){
		Camera cam = (engine.getRenderSystem().getViewport("MAIN").getCamera()); //Sets up camera object
		return (dol.getWorldLocation()).distance(cam.getLocation()); 		
	}

	
	/**Checks to see if that game is still playing or if the player won  */
	public void checkGameState(){
		if( visitedObj1==true && visitedObj2==true && visitedObj3==true && visitedObj4==true){
			gameOver = true;
			if(allVisited==false){
				allVisited=true;
				ic.toggle();
			}

		}
		if( touchingLava ){
			gameOver = true;
			paused = true;
		}
	}

	/**Aligns location and x, y, z vectors with a certain GameObject's */
	public void resetVectors(Vector3f a, Vector3f b, Vector3f c, Vector3f d, GameObject e){
		a = e.getWorldLocation();
		b = e.getLocalRightVector();
		c = e.getLocalUpVector();
		d = e.getLocalForwardVector();
	}

	/**Gets distance between a specified object location and the camera*/
	public float getDistObjCam(Vector3f x){
		Camera cam = (engine.getRenderSystem().getViewport("MAIN").getCamera()); //Sets up camera object
		return x.distance(cam.getLocation());
	}

	/**Gets distance between two game objects */
	public float getDistanceGameObjs(GameObject a, GameObject b){
		Vector3f aVec = a.getWorldLocation();
		Vector3f bVec = b.getWorldLocation();
		return aVec.distance(bVec);
	}

	/**Returns game avatar */
	public GameObject getAvatar(){
		return dol;
	}

	/**Returns engines elapsed time */
	public float getElapsedTime(){
		return (float)elapsTime;
	}

	/**Runs a constant "collision check" to see if the player has visited these sites, and keeps track */
	public void checkCollisionObjects() {
		Vector3f dolVec = dol.getWorldLocation();
		Vector3f loc, newLocation;
		Vector3f right = dol.getLocalRightVector();
		Vector3f up = dol.getLocalUpVector();
		Vector3f fwd = dol.getLocalForwardVector();
		Vector3f cubeVec = objCube.getWorldLocation(); //obj 1, <3 is a collision
		Vector3f planeVec = objPlane.getWorldLocation(); //obj 2, <5 is a collision>
		Vector3f sphereVec = objSphere.getWorldLocation(); //obj 3, <5.25 is a collision
		Vector3f torusVec = objTorus.getWorldLocation(); //obj 4, if same, 2.5 is a collision
		
		//Finish rest of code here on home machine
		if(getDistanceGameObjs(dol, objCube) < 3 && visitedObj1==false){
			visitedObj1 = true;
			score++;
		}
		if(getDistanceGameObjs(dol, objPlane) < 5 && visitedObj2==false){
			visitedObj2 = true;
			score++;
		}
		if(getDistanceGameObjs(dol, objSphere) < 5.25 && visitedObj3==false){
			visitedObj3 = true;
			score++;
		}
		if(getDistanceGameObjs(dol, objTorus) < 2.5 && visitedObj4==false){
			visitedObj4 = true;
			score++;
		}
	}

	@Override
	/**Input checking with keypressed method */
	public void keyPressed(KeyEvent e)
	{
		Vector3f loc, fwd, up, right, newLocation, testLoc; //Creates 3d vectors for locations and directions
		Matrix4f newRoto, currRoto, rot, xRot, yRot, zRot;
		Camera cam; //Initialize camera object
		

		
			switch (e.getKeyCode())
		{	
			case KeyEvent.VK_C:
				break;

			case KeyEvent.VK_1:
				//Now toggles world axis lines
				if(axisLines==true){
					objXLine.getRenderStates().disableRendering();
					objYLine.getRenderStates().disableRendering();
					objZLine.getRenderStates().disableRendering();
					axisLines=false;
				}else{
					objXLine.getRenderStates().enableRendering();
					objYLine.getRenderStates().enableRendering();
					objZLine.getRenderStates().enableRendering();
					axisLines=true;
				}
				break;

			case KeyEvent.VK_W:
			case KeyEvent.VK_2: //Move dolphin forward
				//dol.getRenderStates().setWireframe(true);
		
				fwd = dol.getWorldForwardVector(); //Sets fwd to the world forward vector
				loc = dol.getWorldLocation(); //Sets loc to current dolphin location
				newLocation = loc.add(fwd.mul((float)(timePassed * 12f))); //Sets new location to along the forward vector * .02 ahead
				testLoc = (new Vector3f(newLocation.x(), 0, newLocation.z()));
				//System.out.println(newLocation.y());
				
				dol.setLocalLocation(newLocation);	//Actually sets dolphin location to new location
						protClient.sendMoveMessage(dol.getWorldLocation());
				
				
				
				break;

			case KeyEvent.VK_S:
			case KeyEvent.VK_3: //Move dolphin backward
				//dol.getRenderStates().setWireframe(false);
				
				fwd = dol.getWorldForwardVector(); //Similar to backward moving
				loc = dol.getWorldLocation();
				newLocation = loc.add(fwd.mul(-(float)(timePassed * 12f)));
				
				dol.setLocalLocation(newLocation);	//Actually sets dolphin location to new location
				protClient.sendMoveMessage(dol.getWorldLocation());

				break;
				
			// Yaw command
			case KeyEvent.VK_A:
				if(!touchingLava)
					dol.yaw((float)(timePassed * 50f));		
				break;

			case KeyEvent.VK_D:
				if(!touchingLava)
					dol.yaw(-(float)(timePassed * 50f));
				break;

			// Pitch command
			case KeyEvent.VK_UP:
				if(!touchingLava)
					dol.pitch((float)(timePassed * 50f));
				
				break;
				
			case KeyEvent.VK_DOWN:
				if(!touchingLava)				
					dol.pitch(-(float)(timePassed * 50f));
				
				break;

			case KeyEvent.VK_SPACE:
				/*if(onDolphin){
					onDolphin = false;
					cam = (engine.getEngine().getRenderSystem()).getViewport("MAIN").getCamera();
					loc = dol.getWorldLocation();
					fwd = dol.getLocalForwardVector();
					up = dol.getLocalUpVector();
					right = dol.getLocalRightVector();
					right.mul(-1f);
					newLocation = loc.add(right.x(), right.y(), right.z());
					cam.setLocation(loc);
				}else{
					onDolphin = true;
				}*/
				break;


			case KeyEvent.VK_4: //Keys 4-6 open for commands

				break;
			
			case KeyEvent.VK_5:

				break;
				
			case KeyEvent.VK_6:

				break;
		}

		super.keyPressed(e);
	}

	public ObjShape getGhostShape() { return ghostS; }
	public TextureImage getGhostTexture() { return ghostTx; }
	public GhostManager getGhostManager() { return gm; }
	public Engine getEngine() { return engine; }

	private void setupNetworking()
	{	isClientConnected = false;	
		try 
		{	protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
		} 	catch (UnknownHostException e) 
		{	e.printStackTrace();
		}	catch (IOException e) 
		{	e.printStackTrace();
		}
		if (protClient == null)
		{	System.out.println("missing protocol host");
		}
		else
		{	// Send the initial join message with a unique identifier for this client
			System.out.println("sending join message to protocol host");
			System.out.println("val: " + serverAddress + serverPort + serverProtocol);
			protClient.sendJoinMessage();
			//Handle ghost avatars
			System.out.println(gm.getGhosts());

		}
	}

	protected void processNetworking(float elapsTime)
	{	// Process packets received by the client from the server
		if (protClient != null)
			protClient.processPackets();
	}

	public Vector3f getPlayerPosition() { return dol.getWorldLocation(); }

	public void setIsConnected(boolean value) { this.isClientConnected = value; }
	
	private class SendCloseConnectionPacketAction extends AbstractInputAction
	{	@Override
		public void performAction(float time, net.java.games.input.Event evt) 
		{	if(protClient != null && isClientConnected == true)
			{	protClient.sendByeMessage();
			}
		}
	}



}