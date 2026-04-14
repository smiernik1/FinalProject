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
const editShoppingListButton = document.getElementById("edit-shopping-list-button");
const downloadShoppingListButton = document.getElementById("download-shopping-list-button");

const loadMealPlansButton = document.getElementById("load-meal-plans-button");
const mealPlansList = document.getElementById("meal-plans-list");

let currentMealPlanId = null;
let currentMealPlan = null;
let mealPlansVisible = false;
let currentShoppingListId = null;

const mealsPerDayInput = document.getElementById("mealsPerDay");
const dishTypeCheckboxes = document.querySelectorAll('input[name="dishType"]');

function getCheckedDishTypes() {
    return Array.from(dishTypeCheckboxes)
        .filter(cb => cb.checked)
        .map(cb => cb.value);
}

function updateShoppingButton(mealPlan) {
    const btn = document.getElementById("shopping-list-button");

    btn.textContent = mealPlan.shoppingListGenerated
        ? "Wyświetl listę zakupów"
        : "Generuj listę zakupów";
}

async function loadMealPlans() {
    const response = await fetch("/api/meal-plans/get/all");
    if (!response.ok) throw new Error("Nie udało się pobrać meal planów.");

    return await response.json();
}

dishTypeCheckboxes.forEach(cb => {
    cb.addEventListener("change", () => {

        const max = parseInt(mealsPerDayInput.value || 0);
        const checked = getCheckedDishTypes();

        if (checked.length > max) {
            cb.checked = false;
            alert(`Możesz wybrać tylko ${max} typy posiłków`);
        }
    });
});

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const daysCount = Number(daysCountInput.value);
    const diet = form.querySelector('input[name="diet"]:checked').value;
    const minCaloriesValue = form.querySelector('input[name="minCalories"]').value;
    const maxCaloriesValue = form.querySelector('input[name="maxCalories"]').value;
    const minCalories = minCaloriesValue === "" ? null : Number(minCaloriesValue);
    const maxCalories = maxCaloriesValue === "" ? null : Number(maxCaloriesValue);
    const mealsPerDay = Number(mealsPerDayInput.value);
    const dishTypes = getCheckedDishTypes();

    if (dishTypes.length !== mealsPerDay) {
        alert("Musisz wybrać dokładnie tyle typów posiłków ile wynosi dzienna liczba posiłków");
        return;
    }

    if (minCalories <= 0) {
        alert("Min kalorii musi być większe od 0");
        return;
    }
    if (maxCalories <= minCalories) {
        alert("Max kolorii musi być większe od min");
        return;
    }

    messageEl.textContent = "Trwa generowanie meal planu...";

    recipeDetailsSection.classList.add("hidden");
    shoppingListSection.classList.add("hidden");

    try {
        const response = await fetch("/api/meal-plans/generate", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                request: {
                    daysCount,
                    mealsPerDay,
                    diet,
                    minCalories,
                    maxCalories
                },
                dishTypes
            })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Nie udało się wygenerować meal planu.");
        }

        const mealPlan = await response.json();
        currentMealPlan = mealPlan;
        currentMealPlanId = mealPlan.id;

        renderMealPlan(mealPlan);

        dishTypeCheckboxes.forEach(cb => cb.checked = false);
        messageEl.textContent = "Meal plan został wygenerowany.";
        if (mealPlansVisible) {
            const mealPlans = await loadMealPlans();
            renderMealPlansList(mealPlans);
        }

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
        const response = await fetch(`/api/meal-plans/${currentMealPlanId}/shopping-list`, {
            method: "POST"
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Nie udało się pobrać listy zakupów.");
        }

        const shoppingListResponse = await response.json();
        currentShoppingListId = shoppingListResponse.id;
        renderShoppingList(shoppingListResponse);
        // updateShoppingButton(mealPlan);

        currentMealPlan.shoppingListGenerated = true;
        updateShoppingButton(currentMealPlan);

        if (mealPlansVisible) {
            const mealPlans = await loadMealPlans();
            renderMealPlansList(mealPlans);
        }

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
        <p><strong>Liczba przepisów:</strong> ${mealPlan.mealPlanRecipes ? mealPlan.mealPlanRecipes.length : 0}</p>
        <p><strong>Dieta:</strong> ${mealPlan.diet || "brak"}</p>
    `;

    recipesList.innerHTML = "";

    const mealPlanRecipes = mealPlan.mealPlanRecipes || [];
    const grouped = {};
    const caloriesByDay = {};

    mealPlanRecipes.forEach(mpr => {
        const day = mpr.day;
        const recipe = mpr.recipe;
        const calories = recipe?.calories || 0;

        if (!grouped[day]) grouped[day] = [];
        grouped[day].push(recipe);

        caloriesByDay[day] = (caloriesByDay[day] || 0) + calories;
    });

    if (mealPlanRecipes.length === 0) {
        recipesList.innerHTML = "<p>Brak przepisów w meal planie.</p>";
        return;
    }

    Object.keys(grouped)
        .sort((a, b) => a - b)
        .forEach(day => {

            const totalCalories = caloriesByDay[day] || 0;

            const dayHeader = document.createElement("h2");
            dayHeader.textContent = `Dzień ${day} (${totalCalories} kcal)`;
            recipesList.appendChild(dayHeader);

            if (mealPlan.minCalories != null && totalCalories < mealPlan.minCalories) {
                dayHeader.textContent += ` <span class="calorie-warning">⚠️ Kalorie poniżej limitu (${mealPlan.minCalories} kcal)</span>`;
            }

            if (mealPlan.maxCalories != null && totalCalories > mealPlan.maxCalories) {
                dayHeader.innerHTML += ` <span class="calorie-warning">⚠️ Przekroczono limit kalorii (${mealPlan.maxCalories} kcal)</span>`;
            }

            grouped[day].forEach(recipe => {
                const recipeCard = document.createElement("div");
                recipeCard.className = "recipe-card";

                recipeCard.innerHTML = `
                <h3>${recipe.name ?? "Brak nazwy"}</h3>
                <div class="recipe-meta">
                    <p><strong>ID:</strong> ${recipe.id}</p>
                    <p><strong>Kalorie:</strong> ${recipe.calories ?? "-"} kcal</p>
                </div>
                <div class="recipe-actions">
                    <button class="details-btn">Pokaż szczegóły</button>
                    ${!mealPlan.shoppingListGenerated ? `<button class="replace-btn">Zamień przepis</button>` : ""}
                </div>
            `;

                recipeCard.querySelector(".details-btn")
                    .addEventListener("click", () => fetchRecipeDetails(recipe.id));

                const replaceBtn = recipeCard.querySelector(".replace-btn");
                if (replaceBtn) {
                    replaceBtn.addEventListener("click", () => replaceRecipe(recipe.id));
                }

                recipesList.appendChild(recipeCard);
            });
        });
    updateShoppingButton(mealPlan);
}

async function replaceRecipe(recipeId) {
    if (!currentMealPlanId) return;

    try {
        messageEl.textContent = "Podmienianie przepisu...";

        const response = await fetch(
            `/api/meal-plans/${currentMealPlanId}/replace-recipe/${recipeId}`,
            {
                method: "POST"
            }
        );

        if (!response.ok) {
            throw new Error("Nie udało się podmienić przepisu.");
        }

        const updatedMealPlan = await response.json();

        currentMealPlan = updatedMealPlan;

        renderMealPlan(updatedMealPlan);

        recipeDetailsSection.classList.add("hidden");

        messageEl.textContent = "Przepis został podmieniony.";
    } catch (error) {
        console.error(error);
        messageEl.textContent = "Błąd podczas podmiany przepisu.";
    }
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
    }
        <div class="recipe-image">
            ${
        imageUrl
            ? `<img src="${imageUrl}" alt="${recipeName}" />`
            : "<p>Brak obrazka</p>"
    }
        </div>
    <h3>Składniki</h3>
    ${ingredientsHtml}
    `
    ;
}

