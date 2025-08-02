package todo.custom.cook.book.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.google.gson.Gson;

import todo.custom.cook.book.entity.CookBook;

public final class CookBookIO {
    private final Path cookBookFilePath = Path.of("./cookBook.json");
    private final Gson jsonParser = new Gson();

    public boolean cookBookExists() {
	return Files.exists(cookBookFilePath);
    }

    public CookBook get() {
	try {
	    final String jsonContent = Files.readString(cookBookFilePath);
	    return jsonParser.fromJson(jsonContent, CookBook.class);
	} catch (final IOException e) {
	    throw new IllegalStateException("Unable to fetch existing cook book from file system", e);
	}
    }

    public void store(final CookBook cookBook) {
	try {
	    final String jsonContent = jsonParser.toJson(cookBook);
	    if (cookBookExists()) {
		Files.writeString(cookBookFilePath, jsonContent, StandardOpenOption.WRITE);
	    } else {
		Files.writeString(cookBookFilePath, jsonContent, StandardOpenOption.CREATE_NEW);
	    }
	} catch (final IOException e) {
	    throw new IllegalStateException("Unabel to store cook book to file system", e);
	}
    }
}
