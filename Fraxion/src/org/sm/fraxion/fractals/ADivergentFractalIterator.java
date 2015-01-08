// ----------------------------------------------
// Filename      : ADivergentFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 19/11/2014
// Target        : Java VM (1.8)
// ----------------------------------------------

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
 * The <CODE>ADivergentFractalIterator</CODE> class provides a base class for divergent fractals (e.g., Mandelbrot).
 * <P>
 * <B>Note that this is an abstract class.</B>
 * 
 * @author  Sven Maerivoet
 * @version 19/11/2014
 */
public abstract class ADivergentFractalIterator extends AFractalIterator
{
	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Returns whether or not the image should be rotated 90 degrees clockwise.
	 * <P>
	 * The default is <CODE>false</CODE>.
	 *
	 * @return a <CODE>boolean</CODE> indicating whether or not the image should be rotated 90 degrees clockwise
	 */
	protected boolean rotateImage()
	{
		return false;
	}

	/**
	 * Returns whether or not the Spider fractal function is activated.
	 * <P>
	 * The default is <CODE>false</CODE>
	 *
	 * @return a <CODE>boolean</CODE> indicating whether or not the Spider fractal function is activated
	 */
	protected boolean renderSpider()
	{
		return false;
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
		if (rotateImage()) {
			// rotate image 90 degrees clockwise
			z = new ComplexNumber(-z.imaginaryComponent(),z.realComponent());
		}

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

		double modulusSqr = z.modulusSquared();
		ComplexNumber zPrevious = z;
		ComplexNumber zPreviousPrevious = zPrevious;

		if (renderSpider()) {
			z = c;
		}

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

			// iterate fractal function
			ComplexNumber zNext = evaluateFractalFunction(z,zPrevious,c);
			zPreviousPrevious = zPrevious;
			zPrevious = z;
			z = zNext;

			if (renderSpider()) {
				c = c.divide(ComplexNumber.kTwo).add(z);
			}

			if (fCalculateAdvancedColoring) {
				curvatures[(int) iterationResult.fNrOfIterations] = Math.abs(z.subtract(zPrevious).divide(zPrevious.subtract(zPreviousPrevious)).argument());
				stripings[(int) iterationResult.fNrOfIterations] = 0.5 * Math.sin(fStripingDensity * z.argument()) + 0.5;
			}

			modulusSqr = z.modulusSquared();
			++iterationResult.fNrOfIterations;
			double modulus = Math.sqrt(modulusSqr);
			iterationResult.fExponentialIterationCount += Math.exp(-modulus);
			iterationResult.fModulus = modulus;
			if (modulus > iterationResult.fMaxModulus) {
				iterationResult.fMaxModulus = modulus;
			}
			iterationResult.fTotalDistance += modulus;
			iterationResult.fAverageDistance = ((iterationResult.fAverageDistance * (iterationResult.fNrOfIterations - 1)) + modulus) / iterationResult.fNrOfIterations;
			iterationResult.fTotalAngle += MathTools.atan(z.realComponent(),z.imaginaryComponent());
			iterationResult.fLyapunovExponent += (0.5 * Math.log(modulusSqr));

			// calculate Gaussian distances
			if (fCalculateAdvancedColoring) {
				double zx = z.realComponent();
				double zy = z.imaginaryComponent();
				double xClosestGaussian = Math.round(zx * fGaussianIntegersTrapFactor) / fGaussianIntegersTrapFactor;
				double yClosestGaussian = Math.round(zy * fGaussianIntegersTrapFactor) / fGaussianIntegersTrapFactor;
				double gaussianDistance = Math.sqrt(((zx - xClosestGaussian) * (zx - xClosestGaussian)) + ((zy - yClosestGaussian) * (zy - yClosestGaussian)));
				if (gaussianDistance < iterationResult.fMinimumGaussianIntegersDistance) {
					iterationResult.fMinimumGaussianIntegersDistance = gaussianDistance;
				}
				iterationResult.fAverageGaussianIntegersDistance = ((iterationResult.fAverageGaussianIntegersDistance * (iterationResult.fNrOfIterations - 1)) + gaussianDistance) / iterationResult.fNrOfIterations;				
			}

			if (saveOrbit) {
				int iterationArrayPos = (int) iterationResult.fNrOfIterations - 1;
				iterationResult.fComplexOrbit[iterationArrayPos] = z;
				iterationResult.fScreenOrbit[iterationArrayPos] = convertComplexNumberToScreenLocation(z);
			}
		} // while ()

		// determine the final values and angle
		iterationResult.fRealComponent = z.realComponent();
		iterationResult.fImaginaryComponent = z.imaginaryComponent();
		iterationResult.fAngle = z.argument();
		iterationResult.fLyapunovExponent /= iterationResult.fNrOfIterations;

		// normalise the total angle between 0 and 2PI
		iterationResult.fTotalAngle = iterationResult.fTotalAngle % (2.0 * Math.PI);

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
			double power = 2.0;
			if (this instanceof APowerFractalIterator) {
				power = ((APowerFractalIterator) this).getPower().modulus();
			}
			double denominator = 1.0;
			if (power != 1.0) {
				denominator = Math.log(power);
			}
			iterationResult.fNormalisedIterationCount = iterationResult.fNrOfIterations + 1.0 - (Math.log(Math.log(Math.sqrt(modulusSqr)) / Math.log(fEscapeRadius)) / denominator);

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
