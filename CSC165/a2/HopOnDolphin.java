/*package a2;

import tage.*;
import net.java.games.input.Event;
import org.joml.*;
import tage.input.action.AbstractInputAction;

public class HopOnDolphin extends AbstractInputAction
{
	private MyGame game;
	private GameObject obj;
	private Vector3f oldPosition, newPosition;
	private Vector4f fwdDirection;
	private boolean ridingDolphin;
	private long lastTime = 0;
	
	public HopOnDolphin(MyGame g, boolean riding){
		game = g;
		ridingDolphin = riding;
	}
	
	@Override
	public void performAction(float time, Event E){	
		
		if(E.getNanos() - lastTime > 1000){				//Add a cool down so button press is responsive
			game.setRidingDolphin(!ridingDolphin);
			ridingDolphin = !ridingDolphin;
		}
		lastTime = E.getNanos();
	}
	
}*/