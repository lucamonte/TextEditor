package TextEditor.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

public class Logger {

	private final static String LOG_FILE_PATH = System.getProperty("user.home") + File.separator + "TextEditor.log";

	public static void writeLog(Exception ex) {
		PrintStream objprint = null;
		File logfile = new File(LOG_FILE_PATH);

		try {
			if(!logfile.exists()) {
				logfile.createNewFile();
			}

			objprint = new PrintStream(new FileOutputStream(logfile, true));

			String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

			//Append timestamp and write stack trace to file
			objprint.print("[" + timestamp + "]: ");
			ex.printStackTrace(objprint);
			objprint.print(System.lineSeparator());

			//Write stack trace to console (for development purposes)
			ex.printStackTrace();

		} catch (Exception e) {
			/* 	In case an exception is thrown while creating the log file,
			 *  print both the stack traces: the one that has originated while creating the file
			 *  and the one that has originated with the exception passed in as a parameter */
			e.printStackTrace();
			ex.printStackTrace();
		} finally {
			objprint.close();
		}
	}
}