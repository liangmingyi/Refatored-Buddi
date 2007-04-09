/*
 * Created on Apr 8, 2007 by wyatt
 */
package org.homeunix.drummer.controller.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.homeunix.drummer.controller.TranslateKeys;
import org.homeunix.drummer.view.MainFrame;
import org.homeunix.thecave.moss.gui.abstractwindows.AbstractFrame;
import org.homeunix.thecave.moss.util.Log;

public class WindowMenuController implements ActionListener {
	public final static long serialVersionUID = 0;
	private AbstractFrame frame;

	public WindowMenuController(AbstractFrame frame){
		this.frame = frame;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(TranslateKeys.MINIMIZE.toString())){
			if (frame != null)
				frame.setExtendedState(JFrame.ICONIFIED);
		}
		else if (e.getActionCommand().equals(TranslateKeys.MAIN_BUDDI_WINDOW.toString())){
			if (!MainFrame.getInstance().isVisible())
				MainFrame.getInstance().setVisible(true);
		}
		else {
			Log.debug("Clicked " + e.getActionCommand());
		}
	}
}