const shoppingListMeta = document.getElementById("shopping-list-meta");
const shoppingListMessage = document.getElementById("shopping-list-message");
const itemsContainer = document.getElementById("shopping-list-items-container");

const addItemButton = document.getElementById("add-item-button");
const saveButton = document.getElementById("save-button");
const saveAndExitButton = document.getElementById("saveAndExitButton");
const cancelButton = document.getElementById("cancel-button");

const params = new URLSearchParams(window.location.search);
const shoppingListId = params.get("id");

let currentShoppingList = null;

document.addEventListener("DOMContentLoaded", () => {
    if (!shoppingListId) {
        shoppingListMessage.textContent = "Brak id listy zakupów w adresie URL.";
        return;
    }

    loadShoppingList();
});

addItemButton.addEventListener("click", () => {
    addItemRow({ name: "", amount: "", unit: "" });
});

saveButton.addEventListener("click", async () => {
    await saveShoppingList();
});

saveAndExitButton.addEventListener("click", async () => {
    await saveShoppingList();
    window.location.href = "/";
})

cancelButton.addEventListener("click", () => {
    window.location.href = "/";
});

async function loadShoppingList() {
    try {
        shoppingListMessage.textContent = "Ładowanie listy zakupów...";

        const response = await fetch(`/api/shopping-lists/${shoppingListId}`);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Nie udało się pobrać listy zakupów.");
        }

        const shoppingList = await response.json();
        currentShoppingList = shoppingList;

        renderShoppingListEditor(shoppingList);
        shoppingListMessage.textContent = "Lista zakupów została załadowana.";
    } catch (error) {
        console.error(error);
        shoppingListMessage.textContent = "Błąd podczas ładowania listy zakupów.";
    }
}

function renderShoppingListEditor(shoppingList) {
    shoppingListMeta.innerHTML = `
<p><strong>ID listy zakupów:</strong> ${shoppingList.id}</p>
<p><strong>ID meal planu:</strong> ${shoppingList.mealPlanId ?? "-"}</p>
`;

    itemsContainer.innerHTML = "";

    const items = shoppingList.items || [];

    if (items.length === 0) {
        addItemRow({ name: "", amount: "", unit: "" });
        return;
    }

    items.forEach((item) => addItemRow(item));
}

function addItemRow(item = { name: "", amount: "", unit: "" }) {
    const row = document.createElement("div");
    row.className = "recipe-card shopping-item-row";

    row.innerHTML = `
<div style="display: grid; grid-template-columns: 2fr 1fr 1fr auto; gap: 12px; align-items: end;">
<div>
<label>Nazwa</label>
<input type="text" class="item-name" value="${escapeHtml(item.name ?? "")}">
</div>
<div>
<label>Ilość</label>
<input type="number" step="0.01" class="item-amount" value="${item.amount ?? ""}">
</div>
<div>
<label>Jednostka</label>
<input type="text" class="item-unit" value="${escapeHtml(item.unit ?? "")}">
</div>
<div>
<button type="button" class="delete-item-button">Usuń</button>
</div>
</div>
`;

    const deleteButton = row.querySelector(".delete-item-button");
    deleteButton.addEventListener("click", () => {
        row.remove();
    });

    itemsContainer.appendChild(row);
}

async function saveShoppingList() {
    try {
        const items = collectItemsFromForm();

        const payload = {
            items: items
        };

        console.log("payload do zapisu:", payload);

        shoppingListMessage.textContent = "Zapisywanie listy zakupów...";

        const response = await fetch(`/api/shopping-lists/${shoppingListId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Nie udało się zapisać listy zakupów.");
        }

        const updatedShoppingList = await response.json();
        currentShoppingList = updatedShoppingList;

        renderShoppingListEditor(updatedShoppingList);
        shoppingListMessage.textContent = "Lista zakupów została zapisana.";
    } catch (error) {
        console.error(error);
        shoppingListMessage.textContent = "Błąd podczas zapisywania listy zakupów.";
    }
}

function collectItemsFromForm() {
    const rows = document.querySelectorAll(".shopping-item-row");
    const items = [];

    rows.forEach((row) => {
        const name = row.querySelector(".item-name").value.trim();
        const amountValue = row.querySelector(".item-amount").value;
        const unit = row.querySelector(".item-unit").value.trim();

        if (!name) {
            return;
        }

        items.push({
            name: name,
            amount: amountValue ? Number(amountValue) : 0,
            unit: unit
        });
    });

    return items;
}

function escapeHtml(value) {
    return value
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}
