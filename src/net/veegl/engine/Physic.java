package net.veegl.engine;


import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;


public class Physic 
{

	
	DynamicsWorld world = null;
	
	ObjectArrayList<CollisionShape> shapes = new ObjectArrayList<CollisionShape>();
	ObjectArrayList<RigidBody> 	 	bodies = new ObjectArrayList<RigidBody>();
	
	
	public DynamicsWorld getWorld() {
		return world;
	}

	
	public ObjectArrayList<CollisionShape> getShapes() {
		return shapes;
	}

	
	public ObjectArrayList<RigidBody> getBodies() {
		return bodies;
	}


	public Physic(Vector3f gravity)
	{
		
		BroadphaseInterface broadphase = new DbvtBroadphase();
		
		DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		world.setGravity(gravity);
	}
	
	
	
	
	public RigidBody createRigidBody(CollisionShape shape, Vector3f position, float mass, Vector3f inertia)
	{
		if(mass != 0)
			shape.calculateLocalInertia(mass, inertia);
		
		RigidBodyConstructionInfo bodyInfo = new RigidBodyConstructionInfo(mass, createMotionState(position), shape, inertia);
		
//		bodyInfo.angularDamping = 0.5f;
//		bodyInfo.friction 		= 0.3f;
//		bodyInfo.restitution 	= 0f;

		
		RigidBody rigidBody = new RigidBody(bodyInfo);
		
		world.addRigidBody(rigidBody);
		bodies.add(rigidBody);
		
		return rigidBody;
	}
	
	
	private MotionState createMotionState(Vector3f position)
	{
		Transform positionTransform = new Transform();
		positionTransform.setIdentity();
		positionTransform.origin.set(position);
		
		return new DefaultMotionState(positionTransform);
	}
	
	
	public void stepPhysicEngine(int fps)
	{
		if(fps < 25)
			fps = 25;
		world.stepSimulation((float)(1/(float)fps), 7);
	}
	
	

	
}
