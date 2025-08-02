package todo.custom.cook.book;

import javax.swing.SwingUtilities;

import todo.custom.cook.book.ui.CookBookEditor;
import todo.custom.cook.book.ui.UICustomization;

public class Main {
    public static void main(final String[] commandLineArguments) {
	UICustomization.initialize();
	SwingUtilities.invokeLater(() -> {
	    new CookBookEditor();
	});
    }
}
