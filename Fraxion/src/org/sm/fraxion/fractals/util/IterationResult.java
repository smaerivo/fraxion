// ------------------------------------
// Filename      : IterationResult.java
// Author        : Sven Maerivoet
// Last modified : 17/12/2014
// Target        : Java VM (1.8)
// ------------------------------------

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

package org.sm.fraxion.fractals.util;

import org.sm.smtools.exceptions.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>IterationResult</CODE> class provides a container for the results of the iteration of a single point's orbit.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 * 
 * @author  Sven Maerivoet
 * @version 17/12/2014
 */
public final class IterationResult
{
	/**
	 * A constant representing positive infinity.
	 */
	public static final double kInfinity = Double.POSITIVE_INFINITY;

	/**
	 * The memory size (in bytes) of the object's data.
	 * <P>
	 * Calculated as (object shell) + (#fields) * (size of double)
	 */
	public static final long kMemorySize = 8L + 17L * 8L;

	// datastructures
	public double fNrOfIterations;
	public double fNormalisedIterationCount;
	public double fExponentialIterationCount;
	public double fRealComponent;
	public double fImaginaryComponent;
	public double fModulus;
	public double fAngle;
	public double fMaxModulus;
	public double fTotalDistance;
	public double fAverageDistance;
	public double fTotalAngle;
	public double fLyapunovExponent;
	public double fCurvature;
	public double fStriping;
	public double fMinimumGaussianIntegersDistance;
	public double fAverageGaussianIntegersDistance;
	public double fRootIndex;
	public ComplexNumber[] fComplexOrbit;
	public ScreenLocation[] fScreenOrbit;

	/*****************
	 * CONSTRUCTORS  *
	 *****************/

	/**
	 * Constructs an <CODE>IterationResult</CODE> object that represents convergence/divergence of an orbit.
	 */
	public IterationResult()
	{
	}

	/**
	 * Returns whether or not the final point of this iteration sequence lies in the fractal's interior.
	 * 
	 * @return a <CODE>boolean</CODE> indicating whether or not the final point of this iteration sequence lies in the fractal's interior
	 */
	public boolean liesInInterior()
	{
		return (fNrOfIterations == kInfinity);
	}

	/**
	 * Returns the sector of the final value.
	 * <P>
	 * Note that sectors start counting at 1.
	 *
	 * @param nrOfSectors  the number of sectors that are possible
	 * @return             the sector of the final value
	 */
	public int getSector(int nrOfSectors)
	{
		double angle = fAngle;
		if (angle < 0.0) {
			angle += (2.0 * Math.PI);
		}

		int sector = ((int) Math.floor((angle / (2.0 * Math.PI)) * nrOfSectors)) + 1;

		if (sector > nrOfSectors) {
			return nrOfSectors;
		}
		else {
			return sector;
		}
	}

	/**
	 * Loads the iteration data from a CSV file.
	 * 
	 * @param  tfp                 a reference to the file parser
	 * @return                     a <CODE>boolean</CODE> indicating whether or not the read record differs from <CODE>null</CODE>
	 * @throws FileParseException  in case a parse error occurs
	 */
	public boolean load(TextFileParser tfp) throws FileParseException
	{
		String[] csv = tfp.getNextCSV();

		boolean resultAvailable = (csv.length > 1);
		if (resultAvailable) {
			fNrOfIterations = Double.parseDouble(csv[0]);
			fNormalisedIterationCount = Double.parseDouble(csv[1]);
			fExponentialIterationCount = Double.parseDouble(csv[2]);
			fRealComponent = Double.parseDouble(csv[3]);
			fImaginaryComponent = Double.parseDouble(csv[4]);
			fModulus = Double.parseDouble(csv[5]);
			fAngle = Double.parseDouble(csv[6]);
			fMaxModulus = Double.parseDouble(csv[7]);
			fTotalDistance = Double.parseDouble(csv[8]);
			fAverageDistance = Double.parseDouble(csv[9]);
			fTotalAngle = Double.parseDouble(csv[10]);
			fLyapunovExponent = Double.parseDouble(csv[11]);
			fCurvature = Double.parseDouble(csv[12]);
			fStriping = Double.parseDouble(csv[13]);
			fMinimumGaussianIntegersDistance = Double.parseDouble(csv[14]);
			fAverageGaussianIntegersDistance = Double.parseDouble(csv[15]);
			fRootIndex = Double.parseDouble(csv[16]);
		}

		return resultAvailable;
	}

	/**
	 * Saves the iteration data as a CSV record to a file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	public void save(TextFileWriter tfw) throws FileWriteException
	{
		String[] csv = new String[17];

		csv[0] = String.valueOf(fNrOfIterations);
		csv[1] = String.valueOf(fNormalisedIterationCount);
		csv[2] = String.valueOf(fExponentialIterationCount);
		csv[3] = String.valueOf(fRealComponent);
		csv[4] = String.valueOf(fImaginaryComponent);
		csv[5] = String.valueOf(fModulus);
		csv[6] = String.valueOf(fAngle);
		csv[7] = String.valueOf(fMaxModulus);
		csv[8] = String.valueOf(fTotalDistance);
		csv[9] = String.valueOf(fAverageDistance);
		csv[10] = String.valueOf(fTotalAngle);
		csv[11] = String.valueOf(fLyapunovExponent);
		csv[12] = String.valueOf(fCurvature);
		csv[13] = String.valueOf(fStriping);
		csv[14] = String.valueOf(fMinimumGaussianIntegersDistance);
		csv[15] = String.valueOf(fAverageGaussianIntegersDistance);
		csv[16] = String.valueOf(fRootIndex);

		tfw.writeCSV(csv);
		tfw.writeLn();
	}
}
