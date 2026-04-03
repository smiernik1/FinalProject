//package pl.coderslab.mealplannerapi.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeDTO;
//import pl.coderslab.mealplannerapi.entity.Recipe;
//import pl.coderslab.mealplannerapi.repository.CategoryRepository;
//import pl.coderslab.mealplannerapi.repository.RecipeRepository;
//
//@Service
//public class SpoonacularService {
//
//    @Value("${spoonacular.api.key}")
//    private String apiKey;
//
//
//    private RestTemplate restTemplate = new RestTemplate();
//
//    public SpoonacularRecipeDTO getRecipeById(Long id) {
//        String url = "https://api.spoonacular.com/recipes/" + id + "/information?apiKey=" + apiKey;
//        return restTemplate.getForObject(url, SpoonacularRecipeDTO.class);
//    }
//}
