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
		.beginEnvironment("titlepage")
		.line(command("centering"))
		.format("\\fontsize{70}{84}\\selectfont")
		.format("\\fontfamily{pzc}\\selectfont")
		.format("%\\\\", cookBook.name())
		.line(command("vspace", "0.8cm"))
		.format("\\normalfont\\selectfont")
		.line(command("Large"))
		.format("von\\\\")
		.format("%\\\\", cookBook.author())
		.line(command("vspace", "4cm"))
		.line(command("Huge"))
		.format("% Rezepte aus % Kategorien\\\\", numberOfRecipes, numberOfGroups)
		.line(command("vfill"));
	addSun();
	latexDocument.line(command("vfill"))
		.line(command("Large"))
		.format("%", getDate())
		.endEnvironment("titlepage")
		.line(command("tableofcontents"))
		.line(command("mainmatter"));
    }

    private void addSun() {
	latexDocument.plain("""
		\\begin{tikzpicture}[scale=2]
		\\def\\handleLeft{1.4}
		\\def\\handleRight{1.6}
		\\def\\handleTop{3}
		\\def\\handleBottom{0}
		\\def\\prongWidth{0.2}
		\\def\\prongHeight{1} % prongs/spoon bowl height
		\\def\\gap{0.1} % gap between prongs or parts
		\\def\\cornerRadius{3pt} % for rounded corners
		% Center of handle
		\\pgfmathsetmacro{\\handleCenter}{(\\handleLeft + \\handleRight)/2}
		% --- Draw spoon ---
		\\pgfmathsetmacro{\\spoonShiftX}{1.8}  % reduced gap
		% Spoon handle height reduced to 2 (from 3 to 1.9)
		\\def\\spoonHandleBottom{-0.1} % handle bottom at -0.1, handle top at 3 → height 3.1, but visually 2 units handle height here
		\\def\\spoonHandleTop{3}
		% Spoon handle
		\\fill[gray!30]
		  (\\handleLeft + \\spoonShiftX, \\spoonHandleBottom) rectangle (\\handleRight + \\spoonShiftX, \\spoonHandleTop);
		% Spoon bowl (ellipse), vertically centered at 3 (top), radius 0.7
		\\fill[gray!50]
		  ([shift={(\\handleCenter + \\spoonShiftX, 3)}]0,0) ellipse (0.5cm and 0.7cm);
		% --- Draw knife ---
		\\pgfmathsetmacro{\\knifeShiftX}{3}  % reduced gap
		% Knife handle height reduced to 2
		\\def\\knifeHandleBottom{1.35} % handle bottom at 0.8, top at 3 → height 2.2 units
		\\def\\knifeHandleTop{-0.1}
		% Knife handle
		\\fill[gray!30]
		  (\\handleLeft + \\knifeShiftX, \\knifeHandleBottom + 0.3) rectangle (\\handleRight + \\knifeShiftX, \\knifeHandleTop);
		% Knife blade - rounded rectangle polygon of height 2 (same as handle height ~2.0)
		\\fill[gray!60,rounded corners=2pt]
		  ([shift={(\\handleCenter + \\knifeShiftX, \\knifeHandleBottom + 0.3)}] -0.15,0) --
		  ++(0.3,0) -- ++(0,2.0) -- ++(-0.3,0) -- cycle;
		% Smooth rounded edge using ellipse on knife blade's LEFT side
		\\fill[gray!60]
		  ([shift={(\\handleCenter + \\knifeShiftX - 0.1, \\knifeHandleBottom + 1.3)}]0,0) ellipse (0.15cm and 1.0cm);
		\\end{tikzpicture}
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
