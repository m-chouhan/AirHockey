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
import java.util.List;

import com.airhockey.ApplicationWrapper;
import com.airhockey.core.Builder;
import com.airhockey.core.Core;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
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
public class PhyTestApp extends SimulationFrame implements ApplicationWrapper {
	/** The serial version id */
	private static final long serialVersionUID = -4132057742762298086L;

	/** The controller body */
	private static Player player;
	
	/** The current mouse drag point */
	private Point point;

	@Override
	public void print(String s) { System.out.println(s); }

	@Override
	public void render(GameState state, List<Body> bodies) {
		renderLoop(bodies);
	}

	@Override
	public void endGame(Player winner) {}

	@Override
	public void scoreUpdated(Player striker) {
		print("Score updated for " + striker.id + " to " + striker.score);
	}

	@Override
	public void resetGame(GameState state) {

	}

	/**
	 * A custom mouse adapter to track mouse drag events.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	public static class InputAdapter  {

		public void mouseDragged(double x, double y) {
			System.out.println("Mouse dragged (" + x + "," + y+ ")" );
			// reset the transform of the controller body
			//Transform tx = new Transform();
			//tx.translate(x, y);
			player.setPosition((float) x, (float)y);
		}
	}

	/**
	 * Default constructor for the window
	 */
	public PhyTestApp() {
		super("Mouse Drag", 32.0);
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

			player.master.setTransform(tx);

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

		System.out.println("Hello, our enviornment is " + System.getenv("ENV"));
		if(System.getenv("ENV").equals("PROD")) return;
		Player player1 = new Player(10);
		Player player2 = new Player(20);
		PhyTestApp.player = player1;

		PhyTestApp javaFrame = new PhyTestApp();
		javaFrame.setupRender();

		Core core = new Core(javaFrame, player1, player2, 5);

		// setup the mouse listening
		InputAdapter ml = new InputAdapter();
		javaFrame.setMouseListener(ml);

		Thread thread = new Thread() {
			public void run() {
				// perform an infinite loop stopped
				// render as fast as possible
				while (!javaFrame.isStopped()) {
					core.run();
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
