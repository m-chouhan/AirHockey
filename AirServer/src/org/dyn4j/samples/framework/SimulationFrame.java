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
package org.dyn4j.samples.framework;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferStrategy;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;

/**
 * A very VERY simple framework for building samples.
 * @since 3.2.0
 * @version 3.2.0
 */
public abstract class SimulationFrame extends JFrame {
	/** The serial version id */
	private static final long serialVersionUID = 7659608187025022915L;

	/** The conversion factor from nano to base */
	public static final double NANO_TO_BASE = 1.0e9;

	/** The canvas to draw to */
	protected final Canvas canvas;

	/** The pixels per meter scale factor */
	protected final double scale;
	
	/** True if the simulation is exited */
	private boolean stopped;
	
	/** True if the simulation is paused */
	private boolean paused;
	
	/** The time stamp for the last iteration */
	private long last;
	
	/**
	 * Constructor.
	 * <p>
	 * By default creates a 800x600 canvas.
	 * @param name the frame name
	 * @param scale the pixels per meter scale factor
	 */
	public SimulationFrame(String name, double scale) {
		super(name);
		
		// set the scale
		this.scale = scale;

		// setup the JFrame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// add a window listener
		this.addWindowListener(new WindowAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				// before we stop the JVM stop the simulation
				stop();
				super.windowClosing(e);
			}
		});
		
		// create the size of the window
		Dimension size = new Dimension(800, 600);
		
		// create a canvas to paint to 
		this.canvas = new Canvas();
		this.canvas.setPreferredSize(size);
		this.canvas.setMinimumSize(size);
		this.canvas.setMaximumSize(size);
		
		// add the canvas to the JFrame
		this.add(this.canvas);
		
		// make the JFrame not resizable
		// (this way I dont have to worry about resize events)
		this.setResizable(false);
		
		// size everything
		this.pack();
		
	}

	/**
	 * The method calling the necessary methods to update
	 * the game, graphics, and poll for input.
	 */
	public void renderLoop(List<Body> bodies) {
		// get the graphics object to render to
		Graphics2D g = (Graphics2D)this.canvas.getBufferStrategy().getDrawGraphics();
		
		// by default, set (0, 0) to be the center of the screen with the positive x axis
		// pointing right and the positive y axis pointing up
		this.transform(g);
		
		// reset the view
		this.clear(g);
		
		// get the current time
        long time = System.nanoTime();
        // get the elapsed time from the last iteration
        long diff = time - this.last;
        // set the last time
        this.last = time;
    	// convert from nanoseconds to seconds
    	double elapsedTime = (double)diff / NANO_TO_BASE;

		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// draw all the objects in the world
		for(Body body : bodies)
			render(g, elapsedTime, body);
		// dispose of the graphics object
		g.dispose();
		
		// blit/flip the buffer
		BufferStrategy strategy = this.canvas.getBufferStrategy();
		if (!strategy.contentsLost()) {
			strategy.show();
		}
		
		// Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * Performs any transformations to the graphics.
	 * <p>
	 * By default, this method puts the origin (0,0) in the center of the window
	 * and points the positive y-axis pointing up.
	 * @param g the graphics object to render to
	 */
	protected void transform(Graphics2D g) {
		final int w = this.canvas.getWidth();
		final int h = this.canvas.getHeight();
		
		// before we render everything im going to flip the y axis and move the
		// origin to the center (instead of it being in the top left corner)
		AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
		AffineTransform move = AffineTransform.getTranslateInstance(w / 2, -h / 2);
		g.transform(yFlip);
		g.transform(move);
	}
	
	/**
	 * Clears the previous frame.
	 * @param g the graphics object to render to
	 */
	protected void clear(Graphics2D g) {
		final int w = this.canvas.getWidth();
		final int h = this.canvas.getHeight();
		
		// lets draw over everything with a white background
		g.setColor(Color.WHITE);
		g.fillRect(-w / 2, -h / 2, w, h);
	}

	/**
	 * Renders the body.
	 * @param g the graphics object to render to
	 * @param elapsedTime the elapsed time from the last update
	 * @param body the body to render
	 */
	protected void render(Graphics2D g, double elapsedTime, Body body) {
		// draw the object
		render(body, g, this.scale, Color.CYAN);
	}

	/**
	 * Draws the body.
	 * <p>
	 * Only coded for polygons and circles.
	 * @param g the graphics object to render to
	 * @param scale the scaling factor
	 * @param color the color to render the body
	 */
	public void render(Body body, Graphics2D g, double scale, Color color) {
		// point radius
		final int pr = 4;

		// save the original transform
		AffineTransform ot = g.getTransform();

		// transform the coordinate system from world coordinates to local coordinates
		AffineTransform lt = new AffineTransform();
		lt.translate(body.getTransform().getTranslationX() * scale, body.getTransform().getTranslationY() * scale);
		lt.rotate(body.getTransform().getRotationAngle());

		// apply the transform
		g.transform(lt);

		// loop over all the body fixtures for this body
		for (BodyFixture fixture : body.getFixtures()) {
			renderFixture(body, g, scale, fixture, color);
		}

		// draw a center point
		Ellipse2D.Double ce = new Ellipse2D.Double(
				body.getLocalCenter().x * scale - pr * 0.5,
				body.getLocalCenter().y * scale - pr * 0.5,
				pr,
				pr);
		g.setColor(Color.WHITE);
		g.fill(ce);
		g.setColor(Color.DARK_GRAY);
		g.draw(ce);

		// set the original transform
		g.setTransform(ot);
	}

	/**
	 * Renders the given fixture.
	 * @param g the graphics object to render to
	 * @param scale the scaling factor
	 * @param fixture the fixture to render
	 * @param color the color to render the fixture
	 */
	protected void renderFixture(Body body, Graphics2D g, double scale, BodyFixture fixture, Color color) {
		// get the shape on the fixture
		Convex convex = fixture.getShape();

		// brighten the color if asleep
		if (body.isAsleep()) {
			color = color.brighter();
		}
		// render the fixture
		Graphics2DRenderer.render(g, convex, scale, color);
	}

	/**
	 * Stops the simulation.
	 */
	public synchronized void stop() {
		this.stopped = true;
	}
	
	/**
	 * Returns true if the simulation is stopped.
	 * @return boolean true if stopped
	 */
	public boolean isStopped() {
		return this.stopped;
	}
	
	/**
	 * Pauses the simulation.
	 */
	public synchronized void pause() {
		this.paused = true;
	}
	
	/**
	 * Pauses the simulation.
	 */
	public synchronized void resume() {
		this.paused = false;
	}
	
	/**
	 * Returns true if the simulation is paused.
	 * @return boolean true if paused
	 */
	public boolean isPaused() {
		return this.paused;
	}
	
	/**
	 * setup the simulation.
	 */
	public void setupRender() {
		// set the look and feel to the system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		// show it
		this.setVisible(true);

		// initialize the last update time
		this.last = System.nanoTime();
		// don't allow AWT to paint the canvas since we are
		this.canvas.setIgnoreRepaint(true);
		// enable double buffering (the JFrame has to be
		// visible before this can be done)
		this.canvas.createBufferStrategy(2);
	}
}
