package todo.custom.cook.book.util;

public class Functions {
    public static boolean emptyString(final String string) {
	return string.isEmpty() || string.isBlank();
    }

    public static boolean isInteger(final String string) {
	if (string == null || string.isEmpty() || string.isBlank()) {
	    return false;
	}
	try {
	    Integer.parseInt(string);
	    return true;
	} catch (final NumberFormatException e) {
	    return false;
	}
    }
}
