// ------------------------------------------------------
// Filename      : GaussianIntegersTrapFactorChooser.java
// Author        : Sven Maerivoet
// Last modified : 06/11/2014
// Target        : Java VM (1.8)
// ------------------------------------------------------

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
 * The <CODE>GaussianIntegersTrapFactorChooser</CODE> class provides a dialog for selecting the trap factor associated with Gaussian integers.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 06/11/2014
 */
public final class GaussianIntegersTrapFactorChooser extends JDefaultDialog implements ChangeListener
{
	// the minimum and maximum colour offsets (expressed in hundredths)
	private static final int kMinGaussianIntegersTrapFactor = 1;
	private static final int kMaxGaussianIntegersTrapFactor = 10 * 100;

	// internal datastructures
	private int fGaussianIntegersTrapFactor;
	private JSlider fGaussianIntegersTrapFactorSlider;
	private JLabel fGaussianIntegersTrapFactorLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>GaussianIntegersTrapFactorChooser</CODE> object.
	 *
	 * @param owner                       the owning frame
	 * @param gaussianIntegersTrapFactor  the initial trap factor
	 */
	public GaussianIntegersTrapFactorChooser(JFrame owner, double gaussianIntegersTrapFactor)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {gaussianIntegersTrapFactor},
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
		if (e.getSource() == fGaussianIntegersTrapFactorSlider) {
			if (!fGaussianIntegersTrapFactorSlider.getValueIsAdjusting()) {
				// extract value
				fGaussianIntegersTrapFactor = (int) fGaussianIntegersTrapFactorSlider.getValue();
				updateGUI();
			}
		}
	}

	/**
	 * Returns the selected trap factor.
	 *
	 * @return the selected trap factor
	 */
	public double getSelectedGaussianIntegersTrapFactor()
	{
		return (fGaussianIntegersTrapFactor / 100.0);
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
		fGaussianIntegersTrapFactor = (int) Math.round(((double) parameters[0]) * 100.0);
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.GaussianIntegersTrapFactorTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fGaussianIntegersTrapFactorSlider = new JSlider(JSlider.HORIZONTAL);
			fGaussianIntegersTrapFactorSlider.setInverted(false);
			fGaussianIntegersTrapFactorSlider.setMinimum(kMinGaussianIntegersTrapFactor);
			fGaussianIntegersTrapFactorSlider.setMaximum(kMaxGaussianIntegersTrapFactor);
			fGaussianIntegersTrapFactorSlider.setMinorTickSpacing(kMaxGaussianIntegersTrapFactor / 10);
			fGaussianIntegersTrapFactorSlider.setMajorTickSpacing(kMaxGaussianIntegersTrapFactor / 5);
			fGaussianIntegersTrapFactorSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> gaussianIntegersTrapFactorSliderLabelTable = new Hashtable<Integer,JLabel>();
				for (int gaussianIntegersTrapFactor = 0; gaussianIntegersTrapFactor <= kMaxGaussianIntegersTrapFactor; gaussianIntegersTrapFactor += (kMaxGaussianIntegersTrapFactor / 5)) {
					if (gaussianIntegersTrapFactor == 0) {
						gaussianIntegersTrapFactorSliderLabelTable.put(kMinGaussianIntegersTrapFactor,new JLabel(StringTools.convertDoubleToString((double) kMinGaussianIntegersTrapFactor / 100.0,2)));
					}
					else {
						gaussianIntegersTrapFactorSliderLabelTable.put(gaussianIntegersTrapFactor,new JLabel(StringTools.convertDoubleToString((double) gaussianIntegersTrapFactor / 100.0,2)));
					}
				}
			fGaussianIntegersTrapFactorSlider.setLabelTable(gaussianIntegersTrapFactorSliderLabelTable);
			fGaussianIntegersTrapFactorSlider.setPaintLabels(true);
			fGaussianIntegersTrapFactorSlider.setPaintTrack(true);
			fGaussianIntegersTrapFactorSlider.setValue(fGaussianIntegersTrapFactor);
			fGaussianIntegersTrapFactorSlider.addChangeListener(this);
		mainPanel.add(fGaussianIntegersTrapFactorSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel gaussianIntegersTrapFactorLabelPanel = new JPanel();
			gaussianIntegersTrapFactorLabelPanel.setLayout(new BoxLayout(gaussianIntegersTrapFactorLabelPanel,BoxLayout.X_AXIS));
			fGaussianIntegersTrapFactorLabel = new JLabel();
			gaussianIntegersTrapFactorLabelPanel.add(fGaussianIntegersTrapFactorLabel);
			gaussianIntegersTrapFactorLabelPanel.add(Box.createHorizontalGlue());
		mainPanel.add(gaussianIntegersTrapFactorLabelPanel);

		mainPanel.add(Box.createVerticalStrut(10));
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fGaussianIntegersTrapFactorSlider.setValue(fGaussianIntegersTrapFactor);
		fGaussianIntegersTrapFactorLabel.setText(I18NL10N.translate("text.ColorMap.GaussianIntegersTrapFactorLabel",StringTools.convertDoubleToString(getSelectedGaussianIntegersTrapFactor(),2)));
	}
}
