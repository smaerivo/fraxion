// --------------------------------------------
// Filename      : BrightnessFactorChooser.java
// Author        : Sven Maerivoet
// Last modified : 12/10/2014
// Target        : Java VM (1.8)
// --------------------------------------------

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
 * The <CODE>BrightnessFactorChooser</CODE> class provides a dialog for selecting a brightness when smooth colouring roots.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 12/10/2014
 */
public final class BrightnessFactorChooser extends JDefaultDialog implements ChangeListener
{
	// the maximum brightness factor (expressed in tenths)
	private static final int kMinBrightnessFactor = 1 * 10;
	private static final int kMaxBrightnessFactor = 100 * 10;

	// internal datastructures
	private int fBrightnessFactor;
	private JSlider fBrightnessFactorSlider;
	private JLabel fBrightnessFactorLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>BrightnessFactorChooser</CODE> object.
	 *
	 * @param owner             the owning frame
	 * @param brightnessFactor  the initial brightness factor
	 */
	public BrightnessFactorChooser(JFrame owner, double brightnessFactor)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {brightnessFactor},
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
		if (!fBrightnessFactorSlider.getValueIsAdjusting()) {
			// extract value
			fBrightnessFactor = (int) fBrightnessFactorSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the selected escape radius.
	 *
	 * @return the selected escape radius
	 */
	public double getSelectedBrightnessFactor()
	{
		return (fBrightnessFactor / 10.0);
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
		fBrightnessFactor = (int) Math.round(((double) parameters[0]) * 10.0);
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.BrightnessFactorTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fBrightnessFactorSlider = new JSlider(JSlider.HORIZONTAL);
			fBrightnessFactorSlider.setInverted(false);
			fBrightnessFactorSlider.setMinimum(kMinBrightnessFactor);
			fBrightnessFactorSlider.setMaximum(kMaxBrightnessFactor);
			fBrightnessFactorSlider.setMinorTickSpacing((kMaxBrightnessFactor - kMinBrightnessFactor) / 10);
			fBrightnessFactorSlider.setMajorTickSpacing((kMaxBrightnessFactor - kMinBrightnessFactor) / 5);
			fBrightnessFactorSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
				for (int BrightnessFactor = kMinBrightnessFactor; BrightnessFactor <= kMaxBrightnessFactor; BrightnessFactor += ((kMaxBrightnessFactor - kMinBrightnessFactor) / 5)) {
					labelTable.put(BrightnessFactor,new JLabel(StringTools.convertDoubleToString((double) BrightnessFactor / 10.0,1)));
				}
			fBrightnessFactorSlider.setLabelTable(labelTable);
			fBrightnessFactorSlider.setPaintLabels(true);
			fBrightnessFactorSlider.setPaintTrack(true);
			fBrightnessFactorSlider.setValue(fBrightnessFactor);
			fBrightnessFactorSlider.addChangeListener(this);
		mainPanel.add(fBrightnessFactorSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fBrightnessFactorLabel = new JLabel();
			lowerPanel.add(fBrightnessFactorLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fBrightnessFactorSlider.setValue(fBrightnessFactor);
		fBrightnessFactorLabel.setText(I18NL10N.translate("text.ColorMap.BrightnessFactorLabel",StringTools.convertDoubleToString(getSelectedBrightnessFactor(),1)));
	}
}
