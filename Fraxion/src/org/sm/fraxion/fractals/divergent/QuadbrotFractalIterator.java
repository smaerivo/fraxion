// --------------------------------------------
// Filename      : QuadbrotFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 07/06/2015
// Target        : Java VM (1.8)
// --------------------------------------------

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

import org.sm.smtools.math.complex.*;

/**
 * The <CODE>QuadbrotFractalIterator</CODE> class provides an implementation of a Quadbrot fractal (4 connected Mandelbrot sets).
 * 
 * @author  Sven Maerivoet
 * @version 07/06/2015
 */
public class QuadbrotFractalIterator extends MandelbrotJuliaFractalIterator
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
		return "Quadbrot";
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Evaluates the Quadbrot function for a specified complex point.
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the function evaluated with the given parameters
	 */
	@Override
	protected ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber c)
	{
		ComplexNumber kOneOverC = ComplexNumber.kOne.divide(c);
		ComplexNumber kSqrt2 = ComplexNumber.kTwo.sqrt();
		ComplexNumber kIMin = ComplexNumber.kI.multiply(c.subtract(kOneOverC)).divide(kSqrt2);
		ComplexNumber kIPlus = ComplexNumber.kI.multiply(c.add(kOneOverC)).divide(kSqrt2);

		return z.sqr().multiply(kIMin).add(kIPlus);
	}
}
