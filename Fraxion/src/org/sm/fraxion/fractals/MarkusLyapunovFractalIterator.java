// --------------------------------------------------
// Filename      : MarkusLyapunovFractalIterator.java
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

import java.util.*;
import org.sm.fraxion.fractals.util.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>MarkusLyapunovFractalIterator</CODE> class provides a base class for Markus-Lyapunov fractals.
 * 
 * @author  Sven Maerivoet
 * @version 22/01/2015
 */
public class MarkusLyapunovFractalIterator extends AFractalIterator
{
	// iteration-specific constants
	private static final double kWarmUpFraction = 0.25;
	private static final double kX0 = 0.5;
	private static final double kLog2 = Math.log(2.0);
	private static final double kMinX = 0.0;
	private static final double kMaxX = 4.0;
	private static final double kMinY = 0.0;
	private static final double kMaxY = 4.0;

	// internal datastructures
	private String fRootSequence;
	private ArrayList<Integer> fRootSequenceIDs;
	private int fRootSequenceLength;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>MarkusLyapunovFractalIterator</CODE> object and initialises it with the default 'AB' sequence.
	 */
	public MarkusLyapunovFractalIterator()
	{
		setRootSequence("AB");
	}

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
		return "Markus / Lyapunov";
	}

	/**
	 * Returns the default upper-left corner in the complex plane.
	 * 
	 * @return the default upper-left corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP1()
	{
		return (new ComplexNumber(kMinY,kMinY));
	}

	/**
	 * Returns the default lower-right corner in the complex plane.
	 * 
	 * @return the default lower-right corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP2()
	{
		return (new ComplexNumber(kMaxX,kMaxY));
	}

	/**
	 * Unused method.
	 * 
	 * @return the default parameter of 0 + 0i
	 */
	@Override
	public final ComplexNumber getDefaultDualParameter()
	{
		// the default dual parameter is 0 + 0i
		return (new ComplexNumber());
	}

	/**
	 * Unused method.
	 * 
	 * @return the default escape radius of 100
	 */
	@Override
	public final double getDefaultEscapeRadius()
	{
		// a high escape radius gives smoother colouring results
		return 100.0;
	}

	/**
	 * Sets the root sequence to be used (a <CODE>String</CODE> of A's and B's.
	 *
	 * @param rootSequence  the root sequence to be used
	 */
	public final void setRootSequence(String rootSequence)
	{
		fRootSequence = rootSequence.trim();
		fRootSequenceIDs = new ArrayList<Integer>();

		for (int i = 0; i < fRootSequence.length(); ++i) {
			String character = fRootSequence.substring(i,i + 1);
			if (character.equalsIgnoreCase("A")) {
				fRootSequenceIDs.add(0);
			}
			else if (character.equalsIgnoreCase("B")) {
				fRootSequenceIDs.add(1);
			}
		}
		fRootSequenceLength = fRootSequenceIDs.size();
	}

	/**
	 * Returns the root sequence that is used.
	 * 
	 * @return the root sequence that is used
	 */
	public final String getRootSequence()
	{
		return fRootSequence;
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Returns the root sequence in the custom filename part.
	 *
	 * @return the root sequence in the custom filename part
	 */
	public String getCustomFilenamePart()
	{
		return (super.getCustomFilenamePart() + "_rootseq=" + getRootSequence());
	}

	/**
	 * Helper method for a general iteration with the Markus-Lyapunov estimation algorithm.
	 *
	 * @param z          the complex variable <I>z</I>
	 * @param c          the complex parameter <I>c</I>
	 * @param saveOrbit  a <CODE>boolean</CODE> indicating whether or not the orbit should be saved
	 * @return           the iteration result associated with the forward orbit of the complex variable starting point due to the specified complex number
	 */
	@Override
	protected final IterationResult iterate(ComplexNumber z, ComplexNumber c, boolean saveOrbit)
	{
		double a = c.realComponent();
		double b = c.imaginaryComponent();

		// limit the valid parameter range in the complex plane
		if ((a < kMinX) || (a > kMaxX) || (b < kMinY) || (b > kMaxY)) {
			return null;
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

		// use a fixed number of iterations
		double x = kX0;
		int nrOfWarmUpIterations = (int) Math.round((double) fMaxNrOfIterations / kWarmUpFraction);
		for (int iteration = 0; iteration < (nrOfWarmUpIterations + fMaxNrOfIterations); ++iteration) {

			// determine sequence
			double r = a;
			if (fRootSequenceIDs.get(iteration % fRootSequenceLength) == 1) {
				r = b;
			}

			// iterate the logistic function
			x = r * x * (1.0 - x);

			// calculate the Lyapunov exponent after initialisation of the sequence
			if (iteration >= nrOfWarmUpIterations) {
				iterationResult.fLyapunovExponent += Math.log(Math.abs(r * (1 - (2.0 * x)))) / kLog2;
				iterationResult.fAverageDistance = ((iterationResult.fAverageDistance * (iterationResult.fNrOfIterations - 1)) + x) / iterationResult.fNrOfIterations;				

				if (saveOrbit) {
					int iterationArrayPos = iteration - nrOfWarmUpIterations;
					// make the Y-axis show the orbits
					iterationResult.fComplexOrbit[iterationArrayPos] = new ComplexNumber(0.0,x);
					iterationResult.fScreenOrbit[iterationArrayPos] = convertComplexNumberToScreenLocation(iterationResult.fComplexOrbit[iterationArrayPos]);
				}
			}
		}

		iterationResult.fLyapunovExponent /= (double) fMaxNrOfIterations;

		iterationResult.fModulus = x;
		iterationResult.fRealComponent = x;

		// mark chaotic points
		if (iterationResult.fLyapunovExponent > 0) {
			iterationResult.fNrOfIterations = IterationResult.kInfinity;
			iterationResult.fNormalisedIterationCount = IterationResult.kInfinity;
		}
		else {
			iterationResult.fNrOfIterations = fMaxNrOfIterations;
			iterationResult.fNormalisedIterationCount = fMaxNrOfIterations;
		}

		return iterationResult;
	}

	/**
	 * Unused method.
	 *
	 * @param z  -
	 * @param c  -
	 * @return   <CODE>null</CODE>
	 */
	@Override
	protected final ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber c)
	{
		return null;
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
		setRootSequence(tfp.getNextString());
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
		tfw.writeString(fRootSequence);
		tfw.writeLn();
	}
}
