/*
 * Created on Aug 6, 2007 by wyatt
 */
package org.homeunix.thecave.buddi.view.menu.items;

import java.awt.event.ActionEvent;

import org.homeunix.thecave.buddi.i18n.keys.MenuKeys;
import org.homeunix.thecave.buddi.model.Account;
import org.homeunix.thecave.buddi.model.Document;
import org.homeunix.thecave.buddi.model.prefs.PrefsModel;
import org.homeunix.thecave.buddi.plugin.api.exception.InvalidValueException;
import org.homeunix.thecave.buddi.plugin.api.exception.ModelException;
import org.homeunix.thecave.buddi.view.MainFrame;
import org.homeunix.thecave.moss.swing.MossFrame;
import org.homeunix.thecave.moss.swing.MossMenuItem;
import org.homeunix.thecave.moss.util.Log;

public class EditDeleteAccount extends MossMenuItem {
	public static final long serialVersionUID = 0;

	public EditDeleteAccount(MossFrame frame) {
		super(frame, PrefsModel.getInstance().getTranslator().get(MenuKeys.MENU_EDIT_DELETE_ACCOUNT));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!(getFrame() instanceof MainFrame))
			throw new RuntimeException("Calling frame not instance of AccountFrame");
			
		for (Account a : ((MainFrame) getFrame()).getSelectedAccounts()) {
			try {
				((Document) ((MainFrame) getFrame()).getDocument()).removeAccount(a);
			}
			catch (ModelException me){
				try {
					a.setDeleted(true);
				}
				catch (InvalidValueException ive){
					Log.error("Error setting deleted flag on account");
				}
			}
		}

		((MainFrame) getFrame()).updateContent();
		((MainFrame) getFrame()).fireStructureChanged();
	}
	
	@Override
	public void updateMenus() {
		super.updateMenus();
		
		this.setEnabled(((MainFrame) getFrame()).getSelectedAccounts().size() > 0);
	}
}
