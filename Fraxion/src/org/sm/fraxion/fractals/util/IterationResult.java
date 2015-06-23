// ------------------------------------
// Filename      : IterationResult.java
// Author        : Sven Maerivoet
// Last modified : 23/06/2015
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

import java.io.*;
import org.sm.smtools.math.complex.*;

/**
 * The <CODE>IterationResult</CODE> class provides a container for the results of the iteration of a single point's orbit.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 * 
 * @author  Sven Maerivoet
 * @version 23/06/2015
 */
public final class IterationResult
{
	/**
	 * A constant representing positive infinity.
	 */
	public static final double kInfinity = Double.POSITIVE_INFINITY;

	// the number of fields
	private static final int kNrOfFields = 19;

	/**
	 * The memory size (in bytes) of the object's data.
	 * <P>
	 * Calculated as (object shell) + (#fields) * (size of double)
	 */
	public static final long kMemorySize = 8L + (long) kNrOfFields * 8L;

	// datastructures
	public double fNrOfIterations;
	public double fNormalisedIterationCount;
	public double fExponentialIterationCount;
	public double fRealComponent;
	public double fImaginaryComponent;
	public double fModulus;
	public double fAverageDistance;
	public double fAngle;
	public double fLyapunovExponent;
	public double fCurvature;
	public double fStriping;
	public double fMinimumGaussianIntegersDistance;
	public double fAverageGaussianIntegersDistance;
	public double fExteriorDistance;
	public double fOrbitTrapDiskDistance;
	public double fOrbitTrapCrossStalksDistance;
	public double fOrbitTrapSineDistance;
	public double fOrbitTrapTangensDistance;
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
	 * Loads the iteration data from a file as a stream.
	 * 
	 * @param  dataInputStream  a data inputstream
	 * @throws IOException      in case a parse error occurs
	 */
	public void streamLoad(DataInputStream dataInputStream) throws IOException
	{
		fNrOfIterations = dataInputStream.readDouble();
		fNormalisedIterationCount = dataInputStream.readDouble();
		fExponentialIterationCount = dataInputStream.readDouble();
		fRealComponent = dataInputStream.readDouble();
		fImaginaryComponent = dataInputStream.readDouble();
		fModulus = dataInputStream.readDouble();
		fAverageDistance = dataInputStream.readDouble();
		fAngle = dataInputStream.readDouble();
		fLyapunovExponent = dataInputStream.readDouble();
		fCurvature = dataInputStream.readDouble();
		fStriping = dataInputStream.readDouble();
		fMinimumGaussianIntegersDistance = dataInputStream.readDouble();
		fAverageGaussianIntegersDistance = dataInputStream.readDouble();
		fExteriorDistance = dataInputStream.readDouble();
		fOrbitTrapDiskDistance = dataInputStream.readDouble();
		fOrbitTrapCrossStalksDistance = dataInputStream.readDouble();
		fOrbitTrapSineDistance = dataInputStream.readDouble();
		fOrbitTrapTangensDistance = dataInputStream.readDouble();
		fRootIndex = dataInputStream.readDouble();
	}

	/**
	 * Saves the iteration data to a file as a stream.
	 * 
	 * @param  dataOutputStream  a data outputstream
	 * @throws IOException       in case a write error occurs
	 */
	public void streamSave(DataOutputStream dataOutputStream) throws IOException
	{
		dataOutputStream.writeDouble(fNrOfIterations);
		dataOutputStream.writeDouble(fNormalisedIterationCount);
		dataOutputStream.writeDouble(fExponentialIterationCount);
		dataOutputStream.writeDouble(fRealComponent);
		dataOutputStream.writeDouble(fImaginaryComponent);
		dataOutputStream.writeDouble(fModulus);
		dataOutputStream.writeDouble(fAverageDistance);
		dataOutputStream.writeDouble(fAngle);
		dataOutputStream.writeDouble(fLyapunovExponent);
		dataOutputStream.writeDouble(fCurvature);
		dataOutputStream.writeDouble(fStriping);
		dataOutputStream.writeDouble(fMinimumGaussianIntegersDistance);
		dataOutputStream.writeDouble(fAverageGaussianIntegersDistance);
		dataOutputStream.writeDouble(fExteriorDistance);
		dataOutputStream.writeDouble(fOrbitTrapDiskDistance);
		dataOutputStream.writeDouble(fOrbitTrapCrossStalksDistance);
		dataOutputStream.writeDouble(fOrbitTrapSineDistance);
		dataOutputStream.writeDouble(fOrbitTrapTangensDistance);
		dataOutputStream.writeDouble(fRootIndex);
	}
}
