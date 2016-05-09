// ---------------------------------------
// Filename      : FilterSetupChooser.java
// Author        : Sven Maerivoet
// Last modified : 24/04/2016
// Target        : Java VM (1.8)
// ---------------------------------------

/**
 * Copyright 2003-2016 Sven Maerivoet
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

import java.awt.event.*;
import javax.swing.*;
import org.sm.fraxion.concurrent.*;
import org.sm.fraxion.gui.*;
import org.sm.fraxion.gui.filters.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.swing.util.*;

/**
 * The <CODE>FilterSetupChooser</CODE> class provides a dialog for setting up the post-processing filter chain.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 24/04/2016
 */
public final class FilterSetupChooser extends JDefaultDialog implements ActionListener
{
	// the number of available filters
	private static final int kNrOfAvailableFilters = 10;

	// specifications of the combobox input fields
	private static final int kBlurFilterIndex = 0;
	private static final int kEdgeFilterIndex = 1;
	private static final int kInvertFilterIndex = 2;
	private static final int kPosteriseFilterIndex = 3;
	private static final int kSharpenFilterIndex = 4;

	private static final String[] kFilterComboBoxData = {
		I18NL10N.translate("text.Filters.Filter.Blur"),
		I18NL10N.translate("text.Filters.Filter.Edge"),
		I18NL10N.translate("text.Filters.Filter.Invert"),
		I18NL10N.translate("text.Filters.Filter.Posterise"),
		I18NL10N.translate("text.Filters.Filter.Sharpen")};

