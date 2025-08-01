package todo.custom.cook.book.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JLabel;

public final class CookBookEditor {
    private final JFrame frame = new JFrame("Eigenes Kochbuch!");
    private final Container contentPane = frame.getContentPane();

    public CookBookEditor() {
	contentPane.add(new JLabel("Test"), BorderLayout.CENTER);
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	frame.pack();
	frame.setLocationRelativeTo(null);
	frame.setVisible(true);
    }
}
