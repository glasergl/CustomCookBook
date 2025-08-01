package todo.custom.cook.book;

import todo.jlatex.GeneratePdf;
import todo.jlatex.LatexDocument;

public class Main {
	public static void main(final String[] commandLineArguments) {
		final LatexDocument cookBook = new LatexDocument("book")
				.beginDocument()
				.line("Mein erster Satz")
				.endDocument();
		System.out.println(cookBook.toString());
		new GeneratePdf(cookBook);
	}
}
