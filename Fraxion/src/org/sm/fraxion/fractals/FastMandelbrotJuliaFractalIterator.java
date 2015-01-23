// --------------------------------------------------
// Filename      : FastMandelbrotJuliaFractalIterator
// Author        : Sven Maerivoet
// Last modified : 22/01/2015
// Target        : Java VM (1.8)
// --------------------------------------------------

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

import org.sm.fraxion.fractals.util.*;
import org.sm.smtools.math.*;
import org.sm.smtools.math.complex.*;

/**
 * The <CODE>FastMandelbrotJuliaFractalIterator</CODE> class provides a fast implementation of the default Mandelbrot/Julia fractal.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 * 
 * @author  Sven Maerivoet
 * @version 22/01/2015
 */
public final class FastMandelbrotJuliaFractalIterator extends AFractalIterator
{
	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the family name of this fractal.
	 *
	 * @return the family name of this fractal
	 */
	@Override
	public String getFamilyName()
	{
		return "Mandelbrot / Julia";
	}

	/**
	 * Returns the default upper-left corner in the complex plane.
	 * 
	 * @return the default upper-left corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP1()
	{
		return (new ComplexNumber(-2.01,-2.01));
	}

	/**
	 * Returns the default lower-right corner in the complex plane.
	 * 
	 * @return the default lower-right corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP2()
	{
		return (new ComplexNumber(+2.01,+2.01));
	}

	/**
	 * Returns the default dual parameter in the complex plane.
	 * 
	 * @return the default dual parameter in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultDualParameter()
	{
		// the default dual parameter is 0 + 0i
		return (new ComplexNumber());
	}

	/**
	 * Returns the default escape radius of 100.
	 * 
	 * @return the default escape radius of 100
	 */
	@Override
	public double getDefaultEscapeRadius()
	{
		// a high escape radius gives smoother colouring results
		return 100.0;
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Evaluates the Mandelbrot / Julia function for a specified complex point.
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the function evaluated with the given parameters
	 */
	@Override
	protected ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber c)
	{
		return null;
	}

