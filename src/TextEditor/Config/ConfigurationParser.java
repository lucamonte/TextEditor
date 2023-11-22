package TextEditor.Config;

import java.io.InputStream;
import java.util.Scanner;

import TextEditor.TextEditor;

public class ConfigurationParser {

	private final static String SEPARATOR = "="; 

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
				TextEditor.showMissingTranslationsError("Missing translations",
						"Unable to find both system (" + localLanguageFileName + ") and fallback (" + fallbackLanguageFileName + ") translation files");

				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();

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
}