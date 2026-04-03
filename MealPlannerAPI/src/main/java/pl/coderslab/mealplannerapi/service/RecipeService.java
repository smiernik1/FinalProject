//package pl.coderslab.mealplannerapi.service;
//
//import org.springframework.stereotype.Service;
//import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeDTO;
//import pl.coderslab.mealplannerapi.entity.Category;
//import pl.coderslab.mealplannerapi.entity.Recipe;
//import pl.coderslab.mealplannerapi.repository.CategoryRepository;
//import pl.coderslab.mealplannerapi.repository.RecipeRepository;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Service
//public class RecipeService {
//
//    private final RecipeRepository recipeRepository;
//    private final CategoryRepository categoryRepository;
//
//    public RecipeService(RecipeRepository recipeRepository, CategoryRepository categoryRepository) {
//        this.recipeRepository = recipeRepository;
//        this.categoryRepository = categoryRepository;
//    }
//
//    public Recipe saveRecipeFromDTO(SpoonacularRecipeDTO dto) {
//        Recipe recipe = new Recipe();
//        recipe.setName(dto.getTitle());
//
//        // Calories
//        dto.getNutrition().getNutrients().stream()
//                .filter(n -> n.getName().equalsIgnoreCase("Calories"))
//                .findFirst()
//                .ifPresent(n -> recipe.setCalories(n.getAmount()));
//
//        // Category
//        Set<Category> categories = new HashSet<>();
//        for (String dt : dto.getDishTypes()) {
//            Category cat = categoryRepository.findByName(dt)
//                    .orElseGet(() -> categoryRepository.save(new Category(dt)));
//            categories.add(cat);
//        }
//        for (String d : dto.getDiets()) {
//            Category cat = categoryRepository.findByName(d)
//                    .orElseGet(() -> categoryRepository.save(new Category(d)));
//            categories.add(cat);
//        }
//        recipe.setCategories(categories);
//
//        return recipeRepository.save(recipe);
//    }
//}
