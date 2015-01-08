// --------------------------------
// Filename      : FilterChain.java
// Author        : Sven Maerivoet
// Last modified : 22/12/2014
// Target        : Java VM (1.8)
// --------------------------------

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

import java.util.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>FilterChain</CODE> class provides the container for a filter chain.
 * 
 * @author  Sven Maerivoet
 * @version 22/12/2014
 */
public class FilterChain implements Cloneable
{
	// internal datastructures
	private ArrayList<AFilter> fFilterChain;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>FilterChain</CODE> object and resets it.
	 */
	public FilterChain()
	{
		reset();
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the number of filters in the filter chain.
	 *
	 * @return the number of filters in the filter chain
	 */
	public int size()
	{
		return fFilterChain.size();
	}

	/**
	 * Resets the filter chain.
	 */
	public void reset()
	{
		fFilterChain = new ArrayList<AFilter>();
	}

	/**
	 * Adds the specified filter to the filter chain.
	 *
	 * @param filter  the filter to add to the filter chain
	 */
	public void addFilter(AFilter filter)
	{
		fFilterChain.add(filter);
	}

	/**
	 * Returns the n-th filter from the filter chain.
	 * <P>
	 * Filter starts numbering at 0.
	 *
	 * @param n  the index of the filter to return
	 * @return   the n-th filter from the filter chain
	 */
	public AFilter getFilter(int n)
	{
		return fFilterChain.get(n);
	}

	/**
	 * Loads the filter chain from a file.
	 * 
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a read error occurs
	 */
	public void load(TextFileParser tfp) throws FileParseException
	{
		reset();

		int filterChainSize = tfp.getNextInteger();

		for (int i = 0; i < filterChainSize; ++i) {
			String filterName = tfp.getNextString();
			AFilter filter = new IdentityFilter();
			if (filterName.equalsIgnoreCase((new BlurFilter()).getName())) {
				filter = new BlurFilter();
			}
			else if (filterName.equalsIgnoreCase((new EdgeFilter()).getName())) {
				filter = new EdgeFilter();
			}
			else if (filterName.equalsIgnoreCase((new InvertFilter()).getName())) {
				filter = new InvertFilter();
			}
			else if (filterName.equalsIgnoreCase((new PosteriseFilter()).getName())) {
				filter = new PosteriseFilter();
			}
			else if (filterName.equalsIgnoreCase((new SharpenFilter()).getName())) {
				filter = new SharpenFilter();
			}
			filter.loadParameters(tfp);
			addFilter(filter);
		}
	}

	/**
	 * Saves the filter chain to a file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	public void save(TextFileWriter tfw) throws FileWriteException
	{
		tfw.writeInteger(size());
		tfw.writeLn();

		for (AFilter filter : fFilterChain) { 
			tfw.writeString(filter.getName());
			tfw.writeLn();
			filter.saveParameters(tfw);
		}
	}

	/**
	 * Clones (deep copy) the current filter chain.
	 *
	 * @return a reference to the cloned filter chain
	 */
	@Override
	public FilterChain clone()
	{
		FilterChain clonedFilterChain = new FilterChain();

		for (AFilter filter : fFilterChain) {
			clonedFilterChain.addFilter(filter.clone());
		}

		return clonedFilterChain;
	}
}
