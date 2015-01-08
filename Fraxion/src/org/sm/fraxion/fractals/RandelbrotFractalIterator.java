// ----------------------------------------------
// Filename      : RandelbrotFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 16/12/2014
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

import org.sm.smtools.exceptions.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>RandelbrotFractalIterator</CODE> class provides an implementation of the Randelbrot fractals.
 * 
 * @author  Sven Maerivoet
 * @version 16/12/2014
 */
public class RandelbrotFractalIterator extends MandelbrotJuliaFractalIterator
{
	// internal datastructures
	private double fNoiseLevel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Creates a <CODE>RandelbrotFractalIterator</CODE> object and initialises it with the default noise level.
	 */
	public RandelbrotFractalIterator()
	{
		setNoiseLevel(getDefaultNoiseLevel());
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
		return "Randelbrot";
	}

	/**
	 * Sets the noise level.
	 * 
	 * @param noiseLevel the noise level
	 */
	public void setNoiseLevel(double noiseLevel)
	{
		fNoiseLevel = noiseLevel;
	}

	/**
	 * Returns the noise level.
	 * 
	 * @return the noise level
	 */
	public double getNoiseLevel()
	{
		return fNoiseLevel;
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Returns the noise level in the custom filename part.
	 *
	 * @return the noise level in the custom filename part
	 */
	public String getCustomFilenamePart()
	{
		return (super.getCustomFilenamePart() + "_noise=" + String.valueOf(getNoiseLevel()));
	}

	/**
	 * Returns the default noise level.
	 *
	 * @return the default noise level
	 */
	protected double getDefaultNoiseLevel()
	{
		return 0.05;
	}

	/**
	 * Evaluates the Randelbrot function for a specified complex point.
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the function evaluated with the given parameters
	 */
	@Override
	protected ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber c)
	{
		ComplexNumber randomNoise = new ComplexNumber(
			-(fNoiseLevel / 2.0) + (Math.random() * fNoiseLevel),
			-(fNoiseLevel / 2.0) + (Math.random() * fNoiseLevel));

		return z.sqr().add(c).add(randomNoise);
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
		setNoiseLevel(tfp.getNextDouble());
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
		tfw.writeDouble(fNoiseLevel);
		tfw.writeLn();
	}
}
