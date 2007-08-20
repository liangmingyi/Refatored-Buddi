/*
 * Created on Aug 19, 2007 by wyatt
 */
package org.homeunix.thecave.buddi.view.dialogs;

import org.homeunix.thecave.buddi.i18n.BuddiKeys;
import org.homeunix.thecave.buddi.i18n.keys.ButtonKeys;
import org.homeunix.thecave.buddi.i18n.keys.MessageKeys;
import org.homeunix.thecave.buddi.plugin.api.util.TextFormatter;
import org.homeunix.thecave.moss.swing.dialog.JPasswordInputDialog;

/**
 * Simple class extending JPasswordInputDialog, which sets up the needed translations.
 * 
 * @author wyatt
 *
 */
public class BuddiPasswordDialog extends JPasswordInputDialog {
	public static final long serialVersionUID = 0;
	
	public BuddiPasswordDialog() {
		super(
				TextFormatter.getTranslation(MessageKeys.MESSAGE_ENTER_PASSWORD),
				TextFormatter.getTranslation(MessageKeys.MESSAGE_ENTER_PASSWORD_TITLE),
				TextFormatter.getTranslation(BuddiKeys.HINT_PASSWORD),
				TextFormatter.getTranslation(BuddiKeys.HINT_CONFIRM_PASSWORD),
				TextFormatter.getTranslation(MessageKeys.MESSAGE_ERROR_PASSWORDS_DONT_MATCH),
				TextFormatter.getTranslation(BuddiKeys.ERROR),
				TextFormatter.getTranslation(MessageKeys.MESSAGE_ERROR_NO_PASSWORD_ENTERED),
				TextFormatter.getTranslation(BuddiKeys.ERROR),
				TextFormatter.getTranslation(ButtonKeys.BUTTON_OK),
				TextFormatter.getTranslation(ButtonKeys.BUTTON_CANCEL)
		);
	}
}
