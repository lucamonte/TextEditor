package TextEditor.Translation;

import java.io.InputStream;
import java.util.Scanner;
import TextEditor.TextEditor;
import TextEditor.Errors.ErrorPaneBridge;
import TextEditor.Config.ConfigurationManager;

public class TranslationManager {

	public static void loadLanguages() {

		String[] splitLine;
		Scanner objscanner = null;
		InputStream input = TextEditor.class.getResourceAsStream("/strings/languages.txt");

		try {
			objscanner = new Scanner(input);
		}catch (NullPointerException npe) {

			ErrorPaneBridge.setErrorPane("<html>Unable to find language list file: the application will not correctly show the language list in the settings</html>");

			TextEditor.showErrorDialog("Missing language list", ErrorPaneBridge.getErrorPane());
		}

		if(objscanner != null) {
			TextEditor.putLanguage("sys", TextEditor.getString("SYSTEM_LANGUAGE"));

			while(objscanner.hasNextLine()) {
				splitLine = objscanner.nextLine().split(TranslationLoader.getSeparator(), -2);

				TextEditor.putLanguage(splitLine[0].trim(), splitLine[1].trim());
			} 

			objscanner.close();
		}		
	}

	public static String getSelectedLanguage() {
		return ConfigurationManager.getProperty("default_language");
	}

	public static boolean setSelectedLanguage(String languageCode) {
		return ConfigurationManager.setProperty("default_language", languageCode);
	}
}
