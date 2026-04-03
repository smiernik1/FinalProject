package pl.coderslab.mealplannerapi;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SpoonacularClient {

    private final SpoonacularProperties properties;

    public SpoonacularClient(SpoonacularProperties spoonacularProperties) {
        this.properties = spoonacularProperties;
    }

    public String getRandomRecipeRaw() {
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
}