function renderShoppingList(shoppingListResponse) {
    shoppingListSection.classList.remove("hidden");

    if (!shoppingListResponse || !shoppingListResponse.items || shoppingListResponse.items.length === 0) {
        shoppingList.innerHTML = "<p>Brak pozycji na liście zakupów.</p>";
        return;
    }

    const html = shoppingListResponse.items.map((item) => `
        <li>
            ${item.name ?? "-"} - ${item.amount ?? 0} ${item.unit ?? ""}
        </li>
    `).join("");

    shoppingList.innerHTML = `<ul class="shopping-list-items">${html}</ul>`;

}

loadMealPlansButton.addEventListener("click", async () => {
    if (mealPlansVisible) {

        mealPlansList.innerHTML = "";
        mealPlansVisible = false;
        loadMealPlansButton.textContent = "Pokaż zapisane meal plany";
        return;
    }

    try {
        const mealPlans = await loadMealPlans();
        renderMealPlansList(mealPlans);

        mealPlansVisible = true;
        loadMealPlansButton.textContent = "Ukryj meal plany";
    } catch (error) {
        console.error(error);
        mealPlansList.innerHTML = "<p>Błąd podczas pobierania meal planów.</p>";
    }
});

function renderMealPlansList(mealPlans) {
    mealPlansList.innerHTML = "";

    if (!mealPlans || mealPlans.length === 0) {
        mealPlansList.innerHTML = "<p>Brak zapisanych meal planów.</p>";
        return;
    }

    mealPlans.forEach(plan => {
        const div = document.createElement("div");
        div.className = "meal-plan-card";

        div.innerHTML = `
            <p><strong>ID:</strong> ${plan.id}</p>
            <p><strong>Liczba dni:</strong> ${plan.daysCount ?? "-"}</p>
            <p><strong>Lista zakupów:</strong>${plan.shoppingListGenerated ? "✔" : "❌"}</p>
            <button data-id="${plan.id}" class="show-btn">Pokaż</button>
            <button data-id="${plan.id}" class="delete-btn">Usuń</button>
        `;

        const showButton = div.querySelector(".show-btn");
        const deleteButton = div.querySelector(".delete-btn");

        showButton.addEventListener("click", () => {
            fetchMealPlanById(plan.id);
        });

        deleteButton.addEventListener("click", () => {
            deleteMealPlan(plan.id);
        });

        mealPlansList.appendChild(div);
    });
}

