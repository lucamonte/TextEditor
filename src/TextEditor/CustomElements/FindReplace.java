package TextEditor.CustomElements;

import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import TextEditor.TextEditor;
import TextEditor.Icons.Icons;

public class FindReplace extends JDialog {
	private static final long serialVersionUID = 1L;
	private JLabel label_find;
	private JTextField textfield_find;
	private JButton button_find;
	private JTextField textfield_replace;

	public FindReplace() {
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(TextEditor.getString("FIND_REPLACE_TITLE"));
		setSize(690, 118);
		getContentPane().setLayout(null);
		setIconImage(Icons.getImage(Icons.IconTypes.APPLICATION));

		label_find = new JLabel(TextEditor.getString("FIND_LABEL_TEXT"));
		label_find.setBounds(80, 20, 32, 14);
		getContentPane().add(label_find);

		textfield_find = new JTextField();
		textfield_find.setBounds(117, 17, 406, 21);
		getContentPane().add(textfield_find);
		textfield_find.setColumns(50);

		button_find = new JButton(TextEditor.getString("FIND_BUTTON_TEXT"));

		button_find.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = TextEditor.getTextArea().getText();
				int index = text.indexOf(textfield_find.getText());

				if (index != -1) {
					TextEditor.getTextArea().requestFocusInWindow();
					TextEditor.getTextArea().select(index, index + text.length());
				} else {
					TextEditor.showMessageDialog(TextEditor.getString("UNABLE_TO_FIND_TEXT_TITLE"), TextEditor.getString("UNABLE_TO_FIND_TEXT_MESSAGE"), JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		button_find.setBounds(528, 16, 85, 23);
		getContentPane().add(button_find);

		JPanel findReplacePanel = new JPanel(new GridLayout(3, 2));
		findReplacePanel.setBounds(594, 16, 0, 0);

		getContentPane().add(findReplacePanel);

		JLabel label_replace = new JLabel(TextEditor.getString("REPLACE_LABEL_TEXT"));
		label_replace.setBounds(41, 46, 71, 14);
		getContentPane().add(label_replace);

		textfield_replace = new JTextField();
		textfield_replace.setColumns(50);
		textfield_replace.setBounds(117, 43, 406, 21);
		getContentPane().add(textfield_replace);

		JButton button_replace = new JButton(TextEditor.getString("REPLACE_BUTTON_TEXT"));

		button_replace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TextEditor.getTextArea().setText(TextEditor.getTextArea().getText().replace(textfield_find.getText(), textfield_replace.getText()));
			}
		});

		button_replace.setBounds(528, 42, 85, 23);
		getContentPane().add(button_replace);
	}

	public void addEscapeListener(final JDialog dialog) {
		ActionListener escapeListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		};

		dialog.getRootPane().registerKeyboardAction(escapeListener,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
}