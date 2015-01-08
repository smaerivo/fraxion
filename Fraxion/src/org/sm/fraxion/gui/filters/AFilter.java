// ------------------------------
// Filename      : AFilter.java
// Author        : Sven Maerivoet
// Last modified : 21/12/2014
// Target        : Java VM (1.8)
// ------------------------------

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

package org.sm.fraxion.gui.filters;

import java.awt.image.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>AFilter</CODE> class provides the functionality specification for filtering a fractal's image buffer.
 * 
 * @author  Sven Maerivoet
 * @version 21/12/2014
 */
public abstract class AFilter implements Cloneable
{
	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the filter's name.
	 *
	 * @return the filter's name
	 */
	public abstract String getName();

	/**
	 * Applies the filter to a specified image and returns the result.
	 *
	 * @param  image the <CODE>BufferedImage</CODE> to apply the filter to
	 * @return the <CODE>BufferedImage</CODE> that results after applying the filter
	 */
	public abstract BufferedImage filter(BufferedImage image);

	/**
	 * Loads the filter's parameters from a file.
	 * 
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a read error occurs
	 */
	public void loadParameters(TextFileParser tfp) throws FileParseException
	{
	}

	/**
	 * Saves the filter's parameters to a file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	public void saveParameters(TextFileWriter tfw) throws FileWriteException
	{
	}

	/**
	 * Clones (deep copy) the current filter.
	 *
	 * @return a reference to the cloned filter
	 */
	@Override
	public AFilter clone()
	{
		return null;
	}
}
