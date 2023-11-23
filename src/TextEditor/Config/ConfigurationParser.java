package TextEditor.Config;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.io.PrintWriter; 
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import TextEditor.TextEditor;

public class ConfigurationParser {

	private final static String SEPARATOR = "="; 
	private static JLabel errorLabel = null;

	public static boolean parse() {

		String[] splitLine;
		String filePrefix = "/config/strings_";
		String systemLanguage = System.getProperty("user.language");
		String fallbackLanguage = "en";
		String fileExtension = ".txt";
		String localLanguageFileName = filePrefix + systemLanguage + fileExtension;
		String fallbackLanguageFileName = filePrefix + fallbackLanguage + fileExtension;
		Scanner objscanner = null;

		InputStream input = TextEditor.class.getResourceAsStream(localLanguageFileName);

		try {
			objscanner = new Scanner(input);
		} catch (NullPointerException npe1) {
			//If the translation file for the current system language is missing, countinue loading the default fallback language (English)
			try {
				input = TextEditor.class.getResourceAsStream(fallbackLanguageFileName);

				objscanner = new Scanner(input);
			} catch (NullPointerException npe2) {
				String url = "https://github.com/lucamonte/TextEditor/releases";

				setLabel("<html>Unable to find both system (" + localLanguageFileName + ") and fallback (" + fallbackLanguageFileName + ") translation files.<br/>" + 
						"Maybe the application executable file is corrupted, try downloading it again from <a href=\"" + url + "\">GitHub</a></html>", url);

				TextEditor.showErrorDialog("Missing translations", errorLabel);

				return false;
			}
		} catch (Exception e) {
			StringWriter stringWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(stringWriter));

			String url = "https://github.com/lucamonte/TextEditor/issues";

			setLabel("<html>An unexpected error has occurred while initializing the application.<br/><br/>" +
					"Stack trace of the error: " + System.lineSeparator() + stringWriter.toString().replace(System.lineSeparator(), "<br/>") + "<br/>" + 
					"Please, report this on <a href=\"" + url + "\">GitHub</a></html>", url);

			TextEditor.showErrorDialog("Generic error", errorLabel);

			return false;
		}

		if(objscanner != null) {
			while(objscanner.hasNextLine()) {
				splitLine = objscanner.nextLine().split(SEPARATOR, -2);
				TextEditor.putString(splitLine[0].trim(), splitLine[1].trim());
			} 

			objscanner.close();
		}		

		return true;
	} 

	private static void setLabel(String text, String url) {
		errorLabel = new JLabel(text);
		errorLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		errorLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {

					Desktop.getDesktop().browse(new URI(url));

				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}