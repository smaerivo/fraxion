// -------------------------------------------
// Filename      : StripingDensityChooser.java
// Author        : Sven Maerivoet
// Last modified : 06/11/2014
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

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>StripingDensityChooser</CODE> class provides a dialog for selecting the striping density.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 06/11/2014
 */
public final class StripingDensityChooser extends JDefaultDialog implements ChangeListener
{
	// the maximum striping density (expressed in tenths)
	private static final int kMaxStripingDensity = 50 * 10;

	// internal datastructures
	private int fStripingDensity;
	private JSlider fStripingDensitySlider;
	private JLabel fStripingDensityLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>StripingDensityChooser</CODE> object.
	 *
	 * @param owner            the owning frame
	 * @param stripingDensity  the initial striping density
	 */
	public StripingDensityChooser(JFrame owner, double stripingDensity)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {stripingDensity},
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
		if (!fStripingDensitySlider.getValueIsAdjusting()) {
			// extract value
			fStripingDensity = (int) fStripingDensitySlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the selected striping density.
	 *
	 * @return the selected striping density
	 */
	public double getSelectedStripingDensity()
	{
		return (fStripingDensity / 10.0);
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
		fStripingDensity = (int) Math.round(((double) parameters[0]) * 10.0);
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.StripingDensityTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fStripingDensitySlider = new JSlider(JSlider.HORIZONTAL);
			fStripingDensitySlider.setInverted(false);
			fStripingDensitySlider.setMinimum(0);
			fStripingDensitySlider.setMaximum(kMaxStripingDensity);
			fStripingDensitySlider.setMinorTickSpacing(kMaxStripingDensity / 10);
			fStripingDensitySlider.setMajorTickSpacing(kMaxStripingDensity / 5);
			fStripingDensitySlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
				for (int escapeRadius = 0; escapeRadius <= kMaxStripingDensity; escapeRadius += (kMaxStripingDensity / 5)) {
					labelTable.put(escapeRadius,new JLabel(StringTools.convertDoubleToString((double) escapeRadius / 10.0,1)));
				}
			fStripingDensitySlider.setLabelTable(labelTable);
			fStripingDensitySlider.setPaintLabels(true);
			fStripingDensitySlider.setPaintTrack(true);
			fStripingDensitySlider.setValue(fStripingDensity);
			fStripingDensitySlider.addChangeListener(this);
		mainPanel.add(fStripingDensitySlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fStripingDensityLabel = new JLabel();
			lowerPanel.add(fStripingDensityLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fStripingDensitySlider.setValue(fStripingDensity);
		fStripingDensityLabel.setText(I18NL10N.translate("text.ColorMap.StripingDensityLabel",StringTools.convertDoubleToString(getSelectedStripingDensity(),1)));
	}
}
