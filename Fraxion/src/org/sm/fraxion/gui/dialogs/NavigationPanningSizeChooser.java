// -------------------------------------------------
// Filename      : NavigationPanningSizeChooser.java
// Author        : Sven Maerivoet
// Last modified : 06/11/2014
// Target        : Java VM (1.8)
// -------------------------------------------------

/**
 * Copyright 2003-2014 Sven Maerivoet
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
 * The <CODE>NavigationPanningSizeChooser</CODE> class provides a dialog for selecting the size when panning.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 06/11/2014
 */
public final class NavigationPanningSizeChooser extends JDefaultDialog implements ChangeListener
{
	// internal datastructures
	private int fPanningSize;
	private JSlider fPanningSizeSlider;
	private JLabel fPanningSizeLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>NavigationPanningSizeChooser</CODE> object.
	 *
	 * @param owner        the owning frame
	 * @param panningSize  the initial panning size
	 */
	public NavigationPanningSizeChooser(JFrame owner, double panningSize)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {panningSize},
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
		if (!fPanningSizeSlider.getValueIsAdjusting()) {
			// extract value
			fPanningSize = (int) fPanningSizeSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the panning size between 0 and 1.
	 *
	 * @return the panning size between 0 and 1
	 */
	public double getSelectedPanningSize()
	{
		return ((double) fPanningSize / 100.0);
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
		fPanningSize = (int) Math.round(((double) parameters[0]) * 100.0);

		if (fPanningSize < 1) {
			fPanningSize = 1;
		}
		else if (fPanningSize > 100) {
			fPanningSize = 100;
		}
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Navigation.PanningSizeTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fPanningSizeSlider = new JSlider(JSlider.HORIZONTAL);
			fPanningSizeSlider.setInverted(false);
			fPanningSizeSlider.setMinimum(1);
			fPanningSizeSlider.setMaximum(100);
			fPanningSizeSlider.setMinorTickSpacing(25);
			fPanningSizeSlider.setMajorTickSpacing(10);
			fPanningSizeSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> panningSizeSliderLabelTable = new Hashtable<Integer,JLabel>();
				for (int panningSize = 0; panningSize <= 100; panningSize += (100 / 5)) {
					if (panningSize == 0) {
						panningSizeSliderLabelTable.put(1,new JLabel("1"));
					}
					else {
						panningSizeSliderLabelTable.put(panningSize,new JLabel(String.valueOf(panningSize)));
					}
				}
				fPanningSizeSlider.setLabelTable(panningSizeSliderLabelTable);
			fPanningSizeSlider.setPaintLabels(true);
			fPanningSizeSlider.setPaintTrack(true);
			fPanningSizeSlider.setValue(fPanningSize);
			fPanningSizeSlider.addChangeListener(this);
		mainPanel.add(fPanningSizeSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fPanningSizeLabel = new JLabel();
			lowerPanel.add(fPanningSizeLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fPanningSizeSlider.setValue(fPanningSize);
		fPanningSizeLabel.setText(I18NL10N.translate("text.Navigation.PanningSizeLabel",StringTools.convertDoubleToString(getSelectedPanningSize() * 100,0)));
	}
}
