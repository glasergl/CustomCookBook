package todo.custom.cook.book;

import java.io.IOException;
import java.util.Optional;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import todo.custom.cook.book.entity.CookBook;
import todo.custom.cook.book.io.CookBookIO;
import todo.custom.cook.book.ui.CookBookEditor;
import todo.custom.cook.book.ui.UICustomization;

public class Main {
    public static void main(final String[] commandLineArguments) throws IOException {
	Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
	    JOptionPane.showMessageDialog(null, String.format("%s: %s", e.getClass()
		    .getSimpleName(), e.getMessage()), "Unbehandelte Ausnahme", JOptionPane.ERROR_MESSAGE);
	    System.exit(1);
	});
	final CookBookIO cookBookIO = new CookBookIO();
	final Optional<CookBook> storedCookBook = cookBookIO.cookBookExists() ? Optional.of(cookBookIO.get()) : Optional.empty();
	UICustomization.initialize();
	SwingUtilities.invokeLater(() -> {
	    if (storedCookBook.isPresent()) {
		new CookBookEditor(storedCookBook.get());
	    } else {
		new CookBookEditor();
	    }
	});
    }
}
