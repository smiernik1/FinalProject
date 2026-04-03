package pl.coderslab.mealplannerapi;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeDTO;
import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeResponseDTO;

@Component
public class SpoonacularClient {

    private final SpoonacularProperties properties;

    public SpoonacularClient(SpoonacularProperties spoonacularProperties) {
        this.properties = spoonacularProperties;
    }

    public String getStringRandomRecipeRaw() {
        try {
            RestTemplate restTemplate = UnsafeRestTemplate.create();
            String url = properties.getBaseUrl()
                    + "/recipes/random?number=1&apiKey="
                    + properties.getApiKey();

            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SpoonacularRecipeDTO getRandomRecipeRaw() {
        try {
            RestTemplate restTemplate = UnsafeRestTemplate.create();
            String url = properties.getBaseUrl()
                    + "/recipes/random?number=1&apiKey="
                    + properties.getApiKey();

            SpoonacularRecipeResponseDTO response =  restTemplate.getForObject(url, SpoonacularRecipeResponseDTO.class);

            if (response == null || response.getRecipes() == null || response.getRecipes().isEmpty()) {
                throw new RuntimeException("Response do not contains recipes");
            }
            return response.getRecipes().get(0);
        } catch (Exception e) {
            throw new RuntimeException("Spoonacular API error", e);
        }
    }
}
