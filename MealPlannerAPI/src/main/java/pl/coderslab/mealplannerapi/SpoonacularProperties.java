package pl.coderslab.mealplannerapi;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spoonacular")
@Setter
@Getter
public class SpoonacularProperties {

    private String baseUrl;
    private String apiKey;
}
