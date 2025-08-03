package todo.custom.cook.book.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import todo.custom.cook.book.entity.Ingredient;
import todo.custom.cook.book.entity.Recipe;
import todo.custom.cook.book.util.Functions;

public final class RecipeEditor {
    private final JPanel recipeEditorPanel = new JPanel();
    private final JTextField nameInput;
    private final JTextField groupInput;
    private final JTextField durationInput;
    private final JTextField numberOfPortionsInput;
    private final JTextArea stepsInput;
    private final IngredientsEditor ingredientsEditor;

    public RecipeEditor(final Recipe recipe) {
	this.nameInput = new JTextField(recipe.name());
	this.groupInput = new JTextField(recipe.group());
	this.stepsInput = new JTextArea(getStepsAsString(recipe.steps()));
	this.durationInput = new JTextField(recipe.duration());
	this.numberOfPortionsInput = new JTextField(String.valueOf(recipe.numberOfPortions()));
	this.ingredientsEditor = new IngredientsEditor(recipe.ingredients());
	setupRecipePanel();
    }

    public RecipeEditor(final String recipeName) {
	this.nameInput = new JTextField(recipeName);
	this.groupInput = new JTextField();
	this.stepsInput = new JTextArea();
	this.durationInput = new JTextField();
	this.numberOfPortionsInput = new JTextField();
	this.ingredientsEditor = new IngredientsEditor();
	setupRecipePanel();
    }

    private void setupRecipePanel() {
	recipeEditorPanel.setLayout(new BoxLayout(recipeEditorPanel, BoxLayout.Y_AXIS));
	recipeEditorPanel.add(UICustomization.getLeftLabelledComponent(nameInput, "Name:", 5));
	recipeEditorPanel.add(UICustomization.getLeftLabelledComponent(groupInput, "Gruppe:", 5));
	recipeEditorPanel.add(UICustomization.getLeftLabelledComponent(numberOfPortionsInput, "Portionen:", 5));
	recipeEditorPanel.add(UICustomization.getLeftLabelledComponent(durationInput, "Dauer:", 5));
	recipeEditorPanel.add(ingredientsEditor.getPanel());
	final JScrollPane scrollapeStepsInput = new JScrollPane(stepsInput, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	recipeEditorPanel.add(UICustomization.getAboveLabelledComponent(scrollapeStepsInput, "Schritte:", 5));
	nameInput.setColumns(20);
	groupInput.setColumns(9);
	durationInput.setColumns(9);
	numberOfPortionsInput.setColumns(9);
	stepsInput.setColumns(50);
	stepsInput.setRows(15);
    }

    private String getStepsAsString(final List<String> steps) {
	final StringBuilder stepsAsSingleStringBuilder = new StringBuilder();
	for (final String step : steps) {
	    stepsAsSingleStringBuilder.append(step + "\n");
	}
	return stepsAsSingleStringBuilder.toString();
    }

    private List<String> getSteps(final String stepsAsSingleString) {
	final List<String> steps = new ArrayList<>();
	for (final String step : stepsAsSingleString.split("\n")) {
	    if (!Functions.emptyString(step)) {
		steps.add(step);
	    }
	}
	return steps;
    }

    public JPanel getPanel() {
	return recipeEditorPanel;
    }

    public Optional<Recipe> get() {
	final String name = nameInput.getText();
	final String group = groupInput.getText();
	final String duration = durationInput.getText();
	final String numberOfPortions = numberOfPortionsInput.getText();
	final List<String> steps = getSteps(stepsInput.getText());
	for (final String recipeAttribute : List.of(name, group, duration, numberOfPortions)) {
	    if (Functions.emptyString(recipeAttribute)) {
		return Optional.empty();
	    }
	}
	if (steps.isEmpty() || numberOfPortions.isEmpty() || numberOfPortions.isBlank()) {
	    return Optional.empty();
	}
	final Set<Ingredient> ingredients = ingredientsEditor.getIngredients();
	if (ingredients.isEmpty()) {
	    return Optional.empty();
	}
	return Optional.of(new Recipe(name, steps, duration, group, numberOfPortions, ingredients));
    }

    @Override
    public String toString() {
	return nameInput.getText();
    }
}
