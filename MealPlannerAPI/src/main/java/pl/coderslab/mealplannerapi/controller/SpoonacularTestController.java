package pl.coderslab.mealplannerapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.coderslab.mealplannerapi.SpoonacularClient;
import pl.coderslab.mealplannerapi.SpoonacularProperties;

@RestController
public class SpoonacularTestController {
    private final SpoonacularClient spoonacularClient;
    public SpoonacularTestController(SpoonacularClient spoonacularClient) {
        this.spoonacularClient = spoonacularClient;
    }

    @GetMapping("/api/test/spoonacular")
    public String testSpoonacular() {
        return spoonacularClient.getRandomRecipeRaw();
    }
}
