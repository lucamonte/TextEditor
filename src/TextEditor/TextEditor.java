package TextEditor;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JSeparator;
import javax.swing.undo.UndoManager;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import TextEditor.Config.ConfigurationManager;
import TextEditor.CustomElements.ContextMenu;
import TextEditor.CustomElements.JFontChooser;
import TextEditor.Translation.TranslationLoader;
import TextEditor.Translation.TranslationManager;
import TextEditor.Logger.Logger;
import TextEditor.Icons.Icons;

public class TextEditor {

	private static String openfilepath = "";
	private static JFileChooser filechooser;
	private static JFontChooser fontchooser;
	private static String oldtext = "";
	private static JFrame frame;
	private static Image icon = Icons.getImage(Icons.IconTypes.APPLICATION);
	private static TrayIcon trayicon;
	private static SystemTray systemtray;
	private static Hashtable<String, String> strings = new Hashtable<String, String>();
	private static Hashtable<String, String> languages = new Hashtable<String, String>();
	private static UndoManager undomanager = new UndoManager();
	private static boolean startup = true;

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
	private static JMenuItem menuitem_undo;
	private static JMenuItem menuitem_redo;
	private static JMenuItem menuitem_deletefile;
	private static JMenuItem menuitem_print;
	private static JMenuItem menuitem_selectfont;
	private static JMenuItem menuitem_selectcolor;
	private static JMenuItem menuitem_notifications;
	private static JMenuItem menuitem_preservecolor;
	private static JMenuItem menuitem_preservefont;

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
	private static JMenu menu_settings;
	private static JMenu submenu_language;

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
	private static KeyStroke shortcut_undo;
	private static KeyStroke shortcut_redo;
	private static KeyStroke shortcut_delete;
	private static KeyStroke shortcut_deletefile;
	private static KeyStroke shortcut_print;
	private static KeyStroke shortcut_selectfont;
	private static KeyStroke shortcut_selectcolor;
	private static KeyStroke shortcut_notifications;
	private static KeyStroke shortcut_preservecolor;
	private static KeyStroke shortcut_preservefont;

	private static JTextArea textarea;
	private static JScrollPane scroll;

	public static void run() {		
		//Generate the main window and its relative elements
		setupWindow();

		//Generate event listeners
		setupEventsListeners();

		startup = false;
	}

	private static void setupWindow() {
		//Refresh configuration (set missing properties to default values)
		ConfigurationManager.saveDefaultProperties();

		//Set the system look and feel. Comment to use the AWT/Swing L&F
		setLookAndFeel();

		//Create interface elements
		createUserInterfaceItems();

		//Read UI elements strings
		loadStrings();

		//Create the main frame
		createFrame();

		//Create the menu and add its relative elements
		createMenu();

		//Create keyboard shortcuts
		createKeyStrokes();

		//Add items to menus
		addMenuItems();

		//Create text area
		createTextArea();

		//Add components to frame
		setupFrame();

		//Setup file chooser
		setupFileChooser();

		//Setup font chooser
		setupFontChooser();

		if(SystemTray.isSupported()) {
			//Create system tray menu
			createTrayMenu();

			//Add elements to the system tray menu
			addTrayMenuItems();
		}
	}

