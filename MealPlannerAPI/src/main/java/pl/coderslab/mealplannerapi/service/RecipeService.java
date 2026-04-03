package pl.coderslab.mealplannerapi.service;

import org.springframework.stereotype.Service;
import pl.coderslab.mealplannerapi.entity.Recipe;
import pl.coderslab.mealplannerapi.repository.RecipeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe getRecipeById(Long id){
        return recipeRepository.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
    }

    public List<Recipe> getAllRecipes(){
        return recipeRepository.findAll();
    }
}
