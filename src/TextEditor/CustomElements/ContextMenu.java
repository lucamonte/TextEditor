package TextEditor.CustomElements;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import TextEditor.TextEditor;
import TextEditor.Icons.Icons;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ContextMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;

	private Clipboard clipboard;

	private static JMenuItem contextmenuitem_undo;
	private static JMenuItem contextmenuitem_redo;
	private static JMenuItem contextmenuitem_cut;
	private static JMenuItem contextmenuitem_copy;
	private static JMenuItem contextmenuitem_paste;
	private static JMenuItem contextmenuitem_delete;
	private static JMenuItem contextmenuitem_selectall;
	private static JMenuItem contextmenuitem_deleteall;

	private JTextComponent textComponent;

	public ContextMenu(JTextComponent component) {
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		addTo(component);
		addPopupMenuItems();
		loadStrings();
	}

	private void addPopupMenuItems() {
		contextmenuitem_undo = new JMenuItem();
		contextmenuitem_undo.setEnabled(false);
		contextmenuitem_undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
		contextmenuitem_undo.addActionListener(event -> TextEditor.undo());
		contextmenuitem_undo.setIcon(Icons.getImageIcon(Icons.IconTypes.UNDO));
		add(contextmenuitem_undo);

		contextmenuitem_redo = new JMenuItem();
		contextmenuitem_redo.setEnabled(false);
		contextmenuitem_redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
		contextmenuitem_redo.addActionListener(event -> TextEditor.redo());
		contextmenuitem_redo.setIcon(Icons.getImageIcon(Icons.IconTypes.REDO));
		add(contextmenuitem_redo);

		add(new JSeparator());

		contextmenuitem_cut = new JMenuItem();
		contextmenuitem_cut.setEnabled(false);
		contextmenuitem_cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
		contextmenuitem_cut.addActionListener(event -> textComponent.cut());
		contextmenuitem_cut.setIcon(Icons.getImageIcon(Icons.IconTypes.CUT));
		add(contextmenuitem_cut);

		contextmenuitem_copy = new JMenuItem();
		contextmenuitem_copy.setEnabled(false);
		contextmenuitem_copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
		contextmenuitem_copy.addActionListener(event -> textComponent.copy());
		contextmenuitem_copy.setIcon(Icons.getImageIcon(Icons.IconTypes.COPY));
		add(contextmenuitem_copy);

		contextmenuitem_paste = new JMenuItem();
		contextmenuitem_paste.setEnabled(false);
		contextmenuitem_paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
		contextmenuitem_paste.addActionListener(event -> textComponent.paste());
		contextmenuitem_paste.setIcon(Icons.getImageIcon(Icons.IconTypes.PASTE));
		add(contextmenuitem_paste);

		contextmenuitem_delete = new JMenuItem();
		contextmenuitem_delete.setEnabled(false);
		contextmenuitem_delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.VK_UNDEFINED));
		contextmenuitem_delete.addActionListener(event -> TextEditor.replaceSelection());
		contextmenuitem_delete.setIcon(Icons.getImageIcon(Icons.IconTypes.DELETE));
		add(contextmenuitem_delete);

		add(new JSeparator());

		contextmenuitem_selectall = new JMenuItem();
		contextmenuitem_selectall.setEnabled(false);
		contextmenuitem_selectall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
		contextmenuitem_selectall.addActionListener(event -> textComponent.selectAll());
		contextmenuitem_selectall.setIcon(Icons.getImageIcon(Icons.IconTypes.SELECT_ALL));
		add(contextmenuitem_selectall);

		contextmenuitem_deleteall = new JMenuItem();
		contextmenuitem_deleteall.setEnabled(false);
		contextmenuitem_deleteall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		contextmenuitem_deleteall.addActionListener(event -> TextEditor.resetTextArea());
		contextmenuitem_deleteall.setIcon(Icons.getImageIcon(Icons.IconTypes.DELETE_ALL));
		add(contextmenuitem_deleteall);
	}

	private void addTo(JTextComponent textComponent) {
		textComponent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent pressedEvent) {
				if((pressedEvent.getKeyCode() == KeyEvent.VK_Z) && ((pressedEvent.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
					if(TextEditor.canUndo()) {
						TextEditor.undo();
					}
				}

				if((pressedEvent.getKeyCode() == KeyEvent.VK_Y) && ((pressedEvent.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
					if(TextEditor.canRedo()) {
						TextEditor.redo();
					}
				}
			}
		});

		textComponent.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent releasedEvent) {
				handleContextMenu(releasedEvent);
			}

			@Override
			public void mouseReleased(MouseEvent releasedEvent) {
				handleContextMenu(releasedEvent);
			}
		});

		textComponent.getDocument().addUndoableEditListener(event -> TextEditor.getUndoManager().addEdit(event.getEdit()));
	}

	private void handleContextMenu(MouseEvent releasedEvent) {
		if(releasedEvent.isPopupTrigger()) {
			processClick(releasedEvent);
		}
	}

	private void processClick(MouseEvent event) {
		textComponent = (JTextComponent) event.getSource();
		textComponent.requestFocus();

		boolean enableCut = false;
		boolean enableCopy = false;
		boolean enablePaste = false;
		boolean enableDelete = false;
		boolean enableSelectAll = false;
		boolean enableDeleteAll = false;

		String selectedText = textComponent.getSelectedText();
		String text = textComponent.getText();

		if(text != null) {
			if(text.length() > 0) {
				enableSelectAll = true;
				enableDeleteAll = true;
			}
		}

		if(selectedText != null) {
			if(selectedText.length() > 0) {
				enableCut = true;
				enableCopy = true;
				enableDelete = true;
			}
		}

		if(clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor) && textComponent.isEnabled()) {
			enablePaste = true;
		}

		contextmenuitem_undo.setEnabled(TextEditor.canUndo());
		contextmenuitem_redo.setEnabled(TextEditor.canRedo());
		contextmenuitem_cut.setEnabled(enableCut);
		contextmenuitem_copy.setEnabled(enableCopy);
		contextmenuitem_paste.setEnabled(enablePaste);
		contextmenuitem_delete.setEnabled(enableDelete);
		contextmenuitem_selectall.setEnabled(enableSelectAll);
		contextmenuitem_deleteall.setEnabled(enableDeleteAll);

		//Shows the popup menu
		show(textComponent, event.getX(), event.getY());
	}

	public static void loadStrings() {
		contextmenuitem_undo.setText(TextEditor.getString("UNDO"));
		contextmenuitem_redo.setText(TextEditor.getString("REDO"));
		contextmenuitem_cut.setText(TextEditor.getString("CUT"));
		contextmenuitem_copy.setText(TextEditor.getString("COPY"));
		contextmenuitem_paste.setText(TextEditor.getString("PASTE"));
		contextmenuitem_delete.setText(TextEditor.getString("DELETE"));
		contextmenuitem_selectall.setText(TextEditor.getString("SELECT_ALL"));
		contextmenuitem_deleteall.setText(TextEditor.getString("DELETE_ALL"));
	}
}