package todo.custom.cook.book.entity;

import java.util.List;
import java.util.Set;

public record Recipe(String name, List<String> steps, String duration, String group, int numberOfPortions, Set<Ingredient> ingredients) {
}
