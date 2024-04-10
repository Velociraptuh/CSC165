package a2;

import tage.*;
import net.java.games.input.Event;
import org.joml.*;

import a2.ProtocolClient;
import tage.input.action.AbstractInputAction;

public class PitchAction extends AbstractInputAction
{
	private MyGame game;
	private GameObject obj;
	private Matrix4f oldRotation, newRotation;
	private Vector4f yawDirection, oldUp;
	private Matrix4f rotAroundAvatarUp;
	private float lastTime;
	private ProtocolClient protClient;
	
	public PitchAction(MyGame g, ProtocolClient p){
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
		
		float diff = time - lastTime;
		turnAmount = turnAmount * diff;
		
		obj = game.getAvatar();
		Matrix4f newPitch = (new Matrix4f()).rotation((float)turnAmount, obj.getLocalRightVector());
		newPitch.mul(obj.getLocalRotation());
		obj.setLocalRotation(newPitch);
		
		lastTime = time;
		protClient.sendMoveMessage(obj.getWorldLocation());
	}
}