	/**
	 * Evaluates the fractal function for a specified complex point.
	 * <P>
	 * The default behaviour redirects to the function without <I>z</I>(<I>n</I> - 1).
	 * 
	 * @param z          the complex variable <I>z</I> at <I>n</I>
	 * @param zPrevious  the complex variable <I>z</I> at <I>n</I> - 1
	 * @param c          the complex parameter <I>c</I>
	 * @return           the function evaluated with the given parameters
	 */
	protected ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber zPrevious, ComplexNumber c)
	{
		return evaluateFractalFunction(z,c);
	}

	/**
	 * Helper method for a general iteration with the escape-time algorithm.
	 *
	 * @param z          the complex variable <I>z</I>
	 * @param c          the complex parameter <I>c</I>
	 * @param saveOrbit  a <CODE>boolean</CODE> indicating whether or not the orbit should be saved
	 * @return           the iteration result associated with the forward orbit of the complex variable starting point due to the specified complex number
	 */
	@Override
	protected IterationResult iterate(ComplexNumber z, ComplexNumber c, boolean saveOrbit)
	{
		IterationResult iterationResult = new IterationResult();
		int maxNrOfIterations = fMaxNrOfIterations;

		if (saveOrbit) {
			iterationResult.fComplexOrbit = new ComplexNumber[maxNrOfIterations];
			iterationResult.fScreenOrbit = new ScreenLocation[maxNrOfIterations];
			for (int iteration = 0; iteration < maxNrOfIterations; ++iteration) {
				iterationResult.fComplexOrbit[iteration] = new ComplexNumber();
				iterationResult.fScreenOrbit[iteration] = new ScreenLocation();
			}
		}

		double zX = z.realComponent();
		double zY = z.imaginaryComponent();
		double cX = c.realComponent();
		double cY = c.imaginaryComponent();

		double modulusSqr = (zX * zX) + (zY * zY);
		double zXPrevious = zX;
		double zYPrevious = zY;
		double zXPreviousPrevious = zXPrevious;
		double zYPreviousPrevious = zYPrevious;

		int maxNrOfCurvaturesStripings = maxNrOfIterations;
		int fixedNrOfIterations = getFixedNrOfIterations();
		if (fixedNrOfIterations > 0) {
			maxNrOfCurvaturesStripings = fixedNrOfIterations;
		}

		double[] curvatures = new double[maxNrOfCurvaturesStripings];
		double[] stripings = new double[maxNrOfCurvaturesStripings];

		iterationResult.fMinimumGaussianIntegersDistance = Double.MAX_VALUE;
		iterationResult.fAverageGaussianIntegersDistance = 0.0;

		while (((fixedNrOfIterations == 0) && (modulusSqr <= fEscapeRadiusSqr) && (iterationResult.fNrOfIterations < maxNrOfIterations)) ||
					(fixedNrOfIterations > 0) && (iterationResult.fNrOfIterations < fixedNrOfIterations)) {

			// explicitly iterate fractal function
			double zXNext = (zX * zX) - (zY * zY) + cX;
			double zYNext = 2.0 * zX * zY + cY;
			zXPreviousPrevious = zXPrevious;
			zYPreviousPrevious = zYPrevious;
			zXPrevious = zX;
			zYPrevious = zY;
			zX = zXNext;
			zY = zYNext;

			modulusSqr = (zX * zX) + (zY * zY);

			if (fCalculateAdvancedColoring) {
				double ztX = zX - zXPrevious;
				double ztY = zY - zYPrevious;
				double zt2X = zXPrevious - zXPreviousPrevious;
				double zt2Y = zYPrevious - zYPreviousPrevious;
				double ztModulusSqr = (zt2X * zt2X) + (zt2Y * zt2Y);
				if (ztModulusSqr != 0.0) {
					double zXRes = ((ztX * zt2X) + (ztY * zt2Y)) / ztModulusSqr;
					double zYRes = ((ztY * zt2X) - (ztX * zt2Y)) / ztModulusSqr;
					curvatures[(int) iterationResult.fNrOfIterations] = Math.abs(Math.atan2(zYRes,zXRes));
				}
				stripings[(int) iterationResult.fNrOfIterations] = 0.5 * Math.sin(fStripingDensity * Math.atan2(zY,zX)) + 0.5;
			}

			++iterationResult.fNrOfIterations;
			double modulus = Math.sqrt(modulusSqr);
			iterationResult.fExponentialIterationCount += Math.exp(-modulus);
			iterationResult.fModulus = modulus;
			iterationResult.fAverageDistance = ((iterationResult.fAverageDistance * (iterationResult.fNrOfIterations - 1)) + modulus) / iterationResult.fNrOfIterations;
			iterationResult.fLyapunovExponent += (0.5 * Math.log(modulusSqr));

			// calculate Gaussian distances
			if (fCalculateAdvancedColoring) {
				double xClosestGaussian = Math.round(zX * fGaussianIntegersTrapFactor) / fGaussianIntegersTrapFactor;
				double yClosestGaussian = Math.round(zY * fGaussianIntegersTrapFactor) / fGaussianIntegersTrapFactor;
				double gaussianDistance = Math.sqrt(((zX - xClosestGaussian) * (zX - xClosestGaussian)) + ((zY - yClosestGaussian) * (zY - yClosestGaussian)));
				if (gaussianDistance < iterationResult.fMinimumGaussianIntegersDistance) {
					iterationResult.fMinimumGaussianIntegersDistance = gaussianDistance;
				}
				iterationResult.fAverageGaussianIntegersDistance = ((iterationResult.fAverageGaussianIntegersDistance * (iterationResult.fNrOfIterations - 1)) + gaussianDistance) / iterationResult.fNrOfIterations;				
			}

			if (saveOrbit) {
				int iterationArrayPos = (int) iterationResult.fNrOfIterations - 1;
				ComplexNumber zIter = new ComplexNumber(zX,zY);
				iterationResult.fComplexOrbit[iterationArrayPos] = zIter;
				iterationResult.fScreenOrbit[iterationArrayPos] = convertComplexNumberToScreenLocation(zIter);
			}
		} // while ()

		// determine the final values and angle
		iterationResult.fRealComponent = zX;
		iterationResult.fImaginaryComponent = zY;
		iterationResult.fAngle = Math.atan2(zY,zX);
		iterationResult.fLyapunovExponent /= iterationResult.fNrOfIterations;

		// estimate curvature and striping
		double prevCurvature = 0.0;
		double prevStriping = 0.0;
		if (fCalculateAdvancedColoring) {
			iterationResult.fCurvature = 0.0;
			iterationResult.fStriping = 0.0;
			for (int i = 0; i < iterationResult.fNrOfIterations; ++i) {
				iterationResult.fCurvature += curvatures[i];
				iterationResult.fStriping += stripings[i];
				if (i < (iterationResult.fNrOfIterations - 1)) {
					prevCurvature += curvatures[i];
					prevStriping += stripings[i];
				}
			}
			if (iterationResult.fNrOfIterations > 0) {
				iterationResult.fCurvature /= iterationResult.fNrOfIterations;
				iterationResult.fStriping /= iterationResult.fNrOfIterations;
				if ((iterationResult.fNrOfIterations - 1) > 0) {
					prevCurvature /= (iterationResult.fNrOfIterations - 1.0);
					prevStriping /= (iterationResult.fNrOfIterations - 1.0);
				}
			}
		}

		// adjust for an assumed infinite number of iterations
		if (iterationResult.fNrOfIterations == maxNrOfIterations) {
			iterationResult.fNrOfIterations = IterationResult.kInfinity;
			iterationResult.fNormalisedIterationCount = IterationResult.kInfinity;
			// leave the other results untouched as they are used for interior colouring
		}
		else if (iterationResult.fNrOfIterations > 0) {
			iterationResult.fNormalisedIterationCount = iterationResult.fNrOfIterations + 1.0 - (Math.log(Math.log(Math.sqrt(modulusSqr)) / Math.log(fEscapeRadius)) / Math.log(2.0));

			// smoothen curvatures and stripings
			if (fCalculateAdvancedColoring) {
				double fraction = MathTools.frac(iterationResult.fNormalisedIterationCount);
				iterationResult.fCurvature = (fraction * iterationResult.fCurvature) + ((1.0 - fraction) * prevCurvature);
				iterationResult.fStriping = (fraction * iterationResult.fStriping) + ((1.0 - fraction) * prevStriping);
			}
		}

		return iterationResult;
	}
}
