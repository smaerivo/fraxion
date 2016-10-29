// --------------------------------------------------
// Filename      : FastMandelbrotJuliaFractalIterator
// Author        : Sven Maerivoet
// Last modified : 29/10/2016
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

package org.sm.fraxion.fractals.divergent;

import java.util.*;
import org.sm.fraxion.fractals.*;
import org.sm.fraxion.fractals.util.*;
import org.sm.smtools.math.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.math.statistics.*;

/**
 * The <CODE>FastMandelbrotJuliaFractalIterator</CODE> class provides a fast implementation of the default Mandelbrot/Julia fractal.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 * 
 * @author  Sven Maerivoet
 * @version 29/10/2016
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
			double zX = z.realComponent();
			double zY = z.imaginaryComponent();
			double cX = c.realComponent();
			double cY = c.imaginaryComponent();

			for (int i = 0; i < (fNrOfBifurcationPointsToDiscard + fNrOfBifurcationPointsPerOrbit); ++i) {
				// explicitly iterate fractal function
				double zXNext = (zX * zX) - (zY * zY) + cX;
				double zYNext = (2.0 * zX * zY) + cY;
				zX = zXNext;
				zY = zYNext;
				double modulusSqr = (zX * zX) + (zY * zY);

				// discard the first group iterations so the orbit can settle
				if ((i >= fNrOfBifurcationPointsToDiscard) && (modulusSqr <= fEscapeRadiusSqr)) {
					double value = zX;
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

		if (saveOrbit) {
			iterationResult.fComplexOrbit = new ComplexNumber[fMaxNrOfIterations];
			iterationResult.fScreenOrbit = new ScreenLocation[fMaxNrOfIterations];
			for (int iteration = 0; iteration < fMaxNrOfIterations; ++iteration) {
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
		double dzX = 0.0;
		double dzY = 0.0;

		while ((iterationResult.fNrOfIterations < fMaxNrOfIterations) && (modulusSqr <= fEscapeRadiusSqr)) {

			// calculate derivative for exterior distance estimation
			if (fCalculateAdvancedColoring) {
				double temp = 2.0 * ((zX * dzX) - (zY * dzY)) + 1.0;
				dzY = 2.0 * ((zX * dzY) + (zY * dzX));
				dzX = temp;
			}

			// explicitly iterate fractal function
			double zXNext = (zX * zX) - (zY * zY) + cX;
			double zYNext = (2.0 * zX * zY) + cY;
			zXPreviousPrevious = zXPrevious;
			zYPreviousPrevious = zYPrevious;
			zXPrevious = zX;
			zYPrevious = zY;
			zX = zXNext;
			zY = zYNext;
			modulusSqr = (zX * zX) + (zY * zY);

			if (fCalculateAdvancedColoring) {
				// calculate curvatures and stripings
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
				stripingAngles[(int) iterationResult.fNrOfIterations] = Math.atan2(zY,zX);
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

		// estimate curvatures, stripings, Gaussian distances, exterior distances, and orbit trap distances
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
			double dzModulusSqr = (dzX * dzX) + (dzY * dzY);
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
		if ((!fUseFixedNrOfIterations && (iterationResult.fNrOfIterations == fMaxNrOfIterations)) ||
				(fUseFixedNrOfIterations && (modulusSqr <= fEscapeRadiusSqr))) {
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
