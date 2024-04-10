package a2;

import tage.*;
import tage.shapes.*;
import tage.input.*;
import tage.nodeControllers.*;

import java.lang.Math;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.joml.*;

public class MyGame extends VariableFrameRateGame
{
	private static Engine engine;

	private boolean paused=false;
	private int counter=0;
	private double lastFrameTime, currFrameTime, elapsTime, diffTime;
	
	private GameObject dol, cube, plane, torus, sphere, magnet1, magnet2, magnet3, magnet4, grass, xAxis, yAxis, zAxis, ground, sky, terrain, player, paper_player, player3, player4, player2;
	private ObjShape dol_S, cube_S, plane_S, torus_S, sphere_S, magnet_S, grass_S, xAxis_S, yAxis_S, zAxis_S, ground_S, sky_S, terrain_S, player_S;
	private TextureImage doltx, carpetTx, paperTx, woodTx, stoneTx, magnetTx, grassTx, groundTx, skyTx, hillsTx, grass2Tx, playerTx, paper_playerTx, player2Tx, player3Tx, player4Tx;
	private Light light1;
	private int fluffyClouds, lakeIslands; //Skyboxes
	
	private boolean ridingDolphin = false;
	private boolean sitesVisited[] = new boolean[4];
	private int stress = 0;
	private boolean lose = false;
	private boolean enableAxis = true;
	
	private InputManager im, im2;
	private CameraOrbitController orbitController;
	private CameraOverheadController overheadController;
	private NodeController rc_cube, rc_sphere, rc_torus, rc_plane, hc_cube, hc_torus, hc_plane, hc_sphere, rc_sky; //Rotation and Hover

	public MyGame() { super(); }

