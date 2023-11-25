package TextEditor.Config;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.io.PrintWriter;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import TextEditor.TextEditor;

public class ConfigurationParser {

	private final static String SEPARATOR = "="; 
	private static JEditorPane errorPane = null;

	public static boolean parse() {

		String[] splitLine;
		final String filePrefix = "/config/strings_";
		final String systemLanguage = System.getProperty("user.language");
		final String fallbackLanguage = "en";
		final String fileExtension = ".txt";
		final String localLanguageFileName = filePrefix + systemLanguage + fileExtension;
		final String fallbackLanguageFileName = filePrefix + fallbackLanguage + fileExtension;
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
				final String url = "https://github.com/lucamonte/TextEditor/releases";

				setErrorPane("<html>Unable to find both system (" + localLanguageFileName + ") and fallback (" + fallbackLanguageFileName + ") translation files.<br/>" + 
						"Maybe the application executable file is corrupted, try downloading it again from <a href=\"" + url + "\">GitHub</a></html>", url);


				TextEditor.showErrorDialog("Missing translations", errorPane);

				return false;
			}
		} catch (Exception e) {
			StringWriter stringWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(stringWriter));

			final String url = "https://github.com/lucamonte/TextEditor/issues";

			setErrorPane("<html>An unexpected error has occurred while initializing the application.<br/><br/>" +
					"Stack trace of the error: <br/>" + stringWriter.toString().replace(System.lineSeparator(), "<br/>") + "<br/>" + 
					"Please, report this on <a href=\"" + url + "\">GitHub</a></html>", url);

			TextEditor.showErrorDialog("Generic error", errorPane);

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

	private static void setErrorPane(String text, String url) {
		errorPane = new JEditorPane();
		errorPane.setContentType("text/html");
		errorPane.setText(text);
		errorPane.setOpaque(false);
		errorPane.setEditable(false);
		errorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

		errorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if(event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {

						Desktop.getDesktop().browse(new URI(url));

					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}