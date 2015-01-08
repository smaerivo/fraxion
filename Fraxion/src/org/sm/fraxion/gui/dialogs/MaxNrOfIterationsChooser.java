// ---------------------------------------------
// Filename      : MaxNrOfIterationsChooser.java
// Author        : Sven Maerivoet
// Last modified : 10/11/2014
// Target        : Java VM (1.8)
// ---------------------------------------------

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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * The <CODE>MaxNrOfIterationsChooser</CODE> class provides a dialog for selecting the maximum number of iterations.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 10/11/2014
 */
public final class MaxNrOfIterationsChooser extends JDefaultDialog implements ActionListener, ChangeListener
{
	// action commands
	private static final String kActionCommandSetMaxNrOfIterationsPrefix = "maxNrOfIterations";

	// the size of the slider
	private static final Dimension kSliderSize = new Dimension(120,380);

	// internal datastructures
	private int fMaxNrOfIterations;
	private JSlider fMaxNrOfIterationsSlider;
	private JLabel fMaxNrOfIterationsLabel;
	private boolean fSliderAdjusted;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>MaxNrOfIterationsChooser</CODE> object.
	 *
	 * @param owner                     the owning frame
	 * @param initialMaxNrOfIterations  the initial maximum number of iterations
	 */
	public MaxNrOfIterationsChooser(JFrame owner, int initialMaxNrOfIterations)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {initialMaxNrOfIterations},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	// the action-listener
	/**
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);

		String command = e.getActionCommand();

		if (command.startsWith(kActionCommandSetMaxNrOfIterationsPrefix)) {
			// extract value
			fMaxNrOfIterations = Integer.parseInt(command.substring(command.indexOf(".") + 1));
			fSliderAdjusted = true;
			adjustMaxNrOfIterationsSlider();
			updateGUI();
		}
	}

	// the change-listener
	/**
	 */
	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (!fSliderAdjusted) {
			if (!fMaxNrOfIterationsSlider.getValueIsAdjusting()) {
				// extract value
				fMaxNrOfIterations = (int) fMaxNrOfIterationsSlider.getValue();
				updateGUI();
			}
		}
		fSliderAdjusted = false;
	}

	/**
	 * Returns the selected maximum number of iterations.
	 *
	 * @return the selected maximum number of iterations
	 */
	public int getSelectedMaxNrOfIterations()
	{
		return fMaxNrOfIterations;
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
		fMaxNrOfIterations = (Integer) parameters[0];
		fSliderAdjusted = false;
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.MaxNrOfIterationsTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			JPanel upperPanel = new JPanel();
			upperPanel.setLayout(new BoxLayout(upperPanel,BoxLayout.X_AXIS));
				JPanel leftPanel = new JPanel();
				leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
				leftPanel.add(Box.createVerticalStrut(10));
				createMaxNrOfIterationsButton(50,leftPanel);
				createMaxNrOfIterationsButton(100,leftPanel);
				createMaxNrOfIterationsButton(250,leftPanel);
				createMaxNrOfIterationsButton(500,leftPanel);
				createMaxNrOfIterationsButton(1000,leftPanel);
				createMaxNrOfIterationsButton(2000,leftPanel);
				createMaxNrOfIterationsButton(5000,leftPanel);
				createMaxNrOfIterationsButton(10000,leftPanel);
				createMaxNrOfIterationsButton(25000,leftPanel);
				createMaxNrOfIterationsButton(50000,leftPanel);
				createMaxNrOfIterationsButton(100000,leftPanel);
				createMaxNrOfIterationsButton(500000,leftPanel);
				leftPanel.add(Box.createVerticalGlue());
			upperPanel.add(leftPanel);

			upperPanel.add(Box.createHorizontalStrut(60));

				JPanel rightPanel = new JPanel();
					fMaxNrOfIterationsSlider = new JSlider(JSlider.VERTICAL);
					fMaxNrOfIterationsSlider.setInverted(true);
					fMaxNrOfIterationsSlider.setPaintTicks(true);
					fMaxNrOfIterationsSlider.setPaintLabels(true);
					fMaxNrOfIterationsSlider.setPaintTrack(true);
					fSliderAdjusted = true;
					adjustMaxNrOfIterationsSlider();
					fMaxNrOfIterationsSlider.addChangeListener(this);
				rightPanel.add(fMaxNrOfIterationsSlider);
			upperPanel.add(rightPanel);

		mainPanel.add(upperPanel);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fMaxNrOfIterationsLabel = new JLabel();
			lowerPanel.add(fMaxNrOfIterationsLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fMaxNrOfIterationsSlider.setValue(fMaxNrOfIterations);
		fMaxNrOfIterationsLabel.setText(I18NL10N.translate("text.Fractal.MaxNrOfIterationsLabel",String.valueOf(fMaxNrOfIterations)));
	}

	/*******************
	 * PRIVATE METHODS *
	 *******************/

	/**
	 * @param maxNrOfIterations  -
	 * @param panel              -
	 */
	private void createMaxNrOfIterationsButton(int maxNrOfIterations, JPanel panel)
	{
			JButton button = new JButton(String.valueOf(maxNrOfIterations));
			button.setActionCommand(kActionCommandSetMaxNrOfIterationsPrefix + "." + String.valueOf(maxNrOfIterations));
			button.addActionListener(this);
		panel.add(button);

		panel.add(Box.createVerticalStrut(5));
	}

	/**
	 */
	private void adjustMaxNrOfIterationsSlider()
	{
		fMaxNrOfIterationsSlider.setMinimum(1);
		fMaxNrOfIterationsSlider.setMaximum(fMaxNrOfIterations);
		double tickIncrement = (int) Math.round((double) fMaxNrOfIterations / 10.0);
		if (tickIncrement == 0.0) {
			tickIncrement = 1.0;
		}
		fMaxNrOfIterationsSlider.setMajorTickSpacing((int) tickIncrement);
		fMaxNrOfIterationsSlider.setValue(fMaxNrOfIterations);
			Hashtable<Integer,JLabel> maxNrOfIterationsSliderLabelTable = new Hashtable<Integer,JLabel>();
			int maxNrOfIterations = 0;
			for (maxNrOfIterations = 0; maxNrOfIterations <= fMaxNrOfIterations; maxNrOfIterations += tickIncrement) {
				if (maxNrOfIterations == 0) {
					maxNrOfIterationsSliderLabelTable.put(1,new JLabel("1"));
				}
				else {
					maxNrOfIterationsSliderLabelTable.put(maxNrOfIterations,new JLabel(String.valueOf(maxNrOfIterations)));
				}
			}
			if (maxNrOfIterations != fMaxNrOfIterations) {
				maxNrOfIterationsSliderLabelTable.put(fMaxNrOfIterations,new JLabel(String.valueOf(fMaxNrOfIterations)));
			}
		fMaxNrOfIterationsSlider.setLabelTable(maxNrOfIterationsSliderLabelTable);
		fMaxNrOfIterationsSlider.setPreferredSize(kSliderSize);
		fMaxNrOfIterationsSlider.updateUI();
		fMaxNrOfIterationsSlider.revalidate();
	}
}
