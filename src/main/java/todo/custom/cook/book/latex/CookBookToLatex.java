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
import static todo.jlatex.LatexLine.f;
import static todo.jlatex.LatexCommand.c;

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
		.line(c("title", cookBook.name()))
		.line(c("author", cookBook.author()))
		.line(c("setlength", "\\parindent", "0cm"));
	formatChapterAndSection();
    }

    private void addTitlePage() {
	latexDocument.line(c("frontmatter"))
		.line(c("maketitle"))
		.line(c("tableofcontents"))
		.line(c("mainmatter"));
    }

    private void addRecipes() {
	final List<String> sortedGroupNames = new ArrayList<>(recipesByGroupName.keySet());
	Collections.sort(sortedGroupNames);
	for (final String groupName : sortedGroupNames) {
	    latexDocument.line(c("chapter", groupName));
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
	latexDocument.line(c("section", recipe.name()))
		.beginEnvironment("flushleft")
		.beginEnvironment("tabular", "@{}ll@{}")
		.line(f("%&%\\\\", c("textit", "Zubereitungsdauer"), f(recipe.duration())))
		.line(f("%&%\\\\", c("textit", "Portionen"), f(recipe.numberOfPortions())))
		.line(f("%&", c("textit", "Zutaten")))
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
		.endEnvironment("flushleft");
	addSeparator();
	latexDocument.line("\\textit{Zubereitungsschritte}")
		.beginEnvironment("enumerate");
	for (final String step : recipe.steps()) {
	    latexDocument.line(String.format("\\item %s", step));
	}
	latexDocument.endEnvironment("enumerate")
		.line(c("newpage"));
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
	latexDocument.line("\\setkomafont{chapter}{\\Huge\\fontfamily{pzc}\\selectfont}")
		.line("\\renewcommand*{\\chapterformat}{%")
		.line("\\centering\\chaptername~\\thechapter\\par\\vspace{1ex}%")
		.line("}")
		.line("\\renewcommand*{\\chapterlinesformat}[3]{%")
		.line("\\centering #2#3%")
		.line("}");
    }

    private void addSeparator() {
	latexDocument.line("\\begin{center}")
		.line("\\begin{tikzpicture}")
		.line("\\draw (-4,0) -- (-0.9,0);")
		.line("\\draw (0.9,0) -- (4,0);")
		.line("\\node at (0,0) {")
		.line("\\begin{tikzpicture}[scale=0.12]")
		.line("\\foreach \\angle in {45, 135, 225, 315} {")
		.line("\\draw[draw=none, fill=green!60!black] (0,0) ++(\\angle:1.0) circle (0.75);")
		.line("}")
		.line("\\fill[draw=none, fill=green!70!black] (0,0) circle (0.25);")
		.line("\\end{tikzpicture}")
		.line("};")
		.line("\\end{tikzpicture}")
		.line("\\end{center}");

    }
}
