// ------------------------------------------
// Filename      : ColorMapOffsetChooser.java
// Author        : Sven Maerivoet
// Last modified : 24/09/2014
// Target        : Java VM (1.8)
// ------------------------------------------

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
 * The <CODE>ColorMapOffsetChooser</CODE> class provides a dialog for selecting the colour map offset.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 24/09/2014
 */
public final class ColorMapOffsetChooser extends JDefaultDialog implements ChangeListener
{
	// the minimum and maximum colour offsets (expressed in hundredths)
	private static final int kMinColorOffset = 0 * 100;
	private static final int kMaxColorOffset = 1 * 100;

	// internal datastructures
	private int fColorOffset;
	private JSlider fColorOffsetSlider;
	private JLabel fColorOffsetLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>ColorMapOffsetChooser</CODE> object.
	 *
	 * @param owner        the owning frame
	 * @param colorOffset  the initial colour offset
	 */
	public ColorMapOffsetChooser(JFrame owner, double colorOffset)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {colorOffset},
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
		if (e.getSource() == fColorOffsetSlider) {
			if (!fColorOffsetSlider.getValueIsAdjusting()) {
				// extract value
				fColorOffset = (int) fColorOffsetSlider.getValue();
				updateGUI();
			}
		}
	}

	/**
	 * Returns the selected colour offset.
	 *
	 * @return the selected colour offset
	 */
	public double getSelectedColorOffset()
	{
		return (fColorOffset / 100.0);
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
		fColorOffset = (int) Math.round(((double) parameters[0]) * 100.0);
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.ColorOffsetTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fColorOffsetSlider = new JSlider(JSlider.HORIZONTAL);
			fColorOffsetSlider.setInverted(false);
			fColorOffsetSlider.setMinimum(kMinColorOffset);
			fColorOffsetSlider.setMaximum(kMaxColorOffset);
			fColorOffsetSlider.setMinorTickSpacing(kMaxColorOffset / 10);
			fColorOffsetSlider.setMajorTickSpacing(kMaxColorOffset / 5);
			fColorOffsetSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> colorOffsetSliderLabelTable = new Hashtable<Integer,JLabel>();
				for (int colorOffset = 0; colorOffset <= kMaxColorOffset; colorOffset += (kMaxColorOffset / 5)) {
					colorOffsetSliderLabelTable.put(colorOffset,new JLabel(StringTools.convertDoubleToString((double) colorOffset / 100.0,2)));
				}
			fColorOffsetSlider.setLabelTable(colorOffsetSliderLabelTable);
			fColorOffsetSlider.setPaintLabels(true);
			fColorOffsetSlider.setPaintTrack(true);
			fColorOffsetSlider.setValue(fColorOffset);
			fColorOffsetSlider.addChangeListener(this);
		mainPanel.add(fColorOffsetSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel colorOffsetLabelPanel = new JPanel();
			colorOffsetLabelPanel.setLayout(new BoxLayout(colorOffsetLabelPanel,BoxLayout.X_AXIS));
			fColorOffsetLabel = new JLabel();
			colorOffsetLabelPanel.add(fColorOffsetLabel);
			colorOffsetLabelPanel.add(Box.createHorizontalGlue());
		mainPanel.add(colorOffsetLabelPanel);

		mainPanel.add(Box.createVerticalStrut(10));
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fColorOffsetSlider.setValue(fColorOffset);
		fColorOffsetLabel.setText(I18NL10N.translate("text.ColorMap.ColorOffsetLabel",StringTools.convertDoubleToString(getSelectedColorOffset(),2)));
	}
}
