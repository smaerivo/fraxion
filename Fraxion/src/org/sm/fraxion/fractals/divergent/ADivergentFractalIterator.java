// ----------------------------------------------
// Filename      : ADivergentFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 31/10/2016
// Target        : Java VM (1.8)
// ----------------------------------------------

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

package org.sm.fraxion.fractals.divergent;

import java.util.*;
import org.sm.fraxion.fractals.*;
import org.sm.fraxion.fractals.util.*;
import org.sm.smtools.math.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.math.statistics.*;

/**
 * The <CODE>ADivergentFractalIterator</CODE> class provides a base class for divergent fractals (e.g., Mandelbrot).
 * <P>
 * <B>Note that this is an abstract class.</B>
 * 
 * @author  Sven Maerivoet
 * @version 31/10/2016
 */
public abstract class ADivergentFractalIterator extends AFractalIterator
{
	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Iterates all points in the bifurcation diagram.
	 *
	 * @param width  the width of the diagram in pixels
	 */
	@Override
	public final void iterateBifurcationDiagram(int width)
	{
		fBifurcationPoints = new double[width][fNrOfBifurcationPointsPerOrbit];
		fNrOfBifurcationPoints = new int[width];
		ArrayList<Double> bifurcationDataList = new ArrayList<Double>();

		for (int x = 0; x < width; ++x) {
			ComplexNumber complexFactor = new ComplexNumber((double) x / (((double) width) - 1.0));
			ComplexNumber c = fBifurcationAxisZ1.add(complexFactor.multiply(fBifurcationAxisZ2.subtract(fBifurcationAxisZ1)));

			ComplexNumber z = ComplexNumber.kZero;
			for (int i = 0; i < (fNrOfBifurcationPointsToDiscard + fNrOfBifurcationPointsPerOrbit); ++i) {
				// iterate fractal function
				ComplexNumber zNext = evaluateFractalFunction(z,c);
				z = zNext;

				// discard the first group iterations so the orbit can settle
				if ((i >= fNrOfBifurcationPointsToDiscard) && (z != null) && (z.modulusSquared() <= fEscapeRadiusSqr)) {
					double value = z.modulus();
					if (!Double.isNaN(value)) {
						fBifurcationPoints[x][fNrOfBifurcationPoints[x]] = value;
						bifurcationDataList.add(value);
						++fNrOfBifurcationPoints[x];
					}
				} // if ((i >= kNrOfIterationsToDiscardForBifurcationDiagram) && (modulusSqr <= fEscapeRadiusSqr))
			} // for (int i = 0; i < (kNrOfIterationsToDiscardForBifurcationDiagram + kNrOfBifurcationPoints); ++i)
		} // for (int x = 0; x < width; ++x)

		// convert all bifurcation points into an array and set up an empirical distribution
		Double[] bifurcationDataObject = bifurcationDataList.toArray(new Double[bifurcationDataList.size()]);
		double[] bifurcationData = new double[bifurcationDataList.size()];
		for (int i = 0; i < bifurcationDataObject.length; ++i) {
			bifurcationData[i] = bifurcationDataObject[i];
		}
		EmpiricalDistribution empiricalDistribution = new EmpiricalDistribution(bifurcationData);
		empiricalDistribution.analyse();

		// remove the outliers
		fMinBifurcationValue = empiricalDistribution.getPercentile(fBifurcationOutlierPercentileOffset);
		fMaxBifurcationValue = empiricalDistribution.getPercentile(100.0 - fBifurcationOutlierPercentileOffset);
		double[][] bifurcationPoints = new double[width][fNrOfBifurcationPointsPerOrbit];
		int[] nrOfBifurcationPoints = new int[width];
		for (int x = 0; x < width; ++x) {
			for (int pointIndex = 0; pointIndex < fNrOfBifurcationPoints[x]; ++pointIndex) {
				double value = fBifurcationPoints[x][pointIndex];
				if ((value >= fMinBifurcationValue) && (value <= fMaxBifurcationValue)) {
					bifurcationPoints[x][nrOfBifurcationPoints[x]] = value;
					++nrOfBifurcationPoints[x];
				}
			}
		}
		fBifurcationPoints = bifurcationPoints;
		fNrOfBifurcationPoints = nrOfBifurcationPoints;
	}

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

