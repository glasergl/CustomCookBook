package todo.custom.cook.book.ui;

import java.awt.Container;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import todo.custom.cook.book.entity.CookBook;
import todo.custom.cook.book.entity.Recipe;
import todo.custom.cook.book.util.Functions;

public final class CookBookEditor {
    private final JFrame frame = new JFrame("Eigenes Kochbuch!");
    private final Container contentPane = frame.getContentPane();
    private final JTextField nameInput = new JTextField();
    private final JTextField authorInput = new JTextField();
    private final Set<RecipeEditor> recipeEditors = new HashSet<>();
    private Optional<RecipeEditor> currentReceipe = Optional.empty();

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
	final String name = nameInput.getText();
	final String author = authorInput.getText();
	if (Functions.emptyString(name) || Functions.emptyString(author) || recipeEditors.isEmpty()) {
	    throw new IllegalStateException();
	}
	final Set<Recipe> recipes = new HashSet<>();
	for (final RecipeEditor recipeEditor : recipeEditors) {
	    recipes.add(recipeEditor.get());
	}
	return new CookBook(name, author, recipes);
    }
}
