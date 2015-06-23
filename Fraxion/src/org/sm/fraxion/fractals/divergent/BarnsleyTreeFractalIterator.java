// ------------------------------------------------
// Filename      : BarnsleyTreeFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 03/11/2014
// Target        : Java VM (1.8)
// ------------------------------------------------

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
 * The <CODE>BarnsleyTreeFractalIterator</CODE> class provides an implementation of the Barnsley tree fractals.
 * <P>
 * This fractal is well suited for Julia sets around <I>c</I> = 0.6 + 1.1i.
 * 
 * @author  Sven Maerivoet
 * @version 03/11/2014
 */
public class BarnsleyTreeFractalIterator extends MandelbrotJuliaFractalIterator
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
		return "Barnsley tree";
	}

	/**
	 * Returns the default dual parameter in the complex plane.
	 * 
	 * @return the default dual parameter in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultDualParameter()
	{
		return (new ComplexNumber(0.6,1.1));
	}

	/**
	 * Returns the default escape radius of 2.0.
	 * 
	 * @return the default escape radius of 2.0
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
	 * Evaluates the Barnsley tree function for a specified complex point.
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the function evaluated with the given parameters
	 */
	@Override
	protected ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber c)
	{
		double sign = +1.0;
		if (z.realComponent() < 0.0) {
			sign = -1.0;
		}

		// Barnsley tree (with an interesting Julia around c = 0.6 + 1.1i)
		return c.multiply(z.subtract(new ComplexNumber(sign)));
	}
}
