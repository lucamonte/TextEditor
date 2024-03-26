package TextEditor.CustomElements;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import TextEditor.TextEditor;
import TextEditor.Icons.Icons;
import TextEditor.Logger.Logger;

public class FindReplace extends JDialog {
	private static final long serialVersionUID = 1L;
	private JLabel label_find;
	private JComboBox<String> textfield_find;
	private JButton button_find;
	private JButton button_replace;
	private JComboBox<String> textfield_replace;
	private JCheckBox checkbox_regex;
	private JCheckBox checkbox_case_insensitive;
	private JCheckBox checkbox_replace_all;
	private Action find_action;
	private Action replace_action;
	private int last_index = -1;

	public FindReplace() {
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(TextEditor.getString("FIND_REPLACE_TITLE"));
		setSize(690, 175);
		getContentPane().setLayout(null);
		setIconImage(Icons.getImage(Icons.IconTypes.APPLICATION));
		find_action = createFindAction();
		replace_action = createReplaceAction();

		label_find = new JLabel(TextEditor.getString("FIND_LABEL_TEXT"));
		label_find.setBounds(80, 20, 32, 14);
		getContentPane().add(label_find);

		textfield_find = new JComboBox<String>();
		textfield_find.setBounds(117, 17, 406, 21);
		getContentPane().add(textfield_find);
		textfield_find.addActionListener(find_action);
		textfield_find.setEditable(true);

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

		textfield_replace = new JComboBox<String>();
		textfield_replace.setBounds(117, 76, 406, 21);
		getContentPane().add(textfield_replace);
		textfield_replace.addActionListener(replace_action);
		textfield_replace.setEditable(true);

		button_replace = new JButton(TextEditor.getString("REPLACE_BUTTON_TEXT"));

		button_replace.addActionListener(replace_action);

		button_replace.setBounds(528, 75, 85, 23);
		getContentPane().add(button_replace);

		checkbox_regex = new JCheckBox(TextEditor.getString("REGEX_LABEL"));
		checkbox_regex.setBounds(117, 45, 155, 23);
		getContentPane().add(checkbox_regex);

		checkbox_regex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkbox_regex.isSelected()) {
					checkbox_case_insensitive.setSelected(false);
					checkbox_case_insensitive.setEnabled(false);
				} else {
					checkbox_case_insensitive.setEnabled(true);
				}
			}
		});

		checkbox_case_insensitive = new JCheckBox(TextEditor.getString("CASE_SENSITIVE_LABEL"));
		checkbox_case_insensitive.setBounds(291, 45, 200, 23);
		getContentPane().add(checkbox_case_insensitive);
		
		checkbox_replace_all = new JCheckBox(TextEditor.getString("REPLACE_ALL_LABEL"));
		checkbox_replace_all.setBounds(117, 104, 180, 23);
		getContentPane().add(checkbox_replace_all);
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
				if(e.getActionCommand().equals("comboBoxEdited") || e.getActionCommand().equals(TextEditor.getString("FIND_BUTTON_TEXT"))) {
					findText(textfield_find.getSelectedItem().toString(), TextEditor.getTextArea(), checkbox_regex.isSelected(), checkbox_case_insensitive.isSelected());
					addComboBoxItem(textfield_find);
				}
			}
		};
	}

	private AbstractAction createReplaceAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("comboBoxEdited") || e.getActionCommand().equals(TextEditor.getString("REPLACE_BUTTON_TEXT"))) {
					replaceText(textfield_replace.getSelectedItem().toString(), checkbox_replace_all.isSelected(), TextEditor.getTextArea());
					addComboBoxItem(textfield_replace);
				}
			}
		};
	}

	private boolean findText(String search_text, JTextArea textarea, boolean regex, boolean case_insensitive) {
		String text = TextEditor.getTextArea().getText();
		int start_index = -1;
		int end_index = 0;

		if(last_index != -1 && last_index < text.length()) {
			text = text.substring(last_index);
		} else {
			last_index = -1;
		}

		if(case_insensitive) {
			text = text.toLowerCase();
			search_text = search_text.toLowerCase();
		}

		if(!regex) {
			if(text.indexOf(search_text) != -1) {
				start_index = text.indexOf(search_text) + last_index;
				
				if(start_index == -1) {
					start_index = 0;
				}
				
				end_index = start_index + search_text.length();
			}
		} else {
			Pattern pattern = Pattern.compile(search_text);
			Matcher matcher = pattern.matcher(text);

			if(matcher.find()) {
				start_index = matcher.start() + last_index;
				end_index = matcher.end() + last_index;
			}
		}

		if (start_index != -1) {
			textarea.requestFocusInWindow();
			textarea.select(start_index, end_index);
			last_index = end_index;
			return true;
		} else {
			TextEditor.showMessageDialog(TextEditor.getString("UNABLE_TO_FIND_TEXT_TITLE"), TextEditor.getString("UNABLE_TO_FIND_TEXT_MESSAGE"), JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
	}

	private void replaceText(String replace_text, boolean replace_all, JTextArea textarea) {
		replaceText(textarea.getSelectedText(), replace_text, replace_all, textarea);
	}

	private void replaceText(String search_text, String replace_text, boolean replace_all, JTextArea textarea) {
		try {
			textarea.setText(replace_all ? textarea.getText().replace(search_text, replace_text) : textarea.getText().replaceFirst(search_text, replace_text));
		} catch (NullPointerException ignore) {
			
		} catch (Exception e) {
			Logger.writeLog(e);
		}
	}
	
	private void addComboBoxItem(JComboBox<String> combobox) {
		for(int i = 0; i < combobox.getItemCount(); i++) {
			if(combobox.getItemAt(i).toString().equals(combobox.getSelectedItem().toString())) {
				return;
			}
		}
		
		combobox.addItem(combobox.getSelectedItem().toString());
	}
}