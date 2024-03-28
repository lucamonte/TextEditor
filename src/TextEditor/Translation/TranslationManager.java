package TextEditor.Translation;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import TextEditor.TextEditor;
import TextEditor.Errors.ErrorPaneBridge;
import TextEditor.Config.ConfigurationManager;

public class TranslationManager {
	private static Hashtable<String, String> strings = new Hashtable<String, String>();
	private static Hashtable<String, String> languages = new Hashtable<String, String>();

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
			putLanguage("sys", getString("SYSTEM_LANGUAGE"));

			while(objscanner.hasNextLine()) {
				splitLine = objscanner.nextLine().split(TranslationLoader.getSeparator(), -2);

				putLanguage(splitLine[0].trim(), splitLine[1].trim());
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
	
	public static void putLanguage(String languageCode, String language) {
		languages.put(languageCode, language);
	}
	
	public static String getLanguage(String languageCode) {
		return languages.get(languageCode);
	}
	
	public static Enumeration<String> getLanguageKeys() {
		return languages.keys();
	}
	
	public static void putString(String key, String string) {
		strings.put(key, string);
	}
	
	public static String getString(String key) {
		return strings.get(key);
	}
	
	public static void clearStrings() {
		strings.clear();
	}
}