		if (renderSpider()) {
			z = c;
		}

		double[] curvatures = new double[fMaxNrOfIterations];
		double[] stripingAngles = new double[fMaxNrOfIterations];

		double minimumInteriorGaussianIntegersDistance = Double.MAX_VALUE;
		double minimumExteriorGaussianIntegersDistance = Double.MAX_VALUE;
		double averageInteriorGaussianIntegersDistance = 0.0;
		double averageExteriorGaussianIntegersDistance = 0.0;

		double interiorOrbitTrapDiskCentreX = fInteriorOrbitTrapDiskCentre.realComponent();
		double interiorOrbitTrapDiskCentreY = fInteriorOrbitTrapDiskCentre.imaginaryComponent();
		double exteriorOrbitTrapDiskCentreX = fExteriorOrbitTrapDiskCentre.realComponent();
		double exteriorOrbitTrapDiskCentreY = fExteriorOrbitTrapDiskCentre.imaginaryComponent();
		double interiorOrbitTrapCrossStalksCentreX = fInteriorOrbitTrapCrossStalksCentre.realComponent();
		double interiorOrbitTrapCrossStalksCentreY = fInteriorOrbitTrapCrossStalksCentre.imaginaryComponent();
		double exteriorOrbitTrapCrossStalksCentreX = fExteriorOrbitTrapCrossStalksCentre.realComponent();
		double exteriorOrbitTrapCrossStalksCentreY = fExteriorOrbitTrapCrossStalksCentre.imaginaryComponent();
		double minInteriorOrbitTrapDiskDistance = Double.MAX_VALUE;
		double minExteriorOrbitTrapDiskDistance = Double.MAX_VALUE;
		double minInteriorOrbitTrapCrossStalksDistance = Double.MAX_VALUE;
		double minExteriorOrbitTrapCrossStalksDistance = Double.MAX_VALUE;
		double minInteriorOrbitTrapSineDistance = Double.MAX_VALUE;
		double minExteriorOrbitTrapSineDistance = Double.MAX_VALUE;
		double minInteriorOrbitTrapTangensDistance = Double.MAX_VALUE;
		double minExteriorOrbitTrapTangensDistance = Double.MAX_VALUE;

		// initialise derivative for exterior distance estimation
		ComplexNumber dz = new ComplexNumber();

