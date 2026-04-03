//package pl.coderslab.mealplannerapi.dto;
//
//import lombok.Getter;
//import lombok.Setter;
//import pl.coderslab.mealplannerapi.entity.Category;
//import pl.coderslab.mealplannerapi.entity.Recipe;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@Getter
//@Setter
//public class SpoonacularRecipeDTO {
//    private Long id;
//    private String title;
//
//    private List<String> dishTypes;
//    private List<String> diets;
//
//    private Nutrition nutrition;
//
//    @Getter
//    @Setter
//    public static class Nutrition {
//        private List<Nutrient> nutrients;
//    }
//
//    @Getter
//    @Setter
//    public static class Nutrient {
//        private String name;
//        private Double amount;
//    }
//
//    public Recipe mapDtoToEntity(SpoonacularRecipeDTO dto) {
//        Recipe recipe = new Recipe();
//        //Name
//        recipe.setName(dto.getTitle());
//
//        //Calories
//        if (dto.getNutrition() != null && dto.getNutrition().getNutrients() != null) {
//            dto.getNutrition().getNutrients().stream()
//                    .filter(n -> n.getName().equalsIgnoreCase("Calories"))
//                    .findFirst()
//                    .ifPresent(n -> recipe.setCalories(n.getAmount().intValue()));
//        }
//
//        // Category
//        Set<Category> categories = new HashSet<>();
//        dto.getDishTypes().forEach(dt -> categories.add(new Category(dt)));
//        dto.getDiets().forEach(d -> categories.add(new Category(d)));
//        recipe.setCategories(categories);
//
//        return recipe;
//    }
//}




