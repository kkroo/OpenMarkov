/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window.message;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.window.mdi.FrameContentPanel;




/**
 * Class which manages a window where message texts of the application will
 * appear.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo - externalize constants and set position for window
 * @version 1.2 jlgozalo - adding get/set to normal and error streams (to avoid System.out and System.err in source code as much as possible)
 */
public class MessageWindow extends FrameContentPanel implements ActionListener {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 4878811677752222720L;

	/**
	 * Scroll pane for the text area.
	 */
	private JScrollPane scrollPane = null;

	/**
	 * Area where the messages will be written.
	 */
	private NonEditableTextArea textArea = null;

	/**
	 * Panel that contains the buttons panel.
	 */
	private JPanel topPanel = null;

	/**
	 * Panel that contains the buttons.
	 */
	private JPanel buttonsPanel = null;

	/**
	 * Button to clear the text area.
	 */
	private JButton buttonClear = null;

	/**
	 * Button to copy the text of the area to the clipboard.
	 */
	private JButton buttonCopy = null;

	/**
	 * Object used to substitute the standard output.
	 */
	private StandardStream normalMessageStream = null;

	/**
	 * Object used to substitute the standard error.
	 */
	private StandardStream errorMessageStream = null;
	/**
	 * convenience variable to store the owner Frame
	 */
	private JFrame ownerFrame = null;
	
	/**
	 * String database
	 */
	StringDatabase stringDatabase = StringDatabase.getUniqueInstance ();

	/**
	 * This is the default constructor
	 * @param owner - frame where this message window is associated
	 */
	public MessageWindow(JFrame owner) {

		ownerFrame = owner;
		initialize();

	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		int x = ownerFrame.getX();
		int y = ownerFrame.getY() + ownerFrame.getHeight() * 5/6;
		int width = ownerFrame.getWidth();
		
        int height = ownerFrame.getHeight()/6;
			
		this.setBounds(x, y, width, height);
		this.setLayout(new BorderLayout());
        this.add(getTopPanel(), BorderLayout.NORTH);
        this.add(getScrollPane(), BorderLayout.CENTER);
        
		normalMessageStream = new StandardStreamOut(textArea);
		errorMessageStream = new StandardStreamErr(textArea);
		System.setOut(normalMessageStream);
		System.setErr(errorMessageStream);
	}

	/**
	 * This method initialises scrollPane.
	 * 
	 * @return a new scroll pane.
	 */
	private JScrollPane getScrollPane() {

		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTextArea());
		}
		return scrollPane;
	}

	/**
	 * This method initialises textArea.
	 * 
	 * @return a new text area.
	 */
	private NonEditableTextArea getTextArea() {

		if (textArea == null) {
			textArea = new NonEditableTextArea();
		}
		return textArea;
	}

	/**
	 * This method initialises topPanel.
	 * 
	 * @return a new top panel.
	 */
	private JPanel getTopPanel() {

		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			topPanel.add(getButtonsPanel());
		}
		return topPanel;
	}

	/**
	 * This method initialises buttonsPanel.
	 * 
	 * @return a new buttons panel.
	 */
	private JPanel getButtonsPanel() {

		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new GridLayout(1, 0, 10, 10));
			buttonsPanel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
			buttonsPanel.add(getButtonCopy());
			buttonsPanel.add(getButtonClear());
		}
		return buttonsPanel;
	}

	/**
	 * This method initialises buttonClear.
	 * 
	 * @return a new button to clear.
	 */
	private JButton getButtonClear() {

		if (buttonClear == null) {
			buttonClear = new JButton();
			buttonClear.setName("Clear");
			buttonClear.setText(stringDatabase
				.getString("Clear.Text.Label"));
			buttonClear.setMnemonic(stringDatabase.getString(
				"Clear.Text.Mnemonic").charAt(0));
			buttonClear.setFocusable(false);
			buttonClear.addActionListener(this);
		}
		return buttonClear;
	}

	/**
	 * This method initialises buttonCopy.
	 * 
	 * @return a new button to copy to the clipboard.
	 */
	private JButton getButtonCopy() {

		if (buttonCopy == null) {
			buttonCopy = new JButton();
			buttonCopy.setName("Copy");
			buttonCopy.setText(stringDatabase
				.getString("Copy.Text.Label"));
			buttonCopy.setMnemonic(stringDatabase.getString(
				"Copy.Text.Mnemonic").charAt(0));
			buttonCopy.setFocusable(false);
			buttonCopy.addActionListener(this);
		}
		return buttonCopy;
	}

	/**
	 * Invoked when an action occurs.
	 * 
	 * @param e
	 *            event information.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(buttonClear)) {
			textArea.setText("");
		} else if (e.getSource().equals(buttonCopy)) {
			JTextArea tmp = new JTextArea(textArea.getText());
			tmp.selectAll();
			tmp.copy();
		}
	}

	
	public StandardStream getNormalMessageStream() {
	
		return normalMessageStream;
	}

	
	public void setNormalMessageStream(StandardStream normalMessageStream) {
	
		this.normalMessageStream = normalMessageStream;
	}

	
	public StandardStream getErrorMessageStream() {
	
		return errorMessageStream;
	}

	
	public void setErrorMessageStream(StandardStream errorMessageStream) {
	
		this.errorMessageStream = errorMessageStream;
	}

    @Override
    public String getTitle ()
    {
        return stringDatabase.getString("MessageWindow.Title.Label");
    }

    @Override
    public void close ()
    {
        setVisible (false);
    }

    @Override
    public double getZoom ()
    {
        // Ignore
        return 0;
    }

    @Override
    public void setZoom (double zoom)
    {
        // Ignore
    }
}
