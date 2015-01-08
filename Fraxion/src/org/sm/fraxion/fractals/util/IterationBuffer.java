// ------------------------------------
// Filename      : IterationBuffer.java
// Author        : Sven Maerivoet
// Last modified : 20/11/2014
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

/**
 * The <CODE>IterationBuffer</CODE> class provides a container for the iteration buffer.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 * 
 * @author  Sven Maerivoet
 * @version 20/11/2014
 */
public final class IterationBuffer
{
	/**
	 * The screen width of the iteration buffer.
	 */
	public int fWidth;

	/**
	 * The screen height of the iteration buffer.
	 */
	public int fHeight;

	/**
	 * The iteration buffer as a linear array.
	 */
	public IterationResult[] fBuffer;

	/*****************
	 * CONSTRUCTORS  *
	 *****************/

	/**
	 * Constructs an <CODE>IterationBuffer</CODE> object that contains an iteration buffer.
	 *
	 * @param width  the screen width of the iteration buffer
	 * @param height the screen height of the iteration buffer
	 */
	public IterationBuffer(int width, int height)
	{
		fWidth = width;
		fHeight = height;
		fBuffer = new IterationResult[width * height];
	}
}
