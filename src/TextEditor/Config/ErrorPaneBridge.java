package TextEditor.Config;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class ErrorPaneBridge {

	private static JEditorPane errorPane = null;

	public static void setErrorPane(String text) {
		setErrorPane(text, "");
	}

	public static void setErrorPane(String text, String url) {
		errorPane = new JEditorPane();
		errorPane.setContentType("text/html");
		errorPane.setText(text);
		errorPane.setOpaque(false);
		errorPane.setEditable(false);
		errorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

		if(!url.equals("")) {
			errorPane.addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent event) {
					if(event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						try {

							Desktop.getDesktop().browse(new URI(url));

						} catch (IOException | URISyntaxException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	public static JEditorPane getErrorPane() {
		return errorPane;
	}
}
