// -------------------------------------------------
// Filename      : EdgeDetectionStrengthChooser.java
// Author        : Sven Maerivoet
// Last modified : 21/12/2014
// Target        : Java VM (1.8)
// -------------------------------------------------

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
 * The <CODE>EdgeDetectionStrengthChooser</CODE> class provides a dialog for selecting the strength of an edge-detecting filter.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 21/12/2014
 */
public final class EdgeDetectionStrengthChooser extends JDefaultDialog implements ChangeListener
{
	// the maximum strength (expressed in tenths)
	private static final int kMinStrength = 1 * 10;
	private static final int kMaxStrength = 100 * 10;

	// internal datastructures
	private int fStrength;
	private JSlider fStrengthSlider;
	private JLabel fStrengthLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>EdgeDetectionStrengthChooser</CODE> object.
	 *
	 * @param owner     the owning window
	 * @param strength  the initial strength
	 */
	public EdgeDetectionStrengthChooser(JFrame owner, double strength)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {strength},
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
		if (!fStrengthSlider.getValueIsAdjusting()) {
			// extract value
			fStrength = (int) fStrengthSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the selected strength.
	 *
	 * @return the selected strength
	 */
	public double getSelectedStrength()
	{
		return (fStrength / 10.0);
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
		fStrength = (int) Math.round(((double) parameters[0]) * 10.0);
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Filters.Modify.EdgeDetectionStrengthTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fStrengthSlider = new JSlider(JSlider.HORIZONTAL);
			fStrengthSlider.setInverted(false);
			fStrengthSlider.setMinimum(kMinStrength);
			fStrengthSlider.setMaximum(kMaxStrength);
			fStrengthSlider.setMinorTickSpacing(kMaxStrength / 10);
			fStrengthSlider.setMajorTickSpacing(kMaxStrength / 5);
			fStrengthSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
				for (int strength = 0; strength <= kMaxStrength; strength += (kMaxStrength / 5)) {
					if (strength == 0) {
						labelTable.put(kMinStrength,new JLabel(StringTools.convertDoubleToString((double) kMinStrength / 10.0,1)));
					}
					else {
						labelTable.put(strength,new JLabel(StringTools.convertDoubleToString((double) strength / 10.0,1)));
					}
				}
			fStrengthSlider.setLabelTable(labelTable);
			fStrengthSlider.setPaintLabels(true);
			fStrengthSlider.setPaintTrack(true);
			fStrengthSlider.setValue(fStrength);
			fStrengthSlider.addChangeListener(this);
		mainPanel.add(fStrengthSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fStrengthLabel = new JLabel();
			lowerPanel.add(fStrengthLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fStrengthSlider.setValue(fStrength);
		fStrengthLabel.setText(I18NL10N.translate("text.Filters.Modify.EdgeDetectionStrengthLabel",StringTools.convertDoubleToString(getSelectedStrength(),1)));
	}
}
