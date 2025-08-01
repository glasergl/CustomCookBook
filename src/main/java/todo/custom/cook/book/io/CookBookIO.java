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

    public CookBook get() throws IOException {
	final String jsonContent = Files.readString(cookBookFilePath);
	return jsonParser.fromJson(jsonContent, CookBook.class);
    }

    public void store(final CookBook cookBook) throws IOException {
	final String jsonContent = jsonParser.toJson(cookBook);
	Files.writeString(cookBookFilePath, jsonContent, StandardOpenOption.WRITE);
    }
}
