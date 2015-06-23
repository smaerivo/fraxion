// -----------------------------------------
// Filename      : GlynnFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 16/11/2014
// Target        : Java VM (1.8)
// -----------------------------------------

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
 * The <CODE>GlynnFractalIterator</CODE> class provides an implementation of the Glynn fractals.
 * <P>
 * The resulting Julia sets are made with powers <I>n</I> between 1 and 2, and <I>c</I> values close outside the western boundary.
 * <P>
 * Interesting values for the Julia <I>c</I> parameter are -0.2 + 0i for <I>n</I> equal to 1.5, and
 * -0.375 + 0i, -0.338 + 0i, 0.22 + 0i for <I>n</I> equal to 1.75.
 * 
 * @author  Sven Maerivoet
 * @version 16/11/2014
 */
public class GlynnFractalIterator extends MandelbrotJuliaFractalIterator
{
	/**
	 * The default power.
	 */
	public static final ComplexNumber kDefaultPower = new ComplexNumber(1.5);

	// internal datastructures
	private ComplexNumber fPower;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Creates a <CODE>GlynnFractalIterator</CODE> object and initialises it with power 1.5.
	 */
	public GlynnFractalIterator()
	{
		setPower(kDefaultPower);
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
		return "Glynn";
	}

	/**
	 * Returns the default dual parameter in the complex plane.
	 * 
	 * @return the default dual parameter in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultDualParameter()
	{
		return (new ComplexNumber(-0.2));
	}

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
	public void setPower(ComplexNumber power)
	{
		fPower = power;
	}

	/**
	 * Returns the power for this fractal.
	 * 
	 * @return the power for this fractal
	 */
	public ComplexNumber getPower()
	{
		return fPower;
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Saves custom fractal parameters to a file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	public void saveCustom(TextFileWriter tfw) throws FileWriteException
	{
		tfw.writeDouble(fPower.realComponent());
		tfw.writeLn();

		tfw.writeDouble(fPower.imaginaryComponent());
		tfw.writeLn();
	}

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
	 * Evaluates the Glynn function for a specified complex point.
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the function evaluated with the given parameters
	 */
	@Override
	protected ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber c)
	{
		return z.pow(fPower).add(c);
	}
}
