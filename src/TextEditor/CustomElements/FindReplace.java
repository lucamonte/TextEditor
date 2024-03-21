package TextEditor.CustomElements;

import java.awt.GridLayout;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;
import TextEditor.TextEditor;
import TextEditor.Icons.Icons;
import javax.swing.JCheckBox;

public class FindReplace extends JDialog {
	private static final long serialVersionUID = 1L;
	private JLabel label_find;
	private JTextField textfield_find;
	private JButton button_find;
	private JTextField textfield_replace;
	private JCheckBox checkbox_regex;
	private Action find_action;

	public FindReplace() {
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(TextEditor.getString("FIND_REPLACE_TITLE"));
		setSize(690, 151);
		getContentPane().setLayout(null);
		setIconImage(Icons.getImage(Icons.IconTypes.APPLICATION));
		find_action = createFindAction();

		label_find = new JLabel(TextEditor.getString("FIND_LABEL_TEXT"));
		label_find.setBounds(80, 20, 32, 14);
		getContentPane().add(label_find);

		textfield_find = new JTextField();
		textfield_find.setBounds(117, 17, 406, 21);
		getContentPane().add(textfield_find);
		textfield_find.setColumns(50);
		textfield_find.addActionListener(find_action);

		button_find = new JButton(TextEditor.getString("FIND_BUTTON_TEXT"));

		button_find.addActionListener(find_action);

		button_find.setBounds(528, 16, 85, 23);
		getContentPane().add(button_find);

		JPanel find_replace_panel = new JPanel(new GridLayout(3, 2));
		find_replace_panel.setBounds(594, 16, 0, 0);

		getContentPane().add(find_replace_panel);

		JLabel label_replace = new JLabel(TextEditor.getString("REPLACE_LABEL_TEXT"));
		label_replace.setBounds(41, 79, 71, 14);
		getContentPane().add(label_replace);

		textfield_replace = new JTextField();
		textfield_replace.setColumns(50);
		textfield_replace.setBounds(117, 76, 406, 21);
		getContentPane().add(textfield_replace);

		JButton button_replace = new JButton(TextEditor.getString("REPLACE_BUTTON_TEXT"));

		button_replace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TextEditor.getTextArea().setText(TextEditor.getTextArea().getText().replace(textfield_find.getText(), textfield_replace.getText()));
			}
		});

		button_replace.setBounds(528, 75, 85, 23);
		getContentPane().add(button_replace);

		checkbox_regex = new JCheckBox(TextEditor.getString("REGEX_LABEL"));
		checkbox_regex.setBounds(117, 45, 154, 23);
		getContentPane().add(checkbox_regex);
	}

	public void addEscapeListener(final JDialog dialog) {
		ActionListener escape_listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		};

		dialog.getRootPane().registerKeyboardAction(escape_listener,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	private AbstractAction createFindAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = TextEditor.getTextArea().getText();
				int start_index = -1;
				int end_index = 0;

				if(!checkbox_regex.isSelected()) {
					start_index = text.indexOf(textfield_find.getText());
					end_index = start_index + textfield_find.getText().length();
				} else {
					Pattern pattern = Pattern.compile(textfield_find.getText());
					Matcher matcher = pattern.matcher(text);

					if(matcher.find()) {
						start_index = matcher.start();
						end_index = matcher.end();
					}
				}

				if (start_index != -1) {
					TextEditor.getTextArea().requestFocusInWindow();
					TextEditor.getTextArea().select(start_index, end_index);
				} else {
					TextEditor.showMessageDialog(TextEditor.getString("UNABLE_TO_FIND_TEXT_TITLE"), TextEditor.getString("UNABLE_TO_FIND_TEXT_MESSAGE"), JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
	}
}