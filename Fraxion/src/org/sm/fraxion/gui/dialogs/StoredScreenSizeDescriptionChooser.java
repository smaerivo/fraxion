// -------------------------------------------------------
// Filename      : StoredScreenSizeDescriptionChooser.java
// Author        : Sven Maerivoet
// Last modified : 22/01/2015
// Target        : Java VM (1.8)
// -------------------------------------------------------

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

package org.sm.fraxion.gui.dialogs;

import javax.swing.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * The <CODE>StoredScreenSizeDescriptionChooser</CODE> class provides a dialog for selecting a description for a stored screen size.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 22/01/2015
 */
public final class StoredScreenSizeDescriptionChooser extends JDefaultDialog
{
	// specifications of the stored screen size description input field
	private static final int kInputFieldWidth = 30;

	// internal datastructures
	private JTextField fStoredScreenSizeDescriptionInputField;
	private String fStoredScreenSizeDescription;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>StoredScreenSizeDescriptionChooser</CODE> object.
	 *
	 * @param owner                        the owning frame
	 * @param storedScreenSizeDescription  the stored screen size description
	 */
	public StoredScreenSizeDescriptionChooser(JFrame owner, String storedScreenSizeDescription)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {storedScreenSizeDescription},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the selected stored screen size description.
	 *
	 * @return the selected stored screen size description
	 */
	public String getSelectedStoredScreenSizeDescription()
	{
		return fStoredScreenSizeDescription;
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Performs custom initialisation.
	 */
	@Override
	protected void initialiseClass(Object[] parameters)
	{
		fStoredScreenSizeDescription = (String) parameters[0];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Navigation.ScreenBoundsChooser.StoredScreenSize.StoredScreenSizeDescriptionTitle");
	}

	/**
	 */
	@Override
	protected void okSelected()
	{
		fStoredScreenSizeDescription = fStoredScreenSizeDescriptionInputField.getText();
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));

		mainPanel.add(new JLabel(I18NL10N.translate("text.Navigation.ScreenBoundsChooser.StoredScreenSize.StoredScreenSizeDescriptionLabel") + ": "));
			fStoredScreenSizeDescriptionInputField = new JTextField(fStoredScreenSizeDescription,kInputFieldWidth);
		mainPanel.add(fStoredScreenSizeDescriptionInputField);
	}
}