	// internal datastructures
	private FilterChain fBackupFilterChain;
	private boolean fBackupUsePostProcessingFilters;
	private AFilter[] fFilterChain;
	private int fNrOfFilters;
	private FractalPanel fFractalPanel;
	private IteratorController fIteratorController;
	private JFilterLine[] fFilterLines;
	private JButton fAddFilterButton;
	private boolean fAdjusting;
	private JCheckBox fAutoProofCheckBox;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>FilterSetupChooser</CODE> object.
	 *
	 * @param owner               the owning window
	 * @param fractalPanel        a reference to the GUI's fractal panel
	 * @param iteratorController  a reference to the iterator controller
	 */
	public FilterSetupChooser(JFrame owner, FractalPanel fractalPanel, IteratorController iteratorController)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {fractalPanel,iteratorController},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the selected filter chain.
	 *
	 * @return the selected filter chain
	 */
	public FilterChain getSelectedFilterChain()
	{
		FilterChain filterChain = new FilterChain();
		for (int filterIndex = 0; filterIndex < fNrOfFilters; ++filterIndex) {
			filterChain.addFilter(fFilterChain[filterIndex].clone());
		}
		return filterChain;
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

		if (command.startsWith(JFilterLine.kRemoveActionCommand)) {
			fAdjusting = true;
			int filterIndex = Integer.parseInt(command.substring(command.indexOf(":") + 1));

			// move all filters one up and disable the last one
			for (int nextFilterIndex = (filterIndex + 1); nextFilterIndex < fNrOfFilters; ++nextFilterIndex) {
				fFilterLines[nextFilterIndex].copyStateTo(fFilterLines[nextFilterIndex - 1]);
				fFilterChain[nextFilterIndex - 1] = fFilterChain[nextFilterIndex];
			}
			fFilterLines[fNrOfFilters - 1].disable();
			fFilterChain[fNrOfFilters - 1] = null;
			--fNrOfFilters;
			adjustMoveButtons();

			fAddFilterButton.setEnabled(fNrOfFilters < kNrOfAvailableFilters);
			autoProof();
			fAdjusting = false;
		}
		else if (command.startsWith(JFilterLine.kFilterTypeActionCommand) && !fAdjusting) {
			int filterIndex = Integer.parseInt(command.substring(command.indexOf(":") + 1));
			if (fFilterLines[filterIndex] != null) {
				int selectedIndex = fFilterLines[filterIndex].fFilterTypeComboBox.getSelectedIndex();
				switch (selectedIndex) {
					case kBlurFilterIndex: fFilterChain[filterIndex] = new BlurFilter(); break;
					case kEdgeFilterIndex: fFilterChain[filterIndex] = new EdgeFilter(); break;
					case kInvertFilterIndex: fFilterChain[filterIndex] = new InvertFilter(); break;
					case kPosteriseFilterIndex: fFilterChain[filterIndex] = new PosteriseFilter(); break;
					case kSharpenFilterIndex: fFilterChain[filterIndex] = new SharpenFilter(); break;
					default: break;
				}
				fFilterLines[filterIndex].setFilter(fFilterChain[filterIndex]);
				autoProof();
			}
		}
		else if (command.startsWith(JFilterLine.kModifyActionCommand)) {
			int filterIndex = Integer.parseInt(command.substring(command.indexOf(":") + 1));
			try {
				if (fFilterChain[filterIndex] instanceof BlurFilter) {
					BlurFilter blurFilter = (BlurFilter) fFilterChain[filterIndex];
					BlurKernelSizeChooser blurKernelSizeChooser = new BlurKernelSizeChooser((JFrame) getOwner(),blurFilter.getKernelSize());
					if (!blurKernelSizeChooser.isCancelled()) {
						blurFilter.setKernelSize(blurKernelSizeChooser.getSelectedKernelSize());
					}
					autoProof();
				}
				else if (fFilterChain[filterIndex] instanceof EdgeFilter) {
					EdgeFilter edgeFilter = (EdgeFilter) fFilterChain[filterIndex];
					EdgeDetectionStrengthChooser edgeDetectionStrengthChooser = new EdgeDetectionStrengthChooser((JFrame) getOwner(),edgeFilter.getStrength());
					if (!edgeDetectionStrengthChooser.isCancelled()) {
						edgeFilter.setStrength(edgeDetectionStrengthChooser.getSelectedStrength());
					}
					autoProof();
				}
			}
			catch (ClassCastException exc) {
				// ignore
			}
		}
		else if (command.startsWith(JFilterLine.kMoveUpActionCommand)) {
			fAdjusting = true;
			int filterIndex = Integer.parseInt(command.substring(command.indexOf(":") + 1));

			// swap filter with the previous one
			fFilterLines[filterIndex].swapWith(fFilterLines[filterIndex - 1]);
			AFilter tempFilter = fFilterChain[filterIndex];
			fFilterChain[filterIndex] = fFilterChain[filterIndex - 1];
			fFilterChain[filterIndex - 1] = tempFilter;

			adjustMoveButtons();
			autoProof();
			fAdjusting = false;
		}
		else if (command.startsWith(JFilterLine.kMoveDownActionCommand)) {
			fAdjusting = true;
			int filterIndex = Integer.parseInt(command.substring(command.indexOf(":") + 1));

			// swap filter with the next one
			fFilterLines[filterIndex].swapWith(fFilterLines[filterIndex + 1]);
			AFilter tempFilter = fFilterChain[filterIndex];
			fFilterChain[filterIndex] = fFilterChain[filterIndex + 1];
			fFilterChain[filterIndex + 1] = tempFilter;

			adjustMoveButtons();
			autoProof();
			fAdjusting = false;
		}
		else if (e.getSource() == fAddFilterButton) {
			if (fNrOfFilters < kNrOfAvailableFilters) {
				fFilterChain[fNrOfFilters] = new BlurFilter();
				fFilterLines[fNrOfFilters].setFilter(fFilterChain[fNrOfFilters]);
				fFilterLines[fNrOfFilters].enable();
				++fNrOfFilters;
				adjustMoveButtons();
			}
			fAddFilterButton.setEnabled(fNrOfFilters < kNrOfAvailableFilters);
			autoProof();
		}
		else if (e.getSource() == fAutoProofCheckBox) {
			autoProof();
		}
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
		fFractalPanel = (FractalPanel) parameters[0];
		fIteratorController = (IteratorController) parameters[1];

		FilterChain originalFilterChain = fIteratorController.getColoringParameters().fPostProcessingFilterChain;
		fBackupFilterChain = originalFilterChain.clone();
		fBackupUsePostProcessingFilters = fIteratorController.getColoringParameters().fUsePostProcessingFilters;

		fFilterChain = new AFilter[kNrOfAvailableFilters];
		for (fNrOfFilters = 0; fNrOfFilters < originalFilterChain.size(); ++fNrOfFilters) {
			AFilter filter = originalFilterChain.getFilter(fNrOfFilters);
			fFilterChain[fNrOfFilters] = filter;
		}
	}

