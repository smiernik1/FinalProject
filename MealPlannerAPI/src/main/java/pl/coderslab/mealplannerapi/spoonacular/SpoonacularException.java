package pl.coderslab.mealplannerapi.spoonacular;

public class SpoonacularException extends RuntimeException {
    public SpoonacularException(String message) {
        super(message);
    }

    public SpoonacularException(String message, Throwable cause) {
        super(message, cause);
    }
}