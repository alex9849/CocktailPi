export default class Recipe {
  constructor(id, name, inPublic, owner, description, shortDescription, recipeIngredients, tags) {
    this.id = id;
    this.name = name;
    this.inPublic = inPublic;
    this.owner = owner;
    this.description = description;
    this.shortDescription = shortDescription;
    this.recipeIngredients = recipeIngredients;
    this.tags = tags;
  }
}
