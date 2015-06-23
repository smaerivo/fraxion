// -------------------------------------------
// Filename      : OrbitTrapRadiusChooser.java
// Author        : Sven Maerivoet
// Last modified : 07/06/2015
// Target        : Java VM (1.8)
// -------------------------------------------

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
import org.sm.smtools.swing.util.*;

/**
 * The <CODE>OrbitTrapRadiusChooser</CODE> class provides a dialog for choosing an orbit trap radius.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 07/06/2015
 */
public final class OrbitTrapRadiusChooser extends JDefaultDialog
{
	// internal datastructures
	private double fRadius;
	private JNumberInputField fRadiusInputField;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>OrbitTrapRadiusChooser</CODE> object.
	 *
	 * @param owner   the owning frame
	 * @param radius  the radius
	 */
	public OrbitTrapRadiusChooser(JFrame owner, double radius)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {radius},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the selected radius.
	 *
	 * @return the selected radius
	 */
	public double getSelectedRadius()
	{
		return fRadiusInputField.getDoubleValue();
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
		fRadius = (double) parameters[0];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.OrbitTrapRadiusChooserTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		final int kInputFieldWidth = 10;
		final boolean kAutoCorrect = false;

		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			JPanel parameterPanel = new JPanel();
			parameterPanel.setLayout(new BoxLayout(parameterPanel,BoxLayout.X_AXIS));
			parameterPanel.add(new JLabel(I18NL10N.translate("text.ColorMap.OrbitTrapRadiusChooserRadius") + " "));
				fRadiusInputField = new JNumberInputField(fRadius,kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
			parameterPanel.add(fRadiusInputField);
		mainPanel.add(parameterPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fRadius = getSelectedRadius();
	}
}
