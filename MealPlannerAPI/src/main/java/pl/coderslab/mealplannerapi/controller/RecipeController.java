package pl.coderslab.mealplannerapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.coderslab.mealplannerapi.entity.Recipe;
import pl.coderslab.mealplannerapi.repository.RecipeRepository;
import pl.coderslab.mealplannerapi.service.RecipeService;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/get/{id}")
    public Recipe getRecipeById(@PathVariable Long id){
        return recipeService.getRecipeById(id);
    }

    @GetMapping("/get/all")
    public List<Recipe> getAllRecipes(){
        return recipeService.getAllRecipes();
    }
}
