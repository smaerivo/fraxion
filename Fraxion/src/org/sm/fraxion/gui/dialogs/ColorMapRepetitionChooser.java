// ----------------------------------------------
// Filename      : ColorMapRepetitionChooser.java
// Author        : Sven Maerivoet
// Last modified : 06/11/2014
// Target        : Java VM (1.8)
// ----------------------------------------------

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
 * The <CODE>ColorMapRepetitionChooser</CODE> class provides a dialog for selecting the colour map repetition.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 06/11/2014
 */
public final class ColorMapRepetitionChooser extends JDefaultDialog implements ChangeListener
{
	// the minimum and maximum colour repetitions (expressed in tenths)
	private static final int kMinColorRepetition = 1 * 10;
	private static final int kMaxColorRepetition = 10 * 10;

	// internal datastructures
	private int fColorRepetition;
	private JSlider fColorRepetitionSlider;
	private JLabel fColorRepetitionLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>ColorMapRepetitionChooser</CODE> object.
	 *
	 * @param owner            the owning frame
	 * @param colorRepetition  the initial colour repetition
	 */
	public ColorMapRepetitionChooser(JFrame owner, double colorRepetition)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {colorRepetition},
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
		if (e.getSource() == fColorRepetitionSlider) {
			if (!fColorRepetitionSlider.getValueIsAdjusting()) {
				// extract value
				fColorRepetition = (int) fColorRepetitionSlider.getValue();
				updateGUI();
			}
		}
	}

	/**
	 * Returns the selected colour repetition.
	 *
	 * @return the selected colour repetition
	 */
	public double getSelectedColorRepetition()
	{
		return (fColorRepetition / 10.0);
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
		fColorRepetition = (int) Math.round(((double) parameters[0]) * 10.0);
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.ColorRepetitionTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fColorRepetitionSlider = new JSlider(JSlider.HORIZONTAL);
			fColorRepetitionSlider.setInverted(false);
			fColorRepetitionSlider.setMinimum(kMinColorRepetition);
			fColorRepetitionSlider.setMaximum(kMaxColorRepetition);
			fColorRepetitionSlider.setMinorTickSpacing(kMaxColorRepetition / 10);
			fColorRepetitionSlider.setMajorTickSpacing(kMaxColorRepetition / 5);
			fColorRepetitionSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> colorRepetitionSliderLabelTable = new Hashtable<Integer,JLabel>();
				for (int colorRepetition = 0; colorRepetition <= kMaxColorRepetition; colorRepetition += (kMaxColorRepetition / 5)) {
					if (colorRepetition == 0) {
						colorRepetitionSliderLabelTable.put(kMinColorRepetition,new JLabel(StringTools.convertDoubleToString((double) kMinColorRepetition / 10.0,1)));
					}
					else {
						colorRepetitionSliderLabelTable.put(colorRepetition,new JLabel(StringTools.convertDoubleToString((double) colorRepetition / 10.0,1)));
					}
				}
			fColorRepetitionSlider.setLabelTable(colorRepetitionSliderLabelTable);
			fColorRepetitionSlider.setPaintLabels(true);
			fColorRepetitionSlider.setPaintTrack(true);
			fColorRepetitionSlider.setValue(fColorRepetition);
			fColorRepetitionSlider.addChangeListener(this);
		mainPanel.add(fColorRepetitionSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel colorRepetitionLabelPanel = new JPanel();
			colorRepetitionLabelPanel.setLayout(new BoxLayout(colorRepetitionLabelPanel,BoxLayout.X_AXIS));
			fColorRepetitionLabel = new JLabel();
			colorRepetitionLabelPanel.add(fColorRepetitionLabel);
			colorRepetitionLabelPanel.add(Box.createHorizontalGlue());
		mainPanel.add(colorRepetitionLabelPanel);

		mainPanel.add(Box.createVerticalStrut(10));
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fColorRepetitionSlider.setValue(fColorRepetition);
		fColorRepetitionLabel.setText(I18NL10N.translate("text.ColorMap.ColorRepetitionLabel",StringTools.convertDoubleToString(getSelectedColorRepetition(),1)));
	}
}
