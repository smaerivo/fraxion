// -------------------------------------------
// Filename      : AMagnetFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 24/01/2015
// Target        : Java VM (1.8)
// -------------------------------------------

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
import org.sm.smtools.exceptions.*;
import org.sm.smtools.math.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>AMagnetFractalIterator</CODE> class provides a base class for magnet fractals (converging and diverging).
 * <P>
 * <B>Note that this is an abstract class.</B>
 * 
 * @author  Sven Maerivoet
 * @version 24/01/2015
 */
public abstract class AMagnetFractalIterator extends AFractalIterator
{
	// internal datastructures
	private double fRootTolerance;
	private double fRootToleranceSquared;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>AMagnetFractalIterator</CODE> object.
	 */
	public AMagnetFractalIterator()
	{
		setRootTolerance(1E-3);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

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

	/**
	 * Sets the tolerance used for finding roots.
	 *
	 * @param rootTolerance  the tolerance used for finding roots
	 */
	public final void setRootTolerance(double rootTolerance)
	{
		fRootTolerance = rootTolerance;
		fRootToleranceSquared = fRootTolerance * fRootTolerance;
	}

	/**
	 * Returns the tolerance used for finding roots.
	 *
	 * @return the tolerance used for finding roots
	 */
	public final double getRootTolerance()
	{
		return fRootTolerance;
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Helper method for a general iteration with a combined escape-time and convergence algorithm.
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

		if (saveOrbit) {
			iterationResult.fComplexOrbit = new ComplexNumber[fMaxNrOfIterations];
			iterationResult.fScreenOrbit = new ScreenLocation[fMaxNrOfIterations];
			for (int iteration = 0; iteration < fMaxNrOfIterations; ++iteration) {
				iterationResult.fComplexOrbit[iteration] = new ComplexNumber();
				iterationResult.fScreenOrbit[iteration] = new ScreenLocation();
			}
		}

		double modulusSqr = z.modulusSquared();
		ComplexNumber zPrevious = z;
		ComplexNumber zPreviousPrevious = zPrevious;
		ComplexNumber zOffset = new ComplexNumber(z.realComponent() - 1.0,z.imaginaryComponent());

		int maxNrOfCurvaturesStripings = fMaxNrOfIterations;
		int fixedNrOfIterations = getFixedNrOfIterations();
		if (fixedNrOfIterations > 0) {
			maxNrOfCurvaturesStripings = fixedNrOfIterations;
		}

		double[] curvatures = new double[maxNrOfCurvaturesStripings];
		double[] angles = new double[maxNrOfCurvaturesStripings];

		double minimumInteriorGaussianIntegersDistance = Double.MAX_VALUE;
		double minimumExteriorGaussianIntegersDistance = Double.MAX_VALUE;
		double averageInteriorGaussianIntegersDistance = 0.0;
		double averageExteriorGaussianIntegersDistance = 0.0;

		while ((modulusSqr < fEscapeRadiusSqr) &&
					(iterationResult.fNrOfIterations < fMaxNrOfIterations) &&
					(zOffset.modulusSquared() > fRootToleranceSquared)) {

			// iterate fractal function
			ComplexNumber zNext = evaluateFractalFunction(z,c);
			zPreviousPrevious = zPrevious;
			zPrevious = z;
			z = zNext;

			if (fCalculateAdvancedColoring) {
				curvatures[(int) iterationResult.fNrOfIterations] = Math.abs(z.subtract(zPrevious).divide(zPrevious.subtract(zPreviousPrevious)).argument());
				angles[(int) iterationResult.fNrOfIterations] = z.argument();
			}

			modulusSqr = z.modulusSquared();
			++iterationResult.fNrOfIterations;
			double modulus = Math.sqrt(modulusSqr);
			iterationResult.fExponentialIterationCount += Math.exp(-modulus);
			iterationResult.fModulus = modulus;
			iterationResult.fAverageDistance = ((iterationResult.fAverageDistance * (iterationResult.fNrOfIterations - 1)) + modulus) / iterationResult.fNrOfIterations;				
			iterationResult.fLyapunovExponent += (0.5 * Math.log(modulusSqr));

			zOffset = new ComplexNumber(z.realComponent() - 1.0,z.imaginaryComponent());

			// calculate Gaussian distances
			if (fCalculateAdvancedColoring) {
				double zX = z.realComponent();
				double zY = z.imaginaryComponent();
				double xClosestInteriorGaussian = Math.round(zX * fInteriorGaussianIntegersTrapFactor) / fInteriorGaussianIntegersTrapFactor;
				double yClosestInteriorGaussian = Math.round(zY * fInteriorGaussianIntegersTrapFactor) / fInteriorGaussianIntegersTrapFactor;
				double interiorGaussianDistance = Math.sqrt(((zX - xClosestInteriorGaussian) * (zX - xClosestInteriorGaussian)) + ((zY - yClosestInteriorGaussian) * (zY - yClosestInteriorGaussian)));
				if (interiorGaussianDistance < minimumInteriorGaussianIntegersDistance) {
					minimumInteriorGaussianIntegersDistance = interiorGaussianDistance;
				}
				averageInteriorGaussianIntegersDistance = ((averageInteriorGaussianIntegersDistance * (iterationResult.fNrOfIterations - 1)) + interiorGaussianDistance) / iterationResult.fNrOfIterations;

				double xClosestExteriorGaussian = Math.round(zX * fExteriorGaussianIntegersTrapFactor) / fExteriorGaussianIntegersTrapFactor;
				double yClosestExteriorGaussian = Math.round(zY * fExteriorGaussianIntegersTrapFactor) / fExteriorGaussianIntegersTrapFactor;
				double exteriorGaussianDistance = Math.sqrt(((zX - xClosestExteriorGaussian) * (zX - xClosestExteriorGaussian)) + ((zY - yClosestExteriorGaussian) * (zY - yClosestExteriorGaussian)));
				if (exteriorGaussianDistance < minimumExteriorGaussianIntegersDistance) {
					minimumExteriorGaussianIntegersDistance = exteriorGaussianDistance;
				}
				averageExteriorGaussianIntegersDistance = ((averageExteriorGaussianIntegersDistance * (iterationResult.fNrOfIterations - 1)) + exteriorGaussianDistance) / iterationResult.fNrOfIterations;
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

		// estimate curvature and striping
		double prevCurvature = 0.0;
		double prevStriping = 0.0;
		if (fCalculateAdvancedColoring) {
			iterationResult.fCurvature = 0.0;
			iterationResult.fStriping = 0.0;
			for (int i = 0; i < iterationResult.fNrOfIterations; ++i) {
				iterationResult.fCurvature += curvatures[i];
				double striping = 0.0;
				if (iterationResult.fNrOfIterations == fMaxNrOfIterations) {
					striping = (0.5 * Math.sin(fInteriorStripingDensity * angles[i]) + 0.5);
					iterationResult.fStriping += striping;
				}
				else {
					striping = (0.5 * Math.sin(fExteriorStripingDensity * angles[i]) + 0.5);
					iterationResult.fStriping += striping;
				}
				if (i < (iterationResult.fNrOfIterations - 1)) {
					prevCurvature += curvatures[i];
					prevStriping += striping;
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

			if (iterationResult.fNrOfIterations == fMaxNrOfIterations) {
				iterationResult.fMinimumGaussianIntegersDistance = minimumInteriorGaussianIntegersDistance;
				iterationResult.fAverageGaussianIntegersDistance = averageInteriorGaussianIntegersDistance;
			}
			else {
				iterationResult.fMinimumGaussianIntegersDistance = minimumExteriorGaussianIntegersDistance;
				iterationResult.fAverageGaussianIntegersDistance = averageExteriorGaussianIntegersDistance;
			}
		} // if (fCalculateAdvancedColoring)

		// adjust for an assumed infinite number of iterations
		if (iterationResult.fNrOfIterations == fMaxNrOfIterations) {
			iterationResult.fNrOfIterations = IterationResult.kInfinity;
			iterationResult.fNormalisedIterationCount = IterationResult.kInfinity;
			// leave the other results untouched as they are used for interior colouring
		}
		else if (iterationResult.fNrOfIterations > 0) {
			iterationResult.fNormalisedIterationCount = iterationResult.fNrOfIterations + ((double) iterationResult.fNrOfIterations / (double) fMaxNrOfIterations);

			// smoothen curvatures and stripings
			if (fCalculateAdvancedColoring) {
				double fraction = MathTools.frac(iterationResult.fNormalisedIterationCount);
				iterationResult.fCurvature = (fraction * iterationResult.fCurvature) + ((1.0 - fraction) * prevCurvature);
				iterationResult.fStriping = (fraction * iterationResult.fStriping) + ((1.0 - fraction) * prevStriping);
			}
		}

		return iterationResult;
	}

	/**
	 * Loads custom fractal parameters from a file.
	 * 
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a read error occurs
	 */
	@Override
	protected void loadCustomParameters(TextFileParser tfp) throws FileParseException
	{
		setRootTolerance(tfp.getNextDouble());
	}

	/**
	 * Saves custom fractal parameters to a file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	@Override
	protected void saveCustomParameters(TextFileWriter tfw) throws FileWriteException
	{
		tfw.writeDouble(fRootTolerance);
		tfw.writeLn();
	}
}
