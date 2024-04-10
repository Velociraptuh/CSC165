package tage;

import java.lang.Math;
import net.java.games.input.Event;
import tage.input.*;
import org.joml.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Component;

/** 
	CameraOverheadController takes an already instanciated camera and provides it with th ability to pan and zoom.
	The Camera Controls are pre-defined and support gamepads and keyboards.
	Zoom in and out is attached to Button 4 and 5 respectfully. On PlayStation, this is buttons L1 and R1. 
	The Pan feature is tied to the POV axis from the hat switch (aka d-pad) on botht the Xbox and Playstation controllers.
	The camera should already be facing downwards with it's desired "pan up" direction being situated as Negative-Z. 
 */
public class CameraOverheadController
{
	
	private Engine engine;
	private Camera camera;			
	private GameObject object;
	private float cameraX;		
	private float cameraY;		
	private float cameraZ;		
	
	/** Creates a new CameraOverheadController object*/
	public CameraOverheadController(Camera cam, GameObject obj, String gpName, Engine e){
		engine = e;
		camera = cam;
		object = obj;
		cameraX = 0f;
		cameraY = 7f;
		cameraZ = 0f;
		setupInputs(gpName);
		updateCameraPosition();
	}
	
	//This camera needs it's own controls, only ones left are D-pad buttons and bumpers.
	
	private void setupInputs(String gp){
		System.out.println("gp: "+gp);
		
		CameraZoomOut cz_out = new CameraZoomOut();
		CameraZoomIn cz_in = new CameraZoomIn();
		CameraPanUp cp_up = new CameraPanUp();
		CameraPanDown cp_down = new CameraPanDown();
		CameraPanLeft cp_left = new CameraPanLeft();
		CameraPanRight cp_right = new CameraPanRight();
		CameraPan cp = new CameraPan();
		
		InputManager im = engine.getInputManager();
		
		im.associateAction(gp, net.java.games.input.Component.Identifier.Button._4, cz_in, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gp, net.java.games.input.Component.Identifier.Button._5, cz_out, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gp, net.java.games.input.Component.Identifier.Axis.POV, cp, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key._8, cz_in, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key._9, cz_out, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key._0, cp_up, 			//Zero
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.P, cp_down, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.O, cp_left, 			//Oh
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.LBRACKET, cp_right, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		
		/*
			PlayStation Controller
			0 = X			7 = R2
			1 = Square		8 = Share
			2 = Circle		9 = Options
			3 = Triangle	10 = L3 / Stick Button
			4 = L1			11 = R3 / Stick Button
			5 = R1			12 = PS Logo Button
			6 = L2			13 = Touchpad
		*/

	}
	
	/** Sets the camera's new position according to the gamepad input */
	public void updateCameraPosition(){
		
		//camera.setLocation(new Vector3f(cameraX,cameraY,cameraZ).add(object.getWorldLocation()));
		camera.setLocation(new Vector3f(cameraX,cameraY,cameraZ));
	}
	
	//Private classes
	private class CameraPan extends AbstractInputAction
	{
		
		@Override 
		public void performAction(float time, Event e){
			
			float amount = e.getValue();
			if(amount == Component.POV.LEFT){
				cameraX -= 0.2f;
			} else if (amount == Component.POV.RIGHT){
				cameraX += 0.2f;
			} else if (amount == Component.POV.UP){
				cameraZ -= 0.2f;
			} else if (amount == Component.POV.DOWN){
				cameraZ += 0.2f;
			}
			
		}
		
	}
	
	private class CameraRecenter extends AbstractInputAction
	{
		@Override
		public void performAction(float time, Event e){
			//Not yet implemented
		}
		
	}
	
	//All Assuming Buttons
	private class CameraPanUp extends AbstractInputAction
	{
		
		@Override 
		public void performAction(float time, Event e){
			
			cameraZ -= 0.2f;
			
		}
		
	}
	
		private class CameraPanDown extends AbstractInputAction
	{
		
		@Override 
		public void performAction(float time, Event e){
			
			cameraZ += 0.2f;
			
		}
		
	}
	
	private class CameraPanLeft extends AbstractInputAction
	{
		
		@Override 
		public void performAction(float time, Event e){
			cameraX -= 0.2f;
		}
		
	}
	
		private class CameraPanRight extends AbstractInputAction
	{
		
		@Override 
		public void performAction(float time, Event e){
			if(e.getValue() > -0.2f && e.getValue() < 0.2f){
				cameraX += 0f;
			} else {
				cameraX += 0.2f;
			}
			
		}
		
	}
	
	private class CameraZoomOut extends AbstractInputAction
	{
		
		@Override 
		public void performAction(float time, Event e){
			
			float amount;
			if(cameraY <= 80f){
				cameraY += 0.2f;
			}
			
		}
		
	}
	
		private class CameraZoomIn extends AbstractInputAction
	{
		
		@Override 
		public void performAction(float time, Event e){
			
			float amount;
			if(cameraY >= 2f){
				cameraY -= 0.2f;
			}
		}
		
	}
	
}