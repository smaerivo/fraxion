// ---------------------------------
// Filename      : IteratorTask.java
// Author        : Sven Maerivoet
// Last modified : 14/01/2015
// Target        : Java VM (1.8)
// ---------------------------------

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

package org.sm.fraxion.concurrent;

import org.sm.fraxion.fractals.*;
import org.sm.fraxion.fractals.util.*;
import org.sm.smtools.application.concurrent.*;
import org.sm.smtools.math.complex.*;

/**
 * The <CODE>IteratorTask</CODE> class provides a threaded task for partially iterating a fractal.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 14/01/2015
 */
public final class IteratorTask extends ATask
{
	// internal datastructures
	private ScreenLocation fS1;
	private ScreenLocation fS2;
	private AFractalIterator fFractalIterator;
	private IterationBuffer fFractalResultBuffer;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>IteratorTask</CODE> object.
	 *
	 * @param s1  the upper-left screen location
	 * @param s2  the lower-right screen location
	 */
	public IteratorTask(ScreenLocation s1, ScreenLocation s2)
  {
		fS1 = s1;
		fS2 = s2;
		fFractalResultBuffer = new IterationBuffer(fS2.fX - fS1.fX + 1,fS2.fY - fS1.fY + 1);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Installs the <CODE>FractalIterator</CODE> that performs the fractal calculations.
	 *
	 * @param fractalIterator  the <CODE>FractalIterator</CODE> that performs the fractal calculations
	 */
	public void installFractalIterator(AFractalIterator fractalIterator)
	{
		fFractalIterator = fractalIterator;
	}

	/**
	 * Returns the upper-left screen location.
	 *
	 * @return the upper-left screen location
	 */
	public ScreenLocation getS1()
	{
		return fS1;
	}

	/**
	 * Returns the lower-right screen location.
	 *
	 * @return the lower-right screen location
	 */
	public ScreenLocation getS2()
	{
		return fS2;
	}

	/**
	 * Returns the iteration results.
	 *
	 * @return the iteration results in a buffer
	 */
	public IterationBuffer getResult()
	{
		return fFractalResultBuffer;
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Partially calculates the fractal in its own thread.
	 */
	@Override
	protected void executeTask()
	{
		AFractalIterator.EFractalType fractalType = fFractalIterator.getFractalType();
		ComplexNumber dualParameter = fFractalIterator.getDualParameter();

		for (int x = 0; x < fFractalResultBuffer.fWidth; ++x) {
			for (int y = 0; y < fFractalResultBuffer.fHeight; ++y) {
				int index = x + (y * fFractalResultBuffer.fWidth);

				if (fractalType == AFractalIterator.EFractalType.kMainFractal) {
					fFractalResultBuffer.fBuffer[index] = fFractalIterator.iterateMainFractal(new ScreenLocation(x + fS1.fX,y + fS1.fY),false);
				}
				else {
					fFractalResultBuffer.fBuffer[index] = fFractalIterator.iterateDualFractal(new ScreenLocation(x + fS1.fX,y + fS1.fY),dualParameter,false);
				}
			}
		}
	}

	/**
	 * This method is empty.
	 */
	@Override
	protected void finishTask()
	{
	}
}
