package todo.custom.cook.book.ui;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import todo.custom.cook.book.entity.Recipe;

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

    public RecipeEditor() {
	this.nameInput = new JTextField();
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
	numberOfPortionsInput.setColumns(3);
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

    public JPanel getPanel() {
	return recipeEditorPanel;
    }

    public Recipe get() {
	// TODO
	return null;
    }
}
