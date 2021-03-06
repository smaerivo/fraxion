// -------------------------------------------
// Filename      : AMagnetFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 23/06/2015
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

package org.sm.fraxion.fractals.magnet;

import java.io.*;
import org.sm.fraxion.fractals.*;
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
 * @version 23/06/2015
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

		while ((modulusSqr < fEscapeRadiusSqr) &&
					(iterationResult.fNrOfIterations < fMaxNrOfIterations) &&
					(zOffset.modulusSquared() > fRootToleranceSquared)) {

			// calculate derivative for exterior distance estimation
			if (fCalculateAdvancedColoring) {
				final double kDerivativeDelta = 1E-7;
				ComplexNumber h = new ComplexNumber(kDerivativeDelta,kDerivativeDelta);
				ComplexNumber zEval = evaluateFractalFunction(z,c);
				dz = (evaluateFractalFunction(z.add(h),c).subtract(zEval)).divide(h);
			}

			// iterate fractal function
			ComplexNumber zNext = evaluateFractalFunction(z,c);
			zPreviousPrevious = zPrevious;
			zPrevious = z;
			z = zNext;

			if (fCalculateAdvancedColoring) {
				curvatures[(int) iterationResult.fNrOfIterations] = Math.abs(z.subtract(zPrevious).divide(zPrevious.subtract(zPreviousPrevious)).argument());
				stripingAngles[(int) iterationResult.fNrOfIterations] = z.argument();
			}

			// calculate default results
			modulusSqr = z.modulusSquared();
			++iterationResult.fNrOfIterations;
			double modulus = Math.sqrt(modulusSqr);
			iterationResult.fExponentialIterationCount += Math.exp(-modulus);
			iterationResult.fModulus = modulus;
			iterationResult.fAverageDistance = ((iterationResult.fAverageDistance * (iterationResult.fNrOfIterations - 1)) + modulus) / iterationResult.fNrOfIterations;				
			iterationResult.fLyapunovExponent += (0.5 * Math.log(modulusSqr));

			zOffset = new ComplexNumber(z.realComponent() - 1.0,z.imaginaryComponent());

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
	 * Loads custom fractal parameters from a plain-text file.
	 * 
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a read error occurs
	 */
	@Override
	protected void plainTextLoadCustomParameters(TextFileParser tfp) throws FileParseException
	{
		setRootTolerance(tfp.getNextDouble());
	}

	/**
	 * Loads custom fractal parameters from a file as a stream.
	 * 
	 * @param  dataInputStream  a data inputstream
	 * @throws IOException      in case a parse error occurs
	 */
	@Override
	protected void streamLoadCustomParameters(DataInputStream dataInputStream) throws IOException
	{
		setRootTolerance(dataInputStream.readDouble());
	}

	/**
	 * Saves custom fractal parameters to a plain-text file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	@Override
	protected void plainTextSaveCustomParameters(TextFileWriter tfw) throws FileWriteException
	{
		tfw.writeDouble(fRootTolerance);
		tfw.writeLn();
	}

	/**
	 * Saves custom fractal parameters to a file as a stream.
	 * 
	 * @param  dataOutputStream  a data outputstream
	 * @throws IOException       in case a write error occurs
	 */
	protected void streamSaveCustomParameters(DataOutputStream dataOutputStream) throws IOException
	{
		dataOutputStream.writeDouble(fRootTolerance);
	}
}
