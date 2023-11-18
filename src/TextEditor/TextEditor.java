package TextEditor;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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
	private static Image icon = Toolkit.getDefaultToolkit().getImage(TextEditor.class.getResource("/images/icon.png"));
	private static TrayIcon trayicon;
	private static SystemTray systemtray = SystemTray.getSystemTray();
	private static Hashtable<String, String> strings = new Hashtable<String, String>();

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
	private static JMenuItem menuitem_print;
	private static JMenuItem menuitem_selectfont;
	private static JMenuItem menuitem_selectcolor;

	private static MenuItem traymenuitem_new;
	private static MenuItem traymenuitem_save;
	private static MenuItem traymenuitem_saveas;
	private static MenuItem traymenuitem_delete;
	private static MenuItem traymenuitem_print;
	private static MenuItem traymenuitem_exit;

	private static JMenuBar menubar;
	private static PopupMenu traymenu;

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
	private static KeyStroke shortcut_print;
	private static KeyStroke shortcut_selectfont;
	private static KeyStroke shortcut_selectcolor;

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
		setLookAndFeel();

		//Lettura delle stringhe degli elementi dell'interfaccia
		loadStrings();

		//Creazione del frame
		createFrame();

		//Creazione del menu ed aggiunta degli elementi
		createMenu();

		//Creazione delle scorciatoie da tastiera
		createKeyStrokes();

		//Aggiunta degli elementi ai menù
		addMenuItems();

		//Creazione area di testo
		createTextArea();

		//Aggiunta dei componenti al frame
		setupFrame();

		//Impostazione del FileChooser
		setupFileChooser();

		//Impostazione del FontChooser
		setupFontChooser();

		//Creazione menu nella System Tray
		createTrayMenu();

		//Aggiunta degli elementi al menu della System Tray
		addTrayMenuItems();
	}

	private static void SetupBusinessLogic() {
		menuitem_saveas.addActionListener(e -> {
			openfilepath = "";
			saveFile();
		});

		menuitem_deleteall.addActionListener(e -> {
			resetTextArea();
		});	

		menuitem_open.addActionListener(e -> {
			boolean openfile = true;

			if(openfilepath.equals("") && !textarea.getText().equals("") || (!openfilepath.equals("") && checkAsterisk())) {
				int dialog = showDialog(getString("WARNING"), getString("SAVE_BEFORE_CONTINUE"));

				if(dialog == JOptionPane.YES_OPTION) {
					saveFile();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} else if (dialog == JOptionPane.CLOSED_OPTION) {
					openfile = false;
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				} else {
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}

			if(openfile) {
				openFile();
			}

			restoreCloseBehaviour();
		});

		menuitem_save.addActionListener(e -> {
			saveFile();
		});

		menuitem_new.addActionListener(e -> {
			boolean newdoc = true;

			if(openfilepath.equals("") && !textarea.getText().equals("") || (!openfilepath.equals("") && checkAsterisk())) {
				int dialog = showDialog(getString("WARNING"), getString("SAVE_BEFORE_CONTINUE"));

				if(dialog == JOptionPane.YES_OPTION) {
					saveFile();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} else if (dialog == JOptionPane.CLOSED_OPTION) {
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					newdoc = false;
				} else {
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}

			if(newdoc) {
				newDocument();
			}

			restoreCloseBehaviour();
		});

		menuitem_exit.addActionListener(e -> {
			boolean close = true;

			if(openfilepath.equals("") && !textarea.getText().equals("") || (!openfilepath.equals("") && checkAsterisk())) {
				int dialog = showDialog(getString("WARNING"), getString("SAVE_BEFORE_EXIT"));

				if(dialog == JOptionPane.YES_OPTION) {
					saveFile();
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
				System.exit(0); //Termino il processo
			}

			restoreCloseBehaviour();
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
			int dialog = showDialog(getString("WARNING"), getString("DELETE_CONFIRMATION"));

			if(dialog == JOptionPane.YES_OPTION) {
				deleteFile();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			} else if (dialog == JOptionPane.CLOSED_OPTION) {
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			} else {
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}

			restoreCloseBehaviour();
		});
		
		menuitem_print.addActionListener(e -> {
			try {
				if(textarea.print()){
					sendSystemTrayNotification(getString("WINDOW_NAME"), getString("SUCCESSFUL_PRINT_NOTIFICATION"), TrayIcon.MessageType.INFO);
				}
			} catch(PrinterException ex) {
				ex.printStackTrace();
				sendSystemTrayNotification(getString("WINDOW_NAME"), getString("PRINT_ERROR_NOTIFICATION"), TrayIcon.MessageType.ERROR);
			}
		});

		menuitem_selectfont.addActionListener(e -> {
			int dialog = fontchooser.showDialog(textarea);

			if(dialog == JFontChooser.OK_OPTION) {
				textarea.setFont(fontchooser.getSelectedFont());
			}
		});
		
		menuitem_selectcolor.addActionListener(e -> {
			Color new_color = JColorChooser.showDialog(textarea, getString("COLOR_WINDOW_NAME"), textarea.getForeground());
	
			if(!textarea.getForeground().equals(new_color) && new_color != null) {
				textarea.setForeground(new_color);
			}
		});

		textarea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				checkEditing();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkEditing();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				checkEditing();
			}
		});

		textarea.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				checkButtons();
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				if(openfilepath.equals("") && !textarea.getText().equals("") || (!openfilepath.equals("") && checkAsterisk())) {

					int dialog = showDialog(getString("WARNING"), getString("SAVE_BEFORE_EXIT"));

					if(dialog == JOptionPane.YES_OPTION) {
						saveFile();
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					} else if (dialog == JOptionPane.CLOSED_OPTION) {
						frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					} else {
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					}
				}

				restoreCloseBehaviour();
			}
		});

		Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new FlavorListener() { 
			@Override 
			public void flavorsChanged(FlavorEvent e) {
				checkButtons();
			} 
		}); 
	}

	private static void setupFileChooser() {
		filechooser = new JFileChooser();
		filechooser.setAcceptAllFileFilterUsed(false);
		filechooser.addChoosableFileFilter(new FileNameExtensionFilter(getString("TXT_FILE_EXTENSION_DESCRIPTION"), "txt"));
	}

	private static void setupFontChooser() {
		fontchooser = new JFontChooser();
	}

	private static void setupFrame() {
		frame.getRootPane().setJMenuBar(menubar);
		frame.getContentPane().add(BorderLayout.CENTER, scroll);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(icon);
		frame.setVisible(true);
	}

	private static void createTextArea() {
		textarea = new JTextArea();
		scroll = new JScrollPane(textarea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		if(UIManager.getLookAndFeel().getClass().toString().contains(UIManager.getSystemLookAndFeelClassName())) {
			scroll.setBorder(null);
		}

		oldtext = textarea.getText();
		textarea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
		textarea.setEditable(true);

		checkButtons();
	}

	private static void createFrame() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1300, 640);
	}

	private static void createMenu() {
		menubar = new JMenuBar();

		menubar.add(menu_file);
		menubar.add(menu_text);
	}

	private static void createKeyStrokes() {
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
		shortcut_print = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK); //CTRL + P per stampare il documento
		shortcut_selectfont = KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK); //CTRL + T per personalizzare il formato del testo
		shortcut_selectcolor = KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK); //CTRL + L per personalizzare il colore del testo

		setAccelerators();
	}

	private static void setAccelerators() {
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
		menuitem_print.setAccelerator(shortcut_print);
		menuitem_selectfont.setAccelerator(shortcut_selectfont);
		menuitem_selectcolor.setAccelerator(shortcut_selectcolor);
	}

	private static void addMenuItems() {
		menu_file.add(menuitem_new);
		menu_file.add(menuitem_open);
		menu_file.add(menuitem_save);
		menu_file.add(menuitem_saveas);
		menu_file.add(menuitem_delete);
		menu_file.add(menuitem_print);
		menu_file.add(menuitem_exit);
		menu_text.add(menuitem_deleteall);
		menu_text.add(menuitem_selectall);
		menu_text.add(menuitem_copy);
		menu_text.add(menuitem_cut);
		menu_text.add(menuitem_paste);
		menu_text.add(menuitem_selectfont);
		menu_text.add(menuitem_selectcolor);
	}

	private static void resetTextArea() {
		textarea.setText(null);
	}

	private static void saveFile() {

		if(openfilepath.equals("")) {
			if(filechooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
				openfilepath = filechooser.getSelectedFile().toString();
				writeFile(openfilepath);
			}
		} else {
			writeFile(openfilepath);
		}

		checkEditing(!openfilepath.equals(""));
	}

	private static void writeFile(String filepath) {

		if(!filepath.contains(".txt")) {
			filepath += ".txt";
		}

		File objfile = new File(filepath);

		try {

			if(!objfile.exists()) {
				objfile.createNewFile();
			} 

			BufferedWriter objwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), StandardCharsets.UTF_8));

			objwriter.write(textarea.getText());
			objwriter.flush();
			objwriter.close();

			sendSystemTrayNotification(getString("WINDOW_NAME"), getString("SUCCESSFUL_SAVE_NOTIFICATION"), TrayIcon.MessageType.INFO);

		} catch(Exception e) {
			e.printStackTrace();
			sendSystemTrayNotification(getString("WINDOW_NAME"), getString("SAVE_ERROR_NOTIFICATION"), TrayIcon.MessageType.ERROR);
		}
	}

	private static void openFile() {
		try {

			if(filechooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {	
				openfilepath = filechooser.getSelectedFile().toString();

				File objfile = new File(openfilepath);

				Scanner objscanner = new Scanner(objfile, StandardCharsets.UTF_8.name());

				textarea.setText(null);

				while(objscanner.hasNextLine()) {
					textarea.append(objscanner.nextLine() + System.lineSeparator());
				}

				oldtext = textarea.getText();

				objscanner.close();

				sendSystemTrayNotification(getString("WINDOW_NAME"), getString("SUCCESSFUL_OPEN_NOTIFICATION"), TrayIcon.MessageType.INFO);
			}	

		} catch(Exception e) {
			e.printStackTrace();
			sendSystemTrayNotification(getString("WINDOW_NAME"), getString("OPEN_ERROR_NOTIFICATION"), TrayIcon.MessageType.ERROR);
		}
	}

	private static void deleteFile() {
		try {

			File objfile = new File(openfilepath);
			objfile.delete();
			openfilepath = "";

			resetTextArea();
			checkEditing(true);

			sendSystemTrayNotification(getString("WINDOW_NAME"), getString("SUCCESSFUL_DELETE_NOTIFICATION"), TrayIcon.MessageType.INFO);

		} catch(Exception e) {
			e.printStackTrace();
			sendSystemTrayNotification(getString("WINDOW_NAME"), getString("DELETE_ERROR_NOTIFICATION"), TrayIcon.MessageType.ERROR);
		}
	}

	private static void newDocument() {
		resetTextArea();
		openfilepath = "";
	}

	private static void checkEditing() {
		checkEditing(false);
	}

	private static void checkEditing(boolean saved) {
		if(!oldtext.equals(textarea.getText()) && !checkAsterisk()) {
			frame.setTitle("* " + frame.getTitle());
		}

		if(saved || (openfilepath.equals("") && textarea.getText().equals(""))) {
			frame.setTitle(frame.getTitle().replace("* ", ""));
		}

		checkButtons();
	}

	private static int showDialog(String title, String text) {
		int result = JOptionPane.showConfirmDialog(frame, text, title,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		return result;
	}
	
	private static void checkButtons() {
		if(openfilepath.equals("")) {
			menuitem_delete.setEnabled(false);
			traymenuitem_delete.setEnabled(false);
		} else {
			menuitem_delete.setEnabled(true);
			traymenuitem_delete.setEnabled(true);
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
			/* Se si è generata una UnsupportedFlavorException, la uso per capire che nella clipboard non c'è un testo
			 * e che quindi devo disattivare il bottone "incolla" */
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

	private static void loadStrings() {
		//Lettura file di configurazione contenente le stringhe
		Config.Parse();

		frame = new JFrame(getString("WINDOW_NAME"));
		menu_file = new JMenu(getString("FILE_MENU"));
		menu_text = new JMenu(getString("TEXT_MENU"));
		menuitem_saveas = new JMenuItem(getString("SAVE_AS"));
		menuitem_deleteall = new JMenuItem(getString("DELETE_ALL"));
		menuitem_open = new JMenuItem(getString("OPEN_FILE"));
		menuitem_save = new JMenuItem(getString("SAVE_FILE"));
		menuitem_new = new JMenuItem(getString("NEW_FILE"));
		menuitem_exit = new JMenuItem(getString("CLOSE_EDITOR"));
		menuitem_selectall = new JMenuItem(getString("SELECT_ALL"));
		menuitem_copy = new JMenuItem(getString("COPY"));
		menuitem_cut = new JMenuItem(getString("CUT"));
		menuitem_paste = new JMenuItem(getString("PASTE"));
		menuitem_delete = new JMenuItem(getString("DELETE_FILE"));
		menuitem_print = new JMenuItem(getString("PRINT_FILE"));
		menuitem_selectfont = new JMenuItem(getString("TEXT_FORMAT"));
		menuitem_selectcolor = new JMenuItem(getString("TEXT_COLOR"));
		traymenuitem_new = new MenuItem(getString("NEW_FILE"));
		traymenuitem_exit = new MenuItem(getString("CLOSE_EDITOR"));
		traymenuitem_save = new MenuItem(getString("SAVE_FILE"));
		traymenuitem_saveas = new MenuItem(getString("SAVE_AS"));
		traymenuitem_delete = new MenuItem(getString("DELETE_FILE"));
		traymenuitem_print = new MenuItem(getString("PRINT_FILE"));

	}

	private static void restoreCloseBehaviour() {
		//Se non ci sono più eventi in coda, ripristino il comportamento predefinito dell'evento di chiusura della finestra
		if(Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent() == null) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}

	private static boolean checkAsterisk() {
		return frame.getTitle().contains("*");
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected static String getString(String key) {
		return strings.get(key);
	}
	
	protected static void putString(String key, String string) {
		strings.put(key, string);
	}

	private static void createTrayMenu() {

		if (SystemTray.isSupported()) {

			traymenu = new PopupMenu();
			trayicon = new TrayIcon(icon, getString("WINDOW_NAME"), traymenu);
			trayicon.setImageAutoSize(true);

			createTrayActionListeners();

			try {
				systemtray.add(trayicon);
			} catch (AWTException e) {
				System.err.println("Unable to add TrayIcon");
			}

		}
	}

	private static void createTrayActionListeners() {
		traymenuitem_new.addActionListener(e -> {
			bringFrameToFront();
			menuitem_new.doClick();
		});

		traymenuitem_save.addActionListener(e -> {
			menuitem_save.doClick();
		});

		traymenuitem_saveas.addActionListener(e -> {
			bringFrameToFront();
			menuitem_saveas.doClick();
		});

		traymenuitem_delete.addActionListener(e -> {
			bringFrameToFront();
			menuitem_delete.doClick();
		});
		
		traymenuitem_print.addActionListener(e -> {
			bringFrameToFront();
			menuitem_print.doClick();
		});

		traymenuitem_exit.addActionListener(e -> {
			bringFrameToFront();
			menuitem_exit.doClick();
		});
	}

	private static void addTrayMenuItems() {
		traymenu.add(traymenuitem_new);
		traymenu.add(traymenuitem_save);
		traymenu.add(traymenuitem_saveas);
		traymenu.add(traymenuitem_delete);
		traymenu.add(traymenuitem_print);
		traymenu.add(traymenuitem_exit);
	}

	private static void sendSystemTrayNotification(String title, String text, TrayIcon.MessageType type) {
		if(getString("ENABLE_NOTIFICATIONS").equals("true")) {
			trayicon.displayMessage(title, text, type);
		}
	}

	private static void bringFrameToFront() {

		/*
		 * Da https://stackoverflow.com/questions/34637597/bring-jframe-window-to-the-front
		 */

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(!frame.isVisible()) {
					frame.setVisible(true);
				}

				frame.setExtendedState(JFrame.NORMAL);
				frame.toFront();
				frame.setAlwaysOnTop(true);

				try {
					final Point oldMouseLocation = MouseInfo.getPointerInfo().getLocation();

					//Simula un click sulla barra del titolo
					Robot robot = new Robot();
					robot.mouseMove(frame.getX() + 100, frame.getY() + 10);
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

					//Sposta il mouse alla posizione precedente
					robot.mouseMove((int) oldMouseLocation.getX(), (int) oldMouseLocation.getY());
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					frame.setAlwaysOnTop(false);
				}
			}
		});
	}
}
