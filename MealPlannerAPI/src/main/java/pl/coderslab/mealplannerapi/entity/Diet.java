package pl.coderslab.mealplannerapi.entity;

import lombok.Getter;

@Getter
public enum Diet {
    NONE(""),
    VEGETARIAN("vegetarian"),
    VEGAN("vegan"),
    GLUTEN_FREE("gluten-free"),
    DAIRY_FREE("dairy-free");

    private final String value;
    Diet(String value) {
        this.value = value;
    }
}
