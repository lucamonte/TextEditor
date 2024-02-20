package TextEditor.Config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import TextEditor.Logger.Logger;

public class ConfigurationManager {

	private static Properties objproperties = new Properties();
	private final static String PROPERTIES_FILE_PATH = System.getProperty("user.home") + File.separator + "TextEditor.properties";
	private static boolean properties_loaded = false;

	private static boolean saveDefaultProperties() {
		objproperties.setProperty("default_language", "sys");
		objproperties.setProperty("enable_notifications", "true");

		return writePropertiesFile();
	}

	private static boolean loadPropertiesFile() {
		try {
			if(!new File(PROPERTIES_FILE_PATH).exists()) {
				saveDefaultProperties();
			}

			objproperties.load(new FileReader(PROPERTIES_FILE_PATH));
			properties_loaded = true;
			return true;
		} catch (IOException e) {
			Logger.writeLog(e);
			return false;
		}
	}

	private static boolean writePropertiesFile() {
		try {
			objproperties.store(new FileWriter(PROPERTIES_FILE_PATH), "TextEditor configuration file");
			return true;
		} catch (IOException e) {
			Logger.writeLog(e);
			return false;
		}
	}

	public static String getProperty(String property) {
		if(!properties_loaded) {
			properties_loaded = loadPropertiesFile();
		}

		return objproperties.getProperty(property);
	}

	public static boolean setProperty(String property, String value) {
		objproperties.put(property, value);

		return writePropertiesFile();
	}
}