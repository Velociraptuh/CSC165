package tage.nodeControllers;

import tage.*;
import org.joml.*;

/** 
	HoverController allows an object to hover or "bob" above the ground if already positioned off the ground. 
	Can be combined with the RotationController nodeController for a higher quality special-effect. 
 */
public class HoverController extends NodeController
{
	private float hoverRate = 0.006f;
	private float cycleTime = 1000.0f;
	private float totalTime = 0.0f;
	private float direction = 1.0f;
	private Matrix4f curHeight, newHeight;
	private Engine engine;
	
	/** Creates a HoverController Object with specified Speed */
	public HoverController(Engine e, float ctime, float speed){
		super();
		cycleTime = ctime;
		engine = e;
		newHeight = new Matrix4f();
		hoverRate = speed;
	}
	
	/** Specifies which game object is to be affected by this nodeController */
	public void apply(GameObject go){
		float elapsedTime = super.getElapsedTime();
		totalTime += elapsedTime / 1000.0f;
		if(totalTime > cycleTime){
			direction = -direction;
			totalTime = 0.0f;
		}
		
		curHeight = go.getLocalTranslation();
		float changeAmount = 1.0f * direction * hoverRate * elapsedTime;
		curHeight.translate(0.0f, changeAmount, 0.0f, newHeight);
		go.setLocalTranslation(newHeight);
	}
	
}