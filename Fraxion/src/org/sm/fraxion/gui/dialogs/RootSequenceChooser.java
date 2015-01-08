// ----------------------------------------
// Filename      : RootSequenceChooser.java
// Author        : Sven Maerivoet
// Last modified : 02/11/2014
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

import java.awt.event.*;
import javax.swing.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * The <CODE>RootSequenceChooser</CODE> class provides a dialog for selecting a root sequence.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 02/11/2014
 */
public final class RootSequenceChooser extends JDefaultDialog
{
	// specifications of the root sequence input field
	private static final int kInputFieldWidth = 30;

	// internal datastructures
	private RootSequenceTextField fRootSequenceInputField;
	private String fRootSequence;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>RootSequenceChooser</CODE> object.
	 *
	 * @param owner         the owning frame
	 * @param rootSequence  the initial root sequence
	 */
	public RootSequenceChooser(JFrame owner, String rootSequence)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {rootSequence},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the selected root sequence.
	 *
	 * @return the selected root sequence
	 */
	public String getSelectedRootSequence()
	{
		return fRootSequence;
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
		fRootSequence = (String) parameters[0];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.RootSequenceTitle");
	}

	/**
	 */
	@Override
	protected void okSelected()
	{
		fRootSequence = fRootSequenceInputField.getText();
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));

		mainPanel.add(new JLabel(I18NL10N.translate("text.Fractal.RootSequenceLabel") + ": "));
			fRootSequenceInputField = new RootSequenceTextField(fRootSequence,kInputFieldWidth);
		mainPanel.add(fRootSequenceInputField);
	}

	/*****************
	 * INNER CLASSES *
	 *****************/

	/**
	 * @author  Sven Maerivoet
	 * @version 02/11/2014
	 */
	private final class RootSequenceTextField extends JTextField
	{
		/****************
		 * CONSTRUCTORS *
		 ****************/

		/**
		 * @param text     -
		 * @param columns  -
		 */
		public RootSequenceTextField(String text, int columns)
		{
			super(text,columns);
		}

		/******************
		 * PUBLIC METHODS *
		 ******************/

		/**
		 * @param keyEvent  -
		 */
		@Override
		public void processKeyEvent(KeyEvent keyEvent)
		{
			char c = Character.toUpperCase(keyEvent.getKeyChar());
			int keyCode = keyEvent.getKeyCode();

			if ((c == 'A') || (c == 'B') || (c == ' ')) {
				keyEvent.setKeyChar(c);
				super.processKeyEvent(keyEvent);
			}
			else if ((keyCode == KeyEvent.VK_BACK_SPACE) ||
							(keyCode == KeyEvent.VK_TAB) ||
							(keyCode == KeyEvent.VK_HOME) ||
							(keyCode == KeyEvent.VK_END) ||
							(keyCode == KeyEvent.VK_LEFT) ||
							(keyCode == KeyEvent.VK_KP_LEFT) ||
							(keyCode == KeyEvent.VK_RIGHT) ||
							(keyCode == KeyEvent.VK_KP_RIGHT)) {
				super.processKeyEvent(keyEvent);
			}
			else {
				keyEvent.consume();
			}
    }
	}
}
