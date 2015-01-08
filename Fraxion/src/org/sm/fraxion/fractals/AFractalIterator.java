// -------------------------------------
// Filename      : AFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 01/01/2015
// Target        : Java VM (1.8)
// -------------------------------------

/**
 * Copyright 2003-2015 Sven Maerivoet
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sm.fraxion.fractals;

import java.awt.*;
import org.sm.fraxion.fractals.util.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>AFractalIterator</CODE> class provides a base class for the iteration of a fractal.
 * <P>
 * All communication to and from the iterator is done via {@link ScreenLocation}s (columns and rows),
 * <CODE>ComplexNumber</CODE>s, and {@link IterationResult}s. 
 * <P>
 * <B>Note that this is an abstract class.</B>
 * 
 * @author  Sven Maerivoet
 * @version 01/01/2015
 */
public abstract class AFractalIterator
{
	/**
	 * The main and dual fractal types.
	 */
	public static enum EFractalType {kMainFractal, kDualFractal};

	/**
	 * The initial fractal screen bounds.
	 */
	public static Dimension kInitialScreenBounds = new Dimension(800,600);

	// initialisation constants
	private static final int kDefaultMaxNrOfIterations = 100;

	// internal datastructures
	protected EFractalType fFractalType;
	protected int fMaxNrOfIterations;
	protected double fEscapeRadius;
	protected double fEscapeRadiusSqr;
	protected ComplexNumber fDualParameter;
	protected int fScreenWidth;
	protected int fScreenHeight;
	protected Dimension fScreenBounds;
	protected double fP1X;
	protected double fP1Y;
	protected double fP2X;
	protected double fP2Y;
	protected ComplexNumber fP1;
	protected ComplexNumber fP2;
	protected double fComplexWidth;
	protected double fComplexHeight;
	protected boolean fInvertYAxis;
	protected ComplexNumber fZ0;
	protected int fFixedNrOfIterations;
	protected boolean fCalculateAdvancedColoring;
	protected double fStripingDensity;
	protected double fGaussianIntegersTrapFactor;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>AFractalIterator</CODE> object and initialises it with the default parameters.
	 */
	public AFractalIterator()
	{
		setFractalType(EFractalType.kMainFractal);
		setMaxNrOfIterations(kDefaultMaxNrOfIterations);
		setFixedNrOfIterations(getDefaultFixedNrOfIterations());
		setEscapeRadius(getDefaultEscapeRadius());
		setDualParameter(getDefaultDualParameter());
		setInvertYAxis(false);
		setComplexBounds(getDefaultP1(),getDefaultP2());
		resetMainFractalOrbitStartingPoint();
		setCalculateAdvancedColoring(true);
		setStripingDensity(4.0);
		setGaussianIntegersTrapFactor(1.0);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the fractal's family name.
	 *
	 * @return the fractal's family name
	 */
	public abstract String getFamilyName();

	/**
	 * Sets the fractal type to render.
	 * 
	 * @param fractalType  the fractal type to render
	 */
	public final void setFractalType(EFractalType fractalType)
	{
		fFractalType = fractalType;
	}

	/**
	 * Returns the fractal type that is displayed.
	 *
	 * @return the fractal type that is displayed
	 */
	public final EFractalType getFractalType()
	{
		return fFractalType;
	}

	/**
	 * Sets the maximum number of iterations to use in the escape-time algorithm.
	 *
	 * @param maxNrOfIterations the maximum number of iterations to use in the escape-time algorithm
	 */
	public final void setMaxNrOfIterations(int maxNrOfIterations)
	{
		fMaxNrOfIterations = maxNrOfIterations;
	}

	/**
	 * Returns the maximum number of iterations that is used in the escape-time algorithm.
	 *
	 * @return the maximum number of iterations that is used in the escape-time algorithm
	 */
	public final int getMaxNrOfIterations()
	{
		return fMaxNrOfIterations;
	}

	/**
	 * Sets the fixed number of iterations that should be used
	 * (thereby disabling checking of the escape radius).
	 * <P>
	 * If 0 is specified, then the fixed number is ignored.
	 * 
	 * @param fixedNrOfIterations  the fixed number of iterations that should be used
	 */
	public final void setFixedNrOfIterations(int fixedNrOfIterations)
	{
		fFixedNrOfIterations = fixedNrOfIterations;
	}

	/**
	 * Returns the fixed number of iterations that should be used
	 * (thereby disabling checking of the escape radius).
	 * 
	 * @return the fixed number of iterations that should be used
	 */
	public final int getFixedNrOfIterations()
	{
		return fFixedNrOfIterations;
	}

	/**
	 * Sets the escape radius to use in the escape-time algorithm.
	 *
	 * @param escapeRadius the escape radius to use in the escape-time algorithm
	 */
	public final void setEscapeRadius(double escapeRadius)
	{
		fEscapeRadius = escapeRadius;
		fEscapeRadiusSqr = escapeRadius * escapeRadius;
	}

	/**
	 * Returns the escape radius that is used in the escape-time algorithm.
	 *
	 * @return the escape radius that is used in the escape-time algorithm
	 */
	public final double getEscapeRadius()
	{
		return fEscapeRadius;
	}

	/**
	 * Sets the dual parameter in the complex plane.
	 * 
	 * @param dualParameter  the dual parameter in the complex plane
	 */
	public final void setDualParameter(ComplexNumber dualParameter)
	{
		fDualParameter = dualParameter;
	}

	/**
	 * Returns the dual parameter in the complex plane.
	 * 
	 * @return the dual parameter in the complex plane
	 */
	public final ComplexNumber getDualParameter()
	{
		return fDualParameter;
	}
	
	/**
	 * Sets whether or not the Y-axis is inverted.
	 * 
	 * @param invertYAxis  a <CODE>boolean</CODE> indicating whether or not the Y-axis is inverted
	 */
	public final void setInvertYAxis(boolean invertYAxis)
	{
		fInvertYAxis = invertYAxis;
	}

	/**
	 * Returns whether or not the Y-axis is inverted.
	 * 
	 * @return a <CODE>boolean</CODE> indicating whether or not the Y-axis is inverted
	 */
	public final boolean getInvertYAxis()
	{
		return fInvertYAxis;
	}

	/**
	 * Sets the screen bounds of this fractal.
	 *
	 * @param width   the width of the fractal
	 * @param height  the height of the fractal
	 */
	public final void setScreenBounds(int width, int height)
	{
		fScreenBounds = new Dimension(width,height);
		fScreenWidth = width;
		fScreenHeight = height;
	}

	/**
	 * Sets the screen bounds of this fractal.
	 *
	 * @param screenBounds  the screen bounds of the fractal
	 */
	public final void setScreenBounds(Dimension screenBounds)
	{
		setScreenBounds(screenBounds.width,screenBounds.height);
	}

	/**
	 * Returns the screen bounds of this fractal.
	 *
	 * @return the screen bounds of this fractal
	 */
	public final Dimension getScreenBounds()
	{
		return fScreenBounds;
	}

	/**
	 * Returns the fractal's screen width.
	 * 
	 * @return the fractal's screen width
	 */
	public final int getScreenWidth()
	{
		return fScreenWidth;
	}

	/**
	 * Returns the fractal's screen height.
	 * 
	 * @return the fractal's screen height
	 */
	public final int getScreenHeight()
	{
		return fScreenHeight;
	}
	
	/**
	 * Sets the bounds for this fractal in the complex plane.
	 *
	 * @param p1  the upper-left corner
	 * @param p2  the lower-right corner
	 */
	public final void setComplexBounds(ComplexNumber p1, ComplexNumber p2)
	{
		fP1X = p1.realComponent();
		fP1Y = p1.imaginaryComponent();
		fP2X = p2.realComponent();
		fP2Y = p2.imaginaryComponent();
		fP1 = new ComplexNumber(p1);
		fP2 = new ComplexNumber(p2);
		fComplexWidth = fP2X - fP1X;
		fComplexHeight = fP2Y - fP1Y;
	}

	/**
	 * Returns the upper-left corner in the complex plane.
	 * 
	 * @return the upper-left corner in the complex plane
	 */
	public final ComplexNumber getP1()
	{
		return fP1;
	}

	/**
	 * Returns the lower-right corner in the complex plane.
	 * 
	 * @return the lower-right corner in the complex plane
	 */
	public final ComplexNumber getP2()
	{
		return fP2;
	}

	/**
	 * Sets the starting point for orbit calculations of the main fractal.
	 * 
	 * @param z0  the starting point for orbit calculations of the main fractal
	 */
	public final void setMainFractalOrbitStartingPoint(ComplexNumber z0)
	{
		fZ0 = z0;
	}

	/**
	 * Sets the starting point for orbit calculations of the main fractal to 0 + 0i.
	 */
	public final void resetMainFractalOrbitStartingPoint()
	{
		fZ0 = new ComplexNumber();
	}

	/**
	 * Returns the starting point for orbit calculations of the main fractal.
	 * 
	 * @return the starting point for orbit calculations of the main fractal
	 */
	public final ComplexNumber getMainFractalOrbitStartingPoint()
	{
		return fZ0;
	}

	/**
	 * Sets the calculation of advanced colouring.
	 *
	 * @param calculateAdvancedColoring  a <CODE>boolean</CODE> indicating whether or not advanced colouring should be calculated
	 */
	public final void setCalculateAdvancedColoring(boolean calculateAdvancedColoring)
	{
		fCalculateAdvancedColoring = calculateAdvancedColoring;
	}

	/**
	 * Returns the calculation of advanced colouring.
	 *
	 * @return a <CODE>boolean</CODE> indicating whether or not advanced colouring should be calculated
	 */
	public final boolean getCalculateAdvancedColoring()
	{
		return fCalculateAdvancedColoring;
	}

	/**
	 * Sets the striping density.
	 *
	 * @param stripingDensity  the striping density
	 */
	public final void setStripingDensity(double stripingDensity)
	{
		fStripingDensity = stripingDensity;
	}

	/**
	 * Returns the striping density.
	 *
	 * @return the striping density
	 */
	public final double getStripingDensity()
	{
		return fStripingDensity;
	}

	/**
	 * Sets the Gaussian integers trap factor.
	 *
	 * @param gaussianIntegersTrapFactor  the Gaussian integers trap factor
	 */
	public final void setGaussianIntegersTrapFactor(double gaussianIntegersTrapFactor)
	{
		fGaussianIntegersTrapFactor = gaussianIntegersTrapFactor;
	}

	/**
	 * Returns the Gaussian integers trap factor.
	 *
	 * @return the Gaussian integers trap factor
	 */
	public final double getGaussianIntegersTrapFactor()
	{
		return fGaussianIntegersTrapFactor;
	}
	
	/**
	 * Helper method for converting a complex number to a screen location.
	 *
	 * @param c  the complex number
	 * @return   the screen location corresponding to the specified complex location
	 */
	public final ScreenLocation convertComplexNumberToScreenLocation(ComplexNumber c)
	{
		return convertComplexNumberToScreenLocation(c,fScreenWidth,fScreenHeight);
	}

	/**
	 * Helper method for converting a complex number to a screen location.
	 *
	 * @param c             the complex number
	 * @param screenWidth   the screen width of the fractal
	 * @param screenHeight  the screen height of the fractal
	 * @return              the screen location corresponding to the specified complex location
	 */
	public final ScreenLocation convertComplexNumberToScreenLocation(ComplexNumber c, int screenWidth, int screenHeight)
	{
		int y =  (int) (((fComplexHeight - (c.imaginaryComponent() - fP1Y)) / fComplexHeight) * screenHeight);
		if (fInvertYAxis) {
			y = (int) (((c.imaginaryComponent() - fP1Y) / fComplexHeight) * screenHeight);
		}
		// determine corresponding location on the screen (invert the Y-axis)
		return (new ScreenLocation((int) (((c.realComponent() - fP1X) / fComplexWidth) * screenWidth),y));
	}

	/**
	 * Helper method for converting a screen location to a complex number.
	 *
	 * @param s  the screen location
	 * @return   the complex number corresponding to the specified screen location
	 */
	public final ComplexNumber convertScreenLocationToComplexNumber(ScreenLocation s)
	{
		return convertScreenLocationToComplexNumber(s,fScreenWidth,fScreenHeight);
	}

	/**
	 * Helper method for converting a screen location to a complex number.
	 *
	 * @param s             the screen location
	 * @param screenWidth   the screen width of the fractal
	 * @param screenHeight  the screen height of the fractal
	 * @return              the complex number corresponding to the specified screen location
	 */
	public final ComplexNumber convertScreenLocationToComplexNumber(ScreenLocation s, int screenWidth, int screenHeight)
	{
		double y =  fP1Y + (((double) (screenHeight - s.fY) / (double) screenHeight) * fComplexHeight);
		if (fInvertYAxis) {
			y = fP1Y + (((double) s.fY / (double) screenHeight) * fComplexHeight);
		}
		// determine corresponding location in the complex plane (invert the Y-axis)
		return (new ComplexNumber(fP1X + (((double) s.fX / (double) screenWidth) * fComplexWidth),y));
	}

	/**
	 * Loads the current fractal parameters from a file.
	 * 
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a parse error occurs
	 */
	public final void loadParameters(TextFileParser tfp) throws FileParseException
	{
		setFractalType(EFractalType.valueOf(tfp.getNextString()));
		setMaxNrOfIterations(tfp.getNextInteger());
		setFixedNrOfIterations(tfp.getNextInteger());
		setEscapeRadius(tfp.getNextDouble());
		setDualParameter(new ComplexNumber(tfp.getNextDouble(),tfp.getNextDouble()));
		setInvertYAxis(tfp.getNextBoolean());
		setScreenBounds(tfp.getNextInteger(),tfp.getNextInteger());
		setComplexBounds(new ComplexNumber(tfp.getNextDouble(),tfp.getNextDouble()),new ComplexNumber(tfp.getNextDouble(),tfp.getNextDouble()));
		setMainFractalOrbitStartingPoint(new ComplexNumber(tfp.getNextDouble(),tfp.getNextDouble()));
		setStripingDensity(tfp.getNextDouble());
		setGaussianIntegersTrapFactor(tfp.getNextDouble());
		loadCustomParameters(tfp);
	}

	/**
	 * Saves the current fractal parameters to a file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	public final void saveParameters(TextFileWriter tfw) throws FileWriteException
	{
		tfw.writeString(getFamilyName());
		tfw.writeLn();

		tfw.writeString(fFractalType.toString());
		tfw.writeLn();

		tfw.writeInteger(fMaxNrOfIterations);
		tfw.writeLn();

		tfw.writeInteger(fFixedNrOfIterations);
		tfw.writeLn();

		tfw.writeDouble(fEscapeRadius);
		tfw.writeLn();

		tfw.writeDouble(fDualParameter.realComponent());
		tfw.writeLn();

		tfw.writeDouble(fDualParameter.imaginaryComponent());
		tfw.writeLn();

		tfw.writeBoolean(fInvertYAxis);
		tfw.writeLn();

		tfw.writeInteger(fScreenWidth);
		tfw.writeLn();

		tfw.writeInteger(fScreenHeight);
		tfw.writeLn();

		tfw.writeDouble(fP1X);
		tfw.writeLn();

		tfw.writeDouble(fP1Y);
		tfw.writeLn();

		tfw.writeDouble(fP2X);
		tfw.writeLn();

		tfw.writeDouble(fP2Y);
		tfw.writeLn();

		tfw.writeDouble(fZ0.realComponent());
		tfw.writeLn();

		tfw.writeDouble(fZ0.imaginaryComponent());
		tfw.writeLn();

		tfw.writeDouble(fStripingDensity);
		tfw.writeLn();

		tfw.writeDouble(fGaussianIntegersTrapFactor);
		tfw.writeLn();

		saveCustomParameters(tfw);
	}

	/**
	 * Returns a custom filename part.
	 * <P>
	 * Note that callers should return <CODE>(super.getCustomFilenamePart() + "xxx")</CODE>.
	 * 
	 * @return a custom filename part
	 */
	public String getCustomFilenamePart()
	{
		return "";
	}

	/**
	 * Checks whether or not a complex parameter <I>c</I> lies in the main fractal set (in parameter space, i.e the c-plane).
	 *
	 * @param s                 the screen location of the complex parameter <I>c</I>
	 * @param saveOrbit  a <CODE>boolean</CODE> indicating whether or not the orbit should be saved
	 * @return                  the iteration result with the forward orbit of 0+0i due to the specified complex parameter <I>c</I> 
	 */
	public final IterationResult iterateMainFractal(ScreenLocation s, boolean saveOrbit)
	{
		return iterate(getMainFractalOrbitStartingPoint(),convertScreenLocationToComplexNumber(s),saveOrbit);
	}

	/**
	 * Checks whether or not a complex parameter <I>c</I> lies in the main fractal set (in parameter space, i.e the c-plane).
	 *
	 * @param s             the screen location of the complex parameter <I>c</I>
	 * @param saveOrbit     a <CODE>boolean</CODE> indicating whether or not the orbit should be saved
	 * @param screenWidth   the screen width of the fractal
	 * @param screenHeight  the screen height of the fractal
	 * @return              the iteration result with the forward orbit of 0+0i due to the specified complex parameter <I>c</I> 
	 */
	public final IterationResult iterateMainFractal(ScreenLocation s, boolean saveOrbit, int screenWidth, int screenHeight)
	{
		return iterate(getMainFractalOrbitStartingPoint(),convertScreenLocationToComplexNumber(s,screenWidth,screenHeight),saveOrbit);
	}

	/**
	 * Checks whether or not a complex variable starting point <I>z</I> lies in a specified dual fractal set
	 * with complex parameter <I>c</I> (in dynamical/variable space, i.e the z-plane).
	 *
	 * @param s                 the screen location of the variable complex starting point <I>z</I> 
	 * @param c                 the complex parameter <I>c</I>
	 * @param saveOrbit  a <CODE>boolean</CODE> indicating whether or not the orbit should be saved
	 * @return                  the iteration result associated with the forward orbit of the complex variable starting point due to the specified complex number
	 */
	public final IterationResult iterateDualFractal(ScreenLocation s, ComplexNumber c, boolean saveOrbit)
	{
		return iterate(convertScreenLocationToComplexNumber(s),c,saveOrbit);
	}

	/**
	 * Checks whether or not a complex variable starting point <I>z</I> lies in a specified dual fractal set
	 * with complex parameter <I>c</I> (in dynamical/variable space, i.e the z-plane).
	 *
	 * @param s             the screen location of the variable complex starting point <I>z</I> 
	 * @param c             the complex parameter <I>c</I>
	 * @param saveOrbit     a <CODE>boolean</CODE> indicating whether or not the orbit should be saved
	 * @param screenWidth   the screen width of the fractal
	 * @param screenHeight  the screen height of the fractal
	 * @return              the iteration result associated with the forward orbit of the complex variable starting point due to the specified complex number
	 */
	public final IterationResult iterateDualFractal(ScreenLocation s, ComplexNumber c, boolean saveOrbit, int screenWidth, int screenHeight)
	{
		return iterate(convertScreenLocationToComplexNumber(s,screenWidth,screenHeight),c,saveOrbit);
	}

	/**
	 * Returns the default upper-left corner in the complex plane.
	 * 
	 * @return the default upper-left corner in the complex plane
	 */
	public abstract ComplexNumber getDefaultP1();

	/**
	 * Returns the default lower-right corner in the complex plane.
	 * 
	 * @return the default lower-right corner in the complex plane
	 */
	public abstract ComplexNumber getDefaultP2();

	/**
	 * Returns the default dual parameter in the complex plane.
	 * 
	 * @return the default dual parameter in the complex plane
	 */
	public abstract ComplexNumber getDefaultDualParameter();

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Returns the default escape radius.
	 * 
	 * @return the default escape radius
	 */
	protected abstract double getDefaultEscapeRadius();

	/**
	 * Returns the default of 0 fixed iterations to be used (so it is by default disabled).
	 * 
	 * @return the default of 0 fixed iterations to be used
	 */
	protected int getDefaultFixedNrOfIterations()
	{
		return 0;
	}

	/**
	 * Evaluates the fractal function for a specified complex point.
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the function evaluated with the given parameters
	 */
	protected abstract ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber c);

	/**
	 * Helper method for a general iteration with the escape-time algorithm.
	 *
	 * @param z          the complex variable <I>z</I>
	 * @param c          the complex parameter <I>c</I>
	 * @param saveOrbit  a <CODE>boolean</CODE> indicating whether or not the orbit should be saved
	 * @return           the iteration result associated with the forward orbit of the complex variable starting point due to the specified complex number
	 */
	protected abstract IterationResult iterate(ComplexNumber z, ComplexNumber c, boolean saveOrbit);

	/**
	 * Loads custom fractal parameters from a file.
	 * 
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a read error occurs
	 */
	protected void loadCustomParameters(TextFileParser tfp) throws FileParseException
	{
	}

	/**
	 * Saves custom fractal parameters to a file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	protected void saveCustomParameters(TextFileWriter tfw) throws FileWriteException
	{
	}
}
