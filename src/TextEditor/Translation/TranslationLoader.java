package TextEditor.Translation;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Scanner;
import java.io.PrintWriter;
import TextEditor.TextEditor;
import TextEditor.Errors.ErrorPaneBridge;
import TextEditor.Logger.Logger;

public class TranslationLoader {

	private final static String SEPARATOR = "="; 

	public static boolean parse(String selectedLanguage) {

		String[] splitLine;
		final String filePrefix = "/strings/strings_";
		String systemLanguage = "";

		if(selectedLanguage.equals("sys")) {
			systemLanguage = System.getProperty("user.language");
		} else {
			systemLanguage = selectedLanguage;
		}

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

				ErrorPaneBridge.setErrorPane("<html>Unable to find both system (" + localLanguageFileName + ") and fallback (" + fallbackLanguageFileName + ") translation files.<br/>" + 
						"Maybe the application executable file is corrupted, try downloading it again from <a href=\"" + url + "\">GitHub</a></html>", url);


				TextEditor.showErrorDialog("Missing translations", ErrorPaneBridge.getErrorPane());

				return false;
			}
		} catch (Exception e) {
			StringWriter stringWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(stringWriter));
			Logger.writeLog(e);

			final String url = "https://github.com/lucamonte/TextEditor/issues";

			ErrorPaneBridge.setErrorPane("<html>An unexpected error has occurred while initializing the application.<br/><br/>" +
					"Stack trace of the error: <br/>" + stringWriter.toString().replace(System.lineSeparator(), "<br/>") + "<br/>" + 
					"Please, report this on <a href=\"" + url + "\">GitHub</a></html>", url);

			TextEditor.showErrorDialog("Generic error", ErrorPaneBridge.getErrorPane());

			return false;
		}

		if(objscanner != null) {
			while(objscanner.hasNextLine()) {
				splitLine = objscanner.nextLine().split(SEPARATOR, -2);

				TranslationManager.putString(splitLine[0].trim(), splitLine[1].trim());
			} 

			objscanner.close();
		}		

		return true;
	} 
	
	public static String getSeparator() {
		return SEPARATOR;
	}
}