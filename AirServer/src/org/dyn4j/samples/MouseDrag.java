/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.samples;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.geometry.*;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * A simple scene showing how to drag an object around the scene
 * with the mouse using a MotorJoint.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.2.0
 *
 */
public class MouseDrag extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -4132057742762298086L;

	/** The controller body */
	private Body controller;
	
	/** The current mouse drag point */
	private Point point;
	
	/**
	 * A custom mouse adapter to track mouse drag events.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	public static class InputAdapter  {

		public void mouseDragged(double x, double y) {
			System.out.println("Mouse dragged (" + x + "," + y+ ")" );
		}
	}

	/**
	 * Default constructor for the window
	 */
	public MouseDrag(World world) {
		super("Mouse Drag", 32.0, world);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected static World initializeWorld() {
		// no gravity please
		World world = new World();
		world.setGravity(World.ZERO_GRAVITY);
		
		// player control setup
		
		Body controller = new Body();
		Circle fixture = Geometry.createCircle(0.1);
	    controller.addFixture(fixture);
	    controller.setAngularDamping(1);
	    controller.setMass(MassType.INFINITE);
	    controller.setAutoSleepingEnabled(false);
	    world.addBody(controller);

		Body sb = new Body();
		sb.addFixture(Geometry.createCircle(0.5));
		sb.setMass(MassType.NORMAL);
		sb.setAngularDamping(1);
		sb.setAutoSleepingEnabled(false);
		world.addBody(sb);

		Body player = new Body();
	    player.addFixture(Geometry.createCircle(0.5));
	    player.setMass(MassType.NORMAL);
		player.setAngularDamping(1);
		player.setAutoSleepingEnabled(false);
	    world.addBody(player);
	    
	    MotorJoint control = new MotorJoint(player, controller);
	    control.setCollisionAllowed(false);
	    control.setMaximumForce(1000.0);
	    control.setMaximumTorque(0.0);
	    world.addJoint(control);
	    
	    // obstacles
	    
	    Body wall1 = new Body();
	    wall1.addFixture(Geometry.createRectangle(1, 10), 1, 0.2, 0.9);
	    wall1.setAngularDamping(1);
	    wall1.setMass(MassType.INFINITE);
	    wall1.translate(4, 0);
	    world.addBody(wall1);

		Body wall2 = new Body();
		wall2.addFixture(Geometry.createRectangle(1, 10), 1, 0.2, 0.9);
		wall2.setAngularDamping(1);
		wall2.setMass(MassType.INFINITE);
		wall2.translate(-4, 0);
		world.addBody(wall2);

		Body wall3 = new Body();
		wall3.addFixture(Geometry.createRectangle(10, 1), 1, 0.2, 0.9);
		wall3.setMass(MassType.INFINITE);
		wall3.setAngularDamping(1);
		wall3.translate(0, 4);
		world.addBody(wall3);

		Body wall4 = new Body();
		wall4.addFixture(Geometry.createRectangle(10, 1), 1, 0.2, 0.9);
		wall4.setMass(MassType.INFINITE);
		wall4.setAngularDamping(1);
		wall4.translate(0, -4);
		world.addBody(wall4);
		return world;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#update(java.awt.Graphics2D, double)
	 */
	protected void update(Graphics2D g, double elapsedTime) {

		// check if the mouse has moved/dragged
		if (this.point != null) {
			// convert from screen space to world space
			double x =  (this.point.getX() - this.canvas.getWidth() / 2.0) / this.scale;
			double y = -(this.point.getY() - this.canvas.getHeight() / 2.0) / this.scale;

			// reset the transform of the controller body
			Transform tx = new Transform();
			tx.translate(x, y);

			this.controller.setTransform(tx);

			// clear the point
			this.point = null;
		}
	}

	public void setMouseListener(InputAdapter ml) {

		MouseAdapter listener = new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent point) {
				double x =  (point.getX() - canvas.getWidth() / 2.0) / scale;
				double y = -(point.getY() - canvas.getHeight() / 2.0) / scale;
				ml.mouseDragged(x,y);
			}
		};

		this.canvas.addMouseMotionListener(listener);
		this.canvas.addMouseWheelListener(listener);
		this.canvas.addMouseListener(listener);
	}

	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		World world = initializeWorld();

		MouseDrag javaFrame = new MouseDrag(world);
		// setup the mouse listening
		InputAdapter ml = new InputAdapter();
		javaFrame.setMouseListener(ml);
		javaFrame.setupRender();
		Thread thread = new Thread() {
			public void run() {
				// perform an infinite loop stopped
				// render as fast as possible
				while (!javaFrame.isStopped()) {

					world.step(1);
					javaFrame.renderLoop();
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
}
