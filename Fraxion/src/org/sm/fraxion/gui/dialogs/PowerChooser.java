// ---------------------------------
// Filename      : PowerChooser.java
// Author        : Sven Maerivoet
// Last modified : 04/12/2014
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

package org.sm.fraxion.gui.dialogs;

import javax.swing.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.swing.util.*;

/**
 * The <CODE>PowerChooser</CODE> class provides a dialog for selecting a power.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 04/12/2014
 */
public final class PowerChooser extends JDefaultDialog
{
	// internal datastructures
	private JNumberInputField fPowerRealInputField;
	private JNumberInputField fPowerImaginaryInputField;
	private ComplexNumber fPower;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>PowerChooser</CODE> object.
	 *
	 * @param owner  the owning frame
	 * @param power  the initial noise level
	 */
	public PowerChooser(JFrame owner, ComplexNumber power)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {power},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the selected power.
	 *
	 * @return the selected power
	 */
	public ComplexNumber getSelectedPower()
	{
		return (new ComplexNumber(
			fPowerRealInputField.getDoubleValue(),
			fPowerImaginaryInputField.getDoubleValue()));
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
		fPower = (ComplexNumber) parameters[0];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.PowerTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		final int kInputFieldWidth = 10;
		final boolean kAutoCorrect = false;

		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));

		mainPanel.add(new JLabel("x" + " "));
			fPowerRealInputField = new JNumberInputField(fPower.realComponent(),kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
		mainPanel.add(fPowerRealInputField);

		mainPanel.add(Box.createHorizontalStrut(5));

		mainPanel.add(new JLabel("+ y" + " "));
			fPowerImaginaryInputField = new JNumberInputField(fPower.imaginaryComponent(),kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
		mainPanel.add(fPowerImaginaryInputField);
		mainPanel.add(new JLabel("i"));
	}
}
