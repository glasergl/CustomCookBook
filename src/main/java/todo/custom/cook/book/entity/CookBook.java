package todo.custom.cook.book.entity;

import java.util.Set;

public record CookBook(String name, String author, Set<Recipe> recipes) {
}
