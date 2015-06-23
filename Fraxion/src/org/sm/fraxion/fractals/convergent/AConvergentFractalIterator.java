// -----------------------------------------------
// Filename      : AConvergentFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 23/06/2015
// Target        : Java VM (1.8)
// -----------------------------------------------

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

package org.sm.fraxion.fractals.convergent;

import java.io.*;
import org.sm.fraxion.fractals.*;
import org.sm.fraxion.fractals.util.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.math.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>AConvergentFractalIterator</CODE> class provides a base class for convergent fractals (e.g., Newton-Raphson).
 * <P>
 * <B>Note that this is an abstract class.</B>
 * 
 * @author  Sven Maerivoet
 * @version 23/06/2015
 */
public abstract class AConvergentFractalIterator extends AFractalIterator
{
	/**
	 * The minimum value for alpha.
	 */
	public static final ComplexNumber kMinAlpha = new ComplexNumber(0.1,-0.85);

	/**
	 * The maximum value for alpha.
	 */
	public static final ComplexNumber kMaxAlpha = new ComplexNumber(1.85,0.85);

	// the default number of fixed iterations
	private static final int kDefaultMaxNrOfFixedIterations = 50;

	// fixed exponential smoothing crispness
	private final double kFixedExponentialSmoothingCrispness = 10.0;

	/**
	 * The power of the fractal iterator.
	 */
	protected ComplexNumber fPower;

