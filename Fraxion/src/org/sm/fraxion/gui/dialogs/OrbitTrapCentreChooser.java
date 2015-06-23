// -------------------------------------------
// Filename      : OrbitTrapCentreChooser.java
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
import org.sm.smtools.math.complex.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.swing.util.*;

/**
 * The <CODE>OrbitTrapCentreChooser</CODE> class provides a dialog for choosing an orbit trap centre.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 07/06/2015
 */
public final class OrbitTrapCentreChooser extends JDefaultDialog
{
	// internal datastructures
	private double fX;
	private double fY;
	private JNumberInputField fXInputField;
	private JNumberInputField fYInputField;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>OrbitTrapCentreChooser</CODE> object.
	 *
	 * @param owner   the owning frame
	 * @param centre  the centre
	 */
	public OrbitTrapCentreChooser(JFrame owner, ComplexNumber centre)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {centre.realComponent(),centre.imaginaryComponent()},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the selected centre.
	 *
	 * @return the selected centre
	 */
	public ComplexNumber getSelectedCentre()
	{
		return (new ComplexNumber(fXInputField.getDoubleValue(),fYInputField.getDoubleValue()));
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
		fX = (double) parameters[0];
		fY = (double) parameters[1];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.OrbitTrapCentreChooserTitle");
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
			upperParameterPanel.add(new JLabel(I18NL10N.translate("text.ColorMap.OrbitTrapCentreChooserX") + " "));
				fXInputField = new JNumberInputField(fX,kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
			upperParameterPanel.add(fXInputField);
		mainPanel.add(upperParameterPanel);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerParameterPanel = new JPanel();
			lowerParameterPanel.setLayout(new BoxLayout(lowerParameterPanel,BoxLayout.X_AXIS));
			lowerParameterPanel.add(new JLabel(I18NL10N.translate("text.ColorMap.OrbitTrapCentreChooserY") + " "));
				fYInputField = new JNumberInputField(fY,kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
			lowerParameterPanel.add(fYInputField);
		mainPanel.add(lowerParameterPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		ComplexNumber centre = getSelectedCentre();
		fX = centre.realComponent();
		fY = centre.imaginaryComponent();
	}
}