	public static void main(String[] args)
	{	MyGame game = new MyGame();
		FindComponents.main(args);
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes()
	{	dol_S = new ImportedModel("dolphinHighPoly.obj");
		player_S = new ImportedModel("ninja_lowpoly.obj");
		cube_S = new Cube();
		plane_S = new Plane();
		sphere_S = new Sphere();
		torus_S = new Sphere();
		magnet_S = new Plane();
		grass_S = new Grass();
		xAxis_S = new Line( (new Vector3f(0,0,0)), (new Vector3f(1,0,0)) );
		yAxis_S = new Line( (new Vector3f(0,0,0)), (new Vector3f(0,1,0)) );
		zAxis_S = new Line( (new Vector3f(0,0,0)), (new Vector3f(0,0,1)) );
		ground_S = new Plane();
		sky_S = new Sphere();
		terrain_S = new TerrainPlane(1000);
	}

	@Override
	public void loadTextures()
	{	doltx = new TextureImage("Dolphin_HighPolyUV.png");
		playerTx = new TextureImage("Ninja_uvmap_test.png");
		paper_playerTx = new TextureImage("Ninja_uvmap_test2.png");
		player2Tx = new TextureImage("Ninja_uvmap_test3.png");
		player3Tx = new TextureImage("Ninja_uvmap_test4.png");
		player4Tx = new TextureImage("Ninja_uvmap_test5.png");
		carpetTx = new TextureImage("carpet.JPG");
		paperTx = new TextureImage("paper.JPG");
		woodTx = new TextureImage("wood_planks.jpg");
		stoneTx = new TextureImage("stone.jpg");
		magnetTx = new TextureImage("magnet.png");			//Note: Magnet texture is a play on the "Your did it" picture
		grassTx = new TextureImage("grasspurpleflowers.jpg");
		groundTx = new TextureImage("bricks.jpg");
		skyTx = new TextureImage("sky.png");
		hillsTx = new TextureImage("hills.png");
		grass2Tx = new TextureImage("grass2.jpg");
	}

	@Override
	public void buildObjects()
	{	Matrix4f initialTranslation, initialScale;

		// build dolphin in the center of the window
		dol = new GameObject(GameObject.root(), dol_S, doltx);
		initialTranslation = (new Matrix4f()).translation(0,0,0);
		initialScale = (new Matrix4f()).scaling(3.0f);
		dol.setLocalTranslation(initialTranslation);
		dol.setLocalScale(initialScale);
		
		player = new GameObject(GameObject.root(), player_S, playerTx);
		initialTranslation = (new Matrix4f()).translation(5,4,5);
		initialScale = (new Matrix4f()).scaling(0.9f);
		player.setLocalTranslation(initialTranslation);
		player.setLocalScale(initialScale);
		
		paper_player = new GameObject(GameObject.root(), player_S, paper_playerTx);
		initialTranslation = (new Matrix4f()).translation(8,4,5);
		initialScale = (new Matrix4f()).scaling(0.9f);
		paper_player.setLocalTranslation(initialTranslation);
		paper_player.setLocalScale(initialScale);
		
		player2 = new GameObject(GameObject.root(), player_S, player2Tx);
		initialTranslation = (new Matrix4f()).translation(10,4,5);
		initialScale = (new Matrix4f()).scaling(0.9f);
		player2.setLocalTranslation(initialTranslation);
		player2.setLocalScale(initialScale);
		
		player3 = new GameObject(GameObject.root(), player_S, player3Tx);
		initialTranslation = (new Matrix4f()).translation(12,4,5);
		initialScale = (new Matrix4f()).scaling(0.9f);
		player3.setLocalTranslation(initialTranslation);
		player3.setLocalScale(initialScale);
		
		player4 = new GameObject(GameObject.root(), player_S, player4Tx);
		initialTranslation = (new Matrix4f()).translation(3,4,5);
		initialScale = (new Matrix4f()).scaling(0.9f);
		player4.setLocalTranslation(initialTranslation);
		player4.setLocalScale(initialScale);
		
		cube = new GameObject(GameObject.root(), cube_S, woodTx);
		initialTranslation = (new Matrix4f()).translation(5,0,3);
		initialScale = (new Matrix4f()).scaling(0.5f);
		cube.setLocalTranslation(initialTranslation);
		cube.setLocalScale(initialScale);
		
		plane = new GameObject(GameObject.root(), plane_S, paperTx);
		initialTranslation = (new Matrix4f()).translation(-3,0,-3);
		initialScale = (new Matrix4f()).scaling(0.3f);
		plane.setLocalTranslation(initialTranslation);
		plane.setLocalScale(initialScale);
		
		torus = new GameObject(GameObject.root(), torus_S, stoneTx);
		initialTranslation = (new Matrix4f()).translation(4,0,-3);
		initialScale = (new Matrix4f()).scaling(1.3f);
		torus.setLocalTranslation(initialTranslation);
		torus.setLocalScale(initialScale);
		
		sphere = new GameObject(GameObject.root(), sphere_S, carpetTx);
		initialTranslation = (new Matrix4f()).translation(-3,0,1);
		initialScale = (new Matrix4f()).scaling(0.4f);
		sphere.setLocalTranslation(initialTranslation);
		sphere.setLocalScale(initialScale);
		
		//magnet1 = new GameObject(dol, magnet_S, magnetTx);			I was already setting Dolphin to parent
		magnet1 = new GameObject(GameObject.root(), magnet_S, magnetTx);	//But this way is more appearent
		initialTranslation = (new Matrix4f()).translation(-3,100,-3);
		initialScale = (new Matrix4f()).scaling(0.1f);
		magnet1.setLocalTranslation(initialTranslation);
		magnet1.setLocalScale(initialScale);
		magnet1.setParent(dol);
		magnet1.propagateTranslation(true);
		magnet1.propagateRotation(true);
		
		//magnet2 = new GameObject(dol, magnet_S, magnetTx);
		magnet2 = new GameObject(GameObject.root(), magnet_S, magnetTx);
		initialTranslation = (new Matrix4f()).translation(-3,100,-3);
		initialScale = (new Matrix4f()).scaling(0.1f);
		magnet2.setLocalTranslation(initialTranslation);
		magnet2.setLocalScale(initialScale);
		magnet2.setParent(dol);
		magnet2.propagateTranslation(true);
		magnet2.propagateRotation(true);
		
		//magnet3 = new GameObject(dol, magnet_S, magnetTx);
		magnet3 = new GameObject(GameObject.root(), magnet_S, magnetTx);
		initialTranslation = (new Matrix4f()).translation(-3,100,-3);
		initialScale = (new Matrix4f()).scaling(0.1f);
		magnet3.setLocalTranslation(initialTranslation);
		magnet3.setLocalScale(initialScale);
		magnet3.setParent(dol);
		magnet3.propagateTranslation(true);
		magnet3.propagateRotation(true);
		
		//magnet4 = new GameObject(dol, magnet_S, magnetTx);
		magnet4 = new GameObject(GameObject.root(), magnet_S, magnetTx);
		initialTranslation = (new Matrix4f()).translation(-3,100,-3);
		initialScale = (new Matrix4f()).scaling(0.1f);
		magnet4.setLocalTranslation(initialTranslation);
		magnet4.setLocalScale(initialScale);
		magnet4.setParent(dol);
		magnet4.propagateTranslation(true);
		magnet4.propagateRotation(true);
		
		grass = new GameObject(GameObject.root(), grass_S, grassTx);
		initialTranslation = (new Matrix4f()).translation(2,0,1);
		initialScale = (new Matrix4f()).scaling(1.0f);
		grass.setLocalTranslation(initialTranslation);
		grass.setLocalScale(initialScale);
		
		xAxis = new GameObject(GameObject.root(), xAxis_S);
		(xAxis.getRenderStates()).setColor(new Vector3f(1,0,0));
		yAxis = new GameObject(GameObject.root(), yAxis_S);
		(yAxis.getRenderStates()).setColor(new Vector3f(0,1,0));
		zAxis = new GameObject(GameObject.root(), zAxis_S);
		(zAxis.getRenderStates()).setColor(new Vector3f(0,0,1));
		
		ground = new GameObject(GameObject.root(), ground_S, groundTx);
		initialTranslation = (new Matrix4f()).translation(0,-1,0);
		initialScale = (new Matrix4f()).scaling(1);
		ground.setLocalTranslation(initialTranslation);
		ground.setLocalScale(initialScale);
		ground.setIsTerrain(true);
		(ground.getRenderStates()).setTiling(2);
		(ground.getRenderStates()).setTileFactor(1);
		
		sky = new GameObject(GameObject.root(), sky_S, skyTx);
		initialTranslation = (new Matrix4f()).translation(0,-5,0);
		initialScale = (new Matrix4f()).scaling(1);
		sky.setLocalTranslation(initialTranslation);
		sky.setLocalScale(initialScale);
		
		terrain = new GameObject(GameObject.root(), terrain_S, grass2Tx);
		initialTranslation = (new Matrix4f()).translation(0f,0f,0f);
		terrain.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scale(40f, 1f, 40f);
		terrain.setLocalScale(initialScale);
		terrain.setHeightMap(hillsTx);
		
		terrain.getRenderStates().setTiling(1);
		terrain.getRenderStates().setTileFactor(10);
	}

	@Override
	public void initializeLights()
	{	Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);			//Ambient
		light1 = new Light();								//Positional?
		light1.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		(engine.getSceneGraph()).addLight(light1);
	}
	
