package tage;

import java.lang.Math;
import net.java.games.input.Event;
import tage.input.*;
import org.joml.*;
import tage.input.action.AbstractInputAction;
	
/** 
	CameraOrbitController takes an already existing camera and adds Orbit functionality to gamepad and keyboard inputs.
	The Camera can be thought of as a bubble around the given game object. 
	The Controller allows the user to Orbit horizontally along the bubble (Azimuth), Orbit Vertically along the bubble (Elevation Control),
	and increase or decrease the radius of the bubble (Zoom)
	The Azimuth Action is tied to a controller's Z-axis. On PlayStation, this is the Horizontal axis on the right stick.
	The Elevation Action is tied to a controller's RY and RX axii. On PlayStation, this is the left and right triggers.
	The Radius / Zoom Action is tied to the controller's RZ axis. On PlayStation, this is the Vertical axis on the right stick. 
 */
public class CameraOrbitController 
{
	private Engine engine;
	private Camera camera;			
	private GameObject object;
	private float cameraAzimuth;		//Rotation around Y axis
	private float cameraElevation;		//Translation on Y Axis
	private float cameraRadius;			//hypotenuse of X & Z Axis (Distance from Object)
	private float lastTime;
	
	/** Creates a CameraOrbitController object around a specified Game Object */
	public CameraOrbitController(Camera cam, GameObject obj, String gpName, Engine e){
		engine = e;
		camera = cam;
		object = obj;
		cameraAzimuth = 0.0f;			//Start Behind
		cameraElevation = 20.0f;		//Start Above
		cameraRadius = 2.0f;			//Start Before
		setupInputs(gpName);
		updateCameraPosition();
	}
	
