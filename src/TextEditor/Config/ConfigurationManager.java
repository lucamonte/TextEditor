package TextEditor.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import TextEditor.Logger.Logger;

public class ConfigurationManager {

	private static Properties objproperties = new Properties();
	private final static String PROPERTIES_FILE_PATH = System.getProperty("user.home") + File.separator + "TextEditor.properties";
	private static boolean properties_loaded = false;
	private static Hashtable<String, String> default_properties = new Hashtable<String, String>();

	public static boolean saveDefaultProperties() {
		default_properties.put("default_language", "sys");
		default_properties.put("enable_notifications", "true");
		default_properties.put("preserve_color", "true");
		default_properties.put("preserve_font", "true");

		Enumeration<String> keys = default_properties.keys();

		while(keys.hasMoreElements()) {
			String property = keys.nextElement();

			if (getProperty(property) == null) {
				setProperty(property, default_properties.get(property));
			}
		}

		return writePropertiesFile();
	}

	private static boolean loadPropertiesFile() {
		try {
			objproperties.load(new FileReader(PROPERTIES_FILE_PATH));
			properties_loaded = true;
		} catch (FileNotFoundException ignore) {

		} catch (Exception e) {
			Logger.writeLog(e);
		}

		return properties_loaded;
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