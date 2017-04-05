package myGameEngine;

import net.java.games.input.Component.POV;
import net.java.games.input.Event;
import net.java.games.input.Component.Identifier.*;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.camera.ICamera;
import sage.input.IInputManager;
import sage.input.action.AbstractInputAction;
import sage.input.action.IAction;
import sage.scene.SceneNode;
import sage.util.MathUtils;


public class TPCamCtrl
{
	private ICamera cam;
	private SceneNode target;
	private float cameraAzimuth;
	private float cameraElevation;
	private float cameraDistanceFromTarget;
	private Point3D targetPos;
	private Vector3D worldUpVec;
	private IInputManager.INPUT_ACTION_TYPE repeat = IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN;
	
	public TPCamCtrl(ICamera cam, SceneNode target, IInputManager im, String name, String gpname)
	{
		this.cam = cam;
		this.target = target;
		worldUpVec = new Vector3D(0,1,0);
		cameraDistanceFromTarget = 5.0f;
		cameraAzimuth = 180;
		cameraElevation = 20.0f;
		update(0.0f);
		setupInput(im, name, gpname);
	}
	
	public void update(float time)
	{
		updateTarget();
		updateCameraPosition();
		cam.lookAt(targetPos, worldUpVec);
	}
	
	private void updateTarget()
	{
		targetPos = new Point3D(target.getWorldTranslation().getCol(3));
	}
	
	private void updateCameraPosition()
	{
		double theta = cameraAzimuth;
		double phi = cameraElevation;
		double r = cameraDistanceFromTarget;
		
		Point3D relativePosition = MathUtils.sphericalToCartesian(theta, phi, r);
		Point3D desiredCameraLoc = relativePosition.add(targetPos);
		cam.setLocation(desiredCameraLoc);
	}
	
	private void setupInput(IInputManager im, String cn, String gp)
	{
		IAction orbitAction = new OrbitAroundAction(0);
		im.associateAction(gp, Axis.RX, orbitAction, repeat);
		IAction orbitLeftAction = new OrbitAroundAction(-1);
		im.associateAction(cn, Key.LEFT, orbitLeftAction, repeat);
		IAction orbitRightAction = new OrbitAroundAction(1);
		im.associateAction(cn, Key.RIGHT, orbitRightAction, repeat);
		
		IAction orbitWithDirection = new OrbitDirectionAction(0);
		im.associateAction(gp, Axis.Z, orbitWithDirection, repeat);
		IAction orbitLeftWithDirection = new OrbitDirectionAction(-1);
		im.associateAction(cn, Key.NUMPAD4, orbitLeftWithDirection, repeat);
		IAction orbitRightWithDirection = new OrbitDirectionAction(1);
		im.associateAction(cn, Key.NUMPAD6, orbitRightWithDirection, repeat);
		
		IAction zoom = new ZoomAction(0);
		im.associateAction(gp, Axis.RY, zoom, repeat);
		IAction zoomOut = new ZoomAction(-1);
		im.associateAction(cn, Key.DOWN, zoomOut, repeat);
		IAction zoomIn = new ZoomAction(1);
		im.associateAction(cn, Key.UP, zoomIn, repeat);
	}
	
	private class OrbitAroundAction extends AbstractInputAction
	{
		private int direction;
		public OrbitAroundAction(int dir)
		{
			direction = dir;
		}

		public void performAction(float time, Event evt)
		{
			float rotAmount;
			if (evt.getValue() < -0.2 || direction > 0) rotAmount = -0.5f;
			else { if (evt.getValue() > 0.2 || direction < 0) rotAmount = 0.5f;
			else rotAmount = 0.0f;
			}
			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
		}
	}
	
	private class ZoomAction extends AbstractInputAction
	{
		private int direction;
		public ZoomAction(int dir)
		{
			direction = dir;
		}

		public void performAction(float time, Event evt)
		{
			float zoomAmount;
			
				if (evt.getValue() < -0.2 || direction > 0) zoomAmount = -0.1f;
				else
				{
					if (evt.getValue() > 0.2 || direction < 0) zoomAmount = 0.1f;
					else zoomAmount = 0.0f;
				}
			if (cameraDistanceFromTarget <= 0.5 && zoomAmount < 0) zoomAmount = 0.0f;
				
			cameraDistanceFromTarget += zoomAmount;
			//cameraAzimuth = cameraAzimuth % 360;
		}
	}
	
	private class OrbitDirectionAction extends AbstractInputAction
	{
		private int direction;
		public OrbitDirectionAction(int dir)
		{
			direction = dir;
		}

		public void performAction(float time, Event evt)
		{
			float rotAmount;
			if (evt.getValue() < -0.2 || direction > 0) rotAmount = -0.5f;
			else { if (evt.getValue() > 0.2 || direction < 0) rotAmount = 0.5f;
			else rotAmount = 0.0f;
			}
			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
			target.rotate(rotAmount, new Vector3D(0, 1, 0));
		}
	}
	/*
	public Point3D getTargetLocation()
	{
		return targetPos;
	}
	*/

	public float getAzimuth() {
		
		return cameraAzimuth;
	}
}
