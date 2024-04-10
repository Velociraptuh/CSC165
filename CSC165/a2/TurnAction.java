package a2;

import tage.*;
import net.java.games.input.Event;
import org.joml.*;

import a2.ProtocolClient;
import tage.input.action.AbstractInputAction;

public class TurnAction extends AbstractInputAction
{
	private MyGame game;
	private GameObject obj;
	private Matrix4f oldRotation, newRotation;
	private Vector4f yawDirection, oldUp;
	private Matrix4f rotAroundAvatarUp;
	private float lastTime;

	
	public TurnAction(MyGame g){
		game = g;
		lastTime = 0.0f;
	}
	
	@Override
	public void performAction(float time, Event E){
		float turnAmount = E.getValue();
		if(turnAmount > -0.2 && turnAmount < 0.2){ //Deadzone
			lastTime = time;
			return;
		}
		
		float diff = time - lastTime;
		if(diff >= 0.1f){				//This is here to get buttons to work, otherwise they will gather too much difference and teleport you (not seemless)
			diff = 0.006f;
		}
		turnAmount = turnAmount * -1.3f * diff;	//Flip left and right and make it a reasonable speed
		
		obj = game.getAvatar();
		Matrix4f newYaw = (new Matrix4f()).rotation((float)turnAmount, 0, 1, 0);
		newYaw.mul(obj.getWorldRotation());
		obj.setLocalRotation(newYaw);
		
		lastTime = time;
	}
	
}