	/**
	 * A callback function for when the cancel-button is selected.
	 */
	@Override
	protected void cancelSelected()
	{
		fIteratorController.getColoringParameters().fPostProcessingFilterChain = fBackupFilterChain;
		fIteratorController.getColoringParameters().fUsePostProcessingFilters = fBackupUsePostProcessingFilters;
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Filters.PostProcessingFilterSetupChooserTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			JPanel filterPanel = new JPanel();
			filterPanel.setLayout(new SpringLayout());

			fFilterLines = new JFilterLine[kNrOfAvailableFilters];
			for (int filterIndex = 0; filterIndex < kNrOfAvailableFilters; ++filterIndex) {
				JFilterLine filterLine = new JFilterLine(filterIndex,filterPanel,this);
				if (filterIndex < fNrOfFilters) {
					filterLine.setFilter(fFilterChain[filterIndex]);
				}
				else {
					filterLine.disable();
				}
				fFilterLines[filterIndex] = filterLine;
			}
			adjustMoveButtons();

			SpringUtilities.makeCompactGrid(filterPanel,kNrOfAvailableFilters,5,0,0,5,0);
		mainPanel.add(filterPanel);

		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(new JEtchedLine(JEtchedLine.EOrientation.kHorizontal));
		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
				fAddFilterButton = new JButton(I18NL10N.translate("text.Filters.AddFilter"));
				fAddFilterButton.addActionListener(this);
			lowerPanel.add(fAddFilterButton);
			lowerPanel.add(Box.createHorizontalGlue());

				fAutoProofCheckBox = new JCheckBox(I18NL10N.translate("text.Filters.AutoProof"));
				fAutoProofCheckBox.setSelected(true);
				fAutoProofCheckBox.addActionListener(this);
			lowerPanel.add(fAutoProofCheckBox);
		mainPanel.add(lowerPanel);

		mainPanel.add(Box.createVerticalStrut(10));

		autoProof();
	}

	/*******************
	 * PRIVATE METHODS *
	 *******************/

	/**
	 * Adjusts all the filter lines' move buttons.
	 */
	private void adjustMoveButtons()
	{
		for (JFilterLine filterLine : fFilterLines) {
			filterLine.adjustMoveButtons();
		}
	}

	/**
	 * Auto proofs the current filter chain.
	 */
	private void autoProof()
	{
		if (fAutoProofCheckBox == null) {
			return;
		}

		if (fAutoProofCheckBox.isSelected()) {
			fIteratorController.getColoringParameters().fPostProcessingFilterChain = getSelectedFilterChain();
			fIteratorController.getColoringParameters().fUsePostProcessingFilters = true;
		}
		else {
			fIteratorController.getColoringParameters().fUsePostProcessingFilters = false;
		}
		fFractalPanel.applyPostProcessingFilters();
	}

	/*****************
	 * INNER CLASSES *
	 *****************/

