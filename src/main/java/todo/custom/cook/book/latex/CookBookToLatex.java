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
import todo.jlatex.LatexDocument;
import static todo.jlatex.LatexCommand.command;

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
		.usePackage("makecell")
		.usePackage("tikz")
		.line(command("title", cookBook.name()))
		.line(command("author", cookBook.author()))
		.line(command("setlength", "\\parindent", "0cm"));
	formatChapterAndSection();
    }

    private void addTitlePage() {
	latexDocument.line(command("frontmatter"))
		.line(command("maketitle"))
		.line(command("tableofcontents"))
		.line(command("mainmatter"));
    }

    private void addRecipes() {
	final List<String> sortedGroupNames = new ArrayList<>(recipesByGroupName.keySet());
	Collections.sort(sortedGroupNames);
	for (final String groupName : sortedGroupNames) {
	    latexDocument.line(command("chapter", groupName));
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
	latexDocument.line(command("section", recipe.name()))
		.beginEnvironment("flushleft")
		.beginEnvironment("tabular", "@{}ll@{}")
		.format("%&%\\\\", command("textit", "Zubereitungsdauer"), recipe.duration())
		.format("%&%\\\\", command("textit", "Portionen"), recipe.numberOfPortions())
		.format("%&", command("textit", "Zutaten"))
		.beginEnvironment("tabular", "@{}rl@{}");
	final List<Ingredient> sortedIngredients = new ArrayList<>(recipe.ingredients());
	sortedIngredients.sort((i1, i2) -> {
	    return i1.name()
		    .compareTo(i2.name());
	});
	for (final Ingredient ingredient : sortedIngredients) {
	    latexDocument.format("% & %\\\\", ingredient.amount(), ingredient.name());
	}
	latexDocument.endEnvironment("tabular")
		.endEnvironment("tabular")
		.endEnvironment("flushleft");
	addSeparator();
	latexDocument.format("\\textit{Zubereitungsschritte}")
		.beginEnvironment("enumerate");
	for (final String step : recipe.steps()) {
	    latexDocument.format("% %", command("item"), step);
	}
	latexDocument.endEnvironment("enumerate")
		.line(command("newpage"));
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

    private void formatChapterAndSection() {
	latexDocument.format("""
		\\setkomafont{chapter}{\\fontsize{50}{60}\\selectfont\\fontfamily{pzc}\\selectfont}
		\\renewcommand*{\\chapterformat}{%%
		\\centering\\chaptername~\\thechapter\\par\\vspace{1ex}%%
		}
		\\renewcommand*{\\chapterlinesformat}[3]{%%
		\\centering #2#3%%
		}
					""");
    }

    private void addSeparator() {
	latexDocument.format("""
		\\begin{center}
		\\begin{tikzpicture}
		\\draw (-4,0) -- (-0.9,0);
		\\draw (0.9,0) -- (4,0);
		\\node at (0,0) {
		\\begin{tikzpicture}[scale=0.12]
		\\foreach \\angle in {45, 135, 225, 315} {
		\\draw[draw=none, fill=green!60!black] (0,0) ++(\\angle:1.0) circle (0.75);
		}
		\\fill[draw=none, fill=green!70!black] (0,0) circle (0.25);
		\\end{tikzpicture}
		};
		\\end{tikzpicture}
		\\end{center};
					""");
    }
}
