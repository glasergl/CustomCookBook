package todo.custom.cook.book.ui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
    }

    public static JPanel getLeftLabelledComponent(final JComponent component, final String labelText, final int horizontalDistance) {
	final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, horizontalDistance, horizontalDistance));
	panel.add(new JLabel(labelText));
	panel.add(component);
	return panel;
    }

    public static JPanel getAboveLabelledComponent(final JComponent component, final String labelText, final int verticalDistance) {
	JPanel panel = new JPanel(new GridBagLayout());
	GridBagConstraints gbc = new GridBagConstraints();

	// Label at the top (row 0)
	gbc.gridx = 0;
	gbc.gridy = 0;
	gbc.anchor = GridBagConstraints.WEST; // Left-align label
	gbc.insets = new Insets(0, 0, verticalDistance, 0); // bottom spacing = verticalDistance
	gbc.fill = GridBagConstraints.NONE;
	gbc.weightx = 0;
	gbc.weighty = 0;
	JLabel label = new JLabel(labelText);
	panel.add(label, gbc);

	// Component below (row 1)
	gbc.gridy = 1;
	gbc.insets = new Insets(0, 0, 0, 0); // no extra spacing below
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 1.0;
	gbc.weighty = 0;
	panel.add(component, gbc);

	return panel;
    }
}
