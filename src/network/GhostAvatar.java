package network;

import myGameEngine.WaveAction;
import sage.input.action.IAction;
import sage.model.loader.ogreXML.OgreXMLParser;
import sage.scene.Group;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.scene.shape.Cube;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import graphicslib3D.Vector3D;
import graphicslib3D.Matrix3D;
import sage.model.loader.ogreXML.*; 
import sage.scene.Model3DTriMesh;
import sage.texture.Texture;
import sage.texture.TextureManager;
import sage.model.loader.ogreXML.*; 
import sage.scene.Model3DTriMesh;

public class GhostAvatar
{
	String id;
//	private Cube avatar;
	TextureState hobbitTextureState; // for texturing the model 
	Group model, ghostModel; // the Ogre model 
	Model3DTriMesh myObject, ghostObject; // the mesh in the Ogre model 
	Model3DTriMesh[] models;
	Model3DTriMesh avatar;
	private IAction wave;
	 
	public GhostAvatar(String ghostID, Vector3D p, int sel)
	{
		super();
		id = new String(ghostID);

		OgreXMLParser loader = new OgreXMLParser(); 
		
		createModels();
		
		//avatar = new Cube("player");
		
		if ( sel == 0 ) avatar = myObject;
		else avatar = ghostObject;
		avatar.translate((float)p.getX(), (float)p.getY(), (float)p.getZ());
		//System.out.println(" select is"+ sel);
}

	
		private void createModels()
		{
			OgreXMLParser loader = new OgreXMLParser(); 
			try 
			{
				model = loader.loadModel("./models/new_ava/Cylinder.001.mesh.xml", 
						"./models/new_ava/Material1.material", 
						"./models/new_ava/Cylinder.001.skeleton.xml", "./models/", sage.texture.Texture.ApplyMode.Replace); 
						model.updateGeometricState(0, true); 
						java.util.Iterator<SceneNode> modelIterator = model.iterator(); 
						myObject = (Model3DTriMesh) modelIterator.next(); 
			 } 
			 catch (Exception e) 
			 {
				 e.printStackTrace(); 
				 System.exit(1); 
			 } 
			
			 //addGameWorldObject(myObject); 
			 myObject.scale(.15f,1f,.15f);
			 
		
	
			 try 
				{
				 ghostModel = loader.loadModel("./models/Cube.mesh.xml", 
							"./models/Material.material", 
							"./models/Cube.skeleton.xml", "./models/", sage.texture.Texture.ApplyMode.Replace); 
							ghostModel.updateGeometricState(0, true); 
							java.util.Iterator<SceneNode> ghostModelIterator = ghostModel.iterator(); 
							ghostObject = (Model3DTriMesh) ghostModelIterator.next(); 
				 } 
				 catch (Exception e) 
				 {
					 e.printStackTrace(); 
					 System.exit(1); 
				 } 
				
				 ghostObject.scale(.15f,.15f,.15f);
				 
				 ghostObject.rotate(90, new Vector3D(0,1,0));
			 //myObject.rotate(90, new Vector3D(0,1,0));
			 
			 // texture the object using a texture image file 	
	}
   
	public void setPosition(Vector3D p)
	{
		avatar.setLocalTranslation(new Matrix3D());
		avatar.translate((float)p.getX(), (float)p.getY(), (float)p.getZ());
	}
   
	public String getID() { return id; }
	public Model3DTriMesh getAvatar() { return avatar; }




	public void performAnimation(int action) {
		action++;	
		if(action%2 == 0) avatar.startAnimation("walk");
		else avatar.stopAnimation();
	//		System.out.println("Inside Ghost avatar, perform animation " + action);
		
	}


	public void performAnimationDance(int action) {

		action++;	
		if(action%2 == 0) avatar.startAnimation("dance");
		else avatar.stopAnimation();
	}
	
   
}