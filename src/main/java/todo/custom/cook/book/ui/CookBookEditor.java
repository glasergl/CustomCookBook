package todo.custom.cook.book.ui;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import todo.custom.cook.book.entity.CookBook;

public final class CookBookEditor {
    private final JFrame frame = new JFrame("Eigenes Kochbuch!");
    private final Container contentPane = frame.getContentPane();
    private final JTextField nameInput = new JTextField();
    private final JTextField authorInput = new JTextField();

    public CookBookEditor() {
	addComponents();
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	frame.setResizable(false);
	frame.pack();
	frame.setLocationRelativeTo(null);
	frame.setVisible(true);
    }

    private void addComponents() {
	contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
	contentPane.add(UICustomization.getLeftLabelledComponent(nameInput, "Name:", 10));
	contentPane.add(UICustomization.getLeftLabelledComponent(authorInput, "Autor:", 10));
	nameInput.setColumns(30);
	authorInput.setColumns(20);

	final RecipeEditor r = new RecipeEditor();
	final JPanel recipeEditorPanel = r.getPanel();
	recipeEditorPanel.setBorder(new TitledBorder("Rezept"));
	contentPane.add(recipeEditorPanel);
    }

    public CookBook get() {
	return null;
    }
}
