package TextEditor;

import java.io.InputStream;
import java.util.Scanner;

public class Config {

	private final static String SEPARATOR = "="; 

	protected static void parse() {

		String[] splitline;
		InputStream input = TextEditor.class.getResourceAsStream("/config/strings.txt");
		Scanner objscanner = new Scanner(input);

		try {
			while(objscanner.hasNextLine()) {
				splitline = objscanner.nextLine().split(SEPARATOR, -2);
				TextEditor.putString(splitline[0].trim(), splitline[1].trim());
			}
		} catch (Exception exception) {
			if(exception.getClass().getSimpleName().equals("FileNotFoundException")) {
				System.out.println("File di configurazione non trovato!");
			} else {
				exception.printStackTrace();
			}
		} finally {
			objscanner.close();
		}
	}
}