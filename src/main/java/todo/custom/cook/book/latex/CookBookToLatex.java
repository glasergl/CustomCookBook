package todo.custom.cook.book.latex;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
		.usePackage("longtable")
		.usePackage("graphicx")
		.usePackage("eso-pic")
		.usePackage("xcolor")
		.usePackage("transparent")
		.line(command("title", cookBook.name()))
		.line(command("author", cookBook.author()))
		.line(command("setlength", "\\parindent", "0cm"));
	formatChapterAndSection();
    }

    private void formatChapterAndSection() {
	latexDocument.plain("""
		\\setkomafont{chapter}{\\fontsize{50}{60}\\selectfont\\fontfamily{pzc}\\selectfont}
		\\renewcommand*{\\chapterformat}{%
		\\centering\\chaptername~\\thechapter\\par\\vspace{0.1cm}%
		}
		\\renewcommand*{\\chapterlinesformat}[3]{%
		\\centering #2#3%
		}
					""")
		.line(command("renewcommand*", "\\chapterheadstartvskip", command("vspace*", "0cm")));
    }

    private void addTitlePage() {
	final int numberOfRecipes = cookBook.recipes()
		.size();
	final int numberOfGroups = recipesByGroupName.keySet()
		.size();
	latexDocument.line(command("frontmatter"))
		.beginEnvironment("titlepage");
	addBackground();
	latexDocument.line(command("centering"))
		.line(command("color", "white"))
		.line(command("vspace*", command("fill")))
		.format("\\fontsize{80}{96}\\selectfont")
		.format("\\fontfamily{pzc}\\selectfont")
		.format("%\\\\", cookBook.name())
		.line(command("vspace", "0.5cm"))
		.format("\\normalfont\\selectfont")
		.format("\\fontsize{30}{36}\\selectfont")
		.line(command("bfseries"))
		.format("von\\\\")
		.format("%\\\\", cookBook.author())
		.line(command("vspace", "1cm"))
		.format("% Rezepte aus % Kategorien\\\\", numberOfRecipes, numberOfGroups)
		.line(command("vspace", "0.3cm"))
		.format("%", getDate())
		.line(command("vspace*", command("fill")))
		.endEnvironment("titlepage")
		.line(command("tableofcontents"))
		.line(command("mainmatter"));
    }

    private void addBackground() {
	latexDocument.plain("""
		\\AddToShipoutPictureBG*{\\transparent{0.7}\\includegraphics[width=\\paperwidth,height=\\paperheight]{images/title-from-gpt.png}}
				""");

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
		.format("%: %\\\\", command("textit", "Zubereitungsdauer"), recipe.duration())
		.format("%: %\\\\", command("textit", "Portionen"), recipe.numberOfPortions())
		.format("%:", command("textit", "Zutaten"))
		.beginEnvironment("longtable", "@{}rl@{}")
		.line(command("hline"))
		.format("%\\\\", command("multicolumn", 2, "r", command("textit", "Weiter auf nächster Seite")))
		.line(command("endfoot"))
		.line(command("endlastfoot"));
	final List<Ingredient> sortedIngredients = new ArrayList<>(recipe.ingredients());
	sortedIngredients.sort((i1, i2) -> {
	    return i1.name()
		    .compareTo(i2.name());
	});
	for (final Ingredient ingredient : sortedIngredients) {
	    latexDocument.format("% & %\\\\", ingredient.amount(), ingredient.name());
	}
	latexDocument.endEnvironment("longtable");
	addSeparator();
	latexDocument.format("\\textit{Zubereitungsschritte}")
		.beginEnvironment("enumerate");
	for (final String step : recipe.steps()) {
	    latexDocument.format("% %", command("item"), step);
	}
	latexDocument.endEnvironment("enumerate")
		.line(command("newpage"));
    }

    private void addSeparator() {
	latexDocument.plain("""
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
		\\end{center}
					""");
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

    private String getDate() {
	final LocalDate today = LocalDate.now();
	final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMAN);
	return today.format(formatter);
    }

    public LatexDocument get() {
	return latexDocument;
    }
}
