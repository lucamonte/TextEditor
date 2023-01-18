package TextEditor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Scanner;

public class Config extends TextEditor {

	private final static String SEPARATOR = "=";
	private final static String FILE_PATH = applicationpath + "strings.txt";

	public static void Parse() {

		String[] splitline;

		try {

			Scanner objscanner = new Scanner(new File(FILE_PATH));

			while(objscanner.hasNextLine()) {
				splitline = objscanner.nextLine().split(SEPARATOR, -2);
				strings.put(splitline[0].trim(), splitline[1].trim());
			}

			objscanner.close();

		} catch (Exception exception) {
			if(exception.getClass().getSimpleName().equals("FileNotFoundException")) {
				System.out.println("File di configurazione non trovato!");
				System.out.println("Generazione file di configurazione predefinito in " + FILE_PATH + "...");
				if(WriteDefault()) {
					Parse();
					System.out.println("File di configurazione generato correttamente!");
				} 
			} else {
				exception.printStackTrace();
			}
		}

	}

	public static boolean WriteDefault() {

		String key;
		boolean success = true;

		SetDefaultValues();

		File objfile = new File(FILE_PATH);

		try {
			BufferedWriter objwriter = new BufferedWriter(new FileWriter(FILE_PATH));

			if(!objfile.exists()) {
				objfile.createNewFile();
			} 

			Enumeration<String> keys = strings.keys();

			while (keys.hasMoreElements()) {
				key = keys.nextElement();
				objwriter.write(key + SEPARATOR + strings.get(key));
				objwriter.newLine();
			}

			objwriter.flush();
			objwriter.close();

		} catch (Exception exception) {
			exception.printStackTrace();
			success = false;
		}

		return success;
	}

	public static void SetDefaultValues() {

		strings.put("WINDOW_NAME", "Blocco note");
		strings.put("FILE_MENU", "File");
		strings.put("TEXT_MENU", "Testo");
		strings.put("SAVE_AS", "Salva con nome");
		strings.put("DELETE_ALL", "Cancella tutto");
		strings.put("OPEN_FILE", "Apri");
		strings.put("SAVE_FILE", "Salva");
		strings.put("NEW_FILE", "Nuovo");
		strings.put("CLOSE_EDITOR", "Chiudi");
		strings.put("SELECT_ALL", "Seleziona tutto");
		strings.put("COPY", "Copia");
		strings.put("CUT", "Taglia");
		strings.put("PASTE", "Incolla");
		strings.put("DELETE_FILE", "Elimina");
		strings.put("WARNING", "Attenzione");
		strings.put("SAVE_BEFORE_CONTINUE", "Salvare il file prima di proseguire?");
		strings.put("SAVE_BEFORE_EXIT", "Salvare il file prima di uscire?");
		strings.put("DELETE_CONFIRMATION", "Eliminare il file?");
		strings.put("TXT_FILE_EXTENSION_DESCRIPTION", "File TXT");

	}

}
