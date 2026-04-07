const form = document.getElementById("meal-plan-form");
const daysCountInput = document.getElementById("daysCount");
const messageEl = document.getElementById("message");

const mealPlanSection = document.getElementById("meal-plan-section");
const mealPlanInfo = document.getElementById("meal-plan-info");
const recipesList = document.getElementById("recipes-list");

const recipeDetailsSection = document.getElementById("recipe-details-section");
const recipeDetails = document.getElementById("recipe-details");

const shoppingListSection = document.getElementById("shopping-list-section");
const shoppingList = document.getElementById("shopping-list");
const shoppingListButton = document.getElementById("shopping-list-button");

let currentMealPlanId = null;
let currentMealPlan = null;

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const daysCount = Number(daysCountInput.value);

    messageEl.textContent = "Trwa generowanie meal planu...";
    recipeDetailsSection.classList.add("hidden");
    shoppingListSection.classList.add("hidden");

    try {
        const response = await fetch("/api/meal-plans/generate", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ daysCount })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Nie udało się wygenerować meal planu.");
        }

        const mealPlan = await response.json();
        currentMealPlan = mealPlan;
        currentMealPlanId = mealPlan.id;

        renderMealPlan(mealPlan);
        messageEl.textContent = "Meal plan został wygenerowany.";
    } catch (error) {
        console.error(error);
        messageEl.textContent = "Błąd podczas generowania meal planu.";
    }
});

shoppingListButton.addEventListener("click", async () => {
    if (!currentMealPlanId) {
        return;
    }

    try {
        const response = await fetch(`/api/meal-plans/get/shopping-list/${currentMealPlanId}`);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Nie udało się pobrać listy zakupów.");
        }

        const items = await response.json();
        renderShoppingList(items);
    } catch (error) {
        console.error(error);
        shoppingList.innerHTML = "<p>Błąd podczas pobierania listy zakupów.</p>";
        shoppingListSection.classList.remove("hidden");
    }
});

function renderMealPlan(mealPlan) {
    mealPlanSection.classList.remove("hidden");

    mealPlanInfo.innerHTML = `
        <p><strong>ID meal planu:</strong> ${mealPlan.id}</p>
        <p><strong>Liczba dni:</strong> ${mealPlan.daysCount ?? "-"}</p>
        <p><strong>Liczba przepisów:</strong> ${mealPlan.recipes ? mealPlan.recipes.length : 0}</p>
    `;

    recipesList.innerHTML = "";

    const recipes = mealPlan.recipes || [];

    if (recipes.length === 0) {
        recipesList.innerHTML = "<p>Brak przepisów w meal planie.</p>";
        return;
    }

    recipes.forEach((recipe) => {
        const recipeCard = document.createElement("div");
        recipeCard.className = "recipe-card";

        recipeCard.innerHTML = `
            <h3>${recipe.name ?? "Brak nazwy"}</h3>
            <div class="recipe-meta">
                <p><strong>ID przepisu:</strong> ${recipe.id ?? "-"}</p>
            </div>
            <div class="recipe-actions">
                <button type="button" data-recipe-id="${recipe.id}">Pokaż szczegóły</button>
            </div>
        `;

        const detailsButton = recipeCard.querySelector("button");
        detailsButton.addEventListener("click", () => {
            fetchRecipeDetails(recipe.id);
        });

        recipesList.appendChild(recipeCard);
    });
}

async function fetchRecipeDetails(recipeId) {
    try {
        const response = await fetch(`/api/recipes/get/${recipeId}`);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Nie udało się pobrać szczegółów przepisu.");
        }

        const recipe = await response.json();
        console.log("szczegóły przepisu: ", recipe);
        renderRecipeDetails(recipe);
    } catch (error) {
        console.error(error);
        recipeDetails.innerHTML = "<p>Błąd podczas pobierania szczegółów przepisu.</p>";
        recipeDetailsSection.classList.remove("hidden");
    }
}

function renderRecipeDetails(recipe) {
    recipeDetailsSection.classList.remove("hidden");

    console.log("renderuję szczegóły przepisu:", recipe);

    const recipeName = recipe.name || "Brak nazwy";
    const sourceUrl = recipe.sourceUrl || null;
    const imageUrl = recipe.imageUrl || null;
    const recipeIngredients = recipe.recipeIngredients || [];

    let ingredientsHtml = "<p>Brak składników.</p>";

    if (recipeIngredients.length > 0) {
        const items = recipeIngredients.map((item) => {
            const ingredientName =
                item.ingredient && item.ingredient.name
                    ? item.ingredient.name
                    : "Brak nazwy składnika";

            const amount = item.amount ?? 0;
            const unit = item.unit ?? "";

            return `<li>${ingredientName} - ${amount} ${unit}</li>`;
        }).join("");

        ingredientsHtml = `<ul class="recipe-details-list">${items}</ul>`;
    }

    recipeDetails.innerHTML = `
        <p><strong>Nazwa:</strong> ${recipeName}</p>
        <p><strong>Źródło:</strong> ${
        sourceUrl
            ? `<a href="${sourceUrl}" target="_blank">Zobacz przepis</a>`
            : "-"
    }</p>
        <p><strong>Obrazek:</strong> ${
        imageUrl
            ? `<a href="${imageUrl}" target="_blank">Podgląd obrazka</a>`
            : "-"
    }</p>
        <h3>Składniki</h3>
        ${ingredientsHtml}
    `;
}

function renderShoppingList(items) {
    shoppingListSection.classList.remove("hidden");

    if (!items || items.length === 0) {
        shoppingList.innerHTML = "<p>Brak pozycji na liście zakupów.</p>";
        return;
    }

    const html = items.map((item) => `
        <li>
            ${item.ingredientName ?? "-"} - ${item.amount ?? 0} ${item.unit ?? ""}
        </li>
    `).join("");

    shoppingList.innerHTML = `<ul class="shopping-list-items">${html}</ul>`;
}