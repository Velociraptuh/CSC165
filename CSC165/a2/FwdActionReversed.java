package a2;

import tage.*;
import net.java.games.input.Event;
import org.joml.*;

import a2.ProtocolClient;
import tage.input.action.AbstractInputAction;

public class FwdActionReversed extends AbstractInputAction
{
	private MyGame game;
	private GameObject obj;
	private Vector3f oldPosition, newPosition;
	private Vector4f fwdDirection;
	private float lastTime;
	private ProtocolClient protClient;
	
	public FwdActionReversed(MyGame g, ProtocolClient p){
		game = g;
		lastTime = 0.0f;
		protClient = p;
	}
	
	@Override
	public void performAction(float time, Event E){
		float turnAmount = E.getValue();
		if(turnAmount > -0.2 && turnAmount < 0.2){ //Deadzone
			lastTime = time;
			return;
		}
		obj = game.getAvatar();
		
		float diff = time - lastTime;
		if(diff >= 0.1f){					//This is here to get face buttons to work, otherwise they will gather too much difference and teleport you (not seemless)
			diff = 0.003f;
		}
		turnAmount = turnAmount * diff * 3.0f;
		
		//Camera cam = game.getEngine().getRenderSystem().getViewport("MAIN").getCamera();		  Assignment 1
		Camera cam = game.getEngine().getRenderSystem().getViewport("LEFT").getCamera();		//Assignment 2
		Vector3f difference = new Vector3f((cam.getLocation()).sub(obj.getWorldLocation()));
		if(difference.length() > 10.0f){
			return;
		}
		
		oldPosition = obj.getWorldLocation();
		fwdDirection = new Vector4f(0f,0f,1f,1f);
		fwdDirection.mul(obj.getWorldRotation());
		if( turnAmount < -0.2){
			fwdDirection.mul(turnAmount);
		} else {
			fwdDirection.mul(turnAmount*-1f);
		}
		newPosition = oldPosition.add(fwdDirection.x(), fwdDirection.y(), fwdDirection.z());
		obj.setLocalLocation(newPosition);
		
		lastTime = time;
		protClient.sendMoveMessage(obj.getWorldLocation());
	}
	
}