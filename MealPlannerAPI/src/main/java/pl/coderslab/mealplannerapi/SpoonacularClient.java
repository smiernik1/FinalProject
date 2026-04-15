package pl.coderslab.mealplannerapi;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.coderslab.mealplannerapi.SpoonacularProperties;
import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeDTO;
import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeResponseDTO;
import pl.coderslab.mealplannerapi.spoonacular.SpoonacularException;
import pl.coderslab.mealplannerapi.spoonacular.UnsafeRestTemplate;

import java.util.List;

@Component
public class SpoonacularClient {

    private final SpoonacularProperties properties;

    public SpoonacularClient(SpoonacularProperties spoonacularProperties) {
        this.properties = spoonacularProperties;
    }

    private List<SpoonacularRecipeDTO> getRecipes (String url) {
        try {
            RestTemplate restTemplate = UnsafeRestTemplate.create();

            SpoonacularRecipeResponseDTO response =  restTemplate.getForObject(url, SpoonacularRecipeResponseDTO.class);

            if (response == null || response.getRecipes() == null || response.getRecipes().isEmpty()) {
                throw new SpoonacularException("No recipes returned from Spoonacular API");
            }
            return response.getRecipes();

        } catch (SpoonacularException e) {
            throw e;
        } catch (Exception e) {
            throw new SpoonacularException("Error while calling Spoonacular API", e);
        }
    }

    public SpoonacularRecipeDTO getRandomRecipe() {

        String url = properties.getBaseUrl()
                    + "/recipes/random?number=1&apiKey="
                    + properties.getApiKey();

        return getRecipes(url).stream()
                .findFirst()
                .orElseThrow(() -> new SpoonacularException("No recipe found"));
    }

    public List<SpoonacularRecipeDTO> getRandomRecipes(int number, String dishType) {

        if (number <= 0) {
            throw new IllegalArgumentException("Number must be greater than 0");
        }

        if (dishType == null || dishType.isBlank()) {
            throw new IllegalArgumentException("dishType cannot be null or empty");
        }

            String url = properties.getBaseUrl()
                    + "/recipes/random?number=" + number
                    + "&tags=" + dishType
                    + "&includeNutrition=true&apiKey="
                    + properties.getApiKey();

            return getRecipes(url);
    }

    public List<SpoonacularRecipeDTO> getRandomRecipesByDiets(String diet, int number, String dishType) {

        if (number <= 0) {
            throw new IllegalArgumentException("Number must be greater than 0");
        }

            String url = properties.getBaseUrl()
                    + "/recipes/random?number=" + number
                    + "&tags=" + diet + "," + dishType
                    + "&includeNutrition=true&apiKey=" + properties.getApiKey();

            return getRecipes(url);
    }

    // FUNKCJA TESTOWA
//    public String getStringRandomRecipeAsString() {
//        try {
//            RestTemplate restTemplate = UnsafeRestTemplate.create();
//            String url = properties.getBaseUrl()
//                    + "/recipes/random?number=1&apiKey="
//                    + properties.getApiKey();
//
//            return restTemplate.getForObject(url, String.class);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