	/**
	 * @author  Sven Maerivoet
	 * @version 24/04/2016
	 */
	private final class JFilterLine
	{
		// the action commands for the filter line
		public final static String kActionCommandPrefix = "FilterLine.";
		public final static String kRemoveActionCommand = kActionCommandPrefix + "Remove";
		public final static String kFilterTypeActionCommand = kActionCommandPrefix + "FilterType";
		public final static String kModifyActionCommand = kActionCommandPrefix + "Modify";
		public final static String kMoveUpActionCommand = kActionCommandPrefix + "MoveUp";
		public final static String kMoveDownActionCommand = kActionCommandPrefix + "MoveDown";

		// public datastructures
		public int fFilterIndex;
		public JButton fRemoveButton;
		public JComboBox<String> fFilterTypeComboBox;
		public JButton fModifyButton;
		public JButton fMoveUpButton;
		public JButton fMoveDownButton;

		/****************
		 * CONSTRUCTORS *
		 ****************/

		/**
		 * Constructs a <CODE>JFilterLine</CODE> object.
		 *
		 * @param filterIndex     -
		 * @param panel           -
		 * @param actionListener  -
		 */
		public JFilterLine(int filterIndex, JPanel panel, ActionListener actionListener)
		{
			fFilterIndex = filterIndex;

				fRemoveButton = new JButton("<HTML><B><FONT COLOR=\"red\">X</FONT></B></HTML>");
				fRemoveButton.setActionCommand(kRemoveActionCommand + ":" + String.valueOf(fFilterIndex));
				fRemoveButton.addActionListener(actionListener);
			panel.add(fRemoveButton);

				fFilterTypeComboBox = new JComboBox<String>(kFilterComboBoxData);
				fFilterTypeComboBox.setSelectedIndex(-1);
				fFilterTypeComboBox.setActionCommand(kFilterTypeActionCommand + ":" + String.valueOf(fFilterIndex));
				fFilterTypeComboBox.addActionListener(actionListener);
			panel.add(fFilterTypeComboBox);

				fModifyButton = new JButton(I18NL10N.translate("text.Filters.Modify"));
				fModifyButton.setEnabled(false);
				fModifyButton.setActionCommand(kModifyActionCommand + ":" + String.valueOf(fFilterIndex));
				fModifyButton.addActionListener(actionListener);
			panel.add(fModifyButton);

				fMoveUpButton = new JButton(I18NL10N.translate("text.Filters.MoveUp"));
				fMoveUpButton.setActionCommand(kMoveUpActionCommand + ":" + String.valueOf(fFilterIndex));
				fMoveUpButton.addActionListener(actionListener);
			panel.add(fMoveUpButton);

				fMoveDownButton = new JButton(I18NL10N.translate("text.Filters.MoveDown"));
				fMoveDownButton.setActionCommand(kMoveDownActionCommand + ":" + String.valueOf(fFilterIndex));
				fMoveDownButton.addActionListener(actionListener);
			panel.add(fMoveDownButton);
		}

		/******************
		 * PUBLIC METHODS *
		 ******************/

		/**
		 * Makes the filter line compliant with the specified filter.
		 *
		 * @param filter  the filter
		 */
		public void setFilter(AFilter filter)
		{
			String filterName = filter.getName();

			if (filterName.equalsIgnoreCase((new BlurFilter()).getName())) {
				fFilterTypeComboBox.setSelectedIndex(kBlurFilterIndex);
				fModifyButton.setEnabled(true);
			}
			else if (filterName.equalsIgnoreCase((new EdgeFilter()).getName())) {
				fFilterTypeComboBox.setSelectedIndex(kEdgeFilterIndex);
				fModifyButton.setEnabled(true);
			}
			else if (filterName.equalsIgnoreCase((new InvertFilter()).getName())) {
				fFilterTypeComboBox.setSelectedIndex(kInvertFilterIndex);
				fModifyButton.setEnabled(false);
			}
			else if (filterName.equalsIgnoreCase((new PosteriseFilter()).getName())) {
				fFilterTypeComboBox.setSelectedIndex(kPosteriseFilterIndex);
				fModifyButton.setEnabled(false);
			}
			else if (filterName.equalsIgnoreCase((new SharpenFilter()).getName())) {
				fFilterTypeComboBox.setSelectedIndex(kSharpenFilterIndex);
				fModifyButton.setEnabled(false);
			}
		}