	// internal datastructures
	private double fDerivativeDelta;
	private double fRootTolerance;
	private ComplexNumber fAlpha;
	private double fMaxObservedExponentialIterationCount;
	private boolean fAutomaticRootDetectionEnabled;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>AConvergentFractalIterator</CODE> object.
	 */
	public AConvergentFractalIterator()
	{
		setMaxNrOfIterations(kDefaultMaxNrOfFixedIterations);
		setUseFixedNrOfIterations(true);
		setPower(getDefaultPower());
		setDerivativeDelta(1E-7);
		setRootTolerance(1E-3);
		setAlpha(new ComplexNumber(1.0));
		setAutomaticRootDetectionEnabled(true);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Sets the power for this fractal.
	 * 
	 * @param power  the power for this fractal
	 */
	public final void setPower(ComplexNumber power)
	{
		fPower = power;
	}

	/**
	 * Returns the power for this fractal.
	 * 
	 * @return the power for this fractal
	 */
	public final ComplexNumber getPower()
	{
		return fPower;
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
	 * Returns the default escape radius of 100.0.
	 * 
	 * @return the default escape radius of 100.0
	 */
	@Override
	public double getDefaultEscapeRadius()
	{
		// use a high iteration radius to trap more complicated roots
		return 100.0;
	}

	/**
	 * Sets the delta used for calculating the derivative.
	 *
	 * @param derivativeDelta  the delta used for calculating the derivative.
	 */
	public final void setDerivativeDelta(double derivativeDelta)
	{
		fDerivativeDelta = derivativeDelta;
	}

	/**
	 * Returns the delta used for calculating the derivative.
	 *
	 * @return the delta used for calculating the derivative.
	 */
	public final double getDerivativeDelta()
	{
		return fDerivativeDelta;
	}

	/**
	 * Sets the tolerance used for finding roots.
	 *
	 * @param rootTolerance  the tolerance used for finding roots
	 */
	public final void setRootTolerance(double rootTolerance)
	{
		fRootTolerance = rootTolerance;
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

	/**
	 * Sets the alpha used for the relaxed Newton/Raphson root finding process.
	 * <P>
	 * The default value is 1.0; good values lie in the interval (0.1 - 0.85i) -&gt; (1.85 + 0.85i).
	 * 
	 * @param alpha  the alpha used for the relaxed Newton/Raphson root finding process
	 */
	public final void setAlpha(ComplexNumber alpha)
	{
		fAlpha = alpha;
	}

	/**
	 * Returns the alpha used for the relaxed Newton/Raphson root finding process.
	 * 
	 * @return the alpha used for the relaxed Newton/Raphson root finding process
	 */
	public final ComplexNumber getAlpha()
	{
		return fAlpha;
	}

	/**
	 * Sets the maximum observed exponential iteration count.
	 *
	 * @param maxObservedExponentialIterationCount  the maximum observed exponential iteration count
	 */
	public final void setMaxObservedExponentialIterationCount(double maxObservedExponentialIterationCount)
	{
		fMaxObservedExponentialIterationCount = maxObservedExponentialIterationCount;
	}

	/**
	 * Returns the maximum observed exponential iteration count.
	 *
	 * @return the maximum observed exponential iteration count
	 */
	public final double getMaxObservedExponentialIterationCount()
	{
		return fMaxObservedExponentialIterationCount;
	}

	/**
	 * Sets whether or not automatic root detection should be enabled.
	 *
	 * @param automaticRootDetectionEnabled  a <CODE>boolean</CODE> indicating whether or not automatic root detection should be enabled
	 */
	public final void setAutomaticRootDetectionEnabled(boolean automaticRootDetectionEnabled)
	{
		fAutomaticRootDetectionEnabled = automaticRootDetectionEnabled;
	}

	/**
	 * Returns whether or not automatic root detection is enabled.
	 *
	 * @return a <CODE>boolean</CODE> indicating whether or not automatic root detection is enabled
	 */
	public final boolean getAutomaticRootDetectionEnabled()
	{
		return fAutomaticRootDetectionEnabled;
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Returns the power in the custom filename part.
	 *
	 * @return the power in the custom filename part
	 */
	@Override
	public String getCustomFilenamePart()
	{
		return (super.getCustomFilenamePart() + "_power=" + String.valueOf(getPower()));
	}

	/**
	 * Returns the default power.
	 *
	 * @return the default power
	 */
	protected ComplexNumber getDefaultPower()
	{
		return new ComplexNumber(1.0);
	}

	/**
	 * Initialises the first iteration.
	 * <P>
	 * This method returns <I>c</I> by default, thereby overriding user-specified initial starting values (making the dual fractal empty).
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the initialised complex variable <I>z</I>
	 */
	protected ComplexNumber initialiseIterations(ComplexNumber z, ComplexNumber c)
	{
		return c;
	}

	/**
	 * Evaluates the orbit of the specified complex variable <I>z</I>.
	 * <P>
	 * By default, this method calculates a complex numerical derivate for an arbitrary function, and then performs relaxed Newton-Raphson root finding.
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the evaluated complex variable <I>z</I>
	 */
	protected ComplexNumber evaluateOrbit(ComplexNumber z, ComplexNumber c)
	{
		ComplexNumber zEval = evaluateFractalFunction(z,c);

		// calculate complex numerical derivative
		ComplexNumber h = new ComplexNumber(fDerivativeDelta,fDerivativeDelta);
		ComplexNumber dz = (evaluateFractalFunction(z.add(h),c).subtract(zEval)).divide(h);

		// perform relaxed Newton-Raphson root finding iteration
		return z.subtract(fAlpha.multiply(evaluateFractalFunction(z,c).divide(dz)));
	}

	/**
	 * Helper method for a general iteration with the Newton-Raphson root finding algorithm.
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

		z = initialiseIterations(z,c);
		double modulusSqr = z.modulusSquared();
		ComplexNumber zPrevious = z;
		ComplexNumber zPreviousPrevious = zPrevious;

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

		boolean convergedOnRoot = false;
		double rootDistance = 0.0;
		while ((iterationResult.fNrOfIterations < fMaxNrOfIterations) && (!convergedOnRoot)) {

			// calculate derivative for exterior distance estimation
			if (fCalculateAdvancedColoring) {
				ComplexNumber h = new ComplexNumber(fDerivativeDelta,fDerivativeDelta);
				ComplexNumber zEval = evaluateOrbit(z,c);
				dz = (evaluateOrbit(z.add(h),c).subtract(zEval)).divide(h);
			}

			// iterate fractal function
			ComplexNumber zNext = evaluateOrbit(z,c);
			zPreviousPrevious = zPrevious;
			zPrevious = z;
			z = zNext;
			modulusSqr = z.modulusSquared();

			if (fCalculateAdvancedColoring) {
				curvatures[(int) iterationResult.fNrOfIterations] = Math.abs(z.subtract(zPrevious).divide(zPrevious.subtract(zPreviousPrevious)).argument());
				stripingAngles[(int) iterationResult.fNrOfIterations] = z.argument();
			}

			// have we converged sufficiently close to a root?
			rootDistance = z.subtract(zPrevious).modulus();
			if ((rootDistance < fRootTolerance)  && (z.modulus() < fEscapeRadius)) {
      	convergedOnRoot = true;
      }
      else {
  			// calculate default results and continue the root finding process
		    ++iterationResult.fNrOfIterations;
      	iterationResult.fExponentialIterationCount += Math.exp(-z.modulus() / kFixedExponentialSmoothingCrispness / z.subtract(zPrevious).modulus());
      	double modulus = z.modulus();
				iterationResult.fAverageDistance = ((iterationResult.fAverageDistance * (iterationResult.fNrOfIterations - 1)) + modulus) / iterationResult.fNrOfIterations;				
				iterationResult.fLyapunovExponent += Math.log(modulus);

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
      }
		} // while ()

		// adjust for an assumed infinite number of iterations
		if (iterationResult.fNrOfIterations == fMaxNrOfIterations) {
			iterationResult.fNrOfIterations = IterationResult.kInfinity;
		}
		else if (convergedOnRoot) {
			iterationResult.fNormalisedIterationCount = iterationResult.fNrOfIterations + (rootDistance / fRootTolerance);
			iterationResult.fRealComponent = z.realComponent();
			iterationResult.fImaginaryComponent = z.imaginaryComponent();
			iterationResult.fModulus = z.modulus();
			iterationResult.fAngle = z.argument();
			iterationResult.fLyapunovExponent /= iterationResult.fNrOfIterations;
			iterationResult.fRootIndex = 1;

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

				// smoothen curvatures and stripings
				double fraction = MathTools.frac(iterationResult.fNormalisedIterationCount);
				iterationResult.fCurvature = (fraction * iterationResult.fCurvature) + ((1.0 - fraction) * prevCurvature);
				iterationResult.fStriping = (fraction * iterationResult.fStriping) + ((1.0 - fraction) * prevStriping);

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
		} // if (convergedOnRoot)

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
		setPower(new ComplexNumber(tfp.getNextDouble(),tfp.getNextDouble()));
		setDerivativeDelta(tfp.getNextDouble());
		setRootTolerance(tfp.getNextDouble());
		setAlpha(new ComplexNumber(tfp.getNextDouble(),tfp.getNextDouble()));
		setMaxObservedExponentialIterationCount(tfp.getNextDouble());
		setAutomaticRootDetectionEnabled(tfp.getNextBoolean());
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
		setPower(new ComplexNumber(dataInputStream.readDouble(),dataInputStream.readDouble()));
		setDerivativeDelta(dataInputStream.readDouble());
		setRootTolerance(dataInputStream.readDouble());
		setAlpha(new ComplexNumber(dataInputStream.readDouble(),dataInputStream.readDouble()));
		setMaxObservedExponentialIterationCount(dataInputStream.readDouble());
		setAutomaticRootDetectionEnabled(dataInputStream.readBoolean());
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
		tfw.writeDouble(fPower.realComponent());
		tfw.writeLn();

		tfw.writeDouble(fPower.imaginaryComponent());
		tfw.writeLn();

		tfw.writeDouble(fDerivativeDelta);
		tfw.writeLn();

		tfw.writeDouble(fRootTolerance);
		tfw.writeLn();

		tfw.writeDouble(fAlpha.realComponent());
		tfw.writeLn();

		tfw.writeDouble(fAlpha.imaginaryComponent());
		tfw.writeLn();

		tfw.writeDouble(fMaxObservedExponentialIterationCount);
		tfw.writeLn();

		tfw.writeBoolean(fAutomaticRootDetectionEnabled);
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
		dataOutputStream.writeDouble(fPower.realComponent());
		dataOutputStream.writeDouble(fPower.imaginaryComponent());
		dataOutputStream.writeDouble(fDerivativeDelta);
		dataOutputStream.writeDouble(fRootTolerance);
		dataOutputStream.writeDouble(fAlpha.realComponent());
		dataOutputStream.writeDouble(fAlpha.imaginaryComponent());
		dataOutputStream.writeDouble(fMaxObservedExponentialIterationCount);
		dataOutputStream.writeBoolean(fAutomaticRootDetectionEnabled);
	}
}