async function fetchMealPlanById(id) {
    try {
        const response = await fetch(`/api/meal-plans/get/${id}`);

        if (!response.ok) {
            throw new Error("Nie udało się pobrać meal planu.");
        }

        const mealPlan = await response.json();

        currentMealPlan = mealPlan;
        currentMealPlanId = mealPlan.id;

        renderMealPlan(mealPlan);

        recipeDetailsSection.classList.add("hidden");
        shoppingListSection.classList.add("hidden");

    } catch (error) {
        console.error(error);
        messageEl.textContent = "Błąd podczas pobierania meal planu.";
    }
}

async function deleteMealPlan(id) {
    const confirmDelete = confirm("Czy na pewno chcesz usunąć ten meal plan?");

    if (!confirmDelete) return;

    try {
        const response = await fetch(`/api/meal-plans/delete/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            throw new Error("Nie udało się usunąć meal planu.");
        }

        if (currentMealPlanId === id) {
            mealPlanSection.classList.add("hidden");
            recipeDetailsSection.classList.add("hidden");
            shoppingListSection.classList.add("hidden");
            currentMealPlanId = null;
            currentMealPlan = null;
        }

        loadMealPlansButton.click();

        messageEl.textContent = "Meal plan został usunięty.";
    } catch (error) {
        console.error(error);
        messageEl.textContent = "Błąd podczas usuwania meal planu.";
    }
}

editShoppingListButton.addEventListener("click", () => {
    if (!currentShoppingListId) {
        return;
    }
    window.location.href = `/shopping-list.html?id=${currentShoppingListId}`;
})

downloadShoppingListButton.addEventListener("click", () => {
    if (!currentShoppingListId) {
        return;
    }
    alert("Funkcja pobierania jest w trakcie tworzenia")
})