package org.dyn4j.samples.framework;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;

/**
 * Custom Body class to add drawing functionality.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.0.0
 */
public class SimulationBody extends Body {
	/** The color of the object */
	protected Color color;
	
	/**
	 * Default constructor.
	 */
	public SimulationBody() {
		this.color = Graphics2DRenderer.getRandomColor();
	}
	
	/**
	 * Constructor.
	 * @param color a set color
	 */
	public SimulationBody(Color color) {
		this.color = color;
	}

	/**
	 * Returns this body's color.
	 * @return Color
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Sets the body's color
	 * @param color the color
	 */
	public void setColor(Color color) {
		this.color = color;
	}
}