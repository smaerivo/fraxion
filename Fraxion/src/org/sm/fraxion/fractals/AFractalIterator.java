// -------------------------------------
// Filename      : AFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 20/04/2016
// Target        : Java VM (1.8)
// -------------------------------------

/**
 * Copyright 2003-2016 Sven Maerivoet
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
import java.io.*;
import org.sm.fraxion.fractals.util.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.math.*;
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
 * @version 20/04/2016
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
	public static final Dimension kInitialScreenBounds = new Dimension(800,600);

	// initialisation constants
	private static final int kDefaultMaxNrOfIterations = 100;

	// internal datastructures
	protected EFractalType fFractalType;
	protected int fMaxNrOfIterations;
	protected boolean fUseFixedNrOfIterations;
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
	protected boolean fCalculateAdvancedColoring;
	protected double fInteriorStripingDensity;
	protected double fExteriorStripingDensity;
	protected double fInteriorGaussianIntegersTrapFactor;
	protected double fExteriorGaussianIntegersTrapFactor;
	protected ComplexNumber fInteriorOrbitTrapDiskCentre;
	protected ComplexNumber fExteriorOrbitTrapDiskCentre;
	protected double fInteriorOrbitTrapDiskRadius;
	protected double fExteriorOrbitTrapDiskRadius;
	protected ComplexNumber fInteriorOrbitTrapCrossStalksCentre;
	protected ComplexNumber fExteriorOrbitTrapCrossStalksCentre;
	protected double fInteriorOrbitTrapSineMultiplicativeFactor;
	protected double fExteriorOrbitTrapSineMultiplicativeFactor;
	protected double fInteriorOrbitTrapSineAdditiveFactor;
	protected double fExteriorOrbitTrapSineAdditiveFactor;
	protected double fInteriorOrbitTrapTangensMultiplicativeFactor;
	protected double fExteriorOrbitTrapTangensMultiplicativeFactor;
	protected double fInteriorOrbitTrapTangensAdditiveFactor;
	protected double fExteriorOrbitTrapTangensAdditiveFactor;

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
		setUseFixedNrOfIterations(false);
		setEscapeRadius(getDefaultEscapeRadius());
		setDualParameter(getDefaultDualParameter());
		setInvertYAxis(false);
		setComplexBounds(getDefaultP1(),getDefaultP2());
		resetMainFractalOrbitStartingPoint();
		setCalculateAdvancedColoring(false);
		setInteriorStripingDensity(4.0);
		setExteriorStripingDensity(4.0);
		setInteriorGaussianIntegersTrapFactor(1.0);
		setExteriorGaussianIntegersTrapFactor(1.0);
		setInteriorOrbitTrapDiskCentre(ComplexNumber.kZero);
		setExteriorOrbitTrapDiskCentre(ComplexNumber.kZero);
		setInteriorOrbitTrapDiskRadius(1.0);
		setExteriorOrbitTrapDiskRadius(1.0);
		setInteriorOrbitTrapCrossStalksCentre(ComplexNumber.kZero);
		setExteriorOrbitTrapCrossStalksCentre(ComplexNumber.kZero);
		setInteriorOrbitTrapSineMultiplicativeFactor(1.0);
		setExteriorOrbitTrapSineMultiplicativeFactor(1.0);
		setInteriorOrbitTrapSineAdditiveFactor(0.0);
		setExteriorOrbitTrapSineAdditiveFactor(0.0);
		setInteriorOrbitTrapTangensMultiplicativeFactor(1.0);
		setExteriorOrbitTrapTangensMultiplicativeFactor(1.0);
		setInteriorOrbitTrapTangensAdditiveFactor(0.0);
		setExteriorOrbitTrapTangensAdditiveFactor(0.0);
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
	 * Sets the maximum number of iterations to use.
	 * 
	 * @param maxNrOfIterations  the maximum number of iterations to use
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
	 * Auto determines the maximum number of iterations to use.
	 *
	 * @return the maximum number of iterations to use
	 */
	public final int autoDetermineMaxNrOfIterations()
	{
		// determine maximum number of iterations
		double zoomLevel = Math.log10(getCurrentZoomLevel());
		int maxNrOfIterations = 0;
		if (zoomLevel <= 1.0) {
			maxNrOfIterations = 100;
		}
		else if (zoomLevel <= 2.0) {
			maxNrOfIterations = 300;
		}
		else if (zoomLevel <= 3.0) {
			maxNrOfIterations = 750;
		}
		else if (zoomLevel > 3.0) {
			// apply a fitted quadratic curve
			maxNrOfIterations = (int) Math.round(
																	(7.0 * MathTools.quadr(zoomLevel))
																	- (160.0 * MathTools.cube(zoomLevel))
																	+ (1360.0 * MathTools.sqr(zoomLevel))
																	- (1655.0 * zoomLevel)
																	+ 300.0);
		}

		return maxNrOfIterations;
	}

	/**
	 * Sets whether or not a fixed number of iterations should be used
	 * (thereby disabling checking of the escape radius).
	 * 
	 * @param useFixedNrOfIterations  a <CODE>boolean</CODE> indicating whether or not a fixed number of iterations should be used
	 */
	public final void setUseFixedNrOfIterations(boolean useFixedNrOfIterations)
	{
		fUseFixedNrOfIterations = useFixedNrOfIterations;
	}

	/**
	 * Returns whether or not a fixed number of iterations should be used
	 * (thereby disabling checking of the escape radius).
	 * 
	 * @return a <CODE>boolean</CODE> indicating whether or not a fixed number of iterations should be used
	 */
	public final boolean getUseFixedNrOfIterations()
	{
		return fUseFixedNrOfIterations;
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
	 * Sets the interior striping density.
	 *
	 * @param interiorStripingDensity  the interior striping density
	 */
	public final void setInteriorStripingDensity(double interiorStripingDensity)
	{
		fInteriorStripingDensity = interiorStripingDensity;
	}

	/**
	 * Returns the interior striping density.
	 *
	 * @return the interior striping density
	 */
	public final double getInteriorStripingDensity()
	{
		return fInteriorStripingDensity;
	}

	/**
	 * Sets the exterior striping density.
	 *
	 * @param exteriorStripingDensity  the exterior striping density
	 */
	public final void setExteriorStripingDensity(double exteriorStripingDensity)
	{
		fExteriorStripingDensity = exteriorStripingDensity;
	}

	/**
	 * Returns the exterior striping density.
	 *
	 * @return the exterior striping density
	 */
	public final double getExteriorStripingDensity()
	{
		return fExteriorStripingDensity;
	}

	/**
	 * Sets the interior Gaussian integers trap factor.
	 *
	 * @param interiorGaussianIntegersTrapFactor  the interior Gaussian integers trap factor
	 */
	public final void setInteriorGaussianIntegersTrapFactor(double interiorGaussianIntegersTrapFactor)
	{
		fInteriorGaussianIntegersTrapFactor = interiorGaussianIntegersTrapFactor;
	}

	/**
	 * Returns the interior Gaussian integers trap factor.
	 *
	 * @return the interior Gaussian integers trap factor
	 */
	public final double getInteriorGaussianIntegersTrapFactor()
	{
		return fInteriorGaussianIntegersTrapFactor;
	}
	
	/**
	 * Sets the exterior Gaussian integers trap factor.
	 *
	 * @param exteriorGaussianIntegersTrapFactor  the exterior Gaussian integers trap factor
	 */
	public final void setExteriorGaussianIntegersTrapFactor(double exteriorGaussianIntegersTrapFactor)
	{
		fExteriorGaussianIntegersTrapFactor = exteriorGaussianIntegersTrapFactor;
	}

	/**
	 * Returns the exterior Gaussian integers trap factor.
	 *
	 * @return the exterior Gaussian integers trap factor
	 */
	public final double getExteriorGaussianIntegersTrapFactor()
	{
		return fExteriorGaussianIntegersTrapFactor;
	}

	/**
	 * Returns the interior orbit trap disk centre.
	 * 
	 *
	 * @return the interior orbit trap disk centre
	 */
	public final ComplexNumber getInteriorOrbitTrapDiskCentre()
	{
		return fInteriorOrbitTrapDiskCentre;
	}

	/**
	 * Sets the interior orbit trap disk centre.
	 *
	 * @param interiorOrbitTrapDiskCentre  the interior orbit trap disk centre
	 */
	public final void setInteriorOrbitTrapDiskCentre(ComplexNumber interiorOrbitTrapDiskCentre)
	{
		fInteriorOrbitTrapDiskCentre = interiorOrbitTrapDiskCentre;
	}

	/**
	 * Returns the exterior orbit trap disk centre.
	 *
	 * @return the exterior orbit trap disk centre
	 */
	public final ComplexNumber getExteriorOrbitTrapDiskCentre()
	{
		return fExteriorOrbitTrapDiskCentre;
	}

	/**
	 * Sets the exterior orbit trap disk centre.
	 *
	 * @param  exteriorOrbitTrapDiskCentre  the exterior orbit trap disk centre
	 */
	public final void setExteriorOrbitTrapDiskCentre(ComplexNumber exteriorOrbitTrapDiskCentre)
	{
		fExteriorOrbitTrapDiskCentre = exteriorOrbitTrapDiskCentre;
	}

	/**
	 * Sets the interior orbit trap disk radius.
	 *
	 * @param interiorOrbitTrapDiskRadius  the interior orbit trap disk radius
	 */
	public final void setInteriorOrbitTrapDiskRadius(double interiorOrbitTrapDiskRadius)
	{
		fInteriorOrbitTrapDiskRadius = interiorOrbitTrapDiskRadius;
	}

	/**
	 * Returns the interior orbit trap disk radius.
	 *
	 * @return the interior orbit trap disk radius
	 */
	public final double getInteriorOrbitTrapDiskRadius()
	{
		return fInteriorOrbitTrapDiskRadius;
	}

	/**
	 * Sets the exterior orbit trap disk radius.
	 *
	 * @param exteriorOrbitTrapDiskRadius  the exterior orbit trap disk radius
	 */
	public final void setExteriorOrbitTrapDiskRadius(double exteriorOrbitTrapDiskRadius)
	{
		fExteriorOrbitTrapDiskRadius = exteriorOrbitTrapDiskRadius;
	}

	/**
	 * Returns the exterior orbit trap disk radius.
	 *
	 * @return the exterior orbit trap disk radius
	 */
	public final double getExteriorOrbitTrapDiskRadius()
	{
		return fExteriorOrbitTrapDiskRadius;
	}

	/**
	 * Returns the interior orbit trap cross/stalks centre.
	 * 
	 *
	 * @return the interior orbit trap cross/stalks centre
	 */
	public final ComplexNumber getInteriorOrbitTrapCrossStalksCentre()
	{
		return fInteriorOrbitTrapCrossStalksCentre;
	}

	/**
	 * Sets the interior orbit trap cross/stalks centre.
	 *
	 * @param interiorOrbitTrapCrossStalksCentre  the interior orbit trap cross/stalks centre
	 */
	public final void setInteriorOrbitTrapCrossStalksCentre(ComplexNumber interiorOrbitTrapCrossStalksCentre)
	{
		fInteriorOrbitTrapCrossStalksCentre = interiorOrbitTrapCrossStalksCentre;
	}

	/**
	 * Returns the exterior orbit trap cross/stalks centre.
	 *
	 * @return the exterior orbit trap cross/stalks centre
	 */
	public final ComplexNumber getExteriorOrbitTrapCrossStalksCentre()
	{
		return fExteriorOrbitTrapCrossStalksCentre;
	}

	/**
	 * Sets the exterior orbit trap cross/stalks centre.
	 *
	 * @param exteriorOrbitTrapCrossStalksCentre  the exterior orbit trap cross/stalks centre
	 */
	public final void setExteriorOrbitTrapCrossStalksCentre(ComplexNumber exteriorOrbitTrapCrossStalksCentre)
	{
		fExteriorOrbitTrapCrossStalksCentre = exteriorOrbitTrapCrossStalksCentre;
	}

	/**
	 * Sets the interior orbit trap sine multiplicative factor.
	 *
	 * @param interiorOrbitTrapSineMultiplicativeFactor  the interior orbit trap sine multiplicative factor
	 */
	public final void setInteriorOrbitTrapSineMultiplicativeFactor(double interiorOrbitTrapSineMultiplicativeFactor)
	{
		fInteriorOrbitTrapSineMultiplicativeFactor = interiorOrbitTrapSineMultiplicativeFactor;
	}

	/**
	 * Returns the interior orbit trap sine multiplicative factor.
	 *
	 * @return the interior orbit trap sine multiplicative factor
	 */
	public final double getInteriorOrbitTrapSineMultiplicativeFactor()
	{
		return fInteriorOrbitTrapSineMultiplicativeFactor;
	}

	/**
	 * Sets the exterior orbit trap sine multiplicative factor.
	 *
	 * @param exteriorOrbitTrapSineMultiplicativeFactor  the exterior orbit trap sine multiplicative factor
	 */
	public final void setExteriorOrbitTrapSineMultiplicativeFactor(double exteriorOrbitTrapSineMultiplicativeFactor)
	{
		fExteriorOrbitTrapSineMultiplicativeFactor = exteriorOrbitTrapSineMultiplicativeFactor;
	}

	/**
	 * Returns the exterior orbit trap sine multiplicative factor.
	 *
	 * @return the exterior orbit trap sine multiplicative factor
	 */
	public final double getExteriorOrbitTrapSineMultiplicativeFactor()
	{
		return fExteriorOrbitTrapSineMultiplicativeFactor;
	}

	/**
	 * Sets the interior orbit trap sine additive factor.
	 *
	 * @param interiorOrbitTrapSineAdditiveFactor  the interior orbit trap sine additive factor
	 */
	public final void setInteriorOrbitTrapSineAdditiveFactor(double interiorOrbitTrapSineAdditiveFactor)
	{
		fInteriorOrbitTrapSineAdditiveFactor = interiorOrbitTrapSineAdditiveFactor;
	}

	/**
	 * Returns the interior orbit trap sine additive factor.
	 *
	 * @return the interior orbit trap sine additive factor
	 */
	public final double getInteriorOrbitTrapSineAdditiveFactor()
	{
		return fInteriorOrbitTrapSineAdditiveFactor;
	}

	/**
	 * Sets the exterior orbit trap sine additive factor.
	 *
	 * @param exteriorOrbitTrapSineAdditiveFactor  the exterior orbit trap sine additive factor
	 */
	public final void setExteriorOrbitTrapSineAdditiveFactor(double exteriorOrbitTrapSineAdditiveFactor)
	{
		fExteriorOrbitTrapSineAdditiveFactor = exteriorOrbitTrapSineAdditiveFactor;
	}

	/**
	 * Returns the exterior orbit trap sine additive factor.
	 *
	 * @return the exterior orbit trap sine additive factor
	 */
	public final double getExteriorOrbitTrapSineAdditiveFactor()
	{
		return fExteriorOrbitTrapSineAdditiveFactor;
	}

	/**
	 * Sets the interior orbit trap tangens factor.
	 *
	 * @param interiorOrbitTrapTangensMultiplicativeFactor  the interior orbit trap tangens multiplicative factor
	 */
	public final void setInteriorOrbitTrapTangensMultiplicativeFactor(double interiorOrbitTrapTangensMultiplicativeFactor)
	{
		fInteriorOrbitTrapTangensMultiplicativeFactor = interiorOrbitTrapTangensMultiplicativeFactor;
	}

	/**
	 * Returns the interior orbit trap tangens multiplicative factor.
	 *
	 * @return the interior orbit trap tangens multiplicative factor
	 */
	public final double getInteriorOrbitTrapTangensMultiplicativeFactor()
	{
		return fInteriorOrbitTrapTangensMultiplicativeFactor;
	}

	/**
	 * Sets the exterior orbit trap tangens multiplicative factor.
	 *
	 * @param exteriorOrbitTrapTangensMultiplicativeFactor  the exterior orbit trap tangens multiplicative factor
	 */
	public final void setExteriorOrbitTrapTangensMultiplicativeFactor(double exteriorOrbitTrapTangensMultiplicativeFactor)
	{
		fExteriorOrbitTrapTangensMultiplicativeFactor = exteriorOrbitTrapTangensMultiplicativeFactor;
	}

	/**
	 * Returns the exterior orbit trap tangens multiplicative factor.
	 *
	 * @return the exterior orbit trap tangens multiplicative factor
	 */
	public final double getExteriorOrbitTrapTangensMultiplicativeFactor()
	{
		return fExteriorOrbitTrapTangensMultiplicativeFactor;
	}

	/**
	 * Sets the interior orbit trap tangens additive factor.
	 *
	 * @param interiorOrbitTrapTangensAdditiveFactor  the interior orbit trap tangens additive factor
	 */
	public final void setInteriorOrbitTrapTangensAdditiveFactor(double interiorOrbitTrapTangensAdditiveFactor)
	{
		fInteriorOrbitTrapTangensAdditiveFactor = interiorOrbitTrapTangensAdditiveFactor;
	}

	/**
	 * Returns the interior orbit trap tangens additive factor.
	 *
	 * @return the interior orbit trap tangens additive factor
	 */
	public final double getInteriorOrbitTrapTangensAdditiveFactor()
	{
		return fInteriorOrbitTrapTangensAdditiveFactor;
	}

	/**
	 * Sets the exterior orbit trap tangens additive factor.
	 *
	 * @param exteriorOrbitTrapTangensAdditiveFactor  the exterior orbit trap tangens additive factor
	 */
	public final void setExteriorOrbitTrapTangensAdditiveFactor(double exteriorOrbitTrapTangensAdditiveFactor)
	{
		fExteriorOrbitTrapTangensAdditiveFactor = exteriorOrbitTrapTangensAdditiveFactor;
	}

	/**
	 * Returns the exterior orbit trap tangens additive factor.
	 *
	 * @return the exterior orbit trap tangens additive factor
	 */
	public final double getExteriorOrbitTrapTangensAdditiveFactor()
	{
		return fExteriorOrbitTrapTangensAdditiveFactor;
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
	 * Returns the current zoom level.
	 * 
	 * @return the current zoom level
	 */
	public final long getCurrentZoomLevel()
	{
		double defaultDeltaX = Math.abs(getDefaultP2().realComponent() - getDefaultP1().realComponent());
		double currentDeltaX = Math.abs(getP2().realComponent() - getP1().realComponent());
		double defaultDeltaY = Math.abs(getDefaultP2().imaginaryComponent() - getDefaultP1().imaginaryComponent());
		double currentDeltaY = Math.abs(getP2().imaginaryComponent() - getP1().imaginaryComponent());
		double zoomLevelX = Math.round(defaultDeltaX / currentDeltaX);
		double zoomLevelY = Math.round(defaultDeltaY / currentDeltaY);

		return (long) Math.max(zoomLevelX,zoomLevelY);
	}

	/**
	 * Loads the current fractal parameters from a plain-text file.
	 * 
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a parse error occurs
	 */
	public final void plainTextLoadParameters(TextFileParser tfp) throws FileParseException
	{
		setFractalType(EFractalType.valueOf(tfp.getNextString()));
		setMaxNrOfIterations(tfp.getNextInteger());
		setUseFixedNrOfIterations(tfp.getNextBoolean());
		setEscapeRadius(tfp.getNextDouble());
		setDualParameter(new ComplexNumber(tfp.getNextDouble(),tfp.getNextDouble()));
		setInvertYAxis(tfp.getNextBoolean());
		setScreenBounds(tfp.getNextInteger(),tfp.getNextInteger());
		setComplexBounds(new ComplexNumber(tfp.getNextDouble(),tfp.getNextDouble()),new ComplexNumber(tfp.getNextDouble(),tfp.getNextDouble()));
		setMainFractalOrbitStartingPoint(new ComplexNumber(tfp.getNextDouble(),tfp.getNextDouble()));
		setInteriorStripingDensity(tfp.getNextDouble());
		setExteriorStripingDensity(tfp.getNextDouble());
		setInteriorGaussianIntegersTrapFactor(tfp.getNextDouble());
		setExteriorGaussianIntegersTrapFactor(tfp.getNextDouble());
		plainTextLoadCustomParameters(tfp);
	}

	/**
	 * Loads the current fractal parameters from a file as a stream.
	 * 
	 * @param  dataInputStream  a data inputstream
	 * @throws IOException      in case a parse error occurs
	 */
	public final void streamLoadParameters(DataInputStream dataInputStream) throws IOException
	{
		setFractalType(EFractalType.valueOf(dataInputStream.readUTF()));
		setMaxNrOfIterations(dataInputStream.readInt());
		setUseFixedNrOfIterations(dataInputStream.readBoolean());
		setEscapeRadius(dataInputStream.readDouble());
		setDualParameter(new ComplexNumber(dataInputStream.readDouble(),dataInputStream.readDouble()));
		setInvertYAxis(dataInputStream.readBoolean());
		setScreenBounds(dataInputStream.readInt(),dataInputStream.readInt());
		setComplexBounds(new ComplexNumber(dataInputStream.readDouble(),dataInputStream.readDouble()),new ComplexNumber(dataInputStream.readDouble(),dataInputStream.readDouble()));
		setMainFractalOrbitStartingPoint(new ComplexNumber(dataInputStream.readDouble(),dataInputStream.readDouble()));
		setInteriorStripingDensity(dataInputStream.readDouble());
		setExteriorStripingDensity(dataInputStream.readDouble());
		setInteriorGaussianIntegersTrapFactor(dataInputStream.readDouble());
		setExteriorGaussianIntegersTrapFactor(dataInputStream.readDouble());
		streamLoadCustomParameters(dataInputStream);
	}

	/**
	 * Saves the current fractal parameters to a plain-text file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	public final void plainTextSaveParameters(TextFileWriter tfw) throws FileWriteException
	{
		tfw.writeString(getFamilyName());
		tfw.writeLn();

		tfw.writeString(fFractalType.toString());
		tfw.writeLn();

		tfw.writeInteger(fMaxNrOfIterations);
		tfw.writeLn();

		tfw.writeBoolean(fUseFixedNrOfIterations);
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

		tfw.writeDouble(fInteriorStripingDensity);
		tfw.writeLn();

		tfw.writeDouble(fExteriorStripingDensity);
		tfw.writeLn();

		tfw.writeDouble(fInteriorGaussianIntegersTrapFactor);
		tfw.writeLn();

		tfw.writeDouble(fExteriorGaussianIntegersTrapFactor);
		tfw.writeLn();

		plainTextSaveCustomParameters(tfw);
	}

	/**
	 * Saves the current fractal parameters to a file using a stream.
	 * 
	 * @param  dataOutputStream  a data outputstream
	 * @throws IOException       in case a write error occurs
	 */
	public final void streamSaveParameters(DataOutputStream dataOutputStream) throws IOException
	{
		dataOutputStream.writeUTF(getFamilyName());
		dataOutputStream.writeUTF(fFractalType.toString());
		dataOutputStream.writeInt(fMaxNrOfIterations);
		dataOutputStream.writeBoolean(fUseFixedNrOfIterations);
		dataOutputStream.writeDouble(fEscapeRadius);
		dataOutputStream.writeDouble(fDualParameter.realComponent());
		dataOutputStream.writeDouble(fDualParameter.imaginaryComponent());
		dataOutputStream.writeBoolean(fInvertYAxis);
		dataOutputStream.writeInt(fScreenWidth);
		dataOutputStream.writeInt(fScreenHeight);
		dataOutputStream.writeDouble(fP1X);
		dataOutputStream.writeDouble(fP1Y);
		dataOutputStream.writeDouble(fP2X);
		dataOutputStream.writeDouble(fP2Y);
		dataOutputStream.writeDouble(fZ0.realComponent());
		dataOutputStream.writeDouble(fZ0.imaginaryComponent());
		dataOutputStream.writeDouble(fInteriorStripingDensity);
		dataOutputStream.writeDouble(fExteriorStripingDensity);
		dataOutputStream.writeDouble(fInteriorGaussianIntegersTrapFactor);
		dataOutputStream.writeDouble(fExteriorGaussianIntegersTrapFactor);

		streamSaveCustomParameters(dataOutputStream);
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
	 * Loads custom fractal parameters from a plain-text file.
	 * 
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a read error occurs
	 */
	protected void plainTextLoadCustomParameters(TextFileParser tfp) throws FileParseException
	{
	}

	/**
	 * Loads custom fractal parameters from a file as a stream.
	 * 
	 * @param  dataInputStream  a data inputstream
	 * @throws IOException      in case a parse error occurs
	 */
	protected void streamLoadCustomParameters(DataInputStream dataInputStream) throws IOException
	{
	}

	/**
	 * Saves custom fractal parameters to a plain-text file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	protected void plainTextSaveCustomParameters(TextFileWriter tfw) throws FileWriteException
	{
	}

	/**
	 * Saves custom fractal parameters to a file as a stream.
	 * 
	 * @param  dataOutputStream  a data outputstream
	 * @throws IOException       in case a write error occurs
	 */
	protected void streamSaveCustomParameters(DataOutputStream dataOutputStream) throws IOException
	{
	}
}
