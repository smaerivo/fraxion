// ------------------------------------------------------------
// Filename      : OrbitTrapTrigonometricParametersChooser.java
// Author        : Sven Maerivoet
// Last modified : 07/06/2015
// Target        : Java VM (1.8)
// ------------------------------------------------------------

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
 * The <CODE>OrbitTrapTrigonometricParametersChooser</CODE> class provides a dialog for choosing orbit trap trigonometric parameters.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 07/06/2015
 */
public final class OrbitTrapTrigonometricParametersChooser extends JDefaultDialog
{
	// internal datastructures
	private double fMultiplicativeFactor;
	private double fAdditiveFactor;
	private JNumberInputField fMultiplicativeFactorInputField;
	private JNumberInputField fAdditiveFactorInputField;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>OrbitTrapTrigonometricParametersChooser</CODE> object.
	 *
	 * @param owner                 the owning frame
	 * @param multiplicativeFactor  the multiplicative factor
	 * @param additiveFactor        the additive factor
	 */
	public OrbitTrapTrigonometricParametersChooser(JFrame owner, double multiplicativeFactor, double additiveFactor)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {multiplicativeFactor,additiveFactor},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the selected multiplicative factor.
	 *
	 * @return the selected multiplicative factor
	 */
	public double getSelectedMultiplicativeFactor()
	{
		return fMultiplicativeFactorInputField.getDoubleValue();
	}

	/**
	 * Returns the selected additive factor.
	 *
	 * @return the selected additive factor
	 */
	public double getSelectedAdditiveFactor()
	{
		return fAdditiveFactorInputField.getDoubleValue();
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
		fMultiplicativeFactor = (double) parameters[0];
		fAdditiveFactor = (double) parameters[1];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.OrbitTrapTrigonometricParametersChooserTitle");
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
			upperParameterPanel.add(new JLabel(I18NL10N.translate("text.ColorMap.OrbitTrapTrigonometricParametersChooserMultiplicativeFactor") + " "));
				fMultiplicativeFactorInputField = new JNumberInputField(fMultiplicativeFactor,kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
			upperParameterPanel.add(fMultiplicativeFactorInputField);
		mainPanel.add(upperParameterPanel);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerParameterPanel = new JPanel();
			lowerParameterPanel.setLayout(new BoxLayout(lowerParameterPanel,BoxLayout.X_AXIS));
			lowerParameterPanel.add(new JLabel(I18NL10N.translate("text.ColorMap.OrbitTrapTrigonometricParametersChooserAdditiveFactor") + " "));
				fAdditiveFactorInputField = new JNumberInputField(fAdditiveFactor,kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
			lowerParameterPanel.add(fAdditiveFactorInputField);
		mainPanel.add(lowerParameterPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fMultiplicativeFactor = getSelectedMultiplicativeFactor();
		fAdditiveFactor = getSelectedAdditiveFactor();
	}
}
