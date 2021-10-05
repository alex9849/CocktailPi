export default class Recipe {
  constructor(id, name, inPublic, owner, description, recipeIngredients, categories, defaultAmountToFill) {
    this.id = id;
    this.name = name;
    this.inPublic = inPublic;
    this.owner = owner;
    this.description = description;
    this.recipeIngredients = recipeIngredients;
    this.categories = categories;
    this.defaultAmountToFill = defaultAmountToFill;
  }
}
