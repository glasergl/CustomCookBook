package todo.custom.cook.book.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import todo.custom.cook.book.entity.CookBook;
import todo.custom.cook.book.entity.Recipe;
import todo.custom.cook.book.io.CookBookIO;
import todo.custom.cook.book.latex.CookBookToLatex;
import todo.custom.cook.book.util.Functions;
import todo.jlatex.GeneratePdf;

public final class CookBookEditor {
    private final JFrame frame = new JFrame("Eigenes Kochbuch!");
    private final Container contentPane = frame.getContentPane();
    private final JTextField nameInput = new JTextField();
    private final JTextField authorInput = new JTextField();
    private final JComboBox<RecipeEditor> recipeSelector = new JComboBox<>();
    private final JButton addEmptyRecipeButton = new JButton("Neues Rezept");
    private final JButton saveButton = new JButton("Speichern");
    private final JButton exportToPdfButton = new JButton("PDF Exportieren");
    private final JPanel recipePanel = new JPanel(new BorderLayout());

    public CookBookEditor(final CookBook cookBook) {
	super();
	nameInput.setText(cookBook.name());
	authorInput.setText(cookBook.author());
	final List<Recipe> sortedRecipes = new ArrayList<>(cookBook.recipes());
	sortedRecipes.sort((r1, r2) -> {
	    return r1.name()
		    .compareTo(r2.name());
	});
	for (final Recipe recipe : sortedRecipes) {
	    recipeSelector.addItem(new RecipeEditor(recipe));
	}
	setup();
	recipeSelector.setSelectedIndex(0);
    }

    public CookBookEditor() {
	setup();
    }

    private void setup() {
	recipePanel.setPreferredSize(new RecipeEditor("").getPanel()
		.getPreferredSize());
	addComponents();
	try {
	    frame.setIconImage(ImageIO.read(getClass().getResourceAsStream("/CookBookIcon.png")));
	} catch (final IOException e) {
	    e.printStackTrace();
	}
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	frame.pack();
	frame.setLocationRelativeTo(null);
	frame.setVisible(true);
    }

    private void addComponents() {
	contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
	contentPane.add(UICustomization.getLeftLabelledComponent(nameInput, "Name:", 10));
	contentPane.add(UICustomization.getLeftLabelledComponent(authorInput, "Autor:", 10));
	contentPane.add(UICustomization.getLeftLabelledComponent(recipeSelector, "Rezepte:", 10));
	final JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
	buttonRow.add(addEmptyRecipeButton);
	buttonRow.add(saveButton);
	buttonRow.add(exportToPdfButton);
	contentPane.add(buttonRow);
	setupButtons();
	nameInput.setColumns(40);
	authorInput.setColumns(20);

	final JPanel separator = new JPanel();
	separator.setBackground(Color.LIGHT_GRAY);
	separator.setPreferredSize(new Dimension(1, 1)); // width will be set by layout, 2 is desired height
	contentPane.add(separator);
	contentPane.add(recipePanel);
    }

    private void setupButtons() {
	recipeSelector.addActionListener(click -> {
	    final RecipeEditor selectedRecipeEditor = (RecipeEditor) recipeSelector.getSelectedItem();
	    recipePanel.removeAll();
	    recipePanel.add(selectedRecipeEditor.getPanel(), BorderLayout.CENTER);
	    recipePanel.revalidate();
	    recipePanel.repaint();
	});
	addEmptyRecipeButton.addActionListener(click -> {
	    final RecipeEditor recipeEditor = new RecipeEditor("Neues Rezept");
	    recipeSelector.addItem(recipeEditor);
	    recipeSelector.setSelectedItem(recipeEditor);
	});
	saveButton.addActionListener(click -> {
	    new SaveCookBook();
	});
	exportToPdfButton.addActionListener(click -> {
	    new ExportPdf();
	});
    }

    public Optional<CookBook> get() {
	final String name = nameInput.getText();
	final String author = authorInput.getText();
	if (Functions.emptyString(name) || Functions.emptyString(author) || recipeSelector.getItemCount() == 0) {
	    return Optional.empty();
	}
	final Set<Recipe> recipes = new HashSet<>();
	for (final RecipeEditor recipeEditor : getRecipeEditors()) {
	    final Optional<Recipe> recipe = recipeEditor.get();
	    if (recipe.isPresent()) {
		recipes.add(recipe.get());
	    } else {
		return Optional.empty();
	    }
	}
	return Optional.of(new CookBook(name, author, recipes));
    }

    private List<RecipeEditor> getRecipeEditors() {
	final List<RecipeEditor> recipeEditors = new ArrayList<>(recipeSelector.getItemCount());
	for (int i = 0; i < recipeSelector.getItemCount(); i++) {
	    recipeEditors.add(recipeSelector.getItemAt(i));
	}
	return recipeEditors;
    }

    private final class SaveCookBook extends SwingWorker<Boolean, Void> {
	private SaveCookBook() {
	    execute();
	}

	@Override
	public Boolean doInBackground() throws Exception {
	    saveButton.setEnabled(false);
	    final CookBookIO cookBookIO = new CookBookIO();
	    final Optional<CookBook> cookBook = CookBookEditor.this.get();
	    if (cookBook.isPresent()) {
		cookBookIO.store(cookBook.get());
	    }
	    return cookBook.isPresent();
	}

	@Override
	public void done() {
	    try {
		final boolean saveSuccessful = get();
		if (!saveSuccessful) {
		    JOptionPane.showMessageDialog(frame, "Bitte füge jedem Feld einen erlaubten Wert hinzu und probiere erneut", "Speichern nicht möglich", JOptionPane.ERROR_MESSAGE);
		}
	    } catch (final InterruptedException | ExecutionException e) {
		/*
		 * not reachable, because done() is only called after doInBackground is
		 * finished, i.e., get() of SwingWorker doesn't block
		 */
	    } finally {
		saveButton.setEnabled(true);
	    }
	}
    }

    private final class ExportPdf extends SwingWorker<Boolean, Void> {
	private ExportPdf() {
	    execute();
	}

	@Override
	public Boolean doInBackground() throws Exception {
	    exportToPdfButton.setEnabled(false);
	    final Optional<CookBook> cookBook = CookBookEditor.this.get();
	    if (cookBook.isPresent()) {
		final CookBookToLatex cookBookToLateX = new CookBookToLatex(cookBook.get());
		new GeneratePdf(cookBookToLateX.get());
	    }
	    return cookBook.isPresent();
	}

	@Override
	public void done() {
	    try {
		final boolean exportSuccessful = get();
		if (!exportSuccessful) {
		    JOptionPane.showMessageDialog(frame, "Bitte füge jedem Feld einen erlaubten Wert hinzu und probiere erneut", "Exportieren nicht möglich", JOptionPane.ERROR_MESSAGE);
		}
	    } catch (final InterruptedException | ExecutionException e) {
		/*
		 * not reachable, because done() is only called after doInBackground is
		 * finished, i.e., get() of SwingWorker doesn't block
		 */
	    } finally {
		exportToPdfButton.setEnabled(true);
	    }
	}
    }
}
