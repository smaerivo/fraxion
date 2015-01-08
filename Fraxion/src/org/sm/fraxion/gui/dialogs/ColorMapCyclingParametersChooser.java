// -----------------------------------------------------
// Filename      : ColorMapCyclingParametersChooser.java
// Author        : Sven Maerivoet
// Last modified : 24/09/2014
// Target        : Java VM (1.8)
// -----------------------------------------------------

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
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.swing.util.*;

/**
 * The <CODE>ColorMapCyclingParametersChooser</CODE> class provides a dialog for selecting the colour map colour cycling parameters.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 24/09/2014
 */
public final class ColorMapCyclingParametersChooser extends JDefaultDialog implements ActionListener, ChangeListener
{
	// the action commands
	private static final String kActionCommandSpecifyForwardColorCyclingDirection = "rb.SpecifyForwardColorCyclingDirection";
	private static final String kActionCommandSpecifyBackwardColorCyclingDirection = "rb.SpecifyBackwardColorCyclingDirection";

	// the minimum and maximum colour cycling delay (expressed in milliseconds between 10 and 1000)
	private static final int kMinColorCyclingDelay = 10;
	private static final int kMaxColorCyclingDelay = 1000;

	// the minimum and maximum colour cycling smoothness (expressed in thousands between 0.001 and 0.100)
	private static final int kMinColorCyclingSmoothness = 1;
	private static final int kMaxColorCyclingSmoothness = 100;

