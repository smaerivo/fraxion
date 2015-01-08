// ---------------------------------------------------
// Filename      : ColorMapContinuousRangeChooser.java
// Author        : Sven Maerivoet
// Last modified : 24/09/2014
// Target        : Java VM (1.8)
// ---------------------------------------------------

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
import javax.swing.event.*;
import java.util.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>ColorMapContinuousRangeChooser</CODE> class provides a dialog for selecting a continuous colour map range.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 24/09/2014
 */
public final class ColorMapContinuousRangeChooser extends JDefaultDialog implements ChangeListener
{
	// the minimum and maximum colour repetitions (expressed in tenths)
	private static final int kMinRange = 1 * 10;
	private static final int kMaxRange = 100 * 10;

	// internal datastructures
	private int fRange;
	private JLabel fRangeLabel;
	private JSlider fRangeSlider;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>ColorMapContinuousRangeChooser</CODE> object.
	 *
	 * @param owner  the owning frame
	 * @param range  the initial range
	 */
	public ColorMapContinuousRangeChooser(JFrame owner, double range)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {range},
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
		if (e.getSource() == fRangeSlider) {
			if (!fRangeSlider.getValueIsAdjusting()) {
				// extract value
				fRange = (int) fRangeSlider.getValue();
				updateGUI();
			}
		}
	}

	/**
	 * Returns the selected range.
	 *
	 * @return the selected range
	 */
	public double getSelectedRange()
	{
		return (fRange / 10.0);
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
		fRange = (int) Math.round(((double) parameters[0]) * 10.0);
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.ContinuousRangeSelectionTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fRangeSlider = new JSlider(JSlider.HORIZONTAL);
			fRangeSlider.setInverted(false);
			fRangeSlider.setMinimum(kMinRange);
			fRangeSlider.setMaximum(kMaxRange);
			fRangeSlider.setMinorTickSpacing(kMaxRange / 10);
			fRangeSlider.setMajorTickSpacing(kMaxRange / 5);
			fRangeSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> colorRepetitionSliderLabelTable = new Hashtable<Integer,JLabel>();
				for (int range = 0; range <= kMaxRange; range += (kMaxRange / 5)) {
					colorRepetitionSliderLabelTable.put(range,new JLabel(StringTools.convertDoubleToString((double) range / 10.0,1)));
				}
			fRangeSlider.setLabelTable(colorRepetitionSliderLabelTable);
			fRangeSlider.setPaintLabels(true);
			fRangeSlider.setPaintTrack(true);
			fRangeSlider.setValue(fRange);
			fRangeSlider.addChangeListener(this);
		mainPanel.add(fRangeSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel rangeLabelPanel = new JPanel();
			rangeLabelPanel.setLayout(new BoxLayout(rangeLabelPanel,BoxLayout.X_AXIS));
				fRangeLabel = new JLabel();
			rangeLabelPanel.add(fRangeLabel);
			rangeLabelPanel.add(Box.createHorizontalGlue());
		mainPanel.add(rangeLabelPanel);

		mainPanel.add(Box.createVerticalStrut(10));
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fRangeSlider.setValue(fRange);
		fRangeLabel.setText(I18NL10N.translate("text.ColorMap.RangeLabel",StringTools.convertDoubleToString(getSelectedRange(),1)));
	}
}
