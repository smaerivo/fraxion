// -------------------------------------
// Filename      : StoredScreenSize.java
// Author        : Sven Maerivoet
// Last modified : 22/01/2015
// Target        : Java VM (1.8)
// -------------------------------------

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

package org.sm.fraxion.gui.util;

import java.io.*;

/**
 * The <CODE>StoredScreenSize</CODE> class provides a container for a stored screen size.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 22/01/2015
 */
public final class StoredScreenSize implements Serializable
{
	// public datastructures
	public boolean fSet;
	public String fDescription;
	public int fWidth;
	public int fHeight;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>StoredScreenSize</CODE> object and initialises it.
	 *
	 * @param set          a <CODE>boolean</CODE> indicating whether or not the stored screen size is set
	 * @param description  the description
	 * @param width        the width expressed in pixels
	 * @param height       the height expressed in pixels
	 */
	public StoredScreenSize(boolean set, String description, int width, int height)
	{
		fSet = set;
		fDescription = description;
		fWidth = width;
		fHeight = height;
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * @param out  -
	 * @throws IOException -
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(fSet);
		out.writeObject(fDescription);
		out.writeObject(fWidth);
		out.writeObject(fHeight);
	}

	/**
	 * @param in  -
	 * @throws IOException -
	 * @throws ClassNotFoundException -
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		fSet = (boolean) in.readObject();
		fDescription = (String) in.readObject();
		fWidth = (int) in.readObject();
		fHeight = (int) in.readObject();
	}
}
