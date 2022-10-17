import json

import requests
import urllib

cocktailsFilePath = 'data/recipes.json'
ingredientsFilePath = 'data/ingredients.json'
categoriesFilePath = 'data/categories.json'
headers = {'Content-type': 'application/json'}
cocktailMakerUrl = "http://localhost:8080"
user = 'admin'
password = '123456'

login_data = {
    'user': 'admin',
    'password': '123456'
}


def fetchRecipesFromServer():
    recipeIds = []
    recipes = []
    token = provideAuthToken(cocktailMakerUrl, user, password)
    headers['Authorization'] = token
    response = requests.get(cocktailMakerUrl + "/api/recipe", {'inPublic': True}, headers=headers)
    totalPages = response.json()['totalPages']
    for recipe in response.json()['content']:
        recipeIds.append(recipe['id'])

    for page in range(1, totalPages):
        response = requests.get(cocktailMakerUrl + "/api/recipe", {'inPublic': True, 'page': page}, headers=headers)
        for recipe in response.json()['content']:
            recipeIds.append(recipe['id'])

    for id in recipeIds:
        response = requests.get(cocktailMakerUrl + "/api/recipe/" + str(id), headers=headers)
        recipes.append(response.json())

    with open(cocktailsFilePath, 'w') as outfile:
        json.dump(recipes, outfile)

    for recipe in recipes:
        if recipe['hasImage']:
            urllib.request.urlretrieve(cocktailMakerUrl + "/api/recipe/" + str(recipe['id']) + "/image",
                                       "data/images/" + str(recipe['name']) + ".jpg")


def fetchIngredientsFromServer():
    ingredients = []
    token = provideAuthToken(cocktailMakerUrl, user, password)
    headers['Authorization'] = token
    response = requests.get(cocktailMakerUrl + "/api/ingredient/export", headers=headers)
    if (response.json() != None):
        ingredients.extend(response.json())

    with open(ingredientsFilePath, 'w') as outfile:
        json.dump(ingredients, outfile)


def fetchCategoriesFromServer():
    categories = []
    token = provideAuthToken(cocktailMakerUrl, user, password)
    headers['Authorization'] = token
    response = requests.get(cocktailMakerUrl + "/api/category", headers=headers)
    if (response.json() != None):
        categories.extend(response.json())

    with open(categoriesFilePath, 'w') as outfile:
        json.dump(categories, outfile)

def provideAuthToken(url, username, password):
    response = requests.post(url + '/api/auth/login', json={'username': username, 'password': password})
    print(response.status_code)
    return response.json()['tokenType'] + " " + response.json()['accessToken']


if __name__ == "__main__":
    fetchCategoriesFromServer()
    fetchIngredientsFromServer()
    fetchRecipesFromServer()