	private static void setupEventsListeners() {
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
				int dialog = showConfirmDialog(getString("WARNING"), getString("SAVE_BEFORE_CONTINUE"));

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
				int dialog = showConfirmDialog(getString("WARNING"), getString("SAVE_BEFORE_CONTINUE"));

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
				int dialog = showConfirmDialog(getString("WARNING"), getString("SAVE_BEFORE_EXIT"));

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
				exit();
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

		menuitem_undo.addActionListener(e -> {
			if(canUndo()) {
				undo();
			}
		});

		menuitem_redo.addActionListener(e -> {
			if(canRedo()) {
				redo();
			}
		});

		menuitem_deletefile.addActionListener(e -> {
			int dialog = showConfirmDialog(getString("WARNING"), getString("DELETE_CONFIRMATION"));

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

		menuitem_delete.addActionListener(e ->  {
			replaceSelection();
		});

		menuitem_print.addActionListener(e -> {
			try {
				if(textarea.print()){
					sendSystemTrayNotification(getString("WINDOW_NAME"), getString("SUCCESSFUL_PRINT_NOTIFICATION"), TrayIcon.MessageType.INFO);
				}
			} catch(PrinterException ex) {
				Logger.writeLog(ex);
				sendSystemTrayNotification(getString("WINDOW_NAME"), getString("PRINT_ERROR_NOTIFICATION"), TrayIcon.MessageType.ERROR);
			}
		});

		menuitem_selectfont.addActionListener(e -> {
			int dialog = fontchooser.showDialog(textarea);

			if(dialog == JFontChooser.OK_OPTION) {
				setFont(fontchooser.getSelectedFont());
			}
		});

		menuitem_selectcolor.addActionListener(e -> {
			setColor(JColorChooser.showDialog(textarea, getString("COLOR_WINDOW_NAME"), textarea.getForeground()));
		});

		menuitem_notifications.addActionListener(e -> {
			toggleProperty("enable_notifications", menuitem_notifications);
		});

		menuitem_preservecolor.addActionListener(e -> {
			toggleProperty("preserve_color", menuitem_preservecolor);

			if(ConfigurationManager.getProperty("preserve_color").equals("true")) {
				setColor(loadColor());
			}
		});

		menuitem_preservefont.addActionListener(e -> {
			toggleProperty("preserve_font", menuitem_preservefont);

			if(ConfigurationManager.getProperty("preserve_font").equals("true")) {
				setFont(loadFont());
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

					int dialog = showConfirmDialog(getString("WARNING"), getString("SAVE_BEFORE_EXIT"));

					if(dialog == JOptionPane.YES_OPTION) {
						saveFile();
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					} else if(dialog == JOptionPane.CLOSED_OPTION) {
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
		if(fontchooser != null) {
			fontchooser = null;
		}

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

		final Color default_color = new Color(0, 0, 0);
		final Font default_font = new Font(Font.MONOSPACED, Font.PLAIN, 15);

		if(UIManager.getLookAndFeel().getClass().toString().contains(UIManager.getSystemLookAndFeelClassName())) {
			scroll.setBorder(null);
		}

		oldtext = textarea.getText();
		textarea.setEditable(true);

		if(ConfigurationManager.getProperty("preserve_color").equals("true")) {
			try {
				setColor(loadColor());
			} catch (NumberFormatException nfe) {
				saveColor(default_color);
				setColor(loadColor());
			} catch (Exception e) {
				Logger.writeLog(e);
			}
		} else {
			setColor(default_color);
		}

		if(ConfigurationManager.getProperty("preserve_color").equals("true")) {
			try {
				setFont(loadFont());
			} catch (NumberFormatException nfe) {
				saveFont(default_font);
				setFont(loadFont());
			} catch (Exception e) {
				Logger.writeLog(e);
			}
		} else {
			setFont(default_font);
		}

		addContextMenu();

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
		menubar.add(menu_settings);
		createLanguagesMenu();
	}

	private static void createKeyStrokes() {
		shortcut_save = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK); //CTRL + S: save
		shortcut_new = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK); //CTRL + N: create a new document
		shortcut_open = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK); //CTRL + O: open an existing document
		shortcut_saveas = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK); //CTRL + SHIFT + S: save as
		shortcut_exit = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK); //CTRL + Q: quit
		shortcut_deleteall = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK); //CTRL + SHIFT + D: delete all
		shortcut_selectall = KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK); //CTRL + A: select all
		shortcut_copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK); //CTRL + C: copy
		shortcut_cut = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK); //CTRL + X: cut
		shortcut_paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK); //CTRL + V: paste
		shortcut_undo = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK); //CTRL + Z: undo
		shortcut_redo = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK); //CTRL + Y: redo
		shortcut_delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.VK_UNDEFINED); //DELETE: delete selection
		shortcut_deletefile = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK); //CTRL + SHIFT + DELETE: delete file
		shortcut_print = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK); //CTRL + P: print document
		shortcut_selectfont = KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK); //CTRL + T: personalize text format
		shortcut_selectcolor = KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK); //CTRL + L: personalize text color
		shortcut_notifications = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK); //CTRL + SHIFT + N: toggle notifications
		shortcut_preservecolor = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK); //CTRL + SHIFT + C: toggle preserve color
		shortcut_preservefont = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK); //CTRL + SHIFT + F: toggle preserve font

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
		menuitem_undo.setAccelerator(shortcut_undo);
		menuitem_redo.setAccelerator(shortcut_redo);
		menuitem_delete.setAccelerator(shortcut_delete);
		menuitem_deletefile.setAccelerator(shortcut_deletefile);
		menuitem_print.setAccelerator(shortcut_print);
		menuitem_selectfont.setAccelerator(shortcut_selectfont);
		menuitem_selectcolor.setAccelerator(shortcut_selectcolor);
		menuitem_notifications.setAccelerator(shortcut_notifications);
		menuitem_preservecolor.setAccelerator(shortcut_preservecolor);
		menuitem_preservefont.setAccelerator(shortcut_preservefont);
	}

	private static void addMenuItems() {
		menu_file.add(menuitem_new);
		menu_file.add(menuitem_open);
		menu_file.add(menuitem_save);
		menu_file.add(menuitem_saveas);
		menu_file.add(menuitem_deletefile);
		menu_file.add(menuitem_print);
		menu_file.add(menuitem_exit);
		menu_text.add(menuitem_undo);
		menu_text.add(menuitem_redo);
		menu_text.add(new JSeparator());
		menu_text.add(menuitem_cut);
		menu_text.add(menuitem_copy);
		menu_text.add(menuitem_paste);
		menu_text.add(menuitem_delete);
		menu_text.add(new JSeparator());
		menu_text.add(menuitem_selectall);
		menu_text.add(menuitem_deleteall);
		menu_text.add(new JSeparator());
		menu_text.add(menuitem_selectfont);
		menu_text.add(menuitem_selectcolor);
		menu_settings.add(submenu_language);

		if(SystemTray.isSupported()) {
			menu_settings.add(menuitem_notifications);
		}

		menu_settings.add(menuitem_preservecolor);
		menu_settings.add(menuitem_preservefont);

		try {
			addIcons();
		} catch(Exception e) {
			Logger.writeLog(e);
		}
	}

	private static void addIcons() {
		menuitem_new.setIcon(Icons.getImageIcon(Icons.IconTypes.NEW_FILE));
		menuitem_open.setIcon(Icons.getImageIcon(Icons.IconTypes.OPEN_FILE));
		menuitem_save.setIcon(Icons.getImageIcon(Icons.IconTypes.SAVE));
		menuitem_saveas.setIcon(Icons.getImageIcon(Icons.IconTypes.SAVE_AS));
		menuitem_deletefile.setIcon(Icons.getImageIcon(Icons.IconTypes.DELETE_FILE));
		menuitem_print.setIcon(Icons.getImageIcon(Icons.IconTypes.PRINT));
		menuitem_exit.setIcon(Icons.getImageIcon(Icons.IconTypes.EXIT));
		menuitem_undo.setIcon(Icons.getImageIcon(Icons.IconTypes.UNDO));
		menuitem_redo.setIcon(Icons.getImageIcon(Icons.IconTypes.REDO));
		menuitem_copy.setIcon(Icons.getImageIcon(Icons.IconTypes.COPY));
		menuitem_paste.setIcon(Icons.getImageIcon(Icons.IconTypes.PASTE));
		menuitem_cut.setIcon(Icons.getImageIcon(Icons.IconTypes.CUT));
		menuitem_delete.setIcon(Icons.getImageIcon(Icons.IconTypes.DELETE));
		menuitem_selectall.setIcon(Icons.getImageIcon(Icons.IconTypes.SELECT_ALL));
		menuitem_deleteall.setIcon(Icons.getImageIcon(Icons.IconTypes.DELETE_ALL));
		menuitem_selectfont.setIcon(Icons.getImageIcon(Icons.IconTypes.SELECT_FONT));
		menuitem_selectcolor.setIcon(Icons.getImageIcon(Icons.IconTypes.SELECT_COLOR));
		submenu_language.setIcon(Icons.getImageIcon(Icons.IconTypes.SELECT_LANGUAGE));

		setMenuIcon(menuitem_notifications, ConfigurationManager.getProperty("enable_notifications").equals("true"));
		setMenuIcon(menuitem_preservecolor, ConfigurationManager.getProperty("preserve_color").equals("true"));
		setMenuIcon(menuitem_preservefont, ConfigurationManager.getProperty("preserve_font").equals("true"));
	}

	private static void addContextMenu() {
		new ContextMenu(textarea);
	}

	public static void resetTextArea() {
		textarea.setText(null);
	}

	public static void replaceSelection() {
		textarea.replaceSelection("");
	}

	public static void undo() {
		undomanager.undo();
	}

	public static void redo() {
		undomanager.redo();
	}

	public static boolean canUndo() {
		return undomanager.canUndo();
	}

	public static boolean canRedo() {
		return undomanager.canRedo();
	}

	public static UndoManager getUndoManager() {
		return undomanager;
	}

	private static void saveFile() {
		boolean show_notification = true;

		if(openfilepath.equals("")) {
			if(filechooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
				openfilepath = filechooser.getSelectedFile().toString();
			} else {
				show_notification = false;
			}
		}

		writeFile(openfilepath, show_notification);

		appendFileName();

		checkEditing(!openfilepath.equals(""));
	}

	private static void writeFile(String filepath, boolean show_notification) {

		filepath = checkFileName(filepath);

		try {

			textarea.write(new BufferedWriter(new FileWriter(filepath)));

			if(show_notification) {
				sendSystemTrayNotification(getString("WINDOW_NAME"), getString("SUCCESSFUL_SAVE_NOTIFICATION"), TrayIcon.MessageType.INFO);
			}

		} catch(Exception e) {
			Logger.writeLog(e);
			sendSystemTrayNotification(getString("WINDOW_NAME"), getString("SAVE_ERROR_NOTIFICATION"), TrayIcon.MessageType.ERROR);
		}
	}

	private static void openFile() {
		try {

			if(filechooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {	
				openfilepath = filechooser.getSelectedFile().toString();

				textarea.setText(null);

				textarea.read(new BufferedReader(new FileReader(openfilepath)), null);

				oldtext = textarea.getText();

				appendFileName();

				sendSystemTrayNotification(getString("WINDOW_NAME"), getString("SUCCESSFUL_OPEN_NOTIFICATION"), TrayIcon.MessageType.INFO);
			}	

		} catch(Exception e) {
			Logger.writeLog(e);
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
			appendFileName();

			sendSystemTrayNotification(getString("WINDOW_NAME"), getString("SUCCESSFUL_DELETE_NOTIFICATION"), TrayIcon.MessageType.INFO);

		} catch(Exception e) {
			Logger.writeLog(e);
			sendSystemTrayNotification(getString("WINDOW_NAME"), getString("DELETE_ERROR_NOTIFICATION"), TrayIcon.MessageType.ERROR);
		}
	}

	private static void appendFileName() {
		resetTitleBar();

		if(!openfilepath.equals("")) {
			openfilepath = checkFileName(openfilepath);

			frame.setTitle(frame.getTitle() + " - " + openfilepath.substring(openfilepath.lastIndexOf(File.separator) + 1));
		} 

		checkEditing();
	}

	private static void resetTitleBar(boolean saved) {
		frame.setTitle(getString("WINDOW_NAME"));
		checkEditing(saved);
	}

	private static void resetTitleBar() {
		resetTitleBar(false);
	}

	private static void newDocument() {
		resetTextArea();
		resetTitleBar(true);
		openfilepath = "";
	}

	private static String checkFileName(String filename) {
		if(!filename.contains(".txt")) {
			filename += ".txt";
		}

		return filename;
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

	private static int showConfirmDialog(String title, String text) {
		int result = JOptionPane.showConfirmDialog(frame, text, title,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		return result;
	}

	public static void showErrorDialog(String title, Object content) {
		showMessageDialog(title, content, JOptionPane.ERROR_MESSAGE);
	}

	private static void showMessageDialog(String title, Object content, int type) {
		JOptionPane.showMessageDialog(frame, content, title, type);
	}

	private static void checkButtons() {
		if(openfilepath.equals("")) {
			menuitem_deletefile.setEnabled(false);
			traymenuitem_delete.setEnabled(false);
		} else {
			menuitem_deletefile.setEnabled(true);
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
				menuitem_delete.setEnabled(true);
			} else {
				menuitem_copy.setEnabled(false);
				menuitem_cut.setEnabled(false);
				menuitem_delete.setEnabled(false);
			}
		} catch(IllegalArgumentException e) {
			/* When a selection composed by more charaters is deleted, 
			 * an IllegalArgumentException, to be ignored, is thrown. */
		}

		String clipboardtext = "";

		try {
			clipboardtext = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
			/* If an UnsupportedFlavorException is thrown, 
			 * the clipboard content is different than text and the "paste" button needs to be disabled */
			clipboardtext = "";
		} catch (Exception e) {
			//Print stack trace for every other generic exception
			Logger.writeLog(e);
		}

		if(clipboardtext.equals("")) {
			menuitem_paste.setEnabled(false);
		} else {
			menuitem_paste.setEnabled(true);
		}

		menuitem_undo.setEnabled(canUndo());
		menuitem_redo.setEnabled(canRedo());
	}

	private static void createUserInterfaceItems() {

		frame = new JFrame();
		menu_file = new JMenu();
		menu_text = new JMenu();
		menu_settings = new JMenu();
		submenu_language = new JMenu();
		menuitem_saveas = new JMenuItem();
		menuitem_deleteall = new JMenuItem();
		menuitem_open = new JMenuItem();
		menuitem_save = new JMenuItem();
		menuitem_new = new JMenuItem();
		menuitem_exit = new JMenuItem();
		menuitem_selectall = new JMenuItem();
		menuitem_copy = new JMenuItem();
		menuitem_cut = new JMenuItem();
		menuitem_paste = new JMenuItem();
		menuitem_undo = new JMenuItem();
		menuitem_redo = new JMenuItem();
		menuitem_delete = new JMenuItem();
		menuitem_deletefile = new JMenuItem();
		menuitem_print = new JMenuItem();
		menuitem_selectfont = new JMenuItem();
		menuitem_selectcolor = new JMenuItem();
		menuitem_notifications = new JMenuItem();
		menuitem_preservecolor = new JMenuItem();
		menuitem_preservefont = new JMenuItem();
		traymenuitem_new = new MenuItem();
		traymenuitem_exit = new MenuItem();
		traymenuitem_save = new MenuItem();
		traymenuitem_saveas = new MenuItem();
		traymenuitem_delete = new MenuItem();
		traymenuitem_print = new MenuItem();

	}

	private static void loadStrings() {
		//Read translation file
		if(TranslationLoader.parse(TranslationManager.getSelectedLanguage())) {

			frame.setTitle(getString("WINDOW_NAME"));
			menu_file.setText(getString("FILE_MENU"));
			menu_text.setText(getString("TEXT_MENU"));
			menu_settings.setText(getString("SETTINGS_MENU"));
			submenu_language.setText(getString("LANGUAGE_MENU"));	
			menuitem_saveas.setText(getString("SAVE_AS"));
			menuitem_deleteall.setText(getString("DELETE_ALL"));
			menuitem_open.setText(getString("OPEN_FILE"));
			menuitem_save.setText(getString("SAVE_FILE"));
			menuitem_new.setText(getString("NEW_FILE"));
			menuitem_exit.setText(getString("CLOSE_EDITOR"));
			menuitem_selectall.setText(getString("SELECT_ALL"));
			menuitem_undo.setText(getString("UNDO"));
			menuitem_redo.setText(getString("REDO"));
			menuitem_copy.setText(getString("COPY"));
			menuitem_cut.setText(getString("CUT"));
			menuitem_paste.setText(getString("PASTE"));
			menuitem_delete.setText(getString("DELETE"));
			menuitem_deletefile.setText(getString("DELETE_FILE"));
			menuitem_print.setText(getString("PRINT_FILE"));
			menuitem_selectfont.setText(getString("TEXT_FORMAT"));
			menuitem_selectcolor.setText(getString("TEXT_COLOR"));
			menuitem_notifications.setText(getString("TEXT_NOTIFICATIONS"));
			menuitem_preservecolor.setText(getString("TEXT_PRESERVE_COLOR"));
			menuitem_preservefont.setText(getString("TEXT_PRESERVE_FONT"));
			traymenuitem_new.setLabel(getString("NEW_FILE"));
			traymenuitem_exit.setLabel(getString("CLOSE_EDITOR"));
			traymenuitem_save.setLabel(getString("SAVE_FILE"));
			traymenuitem_saveas.setLabel(getString("SAVE_AS"));
			traymenuitem_delete.setLabel(getString("DELETE_FILE"));
			traymenuitem_print.setLabel(getString("PRINT_FILE"));

			TranslationManager.loadLanguages();

			//Rebuild font chooser with the new translation
			setupFontChooser();

			if(!startup) {
				appendFileName();

				//Update context menu strings
				ContextMenu.loadStrings();
			}

		} else exit();
	}

	private static void updateStrings() {
		strings.clear();
		loadStrings();
	}

	private static void restoreCloseBehaviour() {
		//If there are no more events in the events queue, restore the default close operation
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
			Logger.writeLog(e);
		}
	}

	public static String getString(String key) {
		return strings.get(key);
	}

	public static void putString(String key, String string) {
		strings.put(key, string);
	}

	private static void createTrayMenu() {

		if (SystemTray.isSupported()) {

			systemtray = SystemTray.getSystemTray();

			traymenu = new PopupMenu();
			trayicon = new TrayIcon(icon, getString("WINDOW_NAME"), traymenu);
			trayicon.setImageAutoSize(true);

			createTrayActionListeners();

			try {
				systemtray.add(trayicon);
			} catch (AWTException e) {
				Logger.writeLog(e);
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
			menuitem_deletefile.doClick();
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
		if(ConfigurationManager.getProperty("enable_notifications").equals("true") && SystemTray.isSupported()) {
			trayicon.displayMessage(title, text, type);
		}
	}

	private static void bringFrameToFront() {

		/*
		 * From https://stackoverflow.com/questions/34637597/bring-jframe-window-to-the-front
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

					//Simulate a click on the title bar
					Robot robot = new Robot();
					robot.mouseMove(frame.getX() + 100, frame.getY() + 10);
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

					//Move the mouse pointer to the previous location
					robot.mouseMove((int) oldMouseLocation.getX(), (int) oldMouseLocation.getY());
				} catch(Exception e) {
					Logger.writeLog(e);
				} finally {
					frame.setAlwaysOnTop(false);
				}
			}
		});
	}

	private static void exit() {
		if(frame != null) {
			frame.setVisible(false); //Hide frame
			frame.dispose(); //Destroy frame
		}
		System.exit(0); //Terminate process
	}

	public static void putLanguage(String languageCode, String language) {
		languages.put(languageCode, language);
	}

	private static void createLanguagesMenu() {
		String langCode = "";
		Enumeration<String> languageCodes = languages.keys();

		while(languageCodes.hasMoreElements()) {
			langCode = languageCodes.nextElement();
			newLanguageMenuItem(langCode, languages.get(langCode));
			updateLanguagesMenu(TranslationManager.getSelectedLanguage());
		}
	}

	private static void updateLanguagesMenu(String langCode) {		
		for(int i = 0; i < submenu_language.getItemCount(); i++) {
			JMenuItem menuitem_language = submenu_language.getItem(i);

			if(menuitem_language.getName().equals(langCode)) {
				menuitem_language.setIcon(Icons.getImageIcon(Icons.IconTypes.SELECTED));
			} else {
				menuitem_language.setIcon(null);
			}

			if(menuitem_language.getName().equals("sys")) {
				menuitem_language.setText(getString("SYSTEM_LANGUAGE"));
			}
		}
	}

	private static void newLanguageMenuItem(String languageCode, String description) {
		JMenuItem menuitem_language = new JMenuItem(description);

		menuitem_language.setName(languageCode);

		menuitem_language.addActionListener(e -> {
			if(TranslationManager.setSelectedLanguage(languageCode)) {
				updateStrings();
				updateLanguagesMenu(languageCode);
			} else {
				showErrorDialog(getString("ERROR_SETTING_LANGUAGE_TITLE"), getString("ERROR_SETTING_LANGUAGE"));
			}
		});

		submenu_language.add(menuitem_language);
	}

	private static void toggleProperty(String property, JMenuItem menuitem) {
		if(ConfigurationManager.getProperty(property).equals("true")) {
			ConfigurationManager.setProperty(property, "false");
			setMenuIcon(menuitem, false);
		} else {
			ConfigurationManager.setProperty(property, "true");
			setMenuIcon(menuitem, true);
		}
	}

	private static void setMenuIcon(JMenuItem menuitem, boolean selected) {
		if(selected) {
			menuitem.setIcon(Icons.getImageIcon(Icons.IconTypes.SELECTED));
		} else {
			menuitem.setIcon(null);
		}
	}

	private static void setColor(Color color) {
		if(!textarea.getForeground().equals(color) && color != null) {
			textarea.setForeground(color);

			if(ConfigurationManager.getProperty("preserve_color").equals("true")) {
				saveColor(color);
			}
		}
	}

	private static void saveColor(Color color) {
		ConfigurationManager.setProperty("color.red", String.valueOf(color.getRed()));
		ConfigurationManager.setProperty("color.green", String.valueOf(color.getGreen()));
		ConfigurationManager.setProperty("color.blue", String.valueOf(color.getBlue()));
	}

	private static Color loadColor() {
		return new Color (Integer.parseInt(ConfigurationManager.getProperty("color.red")),
				Integer.parseInt(ConfigurationManager.getProperty("color.green")),
				Integer.parseInt(ConfigurationManager.getProperty("color.blue")));
	}

	private static void setFont(Font font) {
		textarea.setFont(font);

		if(ConfigurationManager.getProperty("preserve_font").equals("true")) {
			saveFont(font);
		}
	}

	private static void saveFont(Font font) {
		ConfigurationManager.setProperty("font.name", font.getName());
		ConfigurationManager.setProperty("font.style", String.valueOf(font.getStyle()));
		ConfigurationManager.setProperty("font.size", String.valueOf(font.getSize()));
	}

	private static Font loadFont() {
		return new Font(ConfigurationManager.getProperty("font.name"),
				Integer.parseInt(ConfigurationManager.getProperty("font.style")),
				Integer.parseInt(ConfigurationManager.getProperty("font.size")));
	}
}