	private void setupInputs(String gp){
		System.out.println("gp: "+gp);
		OrbitAzimuthAction azmAction = new OrbitAzimuthAction();	//Need OrbitAzimuthAction class
		OrbitElevationAction elvAction = new OrbitElevationAction();
		OrbitElevationActionReversed elvActionReversed = new OrbitElevationActionReversed();
		OrbitRadiusAction radAction = new OrbitRadiusAction();
		
		OrbitAzimuthActionButtonReversed azmButtonR = new OrbitAzimuthActionButtonReversed();
		OrbitAzimuthActionButton azmButton = new OrbitAzimuthActionButton();
		OrbitRadiusActionButtonReversed radButtonR = new OrbitRadiusActionButtonReversed();
		OrbitRadiusActionButton radButton = new OrbitRadiusActionButton();
		
		InputManager im = engine.getInputManager();
		
		im.associateAction(gp, net.java.games.input.Component.Identifier.Axis.Z,
			azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gp, net.java.games.input.Component.Identifier.Axis.RY,
			elvAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gp, net.java.games.input.Component.Identifier.Axis.RX,
			elvActionReversed, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gp, net.java.games.input.Component.Identifier.Axis.RZ,
			radAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//RZ = Right Vertical, RY = Right Trigger, RX = Left Trigger
		
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key._1, azmButton, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key._2, azmButtonR, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key._3, elvAction, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key._4, elvActionReversed, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key._5, radButtonR, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key._6, radButton, 		
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}
	/** Converts the spherical coordinate transformations into Cartesian coordinates and sets the camera to those coordinates */
	public void updateCameraPosition(){
		Vector3f objectRot = object.getWorldForwardVector();
		double objectAngle = Math.toDegrees((double) objectRot.angleSigned(new Vector3f(0,0,-1), new Vector3f(0,1,0)));
		float totalAz = cameraAzimuth - (float) objectAngle;
		double theta = Math.toRadians(totalAz);
		double phi = Math.toRadians(cameraElevation);
		float x = cameraRadius * (float)(Math.cos(phi) * Math.sin(theta));
		float y = cameraRadius * (float)(Math.sin(phi));
		float z = cameraRadius * (float)(Math.cos(phi) * Math.cos(theta));
		camera.setLocation(new Vector3f(x,y,z).add(object.getWorldLocation()));
		camera.lookAt(object);
	}
	
	//Private classes
	private class OrbitAzimuthAction extends AbstractInputAction
	{
		@Override
		public void performAction(float time, Event event){
			//System.out.println("Azi = "+event.getValue());
			//float timeDiff = time - lastTime;
			
			float rotAmount;
			if(event.getValue() < -0.2){
				rotAmount = -0.4f;
			} else {
				if (event.getValue() > 0.2){
					rotAmount = 0.4f;
				} else {
					rotAmount = 0.0f;
				}
			}
			//rotAmount *= timeDiff;
			
			
			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
			updateCameraPosition();
		}
		
	} // End Orbit Azimuth Action
	
	private class OrbitElevationAction extends AbstractInputAction
	{
		@Override
		public void performAction(float time, Event event){
			//System.out.println("Elevation:  = "+event.getValue());
			float distance, verticalSpeed;
			verticalSpeed = 0.25f;
			
			//Trigger Deadzones are weird
			if(event.getValue() < -0.8f){
				distance = 0.0f;
			} else {
				distance = verticalSpeed;
			}
			
			float minElevation = -1f; //Change these
			float maxElevation = 1f;
			
		
			//.out.println("Cam + Ele = "+ (cameraElevation + distance));
			if(cameraElevation + distance < 90f){
				cameraElevation += distance;
			} else {
				//System.out.println("Max Elevation");
			}
			
			updateCameraPosition();
		}
	} // End Orbit Elevation Action
	
	private class OrbitElevationActionReversed extends AbstractInputAction
	{
		@Override
		public void performAction(float time, Event event){
			//System.out.println("Elevation:  = "+event.getValue());
			float distance, verticalSpeed;
			verticalSpeed = -0.25f;				//Note the change here
			
			//Trigger Deadzones are weird
			if(event.getValue() < -0.8f){
				distance = 0.0f;
			} else {
				distance = verticalSpeed;
			}
			
			float minElevation = -1f; //Change these
			float maxElevation = 1f;
			//System.out.println("R Cam + Ele = "+ (cameraElevation + distance));
	
			if(cameraElevation + distance > 0f){
				cameraElevation += distance;
			} else {
				//System.out.println("Min Elevation");
			}
			updateCameraPosition();
		}
	} // End Orbit Elevation Action
	
	private class OrbitRadiusAction extends AbstractInputAction
	{
		@Override
		public void performAction(float time, Event event){
			//System.out.println("Radial: = "+event.getValue());
			float radialSpeed = 0.025f;
			
			float distance;
			if(event.getValue() < -0.2){
				distance = -1 * radialSpeed;
			} else {
				if (event.getValue() > 0.2){
					distance = radialSpeed;
				} else {
					distance = 0.0f;
				}
			}
			
			float minDistance = -1f; //Change these
			float maxDistance = 100f;
			
			if(cameraRadius + distance > 1.5f && cameraRadius + distance < 3000f){
				cameraRadius += distance;
			}
			
			//System.out.println("Camera Radius: "+cameraRadius);
			updateCameraPosition();
		}
	} //End Orbit Radius Action
	
	private class OrbitAzimuthActionButton extends AbstractInputAction
	{
		@Override
		public void performAction(float time, Event event){
			//System.out.println("Azi = "+event.getValue());
			//float timeDiff = time - lastTime;
			
			float rotAmount;
			
			rotAmount = -0.4f;
			
			
			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
			updateCameraPosition();
		}
		
	} // End Orbit Azimuth Action
	
	private class OrbitAzimuthActionButtonReversed extends AbstractInputAction
	{
		@Override
		public void performAction(float time, Event event){
			//System.out.println("Azi = "+event.getValue());
			//float timeDiff = time - lastTime;
			
			float rotAmount;
			
			rotAmount = 0.4f;
			
			
			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
			updateCameraPosition();
		}
		
	} // End Orbit Azimuth Action
	
	private class OrbitRadiusActionButton extends AbstractInputAction
	{
		@Override
		public void performAction(float time, Event event){
			//System.out.println("Radial: = "+event.getValue());
			float radialSpeed = 0.025f;
			
			float distance;

			distance = -1 * radialSpeed;

			
			if(cameraRadius + distance > 1.5f ){
				cameraRadius += distance;
			}
			
			//System.out.println("Camera Radius: "+cameraRadius);
			updateCameraPosition();
		}
	} //End Orbit Radius Action
	
	private class OrbitRadiusActionButtonReversed extends AbstractInputAction
	{
		@Override
		public void performAction(float time, Event event){
			//System.out.println("Radial: = "+event.getValue());
			float radialSpeed = 0.025f;
			
			float distance;

			distance = 1 * radialSpeed;

			
			if(cameraRadius + distance < 3000f){
				cameraRadius += distance;
			}
			
			//System.out.println("Camera Radius: "+cameraRadius);
			updateCameraPosition();
		}
	} //End Orbit Radius Action
	
}