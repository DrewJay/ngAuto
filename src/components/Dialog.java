package components;

import utils.Generals;
import javax.swing.JOptionPane;

public class Dialog {
	
	private String type;
	private String title;
	private String subtitle;
	private int action;
	private String commit;

	
	/**
	 * Construct the dialog object.
	 * 
	 * @param type
	 * @param title
	 * @param subtitle
	 */

	public Dialog(String type, String title, String subtitle) {
		
		this.type = type;
		this.title = title;
		this.subtitle = subtitle;
	}
	

	/**
	 * Set action after dialog click.
	 * 
	 * @param action
	 * @param commit
	 */

	public void setAction(int action, String commit) {

		this.action = action;
		this.commit = commit;
	}

	
	/**
	 * Show dialog and commit action.
	 */
	
	public void show() {
		
		if(this.type == "options") {
			int response = JOptionPane.showOptionDialog(null, this.subtitle, this.title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

			if(response == JOptionPane.OK_OPTION && this.action == 0) {
				Generals.browserify("chrome", commit);
			}
		}
	}
}
