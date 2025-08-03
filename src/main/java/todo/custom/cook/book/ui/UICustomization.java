package todo.custom.cook.book.ui;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class UICustomization {
    public static void initialize() {
	UIManager.put("Label.font", new Font(Font.SANS_SERIF, Font.PLAIN, 16));
	UIManager.put("TextField.font", new Font(Font.SANS_SERIF, Font.PLAIN, 16));
	UIManager.put("Button.font", new Font(Font.SANS_SERIF, Font.PLAIN, 16));
	UIManager.put("TextArea.font", new Font(Font.SANS_SERIF, Font.PLAIN, 16));
	UIManager.put("OptionPane.messageFont", new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

    public static JPanel getLeftLabelledComponent(final JComponent component, final String labelText, final int horizontalDistance) {
	final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, horizontalDistance, horizontalDistance));
	panel.add(new JLabel(labelText));
	panel.add(component);
	return panel;
    }
}
