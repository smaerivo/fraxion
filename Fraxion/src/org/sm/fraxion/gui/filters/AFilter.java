// ------------------------------
// Filename      : AFilter.java
// Author        : Sven Maerivoet
// Last modified : 20/04/2016
// Target        : Java VM (1.8)
// ------------------------------

/**
 * Copyright 2003-2016 Sven Maerivoet
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
import java.io.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>AFilter</CODE> class provides the functionality specification for filtering a fractal's image buffer.
 * 
 * @author  Sven Maerivoet
 * @version 20/04/2016
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
	public void plainTextLoadParameters(TextFileParser tfp) throws FileParseException
	{
	}

	/**
	 * Loads the filter's parameters from a file.
	 * 
	 * @param  dataInputStream  a data inputstream
	 * @throws IOException      in case a parse error occurs
	 */
	public void streamLoadParameters(DataInputStream dataInputStream) throws IOException
	{
	}

	/**
	 * Saves the filter's parameters to a plain-text file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	public void plainTextSaveParameters(TextFileWriter tfw) throws FileWriteException
	{
	}

	/**
	 * Saves the filter's parameters to a file as a stream.
	 * 
	 * @param  dataOutputStream  a data outputstream
	 * @throws IOException       in case a write error occurs
	 */
	public void streamSaveParameters(DataOutputStream dataOutputStream) throws IOException
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
		throw (new AssertionError());
	}
}
