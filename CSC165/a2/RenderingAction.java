package a2;

import tage.*;
import net.java.games.input.Event;
import org.joml.*;
import tage.input.action.AbstractInputAction;

public class RenderingAction extends AbstractInputAction
{
	
	private GameObject[] objects;
	private int numberOfObjects;
	private float lastTime = 0;
	
	public RenderingAction(GameObject game1){
		objects = new GameObject[1];
		objects[0] = game1;
		numberOfObjects = 1;
	}
	
	public RenderingAction(GameObject game1, GameObject game2){
		objects = new GameObject[2];
		objects[0] = game1;
		objects[1] = game2;
		numberOfObjects = 2;
	}
	
	public RenderingAction(GameObject game1, GameObject game2, GameObject game3){
		objects = new GameObject[3];
		objects[0] = game1;
		objects[1] = game2;
		objects[2] = game3;
		numberOfObjects = 3;
	}
	
	@Override 
	public void performAction(float time, Event e){
		if(time - lastTime <= 0.1f){	//Cooldown Timer
			return;
		}
		
		for( int i = 0; i < numberOfObjects; i++){
			if( (objects[i].getRenderStates()).renderingEnabled() ){
				//System.out.println("Disabling");
				(objects[i].getRenderStates()).disableRendering();
			} else {
				//System.out.println("Enabling");
				(objects[i].getRenderStates()).enableRendering();
			}
		}
		lastTime = time;
	}
}