package org.openmarkov.core.gui.window.dt;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.plaf.basic.BasicTreeUI;

public class DecisionTreeUI extends BasicTreeUI {

	protected java.awt.event.MouseListener createMouseListener() {
		return new ZoomMouseListener(super.createMouseListener());
	}	
	
	public class ZoomMouseListener extends MouseAdapter {

		MouseListener parentMouseListener;
		
		public ZoomMouseListener(MouseListener ml) {
			parentMouseListener = ml;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			Double zoom = ((DecisionTree)tree).getZoom();
			int newX = (int)(e.getX() / zoom);
    		int newY = (int)(e.getY() / zoom);
    		e.translatePoint(newX - e.getX(), newY - e.getY()); 
			parentMouseListener.mousePressed(e);
		}
	}	
}
