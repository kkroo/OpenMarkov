package org.openmarkov.core.gui.menutoolbar.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.NumberFormat;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.window.MainPanel;
import org.openmarkov.core.gui.window.MainPanelListenerAssistant;
import org.openmarkov.core.model.network.ProbNet;

public class LookAheadToolBar extends ToolBarBasic implements MouseMotionListener, DocumentListener {

	//ideas: 1. use JFormattedTextField 2. add a int and a boolean field in VisualNetwork for lookahead
	private IntTextField lookAheadSteps;
	private JLabel stepsUneditableText;
	private JButton lookAheadButton  = null;
	private JButton resetButton = null;
	private JButton applyEditButton = null;
	private IconLoader iconLoader = null;
	
	public LookAheadToolBar(ActionListener newListener) {
		super(newListener);
		initialize();
	}
	
	private void initialize() {
		iconLoader = new IconLoader ();
		//Why this text field is not showing up???
		//lookAheadSteps = new JFormattedTextField(5);
//		PlainDocument doc = (PlainDocument) lookAheadSteps.getDocument();
//	      doc.setDocumentFilter(new MyIntFilter());
	    // If you want the value to be committed on each keystroke instead of focus lost
	    //formatter.setCommitsOnValidEdit(true);
	    lookAheadSteps = new IntTextField(0,3);
	    lookAheadSteps.getDocument().addDocumentListener(this);
		//lookAheadSteps.addActionListener(listener);
		stepsUneditableText = new JLabel("steps");
		add(lookAheadSteps);
		add(stepsUneditableText);
		addSeparator ();
		add (getLookAheadButton());
		add (getResetButton());
		add (getApplyEditButton());
	}
	
	private JButton getResetButton() {
		if (resetButton == null) {
			resetButton = new JButton("Reset");
			//resetButton.setIcon();
			resetButton.setFocusable(false);
			resetButton.setActionCommand(ActionCommands.LOOK_AHEAD_RESET);
            //resetButton.setToolTipText (stringDatabase.getString (ActionCommands.CLIPBOARD_COPY
            //        + STRING_TOOLTIP_SUFFIX));
			resetButton.addActionListener (listener);
			resetButton.addMouseMotionListener (this);
		}
		return resetButton;
	}

	private JButton getLookAheadButton() {
		if (lookAheadButton == null) {
			lookAheadButton = new JButton("Look Ahead");
			//lookAheadButton.setIcon (iconLoader.load (IconLoader.ICON_COPY_ENABLED));
			lookAheadButton.setFocusable (false);
			lookAheadButton.setActionCommand (ActionCommands.LOOK_AHEAD);
			lookAheadButton.setToolTipText (stringDatabase.getString (ActionCommands.CLIPBOARD_COPY
                                                                 + STRING_TOOLTIP_SUFFIX));
			lookAheadButton.addActionListener (listener);
			lookAheadButton.addMouseMotionListener (this);
		}
		return lookAheadButton;
	}
	
	private JButton getApplyEditButton() {
		if (applyEditButton == null) {
			applyEditButton = new JButton ("Apply Edits");
            //applyEditButton.setIcon (iconLoader.load (IconLoader.ICON_COPY_ENABLED));
            applyEditButton.setFocusable (false);
            applyEditButton.setActionCommand (ActionCommands.LOOK_AHEAD_APPLY_EDIT);
            applyEditButton.setToolTipText (stringDatabase.getString (ActionCommands.CLIPBOARD_COPY
                                                                 + STRING_TOOLTIP_SUFFIX));
            applyEditButton.addActionListener (listener);
            applyEditButton.addMouseMotionListener (this);
		}
		return applyEditButton;
	}
	

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected JComponent getJComponentActionCommand(String actionCommand) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public void actionPerformed(ActionEvent e) {
//		// TODO Auto-generated method stub
//		String text = lookAheadSteps.getText();
//		
//	}
	
	class IntTextField extends JTextField {
		  public IntTextField(int defval, int size) {
		    super("" + defval, size);
		  }

		  protected Document createDefaultModel() {
		    return new IntTextDocument();
		  }

		  public boolean isValid() {
		    try {
		      Integer.parseInt(getText());
		      return true;
		    } catch (NumberFormatException | NullPointerException e) {
		      return false;
		    }
		  }

		  public int getValue() {
		    try {
		      return Integer.parseInt(getText());
		    } catch (NumberFormatException e) {
		      return 0;
		    }
		  }
		  class IntTextDocument extends PlainDocument {
		    public void insertString(int offs, String str, AttributeSet a)
		        throws BadLocationException {
		      if (str == null)
		        return;
		      String oldString = getText(0, getLength());
		      String newString = oldString.substring(0, offs) + str
		          + oldString.substring(offs);
		      try {
		        Integer.parseInt(newString + "0");
		        super.insertString(offs, str, a);
		      } catch (NumberFormatException e) {
		      }
		    }
		  }
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		setLabel(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		setLabel(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		// update the field in probNet
		
	}
	
	 public void setLabel(DocumentEvent e) {
		    if (lookAheadSteps.isValid() ) {
		      int value = lookAheadSteps.getValue();
		     // label.setText(Integer.toString(value));
		      MainPanelListenerAssistant mpla = MainPanel.getUniqueInstance().getMainPanelListenerAssistant();
		      mpla.getCurrentNetworkPanel().setLookAheadSteps(value);
		      //pb.setLookAheadSteps(value);
		    }
		  }
}
