// -----------------------------------------
// Filename      : DucksFractalIterator.java
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

package org.sm.fraxion.fractals;

import org.sm.smtools.math.complex.*;

/**
 * The <CODE>DucksFractalIterator</CODE> class provides an implementation of the Ducks fractal.
 * <P>
 * This fractal is specifically designed for Julia sets coloured using the total average distance with the Bone colourmap.
 * 
 * @author  Sven Maerivoet
 * @version 16/11/2014
 */
public class DucksFractalIterator extends MandelbrotJuliaFractalIterator
{
	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Creates a <CODE>DucksFractalIterator</CODE> object.
	 */
	public DucksFractalIterator()
	{
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
		return "Ducks";
	}

	/**
	 * Returns the default upper-left corner in the complex plane.
	 * 
	 * @return the default upper-left corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP1()
	{
		return (new ComplexNumber(-3.01,-4.01));
	}

	/**
	 * Returns the default lower-right corner in the complex plane.
	 * 
	 * @return the default lower-right corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP2()
	{
		return (new ComplexNumber(+2.01,+1.01));
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

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Returns the default of 50 fixed iterations to be used.
	 * 
	 * @return the default of 50 fixed iterations to be used
	 */
	@Override
	protected int getDefaultFixedNrOfIterations()
	{
		return 50;
	}

	/**
	 * Evaluates the Ducky function for a specified complex point.
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the function evaluated with the given parameters
	 */
	@Override
	protected ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber c)
	{
		if (z.imaginaryComponent() < 0.0) { 
			z = z.conjugate();
		}

		return (z.add(c)).ln();
	}
}
