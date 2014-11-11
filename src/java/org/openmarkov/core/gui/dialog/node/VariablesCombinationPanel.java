package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;

@SuppressWarnings("serial")
public class VariablesCombinationPanel extends JPanel{
	
private  ButtonGroup buttonGroup = new ButtonGroup();
	
private ArrayList<JRadioButton> radioButtons = new ArrayList<JRadioButton>();

private ProbNode probNode; 

	public VariablesCombinationPanel(ProbNode probNode) {
		this.probNode = probNode;
		initialize();
		repaint();
	}
	@SuppressWarnings("unchecked")
	public void initialize() {
		 setLayout (new BoxLayout(this, BoxLayout.Y_AXIS));
		// ButtonGroup buttonGroup = new ButtonGroup();
		List<Variable> variables =  probNode.getPotentials().get(0).getVariables();
		List<Variable> possibleVariables = new ArrayList<Variable>();
		for (int i = 1; i < variables.size(); i++) {
			possibleVariables.add(variables.get(i));
		}
			
		List<String> possibilities = new ArrayList<String>();
		int size = possibleVariables.size();
		@SuppressWarnings("rawtypes")
		Vector vector= new Vector();
		for (int i = 0; i < size; i++) {
			vector.add(possibleVariables.get(i).getName());
		}
		String inicio = vector.toString();
		String fin = "";
		int i = size - 1;
		while(!inicio.equals(fin)){
			if(i > 0){
				Object aux = vector.get(i);
				vector.set(i, vector.get(i-1));
				vector.set(i-1, aux);
				i--;
			}
			if(i==0){
				i= size - 1;
			}
			fin = vector.toString();
			possibilities.add(fin);
		} 
		
		
		for (int j = possibilities.size()-1; j >= 0; j--) {
			JRadioButton radioButton = new JRadioButton (possibilities.get(j));
			radioButtons.add(radioButton);
			buttonGroup.add(radioButton);
			add(radioButton, BorderLayout.CENTER);
		}
		
		radioButtons.get(0).setSelected(true);
	}
	
	
	
	
	
	public ButtonGroup getButtonGroup() {
		return buttonGroup;
	}

	public ArrayList<JRadioButton> getRadioButtons() {
		return radioButtons;
	}
}
