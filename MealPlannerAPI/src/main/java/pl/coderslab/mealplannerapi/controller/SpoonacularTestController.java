package pl.coderslab.mealplannerapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.coderslab.mealplannerapi.SpoonacularClient;
import pl.coderslab.mealplannerapi.SpoonacularProperties;
import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeDTO;

@RestController
public class SpoonacularTestController {
    private final SpoonacularClient spoonacularClient;
    public SpoonacularTestController(SpoonacularClient spoonacularClient) {
        this.spoonacularClient = spoonacularClient;
    }

    @GetMapping("/api/test0/spoonacular")
    public String test0Spoonacular() {
        return spoonacularClient.getStringRandomRecipeRaw();
    }

    @GetMapping("/api/test/spoonacular")
    public SpoonacularRecipeDTO testSpoonacular() {
        return spoonacularClient.getRandomRecipeRaw();
    }

}