	// internal datastructures
	private int fColorCyclingDelay;
	private int fColorCyclingSmoothness;
	private boolean fColorCyclingDirectionForward;
	private JSlider fColorCyclingDelaySlider;
	private JSlider fColorCyclingSmoothnessSlider;
	private HashMap<String,JRadioButton> fColorCyclingDirectionRadioButtons;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>ColorMapCyclingParametersChooser</CODE> object.
	 *
	 * @param owner                         the owning frame
	 * @param colorCyclingDelay             the delay (expressed in milliseconds) between successive cycles
	 * @param colorCyclingSmoothness        the smoothness (i.e., colour map jump size) for the colour cycling
	 * @param colorCyclingDirectionForward  a <CODE>boolean</CODE> indicating direction of the colour cycling
	 */
	public ColorMapCyclingParametersChooser(JFrame owner, int colorCyclingDelay, double colorCyclingSmoothness, boolean colorCyclingDirectionForward)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {colorCyclingDelay, colorCyclingSmoothness, colorCyclingDirectionForward},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	// the action-listener
	/**
	 * See {@link org.sm.smtools.application.JStandardGUIApplication}.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);

		String command = e.getActionCommand();

		if (command.equalsIgnoreCase(kActionCommandSpecifyForwardColorCyclingDirection)) {
			fColorCyclingDirectionForward = true;
			updateGUI();
		}
		else if (command.equalsIgnoreCase(kActionCommandSpecifyBackwardColorCyclingDirection)) {
			fColorCyclingDirectionForward = false;
			updateGUI();
		}
	}

	// the change-listener
	/**
	 */
	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == fColorCyclingDelaySlider) {
			if (!fColorCyclingDelaySlider.getValueIsAdjusting()) {
				// extract value
				fColorCyclingDelay = (int) fColorCyclingDelaySlider.getValue();
				updateGUI();
			}
		}
		else if (e.getSource() == fColorCyclingSmoothnessSlider) {
			if (!fColorCyclingSmoothnessSlider.getValueIsAdjusting()) {
				// extract value
				fColorCyclingSmoothness = (int) fColorCyclingSmoothnessSlider.getValue();
				updateGUI();
			}
		}
	}

	/**
	 * Returns the selected colour cycling delay.
	 *
	 * @return the selected colour cycling delay
	 */
	public int getSelectedColorCyclingDelay()
	{
		return fColorCyclingDelay;
	}

	/**
	 * Returns the selected colour cycling smoothness.
	 *
	 * @return the selected colour cycling smoothness
	 */
	public double getSelectedColorCyclingSmoothness()
	{
		return (((double) fColorCyclingSmoothness) / 1000.0);
	}

	/**
	 * Returns the selected colour cycling direction (<CODE>true</CODE> indicates forward).
	 *
	 * @return the selected colour cycling direction (<CODE>true</CODE> indicates forward)
	 */
	public boolean getSelectedColorCyclingDirectionForward()
	{
		return fColorCyclingDirectionForward;
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
		fColorCyclingDelay = (int) parameters[0];
		fColorCyclingSmoothness = (int) Math.round(((double) parameters[1]) * 1000.0);
		fColorCyclingDirectionForward = (boolean) parameters[2];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.ColorCyclingParametersTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		JLabel label = null;
		JRadioButton radioButton = null;
		ButtonGroup buttonGroup = null;

		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
			label = new JLabel(I18NL10N.translate("text.ColorMap.ColorCyclingSpeedLabel") + ":");
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(label);

		mainPanel.add(Box.createVerticalStrut(5));

			fColorCyclingDelaySlider = new JSlider(JSlider.HORIZONTAL);
			fColorCyclingDelaySlider.setInverted(true);
			fColorCyclingDelaySlider.setMinimum(kMinColorCyclingDelay);
			fColorCyclingDelaySlider.setMaximum(kMaxColorCyclingDelay);
			fColorCyclingDelaySlider.setMinorTickSpacing(kMaxColorCyclingDelay / 10);
			fColorCyclingDelaySlider.setMajorTickSpacing(kMaxColorCyclingDelay / 5);
			fColorCyclingDelaySlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
				labelTable.put(kMinColorCyclingDelay,new JLabel(I18NL10N.translate("text.ColorMap.ColorCyclingSpeedFastLabel")));
				labelTable.put((kMinColorCyclingDelay + kMaxColorCyclingDelay) / 2,new JLabel(I18NL10N.translate("text.ColorMap.ColorCyclingSpeedMediumLabel")));
				labelTable.put(kMaxColorCyclingDelay,new JLabel(I18NL10N.translate("text.ColorMap.ColorCyclingSpeedSlowLabel")));
			fColorCyclingDelaySlider.setLabelTable(labelTable);
			fColorCyclingDelaySlider.setPaintLabels(true);
			fColorCyclingDelaySlider.setPaintTrack(true);
			fColorCyclingDelaySlider.setValue(fColorCyclingDelay);
			fColorCyclingDelaySlider.addChangeListener(this);
		mainPanel.add(fColorCyclingDelaySlider);

		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(new JEtchedLine());
		mainPanel.add(Box.createVerticalStrut(10));

			label = new JLabel(I18NL10N.translate("text.ColorMap.ColorCyclingSmoothnessLabel") + ":");
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(label);

		mainPanel.add(Box.createVerticalStrut(5));

			fColorCyclingSmoothnessSlider = new JSlider(JSlider.HORIZONTAL);
			fColorCyclingSmoothnessSlider.setInverted(true);
			fColorCyclingSmoothnessSlider.setMinimum(kMinColorCyclingSmoothness);
			fColorCyclingSmoothnessSlider.setMaximum(kMaxColorCyclingSmoothness);
			fColorCyclingSmoothnessSlider.setMinorTickSpacing(kMaxColorCyclingSmoothness / 10);
			fColorCyclingSmoothnessSlider.setMajorTickSpacing(kMaxColorCyclingSmoothness / 5);
			fColorCyclingSmoothnessSlider.setPaintTicks(true);
				labelTable = new Hashtable<Integer,JLabel>();
				labelTable.put(kMinColorCyclingSmoothness,new JLabel(I18NL10N.translate("text.ColorMap.ColorCyclingSmoothnessSmoothLabel")));
				labelTable.put((kMinColorCyclingSmoothness + kMaxColorCyclingSmoothness) / 2,new JLabel(I18NL10N.translate("text.ColorMap.ColorCyclingSmoothnessMediumLabel")));
				labelTable.put(kMaxColorCyclingSmoothness,new JLabel(I18NL10N.translate("text.ColorMap.ColorCyclingSmoothnessRoughLabel")));
			fColorCyclingSmoothnessSlider.setLabelTable(labelTable);
			fColorCyclingSmoothnessSlider.setPaintLabels(true);
			fColorCyclingSmoothnessSlider.setPaintTrack(true);
			fColorCyclingSmoothnessSlider.setValue(fColorCyclingSmoothness);
			fColorCyclingSmoothnessSlider.addChangeListener(this);
		mainPanel.add(fColorCyclingSmoothnessSlider);

		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(new JEtchedLine());
		mainPanel.add(Box.createVerticalStrut(10));

			label = new JLabel(I18NL10N.translate("text.ColorMap.ColorCyclingDirectionLabel") + ":");
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(label);

		mainPanel.add(Box.createVerticalStrut(5));

			buttonGroup = new ButtonGroup();
			fColorCyclingDirectionRadioButtons = new HashMap<String,JRadioButton>();
			radioButton = new JRadioButton(I18NL10N.translate("text.ColorMap.ColorCyclingDirectionForwardLabel"));
			radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioButton.setActionCommand(kActionCommandSpecifyForwardColorCyclingDirection);
			radioButton.addActionListener(this);
			buttonGroup.add(radioButton);
			fColorCyclingDirectionRadioButtons.put(kActionCommandSpecifyForwardColorCyclingDirection,radioButton);

		mainPanel.add(radioButton);
			radioButton = new JRadioButton(I18NL10N.translate("text.ColorMap.ColorCyclingDirectionBackwardLabel"));
			radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioButton.setActionCommand(kActionCommandSpecifyBackwardColorCyclingDirection);
			radioButton.addActionListener(this);
			buttonGroup.add(radioButton);
			fColorCyclingDirectionRadioButtons.put(kActionCommandSpecifyBackwardColorCyclingDirection,radioButton);
		mainPanel.add(radioButton);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fColorCyclingDelaySlider.setValue(fColorCyclingDelay);
		fColorCyclingSmoothnessSlider.setValue(fColorCyclingSmoothness);
		if (fColorCyclingDirectionForward) {
			fColorCyclingDirectionRadioButtons.get(kActionCommandSpecifyForwardColorCyclingDirection).setSelected(true);
		}
		else {
			fColorCyclingDirectionRadioButtons.get(kActionCommandSpecifyBackwardColorCyclingDirection).setSelected(true);
		}
	}
}