		/**
		 * Enables the filter line.
		 */
		public void enable()
		{
			fRemoveButton.setEnabled(true);
			fFilterTypeComboBox.setEnabled(true);
			fMoveUpButton.setEnabled(true);
			fMoveDownButton.setEnabled(true);
		}

		/**
		 * Disables the filter line.
		 */
		public void disable()
		{
			fRemoveButton.setEnabled(false);
			fFilterTypeComboBox.setEnabled(false);
			fFilterTypeComboBox.setSelectedIndex(-1);
			fModifyButton.setEnabled(false);
			fMoveUpButton.setEnabled(false);
			fMoveDownButton.setEnabled(false);
		}

		/**
		 * Adjusts the move buttons depending on the location of the filter line.
		 */
		public void adjustMoveButtons()
		{
			if ((fNrOfFilters == 1) || (fFilterIndex > (fNrOfFilters - 1))) {
				fMoveUpButton.setEnabled(false);
				fMoveDownButton.setEnabled(false);
			}
			else if (fFilterIndex == 0) {
				// set as first
				fMoveUpButton.setEnabled(false);
				fMoveDownButton.setEnabled(true);
			}
			else if (fFilterIndex < (fNrOfFilters - 1)) {
				// set as intermediate
				fMoveUpButton.setEnabled(true);
				fMoveDownButton.setEnabled(true);
			}
			else {
				// set as last
				fMoveUpButton.setEnabled(true);
				fMoveDownButton.setEnabled(false);
			}
		}

		/**
		 * Copies the filter line's state to the specified filter line.
		 */
		public void copyStateTo(JFilterLine filterLine)
		{
			filterLine.fRemoveButton.setEnabled(fRemoveButton.isEnabled());
			filterLine.fFilterTypeComboBox.setEnabled(fFilterTypeComboBox.isEnabled());
			filterLine.fFilterTypeComboBox.setSelectedIndex(fFilterTypeComboBox.getSelectedIndex());
			filterLine.fModifyButton.setEnabled(fModifyButton.isEnabled());
			filterLine.fMoveUpButton.setEnabled(fMoveUpButton.isEnabled());
			filterLine.fMoveDownButton.setEnabled(fMoveDownButton.isEnabled());
		}

		/**
		 * Swaps the filter line's state to with the specified filter line.
		 */
		public void swapWith(JFilterLine filterLine)
		{
			boolean[] tempStates = {filterLine.fRemoveButton.isEnabled(), filterLine.fFilterTypeComboBox.isEnabled(), filterLine.fModifyButton.isEnabled(), filterLine.fMoveUpButton.isEnabled(), filterLine.fMoveDownButton.isEnabled()};
			int tempIndex = filterLine.fFilterTypeComboBox.getSelectedIndex();

			filterLine.fRemoveButton.setEnabled(fRemoveButton.isEnabled());
			filterLine.fFilterTypeComboBox.setEnabled(fFilterTypeComboBox.isEnabled());
			filterLine.fFilterTypeComboBox.setSelectedIndex(fFilterTypeComboBox.getSelectedIndex());
			filterLine.fModifyButton.setEnabled(fModifyButton.isEnabled());
			filterLine.fMoveUpButton.setEnabled(fMoveUpButton.isEnabled());
			filterLine.fMoveDownButton.setEnabled(fMoveDownButton.isEnabled());

			fRemoveButton.setEnabled(tempStates[0]);
			fFilterTypeComboBox.setEnabled(tempStates[1]);
			fFilterTypeComboBox.setSelectedIndex(tempIndex);
			fModifyButton.setEnabled(tempStates[2]);
			fMoveUpButton.setEnabled(tempStates[3]);
			fMoveDownButton.setEnabled(tempStates[3]);
		}
	}
}
