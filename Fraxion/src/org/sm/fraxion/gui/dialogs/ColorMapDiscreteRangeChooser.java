// -------------------------------------------------
// Filename      : ColorMapDiscreteRangeChooser.java
// Author        : Sven Maerivoet
// Last modified : 25/09/2014
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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * The <CODE>ColorMapDiscreteRangeChooser</CODE> class provides a dialog for selecting a discrete colour map range.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 25/09/2014
 */
public final class ColorMapDiscreteRangeChooser extends JDefaultDialog implements ActionListener, ChangeListener
{
	// action commands
	private static final String kActionCommandSetRangePrefix = "range";

	// the size of the slider
	private static final Dimension kSliderSize = new Dimension(120,420);

	// internal datastructures
	private int fRange;
	private JSlider fRangeSlider;
	private JLabel fRangeLabel;
	private boolean fSliderAdjusted;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>ColorMapDiscreteRangeChooser</CODE> object.
	 *
	 * @param owner         the owning frame
	 * @param initialRange  the initial range
	 * @param maxRange      the maximum range
	 */
	public ColorMapDiscreteRangeChooser(JFrame owner, int initialRange, int maxRange)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {initialRange, maxRange},
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

		if (command.startsWith(kActionCommandSetRangePrefix)) {
			// extract value
			fRange = Integer.parseInt(command.substring(command.indexOf(".") + 1));
			fSliderAdjusted = true;
			adjustRangeSlider();
		}
	}

	// the change-listener
	/**
	 */
	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (!fSliderAdjusted) {
			if (!fRangeSlider.getValueIsAdjusting()) {
				// extract value
				fRange = (int) fRangeSlider.getValue();
				updateGUI();
			}
		}
		fSliderAdjusted = false;
	}

	/**
	 * Returns the selected range.
	 *
	 * @return the selected range
	 */
	public int getSelectedRange()
	{
		return fRange;
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
		fRange = (Integer) parameters[0];
		fSliderAdjusted = false;
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.DiscreteRangeSelectionTitle");
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
				createRangeButton(2,leftPanel);
				createRangeButton(4,leftPanel);
				createRangeButton(8,leftPanel);
				createRangeButton(16,leftPanel);
				createRangeButton(32,leftPanel);
				createRangeButton(64,leftPanel);
				createRangeButton(128,leftPanel);
				createRangeButton(256,leftPanel);
				createRangeButton(512,leftPanel);
				createRangeButton(1024,leftPanel);
				createRangeButton(2048,leftPanel);
				createRangeButton(4096,leftPanel);
				createRangeButton(8192,leftPanel);
				createRangeButton(16384,leftPanel);
				leftPanel.add(Box.createVerticalGlue());
			upperPanel.add(leftPanel);

			upperPanel.add(Box.createHorizontalStrut(60));

				JPanel rightPanel = new JPanel();
					fRangeSlider = new JSlider(JSlider.VERTICAL);
					fRangeSlider.setInverted(true);
					fRangeSlider.setMinimum(2);
					fRangeSlider.setPaintTicks(true);
					fRangeSlider.setPaintLabels(true);
					fRangeSlider.setPaintTrack(true);
					fRangeSlider.addChangeListener(this);
				rightPanel.add(fRangeSlider);
			upperPanel.add(rightPanel);
		mainPanel.add(upperPanel);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fRangeLabel = new JLabel();
			lowerPanel.add(fRangeLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);

		fSliderAdjusted = true;
		adjustRangeSlider();
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fRangeSlider.setValue(fRange);
		fRangeLabel.setText(I18NL10N.translate("text.ColorMap.RangeLabel",String.valueOf(fRange)));
	}

	/*******************
	 * PRIVATE METHODS *
	 *******************/

	/**
	 * @param range  -
	 * @param panel  -
	 */
	private void createRangeButton(int range, JPanel panel)
	{
			JButton button = new JButton(String.valueOf(range));
			button.setActionCommand(kActionCommandSetRangePrefix + "." + String.valueOf(range));
			button.addActionListener(this);
		panel.add(button);

		panel.add(Box.createVerticalStrut(5));
	}

	/**
	 */
	private void adjustRangeSlider()
	{
		fRangeSlider.setMaximum(fRange);
		fRangeSlider.setMinorTickSpacing((int) Math.round((double) fRange / 10.0));
		fRangeSlider.setMajorTickSpacing((int) Math.round((double) fRange / 5.0));
		fRangeSlider.setValue(fRange);
			Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
			final int kNrOfLabels = 10;
			for (int labelNr = 0; labelNr < kNrOfLabels; ++ labelNr) {
				int index = (int) Math.round((double) labelNr * ((double) fRange / ((double) kNrOfLabels - 1.0)));
				labelTable.put(index,new JLabel(String.valueOf(index)));
			}
		fRangeSlider.setLabelTable(labelTable);
		fRangeSlider.setPreferredSize(kSliderSize);
		fRangeSlider.updateUI();
		fRangeSlider.revalidate();
	}
}
