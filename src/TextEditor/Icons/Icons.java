package TextEditor.Icons;

import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import TextEditor.TextEditor;

public class Icons {

	public enum IconTypes {
		APPLICATION,
		COPY,
		CUT,
		DELETE,
		DELETE_ALL,
		DELETE_FILE,
		EXIT,
		NEW_FILE,
		OPEN_FILE,
		PASTE,
		PRINT,
		REDO,
		SAVE,
		SAVE_AS,
		SELECT_ALL,
		SELECT_COLOR,
		SELECT_FONT,
		SELECT_LANGUAGE,
		SELECTED,
		UNDO
	}

	private static Image getResource(String resource) {
		return Toolkit.getDefaultToolkit().getImage(TextEditor.class.getResource(resource));
	}

	public static Image getImage(IconTypes icon) {
		return getResource("/images/" + icon.name().toLowerCase() + ".png");
	}

	public static ImageIcon getImageIcon(IconTypes icon) {
		return new ImageIcon(getImage(icon));
	}
}