package games.treasureHunt2014;

import graphicslib3D.Vector3D;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import sage.scene.TriMesh;



public class MyShape extends TriMesh {
	private static float[] vrts = new float[] {0,2,0,2,2,0,2,2,2,0,2,2,1,0,1};
	private static float[] cls = new float [] {1,0,0,1,1,0,1,0,1,0,1,1,0,1,0,1,0,1,1,1};
	private static int[] triangles = new int[] {0,2,3,0,1,2,1,2,4,0,1,4,0,3,4,2,3,4};
	static public Vector3D rotAxis = new Vector3D(0,1,0);
	public MyShape(){
		FloatBuffer vertBuf =  com.jogamp.common.nio.Buffers.newDirectFloatBuffer(vrts); 
		FloatBuffer colorBuf =  com.jogamp.common.nio.Buffers.newDirectFloatBuffer(cls);
		IntBuffer triangleBuf =  com.jogamp.common.nio.Buffers.newDirectIntBuffer(triangles); 
		this.setVertexBuffer(vertBuf); 
		this.setColorBuffer(colorBuf); 
		this.setIndexBuffer(triangleBuf); 
	}
	public Vector3D getRotAxis(){
		return rotAxis;
	}
}
