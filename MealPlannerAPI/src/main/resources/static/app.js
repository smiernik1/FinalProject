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
        loadMealPlansButton.click();

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

        currentMealPlan.shoppingListGenerated = true;
        loadMealPlansButton.click();

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
                <button class="details-btn" data-recipe-id="${recipe.id}">Pokaż szczegóły</button>
                <button class="replace-btn" data-recipe-id="${recipe.id}">Zamień przepis</button>
            </div>
        `;

        const detailsButton = recipeCard.querySelector("button");
        detailsButton.addEventListener("click", () => {
            fetchRecipeDetails(recipe.id);
        });

        const replaceButton = recipeCard.querySelector(".replace-btn");

        replaceButton.addEventListener("click", () => {
            replaceRecipe(recipe.id);
        });

        recipesList.appendChild(recipeCard);
    });
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

    if (!shoppingListResponse || !shoppingListResponse.items ||shoppingListResponse.items.length === 0) {
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
        const response = await fetch("/api/meal-plans/get/all");
        if (!response.ok) throw new Error("Nie udało się pobrać meal planów.");

        const mealPlans = await response.json();
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