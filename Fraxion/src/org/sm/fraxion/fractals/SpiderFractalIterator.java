// ------------------------------------------
// Filename      : SpiderFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 03/11/2014
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

package org.sm.fraxion.fractals;

import org.sm.smtools.math.complex.*;

/**
 * The <CODE>SpiderFractalIterator</CODE> class provides an implementation of the default Spider fractal.
 * 
 * @author  Sven Maerivoet
 * @version 03/11/2014
 */
public class SpiderFractalIterator extends MandelbrotJuliaFractalIterator
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
		return "Spider";
	}

	/**
	 * Returns the default upper-left corner in the complex plane.
	 * 
	 * @return the default upper-left corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP1()
	{
		return (new ComplexNumber(-2.01,-1.51));
	}

	/**
	 * Returns the default lower-right corner in the complex plane.
	 * 
	 * @return the default lower-right corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP2()
	{
		return (new ComplexNumber(+2.01,+1.51));
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Returns that the Spider fractal function is activated.
	 *
	 * @return <CODE>true</CODE>
	 */
	@Override
	protected boolean renderSpider()
	{
		return true;
	}
}
