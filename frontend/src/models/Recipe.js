export default class Recipe {
  constructor(id, name, owner, description, productionSteps, categories, defaultAmountToFill) {
    this.id = id
    this.name = name
    this.owner = owner
    this.description = description
    this.productionSteps = productionSteps
    this.categories = categories
    this.defaultAmountToFill = defaultAmountToFill
  }
}
