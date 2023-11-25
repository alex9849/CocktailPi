import json

import requests
import urllib
import shutil
import os

cocktailsFilePath = 'data/recipes.json'
ingredientsFilePath = 'data/ingredients.json'
categoriesFilePath = 'data/categories.json'
glassFilePath = 'data/glasses.json'
headers = {'Content-type': 'application/json'}
cocktailPiUrl = "http://localhost:8080"
user = 'admin'
password = '123456'

login_data = {
    'user': 'admin',
    'password': '123456'
}


def fetchRecipesFromServer():
    recipeIds = []
    recipes = []
    token = provideAuthToken(cocktailPiUrl, user, password)
    headers['Authorization'] = token
    response = requests.get(cocktailPiUrl + "/api/recipe/", {'inPublic': True}, headers=headers)
    totalPages = response.json()['totalPages']
    for recipe in response.json()['content']:
        recipeIds.append(recipe['id'])

    for page in range(1, totalPages):
        response = requests.get(cocktailPiUrl + "/api/recipe/", {'inPublic': True, 'page': page}, headers=headers)
        for recipe in response.json()['content']:
            recipeIds.append(recipe['id'])

    for id in recipeIds:
        response = requests.get(cocktailPiUrl + "/api/recipe/" + str(id), headers=headers)
        recipes.append(response.json())

    with open(cocktailsFilePath, 'w') as outfile:
        json.dump(recipes, outfile)

    for recipe in recipes:
        if recipe['hasImage']:
            urllib.request.urlretrieve(cocktailPiUrl + "/api/recipe/" + str(recipe['id']) + "/image",
                                       "data/images/recipes/" + str(recipe['name']) + ".jpg")


def fetchIngredientsFromServer():
    ingredients = []
    token = provideAuthToken(cocktailPiUrl, user, password)
    headers['Authorization'] = token
    response = requests.get(cocktailPiUrl + "/api/ingredient/export", headers=headers)
    if (response.json() != None):
        ingredients.extend(response.json())

    with open(ingredientsFilePath, 'w') as outfile:
        json.dump(ingredients, outfile)

    for ingredient in ingredients:
        if 'hasImage' in ingredient and ingredient['hasImage']:
            urllib.request.urlretrieve(cocktailPiUrl + "/api/ingredient/" + str(ingredient['id']) + "/image",
                                       "data/images/ingredients/" + str(ingredient['name']) + ".jpg")

def fetchGlassesFromServer():
    glasses = []
    token = provideAuthToken(cocktailPiUrl, user, password)
    headers['Authorization'] = token
    response = requests.get(cocktailPiUrl + "/api/glass/", headers=headers)
    if (response.json() != None):
        glasses.extend(response.json())

    with open(glassFilePath, 'w') as outfile:
        json.dump(glasses, outfile)


def fetchCategoriesFromServer():
    categories = []
    token = provideAuthToken(cocktailPiUrl, user, password)
    headers['Authorization'] = token
    response = requests.get(cocktailPiUrl + "/api/category/", headers=headers)
    if (response.json() != None):
        categories.extend(response.json())

    with open(categoriesFilePath, 'w') as outfile:
        json.dump(categories, outfile)

def provideAuthToken(url, username, password):
    response = requests.post(url + '/api/auth/login', json={'username': username, 'password': password})
    print(response.status_code)
    return response.json()['tokenType'] + " " + response.json()['accessToken']


if __name__ == "__main__":
    if os.path.exists("data"):
        shutil.rmtree("data")
    os.mkdir("data")
    os.mkdir("data/images")
    os.mkdir("data/images/recipes")
    os.mkdir("data/images/ingredients")
    fetchCategoriesFromServer()
    fetchGlassesFromServer()
    fetchIngredientsFromServer()
    fetchRecipesFromServer()
