// -----------------------------------------------------
// Filename      : ColorMapScalingParametersChooser.java
// Author        : Sven Maerivoet
// Last modified : 04/12/2014
// Target        : Java VM (1.8)
// -----------------------------------------------------

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
 * The <CODE>ColorMapScalingParametersChooser</CODE> class provides a dialog for choosing colour map scaling parameters.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 04/12/2014
 */
public final class ColorMapScalingParametersChooser extends JDefaultDialog
{
	// internal datastructures
	private double fFunctionMultiplier;
	private double fArgumentMultiplier;
	private JNumberInputField fFunctionMultiplierInputField;
	private JNumberInputField fArgumentMultiplierInputField;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>ColorMapScalingParametersChooser</CODE> object.
	 *
	 * @param owner               the owning frame
	 * @param functionMultiplier  the colour map scaling function multiplier
	 * @param argumentMultiplier  the colour map scaling argument multiplier
	 */
	public ColorMapScalingParametersChooser(JFrame owner, double functionMultiplier, double argumentMultiplier)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {functionMultiplier,argumentMultiplier},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the selected colour map scaling function multiplier.
	 *
	 * @return the selected colour map scaling function multiplier
	 */
	public double getSelectedFunctionMultiplier()
	{
		return fFunctionMultiplierInputField.getDoubleValue();
	}

	/**
	 * Returns the selected colour map scaling argument multiplier.
	 *
	 * @return the selected colour map scaling argument multiplier
	 */
	public double getSelectedArgumentMultiplier()
	{
		return fArgumentMultiplierInputField.getDoubleValue();
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
		fFunctionMultiplier = (double) parameters[0];
		fArgumentMultiplier = (double) parameters[1];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.ScalingParametersTitle");
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

			JPanel upperParameterPanel = new JPanel();
			upperParameterPanel.setLayout(new BoxLayout(upperParameterPanel,BoxLayout.X_AXIS));
			upperParameterPanel.add(new JLabel(I18NL10N.translate("text.ColorMap.ColorMapScalingParametersChooserFunctionMultiplier") + " "));
				fFunctionMultiplierInputField = new JNumberInputField(fFunctionMultiplier,kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
			upperParameterPanel.add(fFunctionMultiplierInputField);
		mainPanel.add(upperParameterPanel);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerParameterPanel = new JPanel();
			lowerParameterPanel.setLayout(new BoxLayout(lowerParameterPanel,BoxLayout.X_AXIS));
			lowerParameterPanel.add(new JLabel(I18NL10N.translate("text.ColorMap.ColorMapScalingParametersChooserArgumentMultiplier") + " "));
				fArgumentMultiplierInputField = new JNumberInputField(fArgumentMultiplier,kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
			lowerParameterPanel.add(fArgumentMultiplierInputField);
		mainPanel.add(lowerParameterPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fFunctionMultiplier = getSelectedFunctionMultiplier();
		fArgumentMultiplier = getSelectedArgumentMultiplier();
	}
}