	@Override
	public void loadSkyBoxes(){
		fluffyClouds = (engine.getSceneGraph()).loadCubeMap("fluffyClouds");
		lakeIslands = (engine.getSceneGraph()).loadCubeMap("lakeIslands");
		(engine.getSceneGraph()).setActiveSkyBoxTexture(fluffyClouds);
		(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}

	@Override
	public void initializeGame()
	{	lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		elapsTime = 0.0;
		diffTime = 0.0;
		
		im = engine.getInputManager();
		FwdAction fwdAction = new FwdAction(this);
		FwdActionReversed fwdActionReversed = new FwdActionReversed(this);
		TurnAction turnAction = new TurnAction(this);
		TurnActionReversed turnActionReversed = new TurnActionReversed(this);
		PitchAction pitchAction = new PitchAction(this);
		RenderingAction renderingAction = new RenderingAction(xAxis, yAxis, zAxis);
		//HopOnDolphin hod = new HopOnDolphin(this, ridingDolphin);			Don't need to hop on / off
		
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._1, fwdAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._2, fwdActionReversed,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.X, turnAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.Y, fwdActionReversed,		//Idk why but it needs to be reversed
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.W, fwdAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.S, fwdActionReversed,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.A, turnActionReversed,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.D, turnAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._0, renderingAction,
		InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.E, renderingAction,
		InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		
		//im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._3, hod,
		//InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		/*
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.X, turnAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.Y, fwdAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.RY, pitchAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.RZ, fwdAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.RX, turnAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		*/
		
		//Ps4 Controller
		//X & Y = Left Stick (As you expect) 
		//Z is Horizontal Right stick, RZ is Vertical Right Stick
		//RX is Left Trigger (L2 / LT), RY is Right Trigger (R2 / RT)
		
		rc_cube = new RotationController(engine, new Vector3f(0,1,0), 0.001f);
		rc_plane = new RotationController(engine, new Vector3f(0,1,0), 0.001f);
		rc_sphere = new RotationController(engine, new Vector3f(0,1,0), 0.001f);
		rc_torus = new RotationController(engine, new Vector3f(0,1,0), 0.001f);
		rc_sky = new RotationController(engine, new Vector3f(0,1,0), 0.0001f);
		hc_cube = new HoverController(engine, 1.3f, 0.0002f);
		hc_plane = new HoverController(engine, 1.1f, 0.00022f);
		hc_sphere = new HoverController(engine, 1.5f, 0.0001f);
		hc_torus = new HoverController(engine, 1.2f, 0.00017f);
		rc_cube.addTarget(cube); rc_plane.addTarget(plane); rc_sphere.addTarget(sphere); rc_torus.addTarget(torus);
		rc_sky.addTarget(sky); 
		hc_cube.addTarget(cube); hc_plane.addTarget(plane); hc_sphere.addTarget(sphere); hc_torus.addTarget(torus);
		(engine.getSceneGraph()).addNodeController(rc_cube); (engine.getSceneGraph()).addNodeController(rc_sphere); 
		(engine.getSceneGraph()).addNodeController(rc_plane); (engine.getSceneGraph()).addNodeController(rc_torus);
		(engine.getSceneGraph()).addNodeController(hc_cube); (engine.getSceneGraph()).addNodeController(hc_plane);
		(engine.getSceneGraph()).addNodeController(hc_sphere); (engine.getSceneGraph()).addNodeController(hc_torus);
		(engine.getSceneGraph()).addNodeController(rc_sky);
		rc_sky.toggle();
		
		Camera c = (engine.getRenderSystem()).getViewport("LEFT").getCamera();
		Camera c2 = (engine.getRenderSystem()).getViewport("RIGHT").getCamera();
		String gpName = im.getFirstGamepadName();				//Need Gamepad plugged in or it breaks
		System.out.println("gpName: "+gpName);
		orbitController = new CameraOrbitController(c, dol, gpName, engine);
		overheadController = new CameraOverheadController(c2, dol, gpName, engine);
		
		
		(engine.getRenderSystem()).setWindowDimensions(1900,1000);
		for(int i = 0; i < sitesVisited.length; i++){
			sitesVisited[i] = false;
		} 

		// ------------- positioning the camera -------------
		//(engine.getRenderSystem().getViewport("MAIN").getCamera()).setLocation(new Vector3f(0,0,5));
	}
	
	@Override
	public void createViewports()
	{
		(engine.getRenderSystem()).addViewport("LEFT",0,0,1f,1f);
		(engine.getRenderSystem()).addViewport("RIGHT",0.75f,0,0.25f,0.25f);
		
		Viewport leftVp = (engine.getRenderSystem()).getViewport("LEFT");
		Viewport rightVp = (engine.getRenderSystem()).getViewport("RIGHT");
		Camera leftCamera = leftVp.getCamera();
		Camera rightCamera = rightVp.getCamera();
		
		rightVp.setHasBorder(true);
		rightVp.setBorderWidth(4);
		rightVp.setBorderColor(0.0f,1.0f,0.0f);
		
		leftCamera.setLocation(new Vector3f(0,2,0));
		leftCamera.setU(new Vector3f(1,0,0));
		leftCamera.setV(new Vector3f(0,1,0));
		leftCamera.setN(new Vector3f(0,0,-1));
		
		rightCamera.setLocation(new Vector3f(0,2,0));
		rightCamera.setU(new Vector3f(1,0,0));
		rightCamera.setV(new Vector3f(0,0,-1));
		rightCamera.setN(new Vector3f(0,-1,0));
	}

	@Override
	public void update()
	{	// rotate dolphin if not paused
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		if (!paused) elapsTime += (currFrameTime - lastFrameTime) / 1000.0;
		diffTime = (currFrameTime - lastFrameTime) / 1000.0;
		
		if(!rc_sky.isEnabled()){
			//rc_sky.toggle();
		}
		
		//Input manager
		im.update((float)elapsTime);
		
		//Terrain
		Vector3f loc = dol.getWorldLocation();
		float height = terrain.getHeight(loc.x(), loc.z());
		dol.setLocalLocation(new Vector3f(loc.x(), height, loc.z()));
		
		//Put camera on dolphin and keep it there
		/*
		if(ridingDolphin == true){
			Camera cam = (engine.getRenderSystem().getViewport("MAIN").getCamera());
			Vector3f loc = dol.getWorldLocation();
			Vector3f fwd = dol.getWorldForwardVector();
			Vector3f up = dol.getWorldUpVector();
			Vector3f right = dol.getWorldRightVector();
			cam.setU(right);
			cam.setV(up);
			cam.setN(fwd);
			cam.setLocation(loc.add(up.mul(1.3f)).add(fwd.mul(-2.5f)));
		}
		*/
		stress++;
		collisionDetection();
		

		// build and set HUD
		int elapsTimeSec = Math.round((float)elapsTime);
		String elapsTimeStr = Integer.toString(elapsTimeSec);
		String counterStr = Integer.toString(counter);
		String dispStr1 = "Time = " + elapsTimeStr;
		String dispStr2, dispStr4;
		if(counter == 4 && lose == false){
			dispStr2 = "You Win!";
		} else if(stress >= 3000){
			dispStr2 = "You Lose";
			lose = true;
		}
		else {
			dispStr2 = "Score = " + counterStr;
		}
		
		String dispStr3 = "Stress = " + Integer.toString(stress);
		Vector3f hud1Color = new Vector3f(1,0,0);
		Vector3f hud2Color = new Vector3f(0,0,1);
		Vector3f hud3Color = new Vector3f(0,1,0);
		Vector3f hud4Color = new Vector3f(0,1,0.5f);
		if(stress == 0){
			dispStr3 = "Calm";
		}
		else if(stress >= 1000 && stress < 2000){
			hud3Color = new Vector3f(1,1,0);
			 
		} else if (stress >= 2000){
			hud3Color = new Vector3f(1,0.2f,0);
		}else if (stress >= 3000){
			hud3Color = new Vector3f(1,0,1);
		} else {
			hud3Color = new Vector3f(0,1,0);
		}
		
		Vector3f dol_location = dol.getLocalLocation();
		float hud_x = dol_location.x;
		float hud_y = dol_location.y;
		float hud_z = dol_location.z;
		dispStr4 = "" + hud_x + " " + hud_y + " " + hud_z;
		
		int screenX = (engine.getRenderSystem()).getXSize();
		
		(engine.getHUDmanager()).setHUD1(dispStr1, hud1Color, (int)(screenX * 0.2), 15);
		(engine.getHUDmanager()).setHUD2(dispStr2, hud2Color, (int)(screenX * 0.4), 15);
		(engine.getHUDmanager()).setHUD3(dispStr3, hud3Color, (int)(screenX * 0.6), 15);
		(engine.getHUDmanager()).setHUD4(dispStr4, hud4Color, (int)(screenX * 0.8), 15);
		
		orbitController.updateCameraPosition();
		overheadController.updateCameraPosition();
	}
	
	public void collisionDetection(){
		Camera cam = (engine.getRenderSystem().getViewport("LEFT").getCamera());
		Vector3f cube_diff = new Vector3f((dol.getWorldLocation()).sub(cube.getWorldLocation()));
		Vector3f sphere_diff = new Vector3f((dol.getWorldLocation()).sub(sphere.getWorldLocation()));
		Vector3f torus_diff = new Vector3f((dol.getWorldLocation()).sub(torus.getWorldLocation()));
		Vector3f plane_diff = new Vector3f((dol.getWorldLocation()).sub(plane.getWorldLocation()));
		Vector3f grass_diff = new Vector3f((dol.getWorldLocation()).sub(grass.getWorldLocation()));
		
		if(sitesVisited[0] == false && cube_diff.length() < 1.0f && !rc_cube.isEnabled()){
			sitesVisited[0] = true;
			counter++;
			magnet1.setLocalTranslation((new Matrix4f()).translation(0.75f, 0.65f, 0.75f));
			rc_cube.toggle();
			hc_cube.toggle();
		}
		if(sitesVisited[1] == false && sphere_diff.length() < 1.0f && !rc_sphere.isEnabled()){
			sitesVisited[1] = true;
			counter++;
			magnet2.setLocalTranslation((new Matrix4f()).translation(-0.75f, 0.65f, 0.75f));
			rc_sphere.toggle();
			hc_sphere.toggle();
		}
		if(sitesVisited[2] == false && torus_diff.length() < 1.0f && !rc_torus.isEnabled()){
			sitesVisited[2] = true;
			counter++;
			magnet3.setLocalTranslation((new Matrix4f()).translation(-0.75f, 0.65f, -0.75f));
			rc_torus.toggle();
			hc_torus.toggle();
		}
		if(sitesVisited[3] == false && plane_diff.length() < 1.0f && !rc_plane.isEnabled()){
			sitesVisited[3] = true;
			counter++;
			magnet4.setLocalTranslation((new Matrix4f()).translation(0.75f, 0.65f, -0.75f));
			rc_plane.toggle();
			hc_plane.toggle();
		}
		if(grass_diff.length() < 1.5f){
			if(stress < 3000){
				stress = 0;
			}
		}
		
		magnet1.setLocalRotation((new Matrix4f()).rotation((float)elapsTime, 0, 1, 0));
		magnet2.setLocalRotation((new Matrix4f()).rotation((float)elapsTime, 0, 1, 0));
		magnet3.setLocalRotation((new Matrix4f()).rotation((float)elapsTime, 0, 1, 0));
		magnet4.setLocalRotation((new Matrix4f()).rotation((float)elapsTime, 0, 1, 0));
	}

	@Override
	public void keyPressed(KeyEvent e)
	{	
		/*
		switch (e.getKeyCode()){
			case KeyEvent.VK_P:
				rc_cube.toggle();
				rc_sphere.toggle();
				rc_plane.toggle();
				rc_torus.toggle();
				break;
			
			case KeyEvent.VK_O:
				hc_cube.toggle();
				hc_sphere.toggle();
				hc_plane.toggle();
				hc_torus.toggle();
				break;
		}
		/*
		switch (e.getKeyCode())
		{	case KeyEvent.VK_C:
				counter++;
				break;
			case KeyEvent.VK_1:
				break;
			case KeyEvent.VK_W:
				Vector3f fwd = dol.getWorldForwardVector();
				Vector3f loc = dol.getWorldLocation();
				Vector3f newLocation = loc.add(fwd.mul((float)diffTime*15.0f));
				Camera cam = (engine.getRenderSystem().getViewport("LEFT").getCamera());
				
				System.out.println("Elaps: " + (float)diffTime);
				
				Vector3f difference = new Vector3f((cam.getLocation()).sub(dol.getWorldLocation()));
				//System.out.println("difference: "+difference.length());
				if(difference.length() < 10.0f){
					//newLocation = loc.add(fwd.mul((float)diffTime*-100.0f));
					dol.setLocalLocation(newLocation);
					dol.setLocalLocation(newLocation);
				}	
				break;
			case KeyEvent.VK_S:
				fwd = dol.getWorldForwardVector();
				loc = dol.getWorldLocation();
				cam = (engine.getRenderSystem().getViewport("LEFT").getCamera());
				
				difference = new Vector3f((cam.getLocation()).sub(dol.getWorldLocation()));
				
				if(difference.length() < 10.0f){
					newLocation = loc.add(fwd.mul((float)diffTime*-5.0f));
					dol.setLocalLocation(newLocation);
				}
				
				
				break;
			case KeyEvent.VK_SPACE:
				
				ridingDolphin = !ridingDolphin;
				if(ridingDolphin == false){			//Just got off dolphin
					cam = (engine.getRenderSystem().getViewport("LEFT").getCamera());
					loc = dol.getWorldLocation();
					fwd = dol.getWorldForwardVector();
					Vector3f up = dol.getWorldUpVector();
					Vector3f right = dol.getWorldRightVector();
					cam.setU(right);
					cam.setV(up);
					cam.setN(fwd);
					cam.setLocation(loc.add(up.mul(0.3f)).add(right.mul(1.5f)));
				}
				//System.out.print("4 pressed riding dolphin: "+ridingDolphin);
				
				break;
			case KeyEvent.VK_0:
				if(dol.getRenderStates().isWireframe()){
					dol.getRenderStates().setWireframe(false);
				} else {
					dol.getRenderStates().setWireframe(true);
				}
				break;
			case KeyEvent.VK_A:
				dol.yaw((float)diffTime*10.0f);
				break;
			case KeyEvent.VK_D:
				dol.yaw((float)diffTime*-10.0f);
				break;
			case KeyEvent.VK_UP:
				dol.pitch((float)diffTime*10.0f);
				break;
			case KeyEvent.VK_DOWN:
				dol.pitch((float)diffTime*-10.0f);
				break;
		}
		*/  //Don't need for Hw 2
		super.keyPressed(e);
	}
	
	//This will change with future assignments (maybe)
	public GameObject getAvatar(){
		return dol;
	}
	
	//Unused in Assignment 2
	public void setRidingDolphin(boolean b){
		ridingDolphin = b;
		if(ridingDolphin == false){			//Just got off dolphin
			Camera cam = (engine.getRenderSystem().getViewport("LEFT").getCamera());
			Vector3f loc = dol.getWorldLocation();
			Vector3f fwd = dol.getWorldForwardVector();
			Vector3f up = dol.getWorldUpVector();
			Vector3f right = dol.getWorldRightVector();
			cam.setU(right);
			cam.setV(up);
			cam.setN(fwd);
			cam.setLocation(loc.add(up.mul(0.3f)).add(right.mul(1.5f)));
		}
	}
	
	public Engine getEngine(){
		return engine;
	}

}