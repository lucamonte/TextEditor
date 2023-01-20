package TextEditor;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.UnsupportedFlavorException;
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
	private static JFileChooser filechooser;
	private static JFontChooser fontchooser;
	private static String oldtext = "";
	private static JFrame frame;
	protected static String applicationpath = new TextEditor().getClass().getClassLoader().getResource("").getPath();
	private static Image icon;
	protected static Hashtable<String, String> strings = new Hashtable<String, String>();

	private static JMenuItem menuitem_saveas;
	private static JMenuItem menuitem_deleteall;
	private static JMenuItem menuitem_open;
	private static JMenuItem menuitem_save;
	private static JMenuItem menuitem_new;
	private static JMenuItem menuitem_exit;
	private static JMenuItem menuitem_selectall;
	private static JMenuItem menuitem_copy;
	private static JMenuItem menuitem_cut;
	private static JMenuItem menuitem_paste;
	private static JMenuItem menuitem_delete;
	private static JMenuItem menuitem_selectfont;

	private static JMenuBar menubar;

	private static JMenu menu_file;
	private static JMenu menu_text;

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
	private static KeyStroke shortcut_selectfont;

	private static JTextArea textarea;
	private static JScrollPane scroll;

	public static void Run() {
		//Generazione della finestra e relativi elementi
		SetupWindow();

		//Gestione degli eventi e della logica applicativa
		SetupBusinessLogic();
	}

	private static void SetupWindow() {
		//Impostazione dello stile dei componenti della UI. Commentare per utilizzare lo stile di default di AWT/Swing
		SetLookAndFeel();

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

		//Impostazione del FontChooser
		SetupFontChooser();
	}

	private static void SetupBusinessLogic() {
		menuitem_saveas.addActionListener(e -> {
			openfilepath = "";
			SaveFile();
		});

		menuitem_deleteall.addActionListener(e -> {
			ResetTextArea();
		});	

		menuitem_open.addActionListener(e -> {
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

		menuitem_save.addActionListener(e -> {
			SaveFile();
		});

		menuitem_new.addActionListener(e -> {
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

		menuitem_exit.addActionListener(e -> {
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

		menuitem_selectall.addActionListener(e -> {
			textarea.selectAll();
		});

		menuitem_copy.addActionListener(e -> {
			textarea.copy();
		});

		menuitem_cut.addActionListener(e -> {
			textarea.cut();
		});

		menuitem_paste.addActionListener(e -> {
			textarea.paste();
		});

		menuitem_delete.addActionListener(e -> {
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

		menuitem_selectfont.addActionListener(e -> {
			int dialog = fontchooser.showDialog(textarea);

			if(dialog == JFontChooser.OK_OPTION) {
				textarea.setFont(fontchooser.getSelectedFont());
			}
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

		Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new FlavorListener() { 
			@Override 
			public void flavorsChanged(FlavorEvent e) {
				CheckButtons();
			} 
		}); 
	}

	private static void SetupFileChooser() {
		filechooser = new JFileChooser();
		filechooser.setAcceptAllFileFilterUsed(false);
		filechooser.addChoosableFileFilter(new FileNameExtensionFilter(strings.get("TXT_FILE_EXTENSION_DESCRIPTION"), "txt"));
	}

	private static void SetupFontChooser() {
		fontchooser = new JFontChooser();
	}

	private static void SetupFrame() {
		frame.getRootPane().setJMenuBar(menubar);
		frame.getContentPane().add(BorderLayout.CENTER, scroll);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(icon);
		frame.setVisible(true);
	}

	private static void CreateTextArea() {
		textarea = new JTextArea();
		scroll = new JScrollPane(textarea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		if(UIManager.getLookAndFeel().getClass().toString().contains(UIManager.getSystemLookAndFeelClassName())) {
			scroll.setBorder(null);
		}

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

		menubar.add(menu_file);
		menubar.add(menu_text);
	}

	private static void CreateKeyStrokes() {
		shortcut_save = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK); //CTRL + S per salvare
		shortcut_new = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK); //CTRL + N per creare un nuovo documento
		shortcut_open = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK); //CTRL + O per aprire un documento
		shortcut_saveas = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK); //CTRL + SHIFT + S per salvare con nome
		shortcut_exit = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK); //CTRL + Q per chiudere
		shortcut_deleteall = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK); //CTRL + SHIFT + D per cancellare tutto
		shortcut_selectall = KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK); //CTRL + A per selezionare tutto
		shortcut_copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK); //CTRL + C per copiare
		shortcut_cut = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK); //CTRL + X per tagliare
		shortcut_paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK); //CTRL + V per incollare
		shortcut_delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK); //CTRL + DELETE per eliminare il file
		shortcut_selectfont = KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK); //CTRL + T per personalizzare il formato del testo

		SetAccelerators();
	}

	private static void SetAccelerators() {
		menuitem_saveas.setAccelerator(shortcut_saveas);
		menuitem_deleteall.setAccelerator(shortcut_deleteall);
		menuitem_open.setAccelerator(shortcut_open);
		menuitem_save.setAccelerator(shortcut_save);
		menuitem_new.setAccelerator(shortcut_new);
		menuitem_exit.setAccelerator(shortcut_exit);
		menuitem_selectall.setAccelerator(shortcut_selectall);
		menuitem_copy.setAccelerator(shortcut_copy);
		menuitem_cut.setAccelerator(shortcut_cut);
		menuitem_paste.setAccelerator(shortcut_paste);
		menuitem_delete.setAccelerator(shortcut_delete);
		menuitem_selectfont.setAccelerator(shortcut_selectfont);
	}

	private static void AddMenuItems() {
		menu_file.add(menuitem_new);
		menu_file.add(menuitem_open);
		menu_file.add(menuitem_save);
		menu_file.add(menuitem_saveas);
		menu_file.add(menuitem_delete);
		menu_file.add(menuitem_exit);
		menu_text.add(menuitem_deleteall);
		menu_text.add(menuitem_selectall);
		menu_text.add(menuitem_copy);
		menu_text.add(menuitem_cut);
		menu_text.add(menuitem_paste);
		menu_text.add(menuitem_selectfont);
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
			menuitem_delete.setEnabled(false);
		} else {
			menuitem_delete.setEnabled(true);
		}

		if(textarea.getText().equals("")) {
			menuitem_deleteall.setEnabled(false);
			menuitem_selectall.setEnabled(false);
		} else {
			menuitem_deleteall.setEnabled(true);
			menuitem_selectall.setEnabled(true);
		}

		try {
			if(textarea.getSelectedText() != null) {
				menuitem_copy.setEnabled(true);
				menuitem_cut.setEnabled(true);
			} else {
				menuitem_copy.setEnabled(false);
				menuitem_cut.setEnabled(false);
			}
		} catch(IllegalArgumentException e) {
			/* Quando viene cancellata una selezione composta da più caratteri,
			 * viene sollevata una IllegalArgumentException, che devo ignorare */
		}

		String clipboardtext = "";

		try {
			clipboardtext = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
			//Se si è generata una UnsupportedFlavorException, la uso per capire che nella clipboard non c'è un testo
			clipboardtext = "";
		} catch (Exception e) {
			//Se si è generato un qualsiasi altro tipo di eccezione, scrivo lo stack trace
			e.printStackTrace();
		}

		if(clipboardtext.equals("")) {
			menuitem_paste.setEnabled(false);
		} else {
			menuitem_paste.setEnabled(true);
		}
	}

	private static void LoadStrings() {
		//Lettura file di configurazione contenente le stringhe
		Config.Parse();

		frame = new JFrame(strings.get("WINDOW_NAME"));
		menu_file = new JMenu(strings.get("FILE_MENU"));
		menu_text = new JMenu(strings.get("TEXT_MENU"));
		menuitem_saveas = new JMenuItem(strings.get("SAVE_AS"));
		menuitem_deleteall = new JMenuItem(strings.get("DELETE_ALL"));
		menuitem_open = new JMenuItem(strings.get("OPEN_FILE"));
		menuitem_save = new JMenuItem(strings.get("SAVE_FILE"));
		menuitem_new = new JMenuItem(strings.get("NEW_FILE"));
		menuitem_exit = new JMenuItem(strings.get("CLOSE_EDITOR"));
		menuitem_selectall = new JMenuItem(strings.get("SELECT_ALL"));
		menuitem_copy = new JMenuItem(strings.get("COPY"));
		menuitem_cut = new JMenuItem(strings.get("CUT"));
		menuitem_paste = new JMenuItem(strings.get("PASTE"));
		menuitem_delete = new JMenuItem(strings.get("DELETE_FILE"));
		menuitem_selectfont = new JMenuItem(strings.get("TEXT_FORMAT"));

	}

	private static void SetIcon() {
		StringBuilder stringbuilder = new StringBuilder(applicationpath + "icon.png");

		/* Su Windows, nella applicationpath viene aggiunta una \ alla fine, che causa un errore con la classe File. 
		 * Rimuovo il primo carattere della stringa (\), in modo da avere a disposizione un percorso file pulito */

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

	private static void SetLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static String GetString(String key) {
		return strings.get(key);
	}
}
