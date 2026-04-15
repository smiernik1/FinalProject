# FinalProject
# 🍽️ Meal Plan API

## 📌 Opis projektu

**Meal Plan API** to aplikacja backendowa stworzona w technologii Java (Spring Boot), która umożliwia użytkownikowi generowanie planów posiłków na podstawie wybranych preferencji żywieniowych.

Projekt powstał jako **projekt końcowy bootcampu Coders Lab** i stanowi podsumowanie zdobytej wiedzy z zakresu tworzenia aplikacji backendowych, pracy z bazą danych oraz integracji z zewnętrznymi API.

---

## 🎯 Cel projektu

Celem aplikacji jest rozwiązanie problemu codziennego planowania posiłków poprzez:

* automatyczne generowanie planu żywieniowego
* dostosowanie posiłków do preferencji użytkownika
* tworzenie listy zakupów na podstawie wybranych przepisów

---

## ⚙️ Funkcjonalności

### ✅ Must Have

* Generowanie meal plana na podstawie:

  * liczby dni
  * liczby posiłków dziennie
  * typów posiłków (śniadanie, przystawka, zupa, danie główne, deser, sałatka, przekąska, napój)
  * diety:
    * brak
    * vegetarian
    * vegan
    * gluten-free
    * dairy-free
  * opcjonalnie: minimalnej i maksymalnej liczby kalorii

* Pobieranie przepisów z zewnętrznego API (Spoonacular)

* Wyświetlanie przepisów:

  * lista składników
  * zdjęcie
  * link do oryginalnej strony

* Możliwość ponownego losowania przepisu (z zachowaniem typu posiłku i diety)

* Generowanie listy zakupów:

  * agregacja składników z wielu przepisów
  * zapis do bazy danych

* Edycja listy zakupów:

  * dodawanie produktów
  * usuwanie produktów
  * zmiana ilości/jednostki/nazwy

* Zarządzanie meal planami:

  * pobieranie listy planów użytkownika
  * usuwanie planów
  * edycja planów

* Informacje o kaloriach:

  * kalorie dla każdego przepisu
  * suma dzienna
  * komunikat:

    * za mało kalorii
    * za dużo kalorii

---

### 🔄 Logika kalorii

Podane przez użytkownika wartości kalorii:

* **nie wpływają na zapytania do API**
* są wykorzystywane wyłącznie informacyjnie

Aplikacja:

* sumuje kalorie dzienne
* informuje użytkownika o przekroczeniu zakresu

---

## 🧱 Technologie

* Java 17+
* Spring Boot
* Spring Data JPA
* Hibernate
* REST API
* PostgreSQL / MySQL (w zależności od konfiguracji)
* Maven
* Spoonacular API (zewnętrzne API)

---

## 🗄️ Baza danych

Aplikacja wykorzystuje relacyjną bazę danych i zawiera m.in. następujące encje:

* MealPlan
* MealPlanRecipe
* Recipe
* RecipeIngredient
* Ingredient
* ShoppingList
* ShoppingListItem

### Relacje:

* One-to-Many (MealPlan - MealPlanRecipes)
* Many-to-One (RecipeIngredient - Ingredient)
* One-to-One (np. MealPlan - ShoppingList)

Struktura bazy danych jest znormalizowana i zgodna z logiką biznesową aplikacji.
<img width="683" height="730" alt="image" src="https://github.com/user-attachments/assets/4eef5a65-22e0-4e6a-967e-9858a92e7c4b" />

---

## 🔌 Integracja z API

Aplikacja korzysta z zewnętrznego API:

* Spoonacular API – do pobierania przepisów

---

## 📡 Endpointy (przykłady)

### Meal Plan

* `POST /api/meal-plans/generate` – utworzenie planu
* `GET /api/meal-plans/get/all` – lista planów
* `GET /api/meal-plans/get/{id}` – szczegóły planu
* `DELETE /api/meal-plans/delete/{id}` – usunięcie planu
* `POST /api/meal-plans/{mealPlanId}/replace-recipe/{recipeId}

### Recipes

* `POST /api/meal-plans/{mealPlanId}/replace-recipe/{recipeId}` – ponowne losowanie przepisu

### Shopping List

* `POST /api/meal-plans/{id}/shopping-list` – generowanie listy zakupów
* `GET /api/shopping-lists/{id}` – pobranie listy
* `PUT /api/shopping-lists/{id}` – edycja listy
* `DELETE /api/shopping-lists/{id}` – usunięcie listy

---

## ✅ Walidacja

Aplikacja zawiera walidację danych wejściowych, m.in.:

* poprawność liczby dni i posiłków
* zakres kalorii
* wymagane pola

Błędy zwracane są w czytelnej formie przez API.

---

## 🧠 Architektura

Projekt wykorzystuje klasyczną architekturę warstwową:

* Controller – obsługa requestów
* Service – logika biznesowa
* Repository – dostęp do danych
* DTO – transfer danych

---

## 🚀 Uruchomienie projektu

### Wymagania:

* Java 17+
* Maven
* Baza danych (np. PostgreSQL)
* Klucz API do Spoonacular

### Kroki:

1. Sklonuj repozytorium:

```bash
git clone https://github.com/your-repo/meal-plan-api.git
```

2. Ustaw zmienne środowiskowe:

```bash
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
SPOONACULAR_API_KEY=your_api_key
```

3. Skonfiguruj bazę danych w `application.properties`

4. Uruchom aplikację:

```bash
mvn spring-boot:run
```

---

## 📈 Możliwe rozszerzenia

* autoryzacja JWT
* frontend (React / Angular)
* optymalizacja kalorii (algorytm doboru posiłków)
* cache (np. Redis)
* testy jednostkowe i integracyjne

---

## 📌 Podsumowanie

Meal Plan API to aplikacja backendowa, która:

* integruje się z zewnętrznym API
* przetwarza dane i agreguje składniki
* zarządza planami posiłków i listami zakupów
* demonstruje praktyczne zastosowanie technologii backendowych

Projekt stanowi solidną bazę do dalszego rozwoju oraz portfolio programisty.
