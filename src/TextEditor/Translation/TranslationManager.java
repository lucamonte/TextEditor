package TextEditor.Translation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import TextEditor.TextEditor;
import TextEditor.Config.ConfigurationParser;
import TextEditor.Errors.ErrorPaneBridge;
import TextEditor.Logger.Logger;

public class TranslationManager {

	private final static String SELECTED_LANGUAGE_FILE_PATH = System.getProperty("user.home") + File.separator + "TextEditor_lang.conf";

	public static void loadLanguages() {

		String[] splitLine;
		Scanner objscanner = null;
		InputStream input = TextEditor.class.getResourceAsStream("/config/languages.txt");

		try {
			objscanner = new Scanner(input);
		}catch (NullPointerException npe) {

			ErrorPaneBridge.setErrorPane("<html>Unable to find language list file: the application will not correctly show the language list in the settings</html>");

			TextEditor.showErrorDialog("Missing language list", ErrorPaneBridge.getErrorPane());
		}

		if(objscanner != null) {
			TextEditor.putLanguage("sys", TextEditor.getString("SYSTEM_LANGUAGE"));

			while(objscanner.hasNextLine()) {
				splitLine = objscanner.nextLine().split(ConfigurationParser.getSeparator(), -2);

				TextEditor.putLanguage(splitLine[0].trim(), splitLine[1].trim());
			} 

			objscanner.close();
		}		
	}

	public static String getSelectedLanguage() {

		String selectedLanguage = "sys";
		BufferedReader objreader = null;

		File selectedLanguageFile = new File(SELECTED_LANGUAGE_FILE_PATH);

		if(selectedLanguageFile.exists()) {

			try {
				objreader = new BufferedReader(new FileReader(SELECTED_LANGUAGE_FILE_PATH));
				selectedLanguage = objreader.readLine().trim();
				objreader.close();
			} catch (Exception e) {
				Logger.writeLog(e);
			}
		} else {
			setSelectedLanguage(selectedLanguage);
		}

		return selectedLanguage;
	}

	public static boolean setSelectedLanguage(String languageCode) {

		File selectedLanguageFile = new File(SELECTED_LANGUAGE_FILE_PATH);
		BufferedWriter objwriter = null;

		try {
			if(!selectedLanguageFile.exists()) {
				selectedLanguageFile.createNewFile();
			}

			try {
				objwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(SELECTED_LANGUAGE_FILE_PATH), StandardCharsets.UTF_8));
				objwriter.write(languageCode);
			} catch (Exception e) {
				Logger.writeLog(e);
				return false;
			} finally {
				objwriter.flush();
				objwriter.close();
			}
		} catch (Exception e) {
			Logger.writeLog(e);
		}

		return true;
	}

}
