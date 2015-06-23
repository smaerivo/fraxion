// ------------------------------------------
// Filename      : APowerFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 16/12/2014
// Target        : Java VM (1.8)
// ------------------------------------------

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

import org.sm.smtools.exceptions.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>APowerFractalIterator</CODE> class provides the base class for power fractals.
 * 
 * @author  Sven Maerivoet
 * @version 16/12/2014
 */
public abstract class APowerFractalIterator extends MandelbrotJuliaFractalIterator
{
	// internal datastructures
	protected ComplexNumber fPower;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Creates a <CODE>APowerFractalIterator</CODE> object and initialises it with the default power.
	 */
	public APowerFractalIterator()
	{
		setPower(getDefaultPower());
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the default escape radius of 2.
	 * 
	 * @return the default escape radius of 2
	 */
	@Override
	public double getDefaultEscapeRadius()
	{
		return 2.0;
	}

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

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Returns the power in the custom filename part.
	 *
	 * @return the power in the custom filename part
	 */
	public String getCustomFilenamePart()
	{
		return (super.getCustomFilenamePart() + "_power=" + String.valueOf(getPower()));
	}

	/**
	 * Returns the default power.
	 *
	 * @return the default power
	 */
	protected abstract ComplexNumber getDefaultPower();

	/**
	 * Loads custom fractal parameters from a file.
	 * 
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a read error occurs
	 */
	@Override
	protected void loadCustomParameters(TextFileParser tfp) throws FileParseException
	{
		setPower(new ComplexNumber(tfp.getNextDouble(),tfp.getNextDouble()));
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
		tfw.writeDouble(fPower.realComponent());
		tfw.writeLn();

		tfw.writeDouble(fPower.imaginaryComponent());
		tfw.writeLn();
	}
}
