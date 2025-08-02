package todo.custom.cook.book.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import todo.custom.cook.book.entity.Ingredient;

public final class IngredientsEditor {
    private final JPanel ingredientsEditorPanel = new JPanel();
    private final JPanel ingredientsPanel = new JPanel();
    private final Dimension preferredSizeOfIngredientsPanel = new Dimension(200, 200);
    private final JTextField ingredientNameInput = new JTextField();
    private final JTextField ingredientAmountInput = new JTextField();
    private final JButton addIngredientButton = new JButton("+");
    private final Set<Ingredient> ingredients;

    public IngredientsEditor(final Set<Ingredient> ingredients) {
	this.ingredients = new HashSet<>(ingredients);
	setupIngredientsPanel();
	setupIngredientEditorPanel();
    }

    public IngredientsEditor() {
	this(Collections.emptySet());
    }

    private void setupIngredientsPanel() {
	ingredientsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
	ingredientsPanel.setPreferredSize(preferredSizeOfIngredientsPanel);
	for (final Ingredient ingredient : ingredients) {
	    addIngredient(ingredient);
	}
    }

    private void setupIngredientEditorPanel() {
	ingredientNameInput.setColumns(15);
	ingredientAmountInput.setColumns(11);
	addIngredientButton.addActionListener(click -> {
	    final String name = ingredientNameInput.getText();
	    final String amount = ingredientAmountInput.getText();
	    if (!name.isEmpty() && !name.isBlank() && !amount.isEmpty() && !amount.isBlank()) {
		final Ingredient ingredient = new Ingredient(name, amount);
		addIngredient(ingredient);
	    }
	});
	ingredientsEditorPanel.setLayout(new BoxLayout(ingredientsEditorPanel, BoxLayout.Y_AXIS));
	ingredientsEditorPanel.add(ingredientsPanel);
	ingredientsEditorPanel.add(createIngredientsControlPanel());
    }

    private JPanel createIngredientsControlPanel() {
	final JPanel ingredientControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	ingredientControlPanel.add(UICustomization.getLeftLabelledComponent(ingredientAmountInput, "Menge:", 3));
	ingredientControlPanel.add(UICustomization.getLeftLabelledComponent(ingredientNameInput, "Zutat:", 3));
	ingredientControlPanel.add(addIngredientButton);
	return ingredientControlPanel;
    }

    private void addIngredient(final Ingredient ingredient) {
	if (ingredients.contains(ingredient)) {
	    return;
	}
	ingredients.add(ingredient);
	final JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	panel.add(new JLabel(String.format("%s %s", ingredient.amount(), ingredient.name())));
	panel.add(Box.createHorizontalStrut(3));
	final JButton deleteIngredientButton = new JButton("-");
	deleteIngredientButton.addActionListener(click -> {
	    ingredients.remove(ingredient);
	    ingredientsPanel.remove(panel);
	    ingredientsPanel.revalidate();
	    ingredientsPanel.repaint();
	});
	panel.add(deleteIngredientButton);
	ingredientNameInput.setText("");
	ingredientAmountInput.setText("");
	ingredientsPanel.add(panel);
	ingredientsPanel.add(Box.createHorizontalStrut(7));
	ingredientsPanel.revalidate();
	ingredientsPanel.repaint();
    }

    public JPanel getPanel() {
	return ingredientsEditorPanel;
    }

    public Set<Ingredient> getIngredients() {
	return Collections.unmodifiableSet(ingredients);
    }
}
