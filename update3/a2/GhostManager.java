package a2;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;
import org.joml.*;
import tage.physics.*;
import tage.physics.JBullet.*;

import tage.*;

public class GhostManager
{
	private MyGame game;
	private Vector<GhostAvatar> ghostAvatars = new Vector<GhostAvatar>();

	public GhostManager(VariableFrameRateGame vfrg)
	{	game = (MyGame)vfrg;
	}
	
	public void createGhostAvatar(UUID id, Vector3f position) throws IOException
	{	System.out.println("adding ghost with ID --> " + id);
		ObjShape s = game.getGhostShape();
		TextureImage t = game.getGhostTexture();
		GhostAvatar newAvatar = new GhostAvatar(id, s, t, position);
		Matrix4f initialScale = (new Matrix4f()).scaling(1.0f);
		newAvatar.setLocalScale(initialScale);
		ghostAvatars.add(newAvatar);
		
		//Physics
		float[] vals = game.getVals();
		Engine engine = game.getEngine();
		float mass = 5.0f;
		float up[ ] = {0,1,0};
		float radius = 0.75f;
		float height = 2.75f;
		double[ ] tempTransform;
		
		Matrix4f translation = new Matrix4f(newAvatar.getLocalTranslation());
		tempTransform = game.toDoubleArray(translation.get(vals));

		PhysicsObject ghostCapsulePhys = (engine.getSceneGraph()).addPhysicsCylinder(mass, tempTransform, radius, height);
		ghostCapsulePhys.setBounciness(0.45f);
		newAvatar.setPhysicsObject(ghostCapsulePhys);
	}
	
	public void removeGhostAvatar(UUID id)
	{	GhostAvatar ghostAvatar = findAvatar(id);
		if(ghostAvatar != null)
		{	game.getEngine().getSceneGraph().removeGameObject(ghostAvatar);
			ghostAvatars.remove(ghostAvatar);
		}
		else
		{	System.out.println("tried to remove, but unable to find ghost in list");
		}
	}

	private GhostAvatar findAvatar(UUID id)
	{	GhostAvatar ghostAvatar;
		Iterator<GhostAvatar> it = ghostAvatars.iterator();
		while(it.hasNext())
		{	ghostAvatar = it.next();
			if(ghostAvatar.getID().compareTo(id) == 0)
			{	return ghostAvatar;
			}
		}		
		return null;
	}

	public Vector<GhostAvatar> getGhosts(){
		return ghostAvatars;
	}
	
	public void updateGhostAvatar(UUID id, Vector3f position)
	{	GhostAvatar ghostAvatar = findAvatar(id);
		if (ghostAvatar != null)
		{	
			//if(ghostAvatar.getPosition().x() == position.x() && ghostAvatar.getPosition().y() == position.y() && ghostAvatar.getPosition().z() == position.z()){
				//(ghostAvatar.getShape()).playAnimation();
			//}
			ghostAvatar.setPosition(position);
			//Physics
			float[] vals = game.getVals();
			double[] transform = game.toDoubleArray(ghostAvatar.getWorldTranslation().mul(ghostAvatar.getWorldRotation()).get(vals));
			ghostAvatar.getPhysicsObject().setTransform(transform);
		}
		else
		{	System.out.println("tried to update ghost avatar position, but unable to find ghost in list");
		}
	}

	public void yawGhostAvatar(UUID id, float amount){
		GhostAvatar ghostAvatar = findAvatar(id);
		if(ghostAvatar != null){
			Matrix4f yRot = (new Matrix4f()).rotation((float)(org.joml.Math.toRadians(amount)), 0, 1, 0);
			Matrix4f currRoto = ghostAvatar.getLocalRotation();
			Matrix4f newRoto = yRot.mul(currRoto);
			ghostAvatar.setLocalRotation(newRoto);
			float[] vals = game.getVals();
			double[] transform = game.toDoubleArray(ghostAvatar.getWorldTranslation().mul(ghostAvatar.getWorldRotation()).get(vals));
			ghostAvatar.getPhysicsObject().setTransform(transform);
		}else{
			System.out.println("Could not update rotation");
		}
	}
	
	public void resetFromPhysics(){
		GhostAvatar ghostAvatar;
		Iterator<GhostAvatar> it = ghostAvatars.iterator();
		while(it.hasNext()){	
			ghostAvatar = it.next();
			
			Matrix4f identityRotation = new Matrix4f().identity();
			float[] vals = game.getVals();
					
			//Keep ghost rotation but make it upright
			Vector3f eulerAngles = new Vector3f();
			(ghostAvatar.getWorldRotation()).getEulerAnglesXYZ(eulerAngles);
			float y = eulerAngles.y;
			
			//Reset Ghost Rotation
			identityRotation.rotation(y, 0.0f, 1.0f, 0.0f);
			ghostAvatar.setLocalRotation(identityRotation);
			
			//Reset Physics Object
			double[] transform = game.toDoubleArray(ghostAvatar.getWorldTranslation().mul(ghostAvatar.getWorldRotation()).get(vals));
			ghostAvatar.getPhysicsObject().setTransform(transform);
		}		
	}
}
