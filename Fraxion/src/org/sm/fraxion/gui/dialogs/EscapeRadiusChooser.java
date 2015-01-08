// ----------------------------------------
// Filename      : EscapeRadiusChooser.java
// Author        : Sven Maerivoet
// Last modified : 24/09/2014
// Target        : Java VM (1.8)
// ----------------------------------------

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

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>EscapeRadiusChooser</CODE> class provides a dialog for selecting the escape radius.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 24/09/2014
 */
public final class EscapeRadiusChooser extends JDefaultDialog implements ChangeListener
{
	// the maximum escape radius (expressed in tenths)
	private static final int kMaxEscapeRadius = 100 * 10;

	// internal datastructures
	private int fEscapeRadius;
	private JSlider fEscapeRadiusSlider;
	private JLabel fEscapeRadiusLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>EscapeRadiusChooser</CODE> object.
	 *
	 * @param owner         the owning frame
	 * @param escapeRadius  the initial escape radius
	 */
	public EscapeRadiusChooser(JFrame owner, double escapeRadius)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {escapeRadius},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	// the change-listener
	/**
	 */
	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (!fEscapeRadiusSlider.getValueIsAdjusting()) {
			// extract value
			fEscapeRadius = (int) fEscapeRadiusSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the selected escape radius.
	 *
	 * @return the selected escape radius
	 */
	public double getSelectedEscapeRadius()
	{
		return (fEscapeRadius / 10.0);
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
		fEscapeRadius = (int) Math.round(((double) parameters[0]) * 10.0);
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.EscapeRadiusTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fEscapeRadiusSlider = new JSlider(JSlider.HORIZONTAL);
			fEscapeRadiusSlider.setInverted(false);
			fEscapeRadiusSlider.setMinimum(0);
			fEscapeRadiusSlider.setMaximum(kMaxEscapeRadius);
			fEscapeRadiusSlider.setMinorTickSpacing(kMaxEscapeRadius / 10);
			fEscapeRadiusSlider.setMajorTickSpacing(kMaxEscapeRadius / 5);
			fEscapeRadiusSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
				for (int escapeRadius = 0; escapeRadius <= kMaxEscapeRadius; escapeRadius += (kMaxEscapeRadius / 5)) {
					labelTable.put(escapeRadius,new JLabel(StringTools.convertDoubleToString((double) escapeRadius / 10.0,1)));
				}
			fEscapeRadiusSlider.setLabelTable(labelTable);
			fEscapeRadiusSlider.setPaintLabels(true);
			fEscapeRadiusSlider.setPaintTrack(true);
			fEscapeRadiusSlider.setValue(fEscapeRadius);
			fEscapeRadiusSlider.addChangeListener(this);
		mainPanel.add(fEscapeRadiusSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fEscapeRadiusLabel = new JLabel();
			lowerPanel.add(fEscapeRadiusLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fEscapeRadiusSlider.setValue(fEscapeRadius);
		fEscapeRadiusLabel.setText(I18NL10N.translate("text.Fractal.EscapeRadiusLabel",StringTools.convertDoubleToString(getSelectedEscapeRadius(),1)));
	}
}
