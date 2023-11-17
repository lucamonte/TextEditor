package TextEditor;

import java.io.InputStream;
import java.util.Scanner;

public class Config extends TextEditor {

	private final static String SEPARATOR = "="; 

	protected static void Parse() {

		String[] splitline;

		try {

			InputStream input = TextEditor.class.getResourceAsStream("/config/strings.txt");
			Scanner objscanner = new Scanner(input);

			while(objscanner.hasNextLine()) {
				splitline = objscanner.nextLine().split(SEPARATOR, -2);
				strings.put(splitline[0].trim(), splitline[1].trim());
			}

			objscanner.close();

		} catch (Exception exception) {
			if(exception.getClass().getSimpleName().equals("FileNotFoundException")) {
				System.out.println("File di configurazione non trovato!");
			} else {
				exception.printStackTrace();
			}
		}
	}
}
