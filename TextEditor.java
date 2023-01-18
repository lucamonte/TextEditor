package TextEditor;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TextEditor {

	private static String openfilepath = "";
	private static JFileChooser filechooser = new JFileChooser();
	private static String oldtext = "";
	private static JFrame frame;
	protected static String applicationpath = new TextEditor().getClass().getClassLoader().getResource("").getPath();
	private static Image icon;
	protected static Hashtable<String, String> strings = new Hashtable<String, String>();

	private static JMenuItem menuitem1;
	private static JMenuItem menuitem2;
	private static JMenuItem menuitem3;
	private static JMenuItem menuitem4;
	private static JMenuItem menuitem5;
	private static JMenuItem menuitem6;
	private static JMenuItem menuitem7;
	private static JMenuItem menuitem8;
	private static JMenuItem menuitem9;
	private static JMenuItem menuitem10;
	private static JMenuItem menuitem11;

	private static JMenuBar menubar;

	private static JMenu menu1;
	private static JMenu menu2;

	private static KeyStroke shortcut_save;
	private static KeyStroke shortcut_new;
	private static KeyStroke shortcut_open;
	private static KeyStroke shortcut_saveas;
	private static KeyStroke shortcut_exit;
	private static KeyStroke shortcut_deleteall;
	private static KeyStroke shortcut_selectall;
	private static KeyStroke shortcut_copy;
	private static KeyStroke shortcut_cut;
	private static KeyStroke shortcut_paste;
	private static KeyStroke shortcut_delete;

	private static JTextArea textarea;
	private static JScrollPane scroll;

	public static void Run() {
		//Generazione della finestra e relativi elementi
		SetupWindow();

		//Gestione degli eventi e della logica applicativa
		SetupBusinessLogic();
	}

	private static void SetupWindow() {
		//Lettura delle stringhe degli elementi dell'interfaccia
		LoadStrings();

		//Impostazione dell'icona dell'applicazione
		SetIcon();

		//Creazione del frame
		CreateFrame();

		//Creazione del menu ed aggiunta degli elementi
		CreateMenu();

		//Creazione delle scorciatoie da tastiera
		CreateKeyStrokes();

		//Aggiunta degli elementi ai menù
		AddMenuItems();

		//Creazione area di testo
		CreateTextArea();

		//Aggiunta dei componenti al frame
		SetupFrame();

		//Impostazione del FileChooser
		SetupFileChooser();
	}

	private static void SetupBusinessLogic() {
		menuitem1.addActionListener(e -> {
			openfilepath = "";
			SaveFile();
		});

		menuitem2.addActionListener(e -> {
			ResetTextArea();
		});	

		menuitem3.addActionListener(e -> {
			boolean openfile = true;

			if(openfilepath.equals("") && !textarea.getText().equals("") || (!openfilepath.equals("") && CheckAsterisk())) {
				int dialog = ShowDialog(strings.get("WARNING"), strings.get("SAVE_BEFORE_CONTINUE"));

				if(dialog == JOptionPane.YES_OPTION) {
					SaveFile();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} else if (dialog == JOptionPane.CLOSED_OPTION) {
					openfile = false;
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				} else {
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}

			if(openfile) {
				OpenFile();
			}

			RestoreCloseBehavior();
		});

		menuitem4.addActionListener(e -> {
			SaveFile();
		});

		menuitem5.addActionListener(e -> {
			boolean newdoc = true;

			if(openfilepath.equals("") && !textarea.getText().equals("") || (!openfilepath.equals("") && CheckAsterisk())) {
				int dialog = ShowDialog(strings.get("WARNING"), strings.get("SAVE_BEFORE_CONTINUE"));

				if(dialog == JOptionPane.YES_OPTION) {
					SaveFile();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} else if (dialog == JOptionPane.CLOSED_OPTION) {
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					newdoc = false;
				} else {
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}

			if(newdoc) {
				NewDocument();
			}

			RestoreCloseBehavior();
		});

		menuitem6.addActionListener(e -> {
			boolean close = true;

			if(openfilepath.equals("") && !textarea.getText().equals("") || (!openfilepath.equals("") && CheckAsterisk())) {
				int dialog = ShowDialog(strings.get("WARNING"), strings.get("SAVE_BEFORE_EXIT"));

				if(dialog == JOptionPane.YES_OPTION) {
					SaveFile();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} else if (dialog == JOptionPane.CLOSED_OPTION) {
					close = false;
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				} else {
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}

			if(close) {
				frame.setVisible(false); //Nascondo il frame
				frame.dispose(); //Distruggo il frame
			}

			RestoreCloseBehavior();
		});

		menuitem7.addActionListener(e -> {
			textarea.selectAll();
		});

		menuitem8.addActionListener(e -> {
			textarea.copy();
		});

		menuitem9.addActionListener(e -> {
			textarea.cut();
		});

		menuitem10.addActionListener(e -> {
			textarea.paste();
		});

		menuitem11.addActionListener(e -> {
			int dialog = ShowDialog(strings.get("WARNING"), strings.get("DELETE_CONFIRMATION"));

			if(dialog == JOptionPane.YES_OPTION) {
				DeleteFile();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			} else if (dialog == JOptionPane.CLOSED_OPTION) {
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			} else {
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}

			RestoreCloseBehavior();
		});

		textarea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				CheckEditing();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				CheckEditing();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				CheckEditing();
			}
		});

		textarea.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				CheckButtons();
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				if(openfilepath.equals("") && !textarea.getText().equals("") || (!openfilepath.equals("") && CheckAsterisk())) {

					int dialog = ShowDialog(strings.get("WARNING"), strings.get("SAVE_BEFORE_EXIT"));

					if(dialog == JOptionPane.YES_OPTION) {
						SaveFile();
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					} else if (dialog == JOptionPane.CLOSED_OPTION) {
						frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					} else {
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					}
				}

				RestoreCloseBehavior();
			}
		});
	}

	private static void SetupFileChooser() {
		filechooser.setAcceptAllFileFilterUsed(false);
		filechooser.addChoosableFileFilter(new FileNameExtensionFilter(strings.get("TXT_FILE_EXTENSION_DESCRIPTION"), "txt"));
	}

	private static void SetupFrame() {
		frame.getContentPane().add(BorderLayout.NORTH, menubar);
		frame.getContentPane().add(BorderLayout.CENTER, scroll);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(icon);
		frame.setVisible(true);
	}

	private static void CreateTextArea() {
		textarea = new JTextArea();
		scroll = new JScrollPane(textarea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		oldtext = textarea.getText();
		textarea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
		textarea.setEditable(true);

		CheckButtons();
	}

	private static void CreateFrame() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
	}

	private static void CreateMenu() {
		menubar = new JMenuBar();

		menubar.add(menu1);
		menubar.add(menu2);
	}

	private static void CreateKeyStrokes() {
		shortcut_save = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK); //CTRL + S per salvare
		shortcut_new = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK); //CTRL + N per creare un nuovo documento
		shortcut_open = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK); //CTRL + O per aprire un documento
		shortcut_saveas = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_MASK); //CTRL + SHIFT + S per salvare con nome
		shortcut_exit = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK); //CTRL + Q per chiudere
		shortcut_deleteall = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK); //CTRL + SHIFT + D per cancellare tutto
		shortcut_selectall = KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK); //CTRL + A per selezionare tutto
		shortcut_copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK); //CTRL + C per copiare
		shortcut_cut = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK); //CTRL + X per tagliare
		shortcut_paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK); //CTRL + V per incollare
		shortcut_delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK); //CTRL + DELETE per eliminare il file

		SetAccelerators();
	}

	private static void SetAccelerators() {
		menuitem1.setAccelerator(shortcut_saveas);
		menuitem2.setAccelerator(shortcut_deleteall);
		menuitem3.setAccelerator(shortcut_open);
		menuitem4.setAccelerator(shortcut_save);
		menuitem5.setAccelerator(shortcut_new);
		menuitem6.setAccelerator(shortcut_exit);
		menuitem7.setAccelerator(shortcut_selectall);
		menuitem8.setAccelerator(shortcut_copy);
		menuitem9.setAccelerator(shortcut_cut);
		menuitem10.setAccelerator(shortcut_paste);
		menuitem11.setAccelerator(shortcut_delete);
	}

	private static void AddMenuItems() {
		menu1.add(menuitem5);
		menu1.add(menuitem3);
		menu1.add(menuitem4);
		menu1.add(menuitem1);
		menu1.add(menuitem11);
		menu1.add(menuitem6);
		menu2.add(menuitem2);
		menu2.add(menuitem7);
		menu2.add(menuitem8);
		menu2.add(menuitem9);
		menu2.add(menuitem10);
	}

	private static void ResetTextArea() {
		textarea.setText(null);
	}

	private static void SaveFile() {

		if(openfilepath.equals("")) {
			if(filechooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
				openfilepath = filechooser.getSelectedFile().toString();
				WriteFile(openfilepath);
			}
		} else {
			WriteFile(openfilepath);
		}

		CheckEditing(openfilepath.equals("") ? false : true);
	}

	private static void WriteFile(String filepath) {

		if(!filepath.contains(".txt")) {
			filepath += ".txt";
		}

		File objfile = new File(filepath);

		try {

			if(!objfile.exists()) {
				objfile.createNewFile();
			} 

			BufferedWriter objwriter = new BufferedWriter(new FileWriter(filepath));

			objwriter.write(textarea.getText());
			objwriter.flush();
			objwriter.close();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void OpenFile() {
		try {

			if(filechooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {	
				openfilepath = filechooser.getSelectedFile().toString();

				File objfile = new File(openfilepath);

				Scanner objscanner = new Scanner(objfile);

				textarea.setText(null);

				while(objscanner.hasNextLine()) {
					textarea.append(objscanner.nextLine() + System.lineSeparator());
				}

				oldtext = textarea.getText();

				objscanner.close();
			}	

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void DeleteFile() {
		try {

			File objfile = new File(openfilepath);
			objfile.delete();
			openfilepath = "";

			ResetTextArea();
			CheckEditing(true);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void NewDocument() {
		ResetTextArea();
		openfilepath = "";
	}

	private static void CheckEditing() {
		CheckEditing(false);
	}

	private static void CheckEditing(boolean saved) {
		if(!oldtext.equals(textarea.getText()) && !CheckAsterisk()) {
			frame.setTitle("* " + frame.getTitle());
		}

		if(saved || (openfilepath.equals("") && textarea.getText().equals(""))) {
			frame.setTitle(frame.getTitle().replace("* ", ""));
		}


		CheckButtons();
	}

	private static int ShowDialog(String title, String text) {
		int result = JOptionPane.showConfirmDialog(frame, text, title,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		return result;
	}

	private static void CheckButtons() {
		if(openfilepath.equals("")) {
			menuitem11.setEnabled(false);
		} else {
			menuitem11.setEnabled(true);
		}

		if(textarea.getText().equals("")) {
			menu2.setEnabled(false);
		} else {
			menu2.setEnabled(true);
		}

		try {
			if(textarea.getSelectedText() != null) {
				menuitem8.setEnabled(true);
				menuitem9.setEnabled(true);
				menuitem10.setEnabled(true);
			} else {
				menuitem8.setEnabled(false);
				menuitem9.setEnabled(false);
				menuitem10.setEnabled(false);
			}
		} catch(IllegalArgumentException e) {
			/* Quando viene cancellata una selezione composta da più caratteri,
			 * viene lanciata una IllegalArgumentException, che devo ignorare */
		}
	}

	private static void LoadStrings() {
		//Lettura file di configurazione contenente le stringhe
		Config.Parse();

		frame = new JFrame(strings.get("WINDOW_NAME"));
		menu1 = new JMenu(strings.get("FILE_MENU"));
		menu2 = new JMenu(strings.get("TEXT_MENU"));
		menuitem1 = new JMenuItem(strings.get("SAVE_AS"));
		menuitem2 = new JMenuItem(strings.get("DELETE_ALL"));
		menuitem3 = new JMenuItem(strings.get("OPEN_FILE"));
		menuitem4 = new JMenuItem(strings.get("SAVE_FILE"));
		menuitem5 = new JMenuItem(strings.get("NEW_FILE"));
		menuitem6 = new JMenuItem(strings.get("CLOSE_EDITOR"));
		menuitem7 = new JMenuItem(strings.get("SELECT_ALL"));
		menuitem8 = new JMenuItem(strings.get("COPY"));
		menuitem9 = new JMenuItem(strings.get("CUT"));
		menuitem10 = new JMenuItem(strings.get("PASTE"));
		menuitem11 = new JMenuItem(strings.get("DELETE_FILE"));

	}

	private static void SetIcon() {
		StringBuilder stringbuilder = new StringBuilder(applicationpath + "icon.png");

		if(System.getProperty("os.name").toLowerCase().contains("windows")) {
			stringbuilder.deleteCharAt(0);
		}

		String iconpath = stringbuilder.toString();

		try {

			File objfile = new File(iconpath);

			if(!objfile.exists()) {
				System.out.println("File icona non trovato, download da Internet in corso...");

				InputStream imagestream = new URL("https://cdn-icons-png.flaticon.com/512/579/579703.png").openStream();
				Files.copy(imagestream, Paths.get(iconpath));

				System.out.println("File icona scaricato correttamente!");
			}

			icon = Toolkit.getDefaultToolkit().getImage(iconpath);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void RestoreCloseBehavior() {
		//Se non ci sono più eventi in coda, ripristino il comportamento predefinito dell'evento di chiusura della finestra
		if(Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent() == null) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}

	private static boolean CheckAsterisk() {
		return frame.getTitle().contains("*") ? true : false;
	}
}
