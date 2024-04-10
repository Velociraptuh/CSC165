package tage.shapes;
import tage.*;

//Need to rename to "slope" or "Right Triangular Prism" or something
/**
	Object is called "Grass" but should really be called "Slope" or "Ramp or something because it is more accurate. 
	This object has 24 verticies. The full faces uses the full texture coordinates. The sides uf (0,0), (1,0), and (0,1)
*/
public class Grass extends ObjShape{
	
	float[] vertices = {
		-1.0f, -0.5f, 1.0f, -1.0f, 0.5f, 1.0f, 1.0f, -0.5f, 1.0f,		//left side
		-1.0f, -0.5f, -1.0f, -1.0f, -0.5f, 1.0f, -1.0f, 0.5f, 1.0f,		//Lower back
		-1.0f, -0.5f, -1.0f, -1.0f, 0.5f, -1.0f, -1.0f, 0.5f, 1.0f,		//Upper back
		1.0f, -0.5f, -1.0f, -1.0f, -0.5f, 1.0f, 1.0f, -0.5f, 1.0f,		//Bottom front right
		-1.0f, -0.5f, -1.0f, 1.0f, -0.5f, -1.0f, -1.0f, -0.5f, 1.0f,	//Bottom back left
		1.0f, -0.5f, 1.0f, -1.0f, 0.5f, 1.0f, 1.0f, -0.5f, -1.0f,		//Lower slope
		-1.0f, 0.5f, 1.0f, -1.0f, 0.5f, -1.0f, 1.0f, -0.5f, -1.0f,		//Upper slope
		-1.0f, -0.5f, -1.0f, -1.0f, 0.5f, -1.0f, 1.0f, -0.5f, -1.0f		//Right side
	};
	
	float[] textureCoords = {
		0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,		//Left
		0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,		//Back side
		0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,		//Bottom Side
		0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
		0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,		//Front / Slope Side
		0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
		0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f		//Right Side
	};
	
	float[] normals = new float[]
	{
		0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 			//Left side
		-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,		//Back Side
		-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
		0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 		//Bottom Side
		0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 
		1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 			//Front / Slope Side
		1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 
		0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f 		//Right Side
	};
	
	/** Creates a ramp-like object with 24 verticies.  */
	public Grass(){
		setNumVertices(24);
		setVertices(vertices);
		setTexCoords(textureCoords);
		setNormals(normals);
		setWindingOrderCCW(false);
	}
	
}