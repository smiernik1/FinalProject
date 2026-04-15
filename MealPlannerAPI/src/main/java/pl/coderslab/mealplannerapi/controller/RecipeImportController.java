//package pl.coderslab.mealplannerapi.controller;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import pl.coderslab.mealplannerapi.entity.Recipe;
//import pl.coderslab.mealplannerapi.service.RecipeImportService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/test")
//public class RecipeImportController {
//
//    private final RecipeImportService recipeImportService;
//    public RecipeImportController(RecipeImportService recipeImportService) {
//        this.recipeImportService = recipeImportService;
//    }
//
//    @GetMapping("/import-random-recipe")
//    public String importRandomRecipe() {
//        Recipe recipe = recipeImportService.importRandomRecipe();
//        return "Recipe: " + recipe.getName() + " (id = " + recipe.getId() + ") imported successfully";
//    }
//
//}
