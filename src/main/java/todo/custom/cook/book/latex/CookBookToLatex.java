package todo.custom.cook.book.latex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import todo.custom.cook.book.entity.CookBook;
import todo.custom.cook.book.entity.Ingredient;
import todo.custom.cook.book.entity.Recipe;
import todo.jlatex.LatexCommand;
import todo.jlatex.LatexDocument;

public final class CookBookToLatex {
    private final LatexDocument latexDocument;
    private final CookBook cookBook;
    private final Map<String, Set<Recipe>> recipesByGroupName;

    public CookBookToLatex(final CookBook cookBook) {
	this.latexDocument = new LatexDocument("scrbook", "12pt, headings=big");
	this.cookBook = cookBook;
	this.recipesByGroupName = orderByGroupName();
	addPreamble();
	latexDocument.beginDocument();
	addTitlePage();
	addRecipes();
	latexDocument.endDocument();
    }

    private void addPreamble() {
	latexDocument.usePackage("babel", "ngerman")
		.usePackage("fontenc", "T1")
		.usePackage("lmodern")
		.usePackage("enumitem")
		.line(LatexCommand.get("title", cookBook.name()))
		.line(LatexCommand.get("author", cookBook.author()))
		.line(LatexCommand.get("setlength", "\\parindent", "0cm"));
    }

    private void addTitlePage() {
	latexDocument.line(LatexCommand.get("maketitle"))
		.line(LatexCommand.get("tableofcontents"));
    }

    private void addRecipes() {
	final List<String> sortedGroupNames = new ArrayList<>(recipesByGroupName.keySet());
	Collections.sort(sortedGroupNames);
	for (final String groupName : sortedGroupNames) {
	    latexDocument.line(LatexCommand.get("chapter", groupName));
	    addRecipesOfGroup(groupName);
	}
    }

    private void addRecipesOfGroup(final String groupName) {
	final List<Recipe> sortedRecipes = new ArrayList<>(recipesByGroupName.get(groupName));
	sortedRecipes.sort((r1, r2) -> {
	    return r1.name()
		    .compareTo(r2.name());
	});
	for (final Recipe recipe : sortedRecipes) {
	    addRecipe(recipe);
	}
    }

    private void addRecipe(final Recipe recipe) {
	latexDocument.line(LatexCommand.get("section", recipe.name()))
		.beginEnvironment("flushleft")
		.beginEnvironment("tabular", "@{}ll@{}")
		.line(String.format("\\textit{Zubereitungsdauer}&%s\\\\", recipe.duration()))
		.line(String.format("\\textit{Portionen}&%s\\\\", recipe.numberOfPortions()))
		.line(String.format("\\textit{Zutaten}&"))
		.beginEnvironment("tabular", "@{}rl@{}");
	final List<Ingredient> sortedIngredients = new ArrayList<>(recipe.ingredients());
	sortedIngredients.sort((i1, i2) -> {
	    return i1.name()
		    .compareTo(i2.name());
	});
	for (final Ingredient ingredient : sortedIngredients) {
	    latexDocument.line(String.format("%s & %s\\\\", ingredient.amount(), ingredient.name()));
	}
	latexDocument.endEnvironment("tabular")
		.endEnvironment("tabular")
		.endEnvironment("flushleft")
		.line("\\textit{Schritte}")
		.beginEnvironment("enumerate");
	for (final String step : recipe.steps()) {
	    latexDocument.line(String.format("\\item %s", step));
	}
	latexDocument.endEnvironment("enumerate")
		.line(LatexCommand.get("newpage"));
    }

    private Map<String, Set<Recipe>> orderByGroupName() {
	final Map<String, Set<Recipe>> map = new HashMap<>();
	for (final Recipe recipe : cookBook.recipes()) {
	    if (!map.containsKey(recipe.group())) {
		map.put(recipe.group(), new HashSet<>());
	    }
	    final Set<Recipe> recipesOfSameGroup = map.get(recipe.group());
	    recipesOfSameGroup.add(recipe);
	}
	return map;
    }

    public LatexDocument get() {
	return latexDocument;
    }
}