		while ((!fUseFixedNrOfIterations && (modulusSqr <= fEscapeRadiusSqr) && (iterationResult.fNrOfIterations < fMaxNrOfIterations)) ||
					(fUseFixedNrOfIterations && (iterationResult.fNrOfIterations < fMaxNrOfIterations))) {

			// calculate derivative for exterior distance estimation
			if (fCalculateAdvancedColoring) {
				final double kDerivativeDelta = 1E-7;
				ComplexNumber h = new ComplexNumber(kDerivativeDelta,kDerivativeDelta);
				ComplexNumber zEval = evaluateFractalFunction(z,zPrevious,c);
				dz = (evaluateFractalFunction(z.add(h),zPrevious,c).subtract(zEval)).divide(h);
			}

			// iterate fractal function
			ComplexNumber zNext = evaluateFractalFunction(z,zPrevious,c);
			zPreviousPrevious = zPrevious;
			zPrevious = z;
			z = zNext;
			modulusSqr = z.modulusSquared();

			if (renderSpider()) {
				c = c.divide(ComplexNumber.kTwo).add(z);
			}

			if (fCalculateAdvancedColoring) {
				curvatures[(int) iterationResult.fNrOfIterations] = Math.abs(z.subtract(zPrevious).divide(zPrevious.subtract(zPreviousPrevious)).argument());
				stripingAngles[(int) iterationResult.fNrOfIterations] = z.argument();
			}

			// calculate default results
			++iterationResult.fNrOfIterations;
			double modulus = Math.sqrt(modulusSqr);
			iterationResult.fExponentialIterationCount += Math.exp(-modulus);
			iterationResult.fModulus = modulus;
			iterationResult.fAverageDistance = ((iterationResult.fAverageDistance * (iterationResult.fNrOfIterations - 1)) + modulus) / iterationResult.fNrOfIterations;
			iterationResult.fLyapunovExponent += (0.5 * Math.log(modulusSqr));

			if (fCalculateAdvancedColoring) {
				// calculate Gaussian distances
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

				// calculate orbit trap distances
				double interiorOrbitTrapDiskDistance = Math.abs(((zX - interiorOrbitTrapDiskCentreX) * (zX - interiorOrbitTrapDiskCentreX)) + ((zY - interiorOrbitTrapDiskCentreY) * (zY - interiorOrbitTrapDiskCentreY)) - Math.sqrt(fInteriorOrbitTrapDiskRadius));
				if (interiorOrbitTrapDiskDistance < minInteriorOrbitTrapDiskDistance) {
					minInteriorOrbitTrapDiskDistance = interiorOrbitTrapDiskDistance;
				}
				double exteriorOrbitTrapDiskDistance = Math.abs(((zX - exteriorOrbitTrapDiskCentreX) * (zX - exteriorOrbitTrapDiskCentreX)) + ((zY - exteriorOrbitTrapDiskCentreY) * (zY - exteriorOrbitTrapDiskCentreY)) - Math.sqrt(fExteriorOrbitTrapDiskRadius));
				if (exteriorOrbitTrapDiskDistance < minExteriorOrbitTrapDiskDistance) {
					minExteriorOrbitTrapDiskDistance = exteriorOrbitTrapDiskDistance;
				}

				double interiorOrbitTrapCrossStalksDistance = Math.min(Math.abs(zX - interiorOrbitTrapCrossStalksCentreX),Math.abs(zY - interiorOrbitTrapCrossStalksCentreY));
				if (interiorOrbitTrapCrossStalksDistance < minInteriorOrbitTrapCrossStalksDistance) {
					minInteriorOrbitTrapCrossStalksDistance = interiorOrbitTrapCrossStalksDistance;
				}
				double exteriorOrbitTrapCrossStalksDistance = Math.min(Math.abs(zX - exteriorOrbitTrapCrossStalksCentreX),Math.abs(zY - exteriorOrbitTrapCrossStalksCentreY));
				if (exteriorOrbitTrapCrossStalksDistance < minExteriorOrbitTrapCrossStalksDistance) {
					minExteriorOrbitTrapCrossStalksDistance = exteriorOrbitTrapCrossStalksDistance;
				}

				double interiorOrbitTrapSineDistance = Math.min(Math.abs(zX - Math.sin((zY * fInteriorOrbitTrapSineMultiplicativeFactor) + fInteriorOrbitTrapSineAdditiveFactor)),Math.abs(zY - Math.sin((zX * fInteriorOrbitTrapSineMultiplicativeFactor) + fInteriorOrbitTrapSineAdditiveFactor)));
				if (interiorOrbitTrapSineDistance < minInteriorOrbitTrapSineDistance) {
					minInteriorOrbitTrapSineDistance = interiorOrbitTrapSineDistance;
				}
				double exteriorOrbitTrapSineDistance = Math.min(Math.abs(zX - Math.sin((zY * fExteriorOrbitTrapSineMultiplicativeFactor) + fExteriorOrbitTrapSineAdditiveFactor)),Math.abs(zY - Math.sin((zX * fExteriorOrbitTrapSineMultiplicativeFactor) + fExteriorOrbitTrapSineAdditiveFactor)));
				if (exteriorOrbitTrapSineDistance < minExteriorOrbitTrapSineDistance) {
					minExteriorOrbitTrapSineDistance = exteriorOrbitTrapSineDistance;
				}

				double interiorOrbitTrapTangensDistance = Math.min(Math.abs(zX - Math.tan((zY * fInteriorOrbitTrapTangensMultiplicativeFactor) + fInteriorOrbitTrapTangensAdditiveFactor)),Math.abs(zY - Math.tan((zX * fInteriorOrbitTrapTangensMultiplicativeFactor) + fInteriorOrbitTrapTangensAdditiveFactor)));
				if (interiorOrbitTrapTangensDistance < minInteriorOrbitTrapTangensDistance) {
					minInteriorOrbitTrapTangensDistance = interiorOrbitTrapTangensDistance;
				}
				double exteriorOrbitTrapTangensDistance = Math.min(Math.abs(zX - Math.tan((zY * fExteriorOrbitTrapTangensMultiplicativeFactor) + fExteriorOrbitTrapTangensAdditiveFactor)),Math.abs(zY - Math.tan((zX * fExteriorOrbitTrapTangensMultiplicativeFactor) + fExteriorOrbitTrapTangensAdditiveFactor)));
				if (exteriorOrbitTrapTangensDistance < minExteriorOrbitTrapTangensDistance) {
					minExteriorOrbitTrapTangensDistance = exteriorOrbitTrapTangensDistance;
				}
			} // if (fCalculateAdvancedColoring)

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

		// estimate curvature, striping, Gaussian distance, exterior distance, and orbit trap distances
		double prevCurvature = 0.0;
		double prevStriping = 0.0;
		if (fCalculateAdvancedColoring) {
			iterationResult.fCurvature = 0.0;
			iterationResult.fStriping = 0.0;
			for (int i = 0; i < iterationResult.fNrOfIterations; ++i) {
				iterationResult.fCurvature += curvatures[i];
				double striping = 0.0;
				if (iterationResult.fNrOfIterations == fMaxNrOfIterations) {
					striping = (0.5 * Math.sin(fInteriorStripingDensity * stripingAngles[i]) + 0.5);
					iterationResult.fStriping += striping;
				}
				else {
					striping = (0.5 * Math.sin(fExteriorStripingDensity * stripingAngles[i]) + 0.5);
					iterationResult.fStriping += striping;
				}
				if (i < (iterationResult.fNrOfIterations - 1)) {
					prevCurvature += curvatures[i];
					prevStriping += striping;
				}
			} // for (int i = 0; i < iterationResult.fNrOfIterations; ++i)

			if (iterationResult.fNrOfIterations > 0) {
				iterationResult.fCurvature /= iterationResult.fNrOfIterations;
				iterationResult.fStriping /= iterationResult.fNrOfIterations;
				if ((iterationResult.fNrOfIterations - 1) > 0) {
					prevCurvature /= (iterationResult.fNrOfIterations - 1.0);
					prevStriping /= (iterationResult.fNrOfIterations - 1.0);
				}
			}

			// calculate Gaussian distances
			if (iterationResult.fNrOfIterations == fMaxNrOfIterations) {
				iterationResult.fMinimumGaussianIntegersDistance = minimumInteriorGaussianIntegersDistance;
				iterationResult.fAverageGaussianIntegersDistance = averageInteriorGaussianIntegersDistance;
			}
			else {
				iterationResult.fMinimumGaussianIntegersDistance = minimumExteriorGaussianIntegersDistance;
				iterationResult.fAverageGaussianIntegersDistance = averageExteriorGaussianIntegersDistance;
			}

			// calculate final estimated exterior distances
			double dzModulusSqr = dz.modulusSquared();
			if ((dzModulusSqr != 0.0) && (modulusSqr > 0.0)) {
				iterationResult.fExteriorDistance = Math.sqrt(modulusSqr / dzModulusSqr) * 0.5 * (Math.log(modulusSqr) / Math.log(2.0));
			}

			// calculate final orbit trap distances
			if (iterationResult.fNrOfIterations == fMaxNrOfIterations) {
				iterationResult.fOrbitTrapDiskDistance = minInteriorOrbitTrapDiskDistance;
				iterationResult.fOrbitTrapCrossStalksDistance = minInteriorOrbitTrapCrossStalksDistance;
				iterationResult.fOrbitTrapSineDistance = minInteriorOrbitTrapSineDistance;
				iterationResult.fOrbitTrapTangensDistance = minInteriorOrbitTrapTangensDistance;
			}
			else {
				iterationResult.fOrbitTrapDiskDistance = minExteriorOrbitTrapDiskDistance;
				iterationResult.fOrbitTrapCrossStalksDistance = minExteriorOrbitTrapCrossStalksDistance;
				iterationResult.fOrbitTrapSineDistance = minExteriorOrbitTrapSineDistance;
				iterationResult.fOrbitTrapTangensDistance = minExteriorOrbitTrapTangensDistance;
			}
		} // if (fCalculateAdvancedColoring)

		// adjust for an assumed infinite number of iterations
		if (!fUseFixedNrOfIterations && (iterationResult.fNrOfIterations == fMaxNrOfIterations)) {
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
