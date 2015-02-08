// ----------------------------------------
// Filename      : ScreenBoundsChooser.java
// Author        : Sven Maerivoet
// Last modified : 07/02/2015
// Target        : Java VM (1.8)
// ----------------------------------------

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
import org.sm.fraxion.fractals.util.*;
import org.sm.fraxion.gui.util.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.math.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.swing.util.*;

/**
 * The <CODE>ScreenBoundsChooser</CODE> class provides a dialog for selecting the fractal's screen bounds.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 07/02/2015
 */
public final class ScreenBoundsChooser extends JDefaultDialog implements ActionListener
{
	/**
	 * The number of supported stored screen sizes.
	 */
	public static final int kMaxNrOfStoredScreenSizes = 6;

	// the action commands
	private static final String kActionCommandSpecifyScreenSize = "rb.SpecifyScreenSize";
	private static final String kActionCommandSpecifyPaperSize = "rb.SpecifyPaperSize";

	// specifications of the label and numeric input fields
	private static final String[] kScreenSizeComboBoxData = {
		"320x240 (QVGA)",
		"640x480 (VGA)",
		"768x576 (PAL)",
		"800x600 (SVGA)",
		"1024x768 (XGA)",
		"1280x720 (HD 720)",
		"1280x1024 (SXGA)",
		"1600x900 (WXGA)",
		"1600x1200 (UXGA)",
		"1920x1080 (HD 1080)",
		"2560x1440 (WQHD)"};
	private static final int[] kScreenWidths = {
		320,
		640,
		768,
		800,
		1024,
		1280,
		1280,
		1600,
		1600,
		1920,
		2560};
	private static final int[] kScreenHeights = {
		240,
		480,
		576,
		600,
		768,
		720,
		1024,
		900,
		1200,
		1080,
		1440};
	private static final String[] kPaperSizeComboBoxData = {
		"DIN A7",
		"DIN A6",
		"DIN A5",
		"DIN A4",
		"DIN A3",
		"DIN A2",
		"DIN A1",
		"DIN A0",
		"DIN B7",
		"DIN B6",
		"DIN B5",
		"DIN B4",
		"DIN B3",
		"DIN B2",
		"DIN B1",
		"DIN B0",
		"Letter",
		"Ledger/Tabloid"};
	private static final int[] kPaperWidths = {
		74,
		105,
		148,
		210,
		297,
		420,
		594,
		841,
		88,
		125,
		176,
		250,
		353,
		500,
		707,
		1000,
		216,
		279};
	private static final int[] kPaperHeights = {
		105,
		148,
		210,
		297,
		420,
		594,
		841,
		1189,
		125,
		176,
		250,
		353,
		500,
		707,
		1000,
		1414,
		279,
		432};
	private static final String[] kPaperSizeOrientationComboBoxData = {
		I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Orientation.Portrait"),
		I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Orientation.Landscape")};
	private static final String[] kPaperSizePPIComboBoxData = {
		"72",
		"96",
		"150",
		"300",
		"400",
		"500",
		"600",
		"800",
		"1000",
		"1200"};
	private static final int[] kPaperSizePPIs = {
		72,
		96,
		150,
		300,
		400,
		500,
		600,
		800,
		1000,
		1200};

	// internal datastructures
	private JFrame fOwner;
	private int fScreenWidth;
	private int fScreenHeight;
	private int fMainWidth;
	private int fMainHeight;
	private Insets fScreenInsets;
	private Insets fMainInsets;
	private Insets fScrollInsets;
	private int fScrollBarWidth;
	private int fScrollBarHeight;
	private JLabel fScreenSizeListLabel;
	private JComboBox<String> fScreenSizeComboBox;
	private JLabel fScreenSizeWidthLabel;
	private JNumberInputField fScreenSizeWidthInputField;
	private JLabel fScreenSizeWidthUnitLabel;
	private JLabel fScreenSizeHeightLabel;
	private JNumberInputField fScreenSizeHeightInputField;
	private JLabel fScreenSizeHeightUnitLabel;
	private JButton fUsePhysicalWindowSizeButton;
	private JLabel fPaperSizeListLabel;
	private JComboBox<String> fPaperSizeComboBox;
	private JComboBox<String> fPaperSizeOrientationComboBox;
	private JLabel fPaperSizeWidthLabel;
	private JNumberInputField fPaperSizeWidthInputField;
	private JLabel fPaperSizeWidthUnitLabel;
	private JLabel fPaperSizeHeightLabel;
	private JNumberInputField fPaperSizeHeightInputField;
	private JLabel fPaperSizeHeightUnitLabel;
	private JLabel fPaperSizeResolutionLabel;
	private JComboBox<String> fPaperSizePPIComboBox;
	private JLabel fPaperSizeResolutionUnitLabel;
	private JButton fHalveSizeButton;
	private JButton fDoubleSizeButton;
	private Color fBackgroundColor;
	private ArrayList<StoredScreenSize> fStoredScreenSizes;
	private JButton[] fStoredScreenSizeButtons;
	private JButton[] fDeleteStoredScreenSizeButtons;
	private JLabel fSelectedScreenBoundsLabel;
	private JLabel fProjectedMemoryUsageLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>ScreenBoundsChooser</CODE> object.
	 *
	 * @param owner              the owning frame
	 * @param screenWidth        the initial screen width
	 * @param screenHeight       the initial screen height
	 * @param mainWidth          the main width
	 * @param mainHeight         the main height
	 * @param screenInsets       the screen insets
	 * @param mainInsets         the main insets
	 * @param scrollInsets       the scroll insets
	 * @param scrollBarWidth     the width of the vertical scrollbar
	 * @param scrollBarHeight    the height of the horizontal scrollbar
	 * @param storedScreenSizes  the list with stored screen sizes
	 */
	public ScreenBoundsChooser(JFrame owner, int screenWidth, int screenHeight, int mainWidth, int mainHeight, Insets screenInsets, Insets mainInsets, Insets scrollInsets, int scrollBarWidth, int scrollBarHeight, ArrayList<StoredScreenSize> storedScreenSizes)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {owner,screenWidth,screenHeight,mainWidth,mainHeight,screenInsets,mainInsets,scrollInsets,scrollBarWidth,scrollBarHeight,storedScreenSizes},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the selected screen width.
	 *
	 * @return the selected screen width
	 */
	public int getSelectedScreenWidth()
	{
		return fScreenWidth;
	}

	/**
	 * Returns the selected screen height.
	 *
	 * @return the selected screen height
	 */
	public int getSelectedScreenHeight()
	{
		return fScreenHeight;
	}

	/**
	 * Returns the (modified) stored screen sizes.
	 *
	 * @return the (modified) stored screen sizes
	 */
	public ArrayList<StoredScreenSize> getSelectedStoredScreenSizes()
	{
		return fStoredScreenSizes;
	}

	/**
	 * Returns a <CODE>boolean</CODE> indicating whether or not there is enough memory available.
	 * 
	 * @return a <CODE>boolean</CODE> indicating whether or not there is enough memory available
	 */
	public boolean isProjectedMemoryUsageAvailable()
	{
		return ((MemoryStatistics.getTotalMemory() - calcProjectedMemoryUsage()) > 0L);
	}

	// the action-listener
	/**
	 * See {@link org.sm.smtools.application.JStandardGUIApplication}.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);

		String command = e.getActionCommand();
		Object source = e.getSource();

		if (command.equalsIgnoreCase(kActionCommandSpecifyScreenSize) || command.equalsIgnoreCase(kActionCommandSpecifyPaperSize)) {
			boolean screenSizeSelected = command.equalsIgnoreCase(kActionCommandSpecifyScreenSize);
			fScreenSizeListLabel.setEnabled(screenSizeSelected);
			fScreenSizeComboBox.setEnabled(screenSizeSelected);
			fScreenSizeWidthLabel.setEnabled(screenSizeSelected);
			fScreenSizeWidthInputField.setEnabled(screenSizeSelected);
			fScreenSizeWidthUnitLabel.setEnabled(screenSizeSelected);
			fScreenSizeHeightLabel.setEnabled(screenSizeSelected);
			fScreenSizeHeightInputField.setEnabled(screenSizeSelected);
			fScreenSizeHeightUnitLabel.setEnabled(screenSizeSelected);
			fUsePhysicalWindowSizeButton.setEnabled(screenSizeSelected);
			fPaperSizeListLabel.setEnabled(!screenSizeSelected);
			fPaperSizeComboBox.setEnabled(!screenSizeSelected);
			fPaperSizeOrientationComboBox.setEnabled(!screenSizeSelected);
			fPaperSizeWidthLabel.setEnabled(!screenSizeSelected);
			fPaperSizeWidthInputField.setEnabled(!screenSizeSelected);
			fPaperSizeWidthUnitLabel.setEnabled(!screenSizeSelected);
			fPaperSizeHeightLabel.setEnabled(!screenSizeSelected);
			fPaperSizeHeightInputField.setEnabled(!screenSizeSelected);
			fPaperSizeHeightUnitLabel.setEnabled(!screenSizeSelected);
			fPaperSizeResolutionLabel.setEnabled(!screenSizeSelected);
			fPaperSizePPIComboBox.setEnabled(!screenSizeSelected);
			fPaperSizeResolutionUnitLabel.setEnabled(!screenSizeSelected);
		}
		else if (source == fScreenSizeComboBox) {
			int selectedScreenSizeIndex = fScreenSizeComboBox.getSelectedIndex();
			fScreenWidth = kScreenWidths[selectedScreenSizeIndex];
			fScreenHeight = kScreenHeights[selectedScreenSizeIndex];
			fScreenSizeWidthInputField.setText(String.valueOf(fScreenWidth));
			fScreenSizeHeightInputField.setText(String.valueOf(fScreenHeight));
			updateGUI();
		}
		else if (source == fScreenSizeWidthInputField) {
			fScreenWidth = Integer.parseInt(fScreenSizeWidthInputField.getText());
			updateGUI();
		}
		else if (source == fScreenSizeHeightInputField) {
			fScreenHeight = Integer.parseInt(fScreenSizeHeightInputField.getText());
			updateGUI();
		}
		else if (source == fUsePhysicalWindowSizeButton) {
			// subtract the insets of the current window and OS specifics (e.g., taskbar)
			fScreenWidth = fMainWidth - fScreenInsets.left - fScreenInsets.right - fMainInsets.left - fMainInsets.right - fScrollInsets.left - fScrollInsets.right - fScrollBarWidth;
			fScreenHeight = fMainHeight - fScreenInsets.top - fScreenInsets.bottom - fMainInsets.top - fMainInsets.bottom - fScrollInsets.top - fScrollInsets.bottom - fScrollBarHeight;
			fScreenSizeWidthInputField.setText(String.valueOf(fScreenWidth));
			fScreenSizeHeightInputField.setText(String.valueOf(fScreenHeight));
			updateGUI();
		}
		else if ((source == fPaperSizeComboBox) || (source == fPaperSizeOrientationComboBox)) {
			int selectedPaperSizeIndex = fPaperSizeComboBox.getSelectedIndex();
			int paperWidth = kPaperWidths[selectedPaperSizeIndex];
			int paperHeight = kPaperHeights[selectedPaperSizeIndex];

			// adjust for landscape mode if necessary
			if (fPaperSizeOrientationComboBox.getSelectedIndex() == 1) {
				paperWidth = kPaperHeights[selectedPaperSizeIndex];
				paperHeight = kPaperWidths[selectedPaperSizeIndex];
			}

			fPaperSizeWidthInputField.setText(String.valueOf(paperWidth));
			fPaperSizeHeightInputField.setText(String.valueOf(paperHeight));

			// apply resolution
			int selectedPaperSizePPIIndex = fPaperSizePPIComboBox.getSelectedIndex();
			fScreenWidth = (int) Math.round(((double) paperWidth / 10.0 / 2.54) * (double) kPaperSizePPIs[selectedPaperSizePPIIndex]);
			fScreenHeight = (int) Math.round(((double) paperHeight / 10.0 / 2.54) * (double) kPaperSizePPIs[selectedPaperSizePPIIndex]);

			fScreenSizeWidthInputField.setText(String.valueOf(fScreenWidth));
			fScreenSizeHeightInputField.setText(String.valueOf(fScreenHeight));
			updateGUI();
		}
		else if ((source == fPaperSizeWidthInputField) || (source == fPaperSizeHeightInputField)) {
			int paperWidth = Integer.parseInt(fPaperSizeWidthInputField.getText());
			int paperHeight = Integer.parseInt(fPaperSizeHeightInputField.getText());

			// apply resolution
			int selectedPaperSizePPIIndex = fPaperSizePPIComboBox.getSelectedIndex();
			fScreenWidth = (int) Math.round(((double) paperWidth / 10.0 / 2.54) * (double) kPaperSizePPIs[selectedPaperSizePPIIndex]);
			fScreenHeight = (int) Math.round(((double) paperHeight / 10.0 / 2.54) * (double) kPaperSizePPIs[selectedPaperSizePPIIndex]);

			fScreenSizeWidthInputField.setText(String.valueOf(fScreenWidth));
			fScreenSizeHeightInputField.setText(String.valueOf(fScreenHeight));
			updateGUI();
		}
		else if (source == fPaperSizePPIComboBox) {
			int paperWidth = Integer.parseInt(fPaperSizeWidthInputField.getText());
			int paperHeight = Integer.parseInt(fPaperSizeHeightInputField.getText());

			// apply resolution
			int selectedPaperSizePPIIndex = fPaperSizePPIComboBox.getSelectedIndex();
			fScreenWidth = (int) Math.round(((double) paperWidth / 10.0 / 2.54) * (double) kPaperSizePPIs[selectedPaperSizePPIIndex]);
			fScreenHeight = (int) Math.round(((double) paperHeight / 10.0 / 2.54) * (double) kPaperSizePPIs[selectedPaperSizePPIIndex]);

			fScreenSizeWidthInputField.setText(String.valueOf(fScreenWidth));
			fScreenSizeHeightInputField.setText(String.valueOf(fScreenHeight));
			updateGUI();
		}
		else if (source == fHalveSizeButton) {
			fScreenWidth /= 2;
			fScreenHeight /= 2;
			fScreenSizeWidthInputField.setText(String.valueOf(fScreenWidth));
			fScreenSizeHeightInputField.setText(String.valueOf(fScreenHeight));
			updateGUI();
		}
		else if (source == fDoubleSizeButton) {
			fScreenWidth *= 2;
			fScreenHeight *= 2;
			fScreenSizeWidthInputField.setText(String.valueOf(fScreenWidth));
			fScreenSizeHeightInputField.setText(String.valueOf(fScreenHeight));
			updateGUI();
		}
		else {
			for (int i = 0; i < kMaxNrOfStoredScreenSizes; ++i) {
				try {
					StoredScreenSize storedScreenSize = fStoredScreenSizes.get(i);

					if (source == fStoredScreenSizeButtons[i]) {
						if (storedScreenSize.fSet) {
							fScreenWidth = storedScreenSize.fWidth;
							fScreenHeight = storedScreenSize.fHeight;
							fScreenSizeWidthInputField.setText(String.valueOf(fScreenWidth));
							fScreenSizeHeightInputField.setText(String.valueOf(fScreenHeight));
						}
						else {
							
							StoredScreenSizeDescriptionChooser storedScreenSizeDescriptionChooser = new StoredScreenSizeDescriptionChooser(fOwner,storedScreenSize.fDescription);
							if (!storedScreenSizeDescriptionChooser.isCancelled()) {
								storedScreenSize.fSet = true;
								storedScreenSize.fDescription = storedScreenSizeDescriptionChooser.getSelectedStoredScreenSizeDescription();
								storedScreenSize.fWidth = fScreenWidth;
								storedScreenSize.fHeight = fScreenHeight;
							}
						}
					}

					if (source == fDeleteStoredScreenSizeButtons[i]) {
						if (storedScreenSize.fSet) {
							storedScreenSize.fSet = false;
						}
					}

					adjustStoredScreenSizeButtons();
					updateGUI();
				}
				catch (ArrayIndexOutOfBoundsException exc) {
					// ignore
				}
			}
		}
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Performs custom initialisation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void initialiseClass(Object[] parameters)
	{
		fOwner = (JFrame) parameters[0];
		fScreenWidth = (Integer) parameters[1];
		fScreenHeight = (Integer) parameters[2];
		fMainWidth = (Integer) parameters[3];
		fMainHeight = (Integer) parameters[4];
		fScreenInsets = (Insets) parameters[5];
		fMainInsets = (Insets) parameters[6];
		fScrollInsets = (Insets) parameters[7];
		fScrollBarWidth = (Integer) parameters[8];
		fScrollBarHeight = (Integer) parameters[9];
		fStoredScreenSizes = (ArrayList<StoredScreenSize>) parameters[10];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Navigation.ScreenBoundsChooserTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		final int kInputFieldWidth = 10;
		final boolean kAutoCorrect = false;

		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		ButtonGroup bgRegions = new ButtonGroup();

			JPanel upperTitlePanel = new JPanel();
			upperTitlePanel.setLayout(new BoxLayout(upperTitlePanel,BoxLayout.X_AXIS));
				JRadioButton rbSpecifyScreenSize = new JRadioButton(I18NL10N.translate("text.Navigation.ScreenBoundsChooserSpecifyScreenSize"));
				rbSpecifyScreenSize.setSelected(true);
				rbSpecifyScreenSize.setActionCommand(kActionCommandSpecifyScreenSize);
				rbSpecifyScreenSize.addActionListener(this);
				bgRegions.add(rbSpecifyScreenSize);
			upperTitlePanel.add(rbSpecifyScreenSize);
			upperTitlePanel.add(Box.createHorizontalGlue());
		mainPanel.add(upperTitlePanel);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel specifyScreenSizePanel = new JPanel(new SpringLayout());
				fScreenSizeListLabel = new JLabel(I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.ScreenSize") + ":");
			specifyScreenSizePanel.add(fScreenSizeListLabel);
				fScreenSizeComboBox = new JComboBox<String>(kScreenSizeComboBoxData);
				fScreenSizeComboBox.setSelectedIndex(0);
				fScreenSizeComboBox.addActionListener(this);
			specifyScreenSizePanel.add(fScreenSizeComboBox);
			specifyScreenSizePanel.add(new JLabel());			
				fScreenSizeWidthLabel = new JLabel(I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.Width") + ":");
			specifyScreenSizePanel.add(fScreenSizeWidthLabel);
				fScreenSizeWidthInputField = new JNumberInputField(fScreenWidth,kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterPositiveNumber"));
				fScreenSizeWidthInputField.setNumberFilter(new PositiveNumberFilter());
				fScreenSizeWidthInputField.addActionListener(this);
			specifyScreenSizePanel.add(fScreenSizeWidthInputField);
				fScreenSizeWidthUnitLabel = new JLabel(" " + I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.UnitPixels"));
			specifyScreenSizePanel.add(fScreenSizeWidthUnitLabel);
				fScreenSizeHeightLabel = new JLabel(I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.Height") + ":");
			specifyScreenSizePanel.add(fScreenSizeHeightLabel);
				fScreenSizeHeightInputField = new JNumberInputField(fScreenHeight,kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterPositiveNumber"));
				fScreenSizeHeightInputField.setNumberFilter(new PositiveNumberFilter());
				fScreenSizeHeightInputField.addActionListener(this);
			specifyScreenSizePanel.add(fScreenSizeHeightInputField);
				fScreenSizeHeightUnitLabel = new JLabel(" " + I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.UnitPixels"));
			specifyScreenSizePanel.add(fScreenSizeHeightUnitLabel);
			SpringUtilities.makeCompactGrid(specifyScreenSizePanel,3,3,0,0,5,0);
		mainPanel.add(specifyScreenSizePanel);
		mainPanel.add(Box.createVerticalStrut(5));
			JPanel usePhysicalScreenSizePanel = new JPanel();
			usePhysicalScreenSizePanel.setLayout(new BoxLayout(usePhysicalScreenSizePanel,BoxLayout.X_AXIS));
				fUsePhysicalWindowSizeButton = new JButton(I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.UsePhysicalWindowSize"));
				fUsePhysicalWindowSizeButton.addActionListener(this);
			usePhysicalScreenSizePanel.add(fUsePhysicalWindowSizeButton);
			usePhysicalScreenSizePanel.add(Box.createHorizontalGlue());
		mainPanel.add(usePhysicalScreenSizePanel);

		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(new JEtchedLine(JEtchedLine.EOrientation.kHorizontal));
		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerTitlePanel = new JPanel();
			lowerTitlePanel.setLayout(new BoxLayout(lowerTitlePanel,BoxLayout.X_AXIS));
				JRadioButton rbSpecifyPaperSize = new JRadioButton(I18NL10N.translate("text.Navigation.ScreenBoundsChooserSpecifyPaperSize"));
				rbSpecifyPaperSize.setSelected(false);
				rbSpecifyPaperSize.setActionCommand(kActionCommandSpecifyPaperSize);
				rbSpecifyPaperSize.addActionListener(this);
				bgRegions.add(rbSpecifyPaperSize);
			lowerTitlePanel.add(rbSpecifyPaperSize);
			lowerTitlePanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerTitlePanel);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel specifyPaperSizePanel = new JPanel(new SpringLayout());
				fPaperSizeListLabel = new JLabel(I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.PaperSize") + ":");
				fPaperSizeListLabel.setEnabled(false);
			specifyPaperSizePanel.add(fPaperSizeListLabel);
				fPaperSizeComboBox = new JComboBox<String>(kPaperSizeComboBoxData);
				fPaperSizeComboBox.setEnabled(false);
				fPaperSizeComboBox.addActionListener(this);
			specifyPaperSizePanel.add(fPaperSizeComboBox);
				fPaperSizeOrientationComboBox = new JComboBox<String>(kPaperSizeOrientationComboBoxData);
				fPaperSizeOrientationComboBox.setEnabled(false);
				fPaperSizeOrientationComboBox.addActionListener(this);
			specifyPaperSizePanel.add(fPaperSizeOrientationComboBox);
				fPaperSizeWidthLabel = new JLabel(I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.Width") + ":");
				fPaperSizeWidthLabel.setEnabled(false);
			specifyPaperSizePanel.add(fPaperSizeWidthLabel);
				fPaperSizeWidthInputField = new JNumberInputField(kPaperWidths[0],kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterPositiveNumber"));
				fPaperSizeWidthInputField.setNumberFilter(new PositiveNumberFilter());
				fPaperSizeWidthInputField.setEnabled(false);
				fPaperSizeWidthInputField.addActionListener(this);
			specifyPaperSizePanel.add(fPaperSizeWidthInputField);
				fPaperSizeWidthUnitLabel = new JLabel(" " + I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.UnitMm"));
				fPaperSizeWidthUnitLabel.setEnabled(false);
			specifyPaperSizePanel.add(fPaperSizeWidthUnitLabel);
				fPaperSizeHeightLabel = new JLabel(I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.Height") + ":");
				fPaperSizeHeightLabel.setEnabled(false);
			specifyPaperSizePanel.add(fPaperSizeHeightLabel);
				fPaperSizeHeightInputField = new JNumberInputField(kPaperHeights[0],kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterPositiveNumber"));
				fPaperSizeHeightInputField.setNumberFilter(new PositiveNumberFilter());
				fPaperSizeHeightInputField.setEnabled(false);
				fPaperSizeHeightInputField.addActionListener(this);
			specifyPaperSizePanel.add(fPaperSizeHeightInputField);
				fPaperSizeHeightUnitLabel = new JLabel(" " + I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.UnitMm"));
				fPaperSizeHeightUnitLabel.setEnabled(false);
			specifyPaperSizePanel.add(fPaperSizeHeightUnitLabel);
				fPaperSizeResolutionLabel = new JLabel(I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.Resolution") + ":");
				fPaperSizeResolutionLabel.setEnabled(false);
			specifyPaperSizePanel.add(fPaperSizeResolutionLabel);
				fPaperSizePPIComboBox = new JComboBox<String>(kPaperSizePPIComboBoxData);
				fPaperSizePPIComboBox.setEnabled(false);
				fPaperSizePPIComboBox.addActionListener(this);
			specifyPaperSizePanel.add(fPaperSizePPIComboBox);
				fPaperSizeResolutionUnitLabel = new JLabel(" " + I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.PPI"));
				fPaperSizeResolutionUnitLabel.setEnabled(false);
			specifyPaperSizePanel.add(fPaperSizeResolutionUnitLabel);
			SpringUtilities.makeCompactGrid(specifyPaperSizePanel,4,3,0,0,5,0);
		mainPanel.add(specifyPaperSizePanel);

		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(new JEtchedLine(JEtchedLine.EOrientation.kHorizontal));
		mainPanel.add(Box.createVerticalStrut(10));

			JPanel quickResizePanel = new JPanel();
			quickResizePanel.setLayout(new BoxLayout(quickResizePanel,BoxLayout.X_AXIS));

				fHalveSizeButton = new JButton(I18NL10N.translate("text.Navigation.ScreenBoundsChooserHalveSize"));
				fHalveSizeButton.addActionListener(this);
			quickResizePanel.add(fHalveSizeButton);

			quickResizePanel.add(Box.createHorizontalStrut(10));

				fDoubleSizeButton = new JButton(I18NL10N.translate("text.Navigation.ScreenBoundsChooserDoubleSize"));
				fDoubleSizeButton.addActionListener(this);
			quickResizePanel.add(fDoubleSizeButton);

			quickResizePanel.add(Box.createHorizontalGlue());
		mainPanel.add(quickResizePanel);

		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(new JEtchedLine(JEtchedLine.EOrientation.kHorizontal));
		mainPanel.add(Box.createVerticalStrut(10));

			JPanel storedScreenSizesPanel = new JPanel();
			storedScreenSizesPanel.setLayout(new BoxLayout(storedScreenSizesPanel,BoxLayout.X_AXIS));

				JPanel storedScreenSizesSubPanel = new JPanel();
				storedScreenSizesSubPanel.setLayout(new SpringLayout());

					fStoredScreenSizeButtons = new JButton[kMaxNrOfStoredScreenSizes];
					fDeleteStoredScreenSizeButtons = new JButton[kMaxNrOfStoredScreenSizes];
					for (int i = 0; i < kMaxNrOfStoredScreenSizes; ++i) {
						fStoredScreenSizeButtons[i] = new JButton("M" + String.valueOf(i + 1));
						fStoredScreenSizeButtons[i].addActionListener(this);
					storedScreenSizesSubPanel.add(fStoredScreenSizeButtons[i]);
					}
					for (int i = 0; i < kMaxNrOfStoredScreenSizes; ++i) {
						fDeleteStoredScreenSizeButtons[i] = new JButton("X");
						fDeleteStoredScreenSizeButtons[i].addActionListener(this);
					storedScreenSizesSubPanel.add(fDeleteStoredScreenSizeButtons[i]);
					}
					fBackgroundColor = getBackground();
					adjustStoredScreenSizeButtons();
				SpringUtilities.makeCompactGrid(storedScreenSizesSubPanel,2,kMaxNrOfStoredScreenSizes,0,0,5,5);
			storedScreenSizesPanel.add(storedScreenSizesSubPanel);
			storedScreenSizesPanel.add(Box.createHorizontalGlue());
		mainPanel.add(storedScreenSizesPanel);

		mainPanel.add(Box.createVerticalStrut(5));
		mainPanel.add(new JEtchedLine(JEtchedLine.EOrientation.kHorizontal));
		mainPanel.add(Box.createVerticalStrut(10));

			JPanel selectedSizePanel = new JPanel();
			selectedSizePanel.setLayout(new BoxLayout(selectedSizePanel,BoxLayout.X_AXIS));
				fSelectedScreenBoundsLabel = new JLabel();
			selectedSizePanel.add(fSelectedScreenBoundsLabel);
			selectedSizePanel.add(Box.createHorizontalGlue());
		mainPanel.add(selectedSizePanel);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel projectedMemoryUsagePanel = new JPanel();
			projectedMemoryUsagePanel.setLayout(new BoxLayout(projectedMemoryUsagePanel,BoxLayout.X_AXIS));
				fProjectedMemoryUsageLabel = new JLabel();
			projectedMemoryUsagePanel.add(fProjectedMemoryUsageLabel);
			projectedMemoryUsagePanel.add(Box.createHorizontalGlue());
		mainPanel.add(projectedMemoryUsagePanel);

		mainPanel.add(Box.createVerticalStrut(10));
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fSelectedScreenBoundsLabel.setText("<HTML><B>" + I18NL10N.translate("text.Navigation.ScreenBoundsChooserSelectedSize",String.valueOf(fScreenWidth),String.valueOf(fScreenHeight)) + "</B></HTML>");

		long totalMemoryUsage = calcProjectedMemoryUsage();
		String colorAttribute = "green";
		if (!isProjectedMemoryUsageAvailable()) {
			colorAttribute = "red";
		}
		String memoryUsageAmount = I18NL10N.translate("text.Navigation.ScreenBoundsChooser.Labels.ProjectedMemoryUsage",String.valueOf(Math.round(MathTools.convertBToMiB(totalMemoryUsage))));
		String memoryUsageText =
			"<HTML><B><FONT COLOR=\"" + colorAttribute + "\">" +
					memoryUsageAmount +
			"</FONT></B></HTML>";

		fProjectedMemoryUsageLabel.setText(memoryUsageText);
	}

	/*******************
	 * PRIVATE METHODS *
	 *******************/

	/**
	 */
	private void adjustStoredScreenSizeButtons()
	{
		for (int i = 0; i < kMaxNrOfStoredScreenSizes; ++i) {
			try {
				StoredScreenSize storedScreenSize = fStoredScreenSizes.get(i);
				if (storedScreenSize.fSet) {
					fStoredScreenSizeButtons[i].setToolTipText(storedScreenSize.fDescription);
					fStoredScreenSizeButtons[i].setBackground(Color.GREEN);

					fDeleteStoredScreenSizeButtons[i].setToolTipText(I18NL10N.translate("text.Navigation.ScreenBoundsChooser.StoredScreenSize.DeleteLabel",storedScreenSize.fDescription));
					fDeleteStoredScreenSizeButtons[i].setBackground(Color.RED);
					fDeleteStoredScreenSizeButtons[i].setEnabled(true);
				}
				else {
					fStoredScreenSizeButtons[i].setToolTipText(null);
					fStoredScreenSizeButtons[i].setBackground(fBackgroundColor);

					fDeleteStoredScreenSizeButtons[i].setToolTipText(null);
					fDeleteStoredScreenSizeButtons[i].setBackground(fBackgroundColor);
					fDeleteStoredScreenSizeButtons[i].setEnabled(false);
				}
			}
			catch (IndexOutOfBoundsException exc) {
				// ignore
			}
		}
	}

	/**
	 * @return -
	 */
	private long calcProjectedMemoryUsage()
	{
		// calculate array size in memory
		final long kArrElems = (long) fScreenWidth * (long) fScreenHeight;
		final long kObjShell = 8L;
		final long kArrObjShell = kObjShell + 4L;
		final long kArrElemRef = 4L;
		final long kTotalArrObj = kArrObjShell + (kArrElems * kArrElemRef);
		final long kArrMemUsage = kTotalArrObj + (kArrElems * IterationResult.kMemorySize);

		// calculate image buffer size in memory
		final long kImgMemPerPixel = 25L;
		final long kImgMemUsage = kArrElems * kImgMemPerPixel;

		long totalMemoryUsage = kArrMemUsage + kImgMemUsage;

		// add 15% safety margin
		return (long) Math.round((double) totalMemoryUsage * 1.15);
	}

	/*****************
	 * INNER CLASSES *
	 *****************/

	/**
	 * @author  Sven Maerivoet
	 * @version 05/12/2014
	 */
	private class PositiveNumberFilter extends ANumberFilter
	{
		/******************
		 * PUBLIC METHODS *
		 ******************/

		/**
		 * @param i  -
		 * @return   -
		 */
		public boolean validateInteger(int i)
		{
			return (i > 0);
		}

		/**
		 * @param d  -
		 * @return   -
		 */
		public boolean validateDouble(double d)
		{
			return false;
		}
	}
}
